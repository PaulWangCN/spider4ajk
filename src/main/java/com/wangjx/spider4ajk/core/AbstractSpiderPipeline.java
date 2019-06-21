package com.wangjx.spider4ajk.core;

import java.util.List;

/**
 * @author WangJiaxing
 * @version 1.0.0
 * @ClassName AbstractSpiderPipeline.java
 * @Description 爬虫管道抽象类
 * @createTime 2019年06月21日 14:11:00
 */
public abstract class AbstractSpiderPipeline<T> implements ISpiderPipeline<T> {

    protected List<ISpiderHandler> spiderHandlers;

    public abstract void init();

    @Override
    public void addFirst(ISpiderHandler iSpiderHandler) {
        spiderHandlers.add(0, iSpiderHandler);
    }

    @Override
    public void addLast(ISpiderHandler iSpiderHandler) {
        spiderHandlers.add(iSpiderHandler);
    }

    @Override
    public void remove(ISpiderHandler iSpiderHandler) {
        spiderHandlers.remove(iSpiderHandler);
    }

    @Override
    public void invoke(T t) {
        for (ISpiderHandler handler : spiderHandlers) {
            handler.handle(t);
            if (!handler.goon()) {
                break;
            }
        }
    }

}
