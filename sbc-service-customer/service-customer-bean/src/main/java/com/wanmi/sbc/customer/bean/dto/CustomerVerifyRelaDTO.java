package com.wanmi.sbc.customer.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc_h_tian
 * @description: 绑定子账户校验 回参
 * @author: Mr.Tian
 * @create: 2020-06-05 13:49
 **/
@ApiModel
@Data
public class CustomerVerifyRelaDTO implements Serializable {

    private static final long serialVersionUID = 5079626444171351912L;
    /**
     * 返回状态  0 为企业用户  1为已被绑定的子账户
     */
    @ApiModelProperty(value = "返回状态")
    private Integer status;
    /**
     * 被绑定的手机号码
     */
    @ApiModelProperty(value = "返回状态")
    private String customerAccount;
}
