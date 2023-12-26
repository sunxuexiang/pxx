package com.wanmi.sbc.wms.request;

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
 * @ClassName: order
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 18:17
 * @Version: 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WMSChargeBackRequest implements Serializable {
    private static final long serialVersionUID = 3484763312890154973L;
    @ApiModelProperty(value = "仓库ID")
    private String warehouseId;
    @ApiModelProperty(value = "货主ID")
    private String customerId;
    @ApiModelProperty(value = "订单类型")
    private String asnType;
    @ApiModelProperty(value = "上游来源订单号")
    private String docNo;
    @ApiModelProperty(value = "ASN参考信息A")
    private String asnReferenceA;
    @ApiModelProperty(value = "ASN参考信息B")
    private String asnReferenceB;
    @ApiModelProperty(value = "ASN参考信息C")
    private String asnReferenceC;
    @ApiModelProperty(value = "ASN参考信息D")
    private String asnReferenceD;

    @ApiModelProperty(value = "退单号")
    private String orderNo;

    @ApiModelProperty(value = "ASN创建时间")
    private LocalDateTime asnCreationTime;

    @ApiModelProperty(value = "预期到货时间")
    private LocalDateTime expectedArriveTime1;
    @ApiModelProperty(value = "预期到货时间")
    private LocalDateTime expectedArriveTime2;

    @ApiModelProperty(value = "供应商ID")
    private String supplierId;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    @ApiModelProperty(value = "承运人ID")
    private String carrierId;
    @ApiModelProperty(value = "承运人名称")
    private String carrierName;
    @ApiModelProperty(value = "目的国")
    private String countryOfDestination;
    @ApiModelProperty(value = "原产国")
    private String countryOfOrigin;
    @ApiModelProperty(value = "业务担当")
    private String followUp;

    @ApiModelProperty(value = "EDI相关信息")
    private String hedi01;
    @ApiModelProperty(value = "EDI相关信息")
    private String hedi02;
    @ApiModelProperty(value = "EDI相关信息")
    private String hedi03;
    @ApiModelProperty(value = "EDI相关信息")
    private String hedi04;
    @ApiModelProperty(value = "EDI相关信息")
    private String hedi05;
    @ApiModelProperty(value = "EDI相关信息")
    private String hedi06;
    @ApiModelProperty(value = "EDI相关信息")
    private String hedi07;
    @ApiModelProperty(value = "EDI相关信息")
    private String hedi08;
    @ApiModelProperty(value = "EDI相关信息")
    private BigDecimal hedi09;
    @ApiModelProperty(value = "EDI相关信息")
    private BigDecimal hedi10;

    @ApiModelProperty(value = "卸货地")
    private String placeOfDischarge;
    @ApiModelProperty(value = "装货地")
    private String placeOfLoading;
    @ApiModelProperty(value = "交货地")
    private String placeOfDelivery;
    @ApiModelProperty(value = "优先级")
    private String priority;
    /**
     * 线上线下 0：在线支付   1：线下支付
     */
    @ApiModelProperty(value = "自定义1")
    private String userDefine1;
    /**
     * 付款标识 0：未支付    2：已支付
     */
    @ApiModelProperty(value = "自定义2")
    private String userDefine2;
    /**
     * 先款后货，不限   0：不限   1：先款后货
     */
    @ApiModelProperty(value = "自定义3")
    private String userDefine3;
    /**
     * 未支付订单自动取消时长
     */
    @ApiModelProperty(value = "自定义4")
    private String userDefine4;
    @ApiModelProperty(value = "自定义5")
    private String userDefine5;
    @ApiModelProperty(value = "自定义6")
    private String userDefine6;
    @ApiModelProperty(value = "自定义7")
    private String userDefine7;
    @ApiModelProperty(value = "自定义8")
    private String userDefine8;

    @ApiModelProperty(value = "自定义9")
    private BigDecimal userDefine9;
    @ApiModelProperty(value = "退款总金额")
    private BigDecimal userDefine10;

    @ApiModelProperty(value = "订单详情")
    private List<WMSChargeBackDetailsRequest> details;


}
