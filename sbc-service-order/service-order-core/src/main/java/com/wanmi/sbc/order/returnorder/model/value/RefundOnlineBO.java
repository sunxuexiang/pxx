package com.wanmi.sbc.order.returnorder.model.value;

import com.wanmi.sbc.order.refund.model.root.RefundBill;
import com.wanmi.sbc.pay.api.response.PayChannelItemResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/***
 * @desc  退款辅助类
 * @author shiy  2023/9/1 9:10
*/
@Data
@ApiModel
public class RefundOnlineBO{

    private PayTradeRecordResponse payTradeRecordResponse;

    private RefundBill refundBill;

    private PayChannelItemResponse channelItemResponse;
}
