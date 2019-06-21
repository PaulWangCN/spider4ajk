package com.wangjx.spider4ajk.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * @ClassName HttpClient
 * @Description TODO
 * @Author Wangjx
 * @Date 2019/4/9 17:16
 * @Version 1.0
 **/
@Slf4j
@Component
public class HttpClient {

    private RestTemplate restTemplate = new RestTemplate();

    public String get(String url, Integer time) throws InterruptedException {
        //随机时间执行  防止太频繁被防爬虫屏蔽
        if (time == null) {
            time = (int) (Math.random() * 5 + 2);
        }
        Thread.sleep(time * 1000);
        //headers
        HttpHeaders requestHeaders = setHeaders();
        //body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        //HttpEntity
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(null, requestHeaders);
        //post
        log.info("休息{}秒后准备访问URL：{}", time, url);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            return null;
        }
        return responseEntity.getBody();
    }

    /**
     * 设置请求头
     * @return
     */
    private HttpHeaders setHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("cookie", "aQQ_ajkguid=47DFE71A-B579-2739-FDD6-3C775D2652FE; 58tj_uuid=380fa2a9-20c7-4f4a-83a5-e5b6c7eb1082; als=0; wmda_uuid=b3ec150524fa5d2a1bf920415c248488; wmda_new_uuid=1; wmda_visited_projects=%3B6289197098934; _ga=GA1.2.131990663.1544436735; sessid=4F646789-3903-5B98-7C8C-8178772C9A2B; lps=http%3A%2F%2Fshaoxing.anjuke.com%2Fmarket%2Fzhujinh%2F%7Chttps%3A%2F%2Fwww.google.com%2F; twe=2; ajk_member_captcha=652edc3ae4003a6a4a1c626dfdaea0ee; _gid=GA1.2.1989278788.1554794053; isp=true; Hm_lvt_c5899c8768ebee272710c9c5f365a6d8=1554795127; lp_lt_ut=d21754e1b9539ca78d0d6857fc4594c0; Hm_lpvt_c5899c8768ebee272710c9c5f365a6d8=1554799234; search_words=%E4%B8%AD%E5%A4%AE%E5%8D%8E%E5%BA%9C; browse_comm_ids=1147835%7C1135687%7C911286%7C911852%7C924746; propertys=r7sosz-pporcd_r0xppa-ppor9w_r69swn-ppoqx8_r2o16q-ppoqx1_r7cgju-pponer_qs6eza-pponaj_r83o8a-ppon8j_r3s70r-ppon37_r5r4b5-ppon0b_r5hqfp-ppomze_r7j3el-ppomy0_r50xyq-ppomwk_qxkb9m-ppomrn_; new_uv=3; ctid=66");
        requestHeaders.add("referer", "https://shaoxing.anjuke.com/sale/zhujinh/a626-d1/");
        requestHeaders.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
        requestHeaders.add("cache-control", "max-age=0");
        requestHeaders.add("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        requestHeaders.add("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        MediaType mediaType = new MediaType("text","html", Charset.forName("utf-8"));
        requestHeaders.setContentType(mediaType);
        return requestHeaders;
    }
}
