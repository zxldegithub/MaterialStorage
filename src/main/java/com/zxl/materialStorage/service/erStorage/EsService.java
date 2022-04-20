package com.zxl.materialStorage.service.erStorage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.materialStorage.model.dto.ErStorageDto;
import com.zxl.materialStorage.model.dto.ErStorageUpdateDto;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.vo.ErStorageVo;

import java.util.List;

/**
 * @className: EsService
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/

public interface EsService extends IService<ErStorage> {
    void insertNewOne(ErStorageDto erStorageDto) throws Exception;
    void deleteOne(ErStorageDto erStorageDto) throws Exception;
    void deleteMany(List<ErStorageDto> erStorageDtoList) throws Exception;
    void updateOne(ErStorageUpdateDto erStorageUpdateDto) throws Exception;
    Page<ErStorageVo> selectAll(int pageIndex, int pageSize);
}
