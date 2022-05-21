package com.zxl.materialStorage.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @className: LoginServiceImpl
 * @description: LoginServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/5/21
 **/
@Service
public class LoginServiceImpl implements LoginService{

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void createCode(String email) {

        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(200, 100, 6, 0);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, SecureUtil.md5(shearCaptcha.getCode()),24, TimeUnit.HOURS);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setSubject("激活账户");
        msg.setText("验证码：  "+shearCaptcha.getCode());
        msg.setFrom("zxldewyyx@163.com");
        msg.setTo(email);
        javaMailSender.send(msg);
    }
}
