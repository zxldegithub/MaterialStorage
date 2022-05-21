package com.zxl.materialStorage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: LoginDto
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/5/21
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String email;
    private String verifyCode;
}
