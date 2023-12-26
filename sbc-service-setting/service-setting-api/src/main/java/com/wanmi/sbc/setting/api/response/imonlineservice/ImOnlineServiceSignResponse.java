package com.wanmi.sbc.setting.api.response.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineServiceSignResponse implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "签名")
    private String sign;
    @ApiModelProperty(value = "appid")
    private Long appid;
    @ApiModelProperty(value = "key")
    private String key;

}
