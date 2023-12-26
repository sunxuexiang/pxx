package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGoodsInfoPurchaseNumRequest implements Serializable {
    private static final long serialVersionUID = -6230501195719873212L;

    @ApiModelProperty(value = "需要操作的SKU")
    private List<GoodsInfoPurchaseNumDTO> goodsInfoPurchaseNumDTOS;

    @ApiModelProperty(value = "判断是修改添加还是清除 true 添加 false 删除")
    private Boolean b;
}
