package com.wangjx.spider4ajk.handler;

import com.wangjx.spider4ajk.bean.HouseInfoVO;
import com.wangjx.spider4ajk.core.ISpiderHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author WangJiaxing
 * @version 1.0.0
 * @ClassName SimpleSaveHandler.java
 * @Description 简单保存handler
 * @createTime 2019年06月21日 14:05:00
 */
@Component
public class SimpleSaveHandler<T> implements ISpiderHandler<T> {

    private List<HouseInfoVO> houseInfoVOS;

    @Override
    public void handle(T t) {

    }

    @Override
    public boolean goon() {
        return false;
    }

}
