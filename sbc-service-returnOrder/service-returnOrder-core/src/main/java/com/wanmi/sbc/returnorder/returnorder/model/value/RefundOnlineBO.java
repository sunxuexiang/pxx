package com.wanmi.sbc.returnorder.returnorder.model.value;

import com.wanmi.sbc.returnorder.refund.model.root.RefundBill;
import com.wanmi.sbc.pay.api.response.PayChannelItemResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/***
 * @desc  退款辅助类
 * @author shiy  2023/9/1 9:10
*/
@Data
@ApiModel
public class RefundOnlineBO{

    private static final long serialVersionUID = -6952082465485575874L;

    private PayTradeRecordResponse payTradeRecordResponse;

    private RefundBill refundBill;

    private PayChannelItemResponse channelItemResponse;
}
