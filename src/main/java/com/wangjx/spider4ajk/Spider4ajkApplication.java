package com.wangjx.spider4ajk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Spider4ajkApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spider4ajkApplication.class, args);
    }

}
