package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import com.wanmi.sbc.customer.bean.enums.CustomerTag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业会员修改请求
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEnterpriseRequest extends CustomerBaseRequest {
    private static final long serialVersionUID = -3854269857592932191L;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    private String customerId;

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
     * 会员标签（0:零食店，1:便利店，2:商超，3:二批商，4:水果零售店，5:连锁系统，6:炒货店）
     */
    @ApiModelProperty(value = "会员标签（0:零食店，1:便利店，2:商超，3:二批商，4:水果零售店，5:连锁系统，6:炒货店）")
    private CustomerTag customerTag;


}
