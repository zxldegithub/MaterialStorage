package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EssMapper;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.service.storageManage.EsssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private EsssService esssService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(EsStoreroom esStoreroom) throws Exception {
        //先检查是否已经存在该编号的仓库
        EsStoreroom storeroom = getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, esStoreroom.getEssNo()));
        if (ObjectUtil.isNotNull(storeroom)){
            throw new Exception("已存在此标号的仓库");
        }
        //先改变上级物资库的计数
        ErStorage storage = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esStoreroom.getEsNo()));
        storage.setEsStoreroomNumber(storage.getEsStoreroomNumber()+1);
        esService.updateOne(storage);
        //再补全仓库信息：时间值
        esStoreroom.setEssTimeValue(SystemUtil.getTime()).setEssTs(SystemUtil.getTime());
        save(esStoreroom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String essId) throws Exception {
        //先更新上级物资库的计数
        EsStoreroom byId = getById(essId);
        ErStorage storage = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, byId.getEsNo()));
        storage.setEsStoreroomNumber(storage.getEsStoreroomNumber()-1);
        esService.updateOne(storage);
        //再删除下级关联的库区
        List<EssSpace> essSpaceList = esssService.list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEssNo, byId.getEssNo()));
        List<String> esssIdList = new ArrayList<>();
        for (EssSpace space : essSpaceList) {
            esssIdList.add(space.getEsssId());
        }
        esssService.deleteMany(esssIdList);
        //最后删除仓库数据
        removeById(essId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> essIdList) throws Exception {
        //先更新上级仓库的计数
        List<EsStoreroom> esStoreroomList = listByIds(essIdList);
        Map<String, Integer> esNoMap = new HashMap<>();
        for (EsStoreroom storeroom : esStoreroomList) {
            Integer count = esNoMap.get(storeroom.getEsNo());
            esNoMap.put(storeroom.getEsNo(),count == null ? 1 : ++count);
        }
        for (String esNo : esNoMap.keySet()) {
            ErStorage storage = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esNo));
            storage.setEsStoreroomNumber(storage.getEsStoreroomNumber()-esNoMap.get(esNo));
            esService.updateOne(storage);
        }
        //再删除下级关联的库区
        for (EsStoreroom storeroom : esStoreroomList) {
            List<EssSpace> essSpaceList = esssService.list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEssNo, storeroom.getEssNo()));
            List<String> esssIdList = new ArrayList<>();
            for (EssSpace space : essSpaceList) {
                esssIdList.add(space.getEsssId());
            }
            esssService.deleteMany(esssIdList);
        }
        //最后删除仓库数据
        removeByIds(essIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(EsStoreroom esStoreroom) throws Exception {
        //判断上级物资库编号有没有发生变化
        EsStoreroom byId = getById(esStoreroom.getEssId());
        if (!byId.getEsNo().equals(esStoreroom.getEsNo())){
            //先更新物资库的计数
            ErStorage front = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, byId.getEsNo()));
            front.setEsStoreroomNumber(front.getEsStoreroomNumber()-1);
            esService.updateOne(front);
            ErStorage back = esService.getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esStoreroom.getEsNo()));
            back.setEsStoreroomNumber(back.getEsStoreroomNumber()+1);
            esService.updateOne(back);
            //再更新下级库区的相关编号
            List<EssSpace> spaceList = esssService.list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEssNo, byId.getEssNo()));
            for (EssSpace space : spaceList) {
                space.setEssNo(esStoreroom.getEssNo());
                esssService.updateOne(space);
            }
        }
        esStoreroom.setEssTs(SystemUtil.getTime());
        updateById(esStoreroom);
    }

    @Override
    public Page<EsStoreroom> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex, pageSize));
    }

}
