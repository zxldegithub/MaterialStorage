package com.zxl.materialStorage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @className: PageTest
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/17
 **/

@SpringBootTest
public class PageTest {
    @Test
    public void pageTest(){
        List<Person> list1 = new ArrayList<>();
        Person person1 = new Person("zxl",11);
        Person person2 = new Person("ljr",12);
        list1.add(person1);
        list1.add(person2);
        Page<Person> personPage = new Page<>();
        personPage.setRecords(list1);
        System.out.println(personPage);

        List<Dog> list2 = new ArrayList<>();
        Dog dog1 = new Dog("11","111");
        Dog dog2 = new Dog("22","222");
        list2.add(dog1);
        list2.add(dog2);
//        personPage.setRecords(list2);
    }
}
