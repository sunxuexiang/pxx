package com.wanmi.sbc.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.constant.WareHouseErrorCode;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoVerifyRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateDeliveryAreaByStoreIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GrouponGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.distributionrecord.DistributionRecordQueryProvider;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityFreeDeliveryByIdRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingLevelGoodsListFilterRequest;
import com.wanmi.sbc.marketing.bean.enums.RecruitApplyType;
import com.wanmi.sbc.order.api.provider.groupon.GrouponProvider;
import com.wanmi.sbc.order.api.provider.trade.*;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPilePickTradeItemQueryProvider;
import com.wanmi.sbc.order.api.request.groupon.GrouponOrderValidRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCommitRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetGoodsRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.order.api.response.trade.TradeGetGoodsResponse;
import com.wanmi.sbc.order.bean.dto.StoreCommitInfoDTO;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.order.response.TradeConfirmResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderUtil {

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private TradeItemQueryProvider tradeItemQueryProvider;

    @Autowired
    private NewPilePickTradeItemQueryProvider newPilePickTradeItemQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Resource
    private GoodsIntervalPriceService goodsIntervalPriceService;

    @Resource
    private MarketingLevelPluginProvider marketingLevelPluginProvider;

    @Resource
    private DistributorGoodsInfoQueryProvider distributorGoodsInfoQueryProvider;

    @Resource
    private DistributionCacheService distributionCacheService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GrouponProvider grouponProvider;

    @Autowired
    private GrouponActivityQueryProvider grouponActivityQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private ParentCustomerRelaQueryProvider parentCustomerRelaQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    /**
     * 匹配分仓
     *
     * @param cityCode
     * @return
     */
    public WareHouseVO matchWareStore(Long cityCode) {
        //1. 从redis里获取主仓
        List<WareHouseVO> wareHouseMainList;
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES, WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if (StringUtils.isNotEmpty(wareHousesStr)) {
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr, WareHouseVO.class);
            if (CollectionUtils.isNotEmpty(wareHouseVOS)) {
                wareHouseMainList = wareHouseVOS.stream()
                        .filter(wareHouseVO -> PickUpFlag.NO.equals(wareHouseVO.getPickUpFlag())).collect(Collectors.toList());
            } else {
                wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                        .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
            }
        } else {
            wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
        }
        //设置selectedAreas
        wareHouseMainList.stream().forEach(w -> {
            String[] cityIds = w.getDestinationArea().split(",");
            Long[] cityIdList = (Long[]) ConvertUtils.convert(cityIds, Long.class);
            w.setSelectedAreas(Arrays.asList(cityIdList));
        });
        //2. 匹配分仓信息
        if (wareHouseMainList.stream().anyMatch(w -> w.getSelectedAreas().contains(cityCode))) {
            Optional<WareHouseVO> matchedWareHouse = wareHouseMainList.stream().filter(w -> w.getSelectedAreas()
                    .contains(cityCode)).findFirst();
            if (matchedWareHouse.isPresent()) {
                return matchedWareHouse.get();
            } else {
                throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NO_SERVICE, "您所在的区域没有可配的仓库，请重新修改收货地址");
            }
        } else {
            throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NO_SERVICE, "您所在的区域没有可配的仓库，请重新修改收货地址");
        }
    }



    /**
     * 区域限购验证
     * @param tradeItemGroups
     * @param deliveryAddress
     */
    public void checkGoodsForCommitOrder(List<TradeItemGroupVO> tradeItemGroups, CustomerDeliveryAddressResponse deliveryAddress) {
        List<TradeItemVO> tradeItems = new ArrayList<>();
        tradeItemGroups.forEach(t -> tradeItems.addAll(t.getTradeItems()));
        List<String> skuIds =
                tradeItems.stream().map(TradeItemVO::getSkuId).collect(Collectors.toList());
        GoodsInfoListByIdsRequest request = new GoodsInfoListByIdsRequest();
        request.setGoodsInfoIds(skuIds);
        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(request).getContext().getGoodsInfos();
        goodsInfos.forEach(goodsInfoVO -> {
            if (Objects.nonNull(deliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
                List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，在限购区域限购请重新选择商品购买！");
                }
            }
        });
    }

    public void validHomeDelivery(List<StoreCommitInfoDTO> storeCommitInfo, String addressId, Long wareId, String customerId) {

        for (StoreCommitInfoDTO inner : storeCommitInfo) {
            if (inner.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
                if (DefaultFlag.NO.equals(checkDeliveryArea(wareId, addressId,customerId))) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "超出配送范围");
                }
            }
        }

    }


    /**
     * 功能描述:
     * 检验是否符合免费店配条件
     *
     * @param wareId
     * @param customerDeleiverAddressId
     * @return: com.wanmi.sbc.common.enums.DefaultFlag
     */
    public DefaultFlag checkDeliveryArea(Long wareId, String customerDeleiverAddressId, String customerId) {
        log.info("********************88***********************"+wareId);
        log.info("********************88***********************"+customerDeleiverAddressId);
        log.info("********************88***********************"+customerId);
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(customerDeleiverAddressId).build()).getContext();
        if (Objects.isNull(response) || response.getDelFlag().equals(DeleteFlag.YES)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        if ((Objects.isNull(wareHouseVO) || (DeleteFlag.YES.equals(wareHouseVO.getDelFlag())))) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        //校验商品所在店铺配置免费配送范围
        List<FreightTemplateDeliveryAreaByStoreIdResponse> context = freightTemplateDeliveryAreaQueryProvider
                .query(FreightTemplateDeliveryAreaListRequest
                        .builder().storeId(wareHouseVO.getStoreId()).build()).getContext();
        log.info("运费模板接口返回结果"+context);
        FreightTemplateDeliveryAreaByStoreIdResponse freightTemplateDeliveryAreaByStoreIdResponse = new FreightTemplateDeliveryAreaByStoreIdResponse();
        if (CollectionUtils.isNotEmpty(context)){
            freightTemplateDeliveryAreaByStoreIdResponse= context.stream().filter(v -> {
                if (v.getAreaTenFreightTemplateDeliveryAreaVO().getWareId().equals(wareId)) {
                    return true;
                }
                return false;
            }).findAny().orElse(null);
        }
        log.info("运费模板接口返回结果"+freightTemplateDeliveryAreaByStoreIdResponse);
        if (Objects.isNull(freightTemplateDeliveryAreaByStoreIdResponse)){
            return DefaultFlag.NO;
        }
        //常规
        FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getFreightTemplateDeliveryAreaVO();
        List<TradeItemGroupVO> tradeItemGroupList = newPilePickTradeItemQueryProvider.itemListByCustomerId(TradeItemByCustomerIdRequest.builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        Long tradeItemsNum = 0L;
        if(CollectionUtils.isNotEmpty(tradeItemGroupList)){
            TradeItemGroupVO tradeItemGroupVO = tradeItemGroupList.stream().findFirst().get();
            //赠品数量统计
            List<List<TradeItemMarketingVO>> collect = tradeItemGroupList.stream().map(tg -> tg.getTradeMarketingList()).collect(Collectors.toList());
            List<TradeItemMarketingVO> tradeItemMarketingVOS = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(collect)){
                collect.forEach(m->{
                    if(CollectionUtils.isNotEmpty(m)){
                        tradeItemMarketingVOS.addAll(m);
                    }
                });
                log.info("tradeItemMarketingVOS=========== {}",tradeItemMarketingVOS.size());
//                if(CollectionUtils.isNotEmpty(tradeItemMarketingVOS)){
//                    TradeItemMarketingResponse context = tradeQueryProvider.listGiftsByTradeItemMarketing(TradeItemMarketingRequest.builder()
//                            .tradeMarketingVOList(KsBeanUtil.convertList(tradeItemMarketingVOS, TradeMarketingVO.class))
//                            .wareId(wareId)
//                            .wareCode(tradeItemGroupVO.getWareCode())
//                            .build()).getContext();
//                    if(CollectionUtils.isNotEmpty(context.getGifts())){
//                        tradeItemsNum += context.getGifts().stream().mapToLong(v->v.getNum()).sum();
//                    }
//                }
            }

            //购买商品数量统计
            List<List<TradeItemVO>> tradeItemVOS = tradeItemGroupList.stream().map(tg -> tg.getTradeItems()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(tradeItemVOS)){
                for (List<TradeItemVO> vos: tradeItemVOS) {
                    tradeItemsNum += vos.stream().mapToLong(v -> v.getNum()).sum();
                }
            }
        }
        log.info("tradeItemsNum=========== {}",tradeItemsNum);


        if (
            //常规
                (Objects.nonNull(freightTemplateDeliveryAreaVO) && Objects.nonNull(freightTemplateDeliveryAreaVO.getDestinationArea())
                        && (ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getProvinceId().toString())
                        || ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getCityId().toString())))
        ) {

            if(tradeItemsNum.longValue() < 5 && tradeItemsNum.longValue() > 0 ){
                log.info("DefaultFlag.NO=========== {}",tradeItemsNum);
                //小于5件看是否是同省配送，同省配送则不显示第三方物流
                //根据仓库ID获取省市区
                Long provinceId = wareHouseVO.getProvinceId();
                //获取收货地址的省份ID
                Long provinceIdAddress = response.getProvinceId();
                log.info("provinceIdAddress.provinceId=========== {},{}",provinceIdAddress,provinceId);
                if(provinceId.longValue() == provinceIdAddress.longValue()){
                    return DefaultFlag.YES;
                }
                return DefaultFlag.NO;
            }
            log.info("DefaultFlag.YES=========== {}",tradeItemsNum);
            return DefaultFlag.YES;
        }
        log.info("return DefaultFlag.NO=========== {}",tradeItemsNum);
        return DefaultFlag.NO;
    }

    /**
     * 指定区域销售单笔订单限购校验
     * @param tradeCommitRequest
     * @param tradeItemGroups
     */
    public void checkGoodsNumsForCommitOrder(TradeCommitRequest tradeCommitRequest, List<TradeItemGroupVO> tradeItemGroups) {
        List<TradeItemVO> tradeItems = new ArrayList<>();
        tradeItemGroups.forEach(t -> tradeItems.addAll(t.getTradeItems()));
        Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getNum,(a,b)->a));
        List<String> skuIds =
                tradeItems.stream().map(TradeItemVO::getSkuId).collect(Collectors.toList());
        GoodsInfoListByIdsRequest request = new GoodsInfoListByIdsRequest();
        request.setGoodsInfoIds(skuIds);
        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(request).getContext().getGoodsInfos();
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(tradeCommitRequest.getConsigneeId()).build()).getContext();
        goodsInfos.forEach(goodsInfoVO -> {
            if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && StringUtils.isNotBlank(goodsInfoVO.getSingleOrderAssignArea())){
                List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                log.info("==============>singleAreaList:{}",singleAreaList);
                if (singleAreaList.contains(response.getProvinceId()) || singleAreaList.contains(response.getCityId())){
                    if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))){
                        log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}",tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                        if(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()){
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，单笔订单超过限购数量："+goodsInfoVO.getSingleOrderPurchaseNum()+"件/箱，请修改此商品购买数量！");
                        }
                    }
                }
            }
        });
    }


    /**
     * 验证小店商品，开店礼包
     */
    public void validShopGoods(List<TradeItemGroupVO> tradeItemGroups, DistributeChannel channel) {

        DefaultFlag storeBagsFlag = tradeItemGroups.get(0).getStoreBagsFlag();
        if (DefaultFlag.NO.equals(storeBagsFlag)) {
            if (channel.getChannelType() == ChannelType.SHOP) {
                // 1.验证商品是否是小店商品
                List<String> skuIds = tradeItemGroups.stream().flatMap(i -> i.getTradeItems().stream())
                        .map(item -> item.getSkuId()).collect(Collectors.toList());
                DistributorGoodsInfoVerifyRequest verifyRequest = new DistributorGoodsInfoVerifyRequest();
                verifyRequest.setDistributorId(channel.getInviteeId());
                verifyRequest.setGoodsInfoIds(skuIds);
                List<String> invalidIds = distributorGoodsInfoQueryProvider
                        .verifyDistributorGoodsInfo(verifyRequest).getContext().getInvalidIds();
                if (CollectionUtils.isNotEmpty(invalidIds)) {
                    throw new SbcRuntimeException("K-080302");
                }

                // 2.验证商品对应商家的分销开关有没有关闭
                tradeItemGroups.stream().flatMap(i -> i.getTradeItems().stream().map(item -> {
                    item.setStoreId(i.getSupplier().getStoreId());
                    return item;
                })).forEach(item -> {
                    if (DefaultFlag.NO.equals(
                            distributionCacheService.queryStoreOpenFlag(String.valueOf(item.getStoreId())))) {
                        throw new SbcRuntimeException("K-080302");
                    }
                });
            }
        } else {
            // 开店礼包商品校验
            RecruitApplyType applyType = distributionCacheService.queryDistributionSetting().getApplyType();
            if (RecruitApplyType.REGISTER.equals(applyType)) {
                throw new SbcRuntimeException("K-080302");
            }
            TradeItemVO tradeItem = tradeItemGroups.get(0).getTradeItems().get(0);
            List<String> goodsInfoIds = distributionCacheService.queryStoreBags()
                    .stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            if (!goodsInfoIds.contains(tradeItem.getSkuId())) {
                throw new SbcRuntimeException("K-080302");
            }
        }
    }



    /**
     * 功能描述: 验证自提门店信息,如果存在塞入自提信息
     */
    public void validPickUpPoint(List<StoreCommitInfoDTO> storeCommitInfo, TradeCommitRequest tradeCommitRequest) {
        for (StoreCommitInfoDTO inner : storeCommitInfo) {
            if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                WareHouseVO wareHouseVO = wareHouseQueryProvider.getById(WareHouseByIdRequest.builder()
                        .wareId(inner.getWareId()).storeId(inner.getStoreId()).build()).getContext().getWareHouseVO();
                if (wareHouseVO.getPickUpFlag().toValue() == PickUpFlag.NO.toValue()) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "仓库不合法");
                }
                inner.setWareHouseVO(wareHouseVO);
                tradeCommitRequest.setWareId(wareHouseVO.getWareId());
            }
        }
    }

    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    public GoodsInfoResponse getDevanningGoodsResponseNew(List<Long> devanningIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        StopWatch stopWatch = new StopWatch("获取区间价会员价");
        if (CollectionUtils.isEmpty(devanningIds)) {
            return new GoodsInfoResponse();
        }
        stopWatch.start("获取商品详情");
        log.info("============devanningIds:{}, wareId:{}, wareHouseCode:{}, matchWareHouseFlag:{}", JSONObject.toJSONString(devanningIds), wareId, wareHouseCode, matchWareHouseFlag);

        TradeGetGoodsResponse response =
                tradeQueryProvider.getDevanningGoods(TradeGetGoodsRequest.builder()
                        .devanningIds(devanningIds)
                        .wareHouseCode(wareHouseCode)
                        .wareId(wareId)
                        .matchWareHouseFlag(matchWareHouseFlag)
                        .build()).getContext();
        stopWatch.stop();
        stopWatch.start("获取区间价");
        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
                (response.getGoodsInfos(), customer.getCustomerId());
        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
        stopWatch.stop();
        stopWatch.start("获取客户等级");
        //获取客户的等级
        if (StringUtils.isNotBlank(customer.getCustomerId())) {
            //计算会员价
            response.setGoodsInfos(
                    marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
                            .goodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class))
                            .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class)).build())
                            .getContext().getGoodsInfoVOList());
        }
        stopWatch.stop();
        log.info("获取区间价会员价======="+stopWatch.prettyPrint());
        return GoodsInfoResponse.builder().goodsInfos(response.getGoodsInfos())
                .goodses(response.getGoodses())
                .goodsIntervalPrices(response.getGoodsIntervalPrices())
                .build();
    }


    /**
     * 获取散批订单商品详情,包含区间价，会员级别价
     */
    public GoodsInfoResponse getBulkGoodsResponseNew(List<String> goodsinfoids, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isEmpty(goodsinfoids)) {
            return new GoodsInfoResponse();
        }

        log.info("============devanningIds:{}, wareId:{}, wareHouseCode:{}, matchWareHouseFlag:{}", JSONObject.toJSONString(goodsinfoids), wareId, wareHouseCode, matchWareHouseFlag);

        TradeGetGoodsResponse response =
                tradeQueryProvider.getBulkGoods(TradeGetGoodsRequest.builder()
                        .skuIds(goodsinfoids)
                        .wareHouseCode(wareHouseCode)
                        .wareId(wareId)
                        .matchWareHouseFlag(matchWareHouseFlag)
                        .build()).getContext();
        //计算区间价
//        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
//                (response.getGoodsInfos(), customer.getCustomerId());
//        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
//        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
        //获取客户的等级
        if (StringUtils.isNotBlank(customer.getCustomerId())) {
            //计算会员价
            response.setGoodsInfos(
                    marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
                                    .goodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class))
                                    .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class)).build())
                            .getContext().getGoodsInfoVOList());
        }
        return GoodsInfoResponse.builder().goodsInfos(response.getGoodsInfos())
                .goodses(response.getGoodses())
                .goodsIntervalPrices(response.getGoodsIntervalPrices())
                .build();
    }

    /**
     * 判断当前用户是否企业会员
     *
     * @return
     */
    public boolean isIepCustomer() {
        EnterpriseCheckState customerState = commonUtil.getCustomer().getEnterpriseStatusXyy();
        return !Objects.isNull(customerState)
                && customerState == EnterpriseCheckState.CHECKED;
    }

    /**
     * 校验拼团信息
     * @param response
     * @param tradeItemGroups
     * @param customerId
     */
    public void validGrouponOrder(TradeConfirmResponse response, List<TradeItemGroupVO> tradeItemGroups, String customerId) {
        TradeItemGroupVO tradeItemGroup = tradeItemGroups.get(NumberUtils.INTEGER_ZERO);
        TradeItemVO item = tradeItemGroup.getTradeItems().get(NumberUtils.INTEGER_ZERO);
        TradeConfirmItemVO confirmItem = response.getTradeConfirmItems().get(NumberUtils.INTEGER_ZERO);
        TradeItemVO resItem = confirmItem.getTradeItems().get(NumberUtils.INTEGER_ZERO);
        if (Objects.nonNull(tradeItemGroup.getGrouponForm())) {

            TradeGrouponCommitFormVO grouponForm = tradeItemGroup.getGrouponForm();

            if (!DistributionGoodsAudit.COMMON_GOODS.equals(item.getDistributionGoodsAudit())) {
                log.error("拼团单，不能下分销商品");
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }

            // 1.校验拼团商品
            GrouponGoodsInfoVO grouponGoodsInfo = grouponProvider.validGrouponOrderBeforeCommit(
                    GrouponOrderValidRequest.builder()
                            .buyCount(item.getNum().intValue()).customerId(customerId).goodsId(item.getSpuId())
                            .goodsInfoId(item.getSkuId())
                            .grouponNo(grouponForm.getGrouponNo())
                            .openGroupon(grouponForm.getOpenGroupon())
                            .build()).getContext().getGrouponGoodsInfo();

            if (Objects.isNull(grouponGoodsInfo)) {
                log.error("拼团单下的不是拼团商品");
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }

            // 2.设置拼团活动信息
            boolean freeDelivery = grouponActivityQueryProvider.getFreeDeliveryById(
                    new GrouponActivityFreeDeliveryByIdRequest(grouponGoodsInfo.getGrouponActivityId())).getContext().isFreeDelivery();
            response.setOpenGroupon(grouponForm.getOpenGroupon());
            response.setGrouponFreeDelivery(freeDelivery);

            // 3.设成拼团价
            BigDecimal grouponPrice = grouponGoodsInfo.getGrouponPrice();
            BigDecimal splitPrice = grouponPrice.multiply(new BigDecimal(item.getNum()));
            resItem.setSplitPrice(splitPrice);
            resItem.setPrice(grouponPrice);
            resItem.setLevelPrice(grouponPrice);
            confirmItem.getTradePrice().setGoodsPrice(splitPrice);
            confirmItem.getTradePrice().setTotalPrice(splitPrice);
        }
    }

    /**
     * 1.订单未完成 （订单已支付扒拉了巴拉  显示退货退款按钮-与后台开关设置无关）
     * 2.订单已完成，在截止时间内，且退货开关开启时，前台显示 申请入口（完成时记录订单可退申请的截止时间，如果完成时开关关闭 时间记录完成当时的时间）
     *
     * @param flag
     * @param days
     * @param tradeState
     * @param canReturnFlag
     * @return
     */
    public boolean isCanReturnTime(boolean flag, int days, TradeStateVO tradeState, boolean canReturnFlag) {
        if (canReturnFlag && tradeState.getFlowState() == FlowState.COMPLETED) {
            if (flag) {
                if (Objects.nonNull(tradeState.getFinalTime())) {
                    //是否可退根据订单完成时配置为准
                    flag = tradeState.getFinalTime().isAfter(LocalDateTime.now());
                } else if (Objects.nonNull(tradeState.getEndTime())) {
                    //容错-历史数据
                    //判断是否在可退时间范围内
                    LocalDateTime endTime = tradeState.getEndTime();
                    return endTime.plusDays(days).isAfter(LocalDateTime.now());
                }
            } else {
                return false;
            }
            return flag;
        }
        return canReturnFlag;
    }

    /**
     * 1.订单未完成 （订单已支付扒拉了巴拉  显示退货退款按钮-与后台开关设置无关）
     * 2.订单已完成，在截止时间内，且退货开关开启时，前台显示 申请入口（完成时记录订单可退申请的截止时间，如果完成时开关关闭 时间记录完成当时的时间）
     *
     * @param flag
     * @param days
     * @param tradeState
     * @param canReturnFlag
     * @return
     */
    public boolean isCanReturnTimeNewPile(boolean flag, int days, NewPileTradeStateVO tradeState, boolean canReturnFlag) {
        if (canReturnFlag && tradeState.getFlowState() == NewPileFlowState.COMPLETED) {
            if (flag) {
                if (Objects.nonNull(tradeState.getFinalTime())) {
                    //是否可退根据订单完成时配置为准
                    flag = tradeState.getFinalTime().isAfter(LocalDateTime.now());
                } else if (Objects.nonNull(tradeState.getEndTime())) {
                    //容错-历史数据
                    //判断是否在可退时间范围内
                    LocalDateTime endTime = tradeState.getEndTime();
                    return endTime.plusDays(days).isAfter(LocalDateTime.now());
                }
            } else {
                return false;
            }
            return flag;
        }
        return canReturnFlag;
    }

    public void checkUnauthorized(@PathVariable String tid, TradeVO detail) {
        List<String> customerId = new ArrayList<>(20);
        customerId.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerId.addAll(child);
        }
        if (!customerId.contains(detail.getBuyer().getId())) {
            throw new SbcRuntimeException("K-050100", new Object[]{tid});
        }
    }

    public void checkUnauthorizedNewPile(@PathVariable String tid, NewPileTradeVO detail) {
        List<String> customerId = new ArrayList<>(20);
        customerId.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerId.addAll(child);
        }
        if (!customerId.contains(detail.getBuyer().getId())) {
            throw new SbcRuntimeException("K-050100", new Object[]{tid});
        }
    }


    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    public GoodsInfoResponse getGoodsResponseNew(List<String> skuIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return new GoodsInfoResponse();
        }
        TradeGetGoodsResponse response =
                tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder()
                        .skuIds(skuIds)
                        .wareHouseCode(wareHouseCode)
                        .wareId(wareId)
                        .matchWareHouseFlag(matchWareHouseFlag)
                        .build()).getContext();

        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
                (response.getGoodsInfos(), customer.getCustomerId());
        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
        //获取客户的等级
        if (StringUtils.isNotBlank(customer.getCustomerId())) {
            //计算会员价
            response.setGoodsInfos(
                    marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
                            .goodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class))
                            .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class)).build())
                            .getContext().getGoodsInfoVOList());
        }
        return GoodsInfoResponse.builder().goodsInfos(response.getGoodsInfos())
                .goodses(response.getGoodses())
                .goodsIntervalPrices(response.getGoodsIntervalPrices())
                .build();
    }


    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    public GoodsInfoResponse getGoodsRetailResponseNew(List<String> skuIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return new GoodsInfoResponse();
        }
        TradeGetGoodsResponse response =
                tradeQueryProvider.getRetailGoods(TradeGetGoodsRequest.builder()
                        .skuIds(skuIds)
                        .wareHouseCode(wareHouseCode)
                        .wareId(wareId)
                        .matchWareHouseFlag(matchWareHouseFlag)
                        .build()).getContext();

        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
                (response.getGoodsInfos(), customer.getCustomerId());
        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
        //获取客户的等级
        if (StringUtils.isNotBlank(customer.getCustomerId())) {
            //计算会员价
            response.setGoodsInfos(
                    marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
                            .goodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class))
                            .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class)).build())
                            .getContext().getGoodsInfoVOList());
        }
        return GoodsInfoResponse.builder().goodsInfos(response.getGoodsInfos())
                .goodses(response.getGoodses())
                .goodsIntervalPrices(response.getGoodsIntervalPrices())
                .build();
    }
}
