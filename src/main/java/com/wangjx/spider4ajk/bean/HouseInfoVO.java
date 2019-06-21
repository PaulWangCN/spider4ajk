package com.wangjx.spider4ajk.bean;

import lombok.Data;

/**
 * @ClassName HouseInfoVO
 * @Description 页面转换成房屋信息VO
 * @Author Wangjx
 * @Date 2019/4/10 13:59
 * @Version 1.0
 **/
@Data
public class HouseInfoVO {
    private String title;//房源基本信息
    private String layout;//布局，如三室两厅
    private Double area;//面积
    private String floor;//楼层
    private String buildTime;//建造时间
    private String village;//小区名称
    private String address;//小区地址
    private Double sumPrice;//总价
    private Double perPrice;//单价
    private String fitment;//装潢程度
    private String twoYears;//是否满两年
    private String hasElevator;//是否有电梯
    private Double firstMoney;//参考首付
    private String ajkId;//安居客的id
    private String detailUrl;//明细页url
}