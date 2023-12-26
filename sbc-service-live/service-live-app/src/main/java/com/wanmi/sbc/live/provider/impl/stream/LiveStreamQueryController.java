package com.wanmi.sbc.live.provider.impl.stream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsListResponse;
import com.wanmi.sbc.live.api.response.stream.*;
import com.wanmi.sbc.live.bean.vo.LiveHaveGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamLogInfoVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.live.redis.RedisService;
import com.wanmi.sbc.live.stream.service.LiveStreamService;
import com.wanmi.sbc.live.util.TencentImUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>直播查询服务接口实现</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@RestController
@Validated
public class LiveStreamQueryController implements LiveStreamQueryProvider {
    @Autowired
    private LiveStreamService liveStreamService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public BaseResponse<LiveStreamPageResponse> listPage(@RequestBody @Valid LiveStreamPageRequest liveStreamPageReq) {
        liveStreamPageReq.setPageNum((liveStreamPageReq.getPageNum())*liveStreamPageReq.getPageSize());
        Page<LiveStreamVO> liveStreamVOPage=liveStreamService.getLiveStreamPage(liveStreamPageReq);
        LiveStreamPageResponse liveStreamPageResponse=new LiveStreamPageResponse();
        liveStreamPageResponse.setContent(liveStreamVOPage.getContent());
        liveStreamPageResponse.setTotalElements((int) liveStreamVOPage.getTotalElements());
        return BaseResponse.success(liveStreamPageResponse);
    }

    @Override
    public BaseResponse<LiveStreamInfoResponse>  streamInfo(@RequestBody LiveStreamInfoRequest liveStreamInfoReq) {
        LiveStreamVO streamVO=liveStreamService.getLiveStreamInfo(liveStreamInfoReq);
        if(Objects.nonNull(streamVO)) {
            if (streamVO.getLiveStatus() == 2 && streamVO.getStartTime() != null) {
                String time = timeStampToDhms(new Date().getTime() - streamVO.getStartTime().getTime());
                if(streamVO.getEndTime() != null){
                    time = timeStampToDhms(streamVO.getEndTime().getTime() - streamVO.getStartTime().getTime());
                }
                streamVO.setDateTime(DateUtil.format(streamVO.getStartTime(), "HH:mm") + "~" + DateUtil.format(new Date(), "HH:mm") + "   " + time);
            }
//            String userSig = redisService.getString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER);
//            if (Objects.isNull(userSig)) {
//                userSig = TencentImUtil.createUsersig();
//                redisService.setString(CacheKeyConstant.REDIS_IM_USER_SIG + TencentImUtil.APP_MANAGER, userSig, TencentImUtil.SIGN_CACHE_TIME);
//            }
            //streamVO.setOnlineNum(TencentImUtil.getOnlineMemberNum(streamVO.getGroupId(), userSig));
            streamVO.setOnlineNum(liveStreamService.getBackOnlineNum(streamVO.getLiveRoomId(),streamVO.getGroupId()));
            String num=redisTemplate.opsForValue().get(CacheKeyConstant.LIVE_ROOM_LIKE+streamVO.getLiveId());
            streamVO.setLikeNum(liveStreamService.getBackLikeNum(streamVO.getLiveRoomId(),num==null?0:Integer.parseInt(num)));
        }
        LiveStreamInfoResponse streamInfoResponse=new LiveStreamInfoResponse();
        streamInfoResponse.setContent(streamVO);
        return BaseResponse.success(streamInfoResponse);
    }

    @Override
    public BaseResponse<LiveStreamPageResponse> liveBroadcastSquare(@Valid LiveStreamPageRequest liveStreamListReq) {
        Integer pageNum=liveStreamListReq.getPageNum();
        liveStreamListReq.setPageNum((pageNum)*liveStreamListReq.getPageSize());
        // 查询直播间列表
        Page<LiveStreamVO> liveStreamVOPage=liveStreamService.liveBroadcastSquare(liveStreamListReq);
        LiveStreamPageResponse liveStreamPageResponse=new LiveStreamPageResponse();
        liveStreamPageResponse.setContent(liveStreamVOPage.getContent());
        liveStreamPageResponse.setTotalElements((int) liveStreamVOPage.getTotalElements());
        //if(pageNum==0){
            LiveStreamInfoRequest liveStreamInfoRequest=new LiveStreamInfoRequest();
            //liveStreamInfoRequest.setLiveStatus(1);
            liveStreamInfoRequest.setSysFlag(2);
            liveStreamPageResponse.setMainContent(liveStreamService.selectBySysRequest(liveStreamInfoRequest));
        //}
        long countPage=0;
        if(liveStreamVOPage.getTotalElements() % liveStreamListReq.getPageSize() ==0){
            countPage=liveStreamVOPage.getTotalElements() / liveStreamListReq.getPageSize();
        }else{
            countPage=liveStreamVOPage.getTotalElements() / liveStreamListReq.getPageSize()+1;
        }
        liveStreamPageResponse.setLast(pageNum==countPage-1?true:false);
        liveStreamPageResponse.setTotalPages(Integer.parseInt(countPage+""));
        return BaseResponse.success(liveStreamPageResponse);
    }

    @Override
    public BaseResponse<LiveStreamLogInfoResponse> streamLogInfo(LiveStreamPageRequest liveStreamInfoReq) {
        LiveStreamLogInfoResponse liveStreamLogInfoResponse=new LiveStreamLogInfoResponse();
        LiveStreamLogInfoVO liveStreamLogInfoVO=liveStreamService.getLiveStreamLogInfo(liveStreamInfoReq.getLiveId());
        liveStreamLogInfoResponse.setLiveStreamLogInfoVO(liveStreamLogInfoVO);
        return BaseResponse.success(liveStreamLogInfoResponse);
    }

    @Override
    public BaseResponse<LiveHaveResponse> goodsLiveInfo(LiveStreamInfoRequest liveStreamInfoReq) {
        LiveHaveResponse liveHaveResponse=new LiveHaveResponse();
        LiveHaveGoodsVO liveHaveGoodsVO=liveStreamService.findLiveByGoodsInfoId(liveStreamInfoReq);
        liveHaveResponse.setLiveHaveGoodsVO(liveHaveGoodsVO);
        return BaseResponse.success(liveHaveResponse);
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
     * 商家直播间列表
     * @param requestParam
     * @return
     */
    @Override
    public BaseResponse<LiveStreamPageResponse> getStoreLiveList(LiveStreamPageRequest requestParam) {
        Integer pageNum=requestParam.getPageNum();
        requestParam.setPageNum((pageNum)*requestParam.getPageSize());
        // 查询商家直播间列表
        Page<LiveStreamVO> liveStreamVOPage=liveStreamService.getStoreLiveList(requestParam);
        LiveStreamPageResponse liveStreamPageResponse=new LiveStreamPageResponse();
        liveStreamPageResponse.setContent(liveStreamVOPage.getContent());
        liveStreamPageResponse.setTotalElements((int) liveStreamVOPage.getTotalElements());
        long countPage=0;
        if(liveStreamVOPage.getTotalElements() % requestParam.getPageSize() ==0){
            countPage=liveStreamVOPage.getTotalElements() / requestParam.getPageSize();
        }else{
            countPage=liveStreamVOPage.getTotalElements() / requestParam.getPageSize()+1;
        }
        liveStreamPageResponse.setLast(pageNum==countPage-1?true:false);
        liveStreamPageResponse.setTotalPages(Integer.parseInt(countPage+""));
        return BaseResponse.success(liveStreamPageResponse);
    }

    @Override
    public BaseResponse<LiveStreamPageResponse> getAllLiveList(LiveStreamPageRequest requestParam) {
        requestParam.setStoreId(null);
        return getStoreLiveList(requestParam);
    }

    @Override
    public BaseResponse<LiveStreamInfoResponse> finishLive(LiveStreamInfoRequest liveStreamInfoReq) {
        BaseResponse<LiveStreamInfoResponse> baseResponse = streamInfo(liveStreamInfoReq);
        if (baseResponse.getContext() != null && baseResponse.getContext().getContent() != null) {
            LiveStreamVO streamVO = liveStreamService.getLiveStreamInfo(liveStreamInfoReq);
            if (streamVO != null) {
                baseResponse.getContext().getContent().setViewerNumber(streamVO.getViewerNumber());
            }
        }
        return baseResponse;
    }

    /**
     * 查询正在直播中的店铺ID
     * @return
     */
    @Override
    public BaseResponse<List<Long>> getLiveStoreId() {
        List<Long> storeIds = liveStreamService.getLiveStoreIds();
        if (ObjectUtils.isEmpty(storeIds)) {
            return BaseResponse.success(new ArrayList<>());
        }
        storeIds = storeIds.stream().distinct().collect(Collectors.toList());
        return BaseResponse.success(storeIds);
    }

    @Override
    public BaseResponse<Map<Long, List<StoreLiveStreamResponse>>> getLiveStoreInfo() {
        Map<Long, List<StoreLiveStreamResponse>> resultList = liveStreamService.getLiveStoreInfo();
        if (ObjectUtils.isEmpty(resultList)) {
            resultList = new HashMap<>();
        }
        return BaseResponse.success(resultList);
    }

    @Override
    public BaseResponse<LiveStreamVO> getLiveRomeEditInfo(LiveStreamInfoRequest streamInfoRequest) {
        LiveStreamVO info = liveStreamService.getLiveStreamEditInfoByRoomId(streamInfoRequest.getLiveRoomId());
        return BaseResponse.success(info);
    }


}
