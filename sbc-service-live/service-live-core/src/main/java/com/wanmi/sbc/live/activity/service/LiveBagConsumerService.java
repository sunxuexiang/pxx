package com.wanmi.sbc.live.activity.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerListByConditionRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerListByConditionResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.live.activity.model.root.LiveBagLog;
import com.wanmi.sbc.live.api.constant.JmsBagDestinationConstants;
import com.wanmi.sbc.live.api.request.activity.LiveBagInfoRequest;
import com.wanmi.sbc.live.api.request.stream.IMAppRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamAddRequest;
import com.wanmi.sbc.live.api.response.activity.LiveBagLogInfoResponse;
import com.wanmi.sbc.live.bag.model.root.LiveBag;
import com.wanmi.sbc.live.bag.service.LiveBagService;
import com.wanmi.sbc.live.redis.RedisCache;
import com.wanmi.sbc.live.redis.RedisService;
import com.wanmi.sbc.live.stream.dao.LiveStreamMapper;
import com.wanmi.sbc.live.stream.model.root.LiveStream;
import com.wanmi.sbc.live.stream.service.LiveStreamService;
import com.wanmi.sbc.live.util.TencentImUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponFetchRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoByIdRequest;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@EnableBinding(LiveBagSink.class)
public class LiveBagConsumerService {
    @Autowired
    private LiveBagService liveBagService;
    @Autowired
    private LiveBagLogService liveBagLogService;
    @Autowired
    private LiveStreamService liveStreamService;
    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;
    @Autowired
    private CouponCodeProvider couponCodeProvider;
    @Autowired
    private CustomerQueryProvider customerQueryProvider;
    @Autowired
    private LiveStreamMapper liveStreamMapper;
    @Autowired
    private RedisCache redisCache;
    /**
     * 发送福袋
     * @param bagId
     */
    @StreamListener(JmsBagDestinationConstants.Q_LIVE_SERVICE_BAG_SEND_CONSUMER)
    @LcnTransaction
    public void sendBag(String bagId) {
        if(Objects.nonNull(bagId)) {
            log.info("福袋id：{},福袋发送MQ消息，开始运行处理",bagId);
            int memberSize=redisCache.smembers(CacheKeyConstant.LIVE_BAG_JOIN + bagId).size();
            LiveBag liveBag = liveBagService.getInfo(Long.parseLong(bagId));
            if(memberSize>=liveBag.getWinningNumber()){
                memberSize=liveBag.getWinningNumber().intValue();
            }
            Set<String> setCustomerIds = redisCache.distinctRandomMembers(CacheKeyConstant.LIVE_BAG_JOIN + bagId, memberSize);//随机获取元素个数
            List<String> customerIds=new ArrayList<String>(setCustomerIds);
            List<CustomerVO> customerVOList=new ArrayList<>();
            if(customerIds.size()>0){
                CustomerListByConditionRequest customerListByConditionRequest=new CustomerListByConditionRequest();
                customerListByConditionRequest.setCustomerIds(customerIds);
                CustomerListByConditionResponse customerList=customerQueryProvider.listCustomerByCondition(customerListByConditionRequest).getContext();
                if(Objects.nonNull(customerList)){
                    customerVOList=customerList.getCustomerVOList();
                }
            }
            List<Map<String,String>> customerDetail=new ArrayList<>();
            Map<String,String> mapCustomer=new HashMap<>();
            LiveBagLog liveBagLog = liveBagLogService.getLiveBagInfo(Integer.parseInt(bagId));
            if (customerIds.size() > 0) {
                if(customerVOList.size()>0) {
                    customerVOList.forEach(customerVO -> {
                        mapCustomer.put(customerVO.getCustomerId(),customerVO.getCustomerAccount());
                        Map<String,String> mapCustomerDetail=new HashMap<>();
                        mapCustomerDetail.put("headImg",customerVO.getHeadImg());
                        mapCustomerDetail.put("customerName",customerVO.getCustomerAccount());
                        customerDetail.add(mapCustomerDetail);
                    });
                }
            }
            //发送优惠劵
            String customers="";
            String customerIdAlls="";
            for (String customerId:customerIds){
                CouponFetchRequest request = new CouponFetchRequest();
                request.setCouponActivityId(liveBag.getActivityId());
                request.setCouponInfoId(liveBag.getCouponId());
                request.setCustomerId(customerId);
                customerIdAlls=customerIdAlls+(customerId)+",";
                try {
                    BaseResponse baseResponse = couponCodeProvider.fetch(request);
                    if (baseResponse.getCode().equals("K-000001")) {
                        String customerAccount = mapCustomer.get(customerId) + "(未发卷)";
                        customers=customers+customerAccount+",";
                    } else {
                        customers=customers+(mapCustomer.get(customerId))+",";
                    }
                }catch (Exception e){
                    String customerAccount = mapCustomer.get(customerId) + "(未发卷)";
                    customers=customers+(customerAccount)+",";
                }
            }
            liveBagLog.setJoinNum(redisCache.smembers(CacheKeyConstant.LIVE_BAG_JOIN + bagId).size());
            liveBagLog.setTicketStatus(1);
            liveBagLog.setCustomerAccounts(customers);
            liveBagLog.setCustomerIds(customerIdAlls);
            liveBagLogService.updateLiveBagLog(liveBagLog);
            this.sendMessage(Integer.parseInt(bagId),liveBag.getLiveRoomId().intValue(),liveBagLog.getJoinNum(),customerDetail);
            redisCache.delete(CacheKeyConstant.LIVE_BAG_JOIN + bagId);
            log.info("福袋id：{},福袋发送完成",bagId);
        }
    }

    public void sendMessage(Integer bagId,Integer liveRoomId,Integer joinNum,List<Map<String,String>> customerDetail) {
        IMAppRequest imAppRequest=new IMAppRequest();
        imAppRequest.setLiveRoomId(liveRoomId);
        imAppRequest.setType(8);
        Map mapMesage = new HashMap();
        //发送福袋开奖信息
        mapMesage.put("messageType", 8);
        LiveBagInfoRequest bagInfoRequest=new LiveBagInfoRequest();
        bagInfoRequest.setBagId(bagId);
        LiveBagLogInfoResponse liveBagInfoResponse=new LiveBagLogInfoResponse();
        LiveBag liveBag = liveBagService.getInfo(bagInfoRequest.getBagId().longValue());
        if(Objects.nonNull(liveBag)) {
            liveBagInfoResponse.setBagId(liveBag.getLiveBagId());
            liveBagInfoResponse.setBagName(liveBag.getBagName());
            liveBagInfoResponse.setWinningNumber(new Long(customerDetail.size()));
            liveBagInfoResponse.setSpecifyContent(liveBag.getJoinContent());
            liveBagInfoResponse.setLotteryTime(liveBag.getLotteryTime());
            LiveBagLog liveBagLog = liveBagLogService.getLiveBagInfo(bagInfoRequest.getBagId());
            if (liveBagLog != null) {
                //福袋是否开奖
                if (liveBagLog.getTicketStatus() == 1) {
                    liveBagInfoResponse.setLotteryStatus(2);
                    if(Objects.nonNull(liveBagLog.getCustomerIds())){
                        liveBagInfoResponse.setCustomerIds(liveBagLog.getCustomerIds());
                    }else{
                        liveBagInfoResponse.setCustomerIds("");
                    }
                    liveBagInfoResponse.setJoinNum(joinNum);
                } else {
                    liveBagInfoResponse.setLotteryStatus(0);
                    liveBagInfoResponse.setIsWin(0);
                    Long ticketTime = liveBagLog.getTicketTime().getTime() + (new Double(liveBag.getLotteryTime() * 60 * 1000)).longValue();
                    if ((new Date().getTime()) < ticketTime) {
                        liveBagInfoResponse.setLotteryStatus(1);
                        liveBagInfoResponse.setCountdown((ticketTime - (new Date().getTime())));//开奖中设置倒计时秒
                    } else {
                        liveBagInfoResponse.setLotteryStatus(2);
                        liveBagInfoResponse.setCountdown(0l);
                    }
                    if(liveBagInfoResponse.getJoinNum()>0) {
                        Boolean isJoin = redisCache.sismember(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId(), bagInfoRequest.getCustomerAccount());
                        if (isJoin) {
                            liveBagInfoResponse.setIsJoin(1);
                        } else {
                            liveBagInfoResponse.setIsJoin(0);
                        }
                    }else {
                        liveBagInfoResponse.setIsJoin(0);
                    }
                }

            }
            if(Objects.nonNull(liveBag.getCouponId())) {
                CouponInfoVO couponInfoVO = couponInfoQueryProvider.getById(CouponInfoByIdRequest.builder().couponId(liveBag.getCouponId()).build())
                        .getContext();
                if (Objects.nonNull(couponInfoVO)) {
                    liveBagInfoResponse.setIsHaveCoupon(1);
                    liveBagInfoResponse.setCouponId(couponInfoVO.getCouponId());
                    liveBagInfoResponse.setCouponName(couponInfoVO.getCouponName());
                    liveBagInfoResponse.setDenomination(couponInfoVO.getDenomination());
                    liveBagInfoResponse.setCouponDesc(couponInfoVO.getCouponDesc());
                    liveBagInfoResponse.setFullBuyPrice(couponInfoVO.getFullBuyPrice());
                    liveBagInfoResponse.setFullBuyType(couponInfoVO.getFullBuyType().toValue());
                }else{
                    liveBagInfoResponse.setIsHaveCoupon(0);
                }
            }
        }
        liveBagInfoResponse.setCustomerDetail(customerDetail);
        mapMesage.put("liveBagLogInfoResponse", liveBagInfoResponse);
        JSONObject json = (JSONObject) JSON.toJSON(mapMesage);
        imAppRequest.setSystemMessageJson(json.toJSONString());
        liveStreamService.sendGroupSystemNotification(imAppRequest);
    }


    /**
     * 断流处理
     * @param liveId
     */
    @StreamListener(JmsBagDestinationConstants.Q_LIVE_SERVICE_STREAM_SEND_CONSUMER)
    @LcnTransaction
    public void sendStream(String liveId) {
        if(Objects.nonNull(liveId)) {
            log.info("直播记录id：{},断流收到MQ消息，开始运行处理", liveId);
            LiveStream liveStream=liveStreamMapper.selectByPrimaryKey(Integer.parseInt(liveId));
            if(liveStream.getEventType()==0){
                long time=new Date().getTime()-liveStream.getEventTime();
                if(time>=10 * 60 * 1000){
                    liveStream.setErrmsg("断流时间超过限制，强制关闭直播！");
                    liveStream.setUpdateTime(LocalDateTime.now());
                    liveStream.setLiveStatus(2);
                    LiveStreamAddRequest mobileAddReq=new LiveStreamAddRequest();
                    mobileAddReq.setLiveStatus(2);
                    mobileAddReq.setLiveId(liveStream.getLiveId());
                    mobileAddReq.setLiveRoomId(liveStream.getLiveRoomId());
                    liveStreamService.updateLiveRoom(mobileAddReq);
                    /**
                     * zhouzhenguo  20230721
                     *  为了避免统计的在线人数被覆盖，把字段设置为null，不更新数据库中这几个字段
                     */
                    liveStream.setViewerNumber(null);
                    liveStream.setAddPurchseNumber(null);
                    liveStream.setOncePurchseNumber(null);
                    liveStream.setCouponGetNumber(null);
                    liveStreamMapper.updateByPrimaryKeySelective(liveStream);
                }
            }
        }
    }
}
