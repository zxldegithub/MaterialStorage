package com.zxl.materialStorage.model.vo;

import com.zxl.materialStorage.model.enumPackage.StorageStatus;
import com.zxl.materialStorage.model.enumPackage.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @className: ErStorageVo
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ErStorageVo {
    private String esNo;
    private String esLocation;
    private String esTypeName;
    private int esStoreroomNumber;
    private int esOutSpaceNumber;
    private String esIntroduce;
    private String esStatusName;
    private String esTimeValue;
    private String esTs;
}
