package com.wangjx.spider4ajk.core;

import java.util.List;

/**
 * @author WangJiaxing
 * @version 1.0.0
 * @ClassName Pipeline.java
 * @Description 数据处理器管道接口
 * @createTime 2019年06月21日 11:47:00
 */
public interface ISpiderPipeline {

    void addFirst(ISpiderHandler iSpiderHandler);

    void addLast(ISpiderHandler iSpiderHandler);

    void remove(ISpiderHandler iSpiderHandler);

    void invoke();
}
