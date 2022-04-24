package com.zxl.materialStorage.controller.storageManage;

import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.service.storageManage.EssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: EsController
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@RestController
@RequestMapping("/ess")
public class EssController {
    @Autowired
    private EssService essService;
    @Autowired
    private EsService esService;
    @PostMapping("/insertNewOne")
    public ApiResult<Object> insertNewOne(@RequestBody ){

    }
}
