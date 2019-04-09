package com.wangjx.spider4ajk.core;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName QuartzConfiguration
 * @Description TODO
 * @Author Wangjx
 * @Date 2019/4/9 17:22
 * @Version 1.0
 **/
@Configuration
public class QuartzConfiguration {

    @Value("${spider.timer}")
    private int time;

    @Bean
    public JobDetail testQuartz1() {
        return JobBuilder.newJob(Worker.class).withIdentity("Worker").storeDurably().build();
    }

    @Bean
    public Trigger testQuartzTrigger1() {
        //5秒执行一次
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(time)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(testQuartz1())
                .withIdentity("Worker")
                .withSchedule(scheduleBuilder)
                .build();
    }

}
