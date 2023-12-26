package com.wanmi.sbc.returnorder.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Autho qiaokang
 * @Date：2020-03-30 10:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ProviderTradeExportVO implements Serializable {

    private static final long serialVersionUID = 4696700071307158253L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String parentId;

    /**
     * 子订单号
     */
    @ApiModelProperty(value = "子订单号")
    private String id;

    /**
     * 下单时间
     */
    @ApiModelProperty(value = "下单时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 商家
     */
    @ApiModelProperty(value = "商家")
    private String supplierInfo;

    /**
     * 收货人
     */
    @ApiModelProperty(value = "收货人")
    private String consigneeName;

    /**
     * 收货人电话
     */
    @ApiModelProperty(value = "收货人电话")
    private String consigneePhone;

    /***
     * 详细地址(包含省市区）
     */
    @ApiModelProperty(value = "详细地址(包含省市区）")
    private String detailAddress;

    /**
     * 配送方式
     */
    @ApiModelProperty(value = "配送方式")
    private DeliverWay deliverWay;

    /**
     * 订单商品金额
     */
    @ApiModelProperty(value = "订单商品金额")
    private BigDecimal orderGoodsPrice;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String skuName;

    /**
     * 商品规格
     */
    @ApiModelProperty(value = "商品规格")
    private String specDetails;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    private String skuNo;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    private Long num;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String buyerRemark;

    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态")
    private FlowState flowState;

    /**
     * 发货状态
     */
    @ApiModelProperty(value = "发货状态")
    private DeliverStatus deliverStatus;

    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String wareName;

    /**
     * 支付单ID
     */
    @ApiModelProperty(value = "支付单ID")
    private String payOrderId;
}
