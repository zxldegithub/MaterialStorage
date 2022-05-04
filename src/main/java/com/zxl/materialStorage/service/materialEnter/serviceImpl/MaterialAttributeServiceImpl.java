package com.zxl.materialStorage.service.materialEnter.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.materialEnter.MaterialAttributeMapper;
import com.zxl.materialStorage.model.pojo.MaterialAttribute;
import com.zxl.materialStorage.service.materialEnter.MaterialAttributeService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @className: MaterialAttributeServiceImpl
 * @description: MaterialAttributeServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/
@Service
public class MaterialAttributeServiceImpl extends ServiceImpl<MaterialAttributeMapper, MaterialAttribute> implements MaterialAttributeService {

    @Override
    public void insertNewOne(MaterialAttribute materialAttribute) throws Exception {
        MaterialAttribute existMaterialAttribute = getOne(new QueryWrapper<MaterialAttribute>().lambda().eq(MaterialAttribute::getEmaNo, materialAttribute.getEmaNo()));
        if (ObjectUtil.isNotNull(existMaterialAttribute)){
            throw new Exception("已存在该编号的物资属性");
        }
        materialAttribute.setEmaDateProduct(SystemUtil.formatRequestTime(materialAttribute.getEmaDateProduct()))
                .setEmaTimeValue(SystemUtil.getTime()).setEmaTs(SystemUtil.getTime());
        save(materialAttribute);
    }

    @Override
    public void deleteOne(String emaId) {
        removeById(emaId);
    }

    @Override
    public void deleteMany(List<String> emaIdList) {
        removeByIds(emaIdList);
    }

    @Override
    public void updateOne(MaterialAttribute materialAttribute) {
        materialAttribute.setEmaDateProduct(SystemUtil.formatRequestTime(materialAttribute.getEmaDateProduct())).setEmaTs(SystemUtil.getTime());
        updateById(materialAttribute);
    }

    @Override
    public List<MaterialAttribute> selectAll() {
        return list();
    }
}
