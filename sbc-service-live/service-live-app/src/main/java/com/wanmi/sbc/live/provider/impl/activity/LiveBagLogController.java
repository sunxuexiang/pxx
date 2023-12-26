package com.wanmi.sbc.live.provider.impl.activity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.live.activity.model.root.LiveBagLog;
import com.wanmi.sbc.live.activity.service.LiveBagConsumerService;
import com.wanmi.sbc.live.activity.service.LiveBagLogService;
import com.wanmi.sbc.live.api.provider.activity.LiveBagLogProvider;
import com.wanmi.sbc.live.api.request.activity.LiveBagInfoRequest;
import com.wanmi.sbc.live.api.request.stream.BagAppRequest;
import com.wanmi.sbc.live.api.response.activity.LiveBagLogInfoResponse;
import com.wanmi.sbc.live.api.response.stream.BagAppResponse;
import com.wanmi.sbc.live.bag.model.root.LiveBag;
import com.wanmi.sbc.live.bag.service.LiveBagService;
import com.wanmi.sbc.live.redis.RedisCache;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoByIdRequest;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;
@RestController
@Validated
public class LiveBagLogController implements LiveBagLogProvider {

    @Autowired
    private LiveBagService liveBagService;
    @Autowired
    private LiveBagLogService liveBagLogService;
    @Autowired
    private LiveBagConsumerService liveBagConsumerService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;

    @Override
    public BaseResponse joinBag(@RequestBody BagAppRequest mobileAddReq) {
        redisCache.sadd(CacheKeyConstant.LIVE_BAG_JOIN+mobileAddReq.getBagId(),mobileAddReq.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<BagAppResponse> openBag(@RequestBody BagAppRequest mobileAddReq) {
        BagAppResponse bagAppResponse=new BagAppResponse();
        /*LiveBag liveBag = liveBagService.getInfo(mobileAddReq.getBagId().longValue());
        List<String> customerIds=redisCache.randomMembers(CacheKeyConstant.LIVE_BAG_JOIN+mobileAddReq.getBagId(),liveBag.getWinningNumber());//随机获取元素个数
        LiveBagLog liveBagLog = liveBagLogService.getLiveBagInfo(mobileAddReq.getBagId());
        if(customerIds.size()>0){
            liveBagLog.setCustomerAccounts(customerIds.toString());
            liveBagLogService.updateLiveBagLog(liveBagLog);
        }
        bagAppResponse.setCustomerIds(customerIds);*/
        liveBagConsumerService.sendBag(mobileAddReq.getBagId()+"");
        return BaseResponse.success(bagAppResponse);
    }

    @Override
    public BaseResponse<LiveBagLogInfoResponse> getBagInfo(@RequestBody @Valid LiveBagInfoRequest bagInfoRequest) {
        LiveBagLogInfoResponse liveBagInfoResponse=new LiveBagLogInfoResponse();
        LiveBag liveBag = liveBagService.getInfo(bagInfoRequest.getBagId().longValue());
        if(Objects.nonNull(liveBag)) {
            liveBagInfoResponse.setBagId(liveBag.getLiveBagId());
            liveBagInfoResponse.setBagName(liveBag.getBagName());
            liveBagInfoResponse.setWinningNumber(liveBag.getWinningNumber());
            liveBagInfoResponse.setSpecifyContent(liveBag.getJoinContent());
            liveBagInfoResponse.setLotteryTime(liveBag.getLotteryTime());
            LiveBagLog liveBagLog = liveBagLogService.getLiveBagInfo(bagInfoRequest.getBagId());
            if (liveBagLog != null) {
                //福袋是否开奖
                if (liveBagLog.getTicketStatus() == 1) {
                    liveBagInfoResponse.setLotteryStatus(2);
                    if(Objects.nonNull(liveBagLog.getCustomerIds())){
                        if (liveBagLog.getCustomerIds().contains(bagInfoRequest.getCustomerId())) {
                            liveBagInfoResponse.setIsWin(1);
                        } else {
                            liveBagInfoResponse.setIsWin(0);
                        }
                    }
                    liveBagInfoResponse.setCustomerIds(liveBagLog.getCustomerIds());
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
                }

            }
            if(Objects.nonNull(redisCache.smembers(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId()))){
                if(redisCache.smembers(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId()).size()>0) {
                    liveBagInfoResponse.setJoinNum(redisCache.smembers(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId()).size());
                    Boolean isJoin = redisCache.sismember(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId(), bagInfoRequest.getCustomerId());
                    if (isJoin) {
                        liveBagInfoResponse.setIsJoin(1);
                    } else {
                        liveBagInfoResponse.setIsJoin(0);
                    }
                }
            }
            else {
                liveBagInfoResponse.setIsJoin(0);
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
        return BaseResponse.success(liveBagInfoResponse);
    }

    @Override
    public BaseResponse<LiveBagLogInfoResponse> getPushBagInfo(@Valid LiveBagInfoRequest bagInfoRequest) {
        LiveBagLogInfoResponse liveBagInfoResponse=new LiveBagLogInfoResponse();
        LiveBag liveBag = liveBagService.getInfo(bagInfoRequest.getBagId().longValue());
        if(Objects.nonNull(liveBag)) {
            liveBagInfoResponse.setBagId(liveBag.getLiveBagId());
            liveBagInfoResponse.setBagName(liveBag.getBagName());
            liveBagInfoResponse.setWinningNumber(liveBag.getWinningNumber());
            liveBagInfoResponse.setSpecifyContent(liveBag.getJoinContent());
            liveBagInfoResponse.setLotteryTime(liveBag.getLotteryTime());
            LiveBagLog liveBagLog = liveBagLogService.getLiveBagInfo(bagInfoRequest.getBagId());
            if (liveBagLog != null) {
                //福袋是否开奖
                    if (liveBagLog.getTicketStatus() == 1) {
                    liveBagInfoResponse.setLotteryStatus(2);
                } else {
                    liveBagInfoResponse.setLotteryStatus(0);
                    Long ticketTime = liveBagLog.getTicketTime().getTime() + (new Double(liveBag.getLotteryTime() * 60 * 1000)).longValue();
                    if ((new Date().getTime()) < ticketTime) {
                        liveBagInfoResponse.setLotteryStatus(1);
                        liveBagInfoResponse.setCountdown((ticketTime - (new Date().getTime())));//开奖中设置倒计时秒
                    } else {
                        liveBagInfoResponse.setLotteryStatus(2);
                        liveBagInfoResponse.setCountdown(0l);
                    }
                    if(Objects.nonNull(redisCache.smembers(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId())) && Objects.nonNull(bagInfoRequest.getCustomerId())) {
                        if (redisCache.smembers(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId()).size() > 0) {
                            Boolean isJoin = redisCache.sismember(CacheKeyConstant.LIVE_BAG_JOIN + bagInfoRequest.getBagId(), bagInfoRequest.getCustomerId());
                            if (isJoin) {
                                liveBagInfoResponse.setIsJoin(1);
                            } else {
                                liveBagInfoResponse.setIsJoin(0);
                            }
                        }
                    }else{
                        liveBagInfoResponse.setIsJoin(0);
                    }
                }

            } else {
                liveBagInfoResponse.setLotteryStatus(1);
                liveBagInfoResponse.setCountdown(0l);
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
        return BaseResponse.success(liveBagInfoResponse);
    }
}
