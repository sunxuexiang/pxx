package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-20 10:45
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class DelMsgVO {
    @ApiModelProperty(value = "请求包的处理结果，OK 表示处理成功，FAIL 表示失败")
    private String actionStatus;
    @ApiModelProperty(value = "错误码，0表示成功，非0表示失败，")
    private Integer errorCode;
    @ApiModelProperty(value = "明细信息")
    private String errorInfo;
    @ApiModelProperty(value = "明细信息")
    private String errorDisplay;


}
