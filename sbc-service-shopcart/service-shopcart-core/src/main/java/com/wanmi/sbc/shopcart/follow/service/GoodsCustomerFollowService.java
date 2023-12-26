package com.wanmi.sbc.shopcart.follow.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyCollectNumRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoFillGoodsStatusRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.shopcart.bean.enums.FollowFlag;
import com.wanmi.sbc.shopcart.cart.mq.GoodsCollectNumMq;
import com.wanmi.sbc.shopcart.follow.model.root.GoodsCustomerFollow;
import com.wanmi.sbc.shopcart.follow.reponse.GoodsCustomerFollowResponse;
import com.wanmi.sbc.shopcart.follow.repository.GoodsCustomerFollowRepository;
import com.wanmi.sbc.shopcart.follow.request.GoodsCustomerFollowQueryRequest;
import com.wanmi.sbc.shopcart.follow.request.GoodsCustomerFollowRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品收藏服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class GoodsCustomerFollowService {

    private static final String GOODSFOLLOW = "follow";

    private static final String CAMCELGOODSFOLLOW = "cancel";

    @Autowired
    private GoodsCustomerFollowRepository goodsCustomerFollowRepository;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;

    @Autowired
    private GoodsCollectNumMq goodsCollectNumMq;

    /**
     * 新增商品收藏
     *
     * @param request 参数
     */
    @Transactional
    public void save(GoodsCustomerFollowRequest request) {
        GoodsInfoVO goodsInfo = null;
        if(2 == request.getSubType()){ // 2代表散批
            goodsInfo = bulkGoodsInfoQueryProvider.getBulkById(GoodsInfoByIdRequest.builder().goodsInfoId(request.getGoodsInfoId()).build()).getContext();
        } else {
             goodsInfo = goodsInfoQueryProvider.getById(
                    GoodsInfoByIdRequest.builder().goodsInfoId(request.getGoodsInfoId()).build()
            ).getContext();
        }
        if (goodsInfo == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        //限制个数
        GoodsCustomerFollowQueryRequest queryRequest = GoodsCustomerFollowQueryRequest.builder()
                .customerId(request.getCustomerId())
                .build();
        Long count = goodsCustomerFollowRepository.count(queryRequest.getWhereCriteria());
        if (count >= Constants.FOLLOW_MAX_SIZE) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_030401, new Object[]{Constants.FOLLOW_MAX_SIZE});
        }

        //查询是否存在
        queryRequest.setFollowFlag(null);
        queryRequest.setGoodsInfoId(request.getGoodsInfoId());
        List<GoodsCustomerFollow> followList = goodsCustomerFollowRepository.findAll(queryRequest.getWhereCriteria());
        //如果不存在新增
        if (CollectionUtils.isEmpty(followList)) {
            GoodsCustomerFollow follow = new GoodsCustomerFollow();
            follow.setCompanyInfoId(goodsInfo.getCompanyInfoId());
            follow.setGoodsInfoId(request.getGoodsInfoId());
            follow.setCustomerId(request.getCustomerId());
            follow.setGoodsId(goodsInfo.getGoodsId());
            follow.setFollowTime(LocalDateTime.now());
            follow.setCreateTime(follow.getFollowTime());
            follow.setGoodsNum(1L);
            follow.setFollowFlag(FollowFlag.FOLLOW);
            if(2 == request.getSubType()){
                follow.setFollowType(2);// 2代表散批
                follow.setWareId(request.getBulkWareId());
            } else {
                follow.setFollowType(0);// 1代表批发
                follow.setWareId(request.getWareId());
            }
            follow.setParentGoodsInfoId(goodsInfo.getParentGoodsInfoId());
            goodsCustomerFollowRepository.save(follow);
            //更新商品收藏
            updateGoodsCollectNum(request.getGoodsInfoId(),GOODSFOLLOW, request.getSubType());
        }
    }

    /**
     * 商品收藏
     *
     * @param queryRequest 参数
     * @return 商品收藏列表结果
     * @throws SbcRuntimeException
     */
    public GoodsCustomerFollowResponse list(GoodsCustomerFollowQueryRequest queryRequest) throws SbcRuntimeException {
        //分页查询SKU信息列表
        Page<GoodsCustomerFollow> follows = goodsCustomerFollowRepository.findAll(queryRequest.getWhereCriteria(), queryRequest.getPageRequest());
        if (CollectionUtils.isEmpty(follows.getContent())) {
            return GoodsCustomerFollowResponse.builder().goodsInfos(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageRequest(), follows.getTotalElements())).build();
        }
        // 记录商品ID和收藏时间的关系
        Map<String, LocalDateTime> followMap = follows.getContent().stream().collect(Collectors.toMap(GoodsCustomerFollow::getGoodsInfoId, GoodsCustomerFollow::getFollowTime));

        Map<Integer, List<GoodsCustomerFollow>> collect = follows.getContent().stream().collect(Collectors.groupingBy(GoodsCustomerFollow::getFollowType));
        GoodsInfoViewByIdsResponse response = null;

        List<GoodsCustomerFollow> goodsCustomerFollowsFromWhole = collect.get(0); // 0表示批发
        if(CollectionUtils.isNotEmpty(goodsCustomerFollowsFromWhole)){
            GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
            goodsInfoRequest.setGoodsInfoIds(goodsCustomerFollowsFromWhole.stream().map(GoodsCustomerFollow::getGoodsInfoId).collect(Collectors.toList()));
            goodsInfoRequest.setIsHavSpecText(Constants.yes);//需要显示规格值
            goodsInfoRequest.setWareId(queryRequest.getWareId());
            response = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();

            List<GoodsInfoVO> goodsInfoVOS = Optional.ofNullable(response).map(GoodsInfoViewByIdsResponse::getGoodsInfos).orElse(Lists.newArrayList());
            for (GoodsInfoVO goodsInfoVO : goodsInfoVOS){
                goodsInfoVO.setCreateTime(followMap.get(goodsInfoVO.getGoodsInfoId()));
            }
            if(Objects.nonNull(response)){
                response.setGoodsInfos(goodsInfoVOS);
            }
        }

        List<GoodsCustomerFollow> goodsCustomerFollowsFromBulk = collect.get(2);  // 2表示散批
        if(CollectionUtils.isNotEmpty(goodsCustomerFollowsFromBulk)){
            GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
            goodsInfoRequest.setGoodsInfoIds(goodsCustomerFollowsFromBulk.stream().map(GoodsCustomerFollow::getGoodsInfoId).collect(Collectors.toList()));
            goodsInfoRequest.setIsHavSpecText(Constants.yes);//需要显示规格值
            goodsInfoRequest.setWareId(queryRequest.getBulkWareId());
            GoodsInfoViewByIdsResponse context = bulkGoodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
            if(Objects.isNull(response)){
                response = context;
            } else {
                List<GoodsInfoVO> goodsInfoVOS = Optional.ofNullable(context).map(GoodsInfoViewByIdsResponse::getGoodsInfos).orElse(Lists.newArrayList());
                for (GoodsInfoVO goodsInfoVO : goodsInfoVOS){
                    goodsInfoVO.setCreateTime(followMap.get(goodsInfoVO.getGoodsInfoId()));
                }
                goodsInfoVOS.addAll(response.getGoodsInfos());

                List<GoodsVO> goodsVOS = Optional.ofNullable(context).map(GoodsInfoViewByIdsResponse::getGoodses).orElse(Lists.newArrayList());
                goodsVOS.addAll(response.getGoodses());

                response = GoodsInfoViewByIdsResponse.builder()
                        .goodsInfos(goodsInfoVOS)
                        .goodses(goodsVOS)
                        .build();
            }
        }
        Map<String, GoodsInfoVO> goodsInfoMap = response.getGoodsInfos()
                .stream()
                .collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));
        List<GoodsInfoVO> goodsInfos = follows.getContent()
                .stream()
                .map(goodsCustomerFollow -> goodsInfoMap.get(goodsCustomerFollow.getGoodsInfoId()))
                .collect(Collectors.toList());
        return GoodsCustomerFollowResponse.builder()
                .goodses(response.getGoodses())
                .goodsInfos(new MicroServicePage<>(goodsInfos, queryRequest.getPageRequest(), follows.getTotalElements()))
                .build();
    }

    /**
     * 取消商品收藏
     *
     * @param request 参数
     */
    @Transactional
    public void delete(GoodsCustomerFollowRequest request) {
        List<GoodsCustomerFollow> followList = goodsCustomerFollowRepository.findAll(GoodsCustomerFollowQueryRequest.builder()
                .goodsInfoIds(request.getGoodsInfoIds())
                .customerId(request.getCustomerId())
                .build().getWhereCriteria());
        if (CollectionUtils.isNotEmpty(followList)) {
            //物理删除
            List<Long> followIds = followList.stream().map(GoodsCustomerFollow::getFollowId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(followIds)) {
                goodsCustomerFollowRepository.deleteByFollowIds(followIds, request.getCustomerId());
                updateGoodsCollectNum(request.getGoodsInfoIds().get(0),CAMCELGOODSFOLLOW, request.getSubType());
            }
        }
    }

    /**
     * 删除失效商品
     *
     * @param request 参数
     */
    @Transactional
    public void deleteInvalidGoods(GoodsCustomerFollowRequest request) {
        List<GoodsCustomerFollow> followList = goodsCustomerFollowRepository.findAll(GoodsCustomerFollowQueryRequest.builder()
                .customerId(request.getCustomerId())
                .build().getWhereCriteria());
        if (CollectionUtils.isEmpty(followList)) {
            return;
        }

        Map<String, Boolean> infoIds = getInvalidGoods(followList);
        if (MapUtils.isEmpty(infoIds)) {
            return;
        }
        //物理删除
        List<Long> followIds = followList.stream().filter(goodsCustomerFollow -> infoIds.containsKey(goodsCustomerFollow.getGoodsInfoId()))
                .map(GoodsCustomerFollow::getFollowId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(followIds)) {
            goodsCustomerFollowRepository.deleteByFollowIds(followIds, request.getCustomerId());
        }
    }

    /**
     * 是否有失效商品
     *
     * @param request 参数
     */
    public boolean haveInvalidGoods(GoodsCustomerFollowRequest request) {
        List<GoodsCustomerFollow> followList = goodsCustomerFollowRepository.findAll(GoodsCustomerFollowQueryRequest.builder()
                .customerId(request.getCustomerId())
                .build().getWhereCriteria());
        return CollectionUtils.isNotEmpty(followList) && MapUtils.isNotEmpty(getInvalidGoods(followList));
    }


    /**
     * 获取失效商品
     *
     * @param followList 收藏数据
     * @return 失效商品Map
     */
    private Map<String, Boolean> getInvalidGoods(List<GoodsCustomerFollow> followList) {
        //提取SKU
        List<String> goodsInfoIds = followList.stream().map(GoodsCustomerFollow::getGoodsInfoId)
                .collect(Collectors.toList());

        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(
                GoodsInfoListByIdsRequest.builder().goodsInfoIds(goodsInfoIds).build()
        ).getContext().getGoodsInfos();
        if (CollectionUtils.isEmpty(goodsInfos)) {
            return null;
        }
        //填充失效状态
        goodsInfos = goodsInfoProvider.fillGoodsStatus(
                GoodsInfoFillGoodsStatusRequest.builder()
                        .goodsInfos(KsBeanUtil.convertList(goodsInfos, GoodsInfoDTO.class))
                        .build()
        ).getContext().getGoodsInfos();
        //填装Map
        return goodsInfos.stream()
                .filter(goodsInfo -> Objects.equals(GoodsStatus.INVALID, goodsInfo.getGoodsStatus()))
                .collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, c -> Boolean.TRUE));
    }

    /**
     * 验证SKU是否已收藏
     *
     * @param request 参数
     * @return 已收藏的SkuId
     */
    public List<String> isFollow(GoodsCustomerFollowRequest request) {
        List<GoodsCustomerFollow> follows = goodsCustomerFollowRepository.findAll(GoodsCustomerFollowQueryRequest.builder()
                .customerId(request.getCustomerId())
                .goodsInfoIds(request.getGoodsInfoIds())
                .build().getWhereCriteria());
        if (CollectionUtils.isEmpty(follows)) {
            return Collections.emptyList();
        }
        return follows.stream().map(GoodsCustomerFollow::getGoodsInfoId).collect(Collectors.toList());
    }

    /**
     * 统计收藏表
     * @param queryRequest
     * @return
     */
    public Long count(GoodsCustomerFollowQueryRequest queryRequest){
        return goodsCustomerFollowRepository.count(queryRequest.getWhereCriteria());
    }

    /**
     * @Author lvzhenwei
     * @Description 更新商品收藏量
     * @Date 16:22 2019/4/11
     * @Param [request]
     * @return void
     **/
    private void updateGoodsCollectNum(String goodsInfoId, String followType, Integer subType){
        GoodsInfoByIdRequest goodsInfoByIdRequest = new GoodsInfoByIdRequest();
        goodsInfoByIdRequest.setGoodsInfoId(goodsInfoId);
        GoodsInfoByIdResponse goodsInfoByIdResponse = null;
        if(2 == subType){
            goodsInfoByIdResponse = bulkGoodsInfoQueryProvider.getBulkById(goodsInfoByIdRequest).getContext();
        } else {
            goodsInfoByIdResponse = goodsInfoQueryProvider.getById(goodsInfoByIdRequest).getContext();
        }
        GoodsModifyCollectNumRequest goodsModifyCollectNumRequest = new GoodsModifyCollectNumRequest();
        if(followType.equals(GOODSFOLLOW)){
            goodsModifyCollectNumRequest.setGoodsCollectNum(1L);
        } else {
            goodsModifyCollectNumRequest.setGoodsCollectNum(-1L);
        }
        goodsModifyCollectNumRequest.setSubType(subType);
        goodsModifyCollectNumRequest.setGoodsId(goodsInfoByIdResponse.getGoodsId());
        goodsCollectNumMq.updateGoodsCollectNum(goodsModifyCollectNumRequest);
    }
}
