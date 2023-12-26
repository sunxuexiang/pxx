package com.wanmi.sbc.setting.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName: SobotContext
 * @Description: TODO
 * @Date: 2020/11/20 10:42
 * @Version: 1.0
 */
@ApiModel
@Data
public class SobotContextRequest implements Serializable {

    private static final long serialVersionUID = 551916591487282491L;
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
    @ApiModelProperty(value = "网页端跳转页面")
    private String h5Url;
}
