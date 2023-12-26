package com.wanmi.sbc.live.goods.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsAddRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsModifyRequest;
import com.wanmi.sbc.live.bean.vo.LiveGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamGoodsVO;
import com.wanmi.sbc.live.goods.dao.LiveStreamGoodsMapper;
import com.wanmi.sbc.live.goods.model.root.LiveStreamGoods;
import com.wanmi.sbc.live.room.dao.LiveRoomMapper;
import com.wanmi.sbc.live.room.model.root.LiveRoom;
import com.wanmi.sbc.live.room.service.LiveRoomService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("LiveStreamGoodsService")
public class LiveStreamGoodsService {

    @Autowired
    private LiveStreamGoodsMapper liveStreamGoodsMapper;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private LiveRoomService liveRoomService;

    /**
     * 直播导入商品
     */
    @Transactional
    public void addGoods(LiveStreamGoodsAddRequest supplierAddReq) {

        // 查询直播间
        LiveRoom liveRoom = liveRoomService.getInfo(supplierAddReq.getLiveRoomId() == null ? null : supplierAddReq.getLiveRoomId().longValue());

        supplierAddReq.getGoodsInfoIds().stream().forEach(entity -> {
            List<LiveStreamGoods> dbGoods = liveStreamGoodsMapper.findByLiveRoomAndGoodsInfoId(liveRoom.getLiveRoomId(), entity);
            if (!ObjectUtils.isEmpty(dbGoods)) {
                return;
            }

         LiveStreamGoods convert = new LiveStreamGoods();

         // 插入parent_goods_id
         List<GoodsInfoVO> goodsInfoVOS = Optional.ofNullable(goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(entity).build()))
                .map(BaseResponse::getContext)
                .map(GoodsInfoByIdResponse::getGoodsInfos).orElse(Lists.newArrayList());
         if(CollectionUtils.isNotEmpty(goodsInfoVOS)){
             GoodsInfoVO goodsInfoVO = goodsInfoVOS.get(0);
             String parentGoodsInfoId = goodsInfoVO.getParentGoodsInfoId();
             convert.setParentGoodsInfoId(parentGoodsInfoId);
             convert.setWareId(goodsInfoVO.getWareId());
         }
         if (liveRoom != null) {
             convert.setStoreId(liveRoom.getStoreId());
         }
         convert.setDelFlag(0);
         convert.setGoodsInfoId(entity);
         convert.setLiveId(supplierAddReq.getLiveRoomId());
         convert.setGoodsStatus(1l);
         if(Objects.nonNull(supplierAddReq.getGoodsType())){
             if(supplierAddReq.getGoodsType()==1l){
                 convert.setGoodsType(1l);
             }else{
                 convert.setGoodsType(0l);
             }
         }
         System.out.println(convert.getGoodsType()+"------");
         convert.setExplainFlag(0);
         if(liveStreamGoodsMapper.countGoodsByGoodsInfoId(entity,supplierAddReq.getLiveRoomId())==0){
             liveStreamGoodsMapper.insertSelective(convert);
         }
        });
    }

    /**
     * 查看直播商品列表
     * @return
     */
    public List<LiveGoodsVO> getStreamGoodsInfoByReq(LiveStreamGoodsListRequest liveStreamGoodsListRequest){
        List<LiveStreamGoods> liveStreamGoodsList=liveStreamGoodsMapper.findListByReq(liveStreamGoodsListRequest);
        /**
         * 去重处理（数据库直播间有重复的商品）
         */
        List<LiveStreamGoods> newGoods = filterRepeatGoods(liveStreamGoodsList);

        List<LiveGoodsVO> liveStreamGoodsVOList=KsBeanUtil.copyListProperties(newGoods, LiveGoodsVO.class);
        return liveStreamGoodsVOList;
    }

    /**
     * 去重处理（数据库直播间有重复的商品）
     */
    private static List<LiveStreamGoods> filterRepeatGoods(List<LiveStreamGoods> liveStreamGoodsList) {
        List<LiveStreamGoods> newGoods = new ArrayList<>();
        Set<String> idSet = new HashSet<>();
        for (LiveStreamGoods liveStreamGoods : liveStreamGoodsList) {
            if (idSet.contains(liveStreamGoods.getGoodsInfoId())) {
                continue;
            }
            idSet.add(liveStreamGoods.getGoodsInfoId());
            newGoods.add(liveStreamGoods);
        }
        return newGoods;
    }

    /**
     * 查看直播商品列表
     * @return
     */
    public List<LiveStreamGoodsVO> getStreamGoodsByReq(LiveStreamGoodsListRequest liveStreamGoodsListRequest){
        List<LiveStreamGoods> liveStreamGoodsList=liveStreamGoodsMapper.findListByReq(liveStreamGoodsListRequest);
        /**
         * 去重处理（数据库直播间有重复的商品）
         */
        List<LiveStreamGoods> newGoods = filterRepeatGoods(liveStreamGoodsList);
        List<LiveStreamGoodsVO> liveStreamGoodsVOList=KsBeanUtil.copyListProperties(newGoods, LiveStreamGoodsVO.class);
        return liveStreamGoodsVOList;
    }

    /**
     * 移除直播商品
     */
    public void modifyStreamGoods(String goodsInfoId){
        LiveStreamGoods streamGoods=new LiveStreamGoods();
        streamGoods.setGoodsInfoId(goodsInfoId);
        streamGoods.setDelFlag(1);
        liveStreamGoodsMapper.updateByPrimaryKeySelective(streamGoods);
    }

    public void updateStreamGoods(String goodsInfoId){
        LiveStreamGoods streamGoods=new LiveStreamGoods();
        streamGoods.setGoodsInfoId(goodsInfoId);
        streamGoods.setExplainFlag(0);
        liveStreamGoodsMapper.updateByPrimaryKeySelective(streamGoods);
    }

    public void saleGoods(LiveStreamGoodsModifyRequest supplierModifyReq){
        LiveStreamGoods streamGoods=new LiveStreamGoods();
        streamGoods.setLiveId(supplierModifyReq.getLiveRoomId());
        streamGoods.setGoodsInfoId(supplierModifyReq.getGoodsInfoId());
        streamGoods.setGoodsStatus(supplierModifyReq.getGoodsStatus());
        liveStreamGoodsMapper.updateByPrimaryKeySelective(streamGoods);
    }

    public void saleGoodsBatch(LiveStreamGoodsModifyRequest supplierModifyReq){
        liveStreamGoodsMapper.updateBatchByLiveIdAndGoodsInfoIds(supplierModifyReq);
    }

}
