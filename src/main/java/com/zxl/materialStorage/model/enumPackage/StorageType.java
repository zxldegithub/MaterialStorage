package com.zxl.materialStorage.model.enumPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: StorageType
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@AllArgsConstructor
@Getter
public enum StorageType {

    CENTRAL(1,"中央存储库"),
    PROVINCE(2,"省级存储库"),
    CITY(3,"市级存储库"),
    COUNTY(4,"县级存储库"),
    TOWN(5,"乡级存储库");


    private final int code;
    private final String name;
}
