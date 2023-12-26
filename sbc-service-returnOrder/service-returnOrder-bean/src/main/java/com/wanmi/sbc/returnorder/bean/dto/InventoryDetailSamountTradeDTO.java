package com.wanmi.sbc.returnorder.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel
@Data
public class InventoryDetailSamountTradeDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long inventoryDetailsAmountTradeId;

    /**
     * 订单id
     */
    @ApiModelProperty(name = "订单id")
    private String tradeId;

    /**
     * 退货单Id
     */
    @ApiModelProperty(name = "退货单Id")
    private String returnId;

    /**
     * 商品goods_info_id 这里没拆箱所有用的是goods_info_id
     */
    @ApiModelProperty(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 数量级平摊价格 例子 10元买了3个 那么一个存的是 3.33元  最后一个存的是3.34元
     */
    @ApiModelProperty(name = "amortized_expenses")
    private BigDecimal amortizedExpenses;


    /**
     * 0是未退款1是已退款2已受理退款(已申请退款)3预退款
     */
    @ApiModelProperty(name = "0是未退款1是已退款")
    private int returnFlag =0;


    /**
     * 金钱类型0是余额1真实的钱
     */
    @ApiModelProperty(name = "money_type")
    private int moneyType;



    /**
     * 超时未支付取消订单时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

}
