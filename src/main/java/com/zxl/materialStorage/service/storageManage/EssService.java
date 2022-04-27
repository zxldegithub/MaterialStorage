package com.zxl.materialStorage.service.storageManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;

import java.util.List;
import java.util.Set;

/**
 * @className: EssService
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/23
 **/

public interface EssService extends IService<EsStoreroom> {
    void insertNewOne(EsStoreroom esStoreroom) throws Exception;
    void deleteOne(String essId);
    void deleteMany(List<String> essIdList);
    void updateOne(EsStoreroom esStoreroom);
    Page<EsStoreroom> selectByPage(Integer pageIndex,Integer pageSize);
    Set<EsStoreroom> selectSetFromRedis();
}
