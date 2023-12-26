package com.wanmi.sbc.customer.api.request.loginregister;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 员工登录
 * @author lm
 * @date 2022/09/14 17:56
 */
@ApiModel
@Data
public class EmployeeCopyResetRequest implements Serializable {

    private static final long serialVersionUID = 761117089236331874L;
    /**
     * 账户
     */
    @ApiModelProperty(value = "账户ID")
    @NotBlank
    private String employeeId;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @NotBlank
    private String password;
}
