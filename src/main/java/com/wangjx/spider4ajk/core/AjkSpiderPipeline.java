package com.wangjx.spider4ajk.core;

import com.wangjx.spider4ajk.handler.SimpleSaveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author WangJiaxing
 * @version 1.0.0
 * @ClassName AjkSpiderPipeline.java
 * @Description 安居客爬虫管道
 * @createTime 2019年06月21日 14:17:00
 */
@Component
public class AjkSpiderPipeline extends AbstractSpiderPipeline {

    @Autowired
    private SimpleSaveHandler simpleSaveHandler;

    @Override
    public void init() {
        if (super.spiderHandlers == null) {
            super.spiderHandlers = new ArrayList<>();
        }
        spiderHandlers.add(simpleSaveHandler);
    }


}
