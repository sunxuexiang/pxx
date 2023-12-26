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
public class SwitchIMAccountResponse implements Serializable {

    @ApiModelProperty(value = "腾讯IM客服群组ID")
    private String imGroupId;
}
