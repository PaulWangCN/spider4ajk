package com.wangjx.spider4ajk.handler;

/**
 * @author WangJiaxing
 * @version 1.0.0
 * @ClassName ISpiderHandler.java
 * @Description 数据处理器接口
 * @createTime 2019年06月21日 13:37:00
 */
public interface ISpiderHandler {

    /**
     * 处理参数
     */
    void handle();

    /**
     * 是否继续传递到下一个handler
     * @return
     */
    boolean goon();

    /**
     * 设置是否继续
     * @param goon
     */
    void setGoon(boolean goon);
}
