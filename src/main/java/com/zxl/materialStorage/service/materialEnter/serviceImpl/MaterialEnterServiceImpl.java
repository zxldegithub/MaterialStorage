package com.zxl.materialStorage.service.materialEnter.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.materialEnter.MaterialEnterMapper;
import com.zxl.materialStorage.model.pojo.*;
import com.zxl.materialStorage.service.materialEnter.MaterialAttributeService;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.materialEnter.MaterialPackingService;
import com.zxl.materialStorage.service.materialEnter.MaterialTypeService;
import com.zxl.materialStorage.service.storageManage.EssssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: MaterialEnterServiceImpl
 * @description: MaterialEnterServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/
@Service
public class MaterialEnterServiceImpl extends ServiceImpl<MaterialEnterMapper, MaterialEnter> implements MaterialEnterService {
    @Autowired
    private EssssService essssService;

    @Autowired
    private MaterialTypeService materialTypeService;

    @Autowired
    private MaterialPackingService materialPackingService;

    @Autowired
    private MaterialAttributeService materialAttributeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(MaterialEnter materialEnter) throws Exception {
        //校验
        MaterialEnter existMaterialEnter = getOne(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmeNo, materialEnter.getEmeNo()));
        if (ObjectUtil.isNotNull(existMaterialEnter)) {
            throw new Exception("已存在此编号的入库物资");
        }
        //补全
        EsssShelves esssShelves = essssService.getOne(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssssNo, materialEnter.getEssssNo()));
        materialEnter.setEsNo(esssShelves.getEsNo()).setEssNo(esssShelves.getEsssNo()).setEsssNo(esssShelves.getEsssNo())
                .setEmeTimeValue(SystemUtil.getTime()).setEmeTs(SystemUtil.getTime());
        save(materialEnter);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String emeId) {
        removeById(emeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> emeIdList) {
        removeByIds(emeIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(MaterialEnter materialEnter) {
        EsssShelves esssShelves = essssService.getOne(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssssNo, materialEnter.getEssssNo()));
        materialEnter.setEsNo(esssShelves.getEsNo()).setEssNo(esssShelves.getEssNo()).setEsssNo(esssShelves.getEsssNo())
                .setEmeTs(SystemUtil.getTime());
        updateById(materialEnter);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEmtNos(MaterialType byId, MaterialType materialType){
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmtNo, byId.getEmtNo()));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEmtNo(materialType.getEmtNo()).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEmpNos(MaterialPacking byId, MaterialPacking materialPacking) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmpNo, byId.getEmpNo()));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEmpNo(materialPacking.getEmpNo()).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEmaNos(MaterialAttribute byId, MaterialAttribute materialAttribute) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmaNo, byId.getEmaNo()));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEmaNo(materialAttribute.getEmaNo()).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    public List<MaterialEnter> selectAll() {
        return list();
    }

    @Override
    public List<MaterialEnter> selectAlready() {
        return list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::isEmeIsAccept, false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void materialAccept(MaterialEnter materialEnter) {
        materialEnter.setEmeIsAccept(true).setEmeTs(SystemUtil.getTime());
        updateById(materialEnter);
    }

    @Override
    public Map<String, List<String>> getAllNeedInfo() {
        Map<String,List<String>> map = new HashMap<>();
        List<EsssShelves> shelvesList = essssService.list();
        List<String> essssNoList = new ArrayList<>();
        for (EsssShelves esssShelves : shelvesList) {
            essssNoList.add(esssShelves.getEssssNo());
        }
        map.put("emaNos",essssNoList);

        List<MaterialType> materialTypeList = materialTypeService.list();
        List<String> emtNoList = new ArrayList<>();
        for (MaterialType materialType : materialTypeList) {
            emtNoList.add(materialType.getEmtNo());
        }
        map.put("emtNos",emtNoList);

        List<MaterialPacking> materialPackingList = materialPackingService.list();
        List<String> empNoList = new ArrayList<>();
        for (MaterialPacking materialPacking : materialPackingList) {
            empNoList.add(materialPacking.getEmpNo());
        }
        map.put("empNos",empNoList);

        List<MaterialAttribute> materialAttributeList = materialAttributeService.list();
        List<String> emaNoList = new ArrayList<>();
        List<String> emaNameList = new ArrayList<>();
        for (MaterialAttribute materialAttribute : materialAttributeList) {
            emaNoList.add(materialAttribute.getEmaNo());
            emaNameList.add(materialAttribute.getEmaName());
        }
        map.put("emaNos",emaNoList);
        map.put("emaNames",emaNameList);

        return map;
    }
}
