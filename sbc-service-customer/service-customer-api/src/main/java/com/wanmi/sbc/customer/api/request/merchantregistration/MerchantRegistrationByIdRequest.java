package com.wanmi.sbc.customer.api.request.merchantregistration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商家入驻申请新增信息参数
 * @author hudong
 * @date 2023-06-17 09:03
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantRegistrationByIdRequest implements Serializable {


    private static final long serialVersionUID = -2426273139099738867L;
    /**
     * 申请ID
     */
    @ApiModelProperty(value = "申请ID")
    @NotNull
    private Long applicationId;


}
