package com.wanmi.sbc.livestream;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.PushNotifyProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.NoDeleteCustomerGetByAccountRequest;
import com.wanmi.sbc.customer.api.request.customer.PushNotifyRequest;
import com.wanmi.sbc.customer.api.request.store.NoDeleteStoreByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.customer.NoDeleteCustomerGetByAccountResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoSiteQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsViewByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.live.api.provider.activity.LiveBagLogProvider;
import com.wanmi.sbc.live.api.provider.activity.LiveStreamActivityQueryProvider;
import com.wanmi.sbc.live.api.provider.bag.LiveBagProvider;
import com.wanmi.sbc.live.api.provider.goods.LiveStreamGoodsProvider;
import com.wanmi.sbc.live.api.provider.goods.LiveStreamGoodsQueryProvider;
import com.wanmi.sbc.live.api.provider.room.LiveStreamRoomProvider;
import com.wanmi.sbc.live.api.provider.stream.ChatAppIMProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.activity.LiveBagInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityListRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamRecordListRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.request.room.LiveStreamRoomListRequest;
import com.wanmi.sbc.live.api.request.stream.*;
import com.wanmi.sbc.live.api.response.activity.LiveBagLogInfoResponse;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityInfoResponse;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityListResponse;
import com.wanmi.sbc.live.api.response.bag.LiveBagInfoResponse;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsInfoListResponse;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsListResponse;
import com.wanmi.sbc.live.api.response.room.LiveStreamRoomListResponse;
import com.wanmi.sbc.live.api.response.stream.BagAppResponse;
import com.wanmi.sbc.live.api.response.stream.IMAppResponse;
import com.wanmi.sbc.live.api.response.stream.LiveStreamInfoResponse;
import com.wanmi.sbc.live.api.response.stream.LiveStreamPageResponse;
import com.wanmi.sbc.live.bean.vo.LiveGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.livestream.response.LiveStreamResponse;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetByActivityIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetDetailByIdAndStoreIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponFetchRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoByIdRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsDetailFilterRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailByActivityIdResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.bean.dto.GoodsInfoDetailByGoodsInfoDTO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.order.bean.vo.GrouponInstanceWithCustomerInfoVO;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 拼团Controller
 */
@RestController
@RequestMapping("/liveStream")
@Api(tags = "LiveStreamController", description = "S2B web公用-直播")
@Slf4j
public class LiveStreamController {
    @Autowired
    private ChatAppIMProvider chatAppIMProvider;
    @Autowired
    private LiveStreamProvider liveStreamProvider;
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private GoodsInfoSiteQueryProvider goodsInfoSiteQueryProvider;
    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;
    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;
    @Autowired
    private LiveStreamGoodsQueryProvider liveStreamGoodsQueryProvider;
    @Autowired
    private LiveStreamActivityQueryProvider activityQueryProvider;
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Autowired
    private GoodsQueryProvider goodsQueryProvider;
    @Autowired
    private DistributionCacheService distributionCacheService;
    @Autowired
    private MarketingPluginProvider marketingPluginProvider;
    @Autowired
    private LiveBagLogProvider liveBagLogProvider;
    @Autowired
    private LiveStreamRoomProvider liveStreamRoomProvider;
    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private CustomerQueryProvider customerQueryProvider;
    @Autowired
    private PushNotifyProvider pushNotifyProvider;
    /**
     * 前端获取签名
     */
    @ApiOperation(value = "IM前端获取签名")
    @RequestMapping(value = "/getUserSig/{userId}", method = RequestMethod.GET)
    public BaseResponse<IMAppResponse> getGrouponInstanceInfo(@PathVariable String userId) {
        IMAppRequest appRequest=new IMAppRequest();
        appRequest.setUserId(userId);
        return chatAppIMProvider.getUserSig(appRequest);
    }

    /**
     * 创建直播间
     */
    @ApiOperation(value = "创建直播间")
    @RequestMapping(value = "/createRoom", method = RequestMethod.POST)
    public BaseResponse createRoom(@RequestBody LiveStreamAddRequest mobileAddReq){
        long entryTime = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LiveStreamInfoRequest liveStreamInfoReq=new LiveStreamInfoRequest();
        liveStreamInfoReq.setLiveStatus(1);
        liveStreamInfoReq.setLiveRoomId(mobileAddReq.getLiveRoomId());
        LiveStreamInfoResponse streamInfoResponse=liveStreamQueryProvider.streamInfo(liveStreamInfoReq).getContext();
        stopWatch.stop();
        log.info("创建直播间createRoom， 查询直播信息耗时 {}", stopWatch.getTotalTimeSeconds());
        if(Objects.nonNull(streamInfoResponse.getContent())){
            return BaseResponse.error("正在直播中，请结束后创建直播！");
        }
        stopWatch.start();
        CustomerVO customerVO=commonUtil.getCustomer();
        if (Objects.nonNull(customerVO)) {
            mobileAddReq.setAnchorName(customerVO.getCustomerDetail().getCustomerName());
            mobileAddReq.setCustomerAccount(customerVO.getCustomerAccount());
        }

        /**
         * zhouzhenguo 20230713  在service里判断直播间类型
         * 注释代码；
         */
//        if(Objects.nonNull(mobileAddReq.getSysFlag())){
//            if(mobileAddReq.getSysFlag()==0){
//                mobileAddReq.setSysFlag(2);
//            }else {
//                mobileAddReq.setSysFlag(1);
//            }
//        }

        BaseResponse baseResponse = liveStreamProvider.createRoom(mobileAddReq);
        stopWatch.stop();
        log.info("创建直播间createRoom， 新建直播间耗时 {}", stopWatch.getTotalTimeSeconds());
        if (CommonErrorCode.FAILED.equals(baseResponse.getCode())) {
            long exitTime = System.currentTimeMillis();
            log.info("创建直播间createRoom， 总耗时 {}", (exitTime - entryTime) / 1000d);
            return baseResponse;
        }
        try {
            pushNotifyProvider.pushNotify(PushNotifyRequest.builder().tencentImMessageType(TencentImMessageType.LiveNotify).build());
        }
        catch (Exception e) {
            log.info("推送APP消息异常", e);
        }
        stopWatch.start();
        baseResponse = liveStreamQueryProvider.streamInfo(liveStreamInfoReq);
        stopWatch.stop();
        log.info("创建直播间createRoom， 查询直播间信息耗时 {}", stopWatch.getTotalTimeSeconds());
        long exitTime = System.currentTimeMillis();
        log.info("创建直播间createRoom， 总耗时 {}", (exitTime - entryTime) / 1000d);
        return baseResponse;
    }

    /**
     * 直播广场入口
     * @return
     */
    @ApiOperation(value = "直播广场入口判断")
    @PostMapping("/haveBroadcastSquare")
    public BaseResponse<Integer> haveBroadcastSquare() {
        LiveStreamPageRequest pageReq=new LiveStreamPageRequest();
        pageReq.setPageNum(0);
        pageReq.setPageSize(10);
        Integer totalElements=0;
        LiveStreamPageResponse liveStreamPageResponse=liveStreamQueryProvider.liveBroadcastSquare(pageReq).getContext();
        totalElements=liveStreamPageResponse.getTotalElements();
        LiveStreamVO mainContent=liveStreamPageResponse.getMainContent();
        if(Objects.nonNull(mainContent)){
            totalElements++;
        }
        return BaseResponse.success(totalElements);
    }

    /**
     * 直播广场
     * @param pageReq
     * @return
     */
    @ApiOperation(value = "直播广场")
    @PostMapping("/liveBroadcastSquare")
    public BaseResponse<LiveStreamPageResponse> getList(@RequestBody @Valid LiveStreamPageRequest pageReq) {
        if (pageReq.getStoreId() != null && pageReq.getStoreId() < 1) {
            pageReq.setStoreId(null);
        }
        return liveStreamQueryProvider.liveBroadcastSquare(pageReq);
    }

    @ApiOperation(value = "所有直播间(APP首页直播间Bananer位进入)")
    @PostMapping("/allLiveList")
    public BaseResponse<LiveStreamPageResponse> getAllLiveList (@RequestBody LiveStreamPageRequest requestParam ) {
        return liveStreamQueryProvider.getAllLiveList(requestParam);
    }

    @ApiOperation(value = "商家直播间列表(商家首页直播间Bananer位进入)")
    @PostMapping("/storeLiveList")
    public BaseResponse<LiveStreamPageResponse> getStoreLiveList (@RequestBody LiveStreamPageRequest requestParam ) {
        if (ObjectUtils.isEmpty(requestParam.getStoreId())) {
            return BaseResponse.error("店铺ID不能为空");
        }
        return liveStreamQueryProvider.getStoreLiveList(requestParam);
    }

    /**
     * 更新直播间
     */
    @ApiOperation(value = "更新直播间")
    @RequestMapping(value = "/modifyRoom", method = RequestMethod.POST)
    public BaseResponse modifyRoom(@RequestBody LiveStreamAddRequest mobileAddReq){
        liveStreamProvider.updateRoom(mobileAddReq);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新直播间
     */
    @ApiOperation(value = "结束直播")
    @RequestMapping(value = "/finishRoom", method = RequestMethod.POST)
    public BaseResponse<LiveStreamInfoResponse> finishRoom(@RequestBody LiveStreamInfoRequest liveStreamInfoReq){
        LiveStreamAddRequest mobileAddReq=new LiveStreamAddRequest();
        mobileAddReq.setLiveStatus(2);
        mobileAddReq.setLiveId(liveStreamInfoReq.getLiveId());
        mobileAddReq.setLiveRoomId(liveStreamInfoReq.getLiveRoomId());
        liveStreamProvider.updateRoom(mobileAddReq);
        try {
            pushNotifyProvider.pushNotify(PushNotifyRequest.builder().tencentImMessageType(TencentImMessageType.LiveNotify).build());
        }
        catch (Exception e) {
            log.info("推送APP消息异常", e);
        }
        return liveStreamQueryProvider.finishLive(liveStreamInfoReq);
    }

    /**
     * 获取直播间列表
     * @return
     */
    @ApiOperation(value = "获取直播间列表")
    @RequestMapping(value = "/getRoomList", method = RequestMethod.POST)
    public BaseResponse<LiveStreamRoomListResponse> streamInfo(){
        CustomerVO customerVO=commonUtil.getCustomer();
        LiveStreamRoomListRequest roomListRequest=new LiveStreamRoomListRequest();
        roomListRequest.setCustomerId(customerVO.getCustomerId());
        //roomListRequest.setCustomerId("8a0288db8354c9e80183551bdf420000");
        return liveStreamRoomProvider.getLiveRoomListByCustomerId(roomListRequest);
    }

    /**
     * 获取直播间信息
     * @param liveStreamInfoReq
     * @return
     */
    @ApiOperation(value = "获取直播间信息")
    @RequestMapping(value = "/getRoomInfo", method = RequestMethod.POST)
    public BaseResponse<LiveStreamInfoResponse> streamInfo(@RequestBody  LiveStreamInfoRequest liveStreamInfoReq){
        liveStreamInfoReq.setLiveStatus(1);
        return liveStreamQueryProvider.streamInfo(liveStreamInfoReq);
    }

    /**
     * 获取直播间信息
     * @param liveStreamInfoReq
     * @return
     */
    @ApiOperation(value = "获取直播间信息")
    @RequestMapping(value = "/share/getRoomInfo", method = RequestMethod.POST)
    public BaseResponse<LiveStreamInfoResponse> getRoomInfo(@RequestBody  LiveStreamInfoRequest liveStreamInfoReq){

        // TODO 参数校验
        Integer liveRoomId = liveStreamInfoReq.getLiveRoomId();
        String sharerAccount = liveStreamInfoReq.getSharerAccount();

        BaseResponse<LiveStreamInfoResponse> liveStreamInfoResponseBaseResponse = liveStreamQueryProvider.streamInfo(liveStreamInfoReq);


        NoDeleteCustomerGetByAccountRequest noDeleteCustomerGetByAccountRequest = new NoDeleteCustomerGetByAccountRequest();
        noDeleteCustomerGetByAccountRequest.setCustomerAccount(sharerAccount);
        BaseResponse<NoDeleteCustomerGetByAccountResponse> noDeleteCustomerByAccount = customerQueryProvider.getNoDeleteCustomerByAccount(noDeleteCustomerGetByAccountRequest);
        CustomerVO customerVO = KsBeanUtil.convert(noDeleteCustomerByAccount.getContext(), CustomerVO.class);

        LiveStreamInfoResponse liveStreamInfoResponse = Optional.ofNullable(liveStreamInfoResponseBaseResponse).map(BaseResponse::getContext).orElse(new LiveStreamInfoResponse());
        liveStreamInfoResponse.setSharer(customerVO);

        return BaseResponse.success(liveStreamInfoResponse);
    }

    /**
     * 获取直播间信息、商品、优惠卷活动
     * @param liveStreamInfoReq
     * @return
     */
    @ApiOperation(value = "获取直播间信息、商品、优惠卷活动")
    @RequestMapping(value = "/getRoomInfoDetail", method = RequestMethod.POST)
    public BaseResponse<LiveStreamResponse> streamInfoRetail(@RequestBody LiveStreamInfoRequest liveStreamInfoReq){
        liveStreamInfoReq.setLiveStatus(1);
        LiveStreamResponse liveStreamResponse=new LiveStreamResponse();
        LiveStreamInfoResponse response=liveStreamQueryProvider.streamInfo(liveStreamInfoReq).getContext();
        if(Objects.nonNull(response)){
            liveStreamResponse.setLiveStreamVO(response.getContent());
            if(Objects.nonNull(response.getContent())){
                //String num=redisTemplate.opsForValue().get(CacheKeyConstant.LIVE_ROOM_LIKE+response.getContent().getLiveId());
                //liveStreamResponse.getLiveStreamVO().setLikeNum(num==null?0:Integer.parseInt(num));
                liveStreamResponse.setOnlineNum(response.getContent().getOnlineNum());
                liveStreamResponse.setLikeNum(response.getContent().getLikeNum());
                String goodInfoId=response.getContent().getGoodsInfoId();
                String activityId=response.getContent().getActivityId();
                String couponId=response.getContent().getCouponId();
                Integer bagId=response.getContent().getBagId();
                /**
                 * 商品
                 */
                if(Objects.nonNull(goodInfoId)){
                    liveStreamResponse.setIsHaveGoods(1);
                    GoodsInfoRequest goodsInfoRequest = new GoodsInfoRequest();
                    goodsInfoRequest.setGoodsInfoId(goodInfoId);
                    liveStreamResponse.setGoodsInfoId(goodInfoId);
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
                        BaseResponse<GoodsInfoByIdResponse> goodsInfoResponse = retailGoodsInfoQueryProvider.getRetailById(GoodsInfoByIdRequest.builder().goodsInfoId(goodInfoId).matchWareHouseFlag(true).build());
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
                }else{
                    liveStreamResponse.setIsHaveGoods(0);
                }
                /**
                 * 优惠劵活动
                 * Objects.nonNull(activityId)&&
                 */
                if(StringUtils.isNotEmpty(activityId)) {
                    CouponActivityDetailResponse activityDetailResponse =
                            couponActivityQueryProvider.getDetailByIdAndStoreId(new CouponActivityGetDetailByIdAndStoreIdRequest(activityId, null)).getContext();
                    liveStreamResponse.setActivityId(activityId);
                    if(Objects.nonNull(activityDetailResponse.getCouponActivity())){
                        liveStreamResponse.setActivityName(activityDetailResponse.getCouponActivity().getActivityName());
                    }
                    if(StringUtils.isNotEmpty(couponId)) {
                        CouponInfoVO couponInfoVO = couponInfoQueryProvider.getById(CouponInfoByIdRequest.builder().couponId(couponId).build())
                                .getContext();
                        if (Objects.nonNull(couponInfoVO)) {
                            liveStreamResponse.setIsHaveCoupon(1);
                            liveStreamResponse.setCouponId(couponInfoVO.getCouponId());
                            liveStreamResponse.setCouponName(couponInfoVO.getCouponName());
                            liveStreamResponse.setDenomination(couponInfoVO.getDenomination());
                            liveStreamResponse.setCouponDesc(couponInfoVO.getCouponDesc());
                            liveStreamResponse.setFullBuyPrice(couponInfoVO.getFullBuyPrice());
                            liveStreamResponse.setFullBuyType(couponInfoVO.getFullBuyType().toValue());
                        }else{
                            liveStreamResponse.setIsHaveCoupon(0);
                        }
                    }
                }

                /**
                 *福袋
                 */
                if(Objects.nonNull(bagId)){
                    CustomerVO customerVO=commonUtil.getCustomer();
                    LiveBagInfoRequest bagInfoRequest=new LiveBagInfoRequest();
                    bagInfoRequest.setBagId(bagId);
                    if(Objects.nonNull(customerVO)){
                        bagInfoRequest.setCustomerId(customerVO.getCustomerId());
                    }
                    LiveBagLogInfoResponse baseResponse=liveBagLogProvider.getPushBagInfo(bagInfoRequest).getContext();
                    if(Objects.nonNull(baseResponse)){
                        liveStreamResponse.setIsHaveBag(1);
                        liveStreamResponse.setBagId(baseResponse.getBagId().intValue());
                        liveStreamResponse.setSpecifyContent(baseResponse.getSpecifyContent());
                        liveStreamResponse.setLotteryStatus(baseResponse.getLotteryStatus());
                        liveStreamResponse.setCountdown(baseResponse.getCountdown());
                        liveStreamResponse.setIsJoin(baseResponse.getIsJoin());
                        liveStreamResponse.setWinningNumber(baseResponse.getWinningNumber());
                        liveStreamResponse.setCouponId(baseResponse.getCouponId());
                        liveStreamResponse.setCouponName(baseResponse.getCouponName());
                        liveStreamResponse.setDenomination(baseResponse.getDenomination());
                        liveStreamResponse.setCouponDesc(baseResponse.getCouponDesc());
                        liveStreamResponse.setFullBuyPrice(baseResponse.getFullBuyPrice());
                        liveStreamResponse.setFullBuyType(baseResponse.getFullBuyType());
                    }else{
                        liveStreamResponse.setIsHaveBag(0);
                    }
                }
                //主播状态
                String hostStatus=redisTemplate.opsForValue().get(CacheKeyConstant.LIVE_HOST_STATUS+response.getContent().getLiveId());
                if(Objects.nonNull(hostStatus)){
                    liveStreamResponse.setHostStatus(Integer.parseInt(hostStatus));
                }else{
                    liveStreamResponse.setHostStatus(1);
                }
            }
        }

        return BaseResponse.success(liveStreamResponse);
    }


    /**
     * 获取回放直播间信息
     * @param liveStreamInfoReq
     * @return
     */
    @ApiOperation(value = "获取回放直播间信息")
    @RequestMapping(value = "/playbackRoomInfoDetail", method = RequestMethod.POST)
    public BaseResponse<LiveStreamResponse> findStreamInfoRetail(@RequestBody LiveStreamInfoRequest liveStreamInfoReq){
        LiveStreamResponse liveStreamResponse=new LiveStreamResponse();
        LiveStreamInfoResponse response=liveStreamQueryProvider.streamInfo(liveStreamInfoReq).getContext();
        if(Objects.nonNull(response)){
            liveStreamResponse.setLiveStreamVO(response.getContent());
            if(Objects.nonNull(response.getContent())){
                String num=redisTemplate.opsForValue().get(CacheKeyConstant.LIVE_ROOM_LIKE+response.getContent().getLiveId());
                liveStreamResponse.getLiveStreamVO().setLikeNum(num==null?0:Integer.parseInt(num));
                liveStreamResponse.setOnlineNum(response.getContent().getOnlineNum());
            }
        }
        return BaseResponse.success(liveStreamResponse);
    }


    /**
     * 查询商品
     *
     * @param liveStreamGoodsListRequest 商品 {@link GoodsPageRequest}
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/goodsNewList", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> list(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest) {
        LiveStreamGoodsInfoListResponse goodsListResponse=liveStreamGoodsQueryProvider.listInfo(liveStreamGoodsListRequest).getContext();
        List<LiveGoodsInfoVO> liveGoodsInfoVOS=new ArrayList<>();
        goodsListResponse.getLiveGoodsVOS().forEach(liveGoodsVO -> {
            LiveGoodsInfoVO liveGoodsInfoVO=new LiveGoodsInfoVO();
            liveGoodsInfoVO.setGoodsInfoId(liveGoodsVO.getGoodsInfoId());
            liveGoodsInfoVO.setGoodsType(liveGoodsVO.getGoodsType());
            liveGoodsInfoVOS.add(liveGoodsInfoVO);
        });
        GoodsInfoViewPageRequest pageRequest=new GoodsInfoViewPageRequest();
        pageRequest.setLiveGoodsInfoVOS(liveGoodsInfoVOS);
        pageRequest.setPageNum(liveStreamGoodsListRequest.getPageNum());
        pageRequest.setPageSize(liveStreamGoodsListRequest.getPageSize());
        BaseResponse<GoodsInfoViewPageResponse> pageResponse = goodsInfoQueryProvider.pageViewWrapper(pageRequest);
        return pageResponse;
    }

    @ApiOperation(value = "直播商品列表")
    @PostMapping("/goodsList")
    public BaseResponse<GoodsInfoViewByIdsResponse> getList(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest,HttpServletRequest httpRequest) {
        LiveStreamGoodsListResponse goodsListResponse=liveStreamGoodsQueryProvider.list(liveStreamGoodsListRequest).getContext();
        List<GoodsInfoVO> goodsInfoVOList=new ArrayList<>();
        List<GoodsVO> goodsVOList=new ArrayList<>();
        goodsListResponse.getLiveStreamGoodsVO().forEach(entity-> {
            String goodsInfoId = entity.getGoodsInfoId();
            GoodsViewByIdResponse goodsViewByIdResponse=this.goodsDetailBaseInfo(goodsInfoId,commonUtil.getWareId(HttpUtil.getRequest()),true);
            if(Objects.nonNull(goodsViewByIdResponse)) {
                List<GoodsInfoVO> goodsInfoVOLists = goodsViewByIdResponse.getGoodsInfos().stream()
                        .filter(g -> AddedFlag.YES.toValue() == g.getAddedFlag())
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(goodsInfoVOLists)) {
                    goodsViewByIdResponse = detailGoodsInfoVOList(goodsViewByIdResponse, goodsInfoVOLists, commonUtil.getCustomer(), httpRequest);
                }
                //GoodsInfoViewByIdResponse responseGoodsInfo = goodsInfoQueryProvider.getViewById(GoodsInfoViewByIdRequest.builder().goodsInfoId(goodsInfoId).build()).getContext();
                GoodsViewByIdResponse finalGoodsViewByIdResponse = goodsViewByIdResponse;
                goodsViewByIdResponse.getGoodsInfos().forEach(goodsInfoVO -> {
                    if (entity.getExplainFlag() == 1) {
                        goodsInfoVO.setExplainFlag(1);
                    }
                    goodsInfoVO.setGoods(finalGoodsViewByIdResponse.getGoods());
                    goodsInfoVOList.add(goodsInfoVO);
                });
                //goodsVOList.add(goodsViewByIdResponse.getGoods());
            }
        });
        GoodsInfoViewByIdsResponse response=new GoodsInfoViewByIdsResponse();
        response.setGoodsInfos(goodsInfoVOList);
        response.setGoodses(goodsVOList);
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "直播优惠卷活动列表")
    @PostMapping("/activityList")
    public BaseResponse getActivityList(@RequestBody LiveStreamActivityListRequest liveStreamActivityListRequest){
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

    @ApiOperation(value = "直播加购、立购、优惠卷领取人数")
    @PostMapping("/set")
    public BaseResponse set(@RequestBody LiveStreamSetRequest streamSetRequest){
        streamSetRequest.setCustomerId(commonUtil.getOperatorId());
        return liveStreamProvider.set(streamSetRequest);
    }


    @ApiOperation(value = "主播端直播商品推送列表")
    @PostMapping("/goodsAnchorLists")
    public BaseResponse getNewLists(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest) {
        GoodsInfoViewPageRequest request = GoodsInfoViewPageRequest.builder()
                .likeGoodsInfoNo(liveStreamGoodsListRequest.getLikeGoodsInfoNo())
                .likeGoodsName(liveStreamGoodsListRequest.getLikeGoodsName())
                .build();

        LiveStreamGoodsListRequest newLiveStreamGoodsListRequest = new LiveStreamGoodsListRequest();
        newLiveStreamGoodsListRequest.setGoodsStatus(1L);
        newLiveStreamGoodsListRequest.setPageNum(0);
        newLiveStreamGoodsListRequest.setPageSize(200);
        newLiveStreamGoodsListRequest.setLiveRoomId(liveStreamGoodsListRequest.getLiveRoomId());
        LiveStreamGoodsListResponse goodsListResponse = liveStreamGoodsQueryProvider.list(newLiveStreamGoodsListRequest).getContext();
        List<LiveStreamGoodsVO> liveStreamGoodsVO = goodsListResponse.getLiveStreamGoodsVO();
        // TODO log
        log.info("getNewLists liveStreamGoodsVO：{}", JSON.toJSONString(liveStreamGoodsVO));
        /**
         * 无任何数据
         */
        if(CollectionUtils.isEmpty(liveStreamGoodsVO)){
            GoodsInfoViewPageResponse goodsInfoViewPageResponse = new GoodsInfoViewPageResponse();
            return BaseResponse.success(goodsInfoViewPageResponse);
        }

        /**
         * 按照商品类型进行分组
         */
        Map<Long, List<LiveStreamGoodsVO>> group = liveStreamGoodsVO.stream().collect(Collectors.groupingBy(LiveStreamGoodsVO::getGoodsType));
        List<String> goodsInfoIds = liveStreamGoodsVO.stream().map(LiveStreamGoodsVO::getGoodsInfoId).collect(Collectors.toList());
        request.setGoodsInfoIds(goodsInfoIds);
        // TODO log
        log.info("getNewLists goodsInfoIds：{}", JSON.toJSONString(goodsInfoIds));

        /**
         * 合并查询结果
         */
        List<LiveStreamGoodsVO> liveGoodsInfoForWholeSale = group.get(0L);// 批发
        List<LiveStreamGoodsVO> liveGoodsInfoForRetail = group.get(1L);   // 散批

        // TODO log
        log.info("getNewLists liveGoodsInfoForWholeSale：{}", JSON.toJSONString(liveGoodsInfoForWholeSale));
        log.info("getNewLists liveGoodsInfoForRetail：{}", JSON.toJSONString(liveGoodsInfoForRetail));

        request.setPageNum(0);
        request.setPageSize(100); // 商品橱窗最多关联100个商品
        request.setAddedFlag(1);
        List<GoodsInfoVO> goodsInfoVOS = Lists.newArrayList();
        List<GoodsVO> goodsVOS = Lists.newArrayList();
        List<GoodsBrandVO> goodsBrandVOS = Lists.newArrayList();
        List<GoodsCateVO> goodsCateVOS = Lists.newArrayList();
        List<GoodsIntervalPriceVO> goodsIntervalPriceVOS = Lists.newArrayList();

        // 批发
        if(CollectionUtils.isNotEmpty(liveGoodsInfoForWholeSale)){
            request.setGoodsType(0); // 0代表批发
            BaseResponse<GoodsInfoViewPageResponse> goodsInfoViewPageResponses = goodsInfoQueryProvider.pageViewWrapper(request);
            // TODO log
            log.info("getNewLists goodsInfoViewPageResponses1：{}", JSON.toJSONString(goodsInfoViewPageResponses));

            // SKU
            goodsInfoVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getGoodsInfoPage)
                    .map(MicroServicePage::getContent)
                    .orElse(Lists.newArrayList()));

            // SPU
            goodsVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getGoodses)
                    .orElse(Lists.newArrayList()));

            // 品牌
            goodsBrandVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getBrands)
                    .orElse(Lists.newArrayList()));

            // 分类
            goodsCateVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getCates)
                    .orElse(Lists.newArrayList()));

            // 区间价
            goodsIntervalPriceVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getGoodsIntervalPrices)
                    .orElse(Lists.newArrayList()));
        }
        // 零售
        if(CollectionUtils.isNotEmpty(liveGoodsInfoForRetail)){
            request.setGoodsType(1);// 1代表零售
            BaseResponse<GoodsInfoViewPageResponse> goodsInfoViewPageResponses = goodsInfoQueryProvider.pageViewWrapper(request);

            // TODO log
            log.info("getNewLists goodsInfoViewPageResponses2：{}", JSON.toJSONString(goodsInfoViewPageResponses));
            // SKU
            goodsInfoVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getGoodsInfoPage)
                    .map(MicroServicePage::getContent)
                    .orElse(Lists.newArrayList()));

            // SPU
            goodsVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getGoodses)
                    .orElse(Lists.newArrayList()));

            // 品牌
            goodsBrandVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getBrands)
                    .orElse(Lists.newArrayList()));

            // 分类
            goodsCateVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getCates)
                    .orElse(Lists.newArrayList()));

            // 区间价
            goodsIntervalPriceVOS.addAll(Optional.ofNullable(goodsInfoViewPageResponses)
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoViewPageResponse::getGoodsIntervalPrices)
                    .orElse(Lists.newArrayList()));
        }

        /**
         * 排序
         */
        // 数据为空，提前返回
        if(CollectionUtils.isEmpty(goodsInfoVOS)){
            GoodsInfoViewPageResponse goodsInfoViewPageResponse = new GoodsInfoViewPageResponse();
            return BaseResponse.success(goodsInfoViewPageResponse);
        }
        // TODO log
        log.info("getNewLists goodsInfoVOS：{}", JSON.toJSONString(goodsInfoVOS));

        List<String> collect = liveStreamGoodsVO.stream().map(LiveStreamGoodsVO::getGoodsInfoId).distinct().collect(Collectors.toList());

        // TODO log
        log.info("getNewLists collect：{}", JSON.toJSONString(collect));

        Map<String, GoodsInfoVO> goodsInfos = goodsInfoVOS.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));

        // TODO log
        log.info("getNewLists goodsInfos：{}", JSON.toJSONString(goodsInfos));

        Map<String, Long> goodsStatus = liveStreamGoodsVO.stream().collect(Collectors.toMap(LiveStreamGoodsVO::getGoodsInfoId, LiveStreamGoodsVO::getGoodsStatus,(x, y)->x));

        // TODO log
        log.info("getNewLists goodsStatus：{}", JSON.toJSONString(goodsStatus));

        Map<String, Integer> goodsExplainFlag = liveStreamGoodsVO.stream().collect(Collectors.toMap(LiveStreamGoodsVO::getGoodsInfoId, LiveStreamGoodsVO::getExplainFlag, (x, y)->x));
        // TODO log
        log.info("getNewLists goodsExplainFlag：{}", JSON.toJSONString(goodsExplainFlag));

        List<GoodsInfoVO> orderdeds = Lists.newArrayList();
        for (String goodsInfoId: collect){
            GoodsInfoVO goodsInfoVO = goodsInfos.get(goodsInfoId);
            if(Objects.isNull(goodsInfoVO)){continue;}
            if(Objects.nonNull(goodsExplainFlag.get(goodsInfoId))){
                goodsInfoVO.setExplainFlag(goodsExplainFlag.get(goodsInfoId));
            } else {
                goodsInfoVO.setExplainFlag(0);
            }
            goodsInfoVO.setLiveGoodsStatus(goodsStatus.get(goodsInfoId));
            orderdeds.add(goodsInfoVO);
        }
        // TODO log
        log.info("getNewLists orderdeds：{}", JSON.toJSONString(orderdeds));

        // 自定义分页
        Integer pageNum = liveStreamGoodsListRequest.getPageNum();
        Integer pageSize = liveStreamGoodsListRequest.getPageSize();

        PagedListHolder<GoodsInfoVO> pagedListHolder = new PagedListHolder<>(orderdeds);
        pagedListHolder.setPageSize(pageSize);
        pagedListHolder.setPage(pageNum);

        MicroServicePage<GoodsInfoVO> microServicePage = new MicroServicePage(pagedListHolder.getPageList(), PageRequest.of(pageNum, pageSize), orderdeds.size());
        // TODO log
        log.info("getNewLists microServicePage：{}", JSON.toJSONString(microServicePage));

        List<String> goodsIds = microServicePage.getContent().stream().map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
        List<GoodsVO> goodsVOAfterFormat = goodsVOS.stream().filter(x -> Objects.nonNull(x) && goodsIds.contains(x.getGoodsId())).collect(Collectors.toList());
        List<GoodsIntervalPriceVO> goodsIntervalPriceVOSAfterFormat = goodsIntervalPriceVOS.stream().filter(x -> Objects.nonNull(x) && goodsIds.contains(x.getGoodsId())).collect(Collectors.toList());

        GoodsInfoViewPageResponse goodsInfoViewPageResponse = new GoodsInfoViewPageResponse();
        goodsInfoViewPageResponse.setGoodsInfoPage(microServicePage);
        goodsInfoViewPageResponse.setGoodses(goodsVOAfterFormat);
        goodsInfoViewPageResponse.setBrands(goodsBrandVOS);
        goodsInfoViewPageResponse.setCates(goodsCateVOS);
        goodsInfoViewPageResponse.setGoodsIntervalPrices(goodsIntervalPriceVOSAfterFormat);

        return BaseResponse.success(goodsInfoViewPageResponse);
    }


    @ApiOperation(value = "主播端直播商品推送列表")
    @PostMapping("/goodsAnchorLists2")
    public BaseResponse getNewLists2(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest) {
        GoodsInfoViewPageRequest request=new GoodsInfoViewPageRequest();
        liveStreamGoodsListRequest.setGoodsStatus(1l);
        LiveStreamGoodsListResponse goodsListResponse=liveStreamGoodsQueryProvider.list(liveStreamGoodsListRequest).getContext();
        Map<Object, Long> goodsStatus=goodsListResponse.getLiveStreamGoodsVO().stream().collect(Collectors.groupingByConcurrent(LiveStreamGoodsVO::getGoodsInfoId,Collectors.summingLong(p->p.getGoodsStatus())));
        Map<Object, Integer> goodsExplainFlag=goodsListResponse.getLiveStreamGoodsVO().stream().collect(Collectors.groupingByConcurrent(LiveStreamGoodsVO::getGoodsInfoId,Collectors.summingInt(p->p.getExplainFlag())));
        List<String> goodsInfoIds=goodsListResponse.getLiveStreamGoodsVO().stream().map(LiveStreamGoodsVO::getGoodsInfoId).collect(Collectors.toList());
        if(goodsInfoIds.size()==0){
            request.setLikeGoodsName("string");
        }
        Integer goodsType=liveStreamGoodsListRequest.getGoodsType().intValue();//0 批发 1散批
        Integer pageNum=liveStreamGoodsListRequest.getPageNum();
        Integer pageSize = liveStreamGoodsListRequest.getPageSize();
        request.setGoodsInfoIds(goodsInfoIds);
        if(goodsType!=null){
            request.setGoodsType(goodsType);
        }
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        if(Objects.nonNull(liveStreamGoodsListRequest.getLikeGoodsName())){
            request.setLikeGoodsName(liveStreamGoodsListRequest.getLikeGoodsName());
        }
        if(Objects.nonNull(liveStreamGoodsListRequest.getLikeGoodsInfoNo())){
            request.setLikeGoodsInfoNo(liveStreamGoodsListRequest.getLikeGoodsInfoNo());
        }
        BaseResponse<GoodsInfoViewPageResponse> goodsInfoViewPageResponses=goodsInfoQueryProvider.pageViewWrapper(request);
        List<GoodsInfoVO> goodsInfoVOSNew=new ArrayList<>();
        List<GoodsInfoVO> goodsInfoVOS= goodsInfoViewPageResponses.getContext().getGoodsInfoPage().getContent();
        goodsInfoVOSNew.addAll(goodsInfoVOS);
        //添加散批
        liveStreamGoodsListRequest.setGoodsType(1l);
        goodsListResponse=liveStreamGoodsQueryProvider.list(liveStreamGoodsListRequest).getContext();
        Map<Object, Long> goodsStatusType=goodsListResponse.getLiveStreamGoodsVO().stream().collect(Collectors.groupingByConcurrent(LiveStreamGoodsVO::getGoodsInfoId,Collectors.summingLong(p->p.getGoodsStatus())));
        Map<Object, Integer> goodsExplainFlagType=goodsListResponse.getLiveStreamGoodsVO().stream().collect(Collectors.groupingByConcurrent(LiveStreamGoodsVO::getGoodsInfoId,Collectors.summingInt(p->p.getExplainFlag())));
        List<String> goodsInfoIdSans=goodsListResponse.getLiveStreamGoodsVO().stream().map(LiveStreamGoodsVO::getGoodsInfoId).collect(Collectors.toList());
        goodsStatus.putAll(goodsStatusType);
        goodsExplainFlag.putAll(goodsExplainFlagType);
        if(goodsInfoIdSans.size()==0){
            request.setLikeGoodsName("string");
        }
        request.setGoodsInfoIds(goodsInfoIdSans);
        request.setGoodsType(1);
        BaseResponse<GoodsInfoViewPageResponse> goodsInfoViewPageTypeResponses=goodsInfoQueryProvider.pageViewWrapper(request);
        List<GoodsInfoVO> goodsInfoVOSType= goodsInfoViewPageTypeResponses.getContext().getGoodsInfoPage().getContent();
        goodsInfoVOSNew.addAll(goodsInfoVOSType);
        goodsInfoVOSNew.forEach(goodsInfoVO -> {
            goodsInfoVO.setLiveGoodsStatus(goodsStatus.get(goodsInfoVO.getGoodsInfoId()));
            goodsInfoVO.setExplainFlag(goodsExplainFlag.get(goodsInfoVO.getGoodsInfoId()));
        });
        goodsInfoViewPageResponses.getContext().getGoodsInfoPage().setContent(goodsInfoVOSNew);
        return goodsInfoViewPageResponses;
    }


    private GoodsViewByIdResponse goodsDetailBaseInfo(String skuId, Long wareId, Boolean matchWareHouseFlag) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).build()).getContext();

        if (Objects.isNull(goodsInfo)) {
            return null;
        }
        GoodsViewByIdRequest request = new GoodsViewByIdRequest();
        request.setGoodsId(goodsInfo.getGoodsId());
        request.setMatchWareHouseFlag(matchWareHouseFlag);
        if (wareId != null) {
            request.setWareId(wareId);
        }
        GoodsViewByIdResponse response = goodsQueryProvider.getViewById(request).getContext();
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        if (DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(distributionCacheService.queryStoreOpenFlag
                (String.valueOf(goodsInfo.getStoreId()))) || !DistributionGoodsAudit.CHECKED.equals(goodsInfo
                .getDistributionGoodsAudit())) {
            response.setDistributionGoods(Boolean.FALSE);
        } else {
            response.setDistributionGoods(Boolean.TRUE);
        }

        return response;
    }

    /**
     * SPU商品详情
     *
     * @param response
     * @param goodsInfoVOList
     * @param customer
     * @return
     */
    private GoodsViewByIdResponse detailGoodsInfoVOList(GoodsViewByIdResponse response, List<GoodsInfoVO>
            goodsInfoVOList, CustomerVO customer,HttpServletRequest httpRequest) {
        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
            if (Objects.nonNull(customer)) {
                filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
            }
            filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class));
            response.setGoodsInfos(marketingPluginProvider.goodsListFilter(filterRequest).getContext()
                    .getGoodsInfoVOList());

            //商品详情营销文案更改，其他地方不变
            if(Objects.nonNull(response.getGoodsInfos())){
                response.getGoodsInfos().forEach(goodsInfoVO -> {

                    if(Objects.nonNull(goodsInfoVO.getShelflife()) && goodsInfoVO.getShelflife() == 9999){
                        goodsInfoVO.setShelflife(0L);
                    }

                    if(Objects.nonNull(goodsInfoVO.getMarketingLabels())){
                        goodsInfoVO.getMarketingLabels().forEach(marketingLabelVO -> {
                            String desc = marketingLabelVO.getMarketingDesc();
                            List<String> descList = marketingLabelVO.getMarketingDescList();
                            if(Objects.nonNull(desc) && desc.indexOf("（") != -1){
                                String newDesc = "跨单品"+desc.substring(0, desc.indexOf("（"));
                                marketingLabelVO.setMarketingDesc(newDesc);
                                if (Objects.nonNull(descList)){
                                    List<String> newDescList = new ArrayList<>();
                                    descList.forEach( s -> {
                                        newDescList.add("跨单品"+s.substring(0,s.indexOf("（")));
                                    });
                                    marketingLabelVO.setMarketingDescList(newDescList);
                                }
                            }
                        });
                    }

                    if (Objects.nonNull(customer)
                            && null != customer.getEnterpriseStatusXyy()
                            && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
                        //特价商品销售价取市场价
                        if (goodsInfoVO.getGoodsInfoType() == 1) {
                            goodsInfoVO.setSalePrice(goodsInfoVO.getMarketPrice());
                        } else {
                            goodsInfoVO.setSalePrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                        }
                        goodsInfoVO.setEnterpriseStatusXyy(customer.getEnterpriseStatusXyy().toValue());
                        goodsInfoVO.setMarketPrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                    }

                    CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
                    if (CollectionUtils.isNotEmpty(response.getGoodsInfos()) && Objects.nonNull(customer)){
                        if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
                            List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                    .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                            //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                            if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) &&
                                    !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                            }
                        }
                    }
                });
            }

        }
        return response;
    }

    @ApiOperation(value = "直播详情")
    @PostMapping("/streamDetail")
    public BaseResponse<LiveStreamInfoResponse> streamDetail(@RequestBody @Valid LiveStreamInfoRequest streamInfoRequest) {
        return liveStreamQueryProvider.streamInfo(streamInfoRequest);
    }

    @ApiOperation(value = "查询直播间基本信息")
    @PostMapping("/liveRomeEditInfo")
    public BaseResponse<LiveStreamVO> getLiveRomeEditInfo (@RequestBody LiveStreamInfoRequest streamInfoRequest) {
        return liveStreamQueryProvider.getLiveRomeEditInfo(streamInfoRequest);
    }
}
