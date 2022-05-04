package com.zxl.materialStorage.service.materialEnter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.MaterialPacking;

import java.util.List;

/**
 * @className: MaterialPackingService
 * @description: MaterialPackingService
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/

public interface MaterialPackingService extends IService<MaterialPacking> {
    void insertNewOne(MaterialPacking materialPacking) throws Exception;

    void deleteOne(String empId);

    void deleteMany(List<String> empIdList);

    void updateOne(MaterialPacking materialPacking);

    List<MaterialPacking> selectAll();
}
