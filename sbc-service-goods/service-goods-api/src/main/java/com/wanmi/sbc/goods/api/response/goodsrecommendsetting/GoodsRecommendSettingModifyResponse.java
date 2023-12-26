package com.wanmi.sbc.goods.api.response.goodsrecommendsetting;

import com.google.common.collect.Lists;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>商品推荐配置修改结果</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的商品推荐配置信息
     */
    @ApiModelProperty(value = "已修改的商品推荐配置信息")
    private GoodsRecommendSettingVO goodsRecommendSettingVO;

    private List<GoodsRecommendSettingVO> goodsRecommendSettingVOS = Lists.newArrayList();
}
