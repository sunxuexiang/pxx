package com.wanmi.sbc.order.bean.vo;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.ShipperType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 发货单
 *
 * @author wumeng[OF2627]
 * company qianmi.com
 * Date 2017-04-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeDeliverVO implements Serializable {

    private static final long serialVersionUID = -7897675979340409855L;

    /**
     * 发货单属于的订单号
     */
    @ApiModelProperty(value = "发货单属于的订单号")
    private String tradeId;

    /**
     * 订单的所属商家/供应商
     */
    @ApiModelProperty(value = "订单的所属商家/供应商")
    private String providerName;

    /**
     * 发货单号
     */
    @ApiModelProperty(value = "发货单号")
    private String deliverId;
    /**
     * 物流信息
     */
    @ApiModelProperty(value = "物流信息")
    private LogisticsVO logistics;

    /**
     * 收货人信息
     */
    @ApiModelProperty(value = "收货人信息")
    private ConsigneeVO consignee;

    /**
     * 发货清单
     */
    @ApiModelProperty(value = "发货清单")
    private List<ShippingItemVO> shippingItems = new ArrayList<>();

    /**
     * 赠品信息
     */
    @ApiModelProperty(value = "赠品信息")
    private List<ShippingItemVO> giftItemList = new ArrayList<>();

    /**
     * 发货时间
     */
    @ApiModelProperty(value = "发货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime deliverTime;

    /**
     * 发货状态
     */
    @ApiModelProperty(value = "发货状态")
    private DeliverStatus status;

    /**
     * 子订单 发货单号
     */
    @ApiModelProperty(value = "子订单 发货单号")
    private String sunDeliverId;

    /**
     * 所属 商家/供应商
     */
    @ApiModelProperty(value = "所属 商家/供应商")
    private ShipperType shipperType;

    /**
     *物流公司信息(如果配送方式是物流则存在)
     */
    @ApiModelProperty(value = "物流公司信息(如果配送方式是物流则存在)")
    private LogisticsInfoVO logisticsCompanyInfo;

    /**
     * 是否全部发货0:否，1：是
     */
    private Integer deliverAll ;

    /***
     * @desc  发货选择的配送方式
     * @author shiy  2023/6/29 17:52
    */
    private Integer deliverWay;
}
