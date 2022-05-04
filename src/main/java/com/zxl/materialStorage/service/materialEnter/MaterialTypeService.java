package com.zxl.materialStorage.service.materialEnter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.MaterialType;

import java.util.List;

/**
 * @className: MaterialTypeService
 * @description: MaterialTypeService
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/

public interface MaterialTypeService extends IService<MaterialType> {
    void insertNewOne(MaterialType materialType) throws Exception;
    void deleteOne(String emtId);
    void deleteMany(List<String> emtIdList);
    void updateOne(MaterialType materialType);
    List<MaterialType> selectAll();
}
