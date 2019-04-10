package com.wangjx.spider4ajk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@MapperScan("com.wangjx.spider4ajk.dao")
public class Spider4ajkApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spider4ajkApplication.class, args);
    }

}
