package com.zxl.materialStorage.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @className: ApiCode
 * @description: 枚举请求状态和请求信息
 * @author: ZhangXiaolei
 * @date: 2022/2/27
 **/

@AllArgsConstructor
public enum ApiCode implements ICode{
    SUCCESS(2,"操作成功"),
    ERROR(1,"存在上下级依赖，拒绝删除，请先去除上下级依赖"),
    BLANK(0,"缺少必要的参数信息");

    private final Integer code;
    private final String message;


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ApiCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
