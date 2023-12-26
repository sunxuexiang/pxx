package com.wanmi.sbc.tms.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 快递到家-订单保存VO
 * @author jkp
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressTradeSaveVO implements Serializable {
    private static final long serialVersionUID = 1466616553963975952L;

    //("订单id")
    private String tradeOrderId;

    //("p订id")
    private String tradeParentOrderId;

    //("店铺id")
    private Long storeId;

    //("店铺名称")
    private String storeName;

    //("订单金额")
    private BigDecimal tradeAmount;

    //("商品总件数")
    private Integer goodsTotalNum;

    //("订单类型")
    private Integer tradeType;

    //("订单状态")
    private String tradeStatus;
}
