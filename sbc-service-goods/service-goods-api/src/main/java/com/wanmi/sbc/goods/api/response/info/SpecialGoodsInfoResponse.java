package com.wanmi.sbc.goods.api.response.info;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class SpecialGoodsInfoResponse implements Serializable {

    private static final long serialVersionUID = 7923504494453897425L;
    /**
     * 分页商品SKU信息
     */
    @ApiModelProperty(value = "分页商品SKU信息")
    private MicroServicePage<GoodsInfoVO> goodsInfoPage;

    /**
     * SKU列表
     */
    @ApiModelProperty(value = "SKU列表")
    private List<GoodsInfoVO> goodsInfoList = new ArrayList<>();


    /**
     * 品牌列表
     */
    @ApiModelProperty(value = "品牌列表")
    private List<GoodsBrandVO> brands = new ArrayList<>();

    /**
     * 分类列表
     */
    @ApiModelProperty(value = "分类列表")
    private List<GoodsCateVO> cates = new ArrayList<>();

    /**
     * 商品SKU的规格值全部数据
     */
    @ApiModelProperty(value = "商品SKU的规格值全部数据")
    private List<GoodsInfoSpecDetailRelVO> goodsInfoSpecDetails;

    /**
     * SPU列表
     */
    @ApiModelProperty(value = "SPU列表")
    private List<GoodsVO> goods = new ArrayList<>();


}
