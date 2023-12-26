package com.wanmi.sbc.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求基类
 * Created by aqlu on 15/11/30.
 */
@ApiModel
@Data
@Slf4j
public class BaseRequest implements Serializable {

    /**
     * 登录用户Id
     */
    @ApiModelProperty(value = "登录用户Id")
    private String userId;

    /**
     * 来源渠道
     */
    @ApiModelProperty(value = "来源渠道")
    private String sourceChannel;

    /**
     * 统一参数校验入口
     */
    public void checkParam(){
        log.info("统一参数校验入口");
    }
}
