package com.wanmi.sbc.system.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: SobotResponse
 * @Description: TODO
 * @Date: 2020/11/20 15:23
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SobotResponse {

    @ApiModelProperty(value = "app开关")
    private Integer effectiveApp;
    @ApiModelProperty(value = "h5开关")
    private Integer effectiveH5;
    @ApiModelProperty(value = "小程序开关")
    private Integer effectiveMiniProgram;
    @ApiModelProperty(value = "pc开关")
    private Integer effectivePc;
    @ApiModelProperty(value = "总启动开关")
    private Integer status;
    @ApiModelProperty(value = "网页端跳转页面")
    private String h5Url;
}
