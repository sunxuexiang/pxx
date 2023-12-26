package com.wanmi.sbc.coupon;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoPageRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponInfoPageResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(description = "优惠券信息API", tags = "CouponInfoController")
@RestController
@RequestMapping("/coupon-info")
@Validated
public class CouponInfoController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CouponInfoProvider couponInfoProvider;


    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 获取优惠券列表
     *
     * @param couponInfoQueryRequest
     * @return
     */
    @ApiOperation(value = "获取优惠券列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<CouponInfoPageResponse> page(@RequestBody CouponInfoPageRequest couponInfoQueryRequest) {
        couponInfoQueryRequest.setPlatformFlag(DefaultFlag.YES);
        couponInfoQueryRequest.setDelFlag(DeleteFlag.NO);
        couponInfoQueryRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);
        couponInfoQueryRequest.putSort("createTime", SortType.DESC.toValue());
        return couponInfoQueryProvider.page(couponInfoQueryRequest);
    }

    /**
     * 新增优惠券
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "新增优惠券")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse add(@Valid @RequestBody CouponInfoAddRequest request) {
        request.setPlatformFlag(DefaultFlag.YES);
        request.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);
        request.setCreatePerson(commonUtil.getOperatorId());
        request.setWareId(-1l);
        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "创建优惠券", "优惠券名称：" + request.getCouponName());
        return couponInfoProvider.add(request);
    }

}
