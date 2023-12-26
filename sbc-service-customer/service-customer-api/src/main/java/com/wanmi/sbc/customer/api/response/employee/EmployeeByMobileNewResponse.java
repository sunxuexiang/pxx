package com.wanmi.sbc.customer.api.response.employee;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>描述<p>
 * 获取业务员基本信息
 *
 * @author zhaowei
 * @date 2021/4/15
 */
@ApiModel
@Data
public class EmployeeByMobileNewResponse implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "业务员Id")
    private String employeeId;

    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String employeeName;

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    private String jobNo;

    /**
     * 会员电话
     */
    @ApiModelProperty(value = "会员电话")
    private String employeeMobile;

    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    private Long roleId;

    /**
     * 0 是 1否
     */
    @ApiModelProperty(value = "是否业务员(0 是 1否)")
    private Integer isEmployee;

    /**
     * 账户名
     */
    @ApiModelProperty(value = "账户名")
    private String accountName;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String accountPassword;

    /**
     * salt
     */
    @ApiModelProperty(value = "salt")
    private String employeeSaltVal;

    /**
     * 账号状态
     */
    @ApiModelProperty(value = "账号状态")
    private AccountState accountState;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private String customerId;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志", dataType = "com.wanmi.sbc.common.enums.DeleteFlag")
    private DeleteFlag delFlag = DeleteFlag.NO;


}
