package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse
 * 商品分页响应对象
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午9:34
 */
@ApiModel
@Data
public class GoodsPageResponse implements Serializable {

    private static final long serialVersionUID = 8790632701356539648L;

    /**
     * 商品分页数据
     */
    @ApiModelProperty(value = "商品分页数据")
    private MicroServicePage<GoodsVO> goodsPage;

    /**
     * 商品分页数据
     */
    @ApiModelProperty(value = "商品分页数据")
    private GoodsInsidePageResponse goodsPages;


    /**
     * 商品SKU全部数据
     */
    @ApiModelProperty(value = "商品SKU全部数据")
    private List<GoodsInfoVO> goodsInfoList;

    /**
     * 商品SKU的规格值全部数据
     */
    @ApiModelProperty(value = "商品SKU的规格值全部数据")
    private List<GoodsInfoSpecDetailRelVO> goodsInfoSpecDetails;


    /**
     * （新）商品SKU的规格值全部数据
     */
    @ApiModelProperty(value = "商品SKU的规格值全部数据")
    private List<GoodsAttributeKeyVO> goodsAttributeKeyVOList;


    /**
     * 商品品牌所有数据
     */
    @ApiModelProperty(value = "商品品牌所有数据")
    private List<GoodsBrandVO> goodsBrandList;

    /**
     * 商品分类所有数据
     */
    @ApiModelProperty(value = "商品分类所有数据")
    private List<GoodsCateVO> goodsCateList;

    /**
     * 存放导入商品库的商品
     */
    @ApiModelProperty(value = "存放导入商品库的商品")
    private List<String> importStandard = new ArrayList<>();

    /**
     * 商品区间价格列表
     */
    @ApiModelProperty(value = "商品区间价格列表")
    private List<GoodsIntervalPriceVO> goodsIntervalPrices = new ArrayList<>();


    /**
     * 商品相关图片
     */
    @ApiModelProperty(value = "商品相关图片")
    private List<GoodsImageVO> images;

    /**
     * 促销图片和合成图片合集
     */
    private List<GoodsImageStypeVO> goodsImageStypeVOS;

    /**
     * 合成图片与促销图片与image的关系
     */
    private RelationGoodsImagesVO relationGoodsImagesVO;


}
