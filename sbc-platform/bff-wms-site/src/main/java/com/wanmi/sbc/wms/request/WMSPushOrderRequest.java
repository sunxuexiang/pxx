package com.wanmi.sbc.wms.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: SalesOrder
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/8 14:02
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSPushOrderRequest implements Serializable {
    private static final long serialVersionUID = -8984816685540056039L;
    @ApiModelProperty(value = "仓库ID")
    
    private String warehouseId;
    @ApiModelProperty(value = "货主ID")
    
    private String customerId;
    @ApiModelProperty(value = "订单类型")
    private String orderType;
    @ApiModelProperty(value = "上游来源订单号")
    private String docNo;
    @ApiModelProperty(value = "参考编号A")
    private String soReferenceA;
    @ApiModelProperty(value = "参考编号B")
    private String soReferenceB;
    @ApiModelProperty(value = "参考编号C")
    private String soReferenceC;
    @ApiModelProperty(value = "参考编号D")
    private String soReferenceD;
    @ApiModelProperty(value = "订单创建时间")
    private LocalDateTime orderTime;
    @ApiModelProperty(value = "预期发货时间")
    private LocalDateTime expectedShipmentTime1;
    @ApiModelProperty(value = "要求交货时间")
    private LocalDateTime requiredDeliveryTime;
    @ApiModelProperty(value = "快递单号")
    private String deliveryNo;
    @ApiModelProperty(value = "收货人ID")
    
    private String consigneeId;
    @ApiModelProperty(value = "收货人名称")
    
    private String consigfneeName;
    @ApiModelProperty(value = "收货人地址1")
    private String consigneeAddress1;
    @ApiModelProperty(value = "收货人地址2")
    private String consigneeAddress2;
    @ApiModelProperty(value = "收货人地址3")
    private String consigneeAddress3;
    @ApiModelProperty(value = "收货人国家代码")
    private String consigneeCountry;
    @ApiModelProperty(value = "收货人省")
    private String consigneeProvince;
    @ApiModelProperty(value = "收货人市")
    private String consigneeCity;
    @ApiModelProperty(value = "收货人电子邮件")
    private String consigneeMail;
    @ApiModelProperty(value = "收货人电话1")
    private String consigneeTel1;
    @ApiModelProperty(value = "收货人电话2")
    private String consigneeTel2;
    @ApiModelProperty(value = "收货人邮编")
    private String consigneeZip;
    @ApiModelProperty(value = "承运商ID")
    private String carrierId;
    @ApiModelProperty(value = "承运商名称")
    private String carrierName;
    @ApiModelProperty(value = "承运商传真")
    private String carrierFax;
    @ApiModelProperty(value = "承运商电子邮件")
    private String carrierMail;
    @ApiModelProperty(value = "面单号段")
    private String channel;
    @ApiModelProperty(value = "备注（顾客留言")
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
    private Double hedi09;
    @ApiModelProperty(value = "EDI相关信息")
    private Double hedi10;

    @ApiModelProperty(value = "发票打印")
    private String invoicePrintFlag;
    @ApiModelProperty(value = "路径")
    private String route;
    @ApiModelProperty(value = "站点")
    private String stop;
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
    @ApiModelProperty(value = "自定义4")
    private String userDefine4;
    @ApiModelProperty(value = "自定义5")
    private String userDefine5;
    @ApiModelProperty(value = "自定义6")
    private String userDefine6;
    /**
     *是否全部发货:0否1是
     */
    @ApiModelProperty(value = "是否全部发货:0否1是")
    private String userDefine10;
    @ApiModelProperty(value = "备注")
    private String notes;
    @ApiModelProperty(value = "详情")
    private List<WMSPushOrderDetailsRequest> details;



}
