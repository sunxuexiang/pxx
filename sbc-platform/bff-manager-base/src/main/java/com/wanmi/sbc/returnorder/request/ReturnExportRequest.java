package com.wanmi.sbc.returnorder.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 导出退单如餐
 * Created by jinwei on 6/5/2017.
 */
@ApiModel
@Data
public class ReturnExportRequest extends ReturnQueryRequest {

    // jwt token
    @ApiModelProperty(value = "jwt token")
    private String token;

}
