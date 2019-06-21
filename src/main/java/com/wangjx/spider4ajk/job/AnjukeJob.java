package com.wangjx.spider4ajk.job;

import com.wangjx.spider4ajk.core.AjkSpiderPipeline;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @ClassName Worker
 * @Description 安居客定时任务
 * @Author Wangjx
 * @Date 2019/4/9 17:19
 * @Version 1.0
 **/
@Slf4j
public class AnjukeJob extends QuartzJobBean {

    @Autowired
    private AjkSpiderPipeline ajkSpiderPipeline;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        ajkSpiderPipeline.invoke();
    }
}
