package com.wanmi.sbc.goods.api.response.goodsrecommendgoods;

import com.wanmi.sbc.goods.bean.vo.GoodsRecommendGoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）商品推荐商品信息response</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendGoodsByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品推荐商品信息
     */
    @ApiModelProperty(value = "商品推荐商品信息")
    private GoodsRecommendGoodsVO goodsRecommendGoodsVO;
}
