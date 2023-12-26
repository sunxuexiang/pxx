package com.wanmi.sbc.goods.provider.impl.marketing;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingProvider;
import com.wanmi.sbc.goods.api.request.marketing.*;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingModifyResponse;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingSyncResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import com.wanmi.sbc.goods.marketing.model.data.GoodsMarketing;
import com.wanmi.sbc.goods.marketing.service.GoodsMarketingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description: 商品营销控制器
 * @Date: 2018-11-07 13:46
 */
@Validated
@RestController
public class GoodsMarketingController implements GoodsMarketingProvider {

    @Autowired
    private GoodsMarketingService goodsMarketingService;

    /**
     * @param request 根据用户编号删除商品使用的营销 {@link GoodsMarketingDeleteByCustomerIdRequest}
     * @return
     */
    @Override
    
    public BaseResponse deleteByCustomerId(@RequestBody @Valid GoodsMarketingDeleteByCustomerIdRequest request) {
        return BaseResponse.success(goodsMarketingService.delByCustomerId(request.getCustomerId()));
    }

    /**
     * @param request 根据用户编号和商品编号列表删除商品使用的营销 {@link GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest}
     * @return
     */
    @Override
    
    public BaseResponse deleteByCustomerIdAndGoodsInfoIds(@RequestBody @Valid GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest request) {
        return BaseResponse.success(goodsMarketingService.delByCustomerIdAndGoodsInfoIds(
                request.getCustomerId(), request.getGoodsInfoIds()));
    }

    /**
     * @param request 批量添加商品使用的营销 {@link GoodsMarketingBatchAddRequest}
     * @return
     */
    @Override
    
    public BaseResponse batchAdd(@RequestBody @Valid GoodsMarketingBatchAddRequest request) {


        List<GoodsMarketing> goodsMarketings =  KsBeanUtil.convertList(
                request.getGoodsMarketings(), GoodsMarketing.class);

        return BaseResponse.success(goodsMarketingService.batchAdd(goodsMarketings));
    }

    /**
     * @param request 修改商品使用的营销 {@link GoodsMarketingModifyRequest}
     * @return
     */
    @Override
    
    public BaseResponse<GoodsMarketingModifyResponse> modify(@RequestBody @Valid GoodsMarketingModifyRequest request) {

        GoodsMarketing goodsMarketing = new GoodsMarketing();
        KsBeanUtil.copyPropertiesThird(request, goodsMarketing);

        GoodsMarketingModifyResponse goodsMarketingModifyResponse = new GoodsMarketingModifyResponse();
        KsBeanUtil.copyPropertiesThird(goodsMarketingService.modify(goodsMarketing), goodsMarketingModifyResponse);

        return BaseResponse.success(goodsMarketingModifyResponse);
    }

    @Override
    public BaseResponse<GoodsMarketingSyncResponse> syncGoodsMarketings(@RequestBody @Valid GoodsMarketingSyncRequest request) {
        List<GoodsMarketing> goodsMarketingList = goodsMarketingService.queryGoodsMarketingList(request.getCustomerId());
        Map<String, List<Long>> marketingIdsMap = request.getMarketingIdsMap();
        if (marketingIdsMap.isEmpty() && CollectionUtils.isNotEmpty(goodsMarketingList)) {
            goodsMarketingService.delByCustomerId(request.getCustomerId());
            goodsMarketingList = new ArrayList<>();
        } else if(!marketingIdsMap.isEmpty()) {

            // 借助hashmap去重
            Map<String, GoodsMarketing> goodsMarketingMaps = new HashMap<>();
            for (GoodsMarketing goodsMarketing : goodsMarketingList){
                String goodsInfoId = goodsMarketing.getGoodsInfoId();
                goodsMarketingMaps.put(goodsInfoId, goodsMarketing);
            }
            List<GoodsMarketing> newGoodsMarketingList = new ArrayList<>();
            for (Map.Entry<String, GoodsMarketing> goodsMarketingEntry : goodsMarketingMaps.entrySet()) {
                newGoodsMarketingList.add(goodsMarketingEntry.getValue());
            }
            goodsMarketingList = newGoodsMarketingList;


            Map<String, Long> oldMap = goodsMarketingList.stream()
                    .collect(Collectors.toMap(GoodsMarketing::getGoodsInfoId, GoodsMarketing::getMarketingId, (a,b)->a));

            List<String> delList = goodsMarketingList.stream()
                    .filter(goodsMarketing -> {
                        String goodsInfoId = goodsMarketing.getGoodsInfoId();

                        // 数据库里存的采购单商品没有参与营销或者选择的营销不存在了，则要删除该条记录
                        return marketingIdsMap.get(goodsInfoId) == null
                                || !marketingIdsMap.get(goodsInfoId).stream().anyMatch(marketingId ->
                                marketingId.equals(goodsMarketing.getMarketingId()));
                    }).map(GoodsMarketing::getGoodsInfoId).collect(Collectors.toList());

            List<GoodsMarketing> addList = marketingIdsMap.entrySet().stream()
                    .filter(set -> oldMap.get(set.getKey()) == null
                            || delList.contains(set.getKey()))
                    .map(set -> GoodsMarketing.builder()
                            .customerId(request.getCustomerId())
                            .goodsInfoId(set.getKey())
                            .marketingId(set.getValue().get(0))
                            .build())
                    .collect(Collectors.toList());

            // 先删除
            if (!delList.isEmpty()) {
                goodsMarketingService.delByCustomerIdAndGoodsInfoIds(request.getCustomerId(), delList);
                goodsMarketingList = goodsMarketingList.stream()
                        .filter(goodsMarketing -> !delList.contains(goodsMarketing.getGoodsInfoId()))
                        .collect(Collectors.toList());
            }

            // 再增加
            if (!addList.isEmpty()) {
                List<GoodsMarketing> goodsMarketings = addList.stream().map(info -> {
                    GoodsMarketing goodsMarketing = new GoodsMarketing();
                    goodsMarketing.setCustomerId(info.getCustomerId());
                    goodsMarketing.setGoodsInfoId(info.getGoodsInfoId());
                    goodsMarketing.setMarketingId(info.getMarketingId());
                    goodsMarketing.setId(info.getId());
                    return goodsMarketing;
                }).collect(Collectors.toList());
                List<GoodsMarketing> saveList = goodsMarketingService.batchAdd(goodsMarketings);
                goodsMarketingList.addAll(saveList);
            }
        }
        GoodsMarketingSyncResponse goodsMarketingSyncResponse = new GoodsMarketingSyncResponse();
        List<GoodsMarketingVO> goodsMarketingVOList = KsBeanUtil.convertList(goodsMarketingList, GoodsMarketingVO.class);
        goodsMarketingSyncResponse.setGoodsMarketingList(goodsMarketingVOList);
        return BaseResponse.success(goodsMarketingSyncResponse);
    }

    /**
     * 变动购物车商品数量后修改或生成促销关联记录
     * @param request
     * @return
     */
    @Override
    public BaseResponse mergeGoodsMarketings(@RequestBody @Valid GoodsMarketingSyncRequest request) {
        Map<String, List<Long>> marketingIdsMap = request.getMarketingIdsMap();
        List<GoodsMarketing> addList = marketingIdsMap.entrySet().stream()
                .map(set -> GoodsMarketing.builder()
                        .customerId(request.getCustomerId())
                        .goodsInfoId(set.getKey())
                        .marketingId(set.getValue().get(0))
                        .build())
                .collect(Collectors.toList());

        // 增加
        if (!addList.isEmpty()) {
            addList.stream().forEach(info -> {
                GoodsMarketing goodsMarketing = new GoodsMarketing();
                goodsMarketing.setCustomerId(info.getCustomerId());
                goodsMarketing.setGoodsInfoId(info.getGoodsInfoId());
                goodsMarketing.setMarketingId(info.getMarketingId());
                goodsMarketing.setId(info.getId());
                goodsMarketingService.modify(goodsMarketing);
            });
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除固定的营销
     * @param request
     * @return
     */
    @Override
    public BaseResponse deleteGoodsMarketings(@Valid GoodsMarketingDeleteByMaketingIdAndGoodsInfoIdsRequest request) {
        int i = goodsMarketingService.deleteByMarketingIdAndGoodsInfoId(request.getMarketingId(), request.getGoodsInfoId());
        return BaseResponse.SUCCESSFUL();
    }
}
