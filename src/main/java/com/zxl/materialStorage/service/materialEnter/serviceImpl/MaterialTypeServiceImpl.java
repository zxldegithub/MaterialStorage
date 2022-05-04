package com.zxl.materialStorage.service.materialEnter.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.materialEnter.MaterialTypeMapper;
import com.zxl.materialStorage.model.pojo.MaterialType;
import com.zxl.materialStorage.service.materialEnter.MaterialTypeService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @className: MaterialTypeServiceImpl
 * @description: MaterialTypeServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/
@Service
public class MaterialTypeServiceImpl extends ServiceImpl<MaterialTypeMapper, MaterialType> implements MaterialTypeService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(MaterialType materialType) throws Exception {
        MaterialType existMaterialType = getOne(new QueryWrapper<MaterialType>().lambda().eq(MaterialType::getEmtNo, materialType.getEmtNo()));
        if (ObjectUtil.isNotNull(existMaterialType)){
            throw new Exception("已存在该编号的物资类型");
        }
        materialType.setEmtTimeValue(SystemUtil.getTime()).setEmtTs(SystemUtil.getTime());
        save(materialType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String emtId) {
        removeById(emtId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> emtIdList) {
        removeByIds(emtIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(MaterialType materialType) {
        materialType.setEmtTs(SystemUtil.getTime());
        updateById(materialType);
    }

    @Override
    public List<MaterialType> selectAll() {
        return list();
    }
}
