package com.zxl.materialStorage.service.storageManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;

import java.util.List;
import java.util.Set;

/**
 * @className: EssService
 * @description: EssService
 * @author: ZhangXiaolei
 * @date: 2022/4/23
 **/

public interface EssService extends IService<EsStoreroom> {
    void insertNewOne(EsStoreroom esStoreroom) throws Exception;

    void deleteOne(String essId) throws Exception;

    void deleteMany(List<String> essIdList) throws Exception;

    void updateOne(EsStoreroom esStoreroom) throws Exception;

    Page<EsStoreroom> selectByPage(Integer pageIndex, Integer pageSize);

    List<EsStoreroom> selectAll();

    List<String> selectEssNoList();

    void updateEsNos(String esNoOld, String esNoNew);

    void updateEsNosByDel(List<String> esNoList);

    void deleteCount(String essNo, Integer number);

    void addCount(String essNo);
}
