package com.wanmi.sbc.customer.api.request.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BelugaInfoContractFindRequest {

    @ApiModelProperty(name = "营业执照代码")
    private String creditCode;
}
