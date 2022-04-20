package com.zxl.materialStorage.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: MyBatisPlusConfig
 * @description: MyBatisPlus自动分页插件
 * @author: ZhangXiaolei
 * @date: 2022/3/17
 **/

@Configuration
@MapperScan("com.zxl.materialStorage.mapper")
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor addPageHelper(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //主要是增加分页插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        //增加乐观锁
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }


    //设置该属性防止一级缓存和二级缓存出问题
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer(){
//        return configuration -> configuration.setUseDeprecatedExecutor(false);
//    }
}
