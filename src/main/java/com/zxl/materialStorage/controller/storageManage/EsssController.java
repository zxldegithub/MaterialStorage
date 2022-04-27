package com.zxl.materialStorage.controller.storageManage;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.service.storageManage.EsssService;
import jdk.jpackage.internal.Log;
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
@RequestMapping("/esss")
@Slf4j
public class EsssController {
    @Autowired
    private EsssService esssService;

    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody EssSpace essSpace){
        try {
            if (ObjectUtil.isNull(essSpace)){
                return ApiResult.blank();
            }
            esssService.insertNewOne(essSpace);
        } catch (Exception e) {
            log.error("新增库区失败",e);
            return ApiResult.error(500,"新增库区失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "esssId") String esssId){

    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> esssIdList){

    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody EssSpace essSpace){

    }

    @GetMapping("/seleteByPage")
    public ApiResult<Page<EssSpace>> selectByPage(@RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){

    }

    @GetMapping("/seleteSetFromRedis")
    public ApiResult<Set<EssSpace>> seleteSetFromRedis(){

    }
}
