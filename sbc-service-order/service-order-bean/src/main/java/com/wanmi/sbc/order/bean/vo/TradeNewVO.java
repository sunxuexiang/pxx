package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.order.bean.enums.OrderSource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class TradeNewVO implements Serializable {

    private static final long serialVersionUID = 7283203999012896694L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String id;

    /**
     * 父订单号，用于不同商家订单合并支付场景
     */
    @ApiModelProperty(value = "父订单号，用于不同商家订单合并支付场景")
    private String parentId;

    /**
     * 订单组号
     */
    @ApiModelProperty(value = "订单组号")
    private String groupId;

    /**
     * 开店礼包
     */
    @ApiModelProperty(value = "开店礼包")
    private DefaultFlag storeBagsFlag = DefaultFlag.NO;

    /**
     * 是否可退标识
     */
    @ApiModelProperty(value = "是否可退标识", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean canReturnFlag;

    /**
     *子订单列表
     */
    @ApiModelProperty(value = "子订单列表")
    private List<TradeNewVO> tradeNewVOList;

    /**
     * 商家
     */
    @ApiModelProperty(value = "商家")
    private SupplierVO supplier;

    /**
     * 订单总体状态
     */
    @ApiModelProperty(value = "订单总体状态")
    private TradeStateVO tradeState;

    /**
     * 订单价格
     */
    @ApiModelProperty(value = "订单价格")
    private TradePriceVO tradePrice;

    /**
     * 订单商品图片
     */
    @ApiModelProperty(value = "订单商品图片")
    private List<String> tradeItemPic = new ArrayList<>();

    /**
     * 仓库编号
     */
    private String wareHouseCode;

    /**
     * 仓库Id
     */
    private Long wareId;

    /**
     * 商品总件数
     */
    private Long goodsTotalNum;

    /**
     * 订单来源--区分h5,pc,app,小程序,代客下单
     */
    @ApiModelProperty(value = "订单来源")
    private OrderSource orderSource;

    /**
     * 超时未支付取消订单时间
     */
    @ApiModelProperty(value = "超时未支付取消订单时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderTimeOut;
}
