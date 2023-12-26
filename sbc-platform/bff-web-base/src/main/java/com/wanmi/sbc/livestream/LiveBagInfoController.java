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
import com.wanmi.sbc.live.api.provider.bag.LiveBagProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.activity.LiveBagInfoRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagListRequest;
import com.wanmi.sbc.live.api.request.stream.BagAppRequest;
import com.wanmi.sbc.live.api.request.stream.IMAppRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.response.activity.LiveBagLogInfoResponse;
import com.wanmi.sbc.live.api.response.bag.LiveBagListResponse;
import com.wanmi.sbc.live.api.response.stream.BagAppResponse;
import com.wanmi.sbc.live.api.response.stream.LiveStreamInfoResponse;
import com.wanmi.sbc.livestream.response.LiveStreamResponse;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponFetchRequest;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/liveStream")
@Api(tags = "LiveStreamController", description = "S2B web公用-直播福袋")
public class LiveBagInfoController {
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private LiveBagLogProvider liveBagLogProvider;
    @Autowired
    private LiveBagProvider liveBagProvider;
    @Autowired
    private LiveStreamProvider liveStreamProvider;
    @Autowired
    private GoodsInfoSiteQueryProvider goodsInfoSiteQueryProvider;
    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;
    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "发送福袋")
    @RequestMapping(value = "/sendBag", method = RequestMethod.POST)
    public BaseResponse sendMessage(@RequestBody IMAppRequest imAppRequest) {
        imAppRequest.setType(7);
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
        imAppRequest.setLiveId(liveStreamInfoResponse.getContent().getLiveId());
        Map mapMesage = new HashMap();
        LiveStreamResponse liveStreamResponse = new LiveStreamResponse();
        //发送福袋
        mapMesage.put("messageType", 7);
        LiveBagInfoRequest bagInfoRequest=new LiveBagInfoRequest();
        bagInfoRequest.setBagId(imAppRequest.getBagId());
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
        mapMesage.put("liveStreamResponse", liveStreamResponse);
        JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
        imAppRequest.setSystemMessageJson(json.toJSONString());
        return liveStreamProvider.sendGroupSystemNotification(imAppRequest);
    }

    @ApiOperation(value = "发送商品")
    @RequestMapping(value = "/sendGoods", method = RequestMethod.POST)
    public BaseResponse sendGoods(@RequestBody IMAppRequest imAppRequest) {
        imAppRequest.setType(1);
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
                if (Objects.nonNull(goodsInfoDetailByGoodsInfoResponse.getGoods())) {
                    liveStreamResponse.setGoodsName(goodsInfoDetailByGoodsInfoResponse.getGoods().getGoodsName());
                    liveStreamResponse.setGoodsInfoImg(goodsInfoDetailByGoodsInfoResponse.getGoods().getGoodsImg());
                    liveStreamResponse.setMarketPrice(goodsInfoDetailByGoodsInfoResponse.getGoods().getMarketPrice());
                    liveStreamResponse.setGoodsType(0);
                }
            }catch (Exception e){
                try {
                    BaseResponse<GoodsInfoByIdResponse> goodsInfoResponse = retailGoodsInfoQueryProvider.getRetailById(GoodsInfoByIdRequest.builder().goodsInfoId(imAppRequest.getGoodsInfoId()).matchWareHouseFlag(true).build());
                    if (Objects.nonNull(goodsInfoResponse)) {
                        GoodsInfoVO goodsInfo = goodsInfoResponse.getContext();
                        if (Objects.nonNull(goodsInfo)) {
                            liveStreamResponse.setGoodsName(goodsInfo.getGoodsInfoName());
                            liveStreamResponse.setGoodsInfoImg(goodsInfo.getGoodsInfoImg());
                            liveStreamResponse.setMarketPrice(goodsInfo.getMarketPrice());
                            liveStreamResponse.setGoodsType(1);
                        }
                    } else {
                        liveStreamResponse.setIsHaveGoods(0);
                    }
                }
                catch (Exception e1) {
                    log.error("直播间商品查询失败", e1);
                    return BaseResponse.error("商品不存在");
                }
            }


        } else {
            liveStreamResponse.setIsHaveGoods(0);
        }
        mapMesage.put("messageContent", liveStreamResponse);
        JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
            imAppRequest.setSystemMessageJson(json.toJSONString());
            liveStreamProvider.sendGroupSystemNotification(imAppRequest);
            return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "参与福袋")
    @RequestMapping(value = "/joinBag", method = RequestMethod.POST)
    public BaseResponse joinBag(@RequestBody BagAppRequest imAppRequest) {
        imAppRequest.setCustomerId(commonUtil.getOperatorId());
        imAppRequest.setCustomerAccount(commonUtil.getOperator().getAccount());
        return liveBagLogProvider.joinBag(imAppRequest);
    }

    @ApiOperation(value = "开奖")
    @RequestMapping(value = "/openBag", method = RequestMethod.POST)
    public BaseResponse openBag(@RequestBody BagAppRequest imAppRequest) {
        BagAppResponse bagAppResponse=liveBagLogProvider.openBag(imAppRequest).getContext();
        /*bagAppResponse.getCustomerIds().forEach(customerId->{
            CouponFetchRequest request = new CouponFetchRequest();
            request.setCouponActivityId(bagAppResponse.getActivityId());
            request.setCouponInfoId(bagAppResponse.getCouponId());
            request.setCustomerId(customerId);
            DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
            if (Objects.nonNull(domainStoreRelaVO)) {
                request.setStoreId(domainStoreRelaVO.getStoreId());
            }
            couponCodeProvider.fetch(request);
        });*/

        return BaseResponse.SUCCESSFUL();
    }


    @ApiOperation(value = "用户福袋详情")
    @RequestMapping(value = "/customerBagInfo", method = RequestMethod.POST)
    public BaseResponse<LiveBagLogInfoResponse> getBagInfo(@RequestBody @Valid LiveBagInfoRequest bagInfoRequest){
        bagInfoRequest.setCustomerId(commonUtil.getOperatorId());
        bagInfoRequest.setCustomerAccount(commonUtil.getOperator().getAccount());
        return liveBagLogProvider.getBagInfo(bagInfoRequest);
    }

    @ApiOperation(value = "直播间福袋列表")
    @RequestMapping(value = "/listRoomBag", method = RequestMethod.POST)
    public BaseResponse<LiveBagListResponse> listRoomBag(@RequestBody LiveBagListRequest imAppRequest) {
        return liveBagProvider.liveBagRoomList(imAppRequest);
    }
}
