package com.wanmi.sbc.es.elastic.response;

import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品索引SPU查询结果
 * Created by daiyitian on 2017/3/24.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class EsGoodsResponse {

    /**
     * 索引SKU
     */
    @ApiModelProperty(value = "索引SKU")
    private Page<EsGoods> esGoods = new PageImpl<>(new ArrayList<>());

    /**
     * SPU
     */
    @ApiModelProperty(value = "SPU")
    private List<GoodsVO> goodsList = new ArrayList<>();

    /**
     * 商品区间价格列表
     */
    @ApiModelProperty(value = "商品区间价格列表")
    private List<GoodsIntervalPriceVO> goodsIntervalPrices = new ArrayList<>();

    /**
     * 品牌
     */
    @ApiModelProperty(value = "品牌")
    private List<GoodsBrandVO> brands = new ArrayList<>();

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类")
    private List<GoodsCateVO> cateList = new ArrayList<>();

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    private List<GoodsSpecVO> goodsSpecs = new ArrayList<>();

    /**
     * 规格值
     */
    @ApiModelProperty(value = "规格值")
    private List<GoodsSpecDetailVO> goodsSpecDetails = new ArrayList<>();
}
