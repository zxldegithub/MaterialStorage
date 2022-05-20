package com.zxl.materialStorage.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @className: SystemUtil
 * @description: SystemUtil
 * @author: ZhangXiaolei
 * @date: 2022/4/12
 **/

public class SystemUtil {
    public static String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public static String formatRequestTime(String requestTime){
        String formatRequestTime = requestTime;
        if (requestTime.length()>23){
            formatRequestTime = requestTime.split("T")[0];
            formatRequestTime = formatRequestTime.split("-")[0]+"-"
                    +formatRequestTime.split("-")[1]+"-"
                    +(Integer.parseInt(formatRequestTime.split("-")[2])+1);
        }
        return formatRequestTime;
    }
}
