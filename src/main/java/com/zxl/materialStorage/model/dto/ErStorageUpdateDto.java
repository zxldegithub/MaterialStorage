package com.zxl.materialStorage.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @className: ErStorageUpdateDto
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/12
 **/

@Data
public class ErStorageUpdateDto {
    private Long esId;
    private String esNo;
    private String esLocation;
    private String esTypeName;
    private String esIntroduce;
    private String esStatusName;
}
