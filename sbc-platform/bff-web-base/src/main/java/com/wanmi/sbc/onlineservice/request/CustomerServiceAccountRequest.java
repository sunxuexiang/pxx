package com.wanmi.sbc.onlineservice.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceAccountRequest {

    @ApiModelProperty(value = "手机号码")
    private String mobileNumber;

}
