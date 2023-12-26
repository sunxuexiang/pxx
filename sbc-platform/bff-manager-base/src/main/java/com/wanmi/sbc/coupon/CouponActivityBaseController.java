package com.wanmi.sbc.coupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityGetByIdResponse;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.nonNull;

@RestController
@Api(tags = "CouponActivityBaseController", description = "S2B 管理端公用-优惠券活动管理API")
@RequestMapping("/coupon-activity")
@Validated
public class CouponActivityBaseController {

    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;

    @Autowired
    private CouponActivityProvider couponActivityProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 开始活动
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "开始活动")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "id", value = "优惠券活动Id", required = true)
    @RequestMapping(value = "/start/{id}", method = RequestMethod.PUT)
    public BaseResponse startActivity(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        couponActivityProvider.start(new CouponActivityStartByIdRequest(id));

        CouponActivityGetByIdRequest queryRequest = new CouponActivityGetByIdRequest();
        queryRequest.setId(id);
        CouponActivityGetByIdResponse response = couponActivityQueryProvider.getById(queryRequest).getContext();

        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "开始优惠券活动",
                "优惠券活动：" + (nonNull(response) ? response.getActivityName() : ""));

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 暂停活动
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "暂停活动")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "id", value = "优惠券活动Id", required = true)
    @RequestMapping(value = "/pause/{id}", method = RequestMethod.PUT)
    public BaseResponse pauseActivity(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        couponActivityProvider.pause(new CouponActivityPauseByIdRequest(id));

        CouponActivityGetByIdRequest queryRequest = new CouponActivityGetByIdRequest();
        queryRequest.setId(id);
        CouponActivityGetByIdResponse response = couponActivityQueryProvider.getById(queryRequest).getContext();

        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "暂停优惠券活动",
                "优惠券活动：" + (nonNull(response) ? response.getActivityName() : ""));

        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "删除活动")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "id", value = "优惠券活动Id", required = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public BaseResponse deleteActivity(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CouponActivityGetByIdRequest queryRequest = new CouponActivityGetByIdRequest();
        queryRequest.setId(id);
        CouponActivityGetByIdResponse response = couponActivityQueryProvider.getById(queryRequest).getContext();

        couponActivityProvider.deleteByIdAndOperatorId(new CouponActivityDeleteByIdAndOperatorIdRequest(id,
                commonUtil.getOperatorId()));

        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "删除优惠券活动",
                "优惠券活动：" + (nonNull(response) ? response.getActivityName() : ""));
        return BaseResponse.SUCCESSFUL();
    }

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
                couponActivityQueryProvider.getDetailByIdAndStoreId(new CouponActivityGetDetailByIdAndStoreIdRequest(id, commonUtil.getStoreId())).getContext();
        return BaseResponse.success(response);
    }

    /**
     * 活动列表分页
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "优惠券活动列表分页")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<CouponActivityVO>> page(@RequestBody CouponActivityPageRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        MicroServicePage<CouponActivityVO> response =
                couponActivityQueryProvider.page(request).getContext().getCouponActivityVOPage();
        return BaseResponse.success(response);
    }

}
