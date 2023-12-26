package com.wanmi.sbc.live.provider.impl.stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.ObjectUtils;
import com.wanmi.sbc.live.activity.model.root.LiveBagLog;
import com.wanmi.sbc.live.activity.service.LiveBagLogService;
import com.wanmi.sbc.live.activity.service.LiveBagProducerService;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamProvider;
import com.wanmi.sbc.live.api.request.stream.*;
import com.wanmi.sbc.live.api.response.stream.BagAppResponse;
import com.wanmi.sbc.live.api.response.stream.IMAppResponse;
import com.wanmi.sbc.live.bag.model.root.LiveBag;
import com.wanmi.sbc.live.bag.service.LiveBagService;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.live.redis.RedisCache;
import com.wanmi.sbc.live.redis.RedisService;
import com.wanmi.sbc.live.room.model.root.LiveRoom;
import com.wanmi.sbc.live.room.service.LiveRoomService;
import com.wanmi.sbc.live.stream.service.LiveStreamService;
import com.wanmi.sbc.live.util.TencentImUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>直播服务接口实现</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@RestController
@Validated
@Slf4j
public class LiveStreamController implements LiveStreamProvider {
    @Autowired
    private LiveStreamService liveStreamService;
    @Autowired
    private LiveBagProducerService liveBagProducerService;
    @Autowired
    private LiveBagService liveBagService;
    @Autowired
    private LiveBagLogService liveBagLogService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private RedisService redisService;
    @Autowired
    private LiveRoomService liveRoomService;
    @Override
    public BaseResponse createRoom(@RequestBody LiveStreamAddRequest mobileAddReq) {
        LiveRoom liveRoom = liveRoomService.getInfo(mobileAddReq.getLiveRoomId().longValue());
        if (liveRoom == null) {
            return BaseResponse.error("直播间不存在");
        }
        // 直播记录增加店铺ID
        mobileAddReq.setStoreId(String.valueOf(liveRoom.getStoreId()));

        /**
         * 直播间类型判断
         */
        switch (liveRoom.getRoomType()) {
            // 非自营店铺直播间
            case 0:
                mobileAddReq.setRoomType(0);
                // 非自营直播间，默认为官方已认证
                mobileAddReq.setSysFlag(1);
                break;

            // 自营店铺直播间区分 平台直播间、和普通自营直播间
            case 1:
                // 如果是平台直播间
                if (LiveRoom.SYSFLAG_YES.equals(liveRoom.getSysFlag())) {
                    mobileAddReq.setRoomType(2);  // 平台直播间
                    mobileAddReq.setSysFlag(2);
                }
                else {
                    mobileAddReq.setRoomType(1);  // 自营直播间
                    // 自营直播间，默认为官方已认证
                    mobileAddReq.setSysFlag(1);
                }
                break;

            case 2:
                mobileAddReq.setRoomType(2);
                mobileAddReq.setSysFlag(2);
                break;
        }

        liveStreamService.createLiveRoom(mobileAddReq);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateRoom(LiveStreamAddRequest mobileAddReq) {
        liveStreamService.updateLiveRoom(mobileAddReq);
        return BaseResponse.SUCCESSFUL();
    }


    @Override
    public BaseResponse<IMAppResponse> getTxCloudUserSig(@RequestBody IMAppRequest imAppRequest) {
//        String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + imAppRequest.getUserId());
//        if(Objects.isNull(userSig)){
//            userSig=TencentImUtil.getTxCloudUserSig(imAppRequest.getUserId());
//            redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + imAppRequest.getUserId(),userSig,TencentImUtil.SIGN_CACHE_TIME);
//        }
        String userSig=TencentImUtil.getTxCloudUserSig(imAppRequest.getUserId());
        return BaseResponse.success(new IMAppResponse(userSig));
    }

    @Override
    public BaseResponse sendGroupSystemNotification(@RequestBody IMAppRequest imAppRequest) {
        if(imAppRequest.getType()==7){
            //福袋发送MQ消息
            LiveBag liveBag = liveBagService.getInfo(imAppRequest.getBagId().longValue());
            BagAppRequest bagAppRequest =new BagAppRequest();
            bagAppRequest.setLiveId(imAppRequest.getLiveId());
            bagAppRequest.setTicketStatus(0);
            LiveBagLog liveBagLog=liveBagLogService.selectByBagAndTicketStatus(bagAppRequest);
            if(Objects.nonNull(liveBagLog)){
                Long ticketTime = liveBagLog.getTicketTime().getTime() + (new Double(liveBag.getLotteryTime() * 60 * 1000)).longValue();
                if ((new Date().getTime()) < ticketTime) {
                    return BaseResponse.info("K-333334","请等待上一个福袋开奖再发送！");
                }
            }
            liveStreamService.sendGroupSystemNotification(imAppRequest);
            if(Objects.nonNull(liveBag)){
                Long ticketTime =(new Double(liveBag.getLotteryTime() * 60 * 1000)).longValue();
                log.info("福袋开始发放福袋id:"+liveBag.getLiveBagId());
               liveBagProducerService.sendBag(liveBag.getLiveBagId()+"",ticketTime);
            }
        }else{
            liveStreamService.sendGroupSystemNotification(imAppRequest);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse set(@RequestBody LiveStreamSetRequest streamSetRequest) {
        long entryTime = System.currentTimeMillis();
        if(streamSetRequest.getType()==0){
            redisCache.zadd(CacheKeyConstant.LIVE_ADD_PURCHSE+streamSetRequest.getLiveId(),streamSetRequest.getCustomerId(),0.0);
        }else if(streamSetRequest.getType()==1){
            redisCache.zadd(CacheKeyConstant.LIVE_ONCE_PURCHSE+streamSetRequest.getLiveId(),streamSetRequest.getCustomerId(),0.0);
        }else if(streamSetRequest.getType()==2){
            redisCache.zadd(CacheKeyConstant.LIVE_COUPON_GET+streamSetRequest.getLiveId(),streamSetRequest.getCustomerId(),0.0);
        }else if(streamSetRequest.getType()==3){
         Long increment=redisCache.increment(CacheKeyConstant.LIVE_ROOM_LIKE+streamSetRequest.getLiveId(),1);
            IMAppRequest imAppRequest=new IMAppRequest();
            imAppRequest.setType(5);
            imAppRequest.setLikeNum(increment);
            imAppRequest.setLiveRoomId(streamSetRequest.getLiveId());
            liveStreamService.sendGroupSystemNotification(imAppRequest);
            return BaseResponse.success(increment);
        }else if(streamSetRequest.getType()==4){
            IMAppRequest imAppRequest=new IMAppRequest();
            imAppRequest.setType(6);
            imAppRequest.setLiveRoomId(streamSetRequest.getLiveId());
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            liveStreamService.sendGroupSystemNotification(imAppRequest);

        }else if(streamSetRequest.getType()==5){
            IMAppRequest imAppRequest=new IMAppRequest();
            imAppRequest.setType(9);
            imAppRequest.setLiveRoomId(streamSetRequest.getLiveId());
            redisCache.set(CacheKeyConstant.LIVE_HOST_STATUS+streamSetRequest.getLiveId(),"0",0);
            Map mapMesage = new HashMap();
            mapMesage.put("messageType", 9);
            JSONObject jsonContent=new JSONObject();
            jsonContent.put("liveId",streamSetRequest.getLiveId());
            mapMesage.put("messageContent", jsonContent);
            JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
            imAppRequest.setSystemMessageJson(json.toJSONString());
            liveStreamService.sendGroupSystemNotification(imAppRequest);
        }else if(streamSetRequest.getType()==6){
            IMAppRequest imAppRequest=new IMAppRequest();
            imAppRequest.setType(10);
            imAppRequest.setLiveRoomId(streamSetRequest.getLiveId());
            redisCache.set(CacheKeyConstant.LIVE_HOST_STATUS+streamSetRequest.getLiveId(),"1",0);
            Map mapMesage = new HashMap();
            mapMesage.put("messageType", 10);
            JSONObject jsonContent=new JSONObject();
            jsonContent.put("liveId",streamSetRequest.getLiveId());
            mapMesage.put("messageContent", jsonContent);
            JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
            imAppRequest.setSystemMessageJson(json.toJSONString());
            liveStreamService.sendGroupSystemNotification(imAppRequest);
        }
        long exitTime = System.currentTimeMillis();
        log.info("/liveStream/set总耗时 {}, 参数 {}", (exitTime - entryTime) / 1000d, JSON.toJSONString(streamSetRequest));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse cutoffStream(@RequestBody LiveStreamSetRequest streamSetRequest) {
        if(Objects.nonNull(streamSetRequest.getLiveId())){
            Long ticketTime = 10 * 60 * 1000l;
            liveBagProducerService.sendStream(streamSetRequest.getLiveId()+"",ticketTime);
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新直播间在线人数
     * @param live
     * @return
     */
    @Override
    public BaseResponse updateLiveStreamPeopleNum(LiveStreamVO live) {
        liveStreamService.updateLiveStreamPeopleNum(live);
        return BaseResponse.success(null);
    }

    @Override
    public BaseResponse deleteLiveStream(Integer liveId) {
        liveStreamService.deleteLiveStream(liveId);
        return BaseResponse.success("删除成功");
    }
}
