package com.wanmi.sbc.goods.info.reponse;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class SpecialGoodsQueryResponse {

    /**
     * 分页商品SKU信息
     */
    @ApiModelProperty(value = "分页商品SKU信息")
    private MicroServicePage<GoodsInfo> goodsInfoPage = new MicroServicePage<>(new ArrayList<>());

    /**
     * SKU列表
     */
    private List<GoodsInfo> goodsInfoList = new ArrayList<>();

    /**
     * 商品SKU的规格值全部数据
     */
    private List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();

    /**
     * 商品品牌所有数据
     */
    private List<GoodsBrand> goodsBrandList = new ArrayList<>();

    /**
     * 商品分类所有数据
     */
    private List<GoodsCate> goodsCateList = new ArrayList<>();

    /**
     * SPU列表
     */
    private List<Goods> goodList = new ArrayList<>();
}
