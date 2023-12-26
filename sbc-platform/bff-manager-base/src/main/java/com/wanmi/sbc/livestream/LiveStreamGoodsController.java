package com.wanmi.sbc.livestream;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.goods.api.provider.company.GoodsCompanyQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsGetUsedGoodsRequest;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyByIdResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.live.api.provider.goods.LiveStreamGoodsProvider;
import com.wanmi.sbc.live.api.provider.goods.LiveStreamGoodsQueryProvider;
import com.wanmi.sbc.live.api.provider.room.LiveStreamRoomProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsAddRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsModifyRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsListResponse;
import com.wanmi.sbc.live.api.response.room.LiveRoomBrandIdsResponse;
import com.wanmi.sbc.live.api.response.room.LiveRoomInfoResponse;
import com.wanmi.sbc.live.api.response.stream.LiveStreamLogInfoResponse;
import com.wanmi.sbc.live.bean.vo.LiveStreamGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamLogInfoVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Api(description = "新版直播商品管理API", tags = "LiveStreamGoodsController")
@RestController
@RequestMapping(value = "/liveStream")
public class LiveStreamGoodsController {
    @Autowired
    private LiveStreamGoodsProvider liveStreamGoodsProvider;
    @Autowired
    private LiveStreamGoodsQueryProvider liveStreamGoodsQueryProvider;
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Autowired
    LiveStreamRoomProvider liveRoomProvider;
    @Autowired
    GoodsCompanyQueryProvider goodsCompanyQueryProvider;
    @Autowired
    private PileActivityProvider pileActivityProvider;
    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "列表查询直播商品")
    @PostMapping("/goodsList")
    public BaseResponse getList(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest) {
        LiveStreamGoodsListResponse goodsListResponse=liveStreamGoodsQueryProvider.list(liveStreamGoodsListRequest).getContext();
        List<GoodsInfoViewByIdResponse> goodsInfoVOList=new ArrayList<>();
        goodsListResponse.getLiveStreamGoodsVO().forEach(entity-> {
                    String goodsInfoId = entity.getGoodsInfoId();
                    GoodsInfoViewByIdResponse responseGoodsInfo = goodsInfoQueryProvider.getViewById(GoodsInfoViewByIdRequest.builder().goodsInfoId(goodsInfoId).build()).getContext();
                    if (entity.getExplainFlag() == 1) {
                        responseGoodsInfo.getGoodsInfo().setExplainFlag(1);
                    }else{
                        responseGoodsInfo.getGoodsInfo().setExplainFlag(0);
                    }
            goodsInfoVOList.add(responseGoodsInfo);
                });
        return BaseResponse.success(goodsInfoVOList);
    }

    @ApiOperation(value = "列表查询直播商品")
    @PostMapping("/goodsSkuLists")
    public BaseResponse getNewLists(@RequestBody GoodsInfoViewPageRequest request) {
        List<Long> brandIds=new ArrayList<>();
        request.setStoreId(commonUtil.getStoreId());
        BaseResponse<LiveRoomInfoResponse> liveRoom = liveRoomProvider.getInfo(request.getLiveRoomId());
        LiveRoomInfoResponse liveRoomInfoResponse = liveRoom.getContext();
        if (null != liveRoomInfoResponse) {
            if(liveRoomInfoResponse.getSysFlag()==1){
                 Map<String, String> brandMap=liveRoomInfoResponse.getBrandMap();
                 if(Objects.nonNull(brandMap)) {
                     brandMap.forEach((key, value) -> {
                         brandIds.add(Long.parseLong(key));
                     });
                     request.setBrandIds(brandIds);
                 }
            }
        }
        if(Objects.nonNull(request.getWareId())){
            if(request.getWareId()==0){
                request.setWareId(null);
            }
        }
        BaseResponse<GoodsInfoViewPageResponse> goodsInfoViewPageResponses=goodsInfoQueryProvider.pageViewWrapper(request);
        return goodsInfoViewPageResponses;
    }

    @ApiOperation(value = "列表查询直播商品")
    @PostMapping("/goodsNewLists")
    public BaseResponse getNewLists(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest) {
        GoodsInfoViewPageRequest request=new GoodsInfoViewPageRequest();
        LiveStreamGoodsListResponse goodsListResponse=liveStreamGoodsQueryProvider.list(liveStreamGoodsListRequest).getContext();
        // 20230701 zzg 如果直播间没有添加商品，直接返回空列表数据
        if (ObjectUtils.isEmpty(goodsListResponse.getLiveStreamGoodsVO())) {
            GoodsInfoViewPageResponse goodsInfoViewPageResponse = new GoodsInfoViewPageResponse();
            goodsInfoViewPageResponse.setBrands(new ArrayList<>());
            goodsInfoViewPageResponse.setCates(new ArrayList<>());
            goodsInfoViewPageResponse.setGoodses(new ArrayList<>());
            goodsInfoViewPageResponse.setGoodsIntervalPrices(new ArrayList<>());
            return BaseResponse.success(goodsInfoViewPageResponse);
        }
        Map<Object, Long> goodsStatus=goodsListResponse.getLiveStreamGoodsVO().stream().collect(Collectors.groupingByConcurrent(LiveStreamGoodsVO::getGoodsInfoId,Collectors.summingLong(p->p.getGoodsStatus())));
        Map<Object, Integer> goodsExplainFlag=goodsListResponse.getLiveStreamGoodsVO().stream().collect(Collectors.groupingByConcurrent(LiveStreamGoodsVO::getGoodsInfoId,Collectors.summingInt(p->p.getExplainFlag())));
        List<String> goodsInfoIds=goodsListResponse.getLiveStreamGoodsVO().stream().map(LiveStreamGoodsVO::getGoodsInfoId).collect(Collectors.toList());

        // 直播间商品，按店铺查询，不再按商品类型查询，增加为空判断逻辑
        Integer goodsType=ObjectUtils.isEmpty(liveStreamGoodsListRequest.getGoodsType()) ? null : liveStreamGoodsListRequest.getGoodsType().intValue();//0 批发 1散批
        Integer pageNum=liveStreamGoodsListRequest.getPageNum();
        Integer pageSize = liveStreamGoodsListRequest.getPageSize();

        if(goodsType!=null){
            request.setGoodsType(goodsType);
        }
        List<Long> brandIds=new ArrayList<>();
        BaseResponse<LiveRoomInfoResponse> liveRoom = liveRoomProvider.getInfo(Long.parseLong(liveStreamGoodsListRequest.getLiveRoomId()+""));
        LiveRoomInfoResponse liveRoomInfoResponse = liveRoom.getContext();
        if (null != liveRoomInfoResponse) {
            if(liveRoomInfoResponse.getSysFlag()==1){
                Map<String, String> brandMap=liveRoomInfoResponse.getBrandMap();
                if(Objects.nonNull(brandMap)) {
                    brandMap.forEach((key, value) -> {
                        brandIds.add(Long.parseLong(key));
                    });
                    //查询直播上下架商品
                    if(Objects.isNull(liveStreamGoodsListRequest.getGoodsStatus())) {
                        request.setBrandIds(brandIds);
                    }else{
                        if(goodsInfoIds.size()==0){
                            request.setLikeGoodsName("string");
                        }
                        request.setGoodsInfoIds(goodsInfoIds);
                    }
                }
            }else{
                if(goodsInfoIds.size()==0){
                    request.setLikeGoodsName("string");
                }
                request.setGoodsInfoIds(goodsInfoIds);
            }
        }
        // 20230711，直播间列表商品，只查询操作添加后的商品
        request.setGoodsInfoIds(goodsInfoIds);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        if(Objects.nonNull(liveStreamGoodsListRequest.getLikeGoodsName())){
            request.setLikeGoodsName(liveStreamGoodsListRequest.getLikeGoodsName());
        }
        if(Objects.nonNull(liveStreamGoodsListRequest.getLikeGoodsInfoNo())){
            request.setLikeGoodsInfoNo(liveStreamGoodsListRequest.getLikeGoodsInfoNo());
        }
        BaseResponse<GoodsInfoViewPageResponse> goodsInfoViewPageResponses=goodsInfoQueryProvider.pageViewWrapper(request);
        List<GoodsInfoVO> goodsInfoVOS= goodsInfoViewPageResponses.getContext().getGoodsInfoPage().getContent();

        List<String> goodsInfoIdsForWholeSale=goodsInfoVOS.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        // 格式化囤货信息
        List<PileActivityVO> context = Optional.ofNullable(pileActivityProvider.getStartPileActivity()).map(BaseResponse::getContext)
                .orElse(Lists.newArrayList());
        PileActivityVO pileActivityVO = null;
        Map<String, PileActivityGoodsVO> pileActivityGoodsVOHashMap = new HashMap<>();
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(context) && org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoIdsForWholeSale)){
            pileActivityVO = context.get(0);
            BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods =
                    pileActivityProvider.getStartPileActivityPileActivityGoods(
                            PileActivityPileActivityGoodsRequest.builder()
                                    .pileActivityId(pileActivityVO.getActivityId()).goodsInfoIds(goodsInfoIdsForWholeSale).build());
            BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
            if(CollectionUtils.isNotEmpty(pileActivityReturn.getContext())){
                pileActivityGoodsVOHashMap = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g,(a,b)->a));
            }
        }
        for (GoodsInfoVO goodsInfoVO : goodsInfoVOS){
        //goodsInfoVOS.forEach(goodsInfoVO -> {
            if(Objects.nonNull(goodsStatus.get(goodsInfoVO.getGoodsInfoId()))){
                goodsInfoVO.setLiveGoodsStatus(goodsStatus.get(goodsInfoVO.getGoodsInfoId()));
            }else{
                if(liveRoomInfoResponse.getSysFlag()==1) {
                    goodsInfoVO.setLiveGoodsStatus(2l);
                }else{
                    goodsInfoVO.setLiveGoodsStatus(0l);
                }
            }
            if(Objects.nonNull(goodsExplainFlag.get(goodsInfoVO.getGoodsInfoId()))){
                goodsInfoVO.setExplainFlag(goodsExplainFlag.get(goodsInfoVO.getGoodsInfoId()));
            }else{
                goodsInfoVO.setExplainFlag(0);
            }
            if(Objects.nonNull(pileActivityGoodsVOHashMap.get(goodsInfoVO.getGoodsInfoId()))){
                if(pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()){
                    goodsInfoVO.setPileFlag(ForcePileFlag.FORCEPILE);
                }else {
                    goodsInfoVO.setPileFlag(ForcePileFlag.PILE);
                }
            }else {
                goodsInfoVO.setPileFlag(ForcePileFlag.CLOSE);

            }
        }
        //);
        return goodsInfoViewPageResponses;
    }


    @ApiOperation(value = "直播商品记录列表")
    @PostMapping("/liveGoodsNewRecordList")
    public BaseResponse liveGoodsNewRecordList(@RequestBody LiveStreamPageRequest request) {
        LiveStreamLogInfoResponse liveStreamLogInfoResponse=liveStreamQueryProvider.streamLogInfo(request).getContext();
        if(Objects.nonNull(liveStreamLogInfoResponse.getLiveStreamLogInfoVO())){
            LiveStreamLogInfoVO liveStreamLogInfoVO=liveStreamLogInfoResponse.getLiveStreamLogInfoVO();
            GoodsInfoViewPageRequest goodsInfoViewPageRequest=new GoodsInfoViewPageRequest();
            Integer pageNum=request.getPageNum();
            Integer pageSize = request.getPageSize();
            goodsInfoViewPageRequest.setGoodsInfoIds(Arrays.asList(liveStreamLogInfoVO.getGoodsInfoIds().split(",")));
            if(goodsInfoViewPageRequest.getGoodsInfoIds().size()==0){
                goodsInfoViewPageRequest.setLikeGoodsName("string");
            }
            goodsInfoViewPageRequest.setPageNum(pageNum);
            goodsInfoViewPageRequest.setPageSize(pageSize);
            BaseResponse<GoodsInfoViewPageResponse> goodsInfoViewPageResponses=goodsInfoQueryProvider.pageViewWrapper(goodsInfoViewPageRequest);
            return goodsInfoViewPageResponses;
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "直播商品记录列表")
    @PostMapping("/liveGoodsRecordList")
    public BaseResponse liveGoodsRecordList(@RequestBody LiveStreamGoodsListRequest request) {
        List<GoodsInfoViewByIdResponse> goodsInfoVOList=new ArrayList<>();
        LiveStreamInfoRequest streamInfoRequest=new LiveStreamInfoRequest();
        streamInfoRequest.setLiveId(request.getLiveRoomId());
        LiveStreamVO liveStreamVO= liveStreamQueryProvider.streamInfo(streamInfoRequest).getContext().getContent();
        if(Objects.nonNull(liveStreamVO.getMediaUrl())) {
            for (String goodsInfoId : liveStreamVO.getMediaUrl().split(",")) {
                GoodsInfoViewByIdResponse responseGoodsInfo = goodsInfoQueryProvider.getViewById(GoodsInfoViewByIdRequest.builder().goodsInfoId(goodsInfoId).build()).getContext();
                goodsInfoVOList.add(responseGoodsInfo);
            }
        }
        return BaseResponse.success(goodsInfoVOList);
    }

    @ApiOperation(value = "直播间添加直播商品")
    @RequestMapping(value = "/goodsAdd", method = RequestMethod.POST)
    public BaseResponse add(@RequestBody @Valid LiveStreamGoodsAddRequest addReq) {
        liveStreamGoodsProvider.supplier(addReq);
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播商品管理", "直播间添加直播商品", "操作成功：直播间id" + (Objects.nonNull(addReq) ? addReq.getLiveRoomId() : ""));
        return BaseResponse.SUCCESSFUL();
    }


    @ApiOperation(value = "直播间批量上下架直播商品或移除商品")
    @RequestMapping(value = "/saleGoodsBatch", method = RequestMethod.POST)
    public BaseResponse saleGoodsBatch(@RequestBody @Valid LiveStreamGoodsModifyRequest modifyReq) {
        liveStreamGoodsProvider.saleGoodsBatch(modifyReq);
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播商品管理", "直播间批量上下架直播商品或移除商品", "操作成功：直播间id" + (Objects.nonNull(modifyReq) ? modifyReq.getLiveRoomId() : ""));
        return BaseResponse.SUCCESSFUL();
    }


    @ApiOperation(value = "直播间上下架直播商品")
    @RequestMapping(value = "/saleGoods", method = RequestMethod.POST)
    public BaseResponse goodsDown(@RequestBody @Valid LiveStreamGoodsModifyRequest modifyReq) {
        liveStreamGoodsProvider.saleGoods(modifyReq);
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播商品管理", "直播间上下架直播商品", "操作成功：直播间id" + (Objects.nonNull(modifyReq) ? modifyReq.getLiveRoomId() : ""));
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "直播间设置直播商品")
    @RequestMapping(value = "/goodsExplain", method = RequestMethod.POST)
    public BaseResponse goodsExplain(@RequestBody @Valid LiveStreamGoodsModifyRequest modifyReq) {
        modifyReq.setExplainFlag(0);
        liveStreamGoodsProvider.modify(modifyReq);
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播商品管理", "直播间设置直播商品", "操作成功：直播间id" + (Objects.nonNull(modifyReq) ? modifyReq.getLiveRoomId() : ""));
        return BaseResponse.SUCCESSFUL();
    }

}
