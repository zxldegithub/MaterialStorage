package com.zxl.materialStorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @className: RedisConfig
 * @description: 改变redis默认序列化规则
 * @author: ZhangXiaolei
 * @date: 2022/3/17
 **/

@Configuration
public class RedisConfig {
    /**
     * @description: 将redisTemplate的序列化规则改为Jackson2JsonRedisSerializer
     * @Param: [lettuceConnectionFactory]
     * @return: org.springframework.data.redis.core.RedisTemplate<java.lang.String,java.lang.Object>
     * @author: ZhangXiaoLei
     * @date: 2022/3/17
     */
    @Bean
    public RedisTemplate<String,Object> ChangeRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }
}
