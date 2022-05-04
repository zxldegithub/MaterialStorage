package com.zxl.materialStorage.service.materialEnter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.MaterialEnter;

import java.util.List;

/**
 * @className: MaterialEnterService
 * @description: MaterialEnterService
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/

public interface MaterialEnterService extends IService<MaterialEnter> {
    void insertNewOne(MaterialEnter materialEnter) throws Exception;

    void deleteOne(String emeId);

    void deleteMany(List<String> emeIdList);

    void updateOne(MaterialEnter materialEnter);

    List<MaterialEnter> selectAll();

    List<MaterialEnter> selectAlready();

    void materialAccept();
}
