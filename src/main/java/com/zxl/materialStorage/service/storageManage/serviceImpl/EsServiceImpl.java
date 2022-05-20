package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EsMapper;
import com.zxl.materialStorage.model.enumPackage.StorageStatus;
import com.zxl.materialStorage.model.enumPackage.StorageType;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.MaterialEnter;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.service.storageManage.EsssService;
import com.zxl.materialStorage.service.storageManage.EssssService;
import com.zxl.materialStorage.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @className: ErServiceImpl
 * @description: ErServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@Service
@Slf4j
public class EsServiceImpl extends ServiceImpl<EsMapper, ErStorage> implements EsService {
    @Autowired
    @Lazy
    private EssService essService;

    @Autowired
    @Lazy
    private EsssService esssService;

    @Autowired
    @Lazy
    private EssssService essssService;

    @Autowired
    @Lazy
    private MaterialEnterService materialEnterService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(ErStorage erStorage) throws Exception {
        //检查受否存在该编号的物资库
        ErStorage storage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, erStorage.getEsNo()));
        if (ObjectUtil.isNotNull(storage)) {
            throw new Exception("已存在此编号的物资库");
        }
        //补全信息
        Integer typeCode = null;
        for (StorageType value : StorageType.values()) {
            if (value.getName().equals(erStorage.getEsTypeName())) {
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
        //异步更新下级关联物资库编号
        ErStorage byId = getById(esId);
        essService.updateEsNos(byId.getEsNo(), null);
        esssService.updateEsNos(byId.getEsNo(), null);
        essssService.updateEsNos(byId.getEsNo(), null);
        materialEnterService.updateEsNos(byId.getEsNo(), null);
        //再删除物资库的数据
        removeById(esId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> esIdList) throws Exception {
        //异步更新下级关联物资库编号
        List<String> esNoList = new ArrayList<>();
        List<ErStorage> erStorageList = listByIds(esIdList);
        for (ErStorage erStorage : erStorageList) {
            esNoList.add(erStorage.getEsNo());
        }
        essService.updateEsNosByDel(esNoList);
        esssService.updateEsNosByDel(esNoList);
        essssService.updateEsNosByDel(esNoList);
        materialEnterService.updateEsNosByDel(esNoList);
        //删除物资库的数据
        removeByIds(esIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(ErStorage erStorage) throws Exception {
        //判断物资库的编号是否发生变化
        ErStorage byId = getById(erStorage.getEsId());
        if (!erStorage.getEsNo().equals(byId.getEsNo())) {
            //异步更新下级关联的物资库编号
            essService.updateEsNos(byId.getEsNo(), erStorage.getEsNo());
            esssService.updateEsNos(byId.getEsNo(), erStorage.getEsNo());
            essssService.updateEsNos(byId.getEsNo(), erStorage.getEsNo());
            materialEnterService.updateEsNos(byId.getEsNo(), erStorage.getEsNo());
        }
        //补全信息并更新
        Integer typeCode = null;
        if (erStorage.getEsTypeName() != null) {
            for (StorageType value : StorageType.values()) {
                if (value.getName().equals(erStorage.getEsTypeName())) {
                    typeCode = value.getCode();
                    break;
                }
            }
        }
        Integer statusCode = null;
        if (erStorage.getEsStatusName() != null) {
            for (StorageStatus value : StorageStatus.values()) {
                if (value.getName().equals(erStorage.getEsStatusName())) {
                    statusCode = value.getCode();
                    break;
                }
            }
        }
        erStorage.setEsTypeCode(typeCode).setEsStatusCode(statusCode).setEsTs(SystemUtil.getTime());
        updateById(erStorage);
    }

    @Override
    public Page<ErStorage> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex, pageSize));
    }

    @Override
    public List<ErStorage> selectAll() {
        return list();
    }

    @Override
    public List<String> selectEsNoList() {
        List<ErStorage> erStorageList = selectAll();
        List<String> esNoList = new ArrayList<>();
        for (ErStorage erStorage : erStorageList) {
            esNoList.add(erStorage.getEsNo());
        }
        return esNoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void deleteCount(String esNo, Integer number) {
        if (StringUtils.isEmpty(esNo)) {
            return;
        }
        ErStorage erStorage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esNo));
        erStorage.setEsStoreroomNumber(erStorage.getEsStoreroomNumber() - number).setEsTs(SystemUtil.getTime());
        updateById(erStorage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void addCount(String esNo) {
        ErStorage erStorage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, esNo));
        if (ObjectUtil.isNull(erStorage)) {
            return;
        }
        erStorage.setEsStoreroomNumber(erStorage.getEsStoreroomNumber() == null ? 1 : erStorage.getEsStoreroomNumber() + 1).setEsTs(SystemUtil.getTime());
        updateById(erStorage);
    }
}
