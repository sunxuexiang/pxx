package com.wanmi.sbc.goods.info.reponse;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.info.model.root.BulkGoods;
import com.wanmi.sbc.goods.info.model.root.BulkGoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 商品SKU视图响应
 * @author: XinJiang
 * @time: 2022/3/10 15:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkGoodsInfoResponse implements Serializable {

    private static final long serialVersionUID = 6543724187918742360L;
    /**
     * 分页商品SKU信息
     */
    private MicroServicePage<BulkGoodsInfo> goodsInfoPage;

    /**
     * 商品SKU信息
     */
    private List<BulkGoodsInfo> goodsInfos;

    /**
     * 商品SPU信息
     */
    private List<BulkGoods> goodses;

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
