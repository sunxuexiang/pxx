package com.wanmi.sbc.returnorder.api.response.purchase;

import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseGoodsInfoCheckVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品SKU视图响应
 * Created by daiyitian on 2017/3/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class PurchaseResponse {

    /**
     * 拆箱信息
     */
    @ApiModelProperty(value = "拆箱信息")
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
     * 商品SKU分页的ids
     */
    @ApiModelProperty(value = "商品SKU分页的ids")
    private List<String> goodsInfoPageIds;

    /**
     * 商品SPU分页的ids
     */
    @ApiModelProperty(value = "商品SPU分页的ids")
    private List<String> goodsPageIds;

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

    /**
     * 购物车分页数据
     */
    @ApiModelProperty(value = "购物车分页数据")
    private Page<PurchaseVO> purchasePage;

    /***
     * 已勾选有效有库存的购物车商品
     */
    @ApiModelProperty(value = "已勾选有效有库存的购物车商品")
    private List<PurchaseGoodsInfoCheckVO> purchaseGoodsInfos;

    /**
     * 我的囤货商品总数量
     */
    @ApiModelProperty(value = "商品总数量")
    private Long goodsTotalCount;

}
