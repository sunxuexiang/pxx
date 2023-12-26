package com.wanmi.sbc.account.sms;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.account.bean.enums.SmsTemplate;
import com.wanmi.sbc.common.constant.MQConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信服务
 * Created by aqlu on 15/12/4.
 */
@Component
public class SmsSendUtil {

    @Autowired
    private BinderAwareChannelResolver resolver;

    /**
     * 发送短信
     * @param smsTemplate 短信模板
     * @param phones      手机号码
     * @param params      手机参数
     */
    public void send(SmsTemplate smsTemplate, String[] phones, String... params) {
        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, String> dto = new HashMap<>();

        dto.put("code", params[0]);
        paramsMap.put("templateParamDTO", dto);
        paramsMap.put("businessType", smsTemplate.name());
        paramsMap.put("phoneNumbers", StringUtils.join(phones,","));
        resolver.resolveDestination(MQConstant.Q_SMS_SEND_CODE_MESSAGE_ADD).send(new GenericMessage<>(JSONObject.toJSONString(paramsMap)));
    }
}
