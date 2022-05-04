package com.zxl.materialStorage.service.materialEnter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.MaterialAttribute;

import java.util.List;

/**
 * @className: MaterialAttributeService
 * @description: MaterialAttributeService
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/

public interface MaterialAttributeService extends IService<MaterialAttribute> {
    void insertNewOne(MaterialAttribute materialAttribute) throws Exception;

    void deleteOne(String emaId);

    void deleteMany(List<String> emaIdList);

    void updateOne(MaterialAttribute materialAttribute);

    List<MaterialAttribute> selectAll();
}
