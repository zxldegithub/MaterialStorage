package com.zxl.materialStorage.controller.materialEnter;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.MaterialAttribute;
import com.zxl.materialStorage.service.materialEnter.MaterialAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @className: MaterialAttributeController
 * @description: MaterialAttributeController
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/
@RestController
@RequestMapping("/ema")
@Slf4j
public class MaterialAttributeController {
    @Autowired
    private MaterialAttributeService materialAttributeService;

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody MaterialAttribute materialAttribute) {
        try {
            if (ObjectUtil.isNull(materialAttribute)) {
                return ApiResult.blank();
            }
            materialAttributeService.insertNewOne(materialAttribute);
        } catch (Exception e) {
            log.error("新增物资属性出错", e);
            return ApiResult.error(500, "新增物资属性出错", e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用则拒绝删除
    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "emaId") String emaId) {
        try {
            if (StringUtils.isEmpty(emaId)) {
                return ApiResult.blank();
            }
            materialAttributeService.deleteOne(emaId);
        } catch (Exception e) {
            log.error("删除物资属性出错", e);
            return ApiResult.error(500, "删除物资属性出错", e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用则拒绝删除
    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> emaIdList) {
        try {
            if (ObjectUtil.isEmpty(emaIdList)) {
                return ApiResult.blank();
            }
            materialAttributeService.deleteMany(emaIdList);
        } catch (Exception e) {
            log.error("批量删除物资属性出错", e);
            return ApiResult.error(500, "批量删除物资属性出错", e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用，则更新引用
    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody MaterialAttribute materialAttribute) {
        try {
            if (ObjectUtil.isNull(materialAttribute)) {
                return ApiResult.blank();
            }
            materialAttributeService.updateOne(materialAttribute);
        } catch (Exception e) {
            log.error("更新物资属性出错");
            return ApiResult.error(500, "更新物资属性出错", e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectAll")
    public ApiResult<List<MaterialAttribute>> selectAll() {
        return ApiResult.success(materialAttributeService.selectAll());
    }
}
