package com.wanmi.sbc.setting.imonlineservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.ImChatStateEnum;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceTodayMessageResponse;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceChatRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceTodayMessageRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChat;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceTodayMessage;
import com.wanmi.sbc.setting.imonlineservice.root.ImQuoteMessageVo;
import com.wanmi.sbc.setting.imonlineserviceitem.repository.ImOnlineServiceItemRepository;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>在线客服聊天消息服务类</p>
 * @author zhouzhenguo
 * @date 2023-09--01 16:10:28
 */
@Service
@Slf4j
public class CustomerServiceTodayMessageService {

    @Autowired
    private CustomerServiceTodayMessageRepository customerServiceTodayMessageRepository;

    @Autowired
    private CustomerServiceChatRepository customerServiceChatRepository;

    @Autowired
    private CustomerServiceChatService customerServiceChatService;

    private Pattern pattern = Pattern.compile("^[\\d]+$");

    public List<CustomerServiceTodayMessageResponse> chatMsgNotify(JSONObject jsonParam) {
        CustomerServiceTodayMessage msgData = new CustomerServiceTodayMessage();
        msgData.setFromAccount(jsonParam.getString("From_Account"));
        msgData.setToAccount(jsonParam.getString("To_Account"));
        msgData.setMsgTime(jsonParam.getLong("MsgTime"));
        msgData.setOnlineOnlyFlag(jsonParam.getString("OnlineOnlyFlag"));
        msgData.setSendMsgResult(jsonParam.getString("SendMsgResult"));
        msgData.setGroupId(jsonParam.getString("GroupId"));
        JSONArray msgBody = jsonParam.getJSONArray("MsgBody");
        String sendCharacters = null;
        List<CustomerServiceTodayMessage> msgList = new ArrayList<>();
        if (msgBody != null && msgBody.size() > 0) {
            for (int i=0; i<msgBody.size(); i++) {
                JSONObject contentJson = msgBody.getJSONObject(i);
                CustomerServiceTodayMessage customerServiceTodayMessage = KsBeanUtil.convert(msgData, CustomerServiceTodayMessage.class);
                if (contentJson != null) {
                    customerServiceTodayMessage.setMessageType(contentJson.getString("MsgType"));
                    if ("TIMTextElem".equals(customerServiceTodayMessage.getMessageType())) {
                        JSONObject msgContent = contentJson.getJSONObject("MsgContent");
                        if (msgContent != null) {
                            customerServiceTodayMessage.setMessage(msgContent.getString("Text"));
                            sendCharacters = customerServiceTodayMessage.getMessage();
                        }
                        JSONObject cloudCustomData = jsonParam.getJSONObject("CloudCustomData");
                        if (!ObjectUtils.isEmpty(cloudCustomData) && cloudCustomData.containsKey("messageReply")) {
                            JSONObject replyJson = cloudCustomData.getJSONObject("messageReply");

                            ImQuoteMessageVo quoteMessageVo = new ImQuoteMessageVo();
                            if (replyJson.containsKey("messageAbstract")) {
                                quoteMessageVo.setMsgContent(replyJson.getString("messageAbstract"));
                            }
                            if (replyJson.containsKey("messageID")) {
                                quoteMessageVo.setMsgId(replyJson.getString("messageID"));
                            }
                            if (replyJson.containsKey("messageSender")) {
                                quoteMessageVo.setSender(replyJson.getString("messageSender"));
                            }
                            if (replyJson.containsKey("messageTime")) {
                                Long msgTime = replyJson.getLong("messageTime");
                                quoteMessageVo.setMsgTimestamp(msgTime);
                            }
                            customerServiceTodayMessage.setQuoteMessage(JSON.toJSONString(quoteMessageVo));
                        }
                    } else if ("TIMFileElem".equals(customerServiceTodayMessage.getMessageType())) {
                        JSONObject msgContent = contentJson.getJSONObject("MsgContent");
                        String fileName = msgContent.getString("FileName");
                        String fileUrl = msgContent.getString("Url");
                        customerServiceTodayMessage.setMessage(fileName);
                        customerServiceTodayMessage.setFileUrl(fileUrl);
                    } else if ("TIMVideoFileElem".equals(customerServiceTodayMessage.getMessageType())) {
                        JSONObject msgContent = contentJson.getJSONObject("MsgContent");
                        String fileName = msgContent.getString("ThumbUrl");
                        String fileUrl = msgContent.getString("VideoUrl");
                        customerServiceTodayMessage.setMessage(fileName);
                        customerServiceTodayMessage.setFileUrl(fileUrl);
                    } else if ("TIMImageElem".equals(customerServiceTodayMessage.getMessageType())) {
                        JSONObject msgContent = contentJson.getJSONObject("MsgContent");
                        String fileName = msgContent.getString("UUID");
                        customerServiceTodayMessage.setMessage(fileName);
                        JSONArray imageArrayJson = msgContent.getJSONArray("ImageInfoArray");
                        String fileUrl = imageArrayJson.getJSONObject(0).getString("URL");
                        customerServiceTodayMessage.setFileUrl(fileUrl);
                    } else if ("TIMCustomElem".equals(customerServiceTodayMessage.getMessageType())) {
                        try {
                            JSONObject msgContent = contentJson.getJSONObject("MsgContent");
                            if (msgContent.containsKey("Data")) {
                                String customDataStr = msgContent.getString("Data");
                                JSONObject customData = JSON.parseObject(customDataStr);
                                if (customData.containsKey("businessID")) {
                                    String businessID = customData.getString("businessID");
                                    if ("user_typing_status".equals(businessID)) {
                                        continue;
                                    }
                                }
                            }
                        }
                        catch (Exception e){
                            log.error("解析聊天回调消息异常", e);
                            continue;
                        }
                    } else if ("TIMSoundElem".equals(customerServiceTodayMessage.getMessageType())) {
                        JSONObject msgContent = contentJson.getJSONObject("MsgContent");
                        String fileName = msgContent.getString("UUID");
                        String fileUrl = msgContent.getString("Url");
                        customerServiceTodayMessage.setMessage(fileName);
                        customerServiceTodayMessage.setFileUrl(fileUrl);
                    }
                }
                msgList.add(customerServiceTodayMessage);
            }
        }
        String msgType = null;
        try {
            JSONObject cloudCustomData = jsonParam.getJSONObject("CloudCustomData");
            msgType = cloudCustomData.getString("msgType");
        }
        catch (Exception e) {
            log.info("群组消息，解析自定义内容提 {}", e.getMessage());
        }

        CustomerServiceChat customerServiceChat = null;
        if (!StringUtils.isEmpty(msgData.getGroupId())) {
            customerServiceChat = customerServiceChatRepository.findByImGroupId(msgData.getGroupId());
        }
        else {
            customerServiceChat = customerServiceChatRepository.findByServiceAccountAndUserAccount(msgData.getFromAccount(), msgData.getToAccount());
            if (customerServiceChat == null) {
                customerServiceChat = customerServiceChatRepository.findByServiceAccountAndUserAccount(msgData.getToAccount(), msgData.getFromAccount());
            }
        }
        if (customerServiceChat == null) {
            return KsBeanUtil.convert(msgList, CustomerServiceTodayMessageResponse.class);
        }

        for (CustomerServiceTodayMessage customerServiceTodayMessage : msgList) {
            customerServiceTodayMessage.setCompanyInfoId(customerServiceChat.getCompanyInfoId());
            customerServiceTodayMessage.setStoreId(customerServiceChat.getStoreId());
            if ("system".equals(msgType)) {
                customerServiceTodayMessage.setSendType(0);
            }
            else if (msgData.getFromAccount().equals(customerServiceChat.getCustomerImAccount())) {
                customerServiceTodayMessage.setSendType(2);
            }
            else {
                customerServiceTodayMessage.setSendType(1);
            }
        }
        customerServiceTodayMessageRepository.saveAll(msgList);

        if ("system".equals(msgType)) {
            customerServiceChat.setSendState(1);
            if (StringUtils.isEmpty(customerServiceChat.getLastMessage())  && sendCharacters.length() < 300) {
                customerServiceChat.setLastMessage(sendCharacters);
            }
            customerServiceChatService.saveChat(customerServiceChat);
            return KsBeanUtil.convert(msgList, CustomerServiceTodayMessageResponse.class);
        }

        customerServiceChat.setMsgTime(msgData.getMsgTime());
        if (!ImChatStateEnum.queue.getState().equals(customerServiceChat.getChatState()) && customerServiceChat.getChatState() == null) {
            customerServiceChat.setChatState(0);
        }
        customerServiceChat.setTimeoutState(0);
        if (msgData.getFromAccount().equals(customerServiceChat.getCustomerImAccount())) {
            customerServiceChat.setSendState(0);
            customerServiceChat.setUserTimeoutState(0);

            if (((Integer)1).equals(customerServiceChat.getServiceState())) {
                customerServiceChat.setSendLeave(1);
            }
        }
        else {
            customerServiceChat.setSendState(1);
            customerServiceChat.setServiceState(0);
            if (customerServiceChat.getStartTime() == null || customerServiceChat.getStartTime() < 1) {
                customerServiceChat.setStartTime(System.currentTimeMillis() / 1000);
            }
        }
        if (!StringUtils.isEmpty(sendCharacters) && sendCharacters.length() < 300) {
            customerServiceChat.setLastMessage(sendCharacters);
        }
        customerServiceChatService.saveChat(customerServiceChat);

        return KsBeanUtil.convert(msgList, CustomerServiceTodayMessageResponse.class);
    }
}
