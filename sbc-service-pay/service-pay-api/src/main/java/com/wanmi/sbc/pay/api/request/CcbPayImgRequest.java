package com.wanmi.sbc.pay.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/8/31 15:17
 */
@ApiModel
@Data
public class CcbPayImgRequest {

    @ApiModelProperty("支付单号")
    @NotBlank
    private String payOrderNo;

    @ApiModelProperty("支付凭证")
    @NotBlank
    private String ccbPayImg;
}
