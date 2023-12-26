package com.wanmi.sbc.customer.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lm
 * @date 2022/09/15 9:15
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmpLoginResponse  implements Serializable {

    /**
     * jwt验证token
     */
    @ApiModelProperty(value = "jwt验证token")
    private String token;

    /**
     * 账号名称
     */
    @ApiModelProperty(value = "账号名称")
    private String accountName;


    @ApiModelProperty(value = "员工ID")
    private String employeeId;


    @ApiModelProperty(value = "员工工号")
    private String jobNo;

    @ApiModelProperty(value = "权限标识")
    private Integer permType;

    @ApiModelProperty(value = "员工电话")
    private String employeeMobile;

    @ApiModelProperty(value = "管理区域")
    private String manageArea;

}
