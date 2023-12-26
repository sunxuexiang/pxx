package com.wanmi.sbc.setting.api.request.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName: ImConfigModifyRequest
 * @Description: 腾讯IM请求参数
 * @Date: 2023/06/13 10:25
 * @Version: 1.0
 */
@ApiModel
@Data
public class ImConfigModifyRequest implements Serializable {
    private static final long serialVersionUID = -5642867928644214650L;

    @ApiModelProperty(value = "appId")
    private String appId;
    @ApiModelProperty(value = "appKey")
    private String appKey;
    @ApiModelProperty(value = "app开关")
    @NotNull
    private Integer effectiveApp;
    @ApiModelProperty(value = "h5开关")
    @NotNull
    private Integer effectiveH5;
    @ApiModelProperty(value = "小程序开关")
    @NotNull
    private Integer effectiveMiniProgram;
    @ApiModelProperty(value = "pc开关")
    @NotNull
    private Integer effectivePc;
    @ApiModelProperty(value = "启动开关")
    @NotNull
    private Integer enableFlag;


}
