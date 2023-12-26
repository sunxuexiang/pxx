package com.wanmi.sbc.wms.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.wms.request.WmsBaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/4 9:14
 */

@Service
@Slf4j
public class SendChainService {

    @Value("${send.chain.domian}")
    private String sendChainDomian;

    public String sendChain(WmsBaseRequest req , String uri) {

        String url = sendChainDomian + uri;
        log.info("=====复制请求到大白鲨：{}", url);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        //接口参数
        MultiValueMap<String, Object> map = getStringObjectMultiValueMap(req);

        log.info("=====复制请求到大白鲨参数：{}", JSON.toJSONString(map));
        //头部类型
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //构造实体对象
        HttpEntity<MultiValueMap<String, Object>> param = new HttpEntity<>(map, headers);
        //发起请求,服务地址，请求参数，返回消息体的数据类型
        String result = restTemplate.postForObject(url, param, String.class);
        log.info("=====复制请求到大白鲨返回：{}", result);
        return result;
    }

    public static MultiValueMap<String, Object> getStringObjectMultiValueMap(WmsBaseRequest req) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("method", req.getMethod());
        map.add("client_customerid", req.getClient_customerid());
        map.add("client_db", req.getClient_db());
        map.add("messageid", req.getMessageid());
        map.add("appkey", req.getAppkey());
        map.add("apptoken", req.getApptoken());
        map.add("timestamp", req.getTimestamp());
        map.add("sign", req.getSign());
        map.add("data", req.getData());
        return map;
    }
}
