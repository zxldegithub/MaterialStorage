package com.zxl.materialStorage.service.storageManage.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EssMapper;
import com.zxl.materialStorage.model.pojo.ESStoreroom;
import com.zxl.materialStorage.service.storageManage.EssService;
import org.springframework.stereotype.Service;

/**
 * @className: EsssServiceImpl
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/23
 **/
@Service
public class EssServiceImpl extends ServiceImpl<EssMapper,ESStoreroom> implements EssService {
}
