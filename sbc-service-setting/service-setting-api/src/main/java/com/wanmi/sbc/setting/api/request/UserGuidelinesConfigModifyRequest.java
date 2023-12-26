package com.wanmi.sbc.setting.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@ApiModel
@Data
public class UserGuidelinesConfigModifyRequest extends SettingBaseRequest {

    /**
     * 邮箱配置Id
     */
    @ApiModelProperty(value = "邮箱配置Id")
    @NotNull
    private Integer status;

}
