package com.wanmi.sbc.setting.api.request.sysswitch;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel
@Data
public class SwitchModifyRequest extends SettingBaseRequest {
    private static final long serialVersionUID = 3973672601405737318L;
    /**
     * 开关id
     */
    @ApiModelProperty(value = "开关id")
    private String id;

    private String switchCode;

    /**
     * 开关状态 0：关闭 1：开启
     */
    @ApiModelProperty(value = "开关状态", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer status;
}
