package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EsMapper;
import com.zxl.materialStorage.model.enumPackage.StorageStatus;
import com.zxl.materialStorage.model.enumPackage.StorageType;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @className: ErServiceImpl
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@Service
@Slf4j
public class EsServiceImpl extends ServiceImpl<EsMapper, ErStorage> implements EsService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(ErStorage erStorage) throws Exception {
        //从Redis中检查是否已经存在此编号的物资库
        ErStorage existErStorage = null;
        Set<ErStorage> erStorages = selectListFromRedis();
        for (ErStorage storage : erStorages) {
            if (erStorage.getEsNo().equals(storage.getEsNo())) {
                existErStorage = storage;
                break;
            }
        }
        if (ObjectUtil.isNull(existErStorage)) {
            //枚举所有的物资库类型，获得匹配的那一个
            int esTypeCode = 0;
            for (StorageType value : StorageType.values()) {
                if (value.getName().equals(erStorage.getEsTypeName())) {
                    esTypeCode = value.getCode();
                    break;
                }
            }
            //补充物资库信息并保存到数据库和redis中
            erStorage.setEsTypeCode(esTypeCode).setEsTimeValue(SystemUtil.getTime()).setEsTs(SystemUtil.getTime());
            save(erStorage);
            redisTemplate.opsForSet().add("erStorages", erStorage);
        } else {
            throw new Exception("已存在此仓库");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String esId) {
        try {
            removeById(esId);
            Set<ErStorage> erStorages = selectListFromRedis();
            erStorages.removeIf(erStorage -> esId.equals(erStorage.getEsId()));
            updateCache(erStorages);
        } catch (Exception e) {
            log.error("单个删除物资库时出错", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> esIdList) {
        try {
            removeByIds(esIdList);
            Set<ErStorage> erStorages = selectListFromRedis();
            Iterator<ErStorage> iterator = erStorages.iterator();
            while (iterator.hasNext()) {
                ErStorage erStorage = iterator.next();
                for (String esId : esIdList) {
                    if (esId.equals(erStorage.getEsId())) {
                        iterator.remove();
                    }
                }
            }
            updateCache(erStorages);
        } catch (Exception e) {
            log.error("批量删除物资库时出错", e);
        }
    }

    @Override
    public void updateOne(ErStorage erStorage) throws Exception {
        //根据ID从Redis中获取将要更新的物资库
        ErStorage existErStorage = null;
        Set<ErStorage> erStorages = selectListFromRedis();
        Iterator<ErStorage> iterator = erStorages.iterator();
        while (iterator.hasNext()) {
            ErStorage storage = iterator.next();
            if (erStorage.getEsId().equals(storage.getEsId())) {
                existErStorage = storage;
                iterator.remove();
                break;
            }
        }
        if (ObjectUtil.isNotNull(existErStorage)) {
            //枚举所有物资库类型和状态类型
            int esTypeCode = 0;
            for (StorageType value : StorageType.values()) {
                if (value.getName().equals(erStorage.getEsTypeName())) {
                    esTypeCode = value.getCode();
                }
            }
            int esStatusCode = 0;
            for (StorageStatus value : StorageStatus.values()) {
                if (value.getName().equals(erStorage.getEsStatusName())) {
                    esStatusCode = value.getCode();
                }
            }
            //构建新对象
            existErStorage.setEsNo(erStorage.getEsNo()).setEsLocation(erStorage.getEsLocation()).setEsTypeCode(esTypeCode)
                    .setEsTypeName(erStorage.getEsTypeName()).setEsIntroduce(erStorage.getEsIntroduce()).setEsStatusCode(esStatusCode)
                    .setEsStatusName(erStorage.getEsStatusName()).setEsTs(SystemUtil.getTime());
            //更新数据库和Redis中的物资库信息
            updateById(existErStorage);
            updateCache(erStorages);
        } else {
            throw new Exception("未查询到该条物资库记录");
        }
    }

    @Override
    public Page<ErStorage> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex,pageSize));
    }

    public void updateCache(Set<ErStorage> erStorages) {
        redisTemplate.delete("erStorages");
        for (ErStorage erStorage : erStorages) {
            redisTemplate.opsForSet().add("erStorages",erStorage);
        }
    }

    @Override
    public Set<ErStorage> selectListFromRedis() {
        //从redis中获取物资库数据，若存在直接返回，若不存在则先存入redis再返回
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        Set<Object> erStorages = setOperations.members("erStorages");
        Set<ErStorage> erStorageSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(erStorages)) {
            for (Object o : erStorages) {
                erStorageSet.add((ErStorage) o);
            }
        } else {
            for (ErStorage erStorage : list()) {
                setOperations.add("erStorages",erStorage);
                erStorageSet.add(erStorage);
            }
        }
        return erStorageSet;
    }
}
