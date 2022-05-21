package com.zxl.materialStorage.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWTUtil;
import com.zxl.materialStorage.common.api.ApiResult;
import com.zxl.materialStorage.model.dto.LoginDto;
import com.zxl.materialStorage.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @className: LoginController
 * @description: LoginController
 * @author: ZhangXiaolei
 * @date: 2022/5/21
 **/

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/createCode")
    public ApiResult<Object> createCode(@RequestParam(value = "email") String email){
        if(ObjectUtil.isNull(email)){
            return ApiResult.blank();
        }
        Set<String> emailSet = redisTemplate.opsForSet().members("emails");
        for (String emailExist : emailSet) {
            if (!Objects.equals(email,emailExist)){
                return ApiResult.blank("不存在此邮箱账户");
            }
        }
        loginService.createCode(email);
        return ApiResult.success();
    }

    @PostMapping("/login")
    public ApiResult<Object> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        String code = redisTemplate.opsForValue().get(loginDto.getEmail());
        if (!SecureUtil.md5(loginDto.getVerifyCode()).equals(code)){
            return ApiResult.error(5,"验证码错误，验证失败",null);
        }

        Map<String,Object> map = new HashMap<>();
        map.put("uid",loginDto.getEmail());
        map.put("expire_time",System.currentTimeMillis() + 1000 * 60 * 60 * 24 );

        String token = JWTUtil.createToken(map, loginDto.getEmail().getBytes(StandardCharsets.UTF_8));

        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ApiResult.success();
    }

    @GetMapping("/checkToken")
    public ApiResult<Object> checkToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (ObjectUtil.isNotEmpty(cookies)){
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())){
                    return ApiResult.success();
                }
            }
        }
        return ApiResult.blank();
    }
}
