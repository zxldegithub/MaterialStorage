package com.zxl.materialStorage.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @className: SystemUtil
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/12
 **/

public class SystemUtil {
    public static String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
}
