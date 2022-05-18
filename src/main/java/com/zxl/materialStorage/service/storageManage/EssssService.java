package com.zxl.materialStorage.service.storageManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.EsssShelves;

import java.util.List;

/**
 * @className: EssssService
 * @description: EssssService
 * @author: ZhangXiaolei
 * @date: 2022/4/28
 **/

public interface EssssService extends IService<EsssShelves> {
    void insertNewOne(EsssShelves esssShelves) throws Exception;

    void deleteOne(String essssId);

    void deleteMany(List<String> essssIdList);

    void updateOne(EsssShelves esssShelves);

    Page<EsssShelves> selectByPage(Integer pageIndex, Integer pageSize);

    List<EsssShelves> selectAll();

    void updateEsNos(String esNoOld, String esNoNew);

    void updateEsNosByDel(List<String> esNoList);

    void asyncDeleteByEssNo(String essNo);

    void updateEssNos(String essNoOld, String essNoNew);

    void updateEsNosWithEssNo(String esNoOld, String esNoNew, String essNo);

    void asyncDeleteByEsssNo(String esssNo);

    void updateEsssNos(String esssNoOld, String esssNoNew);

    void updateEssNosAndEsNosWithEsssNo(String essNoOld, String essNoNew, String esssNo);
}
