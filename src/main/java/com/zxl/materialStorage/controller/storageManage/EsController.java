package com.zxl.materialStorage.controller.storageManage;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.service.storageManage.EsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @className: EsController
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@RestController
@RequestMapping("/es")
@Slf4j
public class EsController {
    @Autowired
    private EsService esService;

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody ErStorage erStorage){
        try {
            if (ObjectUtil.isNull(erStorage)){
                return ApiResult.blank();
            }
            esService.insertNewOne(erStorage);
        }catch (Exception e){
            log.error("新增物资库操作失败",e);
            return ApiResult.error(500,"新增物资库操作失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "esId") String esId){
        try {
            if (StringUtils.isEmpty(esId)){
                return ApiResult.blank();
            }
            esService.deleteOne(esId);
        }catch (Exception e){
            log.error("删除物资库操作失败",e);
            return ApiResult.error(500,"删除物资库操作失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> esIdList){
        try {
            if (ObjectUtil.isEmpty(esIdList)){
                return ApiResult.blank();
            }
            esService.deleteMany(esIdList);
        }catch (Exception e){
            log.error("批量删除物资库失败",e);
            return ApiResult.error(500,"批量删除物资库失败",e);
        }
        return ApiResult.success();
    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody ErStorage erStorage){
        try {
            if (ObjectUtil.isNull(erStorage)){
                return ApiResult.blank();
            }
            esService.updateOne(erStorage);
        }catch (Exception e){
            log.error("更新物资库信息失败",e);
            return ApiResult.error(500,"更新物资库信息失败",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectAll")
    public ApiResult<Page<ErStorage>> selectAll(@RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
                                     @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        Page<ErStorage> erStoragePage = esService.selectAll(pageIndex, pageSize);
        return ApiResult.success(erStoragePage);
    }
}
