package com.zxl.materialStorage.controller.materialEnter;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.MaterialPacking;
import com.zxl.materialStorage.service.materialEnter.MaterialPackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @className: MaterialPackingController
 * @description: MaterialPackingController
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/
@RestController
@RequestMapping("/emp")
@Slf4j
public class MaterialPackingController {
    @Autowired
    private MaterialPackingService materialPackingService;

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody MaterialPacking materialPacking){
        try {
            if (ObjectUtil.isNull(materialPacking)){
                return ApiResult.blank();
            }
            materialPackingService.insertNewOne(materialPacking);
        } catch (Exception e) {
            log.error("新增物资打包方式出错",e);
            return ApiResult.error(500,"新增物资打包方式出错",e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用则拒绝删除
    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "empId") String empId){
        try {
            if (StringUtils.isEmpty(empId)){
                return ApiResult.blank();
            }
            materialPackingService.deleteOne(empId);
        } catch (Exception e) {
            log.error("删除物资打包方式出错",e);
            return ApiResult.error(500,"删除物资打包方式出错",e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用则拒绝删除
    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> empIdList){
        try {
            if (ObjectUtil.isEmpty(empIdList)){
                return ApiResult.blank();
            }
            materialPackingService.deleteMany(empIdList);
        } catch (Exception e) {
            log.error("批量删除物资打包方式出错",e);
            return ApiResult.error(500,"批量删除物资打包方式出错",e);
        }
        return ApiResult.success();
    }

    //TODO 若存在引用，则更新引用
    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody MaterialPacking materialPacking){
        try {
            if (ObjectUtil.isNull(materialPacking)){
                return ApiResult.blank();
            }
            materialPackingService.updateOne(materialPacking);
        } catch (Exception e) {
            log.error("更新物资打包方式出错");
            return ApiResult.error(500,"更新物资打包方式出错",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectAll")
    public ApiResult<List<MaterialPacking>> selectAll(){
        return ApiResult.success(materialPackingService.selectAll());
    }
}
