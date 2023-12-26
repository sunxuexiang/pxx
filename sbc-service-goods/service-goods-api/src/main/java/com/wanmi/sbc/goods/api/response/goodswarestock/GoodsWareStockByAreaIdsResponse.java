package com.wanmi.sbc.goods.api.response.goodswarestock;

import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 根据区域ids查询商品库存response
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockByAreaIdsResponse implements Serializable {

    private static final long serialVersionUID = 8202534489116024552L;

    /**
     * sku分仓库存表列表结果
     */
    @ApiModelProperty(value = "sku分仓库存表列表结果")
    private List<GoodsWareStockVO> goodsWareStockList;
}
