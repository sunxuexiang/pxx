package com.wanmi.sbc.setting.api.response.onlineservice;

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
public class ImSignResponse implements Serializable {

    /**
     * 客服IM账号
     */
    private String imAccount;

    /**
     * im登录签名
     */
    private String imSign;
}
