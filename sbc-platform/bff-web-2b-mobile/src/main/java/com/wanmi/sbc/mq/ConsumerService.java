package com.wanmi.sbc.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerForErpRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerSynFlagRequest;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.order.bean.vo.GrouponInstanceVO;
import com.wanmi.sbc.util.HttpClientUtil;
import com.wanmi.sbc.util.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Component
@Slf4j
@EnableBinding(MobileSink.class)
public class ConsumerService {

    @Value("${wms.api.flag}")
    public String pushFlag;

    @Value("${d2p.erp-url}")
    public String url;

    private static final String TO_CLIENT = "TC";
    private static final String TO_BUSINES = "TB";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CustomerProvider customerProvider;

    /**
     * 团长开团-消息推送C端
     * @param json
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_OPEN_GROUPON_MESSAGE_PUSH_TO_C)
    public void openGrouponMsgToC(String json) {
        log.info("团长开团-消息推送C端(消费端)========》{}",json);
        GrouponInstanceVO vo = JSONObject.parseObject(json,GrouponInstanceVO.class);
        //发送消息
        messagingTemplate.convertAndSend("/topic/getGrouponInstance", vo);
    }

    /**
     * 新增会员信息至erp中间表
     * @param request
     */
    @StreamListener(com.wanmi.sbc.customer.api.constant.JmsDestinationConstants.Q_ERP_SERVICE_ADD_CUSTOMER)
    public void addCustomerErp(@RequestBody @Valid CustomerForErpRequest request){
        log.info("会员注册，发送会员信息值erp中间表=======》{}", request);
        if(Objects.nonNull(request) && "true".equals(pushFlag)){
            String customerType = TO_CLIENT;
            String customerName = request.getCustomerAccount();
            if(!CustomerRegisterType.COMMON.equals(request.getCustomerRegisterType())
                    && EnterpriseCheckState.CHECKED.equals(request.getEnterpriseStatusXyy())){
                customerType = TO_BUSINES;
            }
            Map<String,Object> requestMap = new HashMap<>();
            requestMap.put("customerType",customerType);
            requestMap.put("customerErpId",request.getCustomerErpId());
            requestMap.put("customerDescr1",customerName);
            HttpResult result = HttpClientUtil.post(url,requestMap);
            this.afterDelResult(request,result);
            log.info("会员注册，接收erp入表的信息=======》{}", result);
        }
    }

    @Async
    public void afterDelResult(CustomerForErpRequest request ,HttpResult result){
        if(Objects.nonNull(result) && "200".equals(result.getResultCode())){
            if(!StringUtils.isEmpty(result.getResultData())){
                BaseResponse response = JSONObject.parseObject(result.getResultData(), BaseResponse.class);
                if(CommonErrorCode.SUCCESSFUL.equals(response.getCode())){
                    customerProvider.asyncErpFlag(CustomerSynFlagRequest.builder()
                            .customerAccount(request.getCustomerAccount())
                            .build());
                }
            }
        }
    }
}
