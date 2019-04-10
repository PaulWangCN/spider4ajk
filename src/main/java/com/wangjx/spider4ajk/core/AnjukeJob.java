package com.wangjx.spider4ajk.core;

import com.wangjx.spider4ajk.dao.THouseInfoMapper;
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
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName Worker
 * @Description TODO
 * @Author Wangjx
 * @Date 2019/4/9 17:19
 * @Version 1.0
 **/
@Slf4j
public class AnjukeJob extends QuartzJobBean {

    @Autowired
    private THouseInfoMapper tHouseInfoMapper;
    @Autowired
    private HttpClient httpClient;

    private IdGenerator idWorker = new IdGenerator(0, 0);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        Long batchNo = idWorker.nextId();
        log.info("本次爬虫开始，批次：{}", batchNo);
        LinkedBlockingQueue<String> seeds = new LinkedBlockingQueue<>();
        Set<String> usedSeeds = new HashSet<>();
        try {
            seeds.put("https://shaoxing.anjuke.com/sale/zhujinh/a626-b514-p1/");
            clawing(seeds, usedSeeds, batchNo);
            log.info("本次爬虫结束，批次：{}", batchNo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 爬虫方法
     * @param seeds
     * @param usedSeeds
     * @param batchNo
     * @throws InterruptedException
     */
    private void clawing(LinkedBlockingQueue<String> seeds, Set<String> usedSeeds, Long batchNo) throws InterruptedException {
        String url = seeds.poll();
        String tableHtml = httpClient.get(url, null);
        if (tableHtml == null)
            return;
        Document document = Jsoup.parse(tableHtml);
        usedSeeds.add(url.replace("#filtersort", ""));
        Element saleLeft = document.selectFirst(".sale-left");
        Elements elements = saleLeft.select("#houselist-mod-new").select("li");
        List<HouseInfoVO> list = new ArrayList<>();
        for (Element element : elements) {
            Element houseDetails = element.selectFirst(".house-details");
            HouseInfoVO houseInfoVO = new HouseInfoVO();
            houseInfoVO.setTitle(houseDetails.select(".house-title").text());
            //跳转到明细页获取更多信息
            String detailUrl = houseDetails.selectFirst(".house-title").selectFirst("a").absUrl("href");
            String detailHtml = httpClient.get(detailUrl, 1);
            if (detailHtml == null)
                continue;
            Document detailDoc = Jsoup.parse(detailHtml);
            Elements items = detailDoc.selectFirst(".houseInfo-wrap").select("li");
            houseInfoVO.setLayout(items.get(1).selectFirst(".houseInfo-content").text());//户型
            houseInfoVO.setFitment(items.get(11).selectFirst(".houseInfo-content").text());//装修程度
            houseInfoVO.setTwoYears(items.get(14).selectFirst(".houseInfo-content").text());//是否满两年
            houseInfoVO.setHasElevator(items.get(13).selectFirst(".houseInfo-content").text());//是否有电梯
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
        saveHouseInfo(list, batchNo);
        handlePages(saleLeft, seeds, usedSeeds);
        if (!seeds.isEmpty())
            clawing(seeds, usedSeeds, batchNo);
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
                    for (String urlInQueue : seeds)
                        if (urlInQueue.equals(urlInPage)) {
                            isExist = true;
                            break;
                        }

                    if (!isExist) {
                        for (String urlInSet : usedSeeds)
                            if (urlInSet.equals(urlInPage)) {
                                isExist = true;
                                break;
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
            tHouseInfoMapper.insert(tHouseInfo);
            log.info("保存成功: {}", tHouseInfo);
        }
    }
}
