package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EssMapper;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.storageManage.EsService;
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
 * @date: 2022/4/23
 **/
@Service
public class EssServiceImpl extends ServiceImpl<EssMapper, EsStoreroom> implements EssService {
    @Autowired
    private EsService esService;

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
    public void insertNewOne(EsStoreroom esStoreroom) throws Exception {
        //先检查是否已经存在该编号的仓库
        EsStoreroom storeroom = getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, esStoreroom.getEssNo()));
        if (ObjectUtil.isNotNull(storeroom)) {
            throw new Exception("已存在此标号的仓库");
        }
        //异步更新上级物资库的计数
        esService.addCount(esStoreroom.getEsNo());

        //再补全仓库信息：时间值
        esStoreroom.setEssTimeValue(SystemUtil.getTime()).setEssTs(SystemUtil.getTime());
        save(esStoreroom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String essId) throws Exception {
        //异步更新上级物资库的计数
        EsStoreroom byId = getById(essId);
        esService.deleteCount(byId.getEsNo(), 1);
        //异步删除下级库区、货架
        esssService.asyncDeleteByEssNo(byId.getEssNo());
        essssService.asyncDeleteByEssNo(byId.getEssNo());
        //删除仓库
        removeById(essId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> essIdList) throws Exception {
        //异步更新上级仓库的计数
        List<EsStoreroom> esStoreroomList = listByIds(essIdList);
        Map<String, Integer> esNoMap = new HashMap<>();
        for (EsStoreroom storeroom : esStoreroomList) {
            Integer number = esNoMap.get(storeroom.getEsNo());
            esNoMap.put(storeroom.getEsNo(), number == null ? 1 : ++number);
        }
        for (String esNo : esNoMap.keySet()) {
            esService.deleteCount(esNo, esNoMap.get(esNo));
        }

        //异步删除下级关联的库区、货架
        Set<String> essNoSet = new HashSet<>();
        for (EsStoreroom storeroom : esStoreroomList) {
            essNoSet.add(storeroom.getEssNo());
        }
        for (String essNo : essNoSet) {
            esssService.asyncDeleteByEssNo(essNo);
            essssService.asyncDeleteByEssNo(essNo);
        }
        //删除仓库
        removeByIds(essIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(EsStoreroom esStoreroom) throws Exception {
        EsStoreroom byId = getById(esStoreroom.getEssId());
        //检查物资库有没有发生变化
        if (!byId.getEsNo().equals(esStoreroom.getEsNo())) {
            //异步更新上级物资库计数
            esService.addCount(esStoreroom.getEsNo());
            esService.deleteCount(byId.getEsNo(), 1);
            //异步更新下级物资库编号
            esssService.updateEsNosWithEssNo(byId.getEsNo(), esStoreroom.getEsNo(), byId.getEssNo());
            essssService.updateEsNosWithEssNo(byId.getEsNo(), esStoreroom.getEsNo(), byId.getEssNo());
            materialEnterService.updateEsNosWithEssNo(byId.getEsNo(), esStoreroom.getEsNo(), byId.getEssNo());
        }

        //检查仓库编号有没有发生变化
        if (!byId.getEssNo().equals(esStoreroom.getEssNo())) {
            //异步更新下级仓库编号
            esssService.updateEssNos(byId.getEssNo(), esStoreroom.getEssNo());
            essssService.updateEssNos(byId.getEssNo(), esStoreroom.getEssNo());
            materialEnterService.updateEssNos(byId.getEssNo(), esStoreroom.getEssNo());
        }

        //更新仓库
        esStoreroom.setEssTs(SystemUtil.getTime());
        updateById(esStoreroom);
    }

    @Override
    public Page<EsStoreroom> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex, pageSize));
    }

    @Override
    public List<EsStoreroom> selectAll() {
        return list();
    }

    @Override
    public List<String> selectEssNoList() {
        List<EsStoreroom> esStoreroomList = selectAll();
        List<String> essNoList = new ArrayList();
        for (EsStoreroom esStoreroom : esStoreroomList) {
            essNoList.add(esStoreroom.getEssNo());
        }
        return essNoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNos(String esNoOld, String esNoNew) {
        List<EsStoreroom> esStoreroomList = list(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEsNo, esNoOld));
        for (EsStoreroom storeroom : esStoreroomList) {
            storeroom.setEsNo(esNoNew).setEssTs(SystemUtil.getTime());
        }
        updateBatchById(esStoreroomList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNosByDel(List<String> esNoList) {
        LinkedList<EsStoreroom> esStorerooms = new LinkedList<>();
        for (String esNo : esNoList) {
            List<EsStoreroom> esStoreroomList = list(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEsNo, esNo));
            for (EsStoreroom storeroom : esStoreroomList) {
                storeroom.setEsNo(null).setEssTs(SystemUtil.getTime());
            }
            esStorerooms.addAll(esStoreroomList);
        }
        updateBatchById(esStorerooms);
    }

    @Override
    public void deleteCount(String essNo, Integer number) {
        EsStoreroom esStoreroom = getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essNo));
        esStoreroom.setEssSpaceNumber(esStoreroom.getEssSpaceNumber() - number).setEssTs(SystemUtil.getTime());
        updateById(esStoreroom);
    }

    @Override
    public void addCount(String essNo) {
        EsStoreroom esStoreroom = getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essNo));
        esStoreroom.setEssSpaceNumber(esStoreroom.getEssSpaceNumber() + 1).setEssTs(SystemUtil.getTime());
        updateById(esStoreroom);
    }
}
