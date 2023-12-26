package com.wanmi.sbc.goods.info.reponse;

import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.info.model.root.BulkGoods;
import com.wanmi.sbc.goods.info.model.root.BulkGoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.price.model.root.GoodsCustomerPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsLevelPrice;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpec;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品SKU编辑视图响应
 * Created by daiyitian on 2017/3/24.
 */
@Data
public class BulkGoodsInfoEditResponse {

    /**
     * 商品SKU信息
     */
    private BulkGoodsInfo goodsInfo;

    /**
     * 关联本品信息
     */
    private BulkGoodsInfo choseProductGoodsInfo;

    /**
     * 相关商品SPU信息
     */
    private BulkGoods goods;

    /**
     * 商品规格列表
     */
    private List<GoodsSpec> goodsSpecs = new ArrayList<>();

    /**
     * 商品规格值列表
     */
    private List<GoodsSpecDetail> goodsSpecDetails = new ArrayList<>();

    /**
     * 商品等级价格列表
     */
    private List<GoodsLevelPrice> goodsLevelPrices = new ArrayList<>();

    /**
     * 商品客户价格列表
     */
    private List<GoodsCustomerPrice> goodsCustomerPrices = new ArrayList<>();

    /**
     * 商品订货区间价格列表
     */
    private List<GoodsIntervalPrice> goodsIntervalPrices = new ArrayList<>();

    /**
     * 商品相关图片
     */
    private List<GoodsImage> images = new ArrayList<>();


    /**
     * 散批仓位
     */
    private List<WareHouse> wareHousesList = new ArrayList<>();

}
