package com.wangjx.spider4ajk.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangjx.spider4ajk.bean.HouseInfoVO;
import com.wangjx.spider4ajk.common.IdGenerator;
import com.wangjx.spider4ajk.core.HttpClient;
import com.wangjx.spider4ajk.core.ISpiderHandler;
import com.wangjx.spider4ajk.dao.THouseInfoMapper;
import com.wangjx.spider4ajk.model.THouseInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author WangJiaxing
 * @version 1.0.0
 * @ClassName SimpleSaveHandler.java
 * @Description 简单保存handler
 * @createTime 2019年06月21日 14:05:00
 */
@Slf4j
@Component
public class SimpleSaveHandler implements ISpiderHandler {

    @Autowired
    private THouseInfoMapper tHouseInfoMapper;
    @Autowired
    private HttpClient httpClient;
    private boolean goon = false;
    private IdGenerator idWorker = new IdGenerator(0, 0);

    @Override
    public void handle() {
        Long batchNo = idWorker.nextId();
        log.info("本次爬虫开始，批次：{}", batchNo);
        LinkedBlockingQueue<String> seeds = new LinkedBlockingQueue<>();
        Set<String> usedSeeds = new HashSet<>();
        try {
            seeds.put("https://shaoxing.anjuke.com/sale/zhujinh/a626-b514-p1/");
            clawing(seeds, usedSeeds, batchNo);
            log.info("本次爬虫结束，批次：{}", batchNo);
        } catch (Exception e) {
            log.error("AnjukeJob.executeInternal()", e);
        }
    }

    @Override
    public boolean goon() {
        return this.goon;
    }

    @Override
    public void setGoon(boolean goon) {
        this.goon = goon;
    }

    /**
     * 爬虫方法
     * @param seeds
     * @param usedSeeds
     * @param batchNo
     * @throws InterruptedException
     */
    private void clawing(LinkedBlockingQueue<String> seeds, Set<String> usedSeeds, Long batchNo) throws Exception {
        String url = seeds.poll();
        String tableHtml = httpClient.get(url, null);
        if (tableHtml == null) {
            return;
        }
        Document document = Jsoup.parse(tableHtml);
        usedSeeds.add(url.replace("#filtersort", ""));
        Element saleLeft = document.selectFirst(".sale-left");
        List<HouseInfoVO> list = parseFromHtml(saleLeft);
        saveHouseInfo(list, batchNo);
        handlePages(saleLeft, seeds, usedSeeds);
        if (!seeds.isEmpty()) {
            clawing(seeds, usedSeeds, batchNo);
        }
    }

    /**
     * 从html中解析出VO对象
     * @param saleLeft
     * @return
     * @throws Exception
     */
    private List<HouseInfoVO> parseFromHtml(Element saleLeft) throws Exception {
        Elements elements = saleLeft.select("#houselist-mod-new").select("li");
        List<HouseInfoVO> list = new ArrayList<>();
        for (Element element : elements) {
            Element houseDetails = element.selectFirst(".house-details");
            HouseInfoVO houseInfoVO = new HouseInfoVO();
            houseInfoVO.setTitle(houseDetails.select(".house-title").text());
            //跳转到明细页获取更多信息
            String detailUrl = houseDetails.selectFirst(".house-title").selectFirst("a").absUrl("href");
            houseInfoVO.setDetailUrl(detailUrl);
            houseInfoVO.setAjkId(detailUrl.substring(detailUrl.indexOf("/view/") + 6, detailUrl.indexOf("?")));
            String detailHtml = httpClient.get(detailUrl, 1);
            if (detailHtml == null) {
                continue;
            }
            Document detailDoc = Jsoup.parse(detailHtml);
            if (detailDoc.selectFirst(".houseInfo-wrap") == null) {
                continue;
            }
            Elements items = detailDoc.selectFirst(".houseInfo-wrap").select("li");
            houseInfoVO.setLayout(items.get(1).selectFirst(".houseInfo-content").text());//户型
            if (houseInfoVO.getAjkId().indexOf("E") == 0) {
                break;
            }
            houseInfoVO.setFitment(items.size() < 11 ? "" : items.get(11).selectFirst(".houseInfo-content").text());//装修程度
            houseInfoVO.setTwoYears(items.size() < 14 ? "" : items.get(14).selectFirst(".houseInfo-content").text());//是否满两年
            houseInfoVO.setHasElevator(items.size() < 13 ? "" : items.get(13).selectFirst(".houseInfo-content").text());//是否有电梯
            String firstMoney = items.get(5).selectFirst(".houseInfo-content").text();
            houseInfoVO.setFirstMoney(new Double(firstMoney.replaceAll( "[^\\d.]", "" )));//首付

            Elements details = houseDetails.select(".details-item");
            Elements detail1 = details.get(0).select("span");

            String area = detail1.get(1).text();
            houseInfoVO.setArea(new Double(area.substring(0, area.indexOf("m"))));
            houseInfoVO.setFloor(detail1.get(2).text());
            houseInfoVO.setBuildTime(detail1.get(3).text());
            Elements detail2 = details.get(1).select("span");
            houseInfoVO.setAddress(detail2.get(0).text().split(" ")[1]);
            houseInfoVO.setVillage(detail2.get(0).text().split(" ")[0]);
            String priceDet = element.select(".price-det").text();
            String unitPrice = element.select(".unit-price").text();
            houseInfoVO.setSumPrice(new Double(priceDet.replaceAll( "[^\\d.]", "" )));
            houseInfoVO.setPerPrice(new Double(unitPrice.replaceAll( "[^\\d.]", "" )));
            list.add(houseInfoVO);
        }
        return list;
    }

    /**
     * 处理分页的url
     * @param saleLeft
     * @param seeds
     * @param usedSeeds
     */
    private void handlePages(Element saleLeft, LinkedBlockingQueue<String> seeds, Set<String> usedSeeds) {
        Element multiPages = saleLeft.selectFirst(".multi-page");
        if (multiPages != null) {
            Elements pages = multiPages.select("a");
            for (Element page : pages) {
                if (page.text().indexOf("下一页") < 0) {
                    String urlInPage = page.absUrl("href").replace("#filtersort", "");

                    boolean isExist = false;
                    for (String urlInQueue : seeds) {
                        if (urlInQueue.equals(urlInPage)) {
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        for (String urlInSet : usedSeeds) {
                            if (urlInSet.equals(urlInPage)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            log.info("增加url: {}", urlInPage);
                            seeds.add(urlInPage);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将房源信息保存到数据库
     * @param list
     * @param batchNo
     */
    private void saveHouseInfo(List<HouseInfoVO> list, Long batchNo) {
        log.info("准备保存数据库房源信息数：{}", list.size());
        for (HouseInfoVO houseInfoVO : list) {
            THouseInfo tHouseInfo = new THouseInfo();
            BeanUtils.copyProperties(houseInfoVO, tHouseInfo);
            tHouseInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
            tHouseInfo.setBatchNo(batchNo);
            //检查ajkId + batchNo是否数据库存在，存在视为重复不插入
            QueryWrapper<THouseInfo> wrapper = new QueryWrapper();
            wrapper.eq("ajk_id", tHouseInfo.getAjkId()).eq("batch_no", tHouseInfo.getBatchNo());
            if (tHouseInfoMapper.selectOne(wrapper) == null) {
                tHouseInfoMapper.insert(tHouseInfo);
                log.info("保存成功: {}", tHouseInfo);
            }
        }
    }
}
