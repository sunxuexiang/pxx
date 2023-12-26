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
public class EcnyQueryRequest extends PayBaseRequest {
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

    @ApiModelProperty(value = "加密串")
    @NotBlank
    private String ccbParam;                //加密串

    @ApiModelProperty(value = "交易码")
    @NotBlank
    private String TXCODE;                //固定值PDPCX0

    @ApiModelProperty(value = "订单编号")
    @NotBlank
    private String Ordr_ID;        //订单编号
    @ApiModelProperty(value = "状态代码")
    private String SYS_TX_STATUS;    //备注1

     
}
