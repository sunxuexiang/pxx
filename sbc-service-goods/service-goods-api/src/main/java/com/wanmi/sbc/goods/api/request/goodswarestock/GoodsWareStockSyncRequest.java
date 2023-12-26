package com.wanmi.sbc.goods.api.request.goodswarestock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author caofang
 * @date 2020/6/2 10:13
 * @Description
 */
@ApiModel
@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockSyncRequest implements Serializable {

    private static final long serialVersionUID = -8019428046896266734L;

    @ApiModelProperty(value = "商品ID集")
    private List<String> goodsInfoIds;
}
