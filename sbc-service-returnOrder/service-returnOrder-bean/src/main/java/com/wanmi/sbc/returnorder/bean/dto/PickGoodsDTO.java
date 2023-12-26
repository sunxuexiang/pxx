package com.wanmi.sbc.returnorder.bean.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提货单参数dto
 */
@Data
@ApiModel
public class PickGoodsDTO implements Serializable {

    /**
     * 囤货单编号
     */
    private String newPileOrderNo;

    /**
     * skuId
     */
    private String goodsInfoId;

    /**
     * 提货数量/退货数量（放在新提货退单item中时）
     */
    private Long num;

    /**
     * 实付/应退 总金额
     */
    private BigDecimal splitPrice;

    /**
     * 应退余额
     */
    private BigDecimal returnBalancePrice;

    /**
     * 应退现金
     */
    private BigDecimal returnCashPrice;

    /**
     * 实退总金额
     */
    private BigDecimal actualSplitPrice;

    /**
     * 实退余额
     */
    private BigDecimal actualReturnBalancePrice;

    /**
     * 实退现金
     */
    private BigDecimal actualReturnCashPrice;
}
