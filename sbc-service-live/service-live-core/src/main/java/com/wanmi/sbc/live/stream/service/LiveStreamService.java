package com.wanmi.sbc.live.stream.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.live.activity.dao.LiveBagLogMapper;
import com.wanmi.sbc.live.activity.dao.LiveStreamActivityMapper;
import com.wanmi.sbc.live.activity.model.root.LiveBagLog;
import com.wanmi.sbc.live.activity.model.root.LiveStreamActivity;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityListRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsModifyRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomPageRequest;
import com.wanmi.sbc.live.api.request.stream.IMAppRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamAddRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.stream.LiveStreamInfoResponse;
import com.wanmi.sbc.live.api.response.stream.StoreLiveStreamResponse;
import com.wanmi.sbc.live.bag.dao.LiveBagMapper;
import com.wanmi.sbc.live.bag.model.root.LiveBag;
import com.wanmi.sbc.live.bean.vo.LiveHaveGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamLogInfoVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.live.goods.dao.LiveStreamGoodsMapper;
import com.wanmi.sbc.live.goods.model.root.LiveStreamGoods;
import com.wanmi.sbc.live.redis.RedisCache;
import com.wanmi.sbc.live.redis.RedisService;
import com.wanmi.sbc.live.room.model.root.LiveRoom;
import com.wanmi.sbc.live.rule.dao.LiveRuleMapper;
import com.wanmi.sbc.live.rule.model.root.LiveRule;
import com.wanmi.sbc.live.stream.dao.LiveStreamLogInfoMapper;
import com.wanmi.sbc.live.stream.dao.LiveStreamMapper;
import com.wanmi.sbc.live.stream.model.root.LiveStream;
import com.wanmi.sbc.live.stream.model.root.LiveStreamLogInfo;
import com.wanmi.sbc.live.util.TecentCloudUtil;
import com.wanmi.sbc.live.util.TencentImUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;


import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LiveStreamService {
    @Autowired
    private LiveStreamMapper liveStreamMapper;
    @Autowired
    private LiveStreamGoodsMapper goodsMapper;
    @Autowired
    private LiveStreamActivityMapper activityMapper;
    @Autowired
    private LiveBagLogMapper liveBagLogMapper;
    @Autowired
    private LiveBagMapper liveBagMapper;
    @Autowired
    private LiveStreamLogInfoMapper liveStreamLogInfoMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private LiveRuleMapper liveRuleMapper;
    private int[] add=new int[] { 5,5,10,10,10,10,10,10,10,10,10};
    /**
     * 创建直播
     * @param liveStreamVO
     */
    public void createLiveRoom(LiveStreamAddRequest liveStreamVO){
        LiveStreamInfoRequest infoRequest=new LiveStreamInfoRequest();
        infoRequest.setLiveStatus(1);
        infoRequest.setLiveRoomId(liveStreamVO.getLiveRoomId());
        LiveStream liveStream=liveStreamMapper.selectByInfoRequest(infoRequest);
        if(Objects.isNull(liveStream)) {
            try {
                liveStreamVO.setStreamName(RandomUtils.nextInt(1, 999999) + "");
                String pushUrl = TecentCloudUtil.getPushUrl(liveStreamVO.getStreamName());
                String liveUrl = TecentCloudUtil.getPullWbrtcUrl(liveStreamVO.getStreamName(), null);
//                String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//                if (Objects.isNull(userSig)) {
//                    userSig = TencentImUtil.createUsersig();
//                    redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//                }
                String userSig = TencentImUtil.createUsersig();
                String groupId = TencentImUtil.createGroup(liveStreamVO.getRoomName(), userSig);
                liveStreamVO.setGroupId(groupId);
                liveStreamVO.setPushUrl(pushUrl);
                liveStreamVO.setLiveUrl(liveUrl);
                liveStreamVO.setStartTime(new Date());
                liveStreamVO.setLiveStatus(1);
                // 保存店铺ID
                liveStreamVO.setStoreId(liveStreamVO.getStoreId());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            LiveStream convert = KsBeanUtil.convert(liveStreamVO, LiveStream.class);
            liveStreamMapper.insertSelective(convert);
        }
    }


    /**
     * 更新直播间
     * @param liveStreamVO
     */
    public void updateLiveRoom(LiveStreamAddRequest liveStreamVO){
        LiveStream convert = KsBeanUtil.convert(liveStreamVO, LiveStream.class);
        if(Objects.nonNull(convert.getLiveStatus())){
            if (convert.getLiveStatus() == 2) {
                convert.setEndTime(new Date());
//                String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//                if (Objects.isNull(userSig)) {
//                    userSig = TencentImUtil.createUsersig();
//                    redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//                }
                String userSig = TencentImUtil.createUsersig();
                convert.setAddPurchseNumber(redisService.zSize(CacheKeyConstant.LIVE_ADD_PURCHSE+convert.getLiveId()).intValue());
                convert.setOncePurchseNumber(redisService.zSize(CacheKeyConstant.LIVE_ONCE_PURCHSE+convert.getLiveId()).intValue());
                convert.setCouponGetNumber(redisService.zSize(CacheKeyConstant.LIVE_COUPON_GET+convert.getLiveId()).intValue());
                IMAppRequest imAppRequest=new IMAppRequest();
                imAppRequest.setType(3);
                imAppRequest.setLiveRoomId(liveStreamVO.getLiveId());
                this.sendGroupSystemNotification(imAppRequest);
                LiveStreamInfoRequest infoRequest=new LiveStreamInfoRequest();
                infoRequest.setLiveId(convert.getLiveId());
                LiveStream liveStream=liveStreamMapper.selectByInfoRequest(infoRequest);
                if(Objects.nonNull(liveStream)){
                    int onlineNum = TencentImUtil.getOnlineMemberNum(liveStream.getGroupId(), userSig);
                    if (liveStream.getViewerNumber() != null && liveStream.getViewerNumber() > onlineNum) {
                        onlineNum = liveStream.getViewerNumber();
                    }
                    convert.setViewerNumber(onlineNum);
                    TencentImUtil.destroyGroup(liveStream.getGroupId(),userSig);
                }
                this.saveStreamLogInfo(liveStreamVO.getLiveId(),liveStreamVO.getLiveRoomId());//保存商品，优惠劵活动信息
                //设置所有商品为未推送
                goodsMapper.updateAllByLiveRoomId(liveStreamVO.getLiveRoomId());
                //设置所有活动对应优惠卷为未发送
                activityMapper.updateAllByLiveRoomId(liveStreamVO.getLiveRoomId());
            }
        }
        liveStreamMapper.updateByPrimaryKeySelective(convert);
    }



    /**
     * 保存商品，优惠劵活动记录
     */
    public void saveStreamLogInfo(Integer liveId,Integer LiveRoomId){
        //更新直播商品
        LiveStreamLogInfo streamLogInfo=new LiveStreamLogInfo();
        LiveStreamGoodsListRequest liveStreamGoodsListRequest=new LiveStreamGoodsListRequest();
        liveStreamGoodsListRequest.setLiveRoomId(LiveRoomId);
        List<LiveStreamGoods> liveStreamGoodsList=goodsMapper.findListByReq(liveStreamGoodsListRequest);
        String goodsInfoId="";
        for (LiveStreamGoods streamGoods:liveStreamGoodsList){
            goodsInfoId+=streamGoods.getGoodsInfoId()+",";
        }
        streamLogInfo.setLiveId(liveId);
        streamLogInfo.setGoodsInfoIds(goodsInfoId);
        //更新优惠卷活动
        LiveStreamActivityListRequest liveStreamActivityListRequest=new LiveStreamActivityListRequest(LiveRoomId);
        List<LiveStreamActivity> liveStreamActivityList=activityMapper.liveStreamActivityList(liveStreamActivityListRequest);
        String activityIds="";
        for (LiveStreamActivity streamActivity:liveStreamActivityList){
            activityIds+=streamActivity.getActivityId()+",";
        }
        streamLogInfo.setActivityIds(activityIds);
        streamLogInfo.setCreateTime(new Date());
        liveStreamLogInfoMapper.insertSelective(streamLogInfo);
    }


    /**
     * 获取直播分页列表
     * @param pageRequest
     * @return
     */
    public Page<LiveStreamVO> getLiveStreamPage(LiveStreamPageRequest pageRequest){
        List<LiveStream> pageList=liveStreamMapper.pageListByLiveStreamReq(pageRequest);
        int total=liveStreamMapper.pageCountByLiveStreamReq(pageRequest);
        List<LiveStreamVO> liveStreamVOList=new ArrayList<>();
        pageList.forEach(liveStream -> {
            LiveStreamVO vo=new LiveStreamVO();
            BeanUtils.copyProperties(liveStream, vo);
            liveStreamVOList.add(vo);
        });
        pageRequest.setPageNum(pageRequest.getPageNum() / pageRequest.getPageSize());
        return new PageImpl<LiveStreamVO>(liveStreamVOList, pageRequest.getPageable(), total);
    }

    /**
     * 获取直播分页列表
     * @param pageRequest
     * @return
     */
    public Page<LiveStreamVO> liveBroadcastSquare(LiveStreamPageRequest pageRequest){
        List<LiveStream> pageList=liveStreamMapper.liveBroadcastSquare(pageRequest);
        int total=liveStreamMapper.pageCountLiveBroadcastSquare(pageRequest);
        List<LiveStreamVO> liveStreamVOList=new ArrayList<>();
        setLiveUserSig(pageList, liveStreamVOList);
        pageRequest.setPageNum(total / pageRequest.getPageSize());
        return new PageImpl<LiveStreamVO>(liveStreamVOList, pageRequest.getPageable(), total);
    }
    /**
     * 获取直播间
     * @param infoRequest
     * @return
     */
    public LiveStreamVO getLiveStreamInfo(LiveStreamInfoRequest infoRequest){
        LiveStream liveStream=liveStreamMapper.selectByInfoRequest(infoRequest);
        LiveStreamVO convert = KsBeanUtil.convert(liveStream, LiveStreamVO.class);
        return convert;
    }


    /**
     * 获取直播间
     * @param infoRequest
     * @return
     */
    public LiveStreamVO selectBySysRequest(LiveStreamInfoRequest infoRequest){
        LiveStream liveStream=liveStreamMapper.selectBySysRequest(infoRequest);
        if (liveStream == null) {
            return null;
        }
        LiveStreamVO convert = KsBeanUtil.convert(liveStream, LiveStreamVO.class);
        if(liveStream.getLiveStatus()==1){
//            String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//            if (Objects.nonNull(userSig)) {
//                userSig = TencentImUtil.createUsersig();
//                redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//            }
            convert.setViewerNumber(getBackOnlineNum(liveStream.getLiveRoomId(),liveStream.getGroupId()));
        }else{
            convert.setViewerNumber(0);
        }
        String num=redisService.getString(CacheKeyConstant.LIVE_ROOM_LIKE+liveStream.getLiveId());
        convert.setLikeNum(getBackLikeNum(liveStream.getLiveRoomId(),num==null?0:Integer.parseInt(num)));
        return convert;
    }

    /**
     * 获取商品对应直播间
     * @param infoRequest
     * @return
     */
    public LiveHaveGoodsVO findLiveByGoodsInfoId(LiveStreamInfoRequest infoRequest){
        LiveHaveGoodsVO liveHaveGoodsVO=new LiveHaveGoodsVO();
        List<LiveHaveGoodsVO> liveStreamList=liveStreamMapper.findLiveByGoodsInfoId(infoRequest.getGoodsInfoId());
        if(liveStreamList.size()>0){
            liveHaveGoodsVO.setIsHaveLive(1);
            for (LiveHaveGoodsVO liveStream:liveStreamList){
              if(liveStream.getExplainFlag()==1){//正在讲解商品优先
                  liveHaveGoodsVO.setLiveRoomId(liveStream.getLiveRoomId());
                  liveHaveGoodsVO.setLiveId(liveStream.getLiveId());
                  return liveHaveGoodsVO;
              }else if(liveStream.getSysFlag()==2){//平台直播商品
                  liveHaveGoodsVO.setLiveRoomId(liveStream.getLiveRoomId());
                  liveHaveGoodsVO.setLiveId(liveStream.getLiveId());
                  return liveHaveGoodsVO;
              }else{
                  liveHaveGoodsVO.setLiveRoomId(liveStream.getLiveRoomId());
                  liveHaveGoodsVO.setLiveId(liveStream.getLiveId());
                  return liveHaveGoodsVO;
              }
            }
        }else{
            liveHaveGoodsVO.setIsHaveLive(0);
        }
        return liveHaveGoodsVO;
    }


    /**
     * 发送商品、优惠卷、通知IM系统消息
     * @param imAppRequest
     */
    @Transactional
    public void sendGroupSystemNotification(IMAppRequest imAppRequest){
        long entryTime = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LiveStreamInfoRequest infoRequest=new LiveStreamInfoRequest();
        //5点赞人数 6 在线人数 处理
        if(imAppRequest.getType() == 5 || imAppRequest.getType() == 6 || imAppRequest.getType() == 3 || imAppRequest.getType() == 9 || imAppRequest.getType() == 10){
            infoRequest.setLiveId(imAppRequest.getLiveRoomId());
        }else{
            infoRequest.setLiveStatus(1);
            infoRequest.setLiveRoomId(imAppRequest.getLiveRoomId());
        }
        LiveStream liveStream=liveStreamMapper.selectByInfoRequest(infoRequest);
        stopWatch.stop();
        log.info("sendGroupSystemNotification查询liveStream耗时 {}", stopWatch.getTotalTimeSeconds());
        if(Objects.nonNull(liveStream)) {
//            String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//            if (Objects.isNull(userSig)) {
//                userSig = TencentImUtil.createUsersig();
//                redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//            }
            String userSig = TencentImUtil.createUsersig();
            if (imAppRequest.getType() == 1) {
                //发送商品
                LiveStreamGoodsModifyRequest liveStreamGoodsModifyRequest=new LiveStreamGoodsModifyRequest();
                liveStreamGoodsModifyRequest.setGoodsInfoId(imAppRequest.getGoodsInfoId());
                liveStreamGoodsModifyRequest.setLiveRoomId(imAppRequest.getLiveRoomId());
                goodsMapper.updateAllByGoodsInfoId(liveStreamGoodsModifyRequest);
                liveStream.setGoodsInfoId(imAppRequest.getGoodsInfoId());
                LiveStreamGoods record = new LiveStreamGoods();
                record.setGoodsInfoId(imAppRequest.getGoodsInfoId());
                record.setLiveId(imAppRequest.getLiveRoomId());
                record.setExplainFlag(1);
                goodsMapper.updateByPrimaryKeySelective(record);
            }else if (imAppRequest.getType() == 2) {
                //发送优惠劵
                liveStream.setActivityId(imAppRequest.getActivityId());
                liveStream.setCouponId(imAppRequest.getCouponId());
                LiveStreamActivity streamActivity = new LiveStreamActivity();
                streamActivity.setActivityId(imAppRequest.getActivityId());
                streamActivity.setLiveRoomId(imAppRequest.getLiveRoomId());
                LiveStreamActivityInfoRequest activityInfoRequest=new LiveStreamActivityInfoRequest();
                activityInfoRequest.setActivityId(imAppRequest.getActivityId());
                activityInfoRequest.setLiveId(imAppRequest.getLiveRoomId());
                LiveStreamActivity liveStreamActivity=activityMapper.selectByPrimaryKey(activityInfoRequest);
                if(Objects.nonNull(liveStreamActivity)) {
                    if (liveStreamActivity.getCouponId() != null) {
                        if (!liveStreamActivity.getCouponId().contains(imAppRequest.getCouponId())) {
                            streamActivity.setCouponId(liveStreamActivity.getCouponId() + "," + imAppRequest.getCouponId());
                        }
                    } else {
                        streamActivity.setCouponId(imAppRequest.getCouponId());
                    }
                }
                activityMapper.updateByPrimaryKeySelective(streamActivity);
            }else if (imAppRequest.getType() == 3) {
                //发送直播结束时间
                Map mapMesage = new HashMap();
                mapMesage.put("messageType", 3);
                JSONObject jsonContent=new JSONObject();
                jsonContent.put("liveId",liveStream.getLiveId());
                long time= new Date().getTime()- liveStream.getStartTime().getTime();
                jsonContent.put("liveTime",timeStampToDhms(time));
                mapMesage.put("messageContent", jsonContent);
                JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
                imAppRequest.setSystemMessageJson(json.toJSONString());
            }else if (imAppRequest.getType() == 5) {
                //发送点赞人数
                Map mapMesage = new HashMap();
                mapMesage.put("messageType", 5);
                JSONObject jsonContent=new JSONObject();
                jsonContent.put("liveId",liveStream.getLiveId());
                //jsonContent.put("likeNum",imAppRequest.getLikeNum());
                jsonContent.put("likeNum",getBackLikeNum(liveStream.getLiveRoomId(),imAppRequest.getLikeNum().intValue()));
                mapMesage.put("messageContent", jsonContent);
                JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
                imAppRequest.setSystemMessageJson(json.toJSONString());
            }else if (imAppRequest.getType() == 6) {
                //发送在线人数
                Map mapMesage = new HashMap();
                mapMesage.put("messageType", 6);
                JSONObject jsonContent=new JSONObject();
                jsonContent.put("liveId",liveStream.getLiveId());
                //jsonContent.put("onlineNum",TencentImUtil.getOnlineMemberNum(liveStream.getGroupId(), userSig));
                jsonContent.put("onlineNum",getBackOnlineNum(liveStream.getLiveRoomId(),liveStream.getGroupId()));
                mapMesage.put("messageContent", jsonContent);
                JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
                imAppRequest.setSystemMessageJson(json.toJSONString());
            }else if (imAppRequest.getType() == 7) {
                //发送福袋
                //添加福袋发放记录
                LiveBagLog liveBagLog=new LiveBagLog();
                liveBagLog.setBagId(imAppRequest.getBagId());
                liveBagLog.setLiveId(liveStream.getLiveId());
                liveBagLog.setTicketTime(new Date());
                liveBagLog.setCreateTime(new Date());
                LiveBag liveBag=liveBagMapper.getInfo(imAppRequest.getBagId().longValue());
                liveBag.setProvideStatus(1l);
                liveBag.setProvideNums(liveBag.getProvideNums()+1);//福袋发放次数加1
                liveBagMapper.modify(liveBag);
                liveBagLogMapper.insertSelective(liveBagLog);
                liveStream.setBagId(imAppRequest.getBagId());
            }else if (imAppRequest.getType() == 4) {
                //取消发送优惠劵
                liveStream.setActivityId("");
                liveStream.setCouponId("");
                LiveStreamActivityInfoRequest activityInfoRequest = new LiveStreamActivityInfoRequest();
                activityInfoRequest.setActivityId(imAppRequest.getActivityId());
                activityInfoRequest.setLiveId(imAppRequest.getLiveRoomId());
                LiveStreamActivity streamActivity = activityMapper.selectByPrimaryKey(activityInfoRequest);
                if (Objects.nonNull(streamActivity)) {
                    if (Objects.nonNull(streamActivity.getCouponId()) && Objects.nonNull(imAppRequest.getCouponId())) {
                        streamActivity.setCouponId(streamActivity.getCouponId().replace(imAppRequest.getCouponId(), ""));
                        activityMapper.updateByPrimaryKeySelective(streamActivity);
                    }
                }
            }
                liveStreamMapper.updateByPrimaryKeySelective(liveStream);
                log.info("userSig-----------" + userSig);

                stopWatch.start();
                TencentImUtil.sendGroupSystemNotification(liveStream.getGroupId(), imAppRequest.getSystemMessageJson(), userSig);
                stopWatch.stop();
                log.info("sendGroupSystemNotification调用腾讯发送群通知耗时 {}", stopWatch.getTotalTimeSeconds());

        }else{
            log.info("暂无直播-----------");
        }
        long exitTime = System.currentTimeMillis();
        log.info("直播群组消息发送方法sendGroupSystemNotification耗时 {}", (exitTime - entryTime) / 1000d);
    }

    public LiveStreamLogInfoVO getLiveStreamLogInfo(Integer liveId){
        LiveStreamLogInfo liveStreamLogInfo=liveStreamLogInfoMapper.selectByLiveId(liveId);
        LiveStreamLogInfoVO convert = KsBeanUtil.convert(liveStreamLogInfo, LiveStreamLogInfoVO.class);
        return convert;
    }

    // 毫秒时间戳转换为日、时、分、秒
    public static String timeStampToDhms(long milliseconds) {
        long day = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        StringBuilder sb = new StringBuilder();
        if (day != 0) {
            sb.append(day + "天");
        }
        sb.append(hours + "小时");
        sb.append(minutes + "分钟");
        return sb.toString();
    }


    /**
     * 规则获取在线人数
     * @param groupId
     * @return
     */
    public int getBackOnlineNum(Integer liveRoomId,String groupId){
        LiveRule liveRule=null;
        if(redisService.hasKey(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_COE+liveRoomId+":1")&&
           redisService.hasKey(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_FIX+liveRoomId+":1")){
            String coe=redisService.getString(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_COE+liveRoomId+":1");
            String fixed=redisService.getString(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_FIX+liveRoomId+":1");
            if(Objects.isNull(coe)&&Objects.isNull(fixed)){
                liveRule=liveRuleMapper.selectByType(1,liveRoomId);
            }else{
                liveRule=new LiveRule();
                liveRule.setCoefficient(coe);
                liveRule.setFixed(Integer.parseInt(fixed));
            }
        }else{
            liveRule=liveRuleMapper.selectByType(1,liveRoomId);
        }
//        String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//        if (Objects.isNull(userSig)) {
//            userSig = TencentImUtil.createUsersig();
//            redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//        }
        String userSig = TencentImUtil.createUsersig();
        int onlineMemberNum=TencentImUtil.getOnlineMemberNum(groupId, userSig);
        if(Objects.isNull(liveRule)){
            return onlineMemberNum;
        }
        String[] rule=liveRule.getCoefficient().split(",");
        if(onlineMemberNum>10&&onlineMemberNum<=100){
          int whole=onlineMemberNum/10;
          int v=onlineMemberNum%10;
            if(v==0){
                v=10;
                whole=whole-1;
            }
            int wholeAll=0;
            for (int i=0;i<=whole;i++){
                wholeAll+=add[i]*(Integer.parseInt(rule[i]));
            }

            if(wholeAll==0 && liveRule.getFixed()==0){
                return onlineMemberNum;
            }
          int k=Integer.parseInt(rule[whole+1]);
          onlineMemberNum=wholeAll+v*k+liveRule.getFixed();
        }else if(onlineMemberNum>5&&onlineMemberNum<=10) {
            int k=Integer.parseInt(rule[1]);
            if(k==0 && liveRule.getFixed()==0){
                return onlineMemberNum;
            }
            onlineMemberNum=add[1]+(onlineMemberNum-5)*k+liveRule.getFixed();
        }else if(onlineMemberNum>100){
            int wholeAll=0;
            for (int i=0;i<11;i++){
                wholeAll+=add[i]*(Integer.parseInt(rule[i]));
            }
            if(wholeAll==0 && liveRule.getFixed()==0){
                return onlineMemberNum;
            }
            int k=Integer.parseInt(rule[11]);
            onlineMemberNum=wholeAll+(onlineMemberNum-100)*k+liveRule.getFixed();
        }else if(onlineMemberNum>=1 && onlineMemberNum<=5){
            onlineMemberNum=onlineMemberNum+liveRule.getFixed();
        }
        return onlineMemberNum;
    }

    /**
     * 获取规则设置点赞数量
     * @param likeNum
     * @return
     */
    public int getBackLikeNum(Integer liveRoomId,int likeNum){
        LiveRule liveRule=null;
        if(redisService.hasKey(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_COE+liveRoomId+":2")&&
                redisService.hasKey(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_FIX+liveRoomId+":2")){
            String coe=redisService.getString(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_COE+liveRoomId+":2");
            String fixed=redisService.getString(com.wanmi.sbc.live.redis.CacheKeyConstant.LIVE_RULE_FIX+liveRoomId+":2");
            if(Objects.isNull(coe)&&Objects.isNull(fixed)){
                liveRule=liveRuleMapper.selectByType(2,liveRoomId);
            }else{
                liveRule=new LiveRule();
                liveRule.setCoefficient(coe);
                liveRule.setFixed(Integer.parseInt(fixed));
            }
        }else{
            liveRule=liveRuleMapper.selectByType(2,liveRoomId);
        }
        if(Objects.isNull(liveRule)){
            return likeNum;
        }
        String[] rule=liveRule.getCoefficient().split(",");
        if(likeNum>10&&likeNum<=100){
            int whole=likeNum/10;
            int v=likeNum%10;
            if(v==0){
                v=10;
                whole=whole-1;
            }
            int wholeAll=0;
            for (int i=0;i<=whole;i++){
                wholeAll+=add[i]*(Integer.parseInt(rule[i]));
            }
            if(wholeAll==0 && liveRule.getFixed()==0){
                return likeNum;
            }
            int k=Integer.parseInt(rule[whole+1]);
            likeNum=wholeAll+v*k+liveRule.getFixed();
        }else if(likeNum>5&&likeNum<=10) {
            int k=Integer.parseInt(rule[1]);
            if(k==0 && liveRule.getFixed()==0){
                return likeNum;
            }
            likeNum=add[0]+(likeNum-5)*k+liveRule.getFixed();
        }else if(likeNum>100){
            int wholeAll=0;
            for (int i=0;i<11;i++){
                wholeAll+=add[i]*(Integer.parseInt(rule[i]));
            }
            if(wholeAll==0 && liveRule.getFixed()==0){
                return likeNum;
            }
            int k=Integer.parseInt(rule[11]);
            likeNum=wholeAll+(likeNum-100)*k+liveRule.getFixed();
        }else if(likeNum>=3 && likeNum<=5){
            likeNum=likeNum+liveRule.getFixed();
        }
        return likeNum;
    }

    public Page<LiveStreamVO> getStoreLiveList(LiveStreamPageRequest requestParam) {
        List<LiveStream> pageList=liveStreamMapper.getStoreLiveList(requestParam);
        Integer total=liveStreamMapper.countStoreLiveList(requestParam);
        if (total == null) {
            total = 0;
        }
        List<LiveStreamVO> liveStreamVOList=new ArrayList<>();
        setLiveUserSig(pageList, liveStreamVOList);
        requestParam.setPageNum(total / requestParam.getPageSize());
        return new PageImpl<LiveStreamVO>(liveStreamVOList, requestParam.getPageable(), total);
    }

    private void setLiveUserSig(List<LiveStream> pageList, List<LiveStreamVO> liveStreamVOList) {
        pageList.forEach(liveStream -> {
            LiveStreamVO vo=new LiveStreamVO();
            BeanUtils.copyProperties(liveStream, vo);
            if(liveStream.getLiveStatus()==1){
//                String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//                if (Objects.isNull(userSig)) {
//                    userSig = TencentImUtil.createUsersig();
//                    redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//                }
                vo.setViewerNumber(getBackOnlineNum(liveStream.getLiveRoomId(),liveStream.getGroupId()));
            }else{
                vo.setViewerNumber(0);
            }
            String num=redisService.getString(CacheKeyConstant.LIVE_ROOM_LIKE+liveStream.getLiveId());
            vo.setLikeNum(getBackLikeNum(liveStream.getLiveRoomId(),num==null?0:Integer.parseInt(num)));
            liveStreamVOList.add(vo);
        });
    }

    /**
     * 更新直播间在线人数
     * @param live
     * @return
     */
    public void updateLiveStreamPeopleNum(LiveStreamVO live) {
//        String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//        if (Objects.isNull(userSig)) {
//            userSig = TencentImUtil.createUsersig();
//            redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//        }
        String userSig = TencentImUtil.createUsersig();
        int onlineNum = TencentImUtil.getOnlineMemberNum(live.getGroupId(), userSig);
        if (live.getViewerNumber() != null && live.getViewerNumber() > onlineNum) {
            onlineNum = live.getViewerNumber();
        }
        live.setViewerNumber(onlineNum);
        LiveStream liveStream = new LiveStream();
        liveStream.setLiveId(live.getLiveId());
        liveStream.setViewerNumber(onlineNum);
        liveStreamMapper.updateByPrimaryKeySelective(liveStream);
    }

    /**
     * 查询正在直播中的店铺ID
     * @return
     */
    public List<Long> getLiveStoreIds() {
        return liveStreamMapper.selectLiveStoreIds();
    }
    public Map<Long, List<StoreLiveStreamResponse>> getLiveStoreInfo() {
        List<LiveStream> liveList = liveStreamMapper.selectLivingRomeList();
        Map<Long, List<StoreLiveStreamResponse>> map = new HashMap<>();
        liveList.stream().forEach(live -> {
            if (StringUtils.isEmpty(live.getStoreId())) {
                return;
            }
            List<StoreLiveStreamResponse> tmpList = map.get(live.getStoreId());
            try {
                Long storeId = Long.parseLong(live.getStoreId());
                if (tmpList == null) {
                    tmpList = new ArrayList<>();
                    map.put(storeId, tmpList);
                }
                StoreLiveStreamResponse response = KsBeanUtil.convert(live, StoreLiveStreamResponse.class);
                tmpList.add(response);
            }
            catch (Exception e) {

            }
        });
        return map;
    }

    public void deleteLiveStream(Integer liveId) {
        liveStreamMapper.deleteByLiveId(liveId);
    }

    public LiveStreamVO getLiveStreamEditInfoByRoomId(Integer liveRoomId) {
        LiveStream liveStream = liveStreamMapper.getLiveStreamEditInfoByRoomId(liveRoomId);
        if (liveStream == null) {
            return new LiveStreamVO();
        }
        return KsBeanUtil.convert(liveStream, LiveStreamVO.class);
    }

    public List<Long> getLiveRoomIdByLiveTime(String startTime, String endTime) {
        return liveStreamMapper.selectLiveRoomIdByLiveTime(startTime, endTime);
    }

    public List<LiveStream> getByLiveTime(LiveRoomPageRequest pageRequest) {
        return liveStreamMapper.selectByLiveTime(pageRequest.getStoreId(), pageRequest.getLiveRoomIdList(), pageRequest.getStartTime(), pageRequest.getEndTime());
    }

    public List<LiveStreamVO> getDetailByLiveTime(String startTime, String endTime) {
        List<LiveStream> liveStreamList = liveStreamMapper.selectDetailByLiveTime(startTime, endTime);
        return KsBeanUtil.convert(liveStreamList, LiveStreamVO.class);
    }

    public List<LiveStreamVO> getLastLiveByRoomIds(List<Long> roomIdList) {
        if (ObjectUtils.isEmpty(roomIdList)) {
            return new ArrayList<>();
        }
        List<LiveStream> liveStreamList = liveStreamMapper.selectLastLiveByRoomIds(roomIdList);
        return KsBeanUtil.convert(liveStreamList, LiveStreamVO.class);
    }
}
