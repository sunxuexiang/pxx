package com.wanmi.sbc.setting.api.response.onlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * <p>客服服务信息</p>
 * @author lq
 * @date 2023-9-05 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceInfoResponse implements Serializable {

    /**
     * 客服昵称
     */
    @ApiModelProperty(value = "客服昵称")
    private String customerServiceName;

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "客服账号")
    private String customerServiceAccount;

    @ApiModelProperty(value = "状态：0、在线；1、离线")
    private Integer status;

    @ApiModelProperty(value = "登录时间")
    private String loginTime;

    @ApiModelProperty(value = "接待数量")
    private BigInteger acceptQuantity = BigInteger.ZERO;
}
