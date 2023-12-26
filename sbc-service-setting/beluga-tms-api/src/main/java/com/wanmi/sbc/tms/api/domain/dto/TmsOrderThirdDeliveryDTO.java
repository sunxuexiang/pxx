package com.wanmi.sbc.tms.api.domain.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 承运单第三方物流单信息DTO
 */
@Data
public class TmsOrderThirdDeliveryDTO implements Serializable {


    //("承运单id")
    private String tmsOrderId;

    //("订单id")
    private String tradeOrderId;

    //("第三方发货单号")
    private String thirdPartyDeliveryOrderNo;

    //("第三方发货单号类型")
    private String thirdPartyDeliveryType;

    //("第三方发货单号信息")
    private String thirdPartyDeliveryInfo;

    //("未发货需要退款的运费金额")
    private Double refundAmount;
}
