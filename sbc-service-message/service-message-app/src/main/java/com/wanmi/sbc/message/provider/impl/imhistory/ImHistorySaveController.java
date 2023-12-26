package com.wanmi.sbc.message.provider.impl.imhistory;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.message.api.provider.imhistory.ImHistorySaveProvider;
import com.wanmi.sbc.message.appmessage.model.root.AppMessage;
import com.wanmi.sbc.message.bean.vo.AppMessageVO;
import com.wanmi.sbc.message.bean.vo.ImHistoryMsgVO;
import com.wanmi.sbc.message.bean.vo.MsgBody;
import com.wanmi.sbc.message.bean.vo.MsgList;
import com.wanmi.sbc.message.imhistory.root.ImHistory;
import com.wanmi.sbc.message.imhistory.root.ImHistoryMsg;
import com.wanmi.sbc.message.imhistory.service.ImHistoryMsgService;
import com.wanmi.sbc.message.imhistory.service.ImHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;



/**
 * <p>报错im历史数据</p>
 * Created by sgy on 2023/7/1.
 */
@RestController
public class ImHistorySaveController implements ImHistorySaveProvider {
    @Autowired
   private ImHistoryService imHistoryService;
    @Autowired
    private ImHistoryMsgService msgService;
    @Override
    public BaseResponse add(@RequestBody @Valid List<String> list) {
        StringBuilder builder =new StringBuilder();
        list.forEach(l->{
            builder.append(l);
        });
        ImHistoryMsgVO imHistoryMsgVO = JSONObject.parseObject(builder.toString(), ImHistoryMsgVO.class);
        List<MsgList> msgList = imHistoryMsgVO.getMsgList();
        msgList.forEach(msg->{
            ImHistory imHistory =new ImHistory();
            imHistory.setFromAccount(msg.getFrom_Account());
            imHistory.setToAccount(msg.getTo_Account());
            imHistory.setMsgSeq(msg.getMsgSeq());
            imHistory.setMsgRandom(msg.getMsgRandom());
            imHistory.setMsgTime(msg.getMsgTimestamp());
            ImHistory his = imHistoryService.add(imHistory);
            List<MsgBody> msgBody = msg.getMsgBody();
            msgBody.forEach(ms->{
                ImHistoryMsg imHistoryMsg =new ImHistoryMsg();
                imHistoryMsg.setImHistoryId(his.getId());
                imHistoryMsg.setMsgType(ms.getMsgType());
                imHistoryMsg.setMsgContent(ms.getMsgContent().getText());
                msgService.add(imHistoryMsg);
            });
        });
        return BaseResponse.SUCCESSFUL();
    }
}
