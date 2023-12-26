package com.wanmi.sbc.customer.api.request.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@ApiModel
@Data
@Component
public class BusinessParamsRequest {
    private static final long serialVersionUID = -1469274484762938357L;

    @ApiModelProperty(value = "营业执照URL")
    private String businessLicence;
}

