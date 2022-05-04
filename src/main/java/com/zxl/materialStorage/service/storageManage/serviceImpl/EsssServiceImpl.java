package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EsssMapper;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.model.pojo.EsssShelves;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.service.storageManage.EsssService;
import com.zxl.materialStorage.service.storageManage.EssssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @className: EsssServiceImpl
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/27
 **/
@Service
public class EsssServiceImpl extends ServiceImpl<EsssMapper, EssSpace> implements EsssService {

    @Autowired
    private EssService essService;
    @Autowired
    @Lazy
    private EssssService essssService;

    @Override
    public void insertNewOne(EssSpace essSpace) throws Exception{
        //先检查是否已经存在该编号的库区
        EssSpace space = getOne(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsssNo, essSpace.getEsssNo()));
        if(ObjectUtil.isNotNull(space)){
            throw new Exception("已存在该编号的库区");
        }
        //先改变上级仓库的计数
        EsStoreroom storeroom = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essSpace.getEssNo()));
        storeroom.setEssSpaceNumber(storeroom.getEssSpaceNumber()==null?1:storeroom.getEssSpaceNumber()+1);
        essService.updateOne(storeroom);
        //补全库区信息：物资库编号，时间值
        essSpace.setEsNo(storeroom.getEsNo()).setEsssTimeValue(SystemUtil.getTime()).setEsssTs(SystemUtil.getTime());
        save(essSpace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String esssId) throws Exception {
        //先更新上级仓库的计数
        EssSpace byId = getById(esssId);
        EsStoreroom storeroom = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, byId.getEssNo()));
        storeroom.setEssSpaceNumber(storeroom.getEssSpaceNumber()-1);
        essService.updateOne(storeroom);
        //再删除下级货架的数据
        List<EsssShelves> shelvesList = essssService.list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEsssNo, byId.getEsssNo()));
        List<String> essssIdList = new ArrayList();
        for (EsssShelves esssShelves : shelvesList) {
            essssIdList.add(esssShelves.getEssssId());
        }
        essssService.deleteMany(essssIdList);
        //再删除货区的数据
        removeById(esssId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> esssIdList) throws Exception {
        //先更新上级仓库的计数
        if (ObjectUtil.isEmpty(esssIdList)){
            return;
        }
        List<EssSpace> essSpaceList = listByIds(esssIdList);
        Map<String, Integer> essNoMap = new HashMap<>();
        for (EssSpace space : essSpaceList) {
            Integer count = essNoMap.get(space.getEssNo());
            essNoMap.put(space.getEssNo(),count == null ? 1 : ++count);
        }
        for (String essNo : essNoMap.keySet()) {
            EsStoreroom storeroom = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essNo));
            storeroom.setEssSpaceNumber(storeroom.getEssSpaceNumber()-essNoMap.get(essNo));
            essService.updateOne(storeroom);
        }
        //再删除下级货架的数据
        for (EssSpace space : essSpaceList) {
            List<EsssShelves> shelvesList = essssService.list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEsssNo, space.getEsssNo()));
            List<String> essssIdList = new ArrayList<>();
            for (EsssShelves esssShelves : shelvesList) {
                essssIdList.add(esssShelves.getEssssId());
            }
            essssService.deleteMany(essssIdList);
        }

        //再删除货区的数据
        removeBatchByIds(esssIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(EssSpace essSpace) throws Exception {
        //查看仓库编号有没有发生改变，若发生改变则需要更新仓库中的计数和库区中的编号
        EssSpace byId = getById(essSpace.getEsssId());
        if (!essSpace.getEssNo().equals(byId.getEssNo())){
            //先更新仓库的计数
            EsStoreroom front = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, byId.getEssNo()));
            front.setEssSpaceNumber(front.getEssSpaceNumber()-1);
            essService.updateOne(front);
            EsStoreroom back = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essSpace.getEssNo()));
            back.setEssSpaceNumber(back.getEssSpaceNumber()==null?1:back.getEssSpaceNumber()+1);
            essService.updateOne(back);
            //更新下级货架的相关编号
            List<EsssShelves> esssShelvesList = essssService.list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEsssNo, byId.getEsssNo()));
            for (EsssShelves esssShelves : esssShelvesList) {
                esssShelves.setEsssNo(essSpace.getEsssNo());
                essssService.updateOne(esssShelves);
            }
            //更新库区中的物资库的编号
            essSpace.setEsNo(back.getEsNo());
        }
        essSpace.setEsssTs(SystemUtil.getTime());
        updateById(essSpace);
    }

    @Override
    public Page<EssSpace> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex,pageSize));
    }

    @Override
    public List<EssSpace> selectAll() {
        return list();
    }

    @Override
    public List<String> selectEsssNoList() {
        List<EssSpace> essSpaceList = selectAll();
        List<String> esssNoList = new ArrayList<>();
        for (EssSpace space : essSpaceList) {
            esssNoList.add(space.getEsssNo());
        }
        return esssNoList;
    }

}
