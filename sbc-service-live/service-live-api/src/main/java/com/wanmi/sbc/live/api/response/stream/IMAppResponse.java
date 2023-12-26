package com.wanmi.sbc.live.api.response.stream;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class IMAppResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * im前端获取签名
     */
    @ApiModelProperty(value = "im前端获取签名")
    private String userSig;
}
