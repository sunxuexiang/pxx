package com.wanmi.sbc.customer.request;

import com.wanmi.sbc.customer.validGroups.NotCustomerAccount;
import com.wanmi.sbc.customer.validGroups.NotPassword;
import com.wanmi.sbc.customer.validGroups.NotVerify;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordByForgotRequest {


    /**
     * 用户编号
     */
    @ApiModelProperty(value = "用户编号")
    private String customerId;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    @NotBlank(groups = {NotCustomerAccount.class})
    private String customerAccount;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @NotBlank(groups = {NotPassword.class})
    private String customerPassword;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    @NotBlank(groups = {NotVerify.class})
    private String verifyCode;

    /**
     * 是否是忘记密码 true：忘记密码 | false：
     */
    @ApiModelProperty(value = "是否是忘记密码")
    private Boolean isForgetPassword;
}
