package com.wanmi.sbc.livestream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.live.api.provider.activity.LiveStreamActivityProvider;
import com.wanmi.sbc.live.api.provider.activity.LiveStreamActivityQueryProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.activity.*;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityInfoResponse;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityListResponse;
import com.wanmi.sbc.live.api.response.stream.LiveStreamLogInfoResponse;
import com.wanmi.sbc.live.bean.vo.LiveStreamActivityVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamLogInfoVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetByActivityIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetDetailByIdAndStoreIdRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailByActivityIdResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.bean.enums.CouponStatus;
import com.wanmi.sbc.marketing.bean.enums.RangeDayType;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Slf4j
@Api(description = "新版直播活动管理API", tags = "LiveStreamAcitivityController")
@RestController
@RequestMapping(value = "/liveStream")
public class LiveStreamActivityController {
    private static Logger logger = LoggerFactory.getLogger(LiveStreamActivityController.class);
    @Autowired
    private LiveStreamActivityQueryProvider activityQueryProvider;
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private LiveStreamActivityProvider activityProvider;
    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;
    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @ApiOperation(value = "查询直播间活动列表")
    @PostMapping("/activityList")
    public BaseResponse getList(@RequestBody @Valid LiveStreamActivityListRequest liveStreamActivityListRequest) {
        LiveStreamActivityListResponse activityListResponse=activityQueryProvider.list(liveStreamActivityListRequest).getContext();
        List<CouponActivityDetailByActivityIdResponse> responseList=new ArrayList<>();
        CouponActivityGetByActivityIdRequest request = new CouponActivityGetByActivityIdRequest();
        activityListResponse.getActivityVOList().forEach(activityVO -> {
            request.setActivityId(activityVO.getActivityId());
            CouponActivityDetailByActivityIdResponse response = couponActivityQueryProvider.getByActivityId(request)
                    .getContext();
            responseList.add(response);
        });
        return BaseResponse.success(responseList);
    }


    @ApiOperation(value = "直播活动记录列表")
    @PostMapping("/activityNewRecordList")
    public BaseResponse activityNewRecordList(@RequestBody LiveStreamRecordListRequest activityListRequest) {
        LiveStreamPageRequest request=new LiveStreamPageRequest();
        request.setLiveId(activityListRequest.getLiveId());
        LiveStreamLogInfoResponse liveStreamLogInfoResponse=liveStreamQueryProvider.streamLogInfo(request).getContext();
        List<CouponActivityDetailByActivityIdResponse> responseList=new ArrayList<>();
        if(Objects.nonNull(liveStreamLogInfoResponse.getLiveStreamLogInfoVO())) {
            LiveStreamLogInfoVO liveStreamLogInfoVO = liveStreamLogInfoResponse.getLiveStreamLogInfoVO();
            if(StringUtils.isNotEmpty(liveStreamLogInfoVO.getActivityIds())){
                CouponActivityGetByActivityIdRequest activityIdRequest = new CouponActivityGetByActivityIdRequest();
                for (String activityId:liveStreamLogInfoVO.getActivityIds().split(",")){
                    activityIdRequest.setActivityId(activityId);
                    CouponActivityDetailByActivityIdResponse response = couponActivityQueryProvider.getByActivityId(activityIdRequest)
                            .getContext();
                    responseList.add(response);
                }
            }
        }
        return BaseResponse.success(responseList);
    }

    @ApiOperation(value = "直播活动记录列表")
    @PostMapping("/activityRecordList")
    public BaseResponse activityRecordList(@RequestBody LiveStreamRecordListRequest activityListRequest) {
        LiveStreamInfoRequest streamInfoRequest=new LiveStreamInfoRequest();
        streamInfoRequest.setLiveId(activityListRequest.getLiveId());
        LiveStreamVO liveStreamVO= liveStreamQueryProvider.streamInfo(streamInfoRequest).getContext().getContent();
        List<CouponActivityDetailByActivityIdResponse> responseList=new ArrayList<>();
        CouponActivityGetByActivityIdRequest request = new CouponActivityGetByActivityIdRequest();
        if(Objects.nonNull(liveStreamVO.getStoreId())) {
            for (String activityId:liveStreamVO.getStoreId().split(",")){
                request.setActivityId(activityId);
                CouponActivityDetailByActivityIdResponse response = couponActivityQueryProvider.getByActivityId(request)
                        .getContext();
                responseList.add(response);
            }
        }
        return BaseResponse.success(responseList);
    }

    /**
     * 获取活动详情
     *
     * @param infoRequest
     * @return
     */
    @ApiOperation(value = "获取活动详情")
    @RequestMapping(value = "/activityInfo", method = RequestMethod.POST)
    public BaseResponse<CouponActivityDetailResponse> getActivityDetail(@RequestBody LiveStreamActivityInfoRequest infoRequest) {
        if (StringUtils.isBlank(infoRequest.getActivityId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        LiveStreamActivityInfoResponse activityInfoResponse= activityQueryProvider.activityInfo(infoRequest).getContext();
        LiveStreamActivityVO streamActivityVO=null;
        if(Objects.nonNull(activityInfoResponse)) {
            streamActivityVO= activityInfoResponse.getStreamActivityVO();
        }
        CouponActivityDetailResponse response =
                couponActivityQueryProvider.getDetailByIdAndStoreId(new CouponActivityGetDetailByIdAndStoreIdRequest(infoRequest.getActivityId(), commonUtil.getStoreId())).getContext();
        LiveStreamActivityVO finalStreamActivityVO = streamActivityVO;
        response.getCouponInfoList().forEach(couponInfoVO -> {
            if (Objects.nonNull(finalStreamActivityVO)) {
                if (Objects.nonNull(finalStreamActivityVO.getCouponId())) {
                    if (finalStreamActivityVO.getCouponId().contains(couponInfoVO.getCouponId())) {
                        couponInfoVO.setSendStatus(1);
                    } else {
                        couponInfoVO.setSendStatus(0);
                    }
                }
            }else{
                couponInfoVO.setSendStatus(0);
            }
            couponInfoVO.setCouponStatus(getCouponStatus(couponInfoVO));
        });

        return BaseResponse.success(response);
    }

    @ApiOperation(value = "直播间添加直播活动")
    @RequestMapping(value = "/activityAdd", method = RequestMethod.POST)
    public BaseResponse activityAdd(@RequestBody @Valid LiveStreamActivityAddRequest addReq) {
        activityProvider.supplier(addReq);
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播活动管理", "直播间添加直播活动", "操作成功：直播间ID" + (Objects.nonNull(addReq) ? addReq.getLiveRoomId() : ""));
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "直播间移除直播活动")
    @RequestMapping(value = "/activityModify", method = RequestMethod.POST)
    public BaseResponse activityModify(@RequestBody @Valid LiveStreamActivityModifyRequest modifyReq) {
        activityProvider.modify(modifyReq);
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播活动管理", "直播间移除直播活动", "操作成功：直播间ID" + (Objects.nonNull(modifyReq) ? modifyReq.getLiveRoomId() : ""));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取优惠券状态
     *
     * @param couponInfo
     * @return
     */
    public CouponStatus getCouponStatus(CouponInfoVO couponInfo) {
        if (Objects.equals(RangeDayType.DAYS, couponInfo.getRangeDayType())) {
            return CouponStatus.DAYS;
        } else {
            if (couponInfo.getStartTime() != null && couponInfo.getEndTime() != null) {
                if (LocalDateTime.now().isBefore(couponInfo.getStartTime())) {
                    return CouponStatus.NOT_START;
                } else if (LocalDateTime.now().isAfter(couponInfo.getEndTime())) {
                    return CouponStatus.ENDED;
                } else {
                    return CouponStatus.STARTED;
                }
            }
        }
        return null;
    }
}
