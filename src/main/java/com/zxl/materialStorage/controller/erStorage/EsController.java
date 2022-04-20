package com.zxl.materialStorage.controller.erStorage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.dto.ErStorageDto;
import com.zxl.materialStorage.model.dto.ErStorageUpdateDto;
import com.zxl.materialStorage.model.vo.ErStorageVo;
import com.zxl.materialStorage.service.erStorage.EsService;
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
    public ApiResult<Object> insertNewOne(@RequestBody ErStorageDto erStorageDto){
        try {
            esService.insertNewOne(erStorageDto);
        }catch (Exception e){
            log.error("新增物资库操作失败",e);
            return ApiResult.error(500,"新增物资库操作失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteOne")
    public ApiResult<Object> deleteOne(@RequestBody ErStorageDto erStorageDto){
        try {
            esService.deleteOne(erStorageDto);
        }catch (Exception e){
            log.error("删除物资库操作失败",e);
            return ApiResult.error(500,"删除物资库操作失败",e);
        }
        return ApiResult.success();
    }

    @DeleteMapping("/deleteMany")
    public ApiResult<Object> deleteMany(@RequestBody List<ErStorageDto> erStorageDtoList){
        try {
            esService.deleteMany(erStorageDtoList);
        }catch (Exception e){
            log.error("批量删除物资库失败",e);
            return ApiResult.error(500,"批量删除物资库失败",e);
        }
        return ApiResult.success();
    }

    @PostMapping("/updateOne")
    public ApiResult<Object> updateOne(@RequestBody ErStorageUpdateDto erStorageUpdateDto){
        try {
            esService.updateOne(erStorageUpdateDto);
        }catch (Exception e){
            log.error("更新物资库信息失败",e);
            return ApiResult.error(500,"更新物资库信息失败",e);
        }
        return ApiResult.success();
    }

    @GetMapping("/selectAll")
    public ApiResult<Page<ErStorageVo>> selectAll(@RequestParam(value = "pageIndex",defaultValue = "1") int pageIndex,
                                     @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        Page<ErStorageVo> erStorageVoPage = esService.selectAll(pageIndex, pageSize);
        return ApiResult.success(erStorageVoPage);
    }
}
