package com.wanmi.sbc.goods.api.response.merchantconfg;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendGoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>商品推荐商品分页结果</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品推荐商品分页结果
     */
    @ApiModelProperty(value = "商品推荐商品分页结果")
    private MicroServicePage<MerchantRecommendGoodsVO> goodsRecommendGoodsVOPage;
}
