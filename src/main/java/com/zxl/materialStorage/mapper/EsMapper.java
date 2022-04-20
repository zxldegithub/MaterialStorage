package com.zxl.materialStorage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.vo.ErStorageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: EsMapper.xml
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@Mapper
public interface EsMapper extends BaseMapper<ErStorage> {
    Page<ErStorageVo> selectAll(@Param("page") Page<ErStorageVo> page);
}
