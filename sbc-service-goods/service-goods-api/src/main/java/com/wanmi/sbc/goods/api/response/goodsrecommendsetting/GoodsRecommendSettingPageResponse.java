package com.wanmi.sbc.goods.api.response.goodsrecommendsetting;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>商品推荐配置分页结果</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品推荐配置分页结果
     */
    @ApiModelProperty(value = "商品推荐配置分页结果")
    private MicroServicePage<GoodsRecommendSettingVO> goodsRecommendSettingVOPage;
}
