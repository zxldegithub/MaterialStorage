package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EsMapper;
import com.zxl.materialStorage.model.enumPackage.StorageStatus;
import com.zxl.materialStorage.model.enumPackage.StorageType;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private EssService essService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(ErStorage erStorage) throws Exception {
        //检查受否存在该编号的物资库
        ErStorage storage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, erStorage.getEsNo()));
        if (ObjectUtil.isNotNull(storage)){
            throw new Exception("已存在此编号的物资库");
        }
        //补全信息
        Integer typeCode = null;
        for (StorageType value : StorageType.values()) {
            if (value.getName().equals(erStorage.getEsTypeName())){
                typeCode = value.getCode();
                break;
            }
        }
        erStorage.setEsTypeCode(typeCode).setEsTimeValue(SystemUtil.getTime()).setEsTs(SystemUtil.getTime());
        save(erStorage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String esId) throws Exception {
        //先删除下级关联的数据
        ErStorage byId = getById(esId);
        List<EsStoreroom> esStoreroomList = essService.list(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEsNo, byId.getEsNo()));
        List<String> essIdList = new ArrayList<>();
        for (EsStoreroom storeroom : esStoreroomList) {
            essIdList.add(storeroom.getEssId());
        }
        essService.deleteMany(essIdList);
        //再删除物资库的数据
        removeById(esId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> esIdList) throws Exception {
        //先删除下级关联的数据
        List<ErStorage> erStorageList = listByIds(esIdList);
        for (ErStorage erStorage : erStorageList) {
            List<EsStoreroom> storeroomList = essService.list(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEsNo, erStorage.getEsNo()));
            List<String> essIdList = new ArrayList<>();
            for (EsStoreroom storeroom : storeroomList) {
                essIdList.add(storeroom.getEssId());
            }
            essService.deleteMany(essIdList);
        }
        //再删除物资库的数据
        removeByIds(esIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(ErStorage erStorage) throws Exception {
        //判断物资库的编号是否发生变化
        ErStorage byId = getById(erStorage.getEsId());
        if (!erStorage.getEsId().equals(byId.getEsId())){
            //更新下级关联的相关编号
            List<EsStoreroom> storeroomList = essService.list(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEsNo, byId.getEsNo()));
            for (EsStoreroom storeroom : storeroomList) {
                storeroom.setEsNo(erStorage.getEsNo());
                essService.updateOne(storeroom);
            }
        }
        //补全信息
        Integer typeCode = null;
        for (StorageType value : StorageType.values()) {
            if (value.getName().equals(erStorage.getEsTypeName())){
                typeCode = value.getCode();
                break;
            }
        }

        Integer statusCode = null;
        for (StorageStatus value : StorageStatus.values()) {
            if (value.getName().equals(erStorage.getEsStatusName())){
                statusCode = value.getCode();
                break;
            }
        }

        erStorage.setEsTypeCode(typeCode).setEsStatusCode(statusCode).setEsTs(SystemUtil.getTime());
        updateById(erStorage);
    }

    @Override
    public Page<ErStorage> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex,pageSize));
    }
}
