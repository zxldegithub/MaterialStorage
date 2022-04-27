package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EssMapper;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @className: EsssServiceImpl
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/23
 **/
@Service
public class EssServiceImpl extends ServiceImpl<EssMapper, EsStoreroom> implements EssService {
    @Autowired
    private EsService esService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(EsStoreroom esStoreroom) throws Exception {
        //从Redis中检查是否已存在该条仓库记录
        EsStoreroom existEsStoreroom = null;
        Set<EsStoreroom> esStorerooms = selectSetFromRedis();
        for (EsStoreroom storeroom : esStorerooms) {
            if (esStoreroom.getEsNo().equals(storeroom.getEsNo())) {
                existEsStoreroom = storeroom;
                break;
            }
        }
        if (ObjectUtil.isNull(existEsStoreroom)) {
            //补充仓库信息并存入数据库
            esStoreroom.setEssTimeValue(SystemUtil.getTime()).setEssTs(SystemUtil.getTime());
            save(esStoreroom);
            //同时从Redis中获取上级物资信息库
            Set<ErStorage> erStorages = esService.selectSetFromRedis();
            ErStorage existErStorage = null;
            for (ErStorage erStorage : erStorages) {
                if (esStoreroom.getEsNo().equals(erStorage.getEsNo())){
                    existErStorage = erStorage;
                    break;
                }
            }
            //获取到后进行更新
            if (ObjectUtil.isNotNull(existErStorage)) {
                existErStorage.setEsStoreroomNumber(existErStorage.getEsStoreroomNumber() + 1).setEsTs(SystemUtil.getTime());
                esService.updateOne(existErStorage);
                //更新完成后加入缓存
                redisTemplate.opsForSet().add("esStorerooms", esStoreroom);
            } else {
                throw new Exception("不存在上级物资库");
            }
        } else {
            throw new Exception("已存在该仓库");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String essId) {
        try {
            //先更新上级物资库中的数据
            EsStoreroom esStoreroom = getById(essId);
            ErStorage erStorage = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esStoreroom.getEsNo()));
            erStorage.setEsStoreroomNumber(erStorage.getEsStoreroomNumber() - 1);
            esService.updateOne(erStorage);
            //再删除仓库数据同时更新缓存
            Set<EsStoreroom> esStoreroomSet = selectSetFromRedis();
            esStoreroomSet.removeIf(storeroom -> essId.equals(storeroom.getEssId()));
            removeById(essId);
            updateCache(esStoreroomSet);
        } catch (Exception e) {
            log.error("删除单个仓库时出错", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> essIdList) {
        try {
            List<EsStoreroom> esStoreroomList = listByIds(essIdList);
            HashMap<String, Integer> esNoMap = new HashMap<>();
            for (EsStoreroom esStoreroom : esStoreroomList) {
                Integer counts = esNoMap.get(esStoreroom.getEsNo());
                esNoMap.put(esStoreroom.getEsNo(), counts == null ? 1 : ++counts);
            }
            //先修改上级物资库的数据
            Set<String> esNOSet = esNoMap.keySet();
            for (String esNo : esNOSet) {
                ErStorage erStorage = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esNo));
                erStorage.setEsStoreroomNumber(erStorage.getEsStoreroomNumber()-esNoMap.get(esNo));
                esService.updateOne(erStorage);
            }
            //再删除仓库数据
            removeByIds(essIdList);
        } catch (Exception e) {
            log.error("删除多个仓库时出错", e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(EsStoreroom esStoreroom) throws Exception {
        //从Redis中获取现存的仓库信息，并删除
        EsStoreroom existEsStoreroom = null;
        Set<EsStoreroom> esStorerooms = selectSetFromRedis();
        Iterator<EsStoreroom> iterator = esStorerooms.iterator();
        while (iterator.hasNext()) {
            EsStoreroom storeroom = iterator.next();
            if (esStoreroom.getEssId().equals(storeroom.getEssId())){
                existEsStoreroom = storeroom;
                iterator.remove();
                break;
            }
        }
        //若仓库对应上级物资库没有发生更改，则不修改物资库数据
        if (!existEsStoreroom.getEsNo().equals(esStoreroom.getEsNo())) {
            //更新仓库之前所在物资库的数据
            ErStorage erStorageFront = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, existEsStoreroom.getEsNo()));
            erStorageFront.setEsStoreroomNumber(erStorageFront.getEsStoreroomNumber() - 1);
            //更新仓库之后所在物资库的数据
            ErStorage erStorageBack = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esStoreroom.getEsNo()));
            erStorageBack.setEsStoreroomNumber(erStorageBack.getEsStoreroomNumber() + 1);
            List<ErStorage> erStorageList = new ArrayList<>();
            erStorageList.add(erStorageFront);
            erStorageList.add(erStorageBack);
            for (ErStorage erStorage : erStorageList) {
                esService.updateOne(erStorage);
            }
        }
        //构建新对象
        existEsStoreroom.setEssNo(esStoreroom.getEssNo()).setEsNo(esStoreroom.getEsNo()).setEssLocation(esStoreroom.getEssLocation())
                .setEssUse(esStoreroom.getEssUse()).setEssArea(esStoreroom.getEssArea())
                .setEssFloorNumber(esStoreroom.getEssFloorNumber()).setEssSpaceNumber(esStoreroom.getEssSpaceNumber())
                .setEssTs(SystemUtil.getTime());
        //更新数据库和缓存中的仓库信息
        updateById(existEsStoreroom);
        esStorerooms.add(existEsStoreroom);
        updateCache(esStorerooms);
    }

    @Override
    public Page<EsStoreroom> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex, pageSize));
    }

    @Override
    public Set<EsStoreroom> selectSetFromRedis() {
        //从redis中获取仓库数据，若存在直接返回，若不存在则先存入redis再返回
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        Set<Object> esStorerooms = setOperations.members("esStorerooms");
        Set<EsStoreroom> esStoreroomSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(esStorerooms)) {
            for (Object o : esStorerooms) {
                esStoreroomSet.add((EsStoreroom) o);
            }
        } else {
            for (EsStoreroom esStoreroom : list()) {
                setOperations.add("esStorerooms", esStoreroom);
                esStoreroomSet.add(esStoreroom);
            }
            redisTemplate.expire("esStorerooms",30,TimeUnit.MINUTES);
        }
        return esStoreroomSet;
    }

    public void updateCache(Set<EsStoreroom> esStorerooms) {
        redisTemplate.delete("esStorerooms");
        for (EsStoreroom esStoreroom : esStorerooms) {
            redisTemplate.opsForSet().add("esStorerooms", esStoreroom);
        }
        redisTemplate.expire("esStorerooms",30, TimeUnit.MINUTES);
    }
}
