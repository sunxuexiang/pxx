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
 * <p>商品推荐商品修改结果</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendGoodsModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的商品推荐商品信息
     */
    @ApiModelProperty(value = "已修改的商品推荐商品信息")
    private GoodsRecommendGoodsVO goodsRecommendGoodsVO;
}
