/*
 * RussianGo 后端启动入口
 * Spring Boot 应用主类，启动内嵌 Tomcat 并加载所有组件
 */
package com.russtudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RusStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RusStudyApplication.class, args);
    }
}