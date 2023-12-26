package com.wanmi.sbc.setting.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户财务邮箱response
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGuidelinesConfigQueryResponse {
    /**
     * 邮箱配置Id
     */
    @ApiModelProperty(value = "邮箱配置Id")
    private Integer status;
}
