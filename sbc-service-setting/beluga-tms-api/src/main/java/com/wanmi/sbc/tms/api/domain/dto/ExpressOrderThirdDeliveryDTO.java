package com.wanmi.sbc.tms.api.domain.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 承运单第三方物流单信息DTO
 */
@Data
public class ExpressOrderThirdDeliveryDTO implements Serializable {


    //("承运单id")
    private String expressOrderId;

    //("订单id")
    private String tradeOrderId;

    //("快递到家发货单号")
    private String thirdOrderNo;

    //("快递到家发货单号类型")
    private String thirdOrderType;

    //("快递到家发货单号信息")
    private String thirdOrderInfo;

    //("未发货需要退款的运费金额")
    private Double refundAmount;

}
