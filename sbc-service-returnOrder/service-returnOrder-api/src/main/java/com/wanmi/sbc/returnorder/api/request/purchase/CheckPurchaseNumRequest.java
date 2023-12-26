package com.wanmi.sbc.returnorder.api.request.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class CheckPurchaseNumRequest {

    @ApiModelProperty(value = "商品集合")
    List<CheckPurchaseNumDTO> checkPurchaseNumDTOS;
}
