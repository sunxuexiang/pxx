package com.wanmi.sbc.goods.api.response.merchantconfg;

import com.wanmi.sbc.goods.bean.vo.MerchantRecommendGoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>商品推荐商品新增结果</p>
 * @author SGY
 * @date 2019-06-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的商品推荐商品信息
     */
    @ApiModelProperty(value = "已新增的商品推荐商品信息")
    private MerchantRecommendGoodsVO merchantConfigGoodsVO;
}
