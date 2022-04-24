package com.zxl.materialStorage.mapper.storageManage;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.zxl.materialStorage.model.pojo.ESStoreroom;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: EssMapper
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/22
 **/
@Mapper
public interface EssMapper extends BaseMapper<ESStoreroom> {

}
