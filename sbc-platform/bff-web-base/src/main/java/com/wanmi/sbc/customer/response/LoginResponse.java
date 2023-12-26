package com.wanmi.sbc.customer.response;

import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.EnterpriseInfoVO;
import com.wanmi.sbc.marketing.api.response.coupon.GetRegisterOrStoreCouponResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录返回
 * Created by daiyitian on 15/11/28.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse implements Serializable{

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

    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String customerId;

    /**
     * 审核状态 0：待审核 1：已审核 2：审核未通过
     */
    @ApiModelProperty(value = "审核状态", dataType = "com.wanmi.sbc.customer.bean.enums.CheckState")
    private Integer checkState;

    /**
     * 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过
     */
    @ApiModelProperty(value = "企业购会员审核状态")
    private EnterpriseCheckState enterpriseCheckState;

    /**
     * 喜吖吖 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过
     */
    @ApiModelProperty(value = "喜吖吖 企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过")
    private EnterpriseCheckState enterpriseStatusXyy;

    /**
     * 企业购会员审核原因
     */
    @ApiModelProperty(value = "企业购会员审核原因")
    private String enterpriseCheckReason;

    /**
     * 企业购会员公司详情信息
     */
    @ApiModelProperty(value = "企业购会员公司详情信息")
    private EnterpriseInfoVO enterpriseInfoVO;

    /**
     * 客户明细
     */
    @ApiModelProperty(value = "客户明细信息")
    private CustomerDetailVO customerDetail;

    /**
     * 是否直接可以登录 0 否 1 是
     */
    @ApiModelProperty(value = "是否直接可以登录")
    private Boolean isLoginFlag;

    /**
     * 注册赠券信息
     */
    @ApiModelProperty(value = "注册赠券信息")
    private GetRegisterOrStoreCouponResponse couponResponse = new GetRegisterOrStoreCouponResponse();

    /**
     * 被邀请会员邀请码
     */
    @ApiModelProperty(value = "被邀请会员邀请码")
    private String inviteCode;

    /**
     * 会员绑定公众的openId
     */
    private String openId;
}
