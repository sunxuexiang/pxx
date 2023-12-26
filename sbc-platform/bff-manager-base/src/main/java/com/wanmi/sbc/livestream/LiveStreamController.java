package com.wanmi.sbc.livestream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoSiteQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.live.api.provider.activity.LiveBagLogProvider;
import com.wanmi.sbc.live.api.provider.activity.LiveStreamActivityQueryProvider;
import com.wanmi.sbc.live.api.provider.bag.LiveBagProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.activity.LiveBagInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityInfoRequest;
import com.wanmi.sbc.live.api.request.stream.IMAppRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.activity.LiveBagLogInfoResponse;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityInfoResponse;
import com.wanmi.sbc.live.api.response.bag.LiveBagInfoResponse;
import com.wanmi.sbc.live.api.response.stream.LiveStreamInfoResponse;
import com.wanmi.sbc.live.api.response.stream.LiveStreamPageResponse;
import com.wanmi.sbc.live.bean.vo.LiveStreamActivityVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.livestream.response.LiveStreamResponse;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoByIdRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityGetByIdResponse;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Api(description = "新版直播管理API", tags = "LiveStreamController")
@RestController
@RequestMapping(value = "/liveStream")
public class LiveStreamController {
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private LiveStreamProvider liveStreamProvider;
    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;
    @Autowired
    private GoodsInfoSiteQueryProvider goodsInfoSiteQueryProvider;
    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;
    @Autowired
    private LiveStreamActivityQueryProvider activityQueryProvider;
    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;
    @Autowired
    private LiveBagLogProvider liveBagLogProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @ApiOperation(value = "直播列表查询")
    @PostMapping("/streamPageList")
    public BaseResponse<LiveStreamPageResponse> getList(@RequestBody @Valid LiveStreamPageRequest pageReq) {
        return liveStreamQueryProvider.listPage(pageReq);
    }


    @ApiOperation(value = "直播详情")
    @PostMapping("/streamDetail")
    public BaseResponse<LiveStreamInfoResponse> streamDetail(@RequestBody @Valid LiveStreamInfoRequest streamInfoRequest) {
        return liveStreamQueryProvider.streamInfo(streamInfoRequest);
    }

    @ApiOperation(value = "发送系统消息")
    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public BaseResponse sendMessage(@RequestBody @Valid IMAppRequest imAppRequest) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播管理", "发送系统消息", "发送系统消息");
        LiveStreamInfoRequest streamInfoRequest=new LiveStreamInfoRequest();
        streamInfoRequest.setLiveStatus(1);
        streamInfoRequest.setLiveRoomId(imAppRequest.getLiveRoomId());
        LiveStreamInfoResponse liveStreamInfoResponse=liveStreamQueryProvider.streamInfo(streamInfoRequest).getContext();
        if(Objects.isNull(liveStreamInfoResponse)){
            return BaseResponse.error("未开启直播请勿推送！");
        }
        if(Objects.isNull(liveStreamInfoResponse.getContent())){
            return BaseResponse.error("未开启直播请勿推送！");
        }
        Map mapMesage = new HashMap();
        LiveStreamResponse liveStreamResponse = new LiveStreamResponse();
        if (imAppRequest.getType() == 1) {
            //发送商品
            mapMesage.put("messageType", 1);
            if (Objects.nonNull(imAppRequest.getGoodsInfoId())) {
                liveStreamResponse.setIsHaveGoods(1);
                GoodsInfoRequest goodsInfoRequest = new GoodsInfoRequest();
                goodsInfoRequest.setGoodsInfoId(imAppRequest.getGoodsInfoId());
                liveStreamResponse.setGoodsInfoId(imAppRequest.getGoodsInfoId());
                try {
                    GoodsInfoDetailByGoodsInfoResponse goodsInfoDetailByGoodsInfoResponse = goodsInfoSiteQueryProvider.getByGoodsInfo(goodsInfoRequest)
                            .getContext();
                    liveStreamResponse.setGoodsInfoId(imAppRequest.getGoodsInfoId());
                    if (Objects.nonNull(goodsInfoDetailByGoodsInfoResponse.getGoods())) {
                        liveStreamResponse.setGoodsName(goodsInfoDetailByGoodsInfoResponse.getGoods().getGoodsName());
                        liveStreamResponse.setGoodsInfoImg(goodsInfoDetailByGoodsInfoResponse.getGoods().getGoodsImg());
                        liveStreamResponse.setMarketPrice(goodsInfoDetailByGoodsInfoResponse.getGoods().getMarketPrice());
                        liveStreamResponse.setGoodsType(0);
                    }
                }catch (Exception e){
                    BaseResponse<GoodsInfoByIdResponse> goodsInfoResponse = retailGoodsInfoQueryProvider.getRetailById(GoodsInfoByIdRequest.builder().goodsInfoId(imAppRequest.getGoodsInfoId()).matchWareHouseFlag(true).build());
                    if(Objects.nonNull(goodsInfoResponse)) {
                        GoodsInfoVO goodsInfo=goodsInfoResponse.getContext();
                        if (Objects.nonNull(goodsInfo)) {
                            liveStreamResponse.setGoodsName(goodsInfo.getGoodsInfoName());
                            liveStreamResponse.setGoodsInfoImg(goodsInfo.getGoodsInfoImg());
                            liveStreamResponse.setMarketPrice(goodsInfo.getMarketPrice());
                            liveStreamResponse.setGoodsType(1);
                        }
                    }else{
                        liveStreamResponse.setIsHaveGoods(0);
                    }
                }

            } else {
                liveStreamResponse.setIsHaveGoods(0);
            }
        } else if (imAppRequest.getType() == 2) {
            mapMesage.put("messageType", 2);
            if (Objects.nonNull(imAppRequest.getActivityId())) {
                //判断优惠卷是否发放
                LiveStreamActivityInfoRequest infoRequest=new LiveStreamActivityInfoRequest();
                infoRequest.setActivityId(imAppRequest.getActivityId());
                LiveStreamActivityInfoResponse activityInfoResponse= activityQueryProvider.activityInfo(infoRequest).getContext();
                if(Objects.nonNull(activityInfoResponse)){
                    LiveStreamActivityVO streamActivityVO=activityInfoResponse.getStreamActivityVO();
                    if(Objects.nonNull(streamActivityVO)){
                        if(Objects.nonNull(streamActivityVO.getCouponId())) {
                            if (streamActivityVO.getCouponId().contains(imAppRequest.getCouponId())) {
                                return BaseResponse.error("当前直播已经发放过该优惠券，请选择其他优惠券发放!");
                            }
                        }
                    }
                }
                CouponActivityGetByIdRequest queryRequest = new CouponActivityGetByIdRequest();
                queryRequest.setId(imAppRequest.getActivityId());
                CouponActivityGetByIdResponse response = couponActivityQueryProvider.getById(queryRequest).getContext();
                liveStreamResponse.setActivityId(imAppRequest.getActivityId());
                if (Objects.nonNull(response)) {
                    liveStreamResponse.setActivityName(response.getActivityName());
                }
                if (Objects.nonNull(imAppRequest.getCouponId())) {
                    CouponInfoVO couponInfoVO = couponInfoQueryProvider.getById(CouponInfoByIdRequest.builder().couponId(imAppRequest.getCouponId()).build())
                            .getContext();
                    if (Objects.nonNull(couponInfoVO)) {
                        liveStreamResponse.setIsHaveCoupon(1);
                        liveStreamResponse.setCouponId(couponInfoVO.getCouponId());
                        liveStreamResponse.setCouponName(couponInfoVO.getCouponName());
                        liveStreamResponse.setDenomination(couponInfoVO.getDenomination());
                        liveStreamResponse.setCouponDesc(couponInfoVO.getCouponDesc());
                        liveStreamResponse.setFullBuyPrice(couponInfoVO.getFullBuyPrice());
                        liveStreamResponse.setFullBuyType(couponInfoVO.getFullBuyType().toValue());
                    }else {
                        liveStreamResponse.setIsHaveCoupon(0);
                    }
                }
            }
        }else if (imAppRequest.getType() == 4){
            mapMesage.put("messageType", 4);
        } else if (imAppRequest.getType() == 7){
            //发送福袋
            mapMesage.put("messageType", 7);
            LiveBagInfoRequest bagInfoRequest=new LiveBagInfoRequest();
            bagInfoRequest.setBagId(imAppRequest.getBagId());
            imAppRequest.setLiveId(liveStreamInfoResponse.getContent().getLiveId());
            LiveBagLogInfoResponse baseResponse=liveBagLogProvider.getPushBagInfo(bagInfoRequest).getContext();
            if (Objects.nonNull(baseResponse)) {
                liveStreamResponse.setIsHaveBag(1);
                liveStreamResponse.setBagId(baseResponse.getBagId().intValue());
                liveStreamResponse.setSpecifyContent(baseResponse.getSpecifyContent());
                liveStreamResponse.setLotteryStatus(1);
                liveStreamResponse.setCountdown((new Double(baseResponse.getLotteryTime() * 60 * 1000)).longValue());
                liveStreamResponse.setWinningNumber(baseResponse.getWinningNumber());
                liveStreamResponse.setCouponName(baseResponse.getCouponName());
                liveStreamResponse.setDenomination(baseResponse.getDenomination());
                liveStreamResponse.setBagName(baseResponse.getBagName());
                liveStreamResponse.setCouponDesc(baseResponse.getCouponDesc());
                liveStreamResponse.setFullBuyPrice(baseResponse.getFullBuyPrice());
                liveStreamResponse.setFullBuyType(baseResponse.getFullBuyType());
                liveStreamResponse.setIsJoin(baseResponse.getIsJoin());
            }else {
                liveStreamResponse.setIsHaveBag(0);
            }
         }
        if (imAppRequest.getType() == 7){
            mapMesage.put("liveStreamResponse", liveStreamResponse);
        }else{
            mapMesage.put("messageContent", liveStreamResponse);
          }

            JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
            imAppRequest.setSystemMessageJson(json.toJSONString());
            return liveStreamProvider.sendGroupSystemNotification(imAppRequest);
        }

    @PostMapping("/deleteLiveStream")
    public BaseResponse deleteLiveStream (@RequestBody LiveStreamInfoRequest streamInfoRequest) {
        if (streamInfoRequest.getLiveId() == null) {
            return BaseResponse.error("直播ID不能为空");
        }
        return liveStreamProvider.deleteLiveStream(streamInfoRequest.getLiveId());
    }
}
