package com.wanmi.sbc.goods.response;

import com.google.common.collect.Lists;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandShelflifeResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
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
 * @description: 根据id查询任意（包含已删除）商品推荐配置信息response
 * @author: XinJiang
 * @time: 2022/3/4 10:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class GoodsRecommendEsResponse implements Serializable {

    private static final long serialVersionUID = 3973312626817597963L;

    /**
     * 商品推荐配置信息
     */
    @ApiModelProperty(value = "商品推荐配置信息")
    private GoodsRecommendSettingVO goodsRecommendSettingVO;

    private List<GoodsRecommendSettingVO> goodsRecommendSettingVOS = Lists.newArrayList();

    @ApiModelProperty("商品信息==》包含营销")
    private EsGoodsLimitBrandShelflifeResponse esGoodsLimitBrandShelflifeResponse;

    @ApiModelProperty("商品信息==》包含营销")
    private List<GoodsInfoVO> goodsInfoVOS;

    private Integer totalPages;

    private Long totalElements;
}
