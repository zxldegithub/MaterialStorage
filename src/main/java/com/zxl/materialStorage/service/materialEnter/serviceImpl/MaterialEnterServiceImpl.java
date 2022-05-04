package com.zxl.materialStorage.service.materialEnter.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.materialEnter.MaterialEnterMapper;
import com.zxl.materialStorage.model.pojo.MaterialEnter;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @className: MaterialEnterServiceImpl
 * @description: MaterialEnterServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/
@Service
public class MaterialEnterServiceImpl extends ServiceImpl<MaterialEnterMapper, MaterialEnter> implements MaterialEnterService {
    @Override
    public void insertNewOne(MaterialEnter materialEnter) throws Exception {
        MaterialEnter existMaterialEnter = getOne(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmeNo, materialEnter.getEmeNo()));
        if (ObjectUtil.isNotNull(existMaterialEnter)){
            throw new Exception("已存在此编号的入库物资");
        }
        materialEnter.setEmeTimeValue(SystemUtil.getTime()).setEmeTs(SystemUtil.getTime());
        save(materialEnter);
    }

    @Override
    public void deleteOne(String emeId) {
        removeById(emeId);
    }

    @Override
    public void deleteMany(List<String> emeIdList) {
        removeByIds(emeIdList);
    }

    @Override
    public void updateOne(MaterialEnter materialEnter) {
        materialEnter.setEmeTs(SystemUtil.getTime());
        updateById(materialEnter);
    }

    @Override
    public List<MaterialEnter> selectAll() {
        return list();
    }

    @Override
    public List<MaterialEnter> selectAlready() {
        return null;
    }

    @Override
    public void materialAccept() {

    }
}
