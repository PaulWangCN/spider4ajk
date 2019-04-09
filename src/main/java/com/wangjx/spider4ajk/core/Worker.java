package com.wangjx.spider4ajk.core;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName Worker
 * @Description TODO
 * @Author Wangjx
 * @Date 2019/4/9 17:19
 * @Version 1.0
 **/
@Slf4j
public class Worker extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        RestTemplate rt = new RestTemplate();

        String res = rt.getForObject("https://shaoxing.anjuke.com/sale/zhujinh-q-shujishiqu/a626-d1-t9/", String.class);
        Document document = Jsoup.parse(res);
        log.info(document.select("").toString());
    }
}
