package com.wanmi.sbc.fadadaInterface.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class AddressResponse {
//    System.out.println(data.get("province_code")+","+","+data.get("city_code")+data.get("county_code"));
    @ApiModelProperty(value = "省ID")
    private Long provinceCode;
    @ApiModelProperty(value = "市ID")
    private Long cityCode;
    @ApiModelProperty(value = "区ID")
    private Long countyCode;
    @ApiModelProperty(value = "详情地址")
    private String detailAddress;
}
