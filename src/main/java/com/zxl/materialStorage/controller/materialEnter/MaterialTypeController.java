package com.zxl.materialStorage.controller.materialEnter;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.MaterialEnter;
import com.zxl.materialStorage.model.pojo.MaterialType;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.materialEnter.MaterialTypeService;
import com.zxl.materialStorage.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: MaterialTypeController
 * @description: MaterialTypeController
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/
@RestController
@RequestMapping("/emt")
@Slf4j
public class MaterialTypeController {
    @Autowired
    private MaterialTypeService materialTypeService;
    @Autowired
    private MaterialEnterService materialEnterService;

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody MaterialType materialType) {
        try {
            if (ObjectUtil.isNull(materialType)) {
                return ApiResult.blank();
            }
            materialTypeService.insertNewOne(materialType);
        } catch (Exception e) {
            log.error("新增物资类型出错", e);
            return ApiResult.error(500, "新增物资类型出错", e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "emtId") String emtId) {
        try {
            if (StringUtils.isEmpty(emtId)) {
                return ApiResult.blank();
            }
            //存在引用，拒绝删除
            MaterialType byId = materialTypeService.getById(emtId);
            MaterialEnter existMaterialEnter = materialEnterService.getOne(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmtNo, byId.getEmtNo()));
            if (ObjectUtil.isNotNull(existMaterialEnter)) {
                return ApiResult.error();
            }
            materialTypeService.deleteOne(emtId);
        } catch (Exception e) {
            log.error("删除物资类型出错", e);
            return ApiResult.error(500, "删除物资类型出错", e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> emtIdList) {
        try {
            if (ObjectUtil.isEmpty(emtIdList)) {
                return ApiResult.blank();
            }
            //存在引用，拒绝删除
            List<MaterialType> materialTypeList = materialTypeService.listByIds(emtIdList);
            List<String> emtNoList = new ArrayList<>();
            for (MaterialType materialType : materialTypeList) {
                emtNoList.add(materialType.getEmtNo());
            }
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("emt_no",emtNoList);
            List<MaterialEnter> materialEnters = materialEnterService.listByMap(columnMap);
            if (ObjectUtil.isNotEmpty(materialEnters)){
                return ApiResult.error();
            }
            materialTypeService.deleteMany(emtIdList);
        } catch (Exception e) {
            log.error("批量删除物资类型出错", e);
            return ApiResult.error(500, "批量删除物资类型出错", e);
        }
        return ApiResult.success();
    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody MaterialType materialType) {
        try {
            if (ObjectUtil.isNull(materialType)) {
                return ApiResult.blank();
            }
            //异步更新引用
            MaterialType byId = materialTypeService.getById(materialType.getEmtId());
            if (!byId.getEmtNo().equals(materialType.getEmtNo())){
                materialEnterService.updateEmtNos(byId,materialType);
            }

            //再更新自己
            materialTypeService.updateOne(materialType);
        } catch (Exception e) {
            log.error("更新物资类型出错",e);
            return ApiResult.error(500, "更新物资类型出错", e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectAll")
    public ApiResult<List<MaterialType>> selectAll() {
        return ApiResult.success(materialTypeService.selectAll());
    }
}
