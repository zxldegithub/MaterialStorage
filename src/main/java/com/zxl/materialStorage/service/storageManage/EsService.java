package com.zxl.materialStorage.service.storageManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.ErStorage;

import java.util.List;

/**
 * @className: EsService
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/

public interface EsService extends IService<ErStorage> {
    void insertNewOne(ErStorage erStorage) throws Exception;
    void deleteOne(String esId);
    void deleteMany(List<String> esIdList);
    void updateOne(ErStorage erStorage) throws Exception;
    Page<ErStorage> selectAll(int pageIndex, int pageSize);
}
