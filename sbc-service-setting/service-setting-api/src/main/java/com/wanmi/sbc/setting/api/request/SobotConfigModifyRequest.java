package com.wanmi.sbc.setting.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName: SobotConfigModifyRequest
 * @Description: TODO
 * @Date: 2020/11/20 10:25
 * @Version: 1.0
 */
@ApiModel
@Data
public class SobotConfigModifyRequest implements Serializable {
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
    @ApiModelProperty(value = "网页端跳转页面")
    private String h5Url;

}
