package com.wanmi.sbc.goods.api.request.goodswarestock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
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
public class GoodsWareStockSyncNewRequest implements Serializable {

    private static final long serialVersionUID = -8019428046896266734L;

    /**
     * erp的商品编码
     */
    @ApiModelProperty(value = "erp的商品编码")
    @NotNull
    private String erpGoodsInfoNo;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    @NotNull
    private String goodsInfoId;
}

