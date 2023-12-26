package com.wanmi.sbc.pushsend.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.base.Splitter;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.crm.api.provider.customgrouprel.CustomGroupRelProvide;
import com.wanmi.sbc.crm.api.request.crmgroup.CrmGroupRequest;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerListByConditionRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerListByConditionResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.message.api.provider.pushsend.PushSendProvider;
import com.wanmi.sbc.message.api.request.messagesend.MessageSendAddRequest;
import com.wanmi.sbc.message.api.request.messagesend.MessageSendModifyRequest;
import com.wanmi.sbc.message.api.request.pushsend.PushSendAddRequest;
import com.wanmi.sbc.message.api.request.pushsend.PushSendModifyRequest;
import com.wanmi.sbc.message.api.response.pushsend.PushSendAddResponse;
import com.wanmi.sbc.message.api.response.pushsend.PushSendModifyResponse;
import com.wanmi.sbc.message.bean.enums.MessageSendType;
import com.wanmi.sbc.message.bean.enums.MessageType;
import com.wanmi.sbc.message.bean.enums.PushFlag;
import com.wanmi.sbc.message.bean.enums.SendType;
import com.wanmi.sbc.message.service.MessageService;
import com.wanmi.sbc.mq.PushQueryProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: sbc-micro-service
 * @description: 新增push消息
 * @create: 2020-01-14 11:33
 **/
@Service
public class PushSendService {

    @Autowired
    private PushSendProvider pushSendProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomGroupRelProvide customGroupRelProvide;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PushQueryProducer pushQueryProducer;

    @LcnTransaction
    public BaseResponse<PushSendAddResponse> add(PushSendAddRequest addReq) {
        //前端发的是账号，根据账号查询用户唯一ID

        if (!Objects.isNull(addReq.getMsgRecipient()) && "4".equals(addReq.getMsgRecipient().toString())) {
            List<String> result = Splitter.on(",").trimResults().splitToList(addReq.getMsgRecipientDetail());

            List<CustomerDetailVO> detailResponseList = customerQueryProvider.listCustomerDetailByCondition(CustomerDetailListByConditionRequest.builder()
                    .customerNames(result).build()).getContext().getDetailResponseList();

            if(detailResponseList.isEmpty()){
                throw new SbcRuntimeException("K-999999","找不到对应用户数据");
            }

            List<String> ids = detailResponseList.stream().map(CustomerDetailVO :: getCustomerId).collect(Collectors.toList());
            StringBuffer msgRecipientDetail = new StringBuffer();
            for (String str: ids) {
                msgRecipientDetail.append(str).append(",");
            }
            if((msgRecipientDetail.length() - 1) > 0 ){
                msgRecipientDetail.deleteCharAt(msgRecipientDetail.length() - 1);
            }
            addReq.setMsgRecipientDetail(msgRecipientDetail.toString());
        }

        List<String> customerIds = this.getCustomerIds(addReq.getMsgRecipient(), addReq.getMsgRecipientDetail());
        addReq.setCustomers(customerIds);
        addReq.setDelFlag(DeleteFlag.NO);
        PushSendAddResponse pushSendAddResponse = pushSendProvider.add(addReq).getContext();

        MessageSendAddRequest messageSendAddRequest = new MessageSendAddRequest();
        messageSendAddRequest.setPushId(pushSendAddResponse.getPushSendVO().getId().toString());
        messageSendAddRequest.setContent(addReq.getMsgContext());
        messageSendAddRequest.setImgUrl(addReq.getMsgImg());
        messageSendAddRequest.setPushFlag(addReq.getPushFlag());
        messageSendAddRequest.setName(addReq.getMsgName());
        messageSendAddRequest.setMessageType(MessageType.Preferential);
        messageSendAddRequest.setSendTimeType(SendType.NOW);
        messageSendAddRequest.setSendTime(LocalDateTime.now());
        if (addReq.getPushTime() != null){
            messageSendAddRequest.setSendTimeType(SendType.DELAY);
            messageSendAddRequest.setSendTime(addReq.getPushTime());
        }
        messageSendAddRequest.setSendType(MessageSendType.fromValue(addReq.getMsgRecipient()));
        messageSendAddRequest.setTitle(addReq.getMsgTitle());
        if (Objects.nonNull(addReq.getMsgRecipientDetail())){
            messageSendAddRequest.setJoinIds(Arrays.asList(addReq.getMsgRecipientDetail().split(",")));
        }

        if (PushFlag.OPERATION_PLAN.equals(addReq.getPushFlag())){
            messageSendAddRequest.setPlanId(addReq.getPlanId());
            pushQueryProducer.query(pushSendAddResponse.getPushSendVO());
        }
        messageSendAddRequest.setRouteParams(addReq.getMsgRouter());
        messageService.addMessage(messageSendAddRequest);
        return BaseResponse.success(pushSendAddResponse);
    }

    @LcnTransaction
    public BaseResponse<PushSendModifyResponse> modify(PushSendModifyRequest modifyReq) {
        List<String> customerIds = this.getCustomerIds(modifyReq.getMsgRecipient(), modifyReq.getMsgRecipientDetail());
        modifyReq.setCustomers(customerIds);

        MessageSendModifyRequest modifyRequest = new MessageSendModifyRequest();
        modifyRequest.setPushId(modifyReq.getId().toString());
        modifyRequest.setContent(modifyReq.getMsgContext());
        modifyRequest.setImgUrl(modifyReq.getMsgImg());
        modifyRequest.setName(modifyReq.getMsgName());
        modifyRequest.setSendTimeType(SendType.NOW);
        modifyRequest.setSendTime(LocalDateTime.now());
        modifyRequest.setRouteParams(modifyReq.getMsgRouter());
        if (modifyReq.getPushTime() != null){
            modifyRequest.setSendTimeType(SendType.DELAY);
            modifyRequest.setSendTime(modifyReq.getPushTime());
        }
        modifyRequest.setSendType(MessageSendType.fromValue(modifyReq.getMsgRecipient()));
        if (Objects.nonNull(modifyReq.getMsgRecipientDetail())){
            modifyRequest.setJoinIds(Arrays.asList(modifyReq.getMsgRecipientDetail().split(",")));
        }
        modifyRequest.setTitle(modifyReq.getMsgTitle());
        messageService.modify(modifyRequest);

        return pushSendProvider.modify(modifyReq);
    }

    private List<String> getCustomerIds(int msgRecipient, String msgRecipientDetail){
        List<String> customerIds = null;
        switch (msgRecipient){
            case 1:
                String[] detail = msgRecipientDetail.split(",");
                List<Long> levelIds = new ArrayList<>();
                for (String levelId : detail){
                    levelIds.add(Long.parseLong(levelId));
                }
                /*CustomerListByConditionResponse response =
                        customerQueryProvider.listCustomerByCondition(CustomerListByConditionRequest.builder()
                                .customerLevelIds(levelIds).build()).getContext();
                customerIds =
                        response.getCustomerVOList().stream().map(CustomerVO::getCustomerId).collect(Collectors.toList());*/
                // 上面查询事务超时，新增方法查询用户ID方法
                customerIds = customerQueryProvider.getCustomerIdsByLevelIds(levelIds).getContext();
                break;
            case 2:
                break;
            case 3:
                List<String> result = Splitter.on(",").trimResults().splitToList(msgRecipientDetail);
                // 系统人群ID
                List<Long> systemGroupId =
                        result.stream().filter(i -> i.startsWith("0_"))
                                .map(i-> Long.parseLong(i.replace("0_", ""))).collect(Collectors.toList());
                // 自定义人群ID
                List<Long> groupId =
                        result.stream().filter(i -> i.startsWith("1_"))
                                .map(i-> Long.parseLong(i.replace("1_", ""))).collect(Collectors.toList());
                customerIds = customGroupRelProvide.queryListByGroupIds(CrmGroupRequest.builder()
                        .customGroupList(groupId).sysGroupList(systemGroupId).build()).getContext();

                break;
            case 4:
                customerIds = Splitter.on(",").trimResults().splitToList(msgRecipientDetail);
                break;
            default:
        }
        return customerIds;
    }
}