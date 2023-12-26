package com.wanmi.sbc.customer.api.response.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FadadaContractStatusResponese extends FadadaParamsResponese{
    private static final long serialVersionUID = -1469274484762938357L;

    @ApiModelProperty(value = "自定义印章链接")
    private String contractStatus;

    @ApiModelProperty(value = "印章ID")
    private String contractStatusDesc;

}

