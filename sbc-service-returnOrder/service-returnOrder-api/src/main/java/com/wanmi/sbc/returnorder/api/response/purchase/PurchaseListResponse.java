package com.wanmi.sbc.returnorder.api.response.purchase;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.bean.vo.DistributionSettingSimVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseGoodsInfoCheckVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseMarketingCalcVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-03
 */
@Data
@ApiModel
public class PurchaseListResponse implements Serializable {

    private static final long serialVersionUID = -1568352064104571291L;


    /**
     * 拆箱信息
     */
    @ApiModelProperty(value = "拆箱")
    private List<DevanningGoodsInfoVO> devanningGoodsInfoVOS;

    /**
     * 商品SKU信息
     */
    @ApiModelProperty(value = "商品SKU信息")
    private List<GoodsInfoVO> goodsInfos;

    /**
     * 商品SPU信息
     */
    @ApiModelProperty(value = "商品SPU信息")
    private List<GoodsVO> goodses;

    /**
     * 商品SKU分页信息
     */
    @ApiModelProperty(value = "商品SKU分页信息")
    private MicroServicePage<GoodsInfoVO> goodsInfoPage;


    /**
     * 商品SKU分页信息
     */
    @ApiModelProperty(value = "商品SKU分页信息")
    private MicroServicePage<DevanningGoodsInfoVO> devanningGoodsInfoPage;

    /**
     * 商品SPU分页信息
     */
    @ApiModelProperty(value = "商品SPU分页信息")
    private MicroServicePage<GoodsVO> goodsPage;

    /**
     * 商品区间价格列表
     */
    @ApiModelProperty(value = "商品区间价格列表")
    private List<GoodsIntervalPriceVO> goodsIntervalPrices;

    /**
     * 公司信息
     */
    @ApiModelProperty(value = "公司信息")
    private List<CompanyInfoVO> companyInfos;

    /**
     * 店铺列表
     */
    @ApiModelProperty(value = "店铺列表")
    private List<StoreVO> stores;

    /**
     * 店铺营销信息，storeId作为map的key
     */
    @ApiModelProperty(value = "店铺营销信息,key为店铺id，value为营销信息列表")
    private Map<Long, List<PurchaseMarketingCalcVO>> storeMarketingMap;

    /**
     * 店铺优惠券信息，storeId作为map的key，Boolean代表有没有优惠券活动
     */
    @ApiModelProperty(value = "店铺是否有优惠券活动map,key为店铺id，value为是否存在优惠券活动")
    private Map<Long, Boolean> storeCouponMap;

    /**
     * 商品营销信息，skuId作为map的key
     */
    @ApiModelProperty(value = "单品营销信息map,key为单品id，value为营销列表")
    private Map<String, List<MarketingViewVO>> goodsMarketingMap;

    /**
     * 商品选择的营销
     */
    @ApiModelProperty(value = "商品选择的营销")
    private List<GoodsMarketingVO> goodsMarketings;

    /**
     * 采购单商品总金额
     */
    @ApiModelProperty(value = "采购单商品总金额")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 采购单商品总金额减去优惠后的金额
     */
    @ApiModelProperty(value = "采购单商品总金额减去优惠后的金额")
    private BigDecimal tradePrice = BigDecimal.ZERO;

    /**
     * 采购单优惠金额
     */
    @ApiModelProperty(value = "采购单优惠金额")
    private BigDecimal discountPrice = BigDecimal.ZERO;

    /**
     * 采购单商品总分销佣金
     */
    @ApiModelProperty(value = "采购单商品总分销佣金")
    private BigDecimal distributeCommission = BigDecimal.ZERO;

    /**
     * 是否自购-显示返利
     */
    @ApiModelProperty(value = "是否自购")
    private boolean selfBuying = false;

    /***
     * 分销设置VO
     */
    DistributionSettingSimVO distributionSettingSimVO;

    @ApiModelProperty(value = "赠品营销信息")
    private List<PurchaseMarketingCalcVO> giftList;

    /***
     * 已勾选有效有库存的购物车商品
     */
    @ApiModelProperty(value = "已勾选有效有库存的购物车商品")
    private List<PurchaseGoodsInfoCheckVO> purchaseGoodsInfos;

    /**
     * 每个商品对应的营销信息的限购数量
     */
    @ApiModelProperty(value = "每个商品对应的营销信息的限购数量")
    private Map<String, List<MarketingScopeVO>> purchaseNumMap;

    /**
     * 满减，立减，满折，立折金额
     */
    @ApiModelProperty(value = "满减，立减，满折，立折金额")
    private MarketingDiscountDetailsVO marketingDiscountDetails;
}
