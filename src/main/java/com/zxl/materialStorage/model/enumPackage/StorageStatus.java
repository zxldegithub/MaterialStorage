package com.zxl.materialStorage.model.enumPackage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @className: StorageStatus
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/

@AllArgsConstructor
@Getter
public enum StorageStatus {

    SPARE(0,"闲置仓库"),
    USED(1,"使用中的仓库"),
    ABANDONED(2,"废弃仓库");

    private final int code;
    private final String name;

}
