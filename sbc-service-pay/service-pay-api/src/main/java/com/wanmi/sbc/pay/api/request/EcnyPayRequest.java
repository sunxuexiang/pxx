package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: dkq
 * @Date: 2021/06/13
 * @Description:
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class EcnyPayRequest extends PayBaseRequest {
    private static final long serialVersionUID = 6447066155217627362L;
    
    
    /**---必填项---**/
    @ApiModelProperty(value = "商户代码")
    @NotBlank
    private String MERCHANTID;               //商户代码

    @ApiModelProperty(value = "商户柜台代码")
    @NotBlank
    private String POSID;              //商户柜台代码

    @ApiModelProperty(value = "分行代码")
    @NotBlank
    private String BRANCHID;           //分行代码

    @ApiModelProperty(value = "定单号")
    @NotBlank
    private String ORDERID;                //定单号

    @ApiModelProperty(value = "付款金额")
    @NotBlank
    private String PAYMENT;                //付款金额

    @ApiModelProperty(value = "币种")
    @NotBlank
    private String CURCODE;        //币种

    @ApiModelProperty(value = "交易码")
    @NotBlank
    private String TXCODE;           //交易码

    @ApiModelProperty(value = "备注1")
    private String REMARK1;    //备注1

    @ApiModelProperty(value = "备注2")
    private String REMARK2;          //备注2

    @ApiModelProperty(value = "返回类型")
    private String RETURNTYPE;          //返回类型

    @ApiModelProperty(value = "订单超时时间")
    private String TIMEOUT;          //订单超时时间

    /**---非必填项---**/
    @ApiModelProperty(value = "商户结算账号")
    private String CdtrWltId;         //商户结算账号

    @ApiModelProperty(value = "二级商户代码")
    private String SUB_MERCHANTID;           //二级商户代码

    @ApiModelProperty(value = "加密串")
    @NotBlank
    private String MAC;           //加密串
    
    @ApiModelProperty(value = "公钥")
    @NotBlank
    private String PUB;           //加密串
    
    @ApiModelProperty(value = "商户URL")
    private String Mrch_url;           //商户URL
    
    @ApiModelProperty(value = "版本号")
    private String CCB_IBSVersion;           //版本号
   
}
