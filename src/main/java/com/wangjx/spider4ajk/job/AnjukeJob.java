package com.wangjx.spider4ajk.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjx.spider4ajk.bean.HouseInfoVO;
import com.wangjx.spider4ajk.core.AjkSpiderPipeline;
import com.wangjx.spider4ajk.core.HttpClient;
import com.wangjx.spider4ajk.common.IdGenerator;
import com.wangjx.spider4ajk.dao.THouseInfoMapper;
import com.wangjx.spider4ajk.handler.SimpleSaveHandler;
import com.wangjx.spider4ajk.model.THouseInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

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
