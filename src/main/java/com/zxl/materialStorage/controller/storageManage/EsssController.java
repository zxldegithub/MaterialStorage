package com.zxl.materialStorage.controller.storageManage;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.model.pojo.MaterialEnter;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.storageManage.EsssService;
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
@RequestMapping("/esss")
@Slf4j
public class EsssController {
    @Autowired
    private EsssService esssService;

    @Autowired
    private MaterialEnterService materialEnterService;

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
        try {
            if (StringUtils.isEmpty(esssId)){
                return ApiResult.blank();
            }
            //若存在下级物资依赖，则拒绝删除
            EssSpace byId = esssService.getById(esssId);
            List<MaterialEnter> materialEnterList = materialEnterService.list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEsssNo, byId.getEsssNo()));
            if (ObjectUtil.isNotEmpty(materialEnterList)){
                return ApiResult.error();
            }
            //执行单个删除操作
            esssService.deleteOne(esssId);
        } catch (Exception e) {
            log.error("删除库区失败",e);
            return ApiResult.error(500,"删除库区失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<String> esssIdList){
        try {
            if (ObjectUtil.isEmpty(esssIdList)){
                return ApiResult.blank();
            }
            //若存在下级物资依赖，则拒绝删除
            List<EssSpace> essSpaceList = esssService.listByIds(esssIdList);
            for (EssSpace space : essSpaceList) {
                List<MaterialEnter> materialEnterList = materialEnterService.list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEsssNo, space.getEsssNo()));
                if (ObjectUtil.isNotEmpty(materialEnterList)){
                    return ApiResult.error();
                }
            }
            //执行批量删除操作
            esssService.deleteMany(esssIdList);
        } catch (Exception e) {
            log.error("批量删除库区失败",e);
            return ApiResult.error(500,"批量删除库区失败",e);
        }
        return ApiResult.success();
    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody EssSpace essSpace){
        try {
            if (ObjectUtil.isNull(essSpace)){
                return ApiResult.blank();
            }

            esssService.updateOne(essSpace);
        } catch (Exception e) {
            log.error("更新获取信息失败");
            return ApiResult.error(500,"更新获取信息失败",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/seleteByPage")
    public ApiResult<Page<EssSpace>> selectByPage(@RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return ApiResult.success(esssService.selectByPage(pageIndex,pageSize));
    }

    @GetMapping("/selectAll")
    public ApiResult<List<EssSpace>> selectAll(){
        return ApiResult.success(esssService.selectAll());
    }

    @GetMapping("/selectEsssNoList")
    public ApiResult<List<String>> selectEsssNoList(){
        return ApiResult.success(esssService.selectEsssNoList());
    }
}
