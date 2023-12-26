package com.wanmi.sbc.marketing.api.response.coupon;

import com.wanmi.sbc.marketing.bean.vo.CouponActivityConfigVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.marketing.bean.vo.CouponSignDaysVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityDetailByActivityTypeResponse {

    private static final long serialVersionUID = 7266785219929125320L;

    private CouponActivityVO couponActivity;

    private List<CouponActivityConfigVO> couponActivityConfigList;

    private List<CouponInfoVO> couponInfoList;

    private List<CouponSignDaysVO> couponSignDaysList;

}
