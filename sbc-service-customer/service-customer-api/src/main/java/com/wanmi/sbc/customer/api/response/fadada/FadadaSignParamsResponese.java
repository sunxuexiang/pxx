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
public class FadadaSignParamsResponese extends FadadaParamsResponese{
    private static final long serialVersionUID = -1469274484762938357L;

    @ApiModelProperty(value = "手动签署URL链接")
    private String extSignUrl;

}

