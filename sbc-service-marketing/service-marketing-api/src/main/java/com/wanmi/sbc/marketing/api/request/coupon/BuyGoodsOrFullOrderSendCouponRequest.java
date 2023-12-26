package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-09-09 11:45
 */
@ApiModel
@Data
public class BuyGoodsOrFullOrderSendCouponRequest implements Serializable {

    private static final long serialVersionUID = -6391420048468987728L;

    @ApiModelProperty(value = "客户id")
    // @NotNull
    private String customerId;

    @ApiModelProperty(value = "购买商品skuIds")
    private List<String> skuIds;

    @ApiModelProperty("购买商品列表")
    private List<TradeItemInfoDTO> tradeItemInfoDTOS;

    @ApiModelProperty(value = "优惠券活动类型")
    // @NotNull
    private CouponActivityType type;

    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 订单金额
     */
    @ApiModelProperty("订单金额")
    private BigDecimal orderPrice;

    @ApiModelProperty(value = "仓库Id")
    private Long wareId;
}
