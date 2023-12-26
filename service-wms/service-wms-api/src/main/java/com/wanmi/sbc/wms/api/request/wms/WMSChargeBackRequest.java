package com.wanmi.sbc.wms.api.request.wms;

import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: order
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 18:17
 * @Version: 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSChargeBackRequest extends WmsBaseRequest {

    private static final long serialVersionUID = -3397813833101159123L;

    @ApiModelProperty(value = "仓库ID")
    private String warehouseId;

    @ApiModelProperty(value = "货主ID")
    private String customerId;

    @ApiModelProperty(value = "订单类型")
    private String asnType;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "上游来源订单号")
    private String docNo;

    /**
     * 退单号
     */
    @ApiModelProperty(value = "ASN参考信息A")
    private String asnReferenceA;


    /**
     * 订单来源 001代表电商1 002代表电扇2
     */
    @ApiModelProperty(value = "ASN参考信息B")
    private String asnReferenceB;

    @ApiModelProperty(value = "固定值=>电商订单")
    private String asnReferenceC;

    @ApiModelProperty(value = "退款总金额")
    private BigDecimal userDefine9;

    @ApiModelProperty(value = "作业下发时间")
    private String asnCreationTime;

    @ApiModelProperty(value = "参考编号5 —— 固定值")
    private String soReferenceD;

    @ApiModelProperty(value = "供应商ID")
    private String supplierId;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 业务员Id
     */
    @ApiModelProperty(value = "自定义1")
    private String userDefine1;

    /**
     * 制单人Id
     */
    @ApiModelProperty(value = "自定义2")
    private String userDefine2;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "自定义3")
    private String userDefine3;

    @ApiModelProperty(value = "订单详情")
    private List<WMSChargeBackDetailsRequest> details;

}
