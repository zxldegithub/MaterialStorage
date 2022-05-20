package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EsssMapper;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.service.storageManage.EsssService;
import com.zxl.materialStorage.service.storageManage.EssssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @className: EsssServiceImpl
 * @description: EsssServiceImpl
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

    @Autowired
    @Lazy
    private MaterialEnterService materialEnterService;

    @Override
    public void insertNewOne(EssSpace essSpace) throws Exception {
        //先检查是否已经存在该编号的库区
        EssSpace space = getOne(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsssNo, essSpace.getEsssNo()));
        if (ObjectUtil.isNotNull(space)) {
            throw new Exception("已存在该编号的库区");
        }
        //因为构建对象需要获取信息，所以只能做同步改变上级仓库的计数
        EsStoreroom storeroom = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essSpace.getEssNo()));
        storeroom.setEssSpaceNumber(storeroom.getEssSpaceNumber() == null ? 1 : storeroom.getEssSpaceNumber() + 1).setEssTs(SystemUtil.getTime());
        essService.updateById(storeroom);
        //补全库区信息：物资库编号，时间值
        essSpace.setEsNo(storeroom.getEsNo()).setEsssTimeValue(SystemUtil.getTime()).setEsssTs(SystemUtil.getTime());
        save(essSpace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String esssId) throws Exception {
        //异步更新上级仓库的计数
        EssSpace byId = getById(esssId);
        essService.deleteCount(byId.getEssNo(), 1);

        //异步删除下级货架
        essssService.asyncDeleteByEsssNo(byId.getEsssNo());

        //再删除货区的数据
        removeById(esssId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> esssIdList) throws Exception {
        //异步更新上级仓库的计数
        List<EssSpace> essSpaceList = listByIds(esssIdList);
        Map<String, Integer> essNoMap = new HashMap<>();
        for (EssSpace space : essSpaceList) {
            Integer count = essNoMap.get(space.getEssNo());
            essNoMap.put(space.getEssNo(), count == null ? 1 : ++count);
        }
        for (String essNo : essNoMap.keySet()) {
            essService.deleteCount(essNo,essNoMap.get(essNo));
        }

        //异步删除下级货架的数据
        Set<String> esssNoSet = new HashSet<>();
        for (EssSpace space : essSpaceList) {
            esssNoSet.add(space.getEsssNo());
        }
        for (String esssNo : esssNoSet) {
            essssService.asyncDeleteByEsssNo(esssNo);
        }

        //再删除货区的数据
        removeBatchByIds(esssIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(EssSpace essSpace) throws Exception {
        //检查仓库编号有没有发生变化
        EssSpace byId = getById(essSpace.getEsssId());
        if (!byId.getEssNo().equals(essSpace.getEssNo())) {
            //异步更新仓库的计数
            essService.deleteCount(byId.getEssNo(),1);
            essService.addCount(essSpace.getEssNo());

            //异步更新下级仓库和物资库编号
            EsStoreroom back = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essSpace.getEssNo()));
            essssService.updateEssNosAndEsNosWithEsssNo(byId.getEssNo(),back.getEssNo(),byId.getEsssNo());
            materialEnterService.updateEssNosAndEsNosWithEsssNo(byId.getEssNo(),back.getEssNo(),byId.getEsssNo());

            //同时更新库区中的物资库的编号
            essSpace.setEsNo(back.getEsNo());
        }
        //检查库区编号有没有发生变化
        if (!byId.getEsssNo().equals(essSpace.getEsssNo())){
            //异步更新下级库区编号
            essssService.updateEsssNos(byId.getEsssNo(),essSpace.getEsssNo());
            materialEnterService.updateEsssNos(byId.getEsssNo(),essSpace.getEsssNo());
        }
        essSpace.setEsssTs(SystemUtil.getTime());
        updateById(essSpace);
    }

    @Override
    public Page<EssSpace> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex, pageSize));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNos(String esNoOld, String esNoNew) {
        List<EssSpace> essSpaceList = list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsNo, esNoOld));
        for (EssSpace space : essSpaceList) {
            space.setEsNo(esNoNew).setEsssTs(SystemUtil.getTime());
        }
        updateBatchById(essSpaceList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNosByDel(List<String> esNoList) {
        LinkedList<EssSpace> essSpaces = new LinkedList<>();
        for (String esNo : esNoList) {
            List<EssSpace> essSpaceList = list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsNo, esNo));
            for (EssSpace space : essSpaceList) {
                space.setEsNo(null).setEsssTs(SystemUtil.getTime());
            }
            essSpaces.addAll(essSpaceList);
        }
        updateBatchById(essSpaces);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void asyncDeleteByEssNo(String essNo) {
        List<EssSpace> essSpaceList = list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEssNo, essNo));
        removeBatchByIds(essSpaceList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEssNos(String essNoOld, String essNoNew) {
        List<EssSpace> essSpaceList = list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEssNo, essNoOld));
        for (EssSpace space : essSpaceList) {
            space.setEssNo(essNoNew).setEsssTs(SystemUtil.getTime());
        }
        updateBatchById(essSpaceList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNosWithEssNo(String esNoOld, String esNoNew, String essNo) {
        List<EssSpace> essSpaceList = list(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEssNo, essNo).eq(EssSpace::getEsNo, esNoOld));
        for (EssSpace space : essSpaceList) {
            space.setEsNo(esNoNew).setEsssTs(SystemUtil.getTime());
        }
        updateBatchById(essSpaceList);
    }

}
