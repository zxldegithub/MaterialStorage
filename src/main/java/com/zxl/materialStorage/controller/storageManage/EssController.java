package com.zxl.materialStorage.controller.storageManage;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.MaterialEnter;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.service.storageManage.EssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @Autowired
    private MaterialEnterService materialEnterService;

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
            //若存在下级物资依赖，则拒绝删除
            EsStoreroom byId = essService.getById(essId);
            List<MaterialEnter> materialEnterList = materialEnterService.list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEssNo, byId.getEssNo()));
            if (ObjectUtil.isNotEmpty(materialEnterList)){
                return ApiResult.error();
            }
            //执行单个删除操作
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
            //若存在下级物资依赖，则拒绝删除
            List<EsStoreroom> esStoreroomList = essService.listByIds(essIdList);
            for (EsStoreroom storeroom : esStoreroomList) {
                List<MaterialEnter> materialEnterList = materialEnterService.list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEssNo, storeroom.getEssNo()));
                if (ObjectUtil.isNotEmpty(materialEnterList)){
                    return ApiResult.error();
                }
            }
            //执行批量删除操作
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

    @GetMapping("/selectAll")
    public ApiResult<List<EsStoreroom>> selectAll(){
        return ApiResult.success(essService.selectAll());
    }

    @GetMapping("/selectEssNoList")
    public ApiResult<List<String>> selectEssNoList(){
        return ApiResult.success(essService.selectEssNoList());
    }
}
