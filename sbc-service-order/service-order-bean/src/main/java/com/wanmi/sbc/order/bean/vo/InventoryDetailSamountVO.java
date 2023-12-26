package com.wanmi.sbc.order.bean.vo;

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
public class InventoryDetailSamountVO implements Serializable {
    private static final long serialVersionUID = 2203295511010072238L;


    @ApiModelProperty(value = "id")
    private Long inventoryDetailsAmountId;


    /**
     * 囤货单id
     */
    @ApiModelProperty(name = "newPileTrade_id")
    private String newPileTradeId;


    /**
     * 提货单Id
     */
    @ApiModelProperty(name = "takeId")
    private String takeId;

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
     * 0是未提货1是提货
     */
    @ApiModelProperty(name = "take_flag")
    private int takeFlag =0;


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

    @ApiModelProperty(name = "退单号")
    private String returnId;

    @ApiModelProperty(name = "退单类型")
    private String returnType;

}
