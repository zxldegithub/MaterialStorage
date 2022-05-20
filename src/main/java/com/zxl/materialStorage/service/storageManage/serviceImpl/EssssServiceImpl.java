package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EssssMapper;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.model.pojo.EsssShelves;
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

import java.util.LinkedList;
import java.util.List;

/**
 * @className: EssssServiceImpl
 * @description: EssssServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/4/28
 **/

@Service
public class EssssServiceImpl extends ServiceImpl<EssssMapper, EsssShelves> implements EssssService {
    @Autowired
    private EsssService esssService;

    @Autowired
    private EssService essService;

    @Autowired
    @Lazy
    private MaterialEnterService materialEnterService;

    @Override
    public void insertNewOne(EsssShelves esssShelves) throws Exception {
        //判断是否已经存在该编号的货架
        EsssShelves existShelves = getOne(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssssNo, esssShelves.getEssssNo()));
        if (ObjectUtil.isNotNull(existShelves)) {
            throw new Exception("已存在该编号的货架");
        }
        //获取各上级的编号，货区编号已经有了，还需要仓库编号和物资库编号
        EssSpace space = esssService.getOne(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsssNo, esssShelves.getEsssNo()));
        //补全货架的属性
        esssShelves.setEssNo(space.getEssNo()).setEsNo(space.getEsNo())
                .setEssssTimeValue(SystemUtil.getTime()).setEssssTs(SystemUtil.getTime());
        save(esssShelves);
    }

    @Override
    public void deleteOne(String essssId) {
        removeById(essssId);
    }

    @Override
    public void deleteMany(List<String> essssIdList) {
        removeBatchByIds(essssIdList);
    }

    @Override
    public void updateOne(EsssShelves esssShelves) {
        //检查货区编号是否发生变化
        EsssShelves byId = getById(esssShelves.getEssssId());
        if (!byId.getEsssNo().equals(esssShelves.getEsssNo())) {
            //异步修改下级物资库、仓库、货区编号
            EssSpace back = esssService.getOne(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsssNo, esssShelves.getEsssNo()));
            materialEnterService.updateEsNosAndEssNosAndEsssNosWithEssssNo(byId.getEsssNo(),back.getEsssNo(),byId.getEssssNo());

            //更新货架的物资库编号、仓库编号
            esssShelves.setEsNo(back.getEsNo()).setEssNo(back.getEssNo());
        }
        //检查货架编号是否发生变化
        if (!byId.getEssssNo().equals(esssShelves.getEssssNo())){
            //异步修改下级货架编号
            materialEnterService.updateEssssNos(byId.getEssssNo(),esssShelves.getEssssNo());
        }
        esssShelves.setEssssTs(SystemUtil.getTime());
        updateById(esssShelves);
    }

    @Override
    public Page<EsssShelves> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex, pageSize));
    }

    @Override
    public List<EsssShelves> selectAll() {
        return list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNos(String esNoOld, String esNoNew) {
        List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEsNo, esNoOld));
        for (EsssShelves esssShelves : esssShelvesList) {
            esssShelves.setEsNo(esNoOld).setEssssTs(SystemUtil.getTime());
        }
        updateBatchById(esssShelvesList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNosByDel(List<String> esNoList) {
        LinkedList<EsssShelves> esssShelves = new LinkedList<>();
        for (String esNo : esNoList) {
            List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEsNo, esNo));
            for (EsssShelves shelves : esssShelvesList) {
                shelves.setEsNo(null).setEssssTs(SystemUtil.getTime());
            }
            esssShelves.addAll(esssShelvesList);
        }
        updateBatchById(esssShelves);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void asyncDeleteByEssNo(String essNo) {
        List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssNo, essNo));
        removeBatchByIds(esssShelvesList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEssNos(String essNoOld, String essNoNew) {
        List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssNo, essNoOld));
        for (EsssShelves shelves : esssShelvesList) {
            shelves.setEssNo(essNoNew).setEssssTs(SystemUtil.getTime());
        }
        updateBatchById(esssShelvesList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNosWithEssNo(String esNoOld, String esNoNew, String essNo) {
        List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssNo, essNo).eq(EsssShelves::getEsNo, esNoOld));
        for (EsssShelves shelves : esssShelvesList) {
            shelves.setEsNo(esNoNew).setEssssTs(SystemUtil.getTime());
        }
        updateBatchById(esssShelvesList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void asyncDeleteByEsssNo(String esssNo) {
        List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEsssNo, esssNo));
        removeBatchByIds(esssShelvesList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsssNos(String esssNoOld, String esssNoNew) {
        List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEsssNo, esssNoOld));
        for (EsssShelves shelves : esssShelvesList) {
            shelves.setEsssNo(esssNoNew).setEssssTs(SystemUtil.getTime());
        }
        updateBatchById(esssShelvesList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEssNosAndEsNosWithEsssNo(String essNoOld, String essNoNew, String esssNo) {
        EsStoreroom esStoreroom = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essNoNew));
        List<EsssShelves> esssShelvesList = list(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssNo, essNoOld).eq(EsssShelves::getEsssNo, esssNo));
        for (EsssShelves shelves : esssShelvesList) {
            shelves.setEsNo(esStoreroom.getEsNo()).setEssNo(essNoNew).setEssssTs(SystemUtil.getTime());
        }
        updateBatchById(esssShelvesList);
    }
}
