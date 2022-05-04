package com.zxl.materialStorage.service.materialEnter.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.materialEnter.MaterialPackingMapper;
import com.zxl.materialStorage.model.pojo.MaterialPacking;
import com.zxl.materialStorage.service.materialEnter.MaterialPackingService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @className: MaterialPackingServiceImpl
 * @description: MaterialPackingServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/
@Service
public class MaterialPackingServiceImpl extends ServiceImpl<MaterialPackingMapper, MaterialPacking> implements MaterialPackingService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(MaterialPacking materialPacking) throws Exception {
        MaterialPacking existMaterialPacking = getOne(new QueryWrapper<MaterialPacking>().lambda().eq(MaterialPacking::getEmpNo, materialPacking.getEmpNo()));
        if (ObjectUtil.isNotNull(existMaterialPacking)){
            throw new Exception("已存在该编号的打包方式");
        }
        materialPacking.setEmpTimeValue(SystemUtil.getTime()).setEmpTs(SystemUtil.getTime());
        save(materialPacking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String empId) {
        removeById(empId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> empIdList) {
        removeByIds(empIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(MaterialPacking materialPacking) {
        materialPacking.setEmpTs(SystemUtil.getTime());
        updateById(materialPacking);
    }

    @Override
    public List<MaterialPacking> selectAll() {
        return list();
    }
}
