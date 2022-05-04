package com.zxl.materialStorage.controller.materialEnter;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.MaterialType;
import com.zxl.materialStorage.service.materialEnter.MaterialTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody MaterialType materialType){
        try {
            if (ObjectUtil.isNull(materialType)){
                return ApiResult.blank();
            }
            materialTypeService.insertNewOne(materialType);
        } catch (Exception e) {
            log.error("新增物资类型出错",e);
            return ApiResult.error(500,"新增物资类型出错",e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用则拒绝删除
    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "emtId") String emtId){
        try {
            if (StringUtils.isEmpty(emtId)){
                return ApiResult.blank();
            }
            materialTypeService.deleteOne(emtId);
        } catch (Exception e) {
            log.error("删除物资类型出错",e);
            return ApiResult.error(500,"删除物资类型出错",e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用则拒绝删除
    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> emtIdList){
        try {
            if (ObjectUtil.isEmpty(emtIdList)){
                return ApiResult.blank();
            }
            materialTypeService.deleteMany(emtIdList);
        } catch (Exception e) {
            log.error("批量删除物资类型出错",e);
            return ApiResult.error(500,"批量删除物资类型出错",e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用，则更新引用
    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody MaterialType materialType){
        try {
            if (ObjectUtil.isNull(materialType)){
                return ApiResult.blank();
            }
            materialTypeService.updateOne(materialType);
        } catch (Exception e) {
            log.error("更新物资类型出错");
            return ApiResult.error(500,"更新物资类型出错",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectAll")
    public ApiResult<List<MaterialType>> selectAll(){
        return ApiResult.success(materialTypeService.selectAll());
    }
}
