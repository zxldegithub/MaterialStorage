package com.zxl.materialStorage.controller.storageManage;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.EsssShelves;
import com.zxl.materialStorage.service.storageManage.EssssService;
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
@RequestMapping("/essss")
@Slf4j
public class EssssController {
    @Autowired
    private EssssService essssService;

    @RequestMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody EsssShelves esssShelves){
        try {
            if (ObjectUtil.isNull(esssShelves)){
                return ApiResult.blank();
            }
            essssService.insertNewOne(esssShelves);
        } catch (Exception e) {
            log.error("新建货架出错",e);
            return ApiResult.error(500,"新建货架出错",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestParam(value = "essssId") String essssId){
        try {
            if (StringUtils.isEmpty(essssId)){
                return ApiResult.blank();
            }
            essssService.deleteOne(essssId);
        } catch (Exception e) {
            log.error("删除货架出错",e);
            return ApiResult.error(500,"删除货架出错",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> essssIdList){
        try {
            if (ObjectUtil.isEmpty(essssIdList)){
                return ApiResult.blank();
            }
            essssService.deleteMany(essssIdList);
        } catch (Exception e) {
            log.error("批量删除货架出错",e);
            return ApiResult.error(500,"批量删除货架出错",e);
        }
        return ApiResult.success();
    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody EsssShelves esssShelves){
        try {
            if (ObjectUtil.isNull(esssShelves)){
                return ApiResult.blank();
            }
            essssService.updateOne(esssShelves);
        } catch (Exception e) {
            log.error("更新货架信息出错",e);
            return ApiResult.error(500,"更新货架信息出错",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectByPage")
    public ApiResult<Page<EsssShelves>> selectByPage(@RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                        @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return ApiResult.success(essssService.selectByPage(pageIndex,pageSize));
    }

    @GetMapping("/selectAll")
    public ApiResult<List<EsssShelves>> selectAll(){
        return ApiResult.success(essssService.selectAll());
    }
}
