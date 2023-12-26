package com.wanmi.sbc.third.share;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.wechatshareset.WechatShareSetQueryProvider;
import com.wanmi.sbc.setting.api.request.wechatshareset.WechatShareSetInfoByStoreIdRequest;
import com.wanmi.sbc.setting.api.request.wechatshareset.WechatShareSetInfoRequest;
import com.wanmi.sbc.setting.api.response.wechatshareset.WechatShareSetInfoResponse;
import com.wanmi.sbc.third.share.request.GetSignRequest;
import com.wanmi.sbc.third.share.response.TicketResponse;
import com.wanmi.sbc.third.share.response.WeChatTicketResponse;
import com.wanmi.sbc.third.share.response.WeChatTokenResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/third/share")
@Api(tags = "WeChatPublicPlatformController", description = "微信公众平台Controller")
public class WeChatPublicPlatformController {

    private static final Logger log = LoggerFactory.getLogger(WeChatPublicPlatformController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WechatShareSetQueryProvider wechatShareSetQueryProvider;

    @Autowired
    private CommonUtil commonUtil;


    private static String TOKEN_KEY = "get_access_token_PUBLIC";

    private static String TICKET_KEY = CacheKeyConstant.WE_CHAT + SpecialSymbols.COLON + "TICKET";

    @ApiOperation(value = "获得微信sign")
    @RequestMapping(value = {"/weChat/getSign"}, method = RequestMethod.POST)
    public BaseResponse<TicketResponse> getSign(@Validated @RequestBody GetSignRequest request) {
        //获取appId,secret
        String appId;
        String appSecret;
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainInfo)){
            BaseResponse<WechatShareSetInfoResponse> setInfoResponse = wechatShareSetQueryProvider.getInfoByStoreId(WechatShareSetInfoByStoreIdRequest
                    .builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if(Objects.nonNull(setInfoResponse)
                    && Objects.nonNull(setInfoResponse.getContext())){
                appId = setInfoResponse.getContext().getShareAppId();
                appSecret = setInfoResponse.getContext().getShareAppSecret();
            }else {
                throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
            }
        }else {
            WechatShareSetInfoResponse infoResponse = wechatShareSetQueryProvider.getInfo(WechatShareSetInfoRequest.builder().
                    operatePerson(commonUtil.getOperatorId()).build()).getContext();
            appId = infoResponse.getShareAppId();
            appSecret = infoResponse.getShareAppSecret();
        }
        String ticket = this.getTicket(this.getToken(appId, appSecret));
        TicketResponse response = WeChatSign.sign(ticket, request.getUrl());
        response.setAppId(appId);
        return BaseResponse.success(response);

    }

    private String getToken(String appId, String appSecret) {
        String token = redisService.getString(TOKEN_KEY);
        if (!StringUtils.isBlank(token)) {
            return token;
        }
        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("secret", appSecret);
        WeChatTokenResponse response = restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}", WeChatTokenResponse.class, map);
        if (response.getErrcode() != null) {
            log.error("获得微信token异常：请求参数：{},response:{}", map.toString(), response);
            throw new RuntimeException("获得微信token异常");
        }
        token = response.getAccess_token();
        redisService.setString(TOKEN_KEY, token, response.getExpires_in() - 200);
        return token;
    }

    private String getTicket(String token) {
        String ticket = redisService.getString(TICKET_KEY);
        if (!StringUtils.isBlank(ticket)) {
            return ticket;
        }
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        WeChatTicketResponse response = restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={access_token}&type=jsapi", WeChatTicketResponse.class, map);
        if (response == null || response.getErrcode() != 0) {
            log.error("获得微信ticket异常：请求参数：" + map.toString());
            throw new RuntimeException("获得微信ticket异常");
        }
        ticket = response.getTicket();
        redisService.setString(TICKET_KEY, ticket, response.getExpires_in() - 200);
        return ticket;
    }
}
