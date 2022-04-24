package com.zxl.materialStorage.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.status.StatusConsoleListener;

import java.io.Serializable;

/**
 * @className: ApiResult
 * @description: 对返回给前端的数据进行封装
 * @author: ZhangXiaolei
 * @date: 2022/2/27
 **/

@Data
@AllArgsConstructor
public class ApiResult<T> implements Serializable {
    private static final long serializableUid = 21L;
    private long code;
    private String message;
    private T data;


    /**
     * @description: 无响应数据的请求
     * @Param: []
     * @return: com.zxl.materialStorage.common.api.ApiResult<T>
     * @author: ZhangXiaoLei
     * @date: 2022/2/27
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<T>(ApiCode.SUCCESS.getCode(), ApiCode.SUCCESS.getMessage(), null);
    }

    /**
     * @description: 有相应数据的请求
     * @Param: [data]
     * @return: com.zxl.materialStorage.common.api.ApiResult<T>
     * @author: ZhangXiaoLei
     * @date: 2022/2/27
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<T>(ApiCode.SUCCESS.getCode(), ApiCode.SUCCESS.getMessage(), data);
    }

    /**
     * @description: 请求失败时，简单粗暴的提示
     * @Param: []
     * @return: com.zxl.materialStorage.common.api.ApiResult<T>
     * @author: ZhangXiaoLei
     * @date: 2022/2/27
     */
    public static <T> ApiResult<T> error() {
        return new ApiResult<T>(ApiCode.ERROR.getCode(), ApiCode.ERROR.getMessage(), null);
    }

    /**
     * @description: 请求失败时，详细的提示信息
     * @Param: [code, message]
     * @return: com.zxl.materialStorage.common.api.ApiResult<T>
     * @author: ZhangXiaoLei
     * @date: 2022/2/27
     */
    public static <T> ApiResult<T> error(Integer code, String message,T t) {
        return new ApiResult<T>(code, message, null);
    }

    public static <T> ApiResult<T> blank(){
        return new ApiResult<T>(ApiCode.BLANK.getCode(), ApiCode.BLANK.getMessage(),null);
    }
}
