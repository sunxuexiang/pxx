package com.wanmi.sbc.advertising.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class AdvertisingBaseResquest {
    @ApiModelProperty(value = "1 批发 2零售 3 散批")
    private Integer type;

    private Long storeId;
}
