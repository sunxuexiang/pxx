package com.wanmi.sbc.returnorder.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class TradeCreateDTO extends TradeRemedyDTO {

    private static final long serialVersionUID = 3529565997588014310L;

    @ApiModelProperty("自定义")
    private String custom;

    @ApiModelProperty("使用优惠券")
    private String couponCodeId;

}
