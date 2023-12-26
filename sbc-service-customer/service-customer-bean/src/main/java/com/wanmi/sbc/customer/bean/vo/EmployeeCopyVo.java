package com.wanmi.sbc.customer.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.bean.enums.GenderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lm
 * @date 2022/09/15 8:33
 */
@ApiModel
@Data
public class EmployeeCopyVo  implements Serializable {

    @ApiModelProperty(value = "业务员id")
    private String employeeId;

    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String employeeName;

    /**
     * 会员电话
     */
    @ApiModelProperty(value = "会员电话")
    private String employeeMobile;

    /**
     * 权限标识
     */
    @ApiModelProperty(value = "权限标识,0:boss,1:区域负责人，2：普通员工")
    private Integer permType;

    @ApiModelProperty(value = "管理区域")
    private String manageArea;

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
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志")
    private DeleteFlag delFlag = DeleteFlag.NO;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;


    /**
     * 登录失败次数
     */
    @ApiModelProperty(value = "登录失败次数")
    private Integer loginErrorTime = 0;

    /**
     * 锁定时间
     */
    @ApiModelProperty(value = "锁定时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime loginLockTime;

    /**
     * 会有登录时间
     */
    @ApiModelProperty(value = "会员登录时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime loginTime;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    private String jobNo;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "生日")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate birthday;

    @ApiModelProperty(value = "性别，0：保密，1：男，2：女")
    private GenderType sex;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    List<CustomerDetailVO> customerDetailList;
}
