package com.zxl.materialStorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: Person
 * @description: Test
 * @author: ZhangXiaolei
 * @date: 2022/4/17
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String name;
    private int age;
}
