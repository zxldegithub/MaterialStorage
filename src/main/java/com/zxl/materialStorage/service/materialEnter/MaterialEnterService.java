package com.zxl.materialStorage.service.materialEnter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.pojo.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    void updateEsNos(String esNoOld, String esNoNew);

    void updateEmtNos(MaterialType byId, MaterialType materialType);

    void updateEmpNos(MaterialPacking byId, MaterialPacking materialPacking);

    void updateEmaNos(MaterialAttribute byId, MaterialAttribute materialAttribute);

    List<MaterialEnter> selectAll();

    List<MaterialEnter> selectAlready();

    void materialAccept(MaterialEnter materialEnter);

    Map<String, List<String>> getAllNeedInfo();

    Set<MaterialEnter> selectAccept();

    void updateEsNosByDel(List<String> esNoList);

    void updateEssNos(String essNoOld, String essNoNew);

    void updateEsNosWithEssNo(String esNoOld, String esNoNew, String essNo);

    void updateEsssNos(String esssNoOld, String esssNoNew);

    void updateEssNosAndEsNosWithEsssNo(String essNoOld, String essNoNew, String esssNo);

    void updateEsNosAndEssNosAndEsssNosWithEssssNo(String esssNoOld, String esssNoNew, String essssNo);

    void updateEssssNos(String essssNoOld, String essssNoNew);
}
