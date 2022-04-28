package com.zxl.materialStorage.service.storageManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.EssSpace;

import java.util.List;
import java.util.Set;

/**
 * @className: EsssService
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/27
 **/

public interface EsssService extends IService<EssSpace> {
    void insertNewOne(EssSpace essSpace) throws Exception;
    void deleteOne(String esssId) throws Exception;
    void deleteMany(List<String> esssIdList) throws Exception;
    void updateOne(EssSpace essSpace) throws Exception;
    Page<EssSpace> selectByPage(Integer pageIndex,Integer pageSize);
}
