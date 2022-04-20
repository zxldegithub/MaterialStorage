package com.zxl.materialStorage.model.dto;

import com.zxl.materialStorage.model.enumPackage.StorageType;
import lombok.Data;

/**
 * @className: ErStorageDto
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/

@Data
public class ErStorageDto {
    private String esNo;
    private String esLocation;
    private String esTypeName;
    private String esIntroduce;
}
