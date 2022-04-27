package com.zxl.materialStorage.controller.storageManage;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.service.storageManage.EssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @className: EsController
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@RestController
@RequestMapping("/ess")
@Slf4j
public class EssController {
    @Autowired
    private EssService essService;

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody EsStoreroom esStoreroom){
        try {
            if (ObjectUtil.isNull(esStoreroom)){
                return ApiResult.blank();
            }
            essService.insertNewOne(esStoreroom);
        } catch (Exception e) {
            log.error("新增仓库失败",e);
            return ApiResult.error(500, "新增仓库失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "essId") String essId){
        try {
            if (StringUtils.isEmpty(essId)){
                return ApiResult.blank();
            }
            essService.deleteOne(essId);
        } catch (Exception e) {
            log.error("删除仓库失败",e);
            return ApiResult.error(500,"删除仓库失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> essIdList){
        try {
            if (ObjectUtil.isEmpty(essIdList)){
                return ApiResult.blank();
            }
            essService.deleteMany(essIdList);
        } catch (Exception e) {
            log.error("批量删除仓库失败",e);
            return ApiResult.error(500,"批量删除仓库失败",e);
        }
        return ApiResult.success();
    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody EsStoreroom esStoreroom){
        try {
            if (ObjectUtil.isNull(esStoreroom)){
                return ApiResult.blank();
            }
            essService.updateOne(esStoreroom);
        } catch (Exception e) {
            log.error("更新仓库失败",e);
            return ApiResult.error(500,"更新仓库失败",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectByPage")
    public ApiResult<Page<EsStoreroom>> selectByPage(@RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return ApiResult.success(essService.selectByPage(pageIndex,pageIndex));
    }

    @GetMapping("/selectSetFromRedis")
    public ApiResult<Set<EsStoreroom>> selectSetFromRedis(){
        return ApiResult.success(essService.selectSetFromRedis());
    }
}
