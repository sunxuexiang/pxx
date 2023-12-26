package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alipay.api.domain.GoodsInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseMarketingCalcVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ImportGoodsInfosExcel {


    /**
     * 商品信息
     */
    private List<ImportGoodsInfo> importGoodsInfoList;

    /**
     * 未使用的优惠券
     */
    @ApiModelProperty(value = "使用的优惠券")
    private CouponCodeVO couponCode;

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

    @ApiModelProperty(value = "赠品营销信息")
    private List<PurchaseMarketingCalcVO> giftList;

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

}
