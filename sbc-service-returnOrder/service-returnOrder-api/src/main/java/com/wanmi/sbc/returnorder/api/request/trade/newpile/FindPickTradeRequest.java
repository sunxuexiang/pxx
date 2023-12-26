package com.wanmi.sbc.returnorder.api.request.trade.newpile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class FindPickTradeRequest implements Serializable {

    @ApiModelProperty(value = "囤货单号")
    String pileTradeNo;
}