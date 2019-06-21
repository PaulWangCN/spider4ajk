package com.wangjx.spider4ajk.configuration;

import com.wangjx.spider4ajk.job.AnjukeJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName QuartzConfiguration
 * @Description quartz配置类
 * @Author Wangjx
 * @Date 2019/4/9 17:22
 * @Version 1.0
 **/
@Slf4j
@Configuration
public class QuartzConfiguration {

    @Bean
    public JobDetail anjukeQuartz() {
        return JobBuilder.newJob(AnjukeJob.class).withIdentity("anjukeJob").storeDurably().build();
    }

    @Bean
    public Trigger anjukeQuartzTrigger() {
        //12小时执行一次
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(12)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(anjukeQuartz())
                .withIdentity("anjukeJob")
                .withSchedule(scheduleBuilder)
                .build();
    }

}
