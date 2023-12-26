package com.wanmi.sbc.customer.api.request.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreQueryByCompanyInfoIdRequest {

    private static final long serialVersionUID = -5191413261379833651L;

    /**
     * 商家id
     */
    @ApiModelProperty(value = "商家id")
    @NotNull
    private Long companyInfoId;
}
