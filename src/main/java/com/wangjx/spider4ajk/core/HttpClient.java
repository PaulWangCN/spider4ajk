package com.wangjx.spider4ajk.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName HttpClient
 * @Description TODO
 * @Author Wangjx
 * @Date 2019/4/9 17:16
 * @Version 1.0
 **/
@Component
public class HttpClient {

    private RestTemplate restTemplate = new RestTemplate();

    public String get(String url) {
        return restTemplate.getForObject(url, String.class);
    }

}
