package com.zxl.materialStorage.controller.materialEnter;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.MaterialEnter;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @className: MaterialEnterController
 * @description: MaterialEnterController
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/
@RestController
@RequestMapping("/eme")
@Slf4j
public class MaterialEnterController {
    @Autowired
    private MaterialEnterService materialEnterService;

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody MaterialEnter materialEnter) {
        try {
            if (ObjectUtil.isNull(materialEnter)) {
                return ApiResult.blank();
            }
            materialEnterService.insertNewOne(materialEnter);
        } catch (Exception e) {
            log.error("物资入库出错", e);
            return ApiResult.error(500, "物资入库出错", e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "emeId") String emeId) {
        try {
            if (StringUtils.isEmpty(emeId)) {
                return ApiResult.blank();
            }
            MaterialEnter byId = materialEnterService.getById(emeId);
            if (byId.isEmeIsAccept()){
                return ApiResult.error();
            }
            materialEnterService.deleteOne(emeId);
        } catch (Exception e) {
            log.error("删除物资入库出错", e);
            return ApiResult.error(500, "删除物资入库出错", e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> emeIdList) {
        try {
            if (ObjectUtil.isEmpty(emeIdList)) {
                return ApiResult.blank();
            }
            List<MaterialEnter> materialEnters = materialEnterService.listByIds(emeIdList);
            for (MaterialEnter materialEnter : materialEnters) {
                if (materialEnter.isEmeIsAccept()){
                    return ApiResult.error();
                }
            }
            materialEnterService.deleteMany(emeIdList);
        } catch (Exception e) {
            log.error("批量删除物资入库出错", e);
            return ApiResult.error(500, "批量删除物资入库出错", e);
        }
        return ApiResult.success();
    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody MaterialEnter materialEnter) {
        try {
            if (ObjectUtil.isNull(materialEnter)) {
                return ApiResult.blank();
            }
            MaterialEnter byId = materialEnterService.getById(materialEnter.getEmeId());
            if (byId.isEmeIsAccept()){
                return ApiResult.error();
            }
            materialEnterService.updateOne(materialEnter);
        } catch (Exception e) {
            log.error("更新物资入库出错");
            return ApiResult.error(500, "更新物资入库出错", e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectAll")
    public ApiResult<List<MaterialEnter>> selectAll() {
        return ApiResult.success(materialEnterService.selectAll());
    }

    @GetMapping("/selectAlready")
    public ApiResult<List<MaterialEnter>> selectAlready(){
        return ApiResult.success(materialEnterService.selectAlready());
    }

    @PostMapping("/materialAccept")
    public ApiResult<Object> materialAccept(@RequestBody MaterialEnter materialEnter){
        try {
            if (ObjectUtil.isNull(materialEnter)){
                return ApiResult.blank();
            }
            materialEnterService.materialAccept(materialEnter);
        } catch (Exception e) {
            log.error("物资验收失败");
            return ApiResult.error(500,"物资验收失败",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/getAllNeedInfo")
    public ApiResult<Map<String, List<String>>> getAllNeedInfo(){
        return ApiResult.success(materialEnterService.getAllNeedInfo());
    }
}
