package com.wanmi.sbc.goods.devanninggoodsinfo.response;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品SKU视图响应
 * Created by daiyitian on 2017/3/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevanningGoodsInfoResponse {

    /**
     * 分页商品SKU信息
     */
    private MicroServicePage<DevanningGoodsInfo> devanningGoodsInfoPage;

    /**
     * 商品SKU信息
     */
    private List<DevanningGoodsInfo> devanningGoodsInfos;

    /**
     * 商品SPU信息
     */
    private List<Goods> goodses;

    /**
     * 商品区间价格列表
     */
    private List<GoodsIntervalPrice> goodsIntervalPrices;

    /**
     * 品牌列表
     */
    private List<GoodsBrand> brands;

    /**
     * 分类列表
     */
    private List<GoodsCate> cates;
}
