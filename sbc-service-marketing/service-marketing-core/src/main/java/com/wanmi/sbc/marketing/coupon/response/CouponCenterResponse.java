package com.wanmi.sbc.marketing.coupon.response;

import com.wanmi.sbc.marketing.coupon.model.vo.CouponView;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 优惠券 领券中心
 */
@Data
@Builder
public class CouponCenterResponse {

    /**
     * 优惠券list
     */
    private List<CouponView> couponViews;

    /**
     * 店铺名称
     */
    private Map<Long, String> storeMap;

}
