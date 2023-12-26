package com.wanmi.sbc.customer.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户信息主表
 * Created by CHENLI on 2017/4/13.
 */
@ApiModel
@Data
public class CustomerVO implements Serializable {

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 客户等级ID
     */
    @ApiModelProperty(value = "客户等级ID")
    private Long customerLevelId;

    /**
     * 客户成长值
     */
    @ApiModelProperty(value = "客户成长值")
    private Long growthValue;

    /**
     * 可用积分
     */
    @ApiModelProperty(value = "可用积分")
    private Long pointsAvailable;

    /**
     * 已用积分
     */
    @ApiModelProperty(value = "已用积分")
    private Long pointsUsed;

    /**
     * 账户
     */
    @ApiModelProperty(value = "账户")
    private String customerAccount;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String customerPassword;

    /**
     * 支付密码
     */
    @ApiModelProperty(value = "支付密码")
    private String customerPayPassword;

    /**
     * 密码安全等级：20危险 40低、60中、80高
     */
    @ApiModelProperty(value = "密码安全等级")
    private Integer safeLevel;

    /**
     * 盐值，用于密码加密
     */
    @ApiModelProperty(value = "盐值")
    private String customerSaltVal;

    /**
     * 审核状态 0：待审核 1：已审核 2：审核未通过
     */
    @ApiModelProperty(value = "审核状态")
    private CheckState checkState;

    /**
     * 删除标志 0未删除 1已删除
     */
    @ApiModelProperty(value = "删除标志")
    private DeleteFlag delFlag;

    /**
     * 登录IP
     */
    @ApiModelProperty(value = "登录IP")
    private String loginIp;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime loginTime;

    /**
     * 密码错误次数
     */
    @ApiModelProperty(value = "密码错误次数")
    private Integer loginErrorCount = 0;

    /**
     * 创建|注册时间
     */
    @ApiModelProperty(value = "创建|注册时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updatePerson;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String deletePerson;

    /**
     * 会员的详细信息
     */
    @ApiModelProperty(value = "会员的详细信息")
    private CustomerDetailVO customerDetail;

    /**
     * 客户类型（0:平台客户,1:商家客户）
     */
    @ApiModelProperty(value = "客户类型")
    private CustomerType customerType;

    /**
     * 会员标签（0:零食店，1:便利店，2:商超，3:二批商，4:水果零售店，5:连锁系统 6:炒货店）
     */
    @ApiModelProperty(value = "会员标签")
    private CustomerTag customerTag;

    /**
     * 锁定时间
     */
    @ApiModelProperty(value = "锁定时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime loginLockTime;

    /**
     * 商家和客户的关联关系
     */
    @ApiModelProperty(value = "商家和客户的关联关系")
    private List<StoreCustomerRelaVO> storeCustomerRelaListByAll;

    /**
     * 分销渠道信息
     */
    @ApiModelProperty(value = "分销渠道信息")
    private DistributeChannel distributeChannel;

    /**
     * 支付密码错误次数
     */
    @ApiModelProperty(value = "支付密码错误次数")
    private Integer payErrorTime;

    /**
     * 支付锁定时间
     */
    @ApiModelProperty(value = "支付锁定时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime payLockTime;


    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String headImg;

    /**
     * 连续签到天数
     */
    @ApiModelProperty(value = "连续签到天数")
    private Integer signContinuousDays;

    /**
     * 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过
     */
    @ApiModelProperty(value = "企业购会员审核状态")
    private EnterpriseCheckState enterpriseCheckState;

    /**
     * 企业购会员审核原因
     */
    @ApiModelProperty(value = "企业购会员审核原因")
    private String enterpriseCheckReason;

    /**
     * 企业信息
     */
    @ApiModelProperty(value = "企业信息")
    private EnterpriseInfoVO enterpriseInfoVO;

    /**
     * 邀请码
     */
    @ApiModelProperty(value = "邀请码")
    private String inviteCode;

    /**
     * 会员注册的类型
     */
    @ApiModelProperty(value = "会员注册的类型")
    private CustomerRegisterType customerRegisterType;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String socialCreditCode;

    /**
     * 营业执照地址
     */
    @ApiModelProperty(value = "营业执照地址")
    private String businessLicenseUrl;

    /**
     * 喜吖吖 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过
     */
    @ApiModelProperty(value = "喜吖吖 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过")
    private EnterpriseCheckState enterpriseStatusXyy;

    /**
     * 父Id
     */
    @ApiModelProperty(value = "父Id")
    private String parentCustomerId;

    /**
     * 会员的erpId
     */
    @ApiModelProperty(value = "会员的erpId")
    private String customerErpId;

    /**
     * erp会员同步的标志
     */
    @ApiModelProperty(value = "erp会员同步的标志")
    private DefaultFlag erpAsyncFlag;

    /**
     * 大客户标识 0否，1是
     */
    @ApiModelProperty(value = "大客户标识 0否，1是")
    private DefaultFlag vipFlag;


}
