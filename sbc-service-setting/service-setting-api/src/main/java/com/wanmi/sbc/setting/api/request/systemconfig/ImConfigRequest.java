package com.wanmi.sbc.setting.api.request.systemconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-07 16:03
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImConfigRequest {
    /**
     * 账号
     */
    @ApiModelProperty(value="账号")
    @NotNull
    private String account;

    /**
     * appid
     */
    @ApiModelProperty(value="appid")
    private Long appid;

    /**
     * app key
     */
    @ApiModelProperty(value="key")
    private String key;
}
