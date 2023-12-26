package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 企业会员校验信用代码
 */
@ApiModel
@Data
public class CustomerSocialCodeValidateRequest extends CustomerBaseRequest {
    private static final long serialVersionUID = -3854269857592932191L;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    private String customerId;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String socialCreditCode;

}
