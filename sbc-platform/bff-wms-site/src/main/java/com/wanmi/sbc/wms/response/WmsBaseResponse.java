package com.wanmi.sbc.wms.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author baijianzhong
 * @ClassName WmsBaseRequest
 * @Date 2020-07-15 17:36
 * @Description TODO
 **/
@Data
@Api
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WmsBaseResponse {

    /**
     * 返回码
     */
    @ApiModelProperty(name = "返回码")
    private String returnCode;

    /**
     * 错误原因
     */
    @ApiModelProperty(name = "错误原因")
    private String returnDesc;

    /**
     * 返回的标识
     */
    @ApiModelProperty(name = "返回的标识")
    private Integer returnFlag;

}
