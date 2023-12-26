package com.wanmi.sbc.marketing.api.response.coupon;

import com.wanmi.sbc.customer.bean.vo.CouponMarketingCustomerScopeVO;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityFullOrderRequest;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityConfigVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityLevelVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-23
 */
@ApiModel
@Data
public class CouponActivityDetailResponse implements Serializable {

    private static final long serialVersionUID = -2632254645824662407L;

    @ApiModelProperty(value = "优惠券活动")
    private CouponActivityVO couponActivity;

    @ApiModelProperty(value = "优惠券活动配置列表")
    private List<CouponActivityConfigVO> couponActivityConfigList;

    @ApiModelProperty(value = "优惠券信息")
    private List<CouponInfoVO> couponInfoList;

    @ApiModelProperty(value = "客户等级列表")
    private List<CustomerLevelVO> customerLevelList;

    @ApiModelProperty(value = "活动目标客户范围")
    List<CouponMarketingCustomerScopeVO> couponMarketingCustomerScope;

    @ApiModelProperty(value = "目标客户信息")
    List<CustomerVO>  customerDetailVOS;

    @ApiModelProperty(value = "商品信息")
    private List<GoodsInfoVO> goodsInfoVOS;

    @ApiModelProperty(value = "订单满额赠券配置信息")
    private List<CouponActivityFullOrderResponse>  couponActivityFullOrders;

    @ApiModelProperty(value = "购买指定商品赠券促销等级信息")
    private List<CouponActivityLevelVO> couponActivityLevelVOS;

}
