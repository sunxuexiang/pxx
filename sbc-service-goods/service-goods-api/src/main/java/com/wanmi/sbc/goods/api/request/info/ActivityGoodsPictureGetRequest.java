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
public class ActivityGoodsPictureGetRequest implements Serializable {

    @ApiModelProperty(value = "批量skuIds")
    private List<String> goodsInfoIds;

    @ApiModelProperty(value = "批量goodsIds")
    private List<String> goodsIds;
}
