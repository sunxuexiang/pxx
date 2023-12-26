package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerForErpRequest extends CustomerBaseRequest {
    private static final long serialVersionUID = -1709264705378683734L;

    /**
     * 客户的名称
     */
    @ApiModelProperty(value = "客户的名称")
    private String customerAccount;


    /**
     * 会员的审核状态
     */
    @ApiModelProperty(value = "会员的审核状态")
    private EnterpriseCheckState enterpriseStatusXyy;


    /**
     * 会员的注册类型
     */
    @ApiModelProperty(value = "会员的注册类型")
    private CustomerRegisterType customerRegisterType;

    /**
     * erp的会员Id
     */
    @ApiModelProperty(value = "erp的会员Id")
    private String customerErpId;


}
