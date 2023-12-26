package com.wanmi.sbc.wms.api.request.wms;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName: SalesOrder
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/8 14:02
 * @Version: 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSPushOrderRequest extends WmsBaseRequest {

    private static final long serialVersionUID = 98953713304398998L;
    @ApiModelProperty(value = "仓库ID")
    @NotBlank
    private String warehouseId;
    @ApiModelProperty(value = "货主ID")
    @NotBlank
    private String customerId;
    @ApiModelProperty(value = "订单类型 ")
    @NotBlank
    private String orderType;
    @ApiModelProperty(value = "上游来源订单号")
    @NotBlank
    @JSONField(name = "docNo")
    private String docNo;

    @ApiModelProperty(value = "订单创建时间")
    private String orderTime;

    @ApiModelProperty(value = "快递单号")
    private String deliveryNo;

    @ApiModelProperty(value = "省市")
    private String consigneeProvince;

    @ApiModelProperty(value = "区")
    private String consigneeCity;

    @ApiModelProperty(value = "收货人ID")
    @NotBlank
    private String consigneeId;

    @ApiModelProperty(value = "收货人名称")
    @NotBlank
    private String consigneeName;

    @ApiModelProperty(value = "收货人地址1")
    private String consigneeAddress1;

    @ApiModelProperty(value = "客户应付总价-配送费-包装费")
    private String consigneeAddress2;

    /**
     * 传订单号
     */
    @ApiModelProperty(value = "订单号")
    private String soReferenceA;

    /**
     * 参考编号5 —— 固定值
     */
    @ApiModelProperty(value = "参考编号5 —— 固定值")
    private String soReferenceD;

    /**
     * 固定值
     */
    @ApiModelProperty(value = "固定值")
    private String addWho;

    /**
     * 线上线下 0：在线支付   1：线下支付
     */
    @ApiModelProperty(value = "线上线下 0：在线支付   1：线下支付")
    private String userDefine1;
    /**
     * 付款标识 0：未支付    2：已支付
     */
    @ApiModelProperty(value = "付款标识 0：未支付    2：已支付")
    private String userDefine2;
    /**
     * 先款后货，不限   0：不限   1：先款后货
     */
    @ApiModelProperty(value = "先款后货，不限   0：不限   1：先款后货")
    private String userDefine3;

    /**
     * 预约发货时间
     */
    @ApiModelProperty(value = "收货站点")//原备注名预约发货时间
    private String userDefine4;

    /**
     * 物流公司地址
     */
    @ApiModelProperty(value = "物流公司地址")
    private String userDefine5;

    /**
     * 业务员
     */
    @ApiModelProperty(value = "业务员")
    private String userDefine6;

    /**
     * 收货人电话
     */
    @ApiModelProperty(value = "业务员")
    private String consigneeTel1;

    /**
     * 收货人联系人
     */
    @ApiModelProperty(value = "收货人联系人")
    private String consigneeTel2;

    /**
     * 预约发货时间
     */
    private String expectedShipmentTime1;

    /**
     * 订单释放状态
     */
    private String priority;

    /**
     * 订单来源 001代表电商1 002代表电商2
     */
    private String soReferenceB;

    /**
     * 订单备注
     */
    private String notes;
    /**
     * 物流公司代码
     */
    @ApiModelProperty(value = "物流公司代码")
    private String carrierId;
    /**
     * 物流公司名称
     */
    @ApiModelProperty(value = "物流公司名称")
    private String carrierName;
    /**
     * 物流公司电话
     */
    @ApiModelProperty(value = "物流公司电话")
    private String carrierFax;

    @ApiModelProperty(value = "站点")
    private String stop;

    @ApiModelProperty(value = "订单配送类型")
    //自提（ZTCK），快递（KDCK）， 物流,配送到家（XSCK）
    private String carrierMail;

    @ApiModelProperty(value = "详情")
    private List<WMSPushOrderDetailsRequest>    details;

    @ApiModelProperty(value = "是否是乡村件 1是 其他否")
    private String consigneeAddress3;

    @ApiModelProperty(value = "父订单")
    private String hedi01;

    @ApiModelProperty(value = "合并规格")
    private String hedi02;


}
