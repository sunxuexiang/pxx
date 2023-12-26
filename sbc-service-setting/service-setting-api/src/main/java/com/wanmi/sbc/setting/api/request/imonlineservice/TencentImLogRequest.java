package com.wanmi.sbc.setting.api.request.imonlineservice;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TencentImLogRequest implements Serializable {

    private Long logId;

    /**
     * 用户手机号
     */
    private String account;

    /**
     * 签名
     */
    private String sign;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误描述
     */
    private String errorMessage;
}
