package com.wanmi.sbc.goods.api.response.info;

import com.wanmi.sbc.goods.bean.vo.GoodsBySalesRankingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品销量排行
 * @author jeffrey
 * @create 2021-08-07 9:49
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSalesRankingTopResponse {
    private static final long serialVersionUID = -4174954292041196452L;
    /**
     * 商品sku排行
     */
    @ApiModelProperty(value = "商品SKU排行")
    private Long top;

    /**
     * 二级分类分类名称
     */
    @ApiModelProperty(value = "二级分类名称")
    private String cateName;

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cateId;
}
