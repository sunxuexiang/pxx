package com.wanmi.sbc.couponactivity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetDetailByIdAndStoreIdRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 优惠券活动
 * @author: XinJiang
 * @time: 2022/3/3 10:32
 */
@RestController
@Api(tags = "CouponActivityController", description = "S2B 优惠券活动API")
@RequestMapping("/coupon-activity")
@Validated
public class CouponActivityController {

    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;

    /**
     * 获取活动详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取活动详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "id", value = "优惠券活动Id", required = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BaseResponse<CouponActivityDetailResponse> getActivityDetail(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CouponActivityDetailResponse response =
                couponActivityQueryProvider.getDetailByIdAndStoreId(new CouponActivityGetDetailByIdAndStoreIdRequest(id, null)).getContext();
        return BaseResponse.success(response);
    }
}
