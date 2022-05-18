package com.zxl.materialStorage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: VerificationCode
 * @description: VerificationCode
 * @author: ZhangXiaolei
 * @date: 2022/5/17
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodeDto {
    private String telNumber;
    private String telCode;
}
