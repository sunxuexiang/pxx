package com.wanmi.sbc.marketing.gift.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelMapByCustomerIdAndStoreIdsRequest;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelMapGetResponse;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftDetailRepository;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftLevelRepository;
import com.wanmi.sbc.marketing.gift.request.MarketingFullGiftSaveRequest;
import com.wanmi.sbc.marketing.gift.response.MarketingFullGiftLevelResponse;
import com.wanmi.sbc.marketing.plugin.impl.CustomerLevelPlugin;
import com.wanmi.sbc.marketing.redis.RedisService;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 营销满赠业务
 */
@Service
@Slf4j
public class MarketingFullGiftService {

    @Autowired
    private MarketingFullGiftLevelRepository marketingFullGiftLevelRepository;

    @Autowired
    private MarketingFullGiftDetailRepository marketingFullGiftDetailRepository;

    @Autowired
    private MarketingService marketingService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private CustomerLevelPlugin customerLevelPlugin;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private RedisService redisService;

    /**
     * 新增满赠
     */
    @Transactional(rollbackFor = Exception.class)
    public Marketing addMarketingFullGift(MarketingFullGiftSaveRequest request) throws SbcRuntimeException {

        Marketing marketing = marketingService.addMarketing(request);


        // 保存多级优惠信息
        List<MarketingFullGiftLevel> marketingFullGiftLevels = request.generateFullGiftLevelList(marketing.getMarketingId());
        this.saveLevelList(marketingFullGiftLevels);
        //保存活动商品信息
        List<MarketingFullGiftDetail> marketingFullGiftDetails = request.generateFullGiftDetailList(request.getFullGiftLevelList());
        this.saveLevelGiftDetailList(marketingFullGiftDetails);

        //赠品限赠数量存储到redis
        marketingFullGiftDetails.forEach(var ->{
            if(Objects.nonNull(var.getBoundsNum()) && var.getBoundsNum() > 0){
                marketingService.updateMarketingGiftNum(marketing.getMarketingId().toString(),marketingFullGiftLevels.get(0).getGiftLevelId().toString(),var.getProductId(),var.getBoundsNum());
            }
        });
        return marketing;
    }

    /**
     * 修改满赠
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyMarketingFullGift(MarketingFullGiftSaveRequest request) throws SbcRuntimeException {
        marketingService.modifyMarketing(request);

        // 先删除已有的多级优惠信息，然后再保存
        marketingFullGiftLevelRepository.deleteByMarketingId(request.getMarketingId());

        List<MarketingFullGiftLevel> marketingFullGiftLevels = request.generateFullGiftLevelList(request.getMarketingId());
        this.saveLevelList(marketingFullGiftLevels);

        // 先删除已有的赠品信息，然后再保存
        marketingFullGiftDetailRepository.deleteByMarketingId(request.getMarketingId());
        //保存活动商品信息
        List<MarketingFullGiftDetail> marketingFullGiftDetails = request.generateFullGiftDetailList(request.getFullGiftLevelList());
        this.saveLevelGiftDetailList(marketingFullGiftDetails);

        //赠品限赠数量存储到redis
        marketingFullGiftDetails.forEach(var ->{
            if(Objects.nonNull(var.getBoundsNum()) && var.getBoundsNum() > 0){
                marketingService.updateMarketingGiftNum(request.getMarketingId().toString(),marketingFullGiftLevels.get(0).getGiftLevelId().toString(),var.getProductId(),var.getBoundsNum());
            }
        });
    }

    /**
     * 保存多级优惠信息
     */
    private void saveLevelList(List<MarketingFullGiftLevel> fullGiftLevelList) {
        if(CollectionUtils.isNotEmpty(fullGiftLevelList)) {
            marketingFullGiftLevelRepository.saveAll(fullGiftLevelList);
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }

    /**
     * 保存多级优惠赠品信息
     */
    private void saveLevelGiftDetailList(List<MarketingFullGiftDetail> giftDetailList) {
        if(CollectionUtils.isNotEmpty(giftDetailList)) {
            marketingFullGiftDetailRepository.saveAll(giftDetailList);
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }

    /**
     * 获取营销等级信息，包含等级下的detail
     *
     * @param marketingId
     * @return
     */
    public List<MarketingFullGiftLevel> getLevelsByMarketingId(Long marketingId){
        List<MarketingFullGiftLevel> levelList = marketingFullGiftLevelRepository.findByMarketingIdOrderByFullAmountAscFullCountAsc(marketingId);
        if(CollectionUtils.isNotEmpty(levelList)){
            List<MarketingFullGiftDetail> detailList = marketingFullGiftDetailRepository.findByMarketingId(marketingId);
            if(CollectionUtils.isNotEmpty(detailList)){
                Map<Long, List<MarketingFullGiftDetail>> map = detailList.stream().collect(Collectors.groupingBy(MarketingFullGiftDetail::getGiftLevelId));
                levelList.forEach(level -> level.setFullGiftDetailList(map.get(level.getGiftLevelId())));
            }
        }
        return levelList;
    }

    /**
     * 根据活动获取赠品列表
     * @param marketingId
     * @param customer
     * @return
     */
    public MarketingFullGiftLevelResponse getGiftListBoss(Long marketingId, CustomerVO customer,Boolean matchWareHouseFlag){
        MarketingFullGiftLevelResponse response = new MarketingFullGiftLevelResponse();
        List<MarketingFullGiftLevel> levelList = getLevelsByMarketingId(marketingId);
        if (CollectionUtils.isEmpty(levelList)) {
            return response;
        }
        levelList.forEach(v->{
            List<MarketingFullGiftDetail> collect = v.getFullGiftDetailList().stream().collect(Collectors.toList());
            v.setFullGiftDetailList(collect);
        });

        List<String> goodsInfoIds = levelList.stream()
                .map(MarketingFullGiftLevel::getFullGiftDetailList)
                .flatMap(Collection::stream)
                .map(MarketingFullGiftDetail::getProductId)
                .collect(Collectors.toList());
        //获取SKU信息
        ;
        response.setGiftList(
                goodsInfoQueryProvider.listViewByIds(
                        GoodsInfoViewByIdsRequest.builder().goodsInfoIds(goodsInfoIds).matchWareHouseFlag(matchWareHouseFlag)
                                .isHavSpecText(Constants.yes).build()
                ).getContext().getGoodsInfos()
        );
        //获取客户的等级
        if(Objects.nonNull(customer)) {
            List<Long> storeIds = response.getGiftList().stream()
                    .map(GoodsInfoVO::getStoreId)
                    .filter(Objects::nonNull)
                    .distinct().collect(Collectors.toList());
            Map<Long, CommonLevelVO> levelMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(storeIds)) {
                CustomerLevelMapByCustomerIdAndStoreIdsRequest customerLevelMapByCustomerIdAndStoreIdsRequest = new CustomerLevelMapByCustomerIdAndStoreIdsRequest();
                customerLevelMapByCustomerIdAndStoreIdsRequest.setCustomerId(customer.getCustomerId());
                customerLevelMapByCustomerIdAndStoreIdsRequest.setStoreIds(storeIds);
                BaseResponse<CustomerLevelMapGetResponse> customerLevelMapGetResponseBaseResponse =
                        customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds(customerLevelMapByCustomerIdAndStoreIdsRequest);
                Map<Long, CommonLevelVO> tmpLevelMap = customerLevelMapGetResponseBaseResponse.getContext()
                        .getCommonLevelVOMap();
                levelMap.putAll(tmpLevelMap);
            }
            //计算会员价
            customerLevelPlugin.goodsListFilter(response.getGiftList(), MarketingPluginRequest.builder().customer(customer).levelMap(levelMap).build());
        }

        List<GoodsInfoVO> goodsInfoVOList = response.getGiftList();
        List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class);
        GoodsIntervalPriceByCustomerIdRequest request = new GoodsIntervalPriceByCustomerIdRequest();
        if(Objects.nonNull(customer)) {
            request.setCustomerId(customer.getCustomerId());
        }
        request.setGoodsInfoDTOList(goodsInfoDTOList);
        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceProvider.putByCustomerId(request).getContext();

        response.setGiftList(KsBeanUtil.copyListProperties(priceResponse.getGoodsInfoVOList(), GoodsInfoVO.class));
        response.setLevelList(levelList);
        return response;
    }

    /**
     * 根据活动获取赠品列表
     * @param marketingId
     * @param customer
     * @return
     */
    public MarketingFullGiftLevelResponse getGiftList(Long marketingId, CustomerVO customer,Boolean matchWareHouseFlag){
        MarketingFullGiftLevelResponse response = new MarketingFullGiftLevelResponse();
        List<MarketingFullGiftLevel> levelList = getLevelsByMarketingId(marketingId);
        if (CollectionUtils.isEmpty(levelList)) {
            return response;
        }
        List<String> outgitfstocklist = new ArrayList<>();

        levelList.forEach(v->{
            Long nmarketingId = v.getMarketingId();
            Long giftLevelId = v.getGiftLevelId();
            List<MarketingFullGiftDetail> collect = v.getFullGiftDetailList().stream().filter(q -> {
                String productId = q.getProductId();
                String key = nmarketingId.toString()+giftLevelId.toString()+productId;
                String o = redisService.getString(key);
                if (Objects.nonNull(o)){
                    Long num = Long.parseLong(o);
                    if (num.compareTo(0l)<=0){
                        outgitfstocklist.add(productId);
                    }
                }

                if (q.getTerminationFlag().equals(BoolFlag.YES)) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            v.setFullGiftDetailList(collect);
        });

        List<String> goodsInfoIds = levelList.stream()
                .map(MarketingFullGiftLevel::getFullGiftDetailList)
                .flatMap(Collection::stream)
                .filter(xyy->{
                    if (xyy.getTerminationFlag().equals(BoolFlag.YES)){
                        return false;
                    }
                    return true;
                })
                .map(MarketingFullGiftDetail::getProductId)
                .collect(Collectors.toList());
        //获取SKU信息
        ;
        response.setGiftList(
                goodsInfoQueryProvider.listViewByIds(
                        GoodsInfoViewByIdsRequest.builder().goodsInfoIds(goodsInfoIds).matchWareHouseFlag(matchWareHouseFlag)
                                .isHavSpecText(Constants.yes).build()
                ).getContext().getGoodsInfos()
        );
        //获取客户的等级
        if(Objects.nonNull(customer)) {
            List<Long> storeIds = response.getGiftList().stream()
                    .map(GoodsInfoVO::getStoreId)
                    .filter(Objects::nonNull)
                    .distinct().collect(Collectors.toList());
            Map<Long, CommonLevelVO> levelMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(storeIds)) {
                CustomerLevelMapByCustomerIdAndStoreIdsRequest customerLevelMapByCustomerIdAndStoreIdsRequest = new CustomerLevelMapByCustomerIdAndStoreIdsRequest();
                customerLevelMapByCustomerIdAndStoreIdsRequest.setCustomerId(customer.getCustomerId());
                customerLevelMapByCustomerIdAndStoreIdsRequest.setStoreIds(storeIds);
                BaseResponse<CustomerLevelMapGetResponse> customerLevelMapGetResponseBaseResponse =
                        customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds(customerLevelMapByCustomerIdAndStoreIdsRequest);
                Map<Long, CommonLevelVO> tmpLevelMap = customerLevelMapGetResponseBaseResponse.getContext()
                        .getCommonLevelVOMap();
                levelMap.putAll(tmpLevelMap);
            }
            //计算会员价
            customerLevelPlugin.goodsListFilter(response.getGiftList(), MarketingPluginRequest.builder().customer(customer).levelMap(levelMap).build());
        }

        List<GoodsInfoVO> goodsInfoVOList = response.getGiftList();
        List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class);
        GoodsIntervalPriceByCustomerIdRequest request = new GoodsIntervalPriceByCustomerIdRequest();
        if(Objects.nonNull(customer)) {
            request.setCustomerId(customer.getCustomerId());
        }
        request.setGoodsInfoDTOList(goodsInfoDTOList);
        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceProvider.putByCustomerId(request).getContext();

        response.setGiftList(KsBeanUtil.copyListProperties(priceResponse.getGoodsInfoVOList(), GoodsInfoVO.class));
        log.info("===============111111="+response.getGiftList());
        if (CollectionUtils.isNotEmpty(response.getGiftList())){
            //设置赠品状态
            response.setGiftList(response.getGiftList().stream().sorted(Comparator.comparing(GoodsInfoVO::getGoodsStatus).thenComparing(GoodsInfoVO::getMarketPrice,Comparator.reverseOrder())).collect(Collectors.toList()));
            response.getGiftList().forEach(v->{
                if (outgitfstocklist.contains(v.getGoodsInfoId())){
                    v.setGoodsStatus(GoodsStatus.OUT_GIFTS_STOCK);
                }
            });

        }

        response.setLevelList(levelList);



        return response;
    }

    /**
     * 根据活动和阶梯信息获取对应赠品列表
     * @param marketingId
     * @param giftLevelId
     * @return
     */
    public List<MarketingFullGiftDetail> getGiftList(Long marketingId,Long giftLevelId){
        return marketingFullGiftDetailRepository.findByMarketingIdAndGiftLevelId(marketingId,giftLevelId);
    }

}
