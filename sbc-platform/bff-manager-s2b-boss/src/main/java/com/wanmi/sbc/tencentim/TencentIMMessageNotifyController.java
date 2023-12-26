package com.wanmi.sbc.tencentim;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.es.elastic.EsImMessage;
import com.wanmi.sbc.es.elastic.EsImMessageElasticService;
import com.wanmi.sbc.setting.api.provider.onlineservice.TencentIMNotifyProvider;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceTodayMessageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "TencentIMMessageNotifyController", description = "腾讯IM发送聊天消息回调通知API")
@RestController
@RequestMapping("/tencentIM")
@Slf4j
public class TencentIMMessageNotifyController {

    @Autowired
    private TencentIMNotifyProvider tencentIMNotifyProvider;

    @Autowired
    private EsImMessageElasticService esImMessageElasticService;

    @ApiOperation(value = "提供给腾讯回调的接口（用户发送聊天信息以后，会回调该接口）")
    @RequestMapping(value = "/sendMsgNotify", method = RequestMethod.POST)
    public JSONObject chatMsgNotify (@RequestBody JSONObject jsonParam) {
        log.info("腾讯IM发送消息通知参数 {}", jsonParam);

        BaseResponse<List<CustomerServiceTodayMessageResponse>> baseResponse = tencentIMNotifyProvider.chatMsgNotify(jsonParam);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("ActionStatus", "OK");  //请求处理的结果，OK 表示处理成功，FAIL 表示失败
        jsonResponse.put("ErrorCode", 0);  //错误码，0为回调成功；1为回调出错
        jsonResponse.put("ErrorInfo", "");  //错误信息
        if (ObjectUtils.isEmpty(baseResponse.getContext())) {
            return jsonResponse;
        }
        for (CustomerServiceTodayMessageResponse msg : baseResponse.getContext()) {
            if (msg.getMsgTime() == null) {
                continue;
            }
            EsImMessage message = new EsImMessage();
            message.setMsgTimestamp(msg.getMsgTime());
            message.setFromAccount(msg.getFromAccount());
            message.setToAccount(msg.getToAccount());
            message.setMsgType(msg.getMessageType());
            message.setMsgContent(msg.getMessage());
            message.setStoreId(msg.getStoreId());
            message.setGroupId(msg.getGroupId());
            message.setFileUrl(msg.getFileUrl());
            message.setSendType(msg.getSendType());
            message.setQuoteMessage(msg.getQuoteMessage());
            esImMessageElasticService.writeMessage(message);
        }
        return jsonResponse;
    }
}
