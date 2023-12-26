package com.wanmi.sbc.goods.response;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class CateGoodsInfoResponse implements Serializable {

    private static final long serialVersionUID = -55711095924851580L;

    /**
     * 索引SKU
     */
    @ApiModelProperty(value = "索引SKU")
    private List<GoodsInfoByCateIdResponse> goodsInfoByCateIdResponses = Lists.newArrayList();
}
