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
        log.info("本次爬虫开始，批次：{}");
        LinkedBlockingQueue<String> seeds = new LinkedBlockingQueue<>();
        Set<String> usedSeeds = new HashSet<>();
        try {
            seeds.put("https://shaoxing.anjuke.com/sale/zhujinh/a626-b514-p1/");
            clawing(seeds, usedSeeds, batchNo);
            log.info("本次爬虫结束，批次：{}");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clawing(LinkedBlockingQueue<String> seeds, Set<String> usedSeeds, Long batchNo) throws InterruptedException {
        String url;
        Document document = Jsoup.parse(httpClient.get(url = seeds.poll()));
        usedSeeds.add(url.replace("#filtersort", ""));
        Element saleLeft = document.selectFirst(".sale-left");
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
        Elements elements = saleLeft.select("#houselist-mod-new").select("li");
        List<HouseInfoVO> list = new ArrayList<>();
        for (Element element : elements) {
            Element houseDetails = element.selectFirst(".house-details");
            HouseInfoVO houseInfoVO = new HouseInfoVO();
            houseInfoVO.setTitle(houseDetails.select(".house-title").text());
            Elements details = houseDetails.select(".details-item");
            Elements detail1 = details.get(0).select("span");
            houseInfoVO.setLayout(detail1.get(0).text());
            String area = detail1.get(1).text();
            houseInfoVO.setArea(new Double(area.substring(0, area.indexOf("m"))));
            houseInfoVO.setFloor(detail1.get(2).text());
            houseInfoVO.setBuildTime(detail1.get(3).text());
            Elements detail2 = details.get(1).select("span");
            houseInfoVO.setAddress(detail2.get(0).text().split(" ")[1]);
            houseInfoVO.setVillage(detail2.get(0).text().split(" ")[0]);
            String priceDet = element.select(".price-det").text();
            String unitPrice = element.select(".unit-price").text();
            houseInfoVO.setSumPrice(new Double(priceDet.substring(0, priceDet.indexOf("万"))));
            houseInfoVO.setPerPrice(new Double(unitPrice.substring(0, unitPrice.indexOf("元/m²"))));
            list.add(houseInfoVO);
        }
        saveHouseInfo(list, batchNo);
        if (!seeds.isEmpty())
            clawing(seeds, usedSeeds, batchNo);
    }

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
