package com.wanmi.sbc.order.api.response.refund;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.order.bean.vo.ManualRefundImgVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 退款单
 * Created by zhangjin on 2017/4/21.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RefundOrderResponse implements Serializable {

    private static final long serialVersionUID = -1185039413513891011L;
    /**
     * 主键
     */
    @ApiModelProperty(value = "退款单id")
    private String refundId;

    /**
     * 退单编号
     */
    @ApiModelProperty(value = "退单编号")
    private String returnOrderCode;

    /**
     * 退款流水号
     */
    @ApiModelProperty(value = "退款流水号")
    private String refundBillCode;

    /**
     * 退单下单时间
     */
    @ApiModelProperty(value = "退单下单时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    /**
     * 应退金额
     */
    @ApiModelProperty(value = "应退金额")
    private BigDecimal returnPrice;

    /**
     * 实退金额
     */
    @ApiModelProperty(value = "实退金额")
    private BigDecimal actualReturnPrice;

    /**
     * 应退积分
     */
    @ApiModelProperty(value = "应退积分")
    private Long returnPoints;

    /**
     * 实退积分
     */
    @ApiModelProperty(value = "实退积分")
    private Long actualReturnPoints;

    /**
     * 退款账户
     */
    @ApiModelProperty(value = "退款账户")
    private Long returnAccount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String comment;

    /**
     * 退款状态
     */
    @ApiModelProperty(value = "退款单状态")
    private RefundStatus refundStatus;

    /**
     * 退款方式
     */
    @ApiModelProperty(value = "支付类型" )
    private PayType payType;

    /**
     * 退款账户
     */
    @ApiModelProperty(value = "退款账户")
    private String returnAccountName;

    /**
     * 客户账号
     */
    @ApiModelProperty(value = "客户账号")
    private String customerAccountName;

    /**
     * 线下平台账户
     */
    @ApiModelProperty(value = "线下平台账户")
    private Long offlineAccountId;

    /**
     * 退款时间
     */
    @ApiModelProperty(value = "退款时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime refundBillTime;

    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因")
    private String refuseReason;

    /**
     * 收款在线渠道
     */
    @ApiModelProperty(value = "收款在线渠道")
    private String payChannel;

    @ApiModelProperty(value = "收款在线渠道id")
    private Long payChannelId;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;

    /**
     * 活动类型 0:销售订单  1:提货 2:为囤货
     */
    private String activityType;

    @ApiModelProperty(value = "凭证地址")
    private List<ManualRefundImgVO> manualRefundImgVOList;
}
