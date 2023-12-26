package com.wanmi.sbc.customer.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPassRequest {

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String newPassword;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    private String code;


}
