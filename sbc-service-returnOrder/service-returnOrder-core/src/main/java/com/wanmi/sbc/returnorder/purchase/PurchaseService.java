package com.wanmi.sbc.returnorder.purchase;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoQueryByIdsRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyListRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoQueryByIdsResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsPriceAssistProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsInfoSpecDetailRelQueryProvider;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoVerifyRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingListByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingModifyRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingSyncRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsPriceSetBatchByIepRequest;
import com.wanmi.sbc.goods.api.request.spec.GoodsInfoSpecDetailRelByGoodsIdAndSkuIdRequest;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsPriceSetBatchByIepResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCacheProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullDiscountQueryProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullReductionQueryProvider;
import com.wanmi.sbc.marketing.api.provider.distribution.DistributionSettingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCacheListForGoodsGoodInfoListRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.distribution.DistributionStoreSettingGetByStoreIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingQueryByIdsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.*;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCacheListForGoodsDetailResponse;
import com.wanmi.sbc.marketing.api.response.distribution.DistributionStoreSettingGetByStoreIdResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.MarketingErrorCode;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.returnorder.api.request.purchase.*;
import com.wanmi.sbc.returnorder.api.response.purchase.*;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseGoodsInfoDTO;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseMergeDTO;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseQueryDTO;
import com.wanmi.sbc.returnorder.bean.dto.TradeItemDTO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseGoodsInfoCheckVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseGoodsViewVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseVO;
import com.wanmi.sbc.returnorder.constant.AbstractOrderConstant;
import com.wanmi.sbc.returnorder.customer.service.CustomerCommonService;
import com.wanmi.sbc.returnorder.follow.model.root.GoodsCustomerFollow;
import com.wanmi.sbc.returnorder.follow.repository.GoodsCustomerFollowRepository;
import com.wanmi.sbc.returnorder.follow.request.GoodsCustomerFollowQueryRequest;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseService;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseAction;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRepository;
import com.wanmi.sbc.returnorder.purchase.model.ProcurementConfig;
import com.wanmi.sbc.returnorder.purchase.repository.ProcurementConfigRepository;
import com.wanmi.sbc.returnorder.purchase.request.PurchaseRequest;
import com.wanmi.sbc.returnorder.redis.RedisCache;
import com.wanmi.sbc.returnorder.redis.RedisKeyConstants;
import com.wanmi.sbc.returnorder.redisservice.WareHouseRedisService;
import com.wanmi.sbc.returnorder.shopcart.*;
import com.wanmi.sbc.returnorder.shopcart.ChainHandle.StockAndPureChainNode;
import com.wanmi.sbc.returnorder.shopcart.cache.BulkShopCartCacheSupport;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * 采购单服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Transactional(readOnly = true)
@Log4j2
public class PurchaseService {

    @Autowired
    private GoodsCustomerFollowRepository goodsCustomerFollowRepository;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsInfoSpecDetailRelQueryProvider goodsInfoSpecDetailRelQueryProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private MarketingPluginQueryProvider marketingPluginQueryProvider;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private MarketingLevelPluginProvider marketingLevelPluginProvider;

    @Autowired
    private GoodsMarketingProvider goodsMarketingProvider;

    @Autowired
    private GoodsMarketingQueryProvider goodsMarketingQueryProvider;

    @Autowired
    private CouponCacheProvider couponCacheProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private ShopCartService shopCartService;

    @Autowired
    private BulkShopCartService bulkShopCartService;

    @Autowired
    private ShopCartNewPileTradeService shopCartNewPileTradeService;

    @Resource
    private DistributorGoodsInfoQueryProvider distributorGoodsInfoQueryProvider;

    @Autowired
    private GoodsPriceAssistProvider goodsPriceAssistProvider;

    @Autowired
    private DistributionSettingQueryProvider distributionSettingQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private WareHouseRedisService wareHouseRedisService;

    @Autowired
    private MarketingFullReductionQueryProvider marketingFullReductionQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private MarketingFullDiscountQueryProvider marketingFullDiscountQueryProvider;

    @Autowired
    private ProcurementConfigRepository procurementConfigRepository;

    @Autowired
    private PilePurchaseActionRepository pilePurchaseActionRepository;

    @Autowired
    private PilePurchaseService pilePurchaseService;

    @Autowired
    private ShopCartRepository shopCartRepository;

    @Autowired
    private BulkShopCartRepository bulkShopCartRepository;

    @Autowired
    private ShopCartNewPileTradeRepository shopCartNewPileTradeRepository;

    @Autowired
    private RedisCache redisCache;

    /**
     * 通过 service-Order-app >> resource >> spring-plugin.xml 文件注入
     */
    @Resource(name = "stockAndPureChainNodeList")
    private List<StockAndPureChainNode> checkList;

    @Resource(name = "tunhuoAndPureChainNodeList")
    private List<StockAndPureChainNode> tunhuocheckList;

    @Resource(name = "stockAndAreaNodeList")
    private List<StockAndPureChainNode> stockAndAreaNodeList;


    @Resource(name = "tihuoStockChainNodeList")
    private List<StockAndPureChainNode> tihuostockList;

    @Resource(name = "bulkGoodsInfoStockAndAreaNodeList")
    private List<StockAndPureChainNode> bulkGoodsInfoStockAndAreaNodeList;


    /**
     * 新增采购单
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void save(PurchaseRequest request) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase save start");

        try {
            GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                    .goodsInfoId(request.getGoodsInfoId())
                    .wareId(request.getWareId())
                    .build()).getContext();

            sb.append(",goodsInfoQueryProvider.getById end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            if (request.getVerifyStock() && goodsInfo.getStock().compareTo(BigDecimal.valueOf(request.getGoodsNum())) < 0 ) {
                throw new SbcRuntimeException("K-030302", new Object[]{goodsInfo.getStock()});
            }
            if (goodsInfo == null) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
            }
            //saas鉴权
            if (request.getSaasStatus() != null
                    && request.getSaasStatus()
                    && !request.getStoreId().equals(goodsInfo.getStoreId())) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            Purchase purchase = purchaseRepository.findOne(request.getWhereCriteria()).orElse(null);

            sb.append(",purchaseRepository.findOne end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            if (Objects.nonNull(purchase)) {
                request.setGoodsNum(request.getGoodsNum() + purchase.getGoodsNum());
            }
            Integer countNum = countGoods(request.getCustomerId(), request.getInviteeId());

            sb.append(",countGoods end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            addPurchase(goodsInfo, countNum, request);

            sb.append(",addPurchase end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            //生成或修改mongodb中GoodsMarketing客户选择的商品营销信息关联记录
            if(request.getCustomerId()!=null){
                CustomerVO customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request.getCustomerId())).getContext();

                sb.append(",customerQueryProvider.getCustomerById end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                PurchaseGetGoodsMarketingResponse goodsMarketing = this.getGoodsMarketing(Arrays.asList(goodsInfo), customer, request.getWareId());

                sb.append(",getGoodsMarketing end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                this.mergeGoodsMarketings(goodsMarketing.getMap(), request.getCustomerId());

                sb.append(",mergeGoodsMarketings end time=");
                sb.append(System.currentTimeMillis()-sTm);

            }
        }finally {
            log.info(sb.toString());
        }
    }

    /**
     * 批量加入采购单
     *
     * @param request
     */
    @Transactional
    public void batchSave(PurchaseRequest request) {
        //List<GoodsInfo> goodsInfoList = goodsInfoRepository.findAll(GoodsInfoQueryRequest.builder().delFlag(0).
        //      goodsInfoIds(request.getGoodsInfos().stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors
        //      .toList())).build().getWhereCriteria());
        List<GoodsInfoVO> goodsInfoList = goodsInfoQueryProvider.listByCondition(
                GoodsInfoListByConditionRequest.builder().delFlag(Constants.no)
                        .goodsInfoIds(request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList()))
                        .build()
        ).getContext().getGoodsInfos();

        //获取囤货数量
//        Map<String,Long> goodsNumsMap = pilePurchaseService.getPileGoodsNumsBuySkuIds(goodsInfoList.stream()
//                .map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()));

//        List<GoodsCustomerFollow> followList =
//                goodsCustomerFollowRepository.findAll(GoodsCustomerFollowQueryRequest.builder()
//                        .customerId(request.getCustomerId())
//                        .goodsInfoIds(request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList()))
//                        .build().getWhereCriteria());
        List<Purchase> purchaseList = purchaseRepository.findAll(PurchaseRequest.builder()
                .customerId(request.getCustomerId()).inviteeId(request.getInviteeId())
                .goodsInfoIds(request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList())).build().getWhereCriteria());

        GoodsWareStockListResponse context = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoIds(GoodsWareStockByGoodsForIdsRequest
                .builder()
                .goodsForIdList(request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList()))
                .wareId(null)
                .build()).getContext();

        Integer countNum = countGoods(request.getCustomerId(), request.getInviteeId());
        goodsInfoList.forEach(goods -> {

            if(Objects.nonNull(context) && CollectionUtils.isNotEmpty(context.getGoodsWareStockVOList())){
                GoodsWareStockVO goodsWareStockVO = context.getGoodsWareStockVOList().stream().filter(gw -> gw.getGoodsInfoId().equals(goods.getGoodsInfoId())).findFirst().orElse(null);
                if(Objects.nonNull(goodsWareStockVO) && goodsWareStockVO.getStock().compareTo(BigDecimal.ZERO) > 0){
                    //填充库存
                    goods.setStock(goodsWareStockVO.getStock());
                    //获取囤货数量
                    /*Long pileGoodsNum = goodsNumsMap.getOrDefault(goods.getGoodsInfoId(),null);
                    //设置库存为 库存+虚拟库存
                    if (Objects.nonNull(goods.getVirtualStock()) && goods.getVirtualStock() > 0){
                        goods.setStock(goods.getStock()+goods.getVirtualStock());
                    }
                    //设置库存为 库存-囤货数量
                    if (Objects.nonNull(pileGoodsNum) && pileGoodsNum > 0){
                        goods.setStock(goods.getStock()-pileGoodsNum);
                    }*/
                }
            }

            Purchase purchases = purchaseList.stream().map(purchase -> {
                if (goods.getGoodsInfoId().equals(purchase.getGoodsInfoId())) {
                    return purchase;
                }
                return null;
            }).filter(Objects::nonNull).findFirst().orElse(null);
            request.getGoodsInfos().forEach(info -> {
                if (info.getBuyCount() <= 0) {
                    throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
                }
                if (goods.getGoodsInfoId().equals(info.getGoodsInfoId())) {
                    request.setGoodsNum(info.getBuyCount());
                }
            });
            if (purchases != null) {
                //商品详情页加入购物车不需要校验购物车中已选商品数量
//                if (goods.getStock() < (request.getGoodsNum() + purchases.getGoodsNum())) {
//                    throw new SbcRuntimeException("K-030302", new Object[]{goods.getStock()});
//                }
                updatePurchase(purchases, request, goods);
            } else {
                List<GoodsInfoDTO> goodsInfos =
                        request.getGoodsInfos().stream().filter(info -> info.getGoodsInfoId().equals(goods.getGoodsInfoId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(goodsInfos)) {
                    request.setCreateTime(goodsInfos.get(0).getCreateTime());
                }
                addPurchase(goods, countNum, request);
            }
        });

        //生成或修改mongodb中GoodsMarketing客户选择的商品营销信息关联记录
        if(request.getCustomerId()!=null){
            CustomerVO customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request.getCustomerId())).getContext();
            PurchaseGetGoodsMarketingResponse goodsMarketing = this.getGoodsMarketing(goodsInfoList, customer, request.getWareId());
            this.mergeGoodsMarketings(goodsMarketing.getMap(), request.getCustomerId());
        }

    }

    /**
     * 生成或修改mongodb中GoodsMarketing客户变动的商品营销信息关联记录
     * @param
     * @param customerId
     */
    private void mergeGoodsMarketings(Map<String, List<MarketingViewVO>> goodsMarketingMap, String customerId) {
        GoodsMarketingSyncRequest goodsMarketingSyncRequest = new GoodsMarketingSyncRequest();
        goodsMarketingSyncRequest.setCustomerId(customerId);
        Map<String, List<Long>> marketingIdsMap = new HashMap<>();
        if (!goodsMarketingMap.isEmpty()) {
            goodsMarketingMap.forEach((key, value) -> {
                List<Long> marketingIds = value.stream()
                        .map(MarketingViewVO::getMarketingId)
                        .collect(Collectors.toList());
                marketingIdsMap.put(key, marketingIds);
            });
        }
        goodsMarketingSyncRequest.setMarketingIdsMap(marketingIdsMap);
        goodsMarketingProvider.mergeGoodsMarketings(goodsMarketingSyncRequest);
    }

    /**
     * 合并登录前后采购单
     *
     * @param request
     */
    @Transactional
    public void mergePurchase(PurchaseMergeRequest request) {
        CustomerDTO customer = request.getCustomer();
        //获取商品列表
        List<GoodsInfoVO> goodsInfoList = goodsInfoQueryProvider.listByCondition(
                GoodsInfoListByConditionRequest.builder().delFlag(Constants.no)
                        .goodsInfoIds(request.getPurchaseMergeDTOList().stream().map(PurchaseMergeDTO::getGoodsInfoId).collect(Collectors.toList()))
                        .build()
        ).getContext().getGoodsInfos();
        //根据用户获取采购单列表
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setCustomerId(customer.getCustomerId());
        purchaseRequest.setInviteeId(request.getInviteeId());
        purchaseRequest.setSortColumn("createTime");
        purchaseRequest.setSortType("desc");
        List<Purchase> purchaseList;
        Sort sort = purchaseRequest.getSort();
        if (Objects.nonNull(sort)) {
            purchaseList = purchaseRepository.findAll(purchaseRequest.getWhereCriteria(), sort);
        } else {
            purchaseList = purchaseRepository.findAll(purchaseRequest.getWhereCriteria());
        }
        //待插入采购单商品集合
        List<GoodsInfoDTO> goodsInfos = new ArrayList<>();
        goodsInfoList.forEach(goods -> {
            // 存在重复商品 替换采购单购买数量
            Optional<Purchase> purchaseOptional =
                    purchaseList.stream().filter(p -> StringUtils.equals(goods.getGoodsInfoId(), p.getGoodsInfoId())).findFirst();
            if (purchaseOptional.isPresent()) {
                Purchase purchase = purchaseOptional.get();
                Long goodsNum =
                        request.getPurchaseMergeDTOList().stream().filter(p -> StringUtils.equals(p.getGoodsInfoId(),
                                purchase.getGoodsInfoId()))
                                .findFirst().get().getGoodsNum();
                this.updatePurchase(purchase, PurchaseRequest.builder().goodsNum(goodsNum).isCover(true).build(), goods);
//                this.save(PurchaseRequest.builder().customerId(customer.getCustomerId()).goodsInfoId(purchase
//                .getGoodsInfoId())
//                        .goodsNum(goodsNum).isCover(true).build());
            } else {
                //不重复，加入集合待插入采购单
                GoodsInfoDTO goodsInfo = new GoodsInfoDTO();
                BeanUtils.copyProperties(goods, goodsInfo);
                goodsInfos.add(goodsInfo);
            }
        });
        if (goodsInfos.size() > 0) {
            if (goodsInfos.size() == Constants.PURCHASE_MAX_SIZE && purchaseList.size() > 0) {
                purchaseRepository.deleteByGoodsInfoids(purchaseList.stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()), customer.getCustomerId(), request.getInviteeId());
            } else if ((goodsInfos.size() + purchaseList.size()) > Constants.PURCHASE_MAX_SIZE) {
                int num = (goodsInfos.size() + purchaseList.size()) - Constants.PURCHASE_MAX_SIZE;
                if (num >= purchaseList.size() && purchaseList.size() > 0) {
                    purchaseRepository.deleteByGoodsInfoids(purchaseList.stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()), customer.getCustomerId(), request.getInviteeId());
                } else {
                    purchaseRepository.deleteByGoodsInfoids(purchaseList.subList(purchaseList.size() - num,
                            purchaseList.size()).stream()
                                    .map(Purchase::getGoodsInfoId).collect(Collectors.toList()),
                            customer.getCustomerId(),
                            request.getInviteeId());
                }
            }
            LocalDateTime dateTime = LocalDateTime.now();
            request.getPurchaseMergeDTOList().forEach(info -> {
                goodsInfos.stream().filter(obj -> obj.getGoodsInfoId().equals(info.getGoodsInfoId())).findFirst().ifPresent(goodsInfo -> {
                    goodsInfo.setCreateTime(dateTime.minusSeconds(request.getPurchaseMergeDTOList().indexOf(info)));
                    goodsInfo.setBuyCount(info.getGoodsNum());
                });
            });
            this.batchSave(PurchaseRequest.builder().inviteeId(request.getInviteeId()).customerId(customer.getCustomerId()).goodsInfos(goodsInfos).build());
        }
    }

    /**
     * 加入采购单
     *
     * @param goodsInfo
     * @param countNum
     * @param request
     */
    private void addPurchase(GoodsInfoVO goodsInfo, Integer countNum, PurchaseRequest request) {
        request.setGoodsInfoId(goodsInfo.getGoodsInfoId());
        request.setGoodsInfoIds(null);
        Purchase purchase = purchaseRepository.findOne(request.getWhereCriteria()).orElse(null);
        if (Objects.nonNull(purchase)) {
            purchase.setGoodsNum(request.getGoodsNum());
            purchaseRepository.save(purchase);
            return;
        }
        if (countNum >= Constants.PURCHASE_MAX_SIZE) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_050121, new Object[]{Constants.PURCHASE_MAX_SIZE});
        }
        purchase = new Purchase();
        purchase.setCompanyInfoId(goodsInfo.getCompanyInfoId());
        purchase.setGoodsInfoId(goodsInfo.getGoodsInfoId());
        purchase.setInviteeId(request.getInviteeId());
        purchase.setCustomerId(request.getCustomerId());
        purchase.setGoodsId(goodsInfo.getGoodsId());
        purchase.setGoodsNum(request.getGoodsNum());
        purchase.setCreateTime(Objects.nonNull(request.getCreateTime()) ? request.getCreateTime() :
                LocalDateTime.now());
        purchase.setIsCheck(DefaultFlag.YES);

        if (Objects.isNull(goodsInfo.getMarketingId()) &&
                goodsInfo.getStock().compareTo(BigDecimal.valueOf(request.getGoodsNum())) < 0){
            BigDecimal bigDecimal = goodsInfo.getStock().setScale(0, BigDecimal.ROUND_DOWN);
            purchase.setGoodsNum(bigDecimal.longValue());
        }

        if(Objects.nonNull(goodsInfo.getPurchaseNum()) && goodsInfo.getPurchaseNum() != -1){
            if(request.getGoodsNum() >= goodsInfo.getPurchaseNum()){
                purchase.setGoodsNum(goodsInfo.getPurchaseNum());
            }
        }

        if(purchase.getGoodsNum() < NumberUtils.INTEGER_ONE){
            purchase.setGoodsNum(NumberUtils.LONG_ONE);
        }

        purchaseRepository.save(purchase);
    }

    /**
     * 修改采购单
     *
     * @param purchase
     * @param request
     */
    private void updatePurchase(Purchase purchase, PurchaseRequest request, GoodsInfoVO goodsInfoVO) {
        if (request.getIsCover()) {
            purchase.setGoodsNum(request.getGoodsNum());
        } else {
            purchase.setGoodsNum(purchase.getGoodsNum() + request.getGoodsNum());
        }

        if(purchase.getGoodsNum() < NumberUtils.INTEGER_ONE){
            purchase.setGoodsNum(NumberUtils.LONG_ONE);
        }

        purchaseRepository.save(purchase);
    }

    /**
     * 未登录时,根据前端缓存信息查询迷你购物车信息
     *
     * @param frontReq 前端缓存的采购单信息
     * @return
     */
    public MiniPurchaseResponse miniListFront(PurchaseFrontMiniRequest frontReq) {
        MiniPurchaseResponse miniPurchaseResponse = new MiniPurchaseResponse();
        miniPurchaseResponse.setGoodsList(Collections.EMPTY_LIST);
        miniPurchaseResponse.setGoodsIntervalPrices(Collections.EMPTY_LIST);
        miniPurchaseResponse.setPurchaseCount(0);
        miniPurchaseResponse.setNum(0L);
        if (CollectionUtils.isEmpty(frontReq.getGoodsInfoDTOList())) {
            return miniPurchaseResponse;
        }
        GoodsInfoRequest goodsInfoRequest = new GoodsInfoRequest();
        List<String> skuIdList = frontReq.getGoodsInfoDTOList().stream()
                .map(PurchaseGoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList());
        goodsInfoRequest.setGoodsInfoIds(skuIdList);
        // 需要显示规格值
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        GoodsInfoViewByIdsResponse response = getPurchaseGoodsResponse(goodsInfoRequest);
        if (response == null) {
            return miniPurchaseResponse;
        }

        List<GoodsInfoVO> goodsInfoList = response.getGoodsInfos();
        Map<String, GoodsVO> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
        goodsInfoList.forEach(goodsInfoVO -> goodsInfoVO.setGoodsSubtitle(goodsVOMap.get(goodsInfoVO.getGoodsId()).getGoodsSubtitle()));
        // 按照前端传入的采购单顺序进行排序
        goodsInfoList.sort(Comparator.comparingInt((goods) -> skuIdList.indexOf(goods.getGoodsInfoId())));

        goodsInfoList.forEach(goodsInfo -> {
            // 填充前端传入的商品购买数量
            goodsInfo.setBuyCount(0L);
            List<PurchaseGoodsInfoDTO> dtoList = frontReq.getGoodsInfoDTOList().stream().filter(sku -> goodsInfo
                    .getGoodsInfoId().equals(sku.getGoodsInfoId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dtoList)) {
                goodsInfo.setBuyCount(dtoList.get(0).getGoodsNum());
            }
        });
        // 计算区间价
//        response.setGoodsIntervalPrices(goodsIntervalPriceService.putIntervalPrice(response.getGoodsInfos(), null));
        GoodsIntervalPriceByCustomerIdRequest priceRequest = new GoodsIntervalPriceByCustomerIdRequest();
        priceRequest.setGoodsInfoDTOList(KsBeanUtil.copyListProperties(response.getGoodsInfos(), GoodsInfoDTO.class));
        GoodsIntervalPriceByCustomerIdResponse priceResponse =
                goodsIntervalPriceProvider.putByCustomerId(priceRequest).getContext();
        miniPurchaseResponse.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
//        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        // 计算营销价格
        MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
        filterRequest.setGoodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class));
        response.setGoodsInfos(marketingPluginProvider.goodsListFilter(filterRequest).getContext()
                .getGoodsInfoVOList());
        List<PurchaseGoodsReponse> purchaseGoodsReponseList = goodsInfoList.stream().map(info -> {
            PurchaseGoodsReponse purchaseGoodsReponse = new PurchaseGoodsReponse();
            BeanUtils.copyProperties(info, purchaseGoodsReponse);
            purchaseGoodsReponse.setGoodsName(response.getGoodses().stream().filter(goods -> info.getGoodsId().equals(goods.getGoodsId())).findFirst().get().getGoodsName());
            return purchaseGoodsReponse;
        }).collect(Collectors.toList());
        miniPurchaseResponse.setGoodsList(purchaseGoodsReponseList);

        return miniPurchaseResponse;
    }

    /**
     * 查询迷你采购单
     *
     * @param request
     * @return
     */
    public MiniPurchaseResponse miniList(PurchaseRequest request, CustomerDTO customer) {
        boolean vipPriceFlag = false;
        if(Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
            vipPriceFlag = true;
        }
        //按创建时间倒序
        request.putSort("createTime", SortType.DESC.toValue());
        request.setPageSize(5);
        Page<Purchase> purchases = purchaseRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
        MiniPurchaseResponse miniPurchaseResponse = new MiniPurchaseResponse();
        miniPurchaseResponse.setGoodsList(Collections.EMPTY_LIST);
        miniPurchaseResponse.setGoodsIntervalPrices(Collections.EMPTY_LIST);
        miniPurchaseResponse.setPurchaseCount(0);
        miniPurchaseResponse.setNum(0L);
        if (purchases.getContent() == null || purchases.getContent().size() == 0) {
            return miniPurchaseResponse;
        }
        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
        goodsInfoRequest.setGoodsInfoIds(purchases.getContent().stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()));
        //需要显示规格值
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
        Map<String, GoodsVO> goodsVOMap = idsResponse.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));

        //计算区间价
        GoodsIntervalPriceByCustomerIdRequest priceRequest = new GoodsIntervalPriceByCustomerIdRequest();
        priceRequest.setGoodsInfoDTOList(KsBeanUtil.convert(idsResponse.getGoodsInfos(), GoodsInfoDTO.class));
        priceRequest.setCustomerId(customer.getCustomerId());
        GoodsIntervalPriceByCustomerIdResponse priceResponse =
                goodsIntervalPriceProvider.putByCustomerId(priceRequest).getContext();
        miniPurchaseResponse.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        idsResponse.setGoodsInfos(priceResponse.getGoodsInfoVOList());

        //计算营销价格,首先排除pc过滤时分销商品会将企业标识重置问题
        idsResponse.getGoodsInfos().forEach(goodsInfoVO -> {

            Boolean pcAndNoOpenAndNoStoreOpenFlag = Boolean.FALSE;
            if (Objects.nonNull(goodsInfoVO.getStoreId())) {
                DistributionStoreSettingGetByStoreIdResponse setting = distributionSettingQueryProvider.getStoreSettingByStoreId(
                        new DistributionStoreSettingGetByStoreIdRequest(String.valueOf(goodsInfoVO.getStoreId()))).getContext();
                if (Objects.isNull(setting) || DefaultFlag.NO.equals(setting.getOpenFlag())) {
                    pcAndNoOpenAndNoStoreOpenFlag = Boolean.TRUE;
                }
            }

            if (Objects.nonNull(request.getPcAndNoOpenFlag()) || Boolean.TRUE.equals(request.getPcAndNoOpenFlag()) || Boolean.TRUE.equals(pcAndNoOpenAndNoStoreOpenFlag)) {
                goodsInfoVO.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
            }
        });

        MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
        filterRequest.setGoodsInfos(KsBeanUtil.convert(idsResponse.getGoodsInfos(), GoodsInfoDTO.class));
        filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
        idsResponse.setGoodsInfos(marketingPluginProvider.goodsListFilter(filterRequest).getContext()
                .getGoodsInfoVOList());

        //企业购分支，设置企业会员价
        GoodsPriceSetBatchByIepRequest iepRequest = GoodsPriceSetBatchByIepRequest.builder()
                .customer(KsBeanUtil.convert(customer, CustomerVO.class))
                .goodsInfos(idsResponse.getGoodsInfos())
                .goodsIntervalPrices(miniPurchaseResponse.getGoodsIntervalPrices())
                .filteredGoodsInfoIds(idsResponse.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()))
                .build();

        GoodsPriceSetBatchByIepResponse iepResponse = goodsPriceAssistProvider.goodsPriceSetBatchByIep(iepRequest).getContext();
        idsResponse.setGoodsInfos(iepResponse.getGoodsInfos());
        miniPurchaseResponse.setGoodsIntervalPrices(iepResponse.getGoodsIntervalPrices());

        //填充SKU的购买数
        this.fillBuyCount(idsResponse.getGoodsInfos(), purchases.getContent());
        List<PurchaseGoodsReponse> purchaseGoodsReponseList = new ArrayList<>();
        purchases.forEach(purchase -> {
            idsResponse.getGoodsInfos().forEach(info -> {
                if (purchase.getGoodsInfoId().equals(info.getGoodsInfoId())) {
                    PurchaseGoodsReponse purchaseGoodsReponse = new PurchaseGoodsReponse();
                    BeanUtils.copyProperties(info, purchaseGoodsReponse);
                    purchaseGoodsReponse.setGoodsName(idsResponse.getGoodses().stream().filter(goods -> info.getGoodsId().equals(goods.getGoodsId())).findFirst().get().getGoodsName());
                    // 副标题
                    purchaseGoodsReponse.setGoodsSubtitle(goodsVOMap.get(info.getGoodsId()).getGoodsSubtitle());
                    purchaseGoodsReponseList.add(purchaseGoodsReponse);
                }
            });
        });
        if (vipPriceFlag){
            for (PurchaseGoodsReponse inner: purchaseGoodsReponseList){
                if(Objects.nonNull(inner.getVipPrice()) && inner.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                    inner.setSalePrice(inner.getVipPrice());
                }
            }
        }

        miniPurchaseResponse.setGoodsList(purchaseGoodsReponseList);
        if (request.getSaasStatus() != null
                && request.getSaasStatus()) {
            miniPurchaseResponse.setPurchaseCount(purchaseRepository.countByCustomerIdAndInviteeIdAndCompanyInfoId(customer.getCustomerId(), request.getInviteeId(), request.getCompanyInfoId()));
            miniPurchaseResponse.setNum(purchaseRepository.queryGoodsNumByCompanyInfoId(customer.getCustomerId(), request.getInviteeId(), request.getCompanyInfoId()));
        } else {
            miniPurchaseResponse.setPurchaseCount(countGoods(customer.getCustomerId(), request.getInviteeId()));
            miniPurchaseResponse.setNum(purchaseRepository.queryGoodsNum(customer.getCustomerId(), request.getInviteeId()));
        }
        return miniPurchaseResponse;
    }

    /**
     * 采购单按spu,按store组装 公共方法
     *
     * @return
     * @throws SbcRuntimeException
     */
    private PurchaseResponse listBase(List<GoodsInfoVO> goodsInfoList, List<GoodsVO> goodsList) throws SbcRuntimeException {
        //建立商户->SPU的扁平化结构  companyInfoQueryProvider
        List<Long> companyInfoIds =
                goodsInfoList.stream().map(GoodsInfoVO::getCompanyInfoId).collect(Collectors.toList());
        CompanyInfoQueryByIdsRequest request = new CompanyInfoQueryByIdsRequest();
        request.setCompanyInfoIds(companyInfoIds);
        request.setDeleteFlag(DeleteFlag.NO);
        CompanyInfoQueryByIdsResponse response = companyInfoQueryProvider.queryByCompanyInfoIds(request).getContext();
        List<CompanyInfoVO> companyInfoList = response.getCompanyInfoList().stream().map(companyInfo -> {
            companyInfo.setGoodsIds(goodsList.stream().filter(goodsinfo -> goodsinfo.getCompanyInfoId().longValue() == companyInfo.getCompanyInfoId().longValue())
                    .map(GoodsVO::getGoodsId).collect(Collectors.toList()));
            return companyInfo;
        }).collect(Collectors.toList());
//        List<CompanyInfo> companyInfoList = companyInfoRepository.queryByCompanyinfoIds(companyInfoIds, DeleteFlag
//        .NO).stream().map(companyInfo -> {
//            companyInfo.setGoodsIds(goodsList.stream().filter(goodsinfo -> goodsinfo.getCompanyInfoId().longValue()
//            == companyInfo.getCompanyInfoId().longValue())
//                    .map(GoodsDTO::getGoodsId).collect(Collectors.toList()));
//            return companyInfo;
//        }).collect(Collectors.toList());

        //店铺列表
        List<StoreVO> storeList = companyInfoList.stream().map(companyInfo -> {
            if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
                StoreVO store = companyInfo.getStoreVOList().get(0);
                store.setGoodsIds(goodsList.stream()
                        .filter(goodsinfo -> goodsinfo.getCompanyInfoId().longValue() == companyInfo.getCompanyInfoId().longValue())
                        .map(GoodsVO::getGoodsId).collect(Collectors.toList()));
                return store;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<Long> storeIds = goodsList.stream().map(GoodsVO::getStoreId).distinct().collect(Collectors.toList());
        storeList.sort(Comparator.comparingInt((store) -> storeIds.indexOf(store.getStoreId())));
        return PurchaseResponse.builder()
                .goodses(goodsList)
                .goodsInfos(goodsInfoList)
                .companyInfos(companyInfoList)
                .stores(storeList)
                .build();
    }

    /**
     * 此方法仅用于采购单商品查询
     * 若采购单中商品都不存在,则返回空的Response,而不是将异常抛给前端
     */
    private GoodsInfoViewByIdsResponse getPurchaseGoodsResponse(GoodsInfoRequest goodsInfoRequest) throws SbcRuntimeException {
        GoodsInfoViewByIdsRequest request = new GoodsInfoViewByIdsRequest();
        request.setGoodsInfoIds(goodsInfoRequest.getGoodsInfoIds());
        request.setIsHavSpecText(goodsInfoRequest.getIsHavSpecText());
        BaseResponse<GoodsInfoViewByIdsResponse> response = goodsInfoQueryProvider.listViewByIds(request);
        if (!CommonErrorCode.SUCCESSFUL.equals(response.getCode())) {
            if ("K-030001".equals(response.getCode())) {
                return null;
            } else {
                throw new SbcRuntimeException(response.getCode(), response.getMessage());
            }
        }
        return response.getContext();
    }

    /**
     * 未登陆时,根据前端传入的采购单信息,查询组装必要信息
     *
     * @param request 前端传入的采购单信息(skuList)
     * @return 采购单数据
     * @throws SbcRuntimeException
     * @author bail
     */
    public PurchaseResponse listFront(PurchaseFrontRequest request) throws SbcRuntimeException {
        PurchaseResponse emptyResponse = PurchaseResponse.builder()
                .goodsInfos(Collections.emptyList())
                .companyInfos(Collections.emptyList())
                .goodses(Collections.emptyList())
                .goodsIntervalPrices(Collections.emptyList())
                .build();
        if (CollectionUtils.isEmpty(request.getGoodsInfoDTOList())) {
            // 若传入的前端采购单为空,则构建空的采购单作为返回值
            return emptyResponse;
        }

        GoodsInfoRequest goodsInfoRequest = new GoodsInfoRequest();
        List<String> skuIdList = request.getGoodsInfoDTOList().stream()
                .map(PurchaseGoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList());
        goodsInfoRequest.setGoodsInfoIds(skuIdList);
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        GoodsInfoViewByIdsResponse response = getPurchaseGoodsResponse(goodsInfoRequest);
        if (response == null) {
            return emptyResponse;
        }

        final List<GoodsInfoVO> goodsInfoList = response.getGoodsInfos();
        LocalDateTime dateTime = LocalDateTime.now();
        List<String> goodsInfoIdList = new ArrayList<>();
        // 按照前端传入的采购单顺序进行排序
        goodsInfoList.sort(Comparator.comparingInt((goods) -> skuIdList.indexOf(goods.getGoodsInfoId())));
        List<GoodsInfoVO> goodsInfoListNew = goodsInfoList.stream().map(goodsInfo -> {
            List<GoodsInfoSpecDetailRelVO> goodsInfoSpecDetailRelList =
                    goodsInfoSpecDetailRelQueryProvider.listByGoodsIdAndSkuId(new GoodsInfoSpecDetailRelByGoodsIdAndSkuIdRequest(goodsInfo.getGoodsId(), goodsInfo.getGoodsInfoId())).getContext().getGoodsInfoSpecDetailRelVOList();

//            List<GoodsInfoSpecDetailRel> goodsInfoSpecDetailRelList =
//                    goodsInfoSpecDetailRelRepository.findByGoodsIdAndGoodsInfoId(
//                            goodsInfo.getGoodsId(), goodsInfo.getGoodsInfoId());
            goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetailRelList.stream()
                    .map(GoodsInfoSpecDetailRelVO::getSpecDetailRelId).collect(Collectors.toList()));
            // 填充前端传入的商品购买数量
            goodsInfo.setBuyCount(0L);
            List<PurchaseGoodsInfoDTO> dtoList = request.getGoodsInfoDTOList().stream().filter(purchase -> goodsInfo
                    .getGoodsInfoId().equals(purchase.getGoodsInfoId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dtoList)) {
                goodsInfo.setBuyCount(dtoList.get(0).getGoodsNum());
            }
            // 前端根据此time倒序排列(虽然感觉有点蠢,但以前就是这么做的,减少改动的临时做法)
            goodsInfo.setCreateTime(dateTime.minusSeconds(goodsInfoList.indexOf(goodsInfo)));
            goodsInfoIdList.add(goodsInfo.getGoodsInfoId());
            return goodsInfo;
        }).collect(Collectors.toList());

        List<GoodsVO> goodsList = response.getGoodses().stream()
                .map(goods -> {
                    goods.setGoodsDetail("");
                    return goods;
                }).collect(Collectors.toList());

        List<String> goodsIds =
                goodsInfoListNew.stream().map(GoodsInfoVO::getGoodsId).distinct().collect(Collectors.toList());
        // spu也同样按照前端传入的顺序进行排序
        goodsList.sort(Comparator.comparingInt((goods) -> goodsIds.indexOf(goods.getGoodsId())));

        //获取对应的商品分仓库存
        List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockQueryProvider
                .getGoodsWareStockByGoodsInfoIds(GoodsWareStockByGoodsForIdsRequest.builder()
                        .goodsForIdList(goodsInfoIdList)
                        .build()).getContext()
                .getGoodsWareStockVOList();
        goodsInfoListNew.forEach(goodsInfoVO -> {
            List<GoodsWareStockVO> stockList = goodsWareStockVOList.stream().
                    filter(goodsWareStock -> goodsInfoVO.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                    collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(stockList)){
                BigDecimal sumStock = stockList.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                goodsInfoVO.setStock(sumStock);
            }else {
                goodsInfoVO.setStock(BigDecimal.ZERO);
            }
        });

        return listBase(goodsInfoListNew, goodsList);
    }

    /**
     * 登陆后,查询采购单列表(分页)
     *
     * @param request 参数
     * @return 采购单数据
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public PurchaseResponse pageList(PurchaseRequest request) throws SbcRuntimeException {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase list start");

        try {
            PurchaseResponse emptyResponse = PurchaseResponse.builder().goodsInfos(Collections.emptyList()).companyInfos
                    (Collections.emptyList()).stores(Collections.emptyList())
                    .goodses(Collections.emptyList()).goodsIntervalPrices(Collections.emptyList()).build();
            //按创建时间倒序
            request.putSort("createTime", SortType.DESC.toValue());

            List<Purchase> follows = new ArrayList<>();
            GoodsInfoViewByIdsResponse response = null;
            // 查询分页数据，防止前端对象太多，不是初始化的时候只查商品的分页数据
            Page<Purchase> purchasePage = null;
            // 如果是初始化的时候
            if (request.getIsRefresh()) {
                Sort sort = request.getSort();
                if (Objects.nonNull(sort)) {
                    follows = purchaseRepository.findAll(request.getWhereCriteria(), sort);
                } else {
                    follows = purchaseRepository.findAll(request.getWhereCriteria());
                }
                if (follows.size() == 0) {
                    return emptyResponse;
                }
                sb.append(",purchaseRepository.findAll end time=");
                sb.append(System.currentTimeMillis() - sTm);
                sTm = System.currentTimeMillis();

                GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
                goodsInfoRequest.setGoodsInfoIds(follows.stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()));
                //需要显示规格值
                goodsInfoRequest.setIsHavSpecText(Constants.yes);
                goodsInfoRequest.setWareId(request.getWareId());
                goodsInfoRequest.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
                response = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
                // 初始化时重新排序购物车商品
                Map<String, GoodsInfoVO> infoVOMap = response.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
                int asc = 1;
                int desc = follows.size();
                for(int i = 0; i < follows.size(); i++) {
                    Purchase purchase = follows.get(i);
                    GoodsInfoVO goodsInfoVO = infoVOMap.get(follows.get(i).getGoodsInfoId());
                    if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO) > 0 && GoodsStatus.OK.equals(goodsInfoVO.getGoodsStatus())) {
                        purchase.setValidSort(asc);
                        asc++;
                    } else {
                        purchase.setValidSort(desc);
                        desc--;
                    }
                }
                updateList(follows);
                follows.sort(Comparator.comparing(Purchase::getValidSort));
                List<Purchase> purchaseList = follows.stream().limit(request.getPageSize()).collect(Collectors.toList());
                purchasePage = new PageImpl<>(purchaseList, request.getPageable(), follows.size());
            } else {
                request.setSortColumn("validSort");
                request.setSortRole(SortType.ASC.toValue());
                purchasePage = purchaseRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
                List<Purchase> purchasePageList = purchasePage.getContent();
                if (purchasePageList.size() == 0) {
                    return emptyResponse;
                }
                follows = purchasePageList;
                GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
                goodsInfoRequest.setGoodsInfoIds(purchasePage.getContent().stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()));
                //需要显示规格值
                goodsInfoRequest.setIsHavSpecText(Constants.yes);
                goodsInfoRequest.setWareId(request.getWareId());
                goodsInfoRequest.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
                response = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
            }
            sb.append(",goodsInfoQueryProvider.listViewByIds end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            // todo 去除规格值的关联，前台目前没有用到该信息
//        response.getGoodsInfos().forEach(goodsInfo -> {
//            List<GoodsInfoSpecDetailRelVO> goodsInfoSpecDetailRelList =
//                    goodsInfoSpecDetailRelQueryProvider.listByGoodsIdAndSkuId(new GoodsInfoSpecDetailRelByGoodsIdAndSkuIdRequest(goodsInfo.getGoodsId(), goodsInfo.getGoodsInfoId())).getContext().getGoodsInfoSpecDetailRelVOList();
//            goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetailRelList.stream().map(GoodsInfoSpecDetailRelVO::getSpecDetailRelId).collect(Collectors.toList()));
//        });

            //采购单中skuId
            List<String> goodsIds = follows.stream().map(Purchase::getGoodsId).distinct().collect(Collectors.toList());
            List<GoodsVO> goodsList = response.getGoodses().stream()
                    .filter(goods -> goodsIds.contains(goods.getGoodsId()))
                    .map(goods -> {
                        goods.setGoodsDetail("");
                        return goods;
                    }).collect(Collectors.toList());

            goodsList.sort(Comparator.comparingInt((goods) -> goodsIds.indexOf(goods.getGoodsId())));
            List<String> goodsInfoIdList = new ArrayList<>();
            List<GoodsInfoVO> goodsInfoList = IteratorUtils.zip(response.getGoodsInfos(), follows,
                    (GoodsInfoVO sku, Purchase f) -> sku.getGoodsInfoId().equals(f.getGoodsInfoId()),
                    (GoodsInfoVO sku, Purchase f) -> {
                        sku.setCreateTime(f.getCreateTime());
                        sku.setIsCheck(f.getIsCheck());
                        goodsInfoIdList.add(sku.getGoodsInfoId());
                    });

            //建立商户->SPU的扁平化结构
            List<Long> companyInfoIds = goodsInfoList.stream().map(GoodsInfoVO::getCompanyInfoId)
                    .collect(Collectors.toList());

            sb.append(",customerCommonService.listCompanyInfoByCondition start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<CompanyInfoVO> companyInfoList = customerCommonService.listCompanyInfoByCondition(
                    CompanyListRequest.builder().companyInfoIds(companyInfoIds).deleteFlag(DeleteFlag.NO).build())
                    .stream().map(companyInfo -> {
                        companyInfo.setGoodsIds(goodsList.stream()
                                .filter(goodsinfo -> goodsinfo.getCompanyInfoId().longValue() == companyInfo
                                        .getCompanyInfoId().longValue())
                                .map(GoodsVO::getGoodsId).collect(Collectors.toList()));
                        return companyInfo;
                    }).collect(Collectors.toList());

            sb.append(",customerCommonService.listCompanyInfoByCondition end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            //店铺列表
            List<StoreVO> storeList = companyInfoList.stream().map(companyInfo -> {
                if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
                    StoreVO store = companyInfo.getStoreVOList().get(0);
                    store.setGoodsIds(goodsList.stream()
                            .filter(goodsinfo -> goodsinfo.getCompanyInfoId().longValue() == companyInfo.getCompanyInfoId().longValue())
                            .map(GoodsVO::getGoodsId).collect(Collectors.toList()));
                    return store;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> storeIds = goodsList.stream().map(GoodsVO::getStoreId).distinct().collect(Collectors.toList());
            storeList.sort(Comparator.comparingInt((store) -> storeIds.indexOf(store.getStoreId())));
            //填充SKU的购买数
            this.fillBuyCount(response.getGoodsInfos(), follows);

            sb.append(",fillBuyCount end time=");
            sb.append(System.currentTimeMillis()-sTm);
            Page<PurchaseVO> newPage = purchasePage.map(this::wrapperVo);
            // 初始化的时候，需要查其他信息
            if(request.getIsRefresh()) {
                List<String> goodsInfoPageIds = purchasePage.getContent().stream()
                        .map(Purchase::getGoodsInfoId)
                        .collect(Collectors.toList());
                List<String> goodsPageIds = purchasePage.getContent().stream()
                        .map(Purchase::getGoodsId)
                        .distinct()
                        .collect(Collectors.toList());
                Map<String, GoodsInfoVO> goodsInfoVOMap = goodsInfoList.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
                List<PurchaseGoodsInfoCheckVO> purchaseGoodsInfos = new ArrayList<>();
                // 购物车商品
                for(Purchase purchase : follows) {
                    GoodsInfoVO goodsInfoVO = goodsInfoVOMap.get(purchase.getGoodsInfoId());
                    PurchaseGoodsInfoCheckVO vo = new PurchaseGoodsInfoCheckVO();
                    KsBeanUtil.copyPropertiesThird(goodsInfoVO, vo);
                    vo.setIsCheck(purchase.getIsCheck());
                    vo.setBuyCount(purchase.getGoodsNum());
                    if(goodsInfoVO.getGoodsInfoType() == 1){
                        vo.setMarketPrice(goodsInfoVO.getSpecialPrice());
                    }
                    purchaseGoodsInfos.add(vo);
                }
                return PurchaseResponse.builder()
                        .goodses(goodsList)
                        .goodsInfos(goodsInfoList)
                        .goodsPageIds(goodsPageIds)
                        .goodsInfoPageIds(goodsInfoPageIds)
                        .companyInfos(CollectionUtils.isEmpty(companyInfoList) ? Collections.EMPTY_LIST : companyInfoList)
                        .stores(CollectionUtils.isEmpty(storeList) ? Collections.EMPTY_LIST : storeList)
                        .purchasePage(newPage)
                        .purchaseGoodsInfos(purchaseGoodsInfos)
                        .build();
            } else {
                // 不为初始化的时候只查分页数据
                return PurchaseResponse.builder()
                        .goodses(goodsList)
                        .goodsInfos(goodsInfoList)
                        .purchasePage(newPage)
                        .build();
            }
        }finally {
            log.info(sb.toString());
        }

    }

    /**
     * 登陆后,查询采购单列表(不分页)
     *
     * @param request 参数
     * @return 采购单数据
     * @throws SbcRuntimeException
     */
    public PurchaseResponse list(PurchaseRequest request) throws SbcRuntimeException {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase list start");

        try {
            PurchaseResponse emptyResponse = PurchaseResponse.builder().goodsInfos(Collections.emptyList()).companyInfos
                    (Collections.emptyList()).stores(Collections.emptyList())
                    .goodses(Collections.emptyList()).goodsIntervalPrices(Collections.emptyList()).build();
            //按创建时间倒序
            request.putSort("createTime", SortType.DESC.toValue());
            List<Purchase> follows;
            Sort sort = request.getSort();
            if (Objects.nonNull(sort)) {
                follows = purchaseRepository.findAll(request.getWhereCriteria(), sort);
            } else {
                follows = purchaseRepository.findAll(request.getWhereCriteria());
            }

            sb.append(",purchaseRepository.findAll end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            if (CollectionUtils.isEmpty(follows)) {
                return emptyResponse;
            }

            GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
            goodsInfoRequest.setGoodsInfoIds(follows.stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()));
            //需要显示规格值
            goodsInfoRequest.setIsHavSpecText(Constants.yes);
            goodsInfoRequest.setWareId(request.getWareId());
            goodsInfoRequest.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
            GoodsInfoViewByIdsResponse response = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();

            sb.append(",goodsInfoQueryProvider.listViewByIds end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            // todo 去除规格值的关联，前台目前没有用到该信息
//        response.getGoodsInfos().forEach(goodsInfo -> {
//            List<GoodsInfoSpecDetailRelVO> goodsInfoSpecDetailRelList =
//                    goodsInfoSpecDetailRelQueryProvider.listByGoodsIdAndSkuId(new GoodsInfoSpecDetailRelByGoodsIdAndSkuIdRequest(goodsInfo.getGoodsId(), goodsInfo.getGoodsInfoId())).getContext().getGoodsInfoSpecDetailRelVOList();
//            goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetailRelList.stream().map(GoodsInfoSpecDetailRelVO::getSpecDetailRelId).collect(Collectors.toList()));
//        });

            //采购单中skuId
            List<String> goodsIds = follows.stream().map(Purchase::getGoodsId).distinct().collect(Collectors.toList());
            List<GoodsVO> goodsList = response.getGoodses().stream()
                    .filter(goods -> goodsIds.contains(goods.getGoodsId()))
                    .map(goods -> {
                        goods.setGoodsDetail("");
                        return goods;
                    }).collect(Collectors.toList());

            goodsList.sort(Comparator.comparingInt((goods) -> goodsIds.indexOf(goods.getGoodsId())));
            List<String> goodsInfoIdList = new ArrayList<>();
            List<GoodsInfoVO> goodsInfoList = IteratorUtils.zip(response.getGoodsInfos(), follows,
                    (GoodsInfoVO sku, Purchase f) -> sku.getGoodsInfoId().equals(f.getGoodsInfoId()),
                    (GoodsInfoVO sku, Purchase f) -> {
                        sku.setCreateTime(f.getCreateTime());
                        sku.setIsCheck(f.getIsCheck());
                        goodsInfoIdList.add(sku.getGoodsInfoId());
                    });

            //建立商户->SPU的扁平化结构
            List<Long> companyInfoIds = goodsInfoList.stream().map(GoodsInfoVO::getCompanyInfoId)
                    .collect(Collectors.toList());

            sb.append(",customerCommonService.listCompanyInfoByCondition start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<CompanyInfoVO> companyInfoList = customerCommonService.listCompanyInfoByCondition(
                    CompanyListRequest.builder().companyInfoIds(companyInfoIds).deleteFlag(DeleteFlag.NO).build())
                    .stream().map(companyInfo -> {
                        companyInfo.setGoodsIds(goodsList.stream()
                                .filter(goodsinfo -> goodsinfo.getCompanyInfoId().longValue() == companyInfo
                                        .getCompanyInfoId().longValue())
                                .map(GoodsVO::getGoodsId).collect(Collectors.toList()));
                        return companyInfo;
                    }).collect(Collectors.toList());

            sb.append(",customerCommonService.listCompanyInfoByCondition end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            //店铺列表
            List<StoreVO> storeList = companyInfoList.stream().map(companyInfo -> {
                if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
                    StoreVO store = companyInfo.getStoreVOList().get(0);
                    store.setGoodsIds(goodsList.stream()
                            .filter(goodsinfo -> goodsinfo.getCompanyInfoId().longValue() == companyInfo.getCompanyInfoId().longValue())
                            .map(GoodsVO::getGoodsId).collect(Collectors.toList()));
                    return store;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> storeIds = goodsList.stream().map(GoodsVO::getStoreId).distinct().collect(Collectors.toList());
            storeList.sort(Comparator.comparingInt((store) -> storeIds.indexOf(store.getStoreId())));
            //填充SKU的购买数
            this.fillBuyCount(response.getGoodsInfos(), follows);

            sb.append(",fillBuyCount end time=");
            sb.append(System.currentTimeMillis()-sTm);

            return PurchaseResponse.builder()
                    .goodses(goodsList)
                    .goodsInfos(goodsInfoList)
                    .companyInfos(CollectionUtils.isEmpty(companyInfoList) ? Collections.EMPTY_LIST : companyInfoList)
                    .stores(CollectionUtils.isEmpty(storeList) ? Collections.EMPTY_LIST : storeList)
                    .build();
        }finally {
            log.info(sb.toString());
        }

    }

    public void updateList(List<Purchase> purchases) {
        purchaseRepository.saveAll(purchases);
    }
    /**
     * 异步更新选择状态
     *
     * @param request
     * @param goodsInfoIds
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateIsCheck(PurchaseRequest request, List<String> goodsInfoIds) throws SbcRuntimeException {
        List<Purchase> purchases = purchaseRepository.findAll(request.getWhereCriteria());
        List<Purchase> savePurchases = new ArrayList<>();
        for (Purchase purchase : purchases) {
            if (goodsInfoIds.contains(purchase.getGoodsInfoId()) && DefaultFlag.NO.equals(purchase.getIsCheck())) {
                purchase.setIsCheck(DefaultFlag.YES);
                savePurchases.add(purchase);
            }
            if (!(goodsInfoIds.contains(purchase.getGoodsInfoId())) && DefaultFlag.YES.equals(purchase.getIsCheck())) {
                purchase.setIsCheck(DefaultFlag.NO);
                savePurchases.add(purchase);
            }
        }
        if (savePurchases.size() > 0) {
            purchaseRepository.saveAll(savePurchases);
        }
    }

        /**
         * 查询采购单
         *
         * @param customerId
         * @param goodsInfoIds
         * @return
         */
    public List<Purchase> queryPurchase(String customerId, List<String> goodsInfoIds, String inviteeId) {
        //分页查询SKU信息列表
        PurchaseRequest request = PurchaseRequest.builder()
                .customerId(customerId)
                .goodsInfoIds(goodsInfoIds)
                .inviteeId(inviteeId)
                .build();
        Sort sort = request.getSort();
        List<Purchase> list;
        if (Objects.nonNull(sort)) {
            list = purchaseRepository.findAll(request.getWhereCriteria(), sort);
        } else {
            list = purchaseRepository.findAll(request.getWhereCriteria());
        }

        return list;
    }

    //    /**
//     * 获取采购单商品数量
//     *
//     * @param userId
//     * @return
//     */
//    public Integer countGoods(String userId) {
//        return purchaseRepository.countByCustomerId(userId);
//    }
    /*
    /**
     * 获取采购单商品数量
     *
     * @param userId
     * @return
     */
    public Integer countGoods(String userId, String inviteeId) {
        return purchaseRepository.countByCustomerIdAndInviteeId(userId, inviteeId);
    }

    /**
     * 获取采购单商品总数
     *
     * @param userId
     * @return
     */
    public Long queryGoodsNum(String userId, String inviteeId) {
        return purchaseRepository.queryGoodsNum(userId, inviteeId);
    }

    /**
     * 获取采购单商品数量
     *
     * @param userId
     * @param inviteeId
     * @param companyInfoId
     * @return
     */
    public Integer countGoodsByCompanyInfoId(String userId, String inviteeId, Long companyInfoId) {
        return purchaseRepository.countByCustomerIdAndInviteeIdAndCompanyInfoId(userId, inviteeId, companyInfoId);
    }

    /**
     * 商品收藏调整商品数量
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateNum(PurchaseRequest request) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(
                GoodsInfoByIdRequest.builder().goodsInfoId(request.getGoodsInfoId()).wareId(request.getWareId()).build()
        ).getContext();
        if (goodsInfo == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        List<Purchase> purchaseList = purchaseRepository.findAll(PurchaseRequest.builder()
                .customerId(request.getCustomerId())
                .goodsInfoId(request.getGoodsInfoId())
                .inviteeId(request.getInviteeId())
                .build().getWhereCriteria());
        Integer countNum = countGoods(request.getCustomerId(), request.getInviteeId());
        //如果存在，更新标识和数量
        if (CollectionUtils.isNotEmpty(purchaseList)) {
            Purchase purchase = purchaseList.get(0);
            //更新标识：收藏->all
            if (request.getVerifyStock() && goodsInfo.getStock().compareTo(BigDecimal.valueOf(request.getGoodsNum())) < 0 && purchase.getGoodsNum() < request.getGoodsNum()) {
                throw new SbcRuntimeException("K-030302", new Object[]{goodsInfo.getStock()});
            }
            purchase.setGoodsNum(request.getGoodsNum());
            purchaseRepository.save(purchase);
        } else {
//            if (countNum >= Constants.PURCHASE_MAX_SIZE) {
//                throw new SbcRuntimeException(SiteResultCode.ERROR_050121, new Object[]{Constants.PURCHASE_MAX_SIZE});
//            }
            //如果数据不存在，自动加入采购单
            Purchase purchase = new Purchase();
            BeanUtils.copyProperties(request, purchase);
            purchase.setCreateTime(LocalDateTime.now());
            purchase.setCompanyInfoId(goodsInfo.getCompanyInfoId());
            purchase.setGoodsId(goodsInfo.getGoodsId());
            purchase.setIsCheck(DefaultFlag.YES);
            purchaseRepository.save(purchase);
        }
    }

    /**
     * 商品收藏调整商品数量
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateNumByIds(PurchaseRequest request) {
        List<Purchase> purchaseList = purchaseRepository.findAll(PurchaseRequest.builder()
                .customerId(request.getCustomerId())
                .goodsInfoIds(request.getGoodsInfoIds())
                .inviteeId(request.getInviteeId())
                .build().getWhereCriteria());
        //如果存在，更新标识和数量
        if (CollectionUtils.isNotEmpty(purchaseList)) {
            boolean updateFlag=false;
            for (Purchase param : purchaseList) {
                Optional<GoodsInfoDTO> goodsInfoDTO = request.getGoodsInfos().stream().filter(goods -> goods.getGoodsInfoId()
                        .equals(param.getGoodsInfoId())).findFirst();
                if (goodsInfoDTO.isPresent()) {
                    updateFlag = true;
                    param.setGoodsNum(goodsInfoDTO.get().getStock().compareTo(BigDecimal.ZERO) == 0 ? 1 : goodsInfoDTO.get().getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());
                }
            }
            if (updateFlag){
                purchaseRepository.saveAll(purchaseList);
            }
        }
    }

    /**
     * 删除采购单
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(PurchaseRequest request) {
        purchaseRepository.deleteByGoodsInfoids(request.getGoodsInfoIds(), request.getCustomerId(),
                request.getInviteeId());
        GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest = new GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest();
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setCustomerId(request.getCustomerId());
        goodsMarketingProvider.deleteByCustomerIdAndGoodsInfoIds(goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest);
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    /**
     * 采购单商品移入收藏夹
     *
     * @param queryRequest
     * @return
     */
    @Transactional
    public void addFollow(PurchaseRequest queryRequest) {
        //获取所有收藏商品
        GoodsCustomerFollowQueryRequest followQueryRequest = GoodsCustomerFollowQueryRequest.builder()
                .customerId(queryRequest.getCustomerId())
                .wareId(queryRequest.getWareId())
                .bulkWareId(queryRequest.getBulkWareId())
                .build();
        List<GoodsCustomerFollow> existSku =
                goodsCustomerFollowRepository.findAll(followQueryRequest.getWhereCriteria());
        List<String> newSkuIds;
        if (CollectionUtils.isNotEmpty(existSku)) {
            List<String> existSkuIds =
                    existSku.stream().map(GoodsCustomerFollow::getGoodsInfoId).collect(Collectors.toList());
            //提取没有收藏的skuId
            newSkuIds =
                    queryRequest.getGoodsInfoIds().stream().filter(s -> !existSkuIds.contains(s)).collect(Collectors.toList());
            //如果有新收藏的skuId,验认收藏最大限制
            if (CollectionUtils.isNotEmpty(newSkuIds) && existSkuIds.size() + newSkuIds.size() > Constants.FOLLOW_MAX_SIZE) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_030401, new Object[]{Constants.FOLLOW_MAX_SIZE});
            }
        } else {
            newSkuIds = queryRequest.getGoodsInfoIds();
        }

        List<Purchase> purchaseList =
                purchaseRepository.queryPurchaseByGoodsIdsAndCustomerId(queryRequest.getGoodsInfoIds(),
                        queryRequest.getCustomerId(), queryRequest.getInviteeId());
        if (CollectionUtils.isNotEmpty(purchaseList)){
            purchaseList=purchaseList.stream().filter(v->{
                Long pWareID = 0L;
                if (Objects.isNull(v.getWareId())){
                    pWareID = 1L;
                }else {
                    pWareID=v.getWareId();
                }
                if (queryRequest.getWareId()==pWareID){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        List<String> newSkuIdsBack = newSkuIds;

        if(CollectionUtils.isEmpty(purchaseList)){
            if (queryRequest.getSubType()==0){
                List<ShopCart> shopCartList = shopCartService.getShopCarCache(queryRequest.getCustomerId(), queryRequest.getWareId().toString());
                if(CollectionUtils.isEmpty(shopCartList)){
                    return;
                }
                List<String> goodsInfoIds = Optional.ofNullable(queryRequest.getGoodsInfoIds()).orElse(Lists.newArrayList());
                shopCartList = shopCartList.stream().filter(x->{
                    return goodsInfoIds.contains(x.getGoodsInfoId());
                }).collect(Collectors.toList());

                shopCartList=shopCartList.stream().filter(v->{
                    if (queryRequest.getWareId()==v.getWareId()){
                        return true;
                    }
                    return false;
                }).filter(distinctByKey((p)->(p.getGoodsInfoId()))).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(shopCartList)){
                    shopCartList.stream().forEach(info -> {
                        GoodsCustomerFollow follow = new GoodsCustomerFollow();
                        BeanUtils.copyProperties(info, follow);
                        follow.setFollowTime(LocalDateTime.now());
                        follow.setWareId(queryRequest.getWareId());
                        follow.setFollowType(0);// 2表示散批商品
                        if (CollectionUtils.isNotEmpty(newSkuIdsBack) && newSkuIdsBack.contains(follow.getGoodsInfoId())) {
                            goodsCustomerFollowRepository.save(follow);
                        }

                        // 非赠品才删除
                        if (!queryRequest.getIsGift()) {
                            // 清理购物车的中的缓存
                            String customerId = info.getCustomerId();
                            Long wareId = info.getWareId();
                            Long devanningId = info.getDevanningId();
                            redisCache.hashDel(RedisKeyConstants.SHOPPING_CART_EXTRA_HASH+customerId+wareId, String.valueOf(devanningId));

                            String key = RedisKeyConstants.SHOPPING_CART_HASH+customerId+wareId;
                            redisCache.hashDel(key,RedisKeyConstants.is_check+devanningId);
                            redisCache.hashDel(key,RedisKeyConstants.good_num+devanningId);
                            shopCartRepository.delete(info);
                        }
                    });
                }
            } else if(queryRequest.getSubType() == 2){// 2代表新散批
                List<BulkShopCart> bulkShopCarts = bulkShopCartService.getShopCarCache(queryRequest.getCustomerId(), queryRequest.getBulkWareId());
                if(CollectionUtils.isEmpty(bulkShopCarts)){
                    return;
                }
                List<String> goodsInfoIds = Optional.ofNullable(queryRequest.getGoodsInfoIds()).orElse(Lists.newArrayList());
                bulkShopCarts = bulkShopCarts.stream().filter(x->{
                    return goodsInfoIds.contains(x.getGoodsInfoId());
                }).collect(Collectors.toList());
                bulkShopCarts = bulkShopCarts.stream().filter(v->{
                    if (queryRequest.getBulkWareId() == v.getWareId()){
                        return true;
                    }
                    return false;
                }).filter(distinctByKey((p)->(p.getGoodsInfoId()))).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(bulkShopCarts)){
                    bulkShopCarts.stream().forEach(info -> {
                        GoodsCustomerFollow follow = new GoodsCustomerFollow();
                        BeanUtils.copyProperties(info, follow);
                        follow.setFollowTime(LocalDateTime.now());
                        follow.setWareId(queryRequest.getBulkWareId());
                        follow.setFollowType(2);// 2表示散批商品
                        if (CollectionUtils.isNotEmpty(newSkuIdsBack) && newSkuIdsBack.contains(follow.getGoodsInfoId())) {
                            goodsCustomerFollowRepository.save(follow);
                        }

                        // 非赠品才删除
                        if (!queryRequest.getIsGift()) {
                            // 清理购物车的中的缓存
                            String customerId = info.getCustomerId();
                            Long wareId = info.getWareId();
                            String goodsInfoId = info.getGoodsInfoId();

                            redisCache.hashDel(BulkShopCartCacheSupport.buildExtraKey(customerId, wareId), goodsInfoId);
                            redisCache.hashDel(BulkShopCartCacheSupport.buildKey(customerId, wareId),BulkShopCartCacheSupport.buildHashKeyOfIsCheck(goodsInfoId));
                            redisCache.hashDel(BulkShopCartCacheSupport.buildKey(customerId, wareId),BulkShopCartCacheSupport.buildHashKeyOfGoodNum(goodsInfoId));
                            bulkShopCartRepository.delete(info);
                        }
                    });
                }
            } else {
                List<ShopCartNewPileTrade> shopCartList = shopCartNewPileTradeService.getShopCarCache(queryRequest.getCustomerId(), queryRequest.getWareId().toString());
                if(CollectionUtils.isEmpty(shopCartList)){
                    return;
                }
                List<String> goodsInfoIds = Optional.ofNullable(queryRequest.getGoodsInfoIds()).orElse(Lists.newArrayList());
                shopCartList = shopCartList.stream().filter(x->{
                    return goodsInfoIds.contains(x.getGoodsInfoId());
                }).collect(Collectors.toList());

                shopCartList=shopCartList.stream().filter(v->{
                    if (queryRequest.getWareId()==v.getWareId()){
                        return true;
                    }
                    return false;
                }).filter(distinctByKey((p)->(p.getGoodsInfoId()))).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(shopCartList)){
                    shopCartList.stream().forEach(info -> {
                        GoodsCustomerFollow follow = new GoodsCustomerFollow();
                        BeanUtils.copyProperties(info, follow);
                        follow.setFollowTime(LocalDateTime.now());
                        follow.setWareId(queryRequest.getWareId());
                        follow.setFollowType(0);// 2表示散批商品
                        if (CollectionUtils.isNotEmpty(newSkuIdsBack) && newSkuIdsBack.contains(follow.getGoodsInfoId())) {
                            goodsCustomerFollowRepository.save(follow);
                        }

                        // 非赠品才删除
                        if (!queryRequest.getIsGift()) {
                            // 清理购物车的中的缓存
                            String customerId = info.getCustomerId();
                            Long wareId = info.getWareId();
                            Long devanningId = info.getDevanningId();
                            redisCache.hashDel(RedisKeyConstants.STORE_SHOPPING_CART_EXTRA_HASH+customerId+wareId, String.valueOf(devanningId));
                            String key = RedisKeyConstants.STORE_SHOPPING_CART_HASH+customerId+wareId;
                            redisCache.hashDel(key,RedisKeyConstants.is_check+devanningId);
                            redisCache.hashDel(key,RedisKeyConstants.good_num+devanningId);
                            shopCartNewPileTradeRepository.delete(info);
                        }
                    });
                }
            }


        }else{
            purchaseList.stream().forEach(info -> {
                GoodsCustomerFollow follow = new GoodsCustomerFollow();
                BeanUtils.copyProperties(info, follow);
                follow.setFollowTime(LocalDateTime.now());
                if (CollectionUtils.isNotEmpty(newSkuIdsBack) && newSkuIdsBack.contains(follow.getGoodsInfoId())) {
                    goodsCustomerFollowRepository.save(follow);
                }

                // 非赠品才删除
                if (!queryRequest.getIsGift()) {
                    purchaseRepository.delete(info);
                }
            });
        }

    }

    /**
     * 采购单商品移入收藏夹
     *
     * @param queryRequest
     * @return
     */
    @Transactional
    public void newAddFollow(PurchaseRequest queryRequest) {
        this.addFollow(queryRequest);

        //获取所有收藏商品
//        GoodsCustomerFollowQueryRequest followQueryRequest = GoodsCustomerFollowQueryRequest.builder()
//                .customerId(queryRequest.getCustomerId())
//                .wareId(queryRequest.getWareId())
//                .bulkWareId(queryRequest.getBulkWareId())
//                .build();
//        List<GoodsCustomerFollow> existSku = goodsCustomerFollowRepository.findAll(followQueryRequest.getWhereCriteria());
//        List<String> newSkuIds;
//        // 过滤收藏夹原来的SKU
//        if (CollectionUtils.isNotEmpty(existSku)) {
//            List<String> existSkuIds = existSku.stream().map(GoodsCustomerFollow::getGoodsInfoId).collect(Collectors.toList());
//            //提取没有收藏的skuId
//            newSkuIds = queryRequest.getGoodsInfoIds().stream().filter(s -> !existSkuIds.contains(s)).collect(Collectors.toList());
//            //如果有新收藏的skuId,验认收藏最大限制
//            if (CollectionUtils.isNotEmpty(newSkuIds) && existSkuIds.size() + newSkuIds.size() > Constants.FOLLOW_MAX_SIZE) {
//                throw new SbcRuntimeException(SiteResultCode.ERROR_030401, new Object[]{Constants.FOLLOW_MAX_SIZE});
//            }
//        } else {
//            newSkuIds = queryRequest.getGoodsInfoIds();
//        }
//
//        List<Purchase> purchaseList = purchaseRepository.queryPurchaseByGoodsIdsAndCustomerId(
//            queryRequest.getGoodsInfoIds(),// 收藏的商品ID
//            queryRequest.getCustomerId(),
//            queryRequest.getInviteeId()
//        );
//        if (CollectionUtils.isNotEmpty(purchaseList)){
//            purchaseList = purchaseList.stream().filter(v->{
//                Long pWareID = 0L;
//                if (Objects.isNull(v.getWareId())){
//                    pWareID = 1L;
//                }else {
//                    pWareID=v.getWareId();
//                }
//                if (queryRequest.getWareId()==pWareID){
//                    return true;
//                }
//                return false;
//            }).collect(Collectors.toList());
//        }
//        List<String> newSkuIdsBack = newSkuIds;
//
//        if(CollectionUtils.isEmpty(purchaseList)){
//            List<ShopCart> shopCartList = shopCartRepository.queryPurchaseByGoodsIdsAndCustomerId(
//                    queryRequest.getGoodsInfoIds(),
//                    queryRequest.getCustomerId(),
//                    queryRequest.getInviteeId()
//            );
//            shopCartList = shopCartList.stream().filter(v->{
//                if (queryRequest.getWareId()==v.getWareId()){
//                    return true;
//                }
//                return false;
//            }).filter(distinctByKey((p)->(p.getGoodsInfoId()))).collect(Collectors.toList());
//            if(CollectionUtils.isNotEmpty(shopCartList)){
//                shopCartList.stream().forEach(info -> {
//                    GoodsCustomerFollow follow = new GoodsCustomerFollow();
//                    BeanUtils.copyProperties(info, follow);
//                    follow.setFollowTime(LocalDateTime.now());
//                    follow.setWareId(queryRequest.getWareId());
//                    if (CollectionUtils.isNotEmpty(newSkuIdsBack) && newSkuIdsBack.contains(follow.getGoodsInfoId())) {
//                        goodsCustomerFollowRepository.save(follow);
//                    }
//                    // 非赠品才删除
//                    if (!queryRequest.getIsGift()) {
//
//                        // 清理购物车的中的缓存
//                        String customerId = info.getCustomerId();
//                        Long wareId = info.getWareId();
//                        Long devanningId = info.getDevanningId();
//                        String key = RedisKeyConstants.SHOPPING_CART_EXTRA_HASH+customerId+wareId;
//                        redisCache.hashDel(key,String.valueOf(devanningId));
//                        redisCache.hashDel(key,RedisKeyConstants.is_check+devanningId);
//                        redisCache.hashDel(key,RedisKeyConstants.good_num+devanningId);
//
//                        shopCartRepository.delete(info);
//                    }
//                });
//            }
//        } else {
//            purchaseList.stream().forEach(info -> {
//                GoodsCustomerFollow follow = new GoodsCustomerFollow();
//                BeanUtils.copyProperties(info, follow);
//                follow.setFollowTime(LocalDateTime.now());
//                if (CollectionUtils.isNotEmpty(newSkuIdsBack) && newSkuIdsBack.contains(follow.getGoodsInfoId())) {
//                    goodsCustomerFollowRepository.save(follow);
//                }
//                // 非赠品才删除
//                if (!queryRequest.getIsGift()) {
//                    // 删除购物车中对应的条目
//                    purchaseRepository.delete(info);
//                }
//            });
//        }

    }

    /**
     * 清除失效商品
     *
     * @param userId
     */
    @Transactional
    public void clearLoseGoods(String userId, DistributeChannel distributeChannel) {
        List<Purchase> purchaseList = purchaseRepository.queryPurchaseByCustomerIdAndInviteeId(userId,
                distributeChannel.getInviteeId());
        //筛选商品id
        List<String> goodsIds = purchaseList.stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList());
        GoodsInfoListByIdsRequest goodsInfoQueryRequest = new GoodsInfoListByIdsRequest();
        //查询商品列表
        goodsInfoQueryRequest.setGoodsInfoIds(goodsIds);
        List<GoodsInfoVO> goodsInfoList =
                goodsInfoQueryProvider.listByIds(goodsInfoQueryRequest).getContext().getGoodsInfos();
        verifyDistributorGoodsInfo(distributeChannel, goodsInfoList);
        //店铺id
        List<Long> storeIds = goodsInfoList.stream().map(GoodsInfoVO::getStoreId).collect(Collectors.toList());
        //查询店铺列表
        List<StoreVO> storeList = storeQueryProvider
                .listNoDeleteStoreByIds(ListNoDeleteStoreByIdsRequest.builder().storeIds(storeIds).build())
                .getContext().getStoreVOList();
        purchaseList.forEach(item -> {
            GoodsInfoVO goodsInfo = goodsInfoList.stream().filter(goods -> goods.getGoodsInfoId().equals(item
                    .getGoodsInfoId())).findFirst().orElse(null);
            StoreVO store =
                    storeList.stream().filter(store1 -> store1.getStoreId().longValue() == goodsInfo.getStoreId().longValue()).findFirst().orElse(null);
            if (Objects.nonNull(goodsInfo) && Objects.nonNull(store)) {
                Duration duration = Duration.between(store.getContractEndDate(), LocalDateTime.now());
                if (goodsInfo.getAddedFlag() == 0 || goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAuditStatus() == CheckStatus.FORBADE || goodsInfo.getGoodsStatus() == GoodsStatus.INVALID
                        || store.getStoreState() == StoreState.CLOSED || duration.toMinutes() >= 0) {
                    purchaseRepository.delete(item);
                }
            }
        });
    }

    /**
     * 填充客户购买数
     *
     * @param goodsInfoList SKU商品
     * @param customerId    客户编号
     */
    public List<GoodsInfoVO> fillBuyCount(List<GoodsInfoVO> goodsInfoList, String customerId, String inviteeId) {
        //分页查询SKU信息列表
        List<Purchase> follows = purchaseRepository.findAll(PurchaseRequest.builder()
                .customerId(customerId).inviteeId(inviteeId)
                .build().getWhereCriteria());
        this.fillBuyCount(goodsInfoList, follows);
        return goodsInfoList;
    }

    /**
     * 填充客户购买数
     *
     * @param goodsInfoList SKU商品
     * @param followList    SKU商品
     */
    public void fillBuyCount(List<GoodsInfoVO> goodsInfoList, List<Purchase> followList) {
        if (CollectionUtils.isEmpty(goodsInfoList) || CollectionUtils.isEmpty(followList)) {
            return;
        }
        //填充SKU的购买数
        goodsInfoList.stream().forEach(goodsInfo -> {
            goodsInfo.setBuyCount(0L);
            Purchase purchase = followList.stream().filter(p -> goodsInfo.getGoodsInfoId().equals(p.getGoodsInfoId())).findFirst().orElse(null);
            if (Objects.nonNull(purchase)) {
                goodsInfo.setBuyCount(purchase.getGoodsNum());
            }
        });
    }

    /**
     * [公共方法]获取采购单中 参加同种营销的商品列表/总额/优惠
     * 同时处理未登录(customer==null) , 已登录的场景(customer!=null)
     *
     * @param marketingId  营销id
     * @param customer     登陆用户信息
     * @param frontReq     前端传入的采购单信息 以及 勾选skuIdList
     * @param goodsInfoIds 已勾选的,参加同种营销的 商品idList
     * @param isPurchase   是否采购单
     * @return 采购单计算结果集
     * @author bail
     */
    public PurchaseMarketingCalcResponse calcMarketingByMarketingIdBase(Long marketingId,
                                                                        CustomerVO customer,
                                                                        PurchaseFrontRequest frontReq,
                                                                        List<String> goodsInfoIds,
                                                                        boolean isPurchase,
                                                                        List<String> checkSkuIds,
                                                                        Long wareId) {
        PurchaseMarketingCalcResponse response = new PurchaseMarketingCalcResponse();

        // 查询该营销活动参与的商品列表
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        MarketingForEndVO marketingResponse =
                marketingQueryProvider.getByIdForCustomer(marketingGetByIdRequest).getContext().getMarketingForEndVO();

        // 获取用户在店铺里的等级
        Map<Long, CommonLevelVO> levelMap = this.getLevelMapByStoreIds(Arrays.asList(marketingResponse.getStoreId())
                , customer);

        if (!validMarketing(marketingResponse, levelMap)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, marketingResponse.getMarketingName() + "无效！");
        }

        List<GoodsInfoVO> goodsInfoList = Collections.EMPTY_LIST;
        long lackCount = 0L;
        BigDecimal lackAmount = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        // 采购单勾选的商品中没有参与该营销的情况，取营销的最低等级
        if (isPurchase && (goodsInfoIds == null || goodsInfoIds.isEmpty())) {
            // 满金额
            if (marketingResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {

                BigDecimal levelAmount;

                switch (marketingResponse.getMarketingType()) {
                    case REDUCTION:
                        levelAmount = marketingResponse.getFullReductionLevelList().get(0).getFullAmount();
                        discount = marketingResponse.getFullReductionLevelList().get(0).getReduction();
                        response.setFullReductionLevel(marketingResponse.getFullReductionLevelList().get(0));

                        break;
                    case DISCOUNT:
                        levelAmount = marketingResponse.getFullDiscountLevelList().get(0).getFullAmount();
                        discount = marketingResponse.getFullDiscountLevelList().get(0).getDiscount();
                        response.setFullDiscountLevel(marketingResponse.getFullDiscountLevelList().get(0));

                        break;
                    case GIFT:
                        levelAmount = marketingResponse.getFullGiftLevelList().get(0).getFullAmount();
                        response.setFullGiftLevel(marketingResponse.getFullGiftLevelList().get(0));

                        response.setFullGiftLevelList(marketingResponse.getFullGiftLevelList());

                        break;
                    default:
                        levelAmount = BigDecimal.ZERO;
                }

                response.setLack(levelAmount);

            } else if (marketingResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                // 满数量
                // 计算达到营销级别的数量
                Long levelCount;

                switch (marketingResponse.getMarketingType()) {
                    case REDUCTION:
                        levelCount = marketingResponse.getFullReductionLevelList().get(0).getFullCount();
                        discount = marketingResponse.getFullReductionLevelList().get(0).getReduction();
                        response.setFullReductionLevel(marketingResponse.getFullReductionLevelList().get(0));

                        break;
                    case DISCOUNT:
                        levelCount = marketingResponse.getFullDiscountLevelList().get(0).getFullCount();
                        discount = marketingResponse.getFullDiscountLevelList().get(0).getDiscount();
                        response.setFullDiscountLevel(marketingResponse.getFullDiscountLevelList().get(0));

                        break;
                    case GIFT:
                        levelCount = marketingResponse.getFullGiftLevelList().get(0).getFullCount();
                        response.setFullGiftLevel(marketingResponse.getFullGiftLevelList().get(0));

                        response.setFullGiftLevelList(marketingResponse.getFullGiftLevelList());

                        break;
                    default:
                        levelCount = 0L;
                }

                response.setLack(BigDecimal.valueOf(levelCount));
            }else if (marketingResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER) {
                // 订单满赠
                // 计算达到营销级别的数量
                Long levelCount;

                switch (marketingResponse.getMarketingType()) {
                    case GIFT:
                        levelCount = marketingResponse.getFullGiftLevelList().get(0).getFullCount();
                        response.setFullGiftLevelList(marketingResponse.getFullGiftLevelList());
                        response.setFullGiftLevel(marketingResponse.getFullGiftLevelList().get(0));
                        break;
                    default:
                        levelCount = 0L;
                }
                response.setLack(BigDecimal.valueOf(levelCount));
            } else {
                throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
            }

            response.setGoodsInfoList(goodsInfoList);
            response.setDiscount(discount);
        } else {

            GoodsInfoViewByIdsResponse goodsInfoResponse;
            GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();

            if (customer != null) {
                //这里增加了一个逻辑，所有购物车的商品都可参加订单满赠的活动
                List<String> skuIds = goodsInfoIds;
                if(isGifFullOrder(goodsInfoIds)){
                    skuIds = Arrays.asList();
                }
                PurchaseRequest request = PurchaseRequest.builder()
                        .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
                        .goodsInfoIds(skuIds)
                        .build();
                List<Purchase> follows;
                Sort sort = request.getSort();
                if (Objects.nonNull(sort)) {
                    follows = purchaseRepository.findAll(request.getWhereCriteria(), sort);
                } else {
                    follows = purchaseRepository.findAll(request.getWhereCriteria());
                }

                goodsInfoRequest.setGoodsInfoIds(follows.stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()));
                goodsInfoRequest.setWareId(wareId);
                goodsInfoResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
                //填充SKU的购买数
                this.fillBuyCount(goodsInfoResponse.getGoodsInfos(), follows);

            } else {
                goodsInfoRequest.setGoodsInfoIds(goodsInfoIds);
                goodsInfoRequest.setWareId(wareId);
                goodsInfoResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
                if (goodsInfoResponse.getGoodsInfos() != null) {
                    goodsInfoResponse.getGoodsInfos().forEach((goodsInfo) -> {
                        goodsInfo.setBuyCount(0L);
                        List<PurchaseGoodsInfoDTO> dtoList =
                                frontReq.getGoodsInfoDTOList().stream().filter((purchase) ->
                                        goodsInfo.getGoodsInfoId().equals(purchase.getGoodsInfoId())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(dtoList)) {
                            goodsInfo.setBuyCount((dtoList.get(0)).getGoodsNum());
                        }
                    });
                }
            }


            //设定SKU状态
            goodsInfoResponse.setGoodsInfos(
                    goodsInfoProvider.fillGoodsStatus(
                            GoodsInfoFillGoodsStatusRequest.builder()
                                    .goodsInfos(KsBeanUtil.convertList(goodsInfoResponse.getGoodsInfos(),
                                            GoodsInfoDTO.class))
                                    .build()
                    ).getContext().getGoodsInfos()
            );
            //折扣商品 将特价设置成市场价进行计算&大客户价格计算
            goodsInfoResponse.getGoodsInfos().forEach(g -> {
                if (Objects.nonNull(g) && Objects.nonNull(g.getGoodsInfoType()) && 1 == g.getGoodsInfoType()) {
                    if (Objects.nonNull(g.getSpecialPrice())) {
                        g.setMarketPrice(g.getSpecialPrice());
                    }
                }
//                if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())
//                        && Objects.nonNull(g) && Objects.nonNull(g.getVipPrice())
//                        && g.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
//                    g.setMarketPrice(g.getVipPrice());
//                }
            });

            List<GoodsMarketingVO> goodsMarketingList;
            if (CollectionUtils.isNotEmpty(goodsInfoResponse.getGoodsInfos())) {
                if (customer != null) {
                    goodsMarketingList = this.queryGoodsMarketingList(customer.getCustomerId());
                } else {
                    goodsMarketingList = frontReq.getGoodsMarketingDTOList().stream().map((dto) ->
                            GoodsMarketingVO.builder().marketingId(dto.getMarketingId()).goodsInfoId(dto.getGoodsInfoId()).build()
                    ).collect(Collectors.toList());
                }
                if (!isPurchase) {
                    Map<String, Long> goodsMarketingMap =
                            goodsMarketingList.stream().collect(Collectors.toMap(GoodsMarketingVO::getGoodsInfoId,
                                    GoodsMarketingVO::getMarketingId));

                    // 凑单页，过滤出参加营销的商品
                    //订单满赠不过滤营销商品
                    if(Objects.nonNull(goodsMarketingMap.get("all"))){
                        //存在订单满赠
                        goodsInfoList = goodsInfoResponse.getGoodsInfos();
                    } else{
                        List<MarketingScopeVO> marketingScopeList = marketingResponse.getMarketingScopeList();
                        goodsInfoList = goodsInfoResponse.getGoodsInfos().stream()
                                .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                        && marketingScopeList.stream().anyMatch(scope -> scope.getScopeId().equals(goodsInfo.getGoodsInfoId())))
                                .map(goodsInfo -> {
                                    //
                                    if (!marketingId.equals(goodsMarketingMap.get(goodsInfo.getGoodsInfoId()))) {
                                        goodsInfo.setBuyCount(0L);
                                    }

                                    return goodsInfo;
                                }).collect(Collectors.toList());
                    }
                } else {
                    //订单满赠不过滤营销商品
                    if(isGifFullOrder(goodsInfoIds)){
                        // 过滤出参加营销的商品
                        goodsInfoList = goodsInfoResponse.getGoodsInfos().stream()
                                .filter(goodsInfo->goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                    && checkSkuIds.contains(goodsInfo.getGoodsInfoId()))
                                .collect(Collectors.toList());
                        goodsInfoList = goodsInfoList.stream().filter(good->
                                goodsMarketingList.stream().filter(g->
                                        g.getMarketingId().equals(marketingId)
                                                && good.getGoodsInfoId().equals(g.getGoodsInfoId())).findFirst().isPresent()).collect(Collectors.toList());

                    }else{
                        // 过滤出参加营销的商品
                        List<MarketingScopeVO> marketingScopeList = marketingResponse.getMarketingScopeList();
                        goodsInfoList = goodsInfoResponse.getGoodsInfos().stream()
                                .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                        && marketingScopeList.stream().anyMatch(scope -> scope.getScopeId().equals(goodsInfo.getGoodsInfoId())))
                                .collect(Collectors.toList());
                    }

                }
            }

            //计算区间价
            GoodsIntervalPriceByCustomerIdResponse intervalPriceResponse =
                    goodsIntervalPriceProvider.putByCustomerId(
                            GoodsIntervalPriceByCustomerIdRequest.builder()
                                    .goodsInfoDTOList(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class))
                                    .customerId(customer != null ? customer.getCustomerId() : null).build()).getContext();
            List<GoodsIntervalPriceVO> goodsIntervalPrices = intervalPriceResponse.getGoodsIntervalPriceVOList();
            goodsInfoList = intervalPriceResponse.getGoodsInfoVOList();

            //计算级别价格
            goodsInfoList = marketingLevelPluginProvider.goodsListFilter(
                    MarketingLevelGoodsListFilterRequest.builder()
                            .customerDTO(customer != null ? KsBeanUtil.convert(customer, CustomerDTO.class) : null)
                            .goodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class)).build())
                    .getContext().getGoodsInfoVOList();

            // 计算商品总额
            BigDecimal totalAmount = goodsInfoList.stream().map(goodsInfo -> {
                if (goodsInfo.getPriceType() == GoodsPriceType.STOCK.toValue()) {
                    // 按区间设价，获取满足的多个等级的区间价里的最大价格
                    Optional<GoodsIntervalPriceVO> optional = goodsIntervalPrices.stream().filter(goodsIntervalPrice
                            -> goodsIntervalPrice.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                            .filter(goodsIntervalPrice -> goodsInfo.getBuyCount().compareTo(goodsIntervalPrice.getCount()) >= 0).max(Comparator.comparingLong(GoodsIntervalPriceVO::getCount));

                    if (optional.isPresent()) {
                        return optional.get().getPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                    } else {
                        return BigDecimal.ZERO;
                    }
                } else if (goodsInfo.getPriceType() == GoodsPriceType.CUSTOMER.toValue()) {
                    // 按级别设价，获取级别价
                    return goodsInfo.getSalePrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                } else {
                    if ( Objects.nonNull(customer) && Objects.equals(customer.getEnterpriseStatusXyy(), EnterpriseCheckState.CHECKED)
                            && null != goodsInfo.getVipPrice() && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                        return goodsInfo.getVipPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                    }else {
                        return goodsInfo.getMarketPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                    }
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            // 计算商品总数
            Long totalCount = goodsInfoList.stream().map(goodsInfo -> goodsInfo.getBuyCount()).reduce(0L, Long::sum);

            // 满金额
            if (marketingResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {
                // 计算达到营销级别的金额
                BigDecimal levelAmount;

                // 根据不用的营销类型，计算满足条件的营销等级里最大的一个，如果不满足营销等级里任意一个，则默认取最低等级
                switch (marketingResponse.getMarketingType()) {
                    case REDUCTION:
                        Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
                                marketingResponse.getFullReductionLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
                                        .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount));

                        // 满足条件的最大的等级
                        if (fullReductionLevelOptional.isPresent()) {
                            levelAmount = fullReductionLevelOptional.get().getFullAmount();
                            discount = fullReductionLevelOptional.get().getReduction();
                            response.setFullReductionLevel(fullReductionLevelOptional.get());
                        } else {
                            // 没有满足条件，默认取最低等级
                            levelAmount = marketingResponse.getFullReductionLevelList().get(0).getFullAmount();
                            discount = marketingResponse.getFullReductionLevelList().get(0).getReduction();
                            response.setFullReductionLevel(marketingResponse.getFullReductionLevelList().get(0));
                        }
                        // 是否满足最大等级
                        MarketingFullReductionLevelVO maxLevel = marketingResponse.getFullReductionLevelList().stream()
                                .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount)).get();
                        boolean isMax = maxLevel.getFullAmount().compareTo(totalAmount) < 0;
                        // 满金额减最大等级且可叠加计算折扣
                        if(MarketingSubType.REDUCTION_FULL_AMOUNT.equals(marketingResponse.getSubType()) &&
                                BoolFlag.YES.equals(marketingResponse.getIsOverlap()) && isMax){
                            discount = totalAmount.divide(levelAmount, 0, BigDecimal.ROUND_DOWN).multiply(discount);
                        }

                        break;
                    case DISCOUNT:
                        Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
                                marketingResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
                                        .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullAmount));

                        // 满足条件的最大的等级
                        if (fullDiscountLevelOptional.isPresent()) {
                            levelAmount = fullDiscountLevelOptional.get().getFullAmount();
                            discount = fullDiscountLevelOptional.get().getDiscount();
                            response.setFullDiscountLevel(fullDiscountLevelOptional.get());
                        } else {
                            // 没有满足条件，默认取最低等级
                            levelAmount = marketingResponse.getFullDiscountLevelList().get(0).getFullAmount();
                            discount = marketingResponse.getFullDiscountLevelList().get(0).getDiscount();
                            response.setFullDiscountLevel(marketingResponse.getFullDiscountLevelList().get(0));
                        }

                        break;
                    case GIFT:
                        List<MarketingFullGiftLevelVO> levels =
                                marketingResponse.getFullGiftLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0).collect(Collectors.toList());

                        Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
                                levels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount));

                        // 满足条件的最大的等级
                        if (fullGiftLevelOptional.isPresent()) {
                            levelAmount = fullGiftLevelOptional.get().getFullAmount();
                            response.setFullGiftLevel(fullGiftLevelOptional.get());
                        } else {
                            // 没有满足条件，默认取最低等级
                            levelAmount = marketingResponse.getFullGiftLevelList().get(0).getFullAmount();
                            response.setFullGiftLevel(marketingResponse.getFullGiftLevelList().get(0));
                        }
                        // 最大赠级别
                        MarketingFullGiftLevelVO maxLevelVO = marketingResponse.getFullGiftLevelList().stream()
                                .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount)).get();
                        // 是否满足最大等级
                        boolean isMaxLevel = maxLevelVO.getFullAmount().compareTo(totalAmount) < 0;
                        // 满金额赠可叠加且是最大级别计算赠品数量
                        if (MarketingSubType.GIFT_FULL_AMOUNT.equals(marketingResponse.getSubType()) &&
                                BoolFlag.YES.equals(marketingResponse.getIsOverlap()) && isMaxLevel) {
                            // 如果是赠品数量是叠加倍数，赠品营销只取最大级别,计算赠品数量
                            Long multiple = totalAmount.divide(maxLevelVO.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                            if (multiple.compareTo(1L) > 0) {
                                for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                    // 计算赠品数量
                                    detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                }
                                List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                levelVOS.add(maxLevelVO);
                                marketingResponse.setFullGiftLevelList(levelVOS);
                            }
                        }
                        response.setFullGiftLevelList(marketingResponse.getFullGiftLevelList());

                        break;
                    default:
                        levelAmount = BigDecimal.ZERO;
                }

                // 计算达到营销级别的差额
                lackAmount = levelAmount.compareTo(totalAmount) <= 0 ? BigDecimal.ZERO :
                        levelAmount.subtract(totalAmount);

                response.setLack(lackAmount);

            } else if (marketingResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                // 满数量
                // 计算达到营销级别的数量
                Long levelCount;

                switch (marketingResponse.getMarketingType()) {
                    case REDUCTION:
                        Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
                                marketingResponse.getFullReductionLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
                                        .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount));

                        // 满足条件的最大的等级
                        if (fullReductionLevelOptional.isPresent()) {
                            levelCount = fullReductionLevelOptional.get().getFullCount();
                            discount = fullReductionLevelOptional.get().getReduction();
                            response.setFullReductionLevel(fullReductionLevelOptional.get());
                        } else {
                            // 没有满足条件，默认取最低等级
                            levelCount = marketingResponse.getFullReductionLevelList().get(0).getFullCount();
                            discount = marketingResponse.getFullReductionLevelList().get(0).getReduction();
                            response.setFullReductionLevel(marketingResponse.getFullReductionLevelList().get(0));
                        }
                        // 是否满足最大等级
                        MarketingFullReductionLevelVO maxLevel = marketingResponse.getFullReductionLevelList().stream()
                                .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                        boolean isMax = maxLevel.getFullCount().compareTo(totalCount) < 0;
                        // 满数量减可叠加且满足最大级计算折扣
                        if(MarketingSubType.REDUCTION_FULL_COUNT.equals(marketingResponse.getSubType()) &&
                                BoolFlag.YES.equals(marketingResponse.getIsOverlap()) && isMax){
                            discount = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(levelCount), 0, BigDecimal.ROUND_DOWN).multiply(discount);
                        }

                        break;
                    case DISCOUNT:
                        Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
                                marketingResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
                                        .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount));

                        // 满足条件的最大的等级
                        if (fullDiscountLevelOptional.isPresent()) {
                            levelCount = fullDiscountLevelOptional.get().getFullCount();
                            discount = fullDiscountLevelOptional.get().getDiscount();
                            response.setFullDiscountLevel(fullDiscountLevelOptional.get());
                        } else {
                            // 没有满足条件，默认取最低等级
                            levelCount = marketingResponse.getFullDiscountLevelList().get(0).getFullCount();
                            discount = marketingResponse.getFullDiscountLevelList().get(0).getDiscount();
                            response.setFullDiscountLevel(marketingResponse.getFullDiscountLevelList().get(0));
                        }

                        break;
                    case GIFT:
                        List<MarketingFullGiftLevelVO> giftLevels =
                                marketingResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());

                        Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
                                giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));

                        // 满足条件的最大的等级
                        if (fullGiftLevelOptional.isPresent()) {
                            levelCount = fullGiftLevelOptional.get().getFullCount();
                            response.setFullGiftLevel(fullGiftLevelOptional.get());
                        } else {
                            // 没有满足条件，默认取最低等级
                            levelCount = marketingResponse.getFullGiftLevelList().get(0).getFullCount();
                            response.setFullGiftLevel(marketingResponse.getFullGiftLevelList().get(0));
                        }
                        // 最大赠级别
                        MarketingFullGiftLevelVO maxLevelVO = marketingResponse.getFullGiftLevelList().stream()
                                .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount)).get();
                        // 是否满足最大等级
                        boolean isMaxLevel = maxLevelVO.getFullCount().compareTo(totalCount) < 0;
                        // 满数量赠可叠加且满足最大级优惠计算赠品数量
                        if (MarketingSubType.GIFT_FULL_COUNT.equals(marketingResponse.getSubType()) &&
                                BoolFlag.YES.equals(marketingResponse.getIsOverlap()) && isMaxLevel) {
                            // 如果是赠品数量是叠加倍数，赠品营销只取最大级别, 计算赠品数量
                            Long multiple = BigDecimal.valueOf(totalCount)
                                    .divide(BigDecimal.valueOf(maxLevelVO.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                            if (multiple.compareTo(1L) > 0) {
                                for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                    // 计算赠品数量
                                    detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                }
                                List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                levelVOS.add(maxLevelVO);
                                marketingResponse.setFullGiftLevelList(levelVOS);
                            }
                        }

                        response.setFullGiftLevelList(marketingResponse.getFullGiftLevelList());

                        break;
                    default:
                        levelCount = 0L;
                }

                // 计算达到营销级别缺少的数量
                lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                        levelCount.longValue() - totalCount.longValue();

                response.setLack(BigDecimal.valueOf(lackCount));
            }else if ( marketingResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER) {
                // 订单满赠
                // 计算达到营销级别的数量
                Long levelCount;

                switch (marketingResponse.getMarketingType()) {
                    case GIFT:
                        List<MarketingFullGiftLevelVO> giftLevels =
                                marketingResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());

                        Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
                                giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));
                        // 满足条件的最大的等级
                        if (fullGiftLevelOptional.isPresent()) {
                            levelCount = fullGiftLevelOptional.get().getFullCount();
                            response.setFullGiftLevel(fullGiftLevelOptional.get());
                        } else {
                            // 没有满足条件，默认取最低等级
                            levelCount = marketingResponse.getFullGiftLevelList().get(0).getFullCount();
                            response.setFullGiftLevel(marketingResponse.getFullGiftLevelList().get(0));
                        }
                        response.setFullGiftLevelList(marketingResponse.getFullGiftLevelList());
                        break;
                    default:
                        levelCount = 0L;
                }
                // 计算达到营销级别缺少的数量
                lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                        levelCount.longValue() - totalCount.longValue();
                response.setLack(BigDecimal.valueOf(lackCount));
            } else {
                throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
            }

            response.setGoodsInfoList(goodsInfoList);
            response.setTotalCount(totalCount);
            response.setTotalAmount(totalAmount);
            response.setDiscount(discount);
//            }
        }

        // 如果有赠品，则查询赠品商品的详细信息
        if (response.getFullGiftLevelList() != null && !response.getFullGiftLevelList().isEmpty()) {
            List<String> skuIds =
                    response.getFullGiftLevelList().stream().flatMap(marketingFullGiftLevel -> marketingFullGiftLevel.getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId)).distinct().collect(Collectors.toList());
            GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                    .goodsInfoIds(skuIds)
                    .isHavSpecText(Constants.yes)
                    .wareId(wareId)
                    .build();
            response.setGiftGoodsInfoResponse(KsBeanUtil.convert(goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext(), PurchaseGoodsViewVO.class));
        }

        response.setStoreId(marketingResponse.getStoreId());
        response.setMarketingId(marketingResponse.getMarketingId());
        response.setMarketingType(marketingResponse.getMarketingType());
        response.setSubType(marketingResponse.getSubType());
        response.setIsOverlap(marketingResponse.getIsOverlap());


        return response;
    }

    /**
     * 确定是否为，满订单赠
     * @param goodsInfoIds
     * @return
     */
    private boolean isGifFullOrder(List<String> goodsInfoIds){
        if(CollectionUtils.isNotEmpty(goodsInfoIds)){
            if(Constant.FULL_GIT_ORDER_GOODS.equals(goodsInfoIds.get(0))){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取商品营销信息
     *
     * @return
     */
    public PurchaseGetGoodsMarketingResponse getGoodsMarketing(List<GoodsInfoVO> goodsInfos, CustomerVO customer, Long wareId) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase getGoodsMarketing start");
        try {
            Map<String, List<MarketingViewVO>> resMap = new LinkedHashMap<>();
            PurchaseGetGoodsMarketingResponse marketingResponse = new PurchaseGetGoodsMarketingResponse();

            if (goodsInfos.isEmpty()||Objects.isNull(customer)) {
                marketingResponse.setMap(resMap);
                marketingResponse.setGoodsInfos(goodsInfos);
                return marketingResponse;
            }

            List<GoodsInfoDTO> goodsInfoList = KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class);
            CustomerDTO customerDTO = KsBeanUtil.convert(customer, CustomerDTO.class);

            // 设置级别价
       /* Long currentTime = System.currentTimeMillis();
        goodsInfos = marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
                .goodsInfos(goodsInfoList).customerDTO(customerDTO).build()).getContext().getGoodsInfoVOList();
        System.out.println("!!!!!!!! 设置级别价" + (System.currentTimeMillis() - currentTime));
        goodsInfoList = KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class);*/

            //获取营销
            Map<String, List<MarketingViewVO>> marketingMapCount = marketingPluginQueryProvider.getByGoodsInfoListAndCustomer(
                    MarketingPluginByGoodsInfoListAndCustomerRequest.builder().goodsInfoList(goodsInfoList)
                            .customerDTO(customerDTO).build()).getContext().getMarketingMap();

            sb.append(",marketingPluginQueryProvider.getByGoodsInfoListAndCustomer end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();
            Map<String, List<MarketingViewVO>> marketingMap= new HashMap<>();
            //过滤限购营销
            this.filterMarketing(marketingMap,marketingMapCount,goodsInfoList);
            if (Objects.isNull(marketingMap)) {
                marketingResponse.setMap(resMap);
                marketingResponse.setGoodsInfos(goodsInfos);
                return marketingResponse;
            }
            // 满赠的giftIds
            Set<String> giftIds = new HashSet<>();
            marketingMap.forEach((key, value) -> {
                // 赠品营销
                List<MarketingViewVO> giftViewVOS = value.stream()
                        .filter(marketing -> marketing.getMarketingType() == MarketingType.GIFT)
                        .collect(Collectors.toList());

                if (giftViewVOS.size() > 0) {
                    MarketingViewVO marketingViewVO = giftViewVOS.get(0);
                    List<String> skuIds = marketingViewVO.getFullGiftLevelList().stream()
                            .flatMap(marketingFullGiftLevel ->
                                    marketingFullGiftLevel.getFullGiftDetailList().stream()
                                            .map(MarketingFullGiftDetailVO::getProductId))
                            .distinct().collect(Collectors.toList());
                    giftIds.addAll(skuIds);
                }
            });
            // 赠品信息map
            Map<String, GoodsVO> goodsVOMap = new HashedMap();
            Map<String, GoodsInfoVO> goodsInfoVOMap = new HashedMap();
            if (giftIds.size() > 0) {
                GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                        .goodsInfoIds(new ArrayList<>(giftIds))
                        .isHavSpecText(Constants.yes)
                        .wareId(wareId)
                        .build();
                GoodsInfoViewByIdsResponse giftIdsResponse =
                        goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();

                sb.append(",goodsInfoQueryProvider.listViewByIds end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                goodsVOMap = giftIdsResponse.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
                goodsInfoVOMap = giftIdsResponse.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
            }

            sb.append(",marketingMap.foreach start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            for (Map.Entry<String, List<MarketingViewVO>> set : marketingMap.entrySet()) {
                String key = set.getKey();
                List<MarketingViewVO> value = set.getValue();
                // 商品营销排序 减>折>赠
                List<MarketingViewVO> marketingResponses = value.stream()
                        .sorted(Comparator.comparing(MarketingViewVO::getMarketingType))
                        .distinct()
                        .collect(Collectors.toList());

                if (!marketingResponses.isEmpty()) {
                    // 获取该商品的满赠营销
                    Optional<MarketingViewVO> fullGiftMarketingOptional = marketingResponses.stream()
                            .filter(marketing -> marketing.getMarketingType() == MarketingType.GIFT).findFirst();

                    if(fullGiftMarketingOptional.isPresent()) {
                        MarketingViewVO fullGiftMarketingResponse = fullGiftMarketingOptional.get();
                        List<String> skuIds = fullGiftMarketingResponse.getFullGiftLevelList().stream()
                                .flatMap(marketingFullGiftLevel ->
                                        marketingFullGiftLevel.getFullGiftDetailList().stream()
                                                .map(MarketingFullGiftDetailVO::getProductId))
                                .distinct()
                                .collect(Collectors.toList());
//                        GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
//                                .goodsInfoIds(skuIds)
//                                .isHavSpecText(Constants.yes)
//                                .wareId(wareId)
//                                .build();
//                        GoodsInfoViewByIdsResponse idsResponse =
//                                goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
                        List<GoodsVO> giftVOS = new ArrayList<>();
                        List<GoodsInfoVO> giftInfoVOS = new ArrayList<>();
                        for (String skuId : skuIds) {
                            if (Objects.nonNull(goodsInfoVOMap.get(skuId))) {
                                giftInfoVOS.add(goodsInfoVOMap.get(skuId));
                                giftVOS.add(goodsVOMap.get(goodsInfoVOMap.get(skuId).getGoodsId()));
                            }
                        }
                        fullGiftMarketingResponse.setGoodsList(GoodsInfoResponseVO.builder()
                                .goodsInfos(giftInfoVOS).goodses(giftVOS).build());
                    }

                    resMap.put(key, marketingResponses);
                }
            }
            marketingResponse.setMap(resMap);
            marketingResponse.setGoodsInfos(goodsInfos);

            sb.append(",marketingMap.foreach end time=");
            sb.append(System.currentTimeMillis()-sTm);

            return marketingResponse;
        }finally {
            log.info(sb.toString());
        }

    }

    public void filterMarketing(Map<String, List<MarketingViewVO>> marketingMap,Map<String, List<MarketingViewVO>> marketingMapCount,List<GoodsInfoDTO> goodsInfoList){
        //查询所有商品的囤货信息
//        List<PilePurchaseAction> purchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(goodsInfoList.stream().map(GoodsInfoDTO::getGoodsInfoId)
//                .distinct().collect(Collectors.toList()));
        //过滤掉已经买完限购商品
        marketingMapCount.forEach((sku,marketing) -> {
            List<MarketingViewVO> marketingViewVOS = new ArrayList<>();
            marketing.stream().forEach(marketingViewVO -> {
                List<MarketingScopeVO> marketingScopeList = marketingViewVO.getMarketingScopeList();
                for (MarketingScopeVO marketingScopeVO : marketingScopeList) {
                    Long purchaseNum = marketingScopeVO.getPurchaseNum();
                    if(Objects.nonNull(purchaseNum)){
                        if(sku.equals(marketingScopeVO.getScopeId())){
                            //查询出此商品的囤货总量
//                            Long sum = purchaseActions.stream().filter(pilePurchaseAction -> pilePurchaseAction.getGoodsInfoId().equals(sku)).collect(Collectors.toList()).stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
                            //商品购买量
                            Long buyCount = goodsInfoList.stream().filter(goodsInfoDTO -> goodsInfoDTO.getGoodsInfoId().equals(sku)).collect(Collectors.toList()).stream().findFirst().get().getBuyCount();
//                            Long count = sum + buyCount;
                            //当购买量+已购买量 > 限购数量则取消该条营销信息
                            if(buyCount > purchaseNum || purchaseNum <= 0){
                                marketingViewVOS.add(marketingViewVO);
                            }
                        }
                    }
                }
            });
            if(CollectionUtils.isNotEmpty(marketingViewVOS)){
                marketing.removeAll(marketingViewVOS);
            }
            if(CollectionUtils.isNotEmpty(marketing)){
                marketingMap.put(sku,marketing);
            }
        });
    }

    /**
     * 获取采购营销信息及同步商品营销
     * @param goodsInfoIdList
     * @param goodsInfos
     * @param customer
     * @param wareId
     * @return
     */
    public PurchaseMarketingResponse getPurchasesMarketing(List<String> goodsInfoIdList, List<GoodsInfoVO> goodsInfos, CustomerVO customer, Long wareId) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase getPurchasesMarketing start");

        try {
            //获取商品营销信息
            PurchaseGetGoodsMarketingResponse goodsMarketing = this.getGoodsMarketing(goodsInfos, customer, wareId);

            sb.append(",getGoodsMarketing end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            PurchaseMarketingResponse marketingResponse = KsBeanUtil.convert(goodsMarketing, PurchaseMarketingResponse.class);
            // 同步商品使用的营销
            List<GoodsMarketingVO> goodsMarketingVOS = this.syncGoodsMarketings(goodsMarketing.getMap(), customer.getCustomerId());

            sb.append(",syncGoodsMarketings end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            marketingResponse.setGoodsMarketings(goodsMarketingVOS);
            // 获取店铺营销信息
            if (CollectionUtils.isNotEmpty(goodsMarketingVOS) && CollectionUtils.isNotEmpty(goodsInfoIdList)) {
            /*List<GoodsInfoVO> reqGoodsInfo = goodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder()
                    .goodsInfoIds(goodsInfoIdList)
                    .wareId(wareId)
                    .build()).getContext().getGoodsInfos();*/
                List<GoodsInfoVO> reqGoodsInfo = goodsInfos.stream().filter(param -> goodsInfoIdList.contains(param.getGoodsInfoId())).collect(Collectors.toList());
                List<GoodsInfoVO> reqGoodsInfosFilter = reqGoodsInfo.stream().filter(param -> param.getGoodsInfoType() == 0).collect(Collectors.toList());
                List<String> reqGoodsInfoIds = reqGoodsInfosFilter.stream().map(GoodsInfoVO::getGoodsInfoId)
                        .collect(Collectors.toList());
                Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketing = this.getStoreMarketingBase(
                        KsBeanUtil.convertList(goodsMarketingVOS, GoodsMarketingDTO.class), customer, new PurchaseFrontRequest(), reqGoodsInfoIds, wareId, reqGoodsInfosFilter);

                sb.append(",getStoreMarketingBase end time=");
                sb.append(System.currentTimeMillis()-sTm);

                HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                storeMarketing.forEach((k, v) -> {
                    map.put(k, KsBeanUtil.convertList(v, PurchaseMarketingCalcVO.class));
                });
                marketingResponse.setStoreMarketingMap(map);
            }
            return marketingResponse;
        }finally {
            log.info(sb.toString());
        }

    }


    /**
     * 验证营销是否可用
     *
     * @param marketingResponse
     * @param levelMap
     * @return
     */
    private boolean validMarketing(MarketingForEndVO marketingResponse, Map<Long, CommonLevelVO> levelMap) {
        boolean valid = true;
        // 判断会员等级是否满足要求
        //校验营销活动
        if (marketingResponse.getIsPause() == BoolFlag.YES || marketingResponse.getDelFlag() == DeleteFlag.YES || marketingResponse.getBeginTime().isAfter(LocalDateTime.now())
                || marketingResponse.getEndTime().isBefore(LocalDateTime.now())) {
            valid = false;
        }

        //校验用户级别
        CommonLevelVO level = levelMap.get(marketingResponse.getStoreId());
        switch (marketingResponse.getMarketingJoinLevel()) {
            case ALL_CUSTOMER:
                break;
            case ALL_LEVEL:
                if (level == null) {
                    valid = false;
                }
                break;
            case LEVEL_LIST:
                if (level == null || !marketingResponse.getJoinLevelList().contains(level.getLevelId())) {
                    valid = false;
                }
                break;
            default:
                break;
        }

        return valid;
    }

    /**
     * 获取用户在店铺里的等级
     *
     * @return
     */
    private Map<Long, CommonLevelVO> getLevelMap(List<GoodsInfoVO> goodsInfos, CustomerVO customer) {
        return marketingPluginQueryProvider.getCustomerLevelsByGoodsInfoListAndCustomer(
                MarketingPluginGetCustomerLevelsRequest.builder()
                        .goodsInfoList(KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class))
                        .customer(KsBeanUtil.convert(customer, CustomerDTO.class)).build()
        ).getContext().getCommonLevelVOMap();
    }

    /**
     * 获取用户在店铺里的等级
     *
     * @return
     */
    private Map<Long, CommonLevelVO> getLevelMapByStoreIds(List<Long> storeIds, CustomerVO customer) {
        return marketingPluginQueryProvider.getCustomerLevelsByStoreIds(
                MarketingPluginGetCustomerLevelsByStoreIdsRequest.builder().storeIds(storeIds)
                        .customer(KsBeanUtil.convert(customer, CustomerDTO.class)).build()).getContext()
                .getCommonLevelVOMap();
    }

    /**
     * 获取采购单商品选择的营销
     *
     * @param customerId
     * @return
     */
    public List<GoodsMarketingVO> queryGoodsMarketingList(String customerId) {
        GoodsMarketingListByCustomerIdRequest request = new GoodsMarketingListByCustomerIdRequest();
        request.setCustomerId(customerId);
        List<GoodsMarketingVO> voList = goodsMarketingQueryProvider.listByCustomerId(request).getContext()
                .getGoodsMarketings();
        return Objects.isNull(voList) ? Collections.emptyList() : voList;
    }

    /**
     * [公共方法]获取店铺营销信息
     * 同时处理未登录 / 已登录时的逻辑
     *
     * @param goodsMarketings 商品营销信息
     * @param customer        登录人信息
     * @param frontReq        前端传入的采购单信息
     * @param goodsInfoIdList 勾选的skuIdList
     * @return
     */
    public Map<Long, List<PurchaseMarketingCalcResponse>> getStoreMarketingBase(List<GoodsMarketingDTO> goodsMarketings,
                                                                                CustomerVO customer,
                                                                                PurchaseFrontRequest frontReq,
                                                                                List<String> goodsInfoIdList,
                                                                                Long wareId, List<GoodsInfoVO> goodsInfoVOS) {
        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase getStoreMarketingBase start");

        try {
            goodsInfoIdList.add("all");
            Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketingMap = new HashMap<>();

            if (CollectionUtils.isEmpty(goodsMarketings)) {
                return storeMarketingMap;
            }

            // 参加同种营销的商品列表
            Map<Long, List<String>> marketingGoodsesMap = new HashMap<>();

//        IteratorUtils.groupBy(goodsMarketings, GoodsMarketingDTO::getMarketingId).entrySet().stream().forEach(set -> {
//            marketingGoodsesMap.put(set.getKey(), set.getValue().stream()
//                    .filter(goodsMarketing -> goodsInfoIdList != null
//                            && goodsInfoIdList.contains(goodsMarketing.getGoodsInfoId()))
//                    .map(GoodsMarketingDTO::getGoodsInfoId).collect(Collectors.toList()));
//        });
            Map<Long, List<GoodsMarketingDTO>> longListMap = IteratorUtils.groupBy(goodsMarketings, GoodsMarketingDTO::getMarketingId);
            for (Map.Entry<Long, List<GoodsMarketingDTO>> set : longListMap.entrySet()) {
                List<String> filterGoodsInfoIds = new ArrayList<>();
                for (GoodsMarketingDTO goodsMarketing : set.getValue()) {
                    if (goodsInfoIdList != null && goodsInfoIdList.contains(goodsMarketing.getGoodsInfoId())) {
                        filterGoodsInfoIds.add(goodsMarketing.getGoodsInfoId());
                    }
                }
                marketingGoodsesMap.put(set.getKey(), filterGoodsInfoIds);
            }
            // 查询所有营销信息
            List<Long> marketingIds = new ArrayList<>(marketingGoodsesMap.keySet());
            MarketingQueryByIdsRequest marketingQueryByIdsRequest = new MarketingQueryByIdsRequest();
            marketingQueryByIdsRequest.setMarketingIds(marketingIds);

            sb.append(",marketingQueryProvider.getByIdsForCustomer start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            Map<Long, MarketingForEndVO> marketingMap = marketingQueryProvider.getByIdsForCustomer(marketingQueryByIdsRequest).getContext().getMarketingForEndVOS().stream()
                    .collect(Collectors.toMap(MarketingVO::getMarketingId, Function.identity()));
            sb.append(",marketingQueryProvider.getByIdsForCustomer end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();
//            this.filterMarketingForEndVO(marketingMap,goodsInfoVOS);
            //填充SKU信息============================================
//            List<String> skuIds = goodsInfoIdList;
//            if (isGifFullOrder(goodsInfoIdList)) {
//                skuIds = Arrays.asList();
//            }
//            PurchaseRequest request = PurchaseRequest.builder()
//                    .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
//                    .goodsInfoIds(skuIds)
//                    .build();
//            List<Purchase> follows;
//            Sort sort = request.getSort();
//            if (Objects.nonNull(sort)) {
//                follows = purchaseRepository.findAll(request.getWhereCriteria(), sort);
//            } else {
//                follows = purchaseRepository.findAll(request.getWhereCriteria());
//            }
//            List<GoodsInfoVO> checkGoodsInfoVOS = goodsInfoVOS;
//            //填充SKU的购买数
//            this.fillBuyCount(checkGoodsInfoVOS, follows);
            //查询所有的购买的sku的囤货信息
//            List<PilePurchaseAction> pilePurchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(checkGoodsInfoVOS.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()));
//            List<Long> removeIds = new ArrayList<>();
//            if(CollectionUtils.isNotEmpty(pilePurchaseActions)){
//                //过滤限购营销
//                marketingMap.forEach((id,marketingForEndVO) ->{
//                    List<MarketingScopeVO> marketingScopeList = marketingForEndVO.getMarketingScopeList();
//                    for (MarketingScopeVO marketingScopeVO : marketingScopeList) {
//                        Long purchaseNum = marketingScopeVO.getPurchaseNum();
//                        //当限购数量不等于空的时候才去判断是否超出限购范围
//                        if(Objects.nonNull(purchaseNum)){
//                            String sku = marketingScopeVO.getScopeId();
//                            List<PilePurchaseAction> collect = pilePurchaseActions.stream().filter(pilePurchaseAction -> pilePurchaseAction.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
//                            if(CollectionUtils.isNotEmpty(collect)){
//                                Long sum = collect.stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
//                                List<GoodsInfoVO> collect1 = checkGoodsInfoVOS.stream().filter(goodsInfoVO -> goodsInfoVO.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
//                                if(CollectionUtils.isNotEmpty(collect1)){
//                                    sum = sum + collect1.stream().mapToLong(GoodsInfoVO::getBuyCount).sum();
//                                }
//                                //判断购买数量+已囤货数量是否大于限购数量
//                                if(sum > purchaseNum){
//                                    if(!removeIds.contains(id)){
//                                        removeIds.add(id);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//            if(CollectionUtils.isNotEmpty(removeIds)){
//                for (Long id : removeIds) {
//                    marketingMap.remove(id);
//                }
//            }
            // 根据同种营销的商品列表，计算营销
            List<String> goodsInfoIds = new ArrayList<>();
            for(List<String> mapGoodsInfoIds : marketingGoodsesMap.values()) {
                for (String mapInfoId : mapGoodsInfoIds) {
                    goodsInfoIds.add(mapInfoId);
                }
            }
            Map<Long, PurchaseMarketingCalcResponse> responseMap = new HashedMap();
            if (customer != null) {
                if (Objects.nonNull(goodsInfoVOS) && goodsInfoVOS.size() > 0) {
                    responseMap = this.simpCalcMarketingByMarketingId(marketingGoodsesMap, customer, null,
                            goodsInfoIds, true, goodsInfoIdList, marketingMap, goodsInfoVOS, goodsMarketings);

                    sb.append(",simpCalcMarketingByMarketingId end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    responseMap = this.calcMarketingByMarketingIdBaseList(marketingGoodsesMap, customer, null,
                            goodsInfoIds, true, goodsInfoIdList, wareId, marketingMap);

                    sb.append(",customer nonNull calcMarketingByMarketingIdBaseList end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                }


            } else {
                responseMap = this.calcMarketingByMarketingIdBaseList(marketingGoodsesMap, customer, frontReq,
                        goodsInfoIds, true, goodsInfoIdList, wareId, marketingMap);

                sb.append(",customer null calcMarketingByMarketingIdBaseList end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();
            }
            for(Map.Entry<Long, List<String>> set : marketingGoodsesMap.entrySet()) {
                if(CollectionUtils.isNotEmpty(set.getValue())) {
//                if (customer != null) {
//                    response = this.calcMarketingByMarketingIdBase(Long.valueOf(set.getKey()), customer, null,
//                            set.getValue(), true,goodsInfoIdList, wareId );
//                } else {
//                    response = this.calcMarketingByMarketingIdBase(Long.valueOf(set.getKey()), customer, frontReq,
//                            set.getValue(), true,goodsInfoIdList, wareId);
//                }
                    PurchaseMarketingCalcResponse response = responseMap.get(set.getKey());

                    if (storeMarketingMap.get(response.getStoreId()) == null) {
                        List<PurchaseMarketingCalcResponse> calcResponses = new ArrayList<>();
                        calcResponses.add(response);

                        storeMarketingMap.put(response.getStoreId(), calcResponses);
                    } else {
                        storeMarketingMap.get(response.getStoreId()).add(response);
                    }
                }
            }

            // 营销按减>折>赠排序
            for (Map.Entry<Long, List<PurchaseMarketingCalcResponse>> set : storeMarketingMap.entrySet()) {
                set.getValue().sort(Comparator.comparing(PurchaseMarketingCalcResponse::getMarketingType));
            }

            sb.append(",getStoreMarketingBase end time=");
            sb.append(System.currentTimeMillis()-sTm);

            return storeMarketingMap;
        }finally {
            log.info(sb.toString());
        }

    }


    /**
     * [公共方法]获取店铺营销信息
     * 同时处理未登录 / 已登录时的逻辑
     *
     * @param goodsMarketings 商品营销信息
     * @param customer        登录人信息
     * @param frontReq        前端传入的采购单信息
     * @param goodsInfoIdList 勾选的skuIdList
     * @return
     */
    public Map<Long, List<PurchaseMarketingCalcResponse>> getStoreMarketingBaseCopy(List<GoodsMarketingDTO> goodsMarketings,
                                                                                    CustomerVO customer,
                                                                                    PurchaseFrontRequest frontReq,
                                                                                    List<String> goodsInfoIdList,
                                                                                    Long wareId, List<GoodsInfoVO> goodsInfoVOS, List<GoodsInfoVO> goodsInfoVOList, PurchaseStoreMarketingRequest request) {
        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase getStoreMarketingBase start");

        try {
            goodsInfoIdList.add("all");
            Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketingMap = new HashMap<>();

            if (CollectionUtils.isEmpty(goodsMarketings)) {
                return storeMarketingMap;
            }

            // 参加同种营销的商品列表
            Map<Long, List<String>> marketingGoodsesMap = new HashMap<>();

//        IteratorUtils.groupBy(goodsMarketings, GoodsMarketingDTO::getMarketingId).entrySet().stream().forEach(set -> {
//            marketingGoodsesMap.put(set.getKey(), set.getValue().stream()
//                    .filter(goodsMarketing -> goodsInfoIdList != null
//                            && goodsInfoIdList.contains(goodsMarketing.getGoodsInfoId()))
//                    .map(GoodsMarketingDTO::getGoodsInfoId).collect(Collectors.toList()));
//        });
            List<String> skus = new ArrayList<>();
            Map<Long, List<GoodsMarketingDTO>> longListMap = IteratorUtils.groupBy(goodsMarketings, GoodsMarketingDTO::getMarketingId);
            for (Map.Entry<Long, List<GoodsMarketingDTO>> set : longListMap.entrySet()) {
                List<String> filterGoodsInfoIds = new ArrayList<>();
                for (GoodsMarketingDTO goodsMarketing : set.getValue()) {
                    if (goodsInfoIdList != null && goodsInfoIdList.contains(goodsMarketing.getGoodsInfoId())) {
                        filterGoodsInfoIds.add(goodsMarketing.getGoodsInfoId());
                    }
                }
                //根据营销id查询营销活动信息
                Long marketingId = set.getKey();
                MarketingGetByIdRequest req = new MarketingGetByIdRequest();
                req.setMarketingId(marketingId);
                MarketingVO marketingVO = marketingQueryProvider.getById(req).getContext().getMarketingVO();
                log.info("==================== 营销活动信息：{}==========================",marketingVO);
                List<String> invalidGoodsInfoId = this.invalidGoodsInfoId(marketingVO, goodsInfoVOList);
                if(CollectionUtils.isNotEmpty(invalidGoodsInfoId)){
                    skus.addAll(invalidGoodsInfoId);
                    filterGoodsInfoIds.removeAll(invalidGoodsInfoId);
                }
                if(CollectionUtils.isNotEmpty(filterGoodsInfoIds)){
                    marketingGoodsesMap.put(set.getKey(), filterGoodsInfoIds);
                }
            }
            log.info("============================ 失效的 sku 信息：{}",skus);
            log.info("============================ 删除前的 sku 信息：{}",new ArrayList(request.getGoodsMarketingMap().keySet()));
            if(CollectionUtils.isNotEmpty(skus)){
                for (String s : skus) {
                    request.getGoodsMarketingMap().remove(s);
                }
            }
            if(marketingGoodsesMap.isEmpty() || marketingGoodsesMap.size() <= 0){
                return storeMarketingMap;
            }
            // 查询所有营销信息
            List<Long> marketingIds = new ArrayList<>(marketingGoodsesMap.keySet());
            MarketingQueryByIdsRequest marketingQueryByIdsRequest = new MarketingQueryByIdsRequest();
            marketingQueryByIdsRequest.setMarketingIds(marketingIds);

            sb.append(",marketingQueryProvider.getByIdsForCustomer start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            Map<Long, MarketingForEndVO> marketingMap = marketingQueryProvider.getByIdsForCustomer(marketingQueryByIdsRequest).getContext().getMarketingForEndVOS().stream()
                    .collect(Collectors.toMap(MarketingVO::getMarketingId, Function.identity()));
            sb.append(",marketingQueryProvider.getByIdsForCustomer end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();
            log.info("============================= 删除前的营销信息：{}",marketingMap);
            //得到失效商品的skus
//            List<String> skus = this.filterMarketingForEndVO(marketingMap, goodsInfoVOList);
//            List<GoodsMarketingDTO> goodsMarketings2 = new ArrayList<>();
//            goodsMarketings.forEach(goodsMarketingDTO -> {
//                for (String sku : skus) {
//                    if(goodsMarketingDTO.getGoodsInfoId().equals(sku)){
//                        goodsMarketings2.add(goodsMarketingDTO);
//                    }
//                }
//            });
//            if(CollectionUtils.isNotEmpty(goodsMarketings2)){
//                goodsMarketings.removeAll(goodsMarketings2);
//            }
//            List<Long> deleteIds = new ArrayList<>();
//            log.info("================================= 需要删除的sku信息：{}==========================",skus);
//            if(CollectionUtils.isNotEmpty(skus)){
//                skus.forEach(sku ->{
//                    request.getGoodsMarketingMap().remove(sku);
//                    marketingGoodsesMap.forEach((id,marketingGoodses) ->{
//                        if(marketingGoodses.contains(sku)){
//                            if(!deleteIds.contains(id)){
//                                deleteIds.add(id);
//                            }
//                        }
//                    });
//                });
//            }
//            log.info("=========================== 删除前的 marketingGoodsesMap ：{}",marketingGoodsesMap);
//            if(CollectionUtils.isNotEmpty(deleteIds)){
//                for (Long deleteId : deleteIds) {
//                    marketingGoodsesMap.remove(deleteId);
//                }
//            }
//            log.info("=========================== 删除后的 marketingGoodsesMap ：{}",marketingGoodsesMap);
            if(marketingMap.isEmpty() || marketingMap.size() <= 0 || Objects.isNull(marketingMap)){
                log.info("===================== 营销信息为空 ============================");
                return null;
            }
            log.info("============================= 删除后的营销信息：{}",marketingMap);
            // 根据同种营销的商品列表，计算营销
            List<String> goodsInfoIds = new ArrayList<>();
            for(List<String> mapGoodsInfoIds : marketingGoodsesMap.values()) {
                for (String mapInfoId : mapGoodsInfoIds) {
                    goodsInfoIds.add(mapInfoId);
                }
            }
            Map<Long, PurchaseMarketingCalcResponse> responseMap = new HashedMap();
            if (customer != null) {
                if (Objects.nonNull(goodsInfoVOS) && goodsInfoVOS.size() > 0) {
                    responseMap = this.simpCalcMarketingByMarketingId(marketingGoodsesMap, customer, null,
                            goodsInfoIds, true, goodsInfoIdList, marketingMap, goodsInfoVOS, goodsMarketings);

                    sb.append(",simpCalcMarketingByMarketingId end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    responseMap = this.calcMarketingByMarketingIdBaseList(marketingGoodsesMap, customer, null,
                            goodsInfoIds, true, goodsInfoIdList, wareId, marketingMap);

                    sb.append(",customer nonNull calcMarketingByMarketingIdBaseList end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                }


            } else {
                responseMap = this.calcMarketingByMarketingIdBaseList(marketingGoodsesMap, customer, frontReq,
                        goodsInfoIds, true, goodsInfoIdList, wareId, marketingMap);

                sb.append(",customer null calcMarketingByMarketingIdBaseList end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();
            }
            for(Map.Entry<Long, List<String>> set : marketingGoodsesMap.entrySet()) {
                if(CollectionUtils.isNotEmpty(set.getValue())) {
//                if (customer != null) {
//                    response = this.calcMarketingByMarketingIdBase(Long.valueOf(set.getKey()), customer, null,
//                            set.getValue(), true,goodsInfoIdList, wareId );
//                } else {
//                    response = this.calcMarketingByMarketingIdBase(Long.valueOf(set.getKey()), customer, frontReq,
//                            set.getValue(), true,goodsInfoIdList, wareId);
//                }
                    PurchaseMarketingCalcResponse response = responseMap.get(set.getKey());

                    if (storeMarketingMap.get(response.getStoreId()) == null) {
                        List<PurchaseMarketingCalcResponse> calcResponses = new ArrayList<>();
                        calcResponses.add(response);

                        storeMarketingMap.put(response.getStoreId(), calcResponses);
                    } else {
                        storeMarketingMap.get(response.getStoreId()).add(response);
                    }
                }
            }

            // 营销按减>折>赠排序
            for (Map.Entry<Long, List<PurchaseMarketingCalcResponse>> set : storeMarketingMap.entrySet()) {
                set.getValue().sort(Comparator.comparing(PurchaseMarketingCalcResponse::getMarketingType));
            }

            sb.append(",getStoreMarketingBase end time=");
            sb.append(System.currentTimeMillis()-sTm);

            return storeMarketingMap;
        }finally {
            log.info(sb.toString());
        }

    }

    public List<String> invalidGoodsInfoId(MarketingVO marketingVO,List<GoodsInfoVO> goodsInfoVOList){
        List<String> goodsIndoIds = new ArrayList<>();
//        List<PilePurchaseAction> purchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(goodsInfoVOList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()));
                List<MarketingScopeVO> marketingScopeList = marketingVO.getMarketingScopeList();
        marketingScopeList.stream().forEach(marketingScopeVO -> {
            Long purchaseNum = marketingScopeVO.getPurchaseNum();
            if(Objects.nonNull(purchaseNum)){
                String sku = marketingScopeVO.getScopeId();
//                List<PilePurchaseAction> collect = purchaseActions.stream().filter(pilePurchaseAction -> pilePurchaseAction.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
//                long sum = collect.stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
                List<GoodsInfoVO> goodsInfoVOS = goodsInfoVOList.stream().filter(goodsInfoVO -> goodsInfoVO.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
                //购买数量
                long count = goodsInfoVOS.stream().mapToLong(GoodsInfoVO::getBuyCount).sum();
//                long count = sum + buyCount;
                if(count> purchaseNum || purchaseNum<=0){
                    if(!goodsIndoIds.contains(sku)){
                        goodsIndoIds.add(sku);
                    }
                }
            }
        });
        return goodsIndoIds;
    }


    /**
     *
     * @param marketingMapCount
     * @param goodsInfoList
     * @return
     */
    public List<String> filterMarketingForEndVO(Map<Long, MarketingForEndVO> marketingMapCount,List<GoodsInfoVO> goodsInfoList){
        List<String> skus = new ArrayList<>();
        //查询所有商品的囤货信息
        List<PilePurchaseAction> purchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId)
                .distinct().collect(Collectors.toList()));
        List<Long> ids = new ArrayList<>();
        marketingMapCount.forEach((id,marketingMapVO) ->{
            List<MarketingScopeVO> marketingScopeList = marketingMapVO.getMarketingScopeList();
            for (MarketingScopeVO marketingScopeVO : marketingScopeList) {
                Long purchaseNum = marketingScopeVO.getPurchaseNum();
                if(Objects.nonNull(purchaseNum)){
                    String sku = marketingScopeVO.getScopeId();
                    List<PilePurchaseAction> collect = purchaseActions.stream().filter(pilePurchaseAction -> pilePurchaseAction.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
                    long sum = collect.stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
                    List<GoodsInfoVO> goodsInfoVOS = goodsInfoList.stream().filter(goodsInfoVO -> goodsInfoVO.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
                    //购买数量
                    long buyCount = goodsInfoVOS.stream().mapToLong(GoodsInfoVO::getBuyCount).sum();
                    long count = sum + buyCount;
                    if(count> purchaseNum){
                        if(!skus.contains(sku)){
                            skus.add(sku);
                        }
                        if(!ids.contains(id)){
                            ids.add(id);
                        }
                    }
                }
            }
        });
        if(CollectionUtils.isNotEmpty(ids)){
            for (Long id : ids) {
                marketingMapCount.remove(id);
            }
        }
        return skus;
    }

    /**
     * 获取店铺营销信息
     * 已登录场景
     *
     * @param customer
     * @param goodsInfoIdList
     * @return
     */
    public Map<Long, List<PurchaseMarketingCalcResponse>> getStoreMarketing(List<GoodsMarketingDTO> goodsMarketings,
                                                                            CustomerVO customer,
                                                                            List<String> goodsInfoIdList) {
        return this.getStoreMarketingBase(goodsMarketings, customer, null, goodsInfoIdList, null, null);
    }

    /**
     * 获取店铺下，是否有优惠券营销，展示优惠券标签
     *
     * @param goodsInfoList
     * @param customer
     * @return
     */
//    public Map<Long, Boolean> getStoreCouponExist(List<GoodsInfo> goodsInfoList, Customer customer) {
    public Map<Long, Boolean> getStoreCouponExist(List<GoodsInfoVO> goodsInfoList, CustomerVO customer) {
        Map<Long, Boolean> result = new HashMap<>();
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            return result;
        }
        //  List<CouponCache> couponCaches = couponCacheService.listCouponForGoodsList(goodsInfoList, customer);

        CouponCacheListForGoodsGoodInfoListRequest request =
                CouponCacheListForGoodsGoodInfoListRequest.builder().customer(customer)
                        .goodsInfoList(goodsInfoList).build();

        BaseResponse<CouponCacheListForGoodsDetailResponse> cacheresponse =
                couponCacheProvider.listCouponForGoodsList(request);

        List<CouponCacheVO> couponCaches = cacheresponse.getContext().getCouponCacheVOList();

        boolean hasPlatformCoupon =
                couponCaches.stream().filter(item -> item.getCouponInfo().getCouponType().equals(CouponType.GENERAL_VOUCHERS))
                        .collect(Collectors.toList()).size() > 0;
        Map<Long, List<CouponCacheVO>> couponCacheMap =
                couponCaches.stream().collect(Collectors.groupingBy(item -> item.getCouponInfo().getStoreId()));
        goodsInfoList.stream().map(GoodsInfoVO::getStoreId).forEach(item -> {
            if (hasPlatformCoupon) {
                result.put(item, true);
            } else {
                result.put(item, CollectionUtils.isNotEmpty(couponCacheMap.get(item)));
            }
        });
        return result;
    }


    /**
     * 计算采购单金额
     *
     * @param response
     * @param goodsInfoIds
     */
    public PurchaseListResponse calcAmount(PurchaseListResponse response, CustomerVO customerVO, List<String> goodsInfoIds) {
        log.info("计算采购单金额========================》response：{}",response.getGoodsInfos());
        // 没有勾选商品，默认金额为0
        if (goodsInfoIds == null || goodsInfoIds.isEmpty()) {
            return response;
        }

        // 参与营销的商品
        List<String> marketingGoodsInfoIds = response.getGoodsMarketingMap().entrySet().stream().map(set -> set.getKey()).collect(Collectors.toList());
        if (marketingGoodsInfoIds.contains("all")){
            for (Map.Entry<Long, List<PurchaseMarketingCalcVO>> entry : response.getStoreMarketingMap().entrySet()) {
                for (PurchaseMarketingCalcVO inner:entry.getValue()){
                    for (GoodsInfoVO goodsInfo:inner.getGoodsInfoList()){
                        marketingGoodsInfoIds.add(goodsInfo.getGoodsInfoId());
                    }
                }
            }
        }
        log.info("=================================== 参与营销的商品 :{}==========================",marketingGoodsInfoIds);
        List<Long> marketingIds = response.getStoreMarketingMap().values().parallelStream()
                                          .flatMap(storeMarketing -> storeMarketing.parallelStream())
                                          .map(PurchaseMarketingCalcVO::getMarketingId).collect(Collectors.toList());
        List<String> goodsIds = response.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        log.info("PurchaseService.calcAmount marketingIds:{} goodsIds:{}", JSONObject.toJSONString(marketingIds),
                 JSONObject.toJSONString(goodsIds));
        List<Long> marketingStrList = new ArrayList<>();
        if (marketingIds.size() > 0 && goodsIds.size() > 0){
            //查找出有必选商品的活动
            MarketingQueryByIdsRequest request = new MarketingQueryByIdsRequest();
            request.setMarketingIds(marketingIds);
            List<MarketingVO> marketingVOList = marketingQueryProvider.getChooseGoodsMarketing(request).getContext().getMarketingVOList();
            List<Long> list = marketingVOList.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList());
            marketingStrList.addAll(list);
            if (marketingVOList.size() > 0){
                //可以参加活动的有必选商品的活动数据
                List<Long> chooseList = new ArrayList<>();
                //筛选条件
                marketingVOList.stream().forEach(marketingVO -> {
                    //获取活动中商品
                    for (MarketingScopeVO scope :marketingVO.getMarketingScopeList()) {
                        //获取购物车中的商品
                        for (String goodId :goodsIds) {
                            if (goodId.equals(scope.getScopeId()) && scope.getWhetherChoice() == BoolFlag.YES){
                                //删除满足有必选商品的活动
                                if (!chooseList.contains(marketingVO.getMarketingId())){
                                    chooseList.add(marketingVO.getMarketingId());
                                    marketingStrList.remove(marketingVO.getMarketingId());
                                }
                            }
                        }
                    }
                });
            }
        }
        log.info("PurchaseService.calcAmount no Participate in mandatory activities marketingStrList:{}",JSONObject.toJSONString(marketingStrList));




        BigDecimal marketingGoodsTotalPrice = response.getStoreMarketingMap().values().parallelStream()
                .flatMap(storeMarketings -> storeMarketings.parallelStream())
                .map(PurchaseMarketingCalcVO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // 勾选的商品中没有参与营销的商品总额
        BigDecimal noMarketingGoodsTotalPrice =
                response.getGoodsInfos().stream().filter(goodsInfo ->(goodsInfoIds.contains(goodsInfo.getGoodsInfoId())
                        && !marketingGoodsInfoIds.contains(goodsInfo.getGoodsInfoId()))).map(goodsInfo -> {
                    goodsInfo.setMarketPrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO : goodsInfo
                            .getMarketPrice());
                    log.info(" ====================== 开始计算商品总额 ：goodsInfo:{}=====================",goodsInfo);
                    // 社交分销-购物车-分销价2
                    if (Objects.equals(goodsInfo.getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED)) {
                        //取市场价
                        return goodsInfo.getMarketPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                    }
                    if (goodsInfo.getPriceType() == GoodsPriceType.STOCK.toValue()) {
                        // 按区间设价，获取满足的多个等级的区间价里的最大价格
                        Optional<GoodsIntervalPriceVO> optional =
                                response.getGoodsIntervalPrices().stream().filter(goodsIntervalPrice -> goodsIntervalPrice.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                                        .filter(goodsIntervalPrice -> goodsInfo.getBuyCount().compareTo(goodsIntervalPrice.getCount()) >= 0).max(Comparator.comparingLong(GoodsIntervalPriceVO::getCount));

                        if (optional.isPresent()) {
                            return optional.get().getPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                        } else {
                            return BigDecimal.ZERO;
                        }
                    } else if (goodsInfo.getPriceType() == GoodsPriceType.CUSTOMER.toValue()) {
                        // 按级别设价，获取级别价
                        return goodsInfo.getSalePrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                    } else {
                        if(Objects.nonNull(goodsInfo.getGoodsInfoType()) && goodsInfo.getGoodsInfoType() == 1){
                            return goodsInfo.getMarketPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                        }else
                        // 已登录且为企业会员取大客户价
                        if ( Objects.nonNull(customerVO) && Objects.equals(customerVO.getEnterpriseStatusXyy(), EnterpriseCheckState.CHECKED)
                                && null != goodsInfo.getVipPrice() && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                            //取大客户价
                            return goodsInfo.getVipPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                        }
                        // 已登录且为大客户标识取大客户价
                        if ( Objects.nonNull(customerVO) && Objects.equals(customerVO.getVipFlag(), DefaultFlag.YES)
                                && null != goodsInfo.getVipPrice() && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                            //取大客户价
                            return goodsInfo.getVipPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                        }
                        // 默认取市场价
                        return goodsInfo.getMarketPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                    }


                }).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 优惠总额
        BigDecimal discountTotalPrice =
                response.getStoreMarketingMap().values().parallelStream().flatMap(storeMarketings -> storeMarketings.parallelStream()).map(storeMarketing -> {
                    if (BigDecimal.ZERO.compareTo(storeMarketing.getLack()) == 0) {
                        BigDecimal discountPrice = BigDecimal.ZERO;
                        BigDecimal totalAmount = storeMarketing.getTotalAmount();

                        //过滤不符合有必选商品但所选的商品中没有必选商品的活动
                        if (marketingStrList.size() > 0 && marketingStrList.contains(storeMarketing.getMarketingId())){
                            return BigDecimal.ZERO;
                        }

                        // 满减
                        if (storeMarketing.getMarketingType() == MarketingType.REDUCTION) {
                            // 若满减叠加且为最大级优惠，计算叠加金额
                            if (BoolFlag.YES.equals(storeMarketing.getIsOverlap())) {
                                MarketingFullReductionLevelVO fullReductionLevel = storeMarketing.getFullReductionLevel();
                                // 所有满减等级
                                List<MarketingFullReductionLevelVO> marketingFullReductionLevelVOList = marketingFullReductionQueryProvider.listByMarketingId(new MarketingFullReductionByMarketingIdRequest(fullReductionLevel.getMarketingId()))
                                        .getContext().getMarketingFullReductionLevelVOList();
                                if (MarketingSubType.REDUCTION_FULL_AMOUNT.equals(storeMarketing.getSubType())) {
                                    // 最大级别
                                    MarketingFullReductionLevelVO maxLevel = marketingFullReductionLevelVOList.stream()
                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount)).get();
                                    // 是否是最大优惠级别
                                    boolean isMax = maxLevel.getReductionLevelId().equals(fullReductionLevel.getReductionLevelId());
                                    if (isMax) {
                                        discountPrice = totalAmount.divide(fullReductionLevel.getFullAmount(), 0, BigDecimal.ROUND_DOWN).multiply(fullReductionLevel.getReduction());
                                    } else {
                                        discountPrice = storeMarketing.getDiscount();
                                    }
                                }
                                if (MarketingSubType.REDUCTION_FULL_COUNT.equals(storeMarketing.getSubType())) {
                                    // 最大级别
                                    MarketingFullReductionLevelVO maxLevel = marketingFullReductionLevelVOList.stream()
                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                                    // 是否是最大优惠级别
                                    boolean isMax = maxLevel.getReductionLevelId().equals(fullReductionLevel.getReductionLevelId());
                                    if (isMax) {
                                        discountPrice = BigDecimal.valueOf(storeMarketing.getTotalCount())
                                                .divide(BigDecimal.valueOf(fullReductionLevel.getFullCount()), 0, BigDecimal.ROUND_DOWN).multiply(fullReductionLevel.getReduction());
                                    } else {
                                        discountPrice = storeMarketing.getDiscount();
                                    }
                                }
                                if(MarketingSubType.REDUCTION_FULL_ORDER.equals(storeMarketing.getSubType())){
                                    // 最大级别
                                    MarketingFullReductionLevelVO maxLevel = marketingFullReductionLevelVOList.stream()
                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                                    // 是否是最大优惠级别
                                    boolean isMax = maxLevel.getReductionLevelId().equals(fullReductionLevel.getReductionLevelId());
                                    if (isMax) {
                                        discountPrice = BigDecimal.valueOf(storeMarketing.getTotalCount())
                                                .divide(BigDecimal.valueOf(fullReductionLevel.getFullCount()), 0, BigDecimal.ROUND_DOWN).multiply(fullReductionLevel.getReduction());
                                    } else {
                                        discountPrice = storeMarketing.getDiscount();
                                    }
                                }
                            } else {
                                discountPrice = storeMarketing.getDiscount();
                            }
                        } else if (storeMarketing.getMarketingType() == MarketingType.DISCOUNT) {
                            // 满折
                            discountPrice = totalAmount.multiply(BigDecimal.ONE.subtract(storeMarketing.getDiscount()));
                        }

                        // 只有参与营销活动的商品总金额不小于优惠金额时，才返回优惠金额，否则返回商品总额
                        if (totalAmount.compareTo(discountPrice) >= 0) {
                            return discountPrice;
                        } else {
                            return totalAmount;
                        }
                    }

                    return BigDecimal.ZERO;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 商品总额=营销商品总额+非营销商品总额
        BigDecimal totalPrice = marketingGoodsTotalPrice.add(noMarketingGoodsTotalPrice);

        BigDecimal tradePrice = totalPrice.subtract(discountTotalPrice);

        // 避免总价为负数
        if (tradePrice.compareTo(BigDecimal.ZERO) < 0) {
            tradePrice = BigDecimal.ZERO;
        }
        //社交分销-购物车-分销佣金2
        //分销佣金
        BigDecimal distributeCommission =
                response.getGoodsInfos().stream()
                        .filter(goodsInfo -> goodsInfoIds.contains(goodsInfo.getGoodsInfoId()) && Objects.equals(goodsInfo.getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED))
                        .map(goodsInfo -> goodsInfo.getDistributionCommission().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalPrice(totalPrice);
        response.setTradePrice(tradePrice);
        response.setDiscountPrice(discountTotalPrice);
        response.setDistributeCommission(distributeCommission);
        return response;
    }

    /**
     * 未登录时,验证并设置前端传入的商品使用营销信息
     *
     * @param response              采购单返回对象
     * @param goodsMarketingDTOList 前端传入的用户针对sku选择的营销活动信息
     */
    @Transactional(rollbackFor = Exception.class)
    public PurchaseResponse validateAndSetGoodsMarketings(PurchaseResponse response,
                                                          List<GoodsMarketingDTO> goodsMarketingDTOList) {
        Map<String, List<MarketingViewVO>> goodsMarketingMap = response.getGoodsMarketingMap();
        List<GoodsMarketingVO> goodsMarketingList = goodsMarketingDTOList.stream().map(dto ->
                GoodsMarketingVO.builder().goodsInfoId(dto.getGoodsInfoId()).marketingId(dto.getMarketingId()).build())
                .collect(Collectors.toList());

        if (MapUtils.isEmpty(goodsMarketingMap) && CollectionUtils.isNotEmpty(goodsMarketingList)) {
            goodsMarketingList = new ArrayList<>();
        } else if (MapUtils.isNotEmpty(goodsMarketingMap)) {
            // 过滤出仍然有效的商品营销信息
            goodsMarketingList = goodsMarketingList.stream()
                    .filter(goodsMarketing -> {
                        String goodsInfoId = goodsMarketing.getGoodsInfoId();
                        // 选择的营销存在 并且 采购单商品参与了该营销
                        return goodsMarketingMap.get(goodsInfoId) != null
                                && goodsMarketingMap.get(goodsInfoId).stream().anyMatch(marketingResponse ->
                                marketingResponse.getMarketingId().equals(goodsMarketing.getMarketingId()));
                    }).collect(Collectors.toList());
            Map<String, Long> oldMap = goodsMarketingList.stream()
                    .collect(Collectors.toMap(GoodsMarketingVO::getGoodsInfoId, GoodsMarketingVO::getMarketingId));

            // 过滤出用户未选择的商品营销活动信息
            List<GoodsMarketingVO> addList = goodsMarketingMap.entrySet().stream()
                    .filter(set -> oldMap.get(set.getKey()) == null)
                    .map(set -> GoodsMarketingVO.builder()
                            .goodsInfoId(set.getKey())
                            .marketingId(set.getValue().get(0).getMarketingId())
                            .build())
                    .collect(Collectors.toList());
            if (!addList.isEmpty()) {
                goodsMarketingList.addAll(addList);
            }
        }
        response.setGoodsMarketings(goodsMarketingList);
        return response;
    }

    /**
     * 同步商品使用的营销
     *
     * @param goodsMarketingMap
     * @param customerId
     */
    @Transactional(rollbackFor = Exception.class)
    public List<GoodsMarketingVO> syncGoodsMarketings(Map<String, List<MarketingViewVO>> goodsMarketingMap, String customerId) {
        // 优化，逻辑迁移
        GoodsMarketingSyncRequest goodsMarketingSyncRequest = new GoodsMarketingSyncRequest();
        goodsMarketingSyncRequest.setCustomerId(customerId);
        Map<String, List<Long>> marketingIdsMap = new HashMap<>();
        if (!goodsMarketingMap.isEmpty()) {
            goodsMarketingMap.forEach((key, value) -> {
                List<Long> marketingIds = value.stream()
                        .map(MarketingViewVO::getMarketingId)
                        .collect(Collectors.toList());
                marketingIdsMap.put(key, marketingIds);
            });
        }
        goodsMarketingSyncRequest.setMarketingIdsMap(marketingIdsMap);
        return goodsMarketingProvider.syncGoodsMarketings(goodsMarketingSyncRequest).getContext().getGoodsMarketingList();

        // 逻辑已迁移
//        List<GoodsMarketingVO> goodsMarketingList = this.queryGoodsMarketingList(customerId);

//        if (goodsMarketingMap.isEmpty() && CollectionUtils.isNotEmpty(goodsMarketingList)) {
//            goodsMarketingProvider.deleteByCustomerId(new GoodsMarketingDeleteByCustomerIdRequest(customerId));
//        } else if (!goodsMarketingMap.isEmpty()) {
//            Map<String, Long> oldMap = goodsMarketingList.stream()
//                    .collect(Collectors.toMap(GoodsMarketingVO::getGoodsInfoId, GoodsMarketingVO::getMarketingId));
//
//            List<String> delList = goodsMarketingList.stream()
//                    .filter(goodsMarketing -> {
//                        String goodsInfoId = goodsMarketing.getGoodsInfoId();
//
//                        // 数据库里存的采购单商品没有参与营销或者选择的营销不存在了，则要删除该条记录
//                        return goodsMarketingMap.get(goodsInfoId) == null
//                                || !goodsMarketingMap.get(goodsInfoId).stream().anyMatch(marketingResponse ->
//                                marketingResponse.getMarketingId().equals(goodsMarketing.getMarketingId()));
//                    }).map(GoodsMarketingVO::getGoodsInfoId).collect(Collectors.toList());
//
//            List<GoodsMarketingVO> addList = goodsMarketingMap.entrySet().stream()
//                    .filter(set -> oldMap.get(set.getKey()) == null
//                            || delList.contains(set.getKey()))
//                    .map(set -> GoodsMarketingVO.builder()
//                            .customerId(customerId)
//                            .goodsInfoId(set.getKey())
//                            .marketingId(set.getValue().get(0).getMarketingId())
//                            .build())
//                    .collect(Collectors.toList());
//
//            // 先删除
//            if (!delList.isEmpty()) {
//                GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest
//                        = new GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest();
//                goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setCustomerId(customerId);
//                goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setGoodsInfoIds(delList);
//                goodsMarketingProvider.deleteByCustomerIdAndGoodsInfoIds(goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest);
//            }
//
//            // 再增加
//            if (!addList.isEmpty()) {
//                List<GoodsMarketingDTO> goodsMarketingDTOS = addList.stream().map(info -> {
//                    GoodsMarketingDTO goodsMarketingDTO = new GoodsMarketingDTO();
//                    goodsMarketingDTO.setCustomerId(info.getCustomerId());
//                    goodsMarketingDTO.setGoodsInfoId(info.getGoodsInfoId());
//                    goodsMarketingDTO.setMarketingId(info.getMarketingId());
//                    goodsMarketingDTO.setId(info.getId());
//                    return goodsMarketingDTO;
//                }).collect(Collectors.toList());
//                GoodsMarketingBatchAddRequest goodsMarketingBatchAddRequest = new GoodsMarketingBatchAddRequest();
//                goodsMarketingBatchAddRequest.setGoodsMarketings(goodsMarketingDTOS);
//                goodsMarketingProvider.batchAdd(goodsMarketingBatchAddRequest);
//            }
//        }
    }


    /**
     * 修改商品使用的营销
     *
     * @param goodsInfoId
     * @param marketingId
     * @param customer
     */
    @LcnTransaction
    public void modifyGoodsMarketing(String goodsInfoId, Long marketingId, CustomerVO customer,Long wareId) {
//        GoodsInfoEditResponse response = goodsInfoService.findById(goodsInfoId);
        GoodsInfoViewByIdResponse response = goodsInfoQueryProvider.getViewById(
                GoodsInfoViewByIdRequest.builder()
                        .goodsInfoId(goodsInfoId)
                        .wareId(wareId)
                        .build()
        ).getContext();
        //todo 后台修改营销时库存显示
        PurchaseGetGoodsMarketingResponse goodsMarketingMap =
                this.getGoodsMarketing(Arrays.asList(response.getGoodsInfo()), customer, null);

        List<MarketingViewVO> marketingResponses = goodsMarketingMap.getMap().get(goodsInfoId);

        if (marketingResponses != null && marketingResponses.stream().anyMatch(marketingResponse -> marketingResponse.getMarketingId().equals(marketingId))) {
            // 更新商品选择的营销
            GoodsMarketingModifyRequest goodsMarketingModifyRequest = new GoodsMarketingModifyRequest();
            goodsMarketingModifyRequest.setCustomerId(customer.getCustomerId());
            goodsMarketingModifyRequest.setGoodsInfoId(goodsInfoId);
            goodsMarketingModifyRequest.setMarketingId(marketingId);
            log.info("修改商品使用的营销================>goodsMarketingModifyRequest:::{}",goodsMarketingModifyRequest);
            goodsMarketingProvider.modify(goodsMarketingModifyRequest);
        }
    }

    /**
     * 验证分销店铺商品
     *
     * @param goodsInfoVOList
     */
    public void verifyDistributorGoodsInfo(DistributeChannel channel, List<GoodsInfoVO> goodsInfoVOList) {

        // 如果是小店请求，验证商品是否是小店商品
        if (channel.getChannelType() == ChannelType.SHOP) {
            List<String> skuIdList =
                    goodsInfoVOList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            //验证是否为分销商品
            List<String> invalidDistributeIds =
                    goodsInfoVOList.stream().filter(goodsInfo -> !Objects.equals(goodsInfo.getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED)).map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            DistributorGoodsInfoVerifyRequest verifyRequest = new DistributorGoodsInfoVerifyRequest();
            verifyRequest.setGoodsInfoIds(skuIdList);
            verifyRequest.setDistributorId(channel.getInviteeId());
            //验证是否在店铺精选范围内
            List<String> invalidIds = distributorGoodsInfoQueryProvider
                    .verifyDistributorGoodsInfo(verifyRequest).getContext().getInvalidIds();
            if (CollectionUtils.isNotEmpty(invalidDistributeIds)) {
                if (CollectionUtils.isEmpty(invalidIds)) {
                    invalidIds = invalidDistributeIds;
                } else {
                    invalidIds.addAll(invalidDistributeIds);
                }
            }
            if (CollectionUtils.isNotEmpty(invalidIds)) {
                List<String> ids = invalidIds;
                //叠加分销状态，并设为无效
                goodsInfoVOList.stream().filter(goodsInfo -> Objects.equals(GoodsStatus.OK,
                        goodsInfo.getGoodsStatus())).forEach(goodsInfo -> {
                    if (ids.contains(goodsInfo.getGoodsInfoId())) {
                        goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                    }
                });
            }
        }
    }

    /**
     * 获取采购单中 参加同种营销的商品列表/总额/优惠(批量处理)
     * @param marketingGoodsMap
     * @param customer
     * @param frontReq
     * @param goodsInfoIds
     * @param isPurchase
     * @param checkSkuIds
     * @param wareId
     * @param marketingMap
     * @return
     */
    public Map<Long, PurchaseMarketingCalcResponse> calcMarketingByMarketingIdBaseList(Map<Long, List<String>> marketingGoodsMap,
                                                                                       CustomerVO customer,
                                                                                       PurchaseFrontRequest frontReq,
                                                                                       List<String> goodsInfoIds,
                                                                                       boolean isPurchase,
                                                                                       List<String> checkSkuIds,
                                                                                       Long wareId,
                                                                                       Map<Long, MarketingForEndVO> marketingMap) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase calcMarketingByMarketingIdBaseList start");

        try {
            List<Long> storeIds = marketingMap.values().stream()
                    .map(MarketingVO::getStoreId)
                    .collect(Collectors.toList());
            // 获取用户在店铺里的等级
            Map<Long, CommonLevelVO> levelMap = this.getLevelMapByStoreIds(storeIds, customer);

            sb.append(",getLevelMapByStoreIds end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<GoodsMarketingVO> goodsMarketingList = this.queryGoodsMarketingList(customer.getCustomerId());

            sb.append(",queryGoodsMarketingList end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            // 查询商品
            GoodsInfoViewByIdsResponse goodsInfoResponse = new GoodsInfoViewByIdsResponse();
            GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
            List<GoodsIntervalPriceVO> goodsIntervalPrices = new ArrayList<>();
            List<GoodsInfoVO> goodsInfos = Collections.EMPTY_LIST;
            if (!(goodsInfoIds == null || goodsInfoIds.isEmpty())) {
                if (customer != null) {
                    //这里增加了一个逻辑，所有购物车的商品都可参加订单满赠的活动
                    List<String> skuIds = goodsInfoIds;
                    if (isGifFullOrder(goodsInfoIds)) {
                        skuIds = Arrays.asList();
                    }
                    PurchaseRequest request = PurchaseRequest.builder()
                            .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
                            .goodsInfoIds(skuIds)
                            .build();
                    List<Purchase> follows;
                    Sort sort = request.getSort();
                    if (Objects.nonNull(sort)) {
                        follows = purchaseRepository.findAll(request.getWhereCriteria(), sort);
                    } else {
                        follows = purchaseRepository.findAll(request.getWhereCriteria());
                    }

                    sb.append(",purchaseRepository.findAll end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();

                    goodsInfoRequest.setGoodsInfoIds(follows.stream().map(Purchase::getGoodsInfoId).collect(Collectors.toList()));
                    goodsInfoRequest.setWareId(wareId);
                    goodsInfoResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();

                    sb.append(",goodsInfoQueryProvider.listViewByIds end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();

                    //填充SKU的购买数
                    this.fillBuyCount(goodsInfoResponse.getGoodsInfos(), follows);

                    sb.append(",fillBuyCount end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    goodsInfoRequest.setGoodsInfoIds(goodsInfoIds);
                    goodsInfoRequest.setWareId(wareId);
                    goodsInfoResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();

                    sb.append(",goodsInfoQueryProvider.listViewByIds end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();

                    if (goodsInfoResponse.getGoodsInfos() != null) {
                        goodsInfoResponse.getGoodsInfos().forEach((goodsInfo) -> {
                            goodsInfo.setBuyCount(0L);
                            List<PurchaseGoodsInfoDTO> dtoList =
                                    frontReq.getGoodsInfoDTOList().stream().filter((purchase) ->
                                            goodsInfo.getGoodsInfoId().equals(purchase.getGoodsInfoId())).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(dtoList)) {
                                goodsInfo.setBuyCount((dtoList.get(0)).getGoodsNum());
                            }
                        });
                    }
                }
                goodsInfos = goodsInfoResponse.getGoodsInfos();
                //设定SKU状态
                goodsInfoResponse.setGoodsInfos(
                        goodsInfoProvider.fillGoodsStatus(
                                GoodsInfoFillGoodsStatusRequest.builder()
                                        .goodsInfos(KsBeanUtil.convertList(goodsInfoResponse.getGoodsInfos(),
                                                GoodsInfoDTO.class))
                                        .build()
                        ).getContext().getGoodsInfos()
                );

                sb.append(",goodsInfoProvider.fillGoodsStatus end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                if (CollectionUtils.isNotEmpty(goodsInfoResponse.getGoodsInfos()) && customer == null) {
                    goodsMarketingList = frontReq.getGoodsMarketingDTOList().stream().map((dto) ->
                            GoodsMarketingVO.builder().marketingId(dto.getMarketingId()).goodsInfoId(dto.getGoodsInfoId()).build()
                    ).collect(Collectors.toList());
                }

                //计算区间价
                GoodsIntervalPriceByCustomerIdResponse intervalPriceResponse =
                        goodsIntervalPriceProvider.putByCustomerId(
                                GoodsIntervalPriceByCustomerIdRequest.builder()
                                        .goodsInfoDTOList(KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class))
                                        .customerId(customer != null ? customer.getCustomerId() : null).build()).getContext();

                sb.append(",goodsIntervalPriceProvider.putByCustomerId end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                goodsIntervalPrices = intervalPriceResponse.getGoodsIntervalPriceVOList();
                goodsInfos = intervalPriceResponse.getGoodsInfoVOList();

                //计算级别价格
                goodsInfos = marketingLevelPluginProvider.goodsListFilter(
                        MarketingLevelGoodsListFilterRequest.builder()
                                .customerDTO(customer != null ? KsBeanUtil.convert(customer, CustomerDTO.class) : null)
                                .goodsInfos(KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class)).build())
                        .getContext().getGoodsInfoVOList();

                sb.append(",marketingLevelPluginProvider.goodsListFilter end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();
            }
            Map<String, GoodsInfoVO> goodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
            Map<Long, PurchaseMarketingCalcResponse> responseMap = new HashedMap();

            sb.append(",marketingGoodsMap.foreach start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            for (Map.Entry<Long, List<String>> map : marketingGoodsMap.entrySet()) {
                if (CollectionUtils.isEmpty(map.getValue())) {
                    continue;
                }
                List<GoodsInfoVO> goodsInfoList = map.getValue().stream()
                        .map(goodsInfoVOMap::get)
                        .collect(Collectors.toList());
                PurchaseMarketingCalcResponse response = new PurchaseMarketingCalcResponse();

                // 查询该营销活动参与的商品列表
                MarketingForEndVO marketingVOResponse = marketingMap.get(map.getKey());

                if (!validMarketing(marketingVOResponse, levelMap)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, marketingVOResponse.getMarketingName() + "无效！");
                }

                long lackCount = 0L;
                BigDecimal lackAmount = BigDecimal.ZERO;
                BigDecimal discount = BigDecimal.ZERO;

                // 采购单勾选的商品中没有参与该营销的情况，取营销的最低等级
                if (isPurchase && (map.getValue() == null || map.getValue().isEmpty())) {
                    // 满金额
                    if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {

                        BigDecimal levelAmount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                levelAmount = marketingVOResponse.getFullReductionLevelList().get(0).getFullAmount();
                                discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));

                                break;
                            case DISCOUNT:
                                levelAmount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullAmount();
                                discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));

                                break;
                            case GIFT:
                                levelAmount = marketingVOResponse.getFullGiftLevelList().get(0).getFullAmount();
                                response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));

                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelAmount = BigDecimal.ZERO;
                        }

                        response.setLack(levelAmount);

                    } else if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                        // 满数量
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));

                                break;
                            case DISCOUNT:
                                levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));

                                break;
                            case GIFT:
                                levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));

                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelCount = 0L;
                        }

                        response.setLack(BigDecimal.valueOf(levelCount));
                    } else if (marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER  || marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_ORDER || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER  ) {

                        // 订单满赠
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                break;
                            case DISCOUNT:
                                levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                break;
                            case GIFT:
                                levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                break;
                            default:
                                levelCount = 0L;
                        }
                        response.setLack(BigDecimal.valueOf(levelCount));
                    } else {
                        throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
                    }

                    response.setGoodsInfoList(goodsInfos);
                    response.setDiscount(discount);
                } else {
                    List<GoodsInfoVO> newGoodsInfos = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(goodsInfoResponse.getGoodsInfos())) {
                        if (!isPurchase) {
                            Map<String, Long> goodsMarketingMap =
                                    goodsMarketingList.stream().collect(Collectors.toMap(GoodsMarketingVO::getGoodsInfoId,
                                            GoodsMarketingVO::getMarketingId));

                            // 凑单页，过滤出参加营销的商品
                            //订单满赠不过滤营销商品
                            if (Objects.nonNull(goodsMarketingMap.get("all"))) {
                                //存在订单满赠
                                newGoodsInfos = goodsInfoList;
                            } else {
                                List<MarketingScopeVO> marketingScopeList = marketingVOResponse.getMarketingScopeList();
                                newGoodsInfos = goodsInfoList.stream()
                                        .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                                && marketingScopeList.stream().anyMatch(scope -> scope.getScopeId().equals(goodsInfo.getGoodsInfoId())))
                                        .map(goodsInfo -> {
                                            //
                                            if (!map.getKey().equals(goodsMarketingMap.get(goodsInfo.getGoodsInfoId()))) {
                                                goodsInfo.setBuyCount(0L);
                                            }
                                            return goodsInfo;
                                        }).collect(Collectors.toList());
                            }
                        } else {
                            //订单满赠不过滤营销商品
                            if (isGifFullOrder(map.getValue())) {
                                // 过滤出参加营销的商品
                                newGoodsInfos = goodsInfoList.stream()
                                        .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                                && checkSkuIds.contains(goodsInfo.getGoodsInfoId()))
                                        .collect(Collectors.toList());
                                List<GoodsMarketingVO> goodsMarketingVOS = goodsMarketingList;
                                newGoodsInfos = newGoodsInfos.stream().filter(good ->
                                        goodsMarketingVOS.stream().filter(g ->
                                                g.getMarketingId().equals(map.getKey())
                                                        && good.getGoodsInfoId().equals(g.getGoodsInfoId())).findFirst().isPresent()).collect(Collectors.toList());

                            } else {
                                // 过滤出参加营销的商品
                                List<MarketingScopeVO> marketingScopeList = marketingVOResponse.getMarketingScopeList();
                                newGoodsInfos = goodsInfoList.stream()
                                        .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                                && marketingScopeList.stream().anyMatch(scope -> scope.getScopeId().equals(goodsInfo.getGoodsInfoId())))
                                        .collect(Collectors.toList());
                            }

                        }
                    }

                    // 计算商品总额
                    List<GoodsIntervalPriceVO> newIntervalPrices = goodsIntervalPrices;
                    BigDecimal totalAmount = newGoodsInfos.stream().map(goodsInfo -> {
                        if (goodsInfo.getPriceType() == GoodsPriceType.STOCK.toValue()) {
                            // 按区间设价，获取满足的多个等级的区间价里的最大价格
                            Optional<GoodsIntervalPriceVO> optional = newIntervalPrices.stream().filter(goodsIntervalPrice
                                    -> goodsIntervalPrice.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                                    .filter(goodsIntervalPrice -> goodsInfo.getBuyCount().compareTo(goodsIntervalPrice.getCount()) >= 0).max(Comparator.comparingLong(GoodsIntervalPriceVO::getCount));

                            if (optional.isPresent()) {
                                return optional.get().getPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                            } else {
                                return BigDecimal.ZERO;
                            }
                        } else if (goodsInfo.getPriceType() == GoodsPriceType.CUSTOMER.toValue()) {
                            // 按级别设价，获取级别价
                            return goodsInfo.getSalePrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                        } else {
                            if (Objects.nonNull(customer) && Objects.equals(customer.getEnterpriseStatusXyy(), EnterpriseCheckState.CHECKED)
                                    && null != goodsInfo.getVipPrice() && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                                return goodsInfo.getVipPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                            } else {
                                return goodsInfo.getMarketPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount()));
                            }
                        }
                    }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    // 计算商品总数
                    Long totalCount = newGoodsInfos.stream().map(goodsInfo -> goodsInfo.getBuyCount()).reduce(0L, Long::sum);

                    // 满金额
                    if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {
                        // 计算达到营销级别的金额
                        BigDecimal levelAmount;

                        // 根据不用的营销类型，计算满足条件的营销等级里最大的一个，如果不满足营销等级里任意一个，则默认取最低等级
                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
                                        marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
                                                .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount));

                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullReductionLevelList())){
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                }
                                // 满足条件的最大的等级
                                if (fullReductionLevelOptional.isPresent()) {
                                    levelAmount = fullReductionLevelOptional.get().getFullAmount();
                                    discount = fullReductionLevelOptional.get().getReduction();
                                    response.setFullReductionLevel(fullReductionLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelAmount = marketingVOResponse.getFullReductionLevelList().get(0).getFullAmount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                }
                                // 是否满足最大等级
                                MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount)).get();
                                boolean isMax = maxLevel.getFullAmount().compareTo(totalAmount) < 0;
                                // 满金额减最大等级且可叠加计算折扣
                                if (MarketingSubType.REDUCTION_FULL_AMOUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                    discount = totalAmount.divide(levelAmount, 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                }

                                break;
                            case DISCOUNT:
                                Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
                                        marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
                                                .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullAmount));

                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullDiscountLevelList())){
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());
                                }
                                // 满足条件的最大的等级
                                if (fullDiscountLevelOptional.isPresent()) {
                                    levelAmount = fullDiscountLevelOptional.get().getFullAmount();
                                    discount = fullDiscountLevelOptional.get().getDiscount();
                                    response.setFullDiscountLevel(fullDiscountLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelAmount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullAmount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                }

                                break;
                            case GIFT:
                                List<MarketingFullGiftLevelVO> levels =
                                        marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0).collect(Collectors.toList());

                                Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
                                        levels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount));

                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullGiftLevelList())){
                                    response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                }
                                // 满足条件的最大的等级
                                if (fullGiftLevelOptional.isPresent()) {
                                    levelAmount = fullGiftLevelOptional.get().getFullAmount();
                                    response.setFullGiftLevel(fullGiftLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelAmount = marketingVOResponse.getFullGiftLevelList().get(0).getFullAmount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                }
                                // 最大赠级别
                                MarketingFullGiftLevelVO maxLevelVO = marketingVOResponse.getFullGiftLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount)).get();
                                // 是否满足最大等级
                                boolean isMaxLevel = maxLevelVO.getFullAmount().compareTo(totalAmount) < 0;
                                // 满金额赠可叠加且是最大级别计算赠品数量
                                if (MarketingSubType.GIFT_FULL_AMOUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMaxLevel) {
                                    // 如果是赠品数量是叠加倍数，赠品营销只取最大级别,计算赠品数量
                                    Long multiple = totalAmount.divide(maxLevelVO.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                                    if (multiple.compareTo(1L) > 0) {
                                        for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                            // 计算赠品数量
                                            detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                        }
                                        List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                        levelVOS.add(maxLevelVO);
                                        marketingVOResponse.setFullGiftLevelList(levelVOS);
                                    }
                                }
                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelAmount = BigDecimal.ZERO;
                        }

                        // 计算达到营销级别的差额
                        lackAmount = levelAmount.compareTo(totalAmount) <= 0 ? BigDecimal.ZERO :
                                levelAmount.subtract(totalAmount);

                        response.setLack(lackAmount);

                    } else if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                        // 满数量
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
                                        marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
                                                .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount));

                                // 满足条件的最大的等级
                                if (fullReductionLevelOptional.isPresent()) {
                                    levelCount = fullReductionLevelOptional.get().getFullCount();
                                    discount = fullReductionLevelOptional.get().getReduction();
                                    response.setFullReductionLevel(fullReductionLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                }
                                // 是否满足最大等级
                                MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                                boolean isMax = maxLevel.getFullCount().compareTo(totalCount) < 0;
                                // 满数量减可叠加且满足最大级计算折扣
                                if (MarketingSubType.REDUCTION_FULL_COUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                    discount = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(levelCount), 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                }

                                break;
                            case DISCOUNT:
                                Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
                                        marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
                                                .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount));

                                // 满足条件的最大的等级
                                if (fullDiscountLevelOptional.isPresent()) {
                                    levelCount = fullDiscountLevelOptional.get().getFullCount();
                                    discount = fullDiscountLevelOptional.get().getDiscount();
                                    response.setFullDiscountLevel(fullDiscountLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                }

                                break;
                            case GIFT:
                                List<MarketingFullGiftLevelVO> giftLevels =
                                        marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());

                                Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
                                        giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));

                                // 满足条件的最大的等级
                                if (fullGiftLevelOptional.isPresent()) {
                                    levelCount = fullGiftLevelOptional.get().getFullCount();
                                    response.setFullGiftLevel(fullGiftLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                }
                                // 最大赠级别
                                MarketingFullGiftLevelVO maxLevelVO = marketingVOResponse.getFullGiftLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount)).get();
                                // 是否满足最大等级
                                boolean isMaxLevel = maxLevelVO.getFullCount().compareTo(totalCount) < 0;
                                // 满数量赠可叠加且满足最大级优惠计算赠品数量
                                if (MarketingSubType.GIFT_FULL_COUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMaxLevel) {
                                    // 如果是赠品数量是叠加倍数，赠品营销只取最大级别, 计算赠品数量
                                    Long multiple = BigDecimal.valueOf(totalCount)
                                            .divide(BigDecimal.valueOf(maxLevelVO.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                                    if (multiple.compareTo(1L) > 0) {
                                        for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                            // 计算赠品数量
                                            detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                        }
                                        List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                        levelVOS.add(maxLevelVO);
                                        marketingVOResponse.setFullGiftLevelList(levelVOS);
                                    }
                                }

                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelCount = 0L;
                        }

                        // 计算达到营销级别缺少的数量
                        lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                                levelCount.longValue() - totalCount.longValue();

                        response.setLack(BigDecimal.valueOf(lackCount));

                    } else if (marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER  || marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_ORDER   || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER ) {
                        // 订单满赠
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
                                        marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
                                                .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount));

                                // 满足条件的最大的等级
                                if (fullReductionLevelOptional.isPresent()) {
                                    levelCount = fullReductionLevelOptional.get().getFullCount();
                                    discount = fullReductionLevelOptional.get().getReduction();
                                    response.setFullReductionLevel(fullReductionLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                }
                                break;
                            case DISCOUNT:
                                Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
                                        marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
                                                .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount));

                                // 满足条件的最大的等级
                                if (fullDiscountLevelOptional.isPresent()) {
                                    levelCount = fullDiscountLevelOptional.get().getFullCount();
                                    discount = fullDiscountLevelOptional.get().getDiscount();
                                    response.setFullDiscountLevel(fullDiscountLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                }
                                break;
                            case GIFT:
                                List<MarketingFullGiftLevelVO> giftLevels =
                                        marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());

                                Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
                                        giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));
                                // 满足条件的最大的等级
                                if (fullGiftLevelOptional.isPresent()) {
                                    levelCount = fullGiftLevelOptional.get().getFullCount();
                                    response.setFullGiftLevel(fullGiftLevelOptional.get());
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                }
                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                break;
                            default:
                                levelCount = 0L;
                        }
                        // 计算达到营销级别缺少的数量
                        lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                                levelCount.longValue() - totalCount.longValue();
                        response.setLack(BigDecimal.valueOf(lackCount));
                    } else {
                        throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
                    }

                    response.setGoodsInfoList(newGoodsInfos);
                    response.setTotalCount(totalCount);
                    response.setTotalAmount(totalAmount);
                    response.setDiscount(discount);
//            }
                }

                sb.append(",marketingGoodsMap.foreach end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                // 如果有赠品，则查询赠品商品的详细信息
                if (response.getFullGiftLevelList() != null && !response.getFullGiftLevelList().isEmpty()) {
                    List<String> skuIds =
                            response.getFullGiftLevelList().stream().flatMap(marketingFullGiftLevel -> marketingFullGiftLevel.getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId)).distinct().collect(Collectors.toList());
                    GoodsInfoViewByIdsRequest goodsInfoViewRequest = GoodsInfoViewByIdsRequest.builder()
                            .goodsInfoIds(skuIds)
                            .isHavSpecText(Constants.yes)
                            .wareId(wareId)
                            .build();
                    response.setGiftGoodsInfoResponse(KsBeanUtil.convert(goodsInfoQueryProvider.listViewByIds(goodsInfoViewRequest).getContext(), PurchaseGoodsViewVO.class));

                    sb.append(",gift goodsInfoQueryProvider.listViewByIds end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                }

                response.setStoreId(marketingVOResponse.getStoreId());
                response.setMarketingId(marketingVOResponse.getMarketingId());
                response.setMarketingType(marketingVOResponse.getMarketingType());
                response.setSubType(marketingVOResponse.getSubType());
                response.setIsOverlap(marketingVOResponse.getIsOverlap());
                response.setGoodsInfoIds(map.getValue());

                responseMap.put(marketingVOResponse.getMarketingId(), response);
            }
            return responseMap;
        }finally {
            log.info(sb.toString());
        }

    }

    public Map<Long, PurchaseMarketingCalcResponse> simpCalcMarketingByMarketingId(Map<Long, List<String>> marketingGoodsMap,
                                                                                      CustomerVO customer,
                                                                                      PurchaseFrontRequest frontReq,
                                                                                      List<String> goodsInfoIds,
                                                                                      boolean isPurchase,
                                                                                      List<String> checkSkuIds,
                                                                                      Map<Long, MarketingForEndVO> marketingMap,
                                                                                      List<GoodsInfoVO> checkGoodsInfoVOS,
                                                                                      List<GoodsMarketingDTO> goodsMarketings) {
        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase simpCalcMarketingByMarketingId start");

        /* List<Long> storeIds = marketingMap.values().stream()
                .map(MarketingVO::getStoreId)
                .collect(Collectors.toList());*/
        // 获取用户在店铺里的等级
       /* Map<Long, CommonLevelVO> levelMap = this.getLevelMapByStoreIds(storeIds, customer);*/

        try {
            List<GoodsIntervalPriceVO> goodsIntervalPrices = new ArrayList<>();
            List<GoodsInfoVO> goodsInfos = Collections.EMPTY_LIST;
            if (!(goodsInfoIds == null || goodsInfoIds.isEmpty())) {
                if (customer != null) {
                    //这里增加了一个逻辑，所有购物车的商品都可参加订单满赠的活动
                    List<String> skuIds = goodsInfoIds;
                    if (isGifFullOrder(goodsInfoIds)) {
                        skuIds = Arrays.asList();
                    }
                    PurchaseRequest request = PurchaseRequest.builder()
                            .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
                            .goodsInfoIds(skuIds)
                            .build();
                    List<Purchase> follows;
                    Sort sort = request.getSort();
                    if (Objects.nonNull(sort)) {
                        follows = purchaseRepository.findAll(request.getWhereCriteria(), sort);
                    } else {
                        follows = purchaseRepository.findAll(request.getWhereCriteria());
                    }

                    sb.append(",purchaseRepository.findAll end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();

                    //填充SKU的购买数
                    this.fillBuyCount(checkGoodsInfoVOS, follows);

                    sb.append(",fillBuyCount end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    if (checkGoodsInfoVOS != null) {
                        for (GoodsInfoVO goodsInfo : checkGoodsInfoVOS) {
                            goodsInfo.setBuyCount(0L);
                            List<PurchaseGoodsInfoDTO> dtoList = new ArrayList<>();
                            for (PurchaseGoodsInfoDTO purchase : frontReq.getGoodsInfoDTOList()) {
                                if (goodsInfo.getGoodsInfoId().equals(purchase.getGoodsInfoId())) {
                                    dtoList.add(purchase);
                                }
                            }
                            if (CollectionUtils.isNotEmpty(dtoList)) {
                                goodsInfo.setBuyCount((dtoList.get(0)).getGoodsNum());
                            }
                        }
                    }
                }
                goodsInfos = checkGoodsInfoVOS;
                //设定SKU状态
                fillGoodsStatus(checkGoodsInfoVOS);

                sb.append(",fillGoodsStatus end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

           /* checkGoodsInfoVOS =
                    goodsInfoProvider.fillGoodsStatus(
                            GoodsInfoFillGoodsStatusRequest.builder()
                                    .goodsInfos(KsBeanUtil.convertList(checkGoodsInfoVOS,
                                            GoodsInfoDTO.class))
                                    .build()
                    ).getContext().getGoodsInfos();*/
                if (CollectionUtils.isNotEmpty(checkGoodsInfoVOS) && customer == null) {
                    goodsMarketings = frontReq.getGoodsMarketingDTOList();
                }

                //计算区间价
                GoodsIntervalPriceByCustomerIdResponse intervalPriceResponse =
                        goodsIntervalPriceProvider.putByCustomerId(
                                GoodsIntervalPriceByCustomerIdRequest.builder()
                                        .goodsInfoDTOList(KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class))
                                        .customerId(customer != null ? customer.getCustomerId() : null).build()).getContext();

                sb.append(",goodsIntervalPriceProvider.putByCustomerId end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                goodsIntervalPrices = intervalPriceResponse.getGoodsIntervalPriceVOList();
                goodsInfos = intervalPriceResponse.getGoodsInfoVOList();

                //计算级别价格
               /* goodsInfos = marketingLevelPluginProvider.goodsListFilter(
                        MarketingLevelGoodsListFilterRequest.builder()
                                .customerDTO(customer != null ? KsBeanUtil.convert(customer, CustomerDTO.class) : null)
                                .goodsInfos(KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class)).build())
                        .getContext().getGoodsInfoVOList();*/

                sb.append(",marketingLevelPluginProvider.goodsListFilter end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();
            }
            Map<String, GoodsInfoVO> goodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
            Map<Long, PurchaseMarketingCalcResponse> responseMap = new HashedMap();

            sb.append(",marketingGoodsMap.foreach start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<String> marketingsGoodsIds=goodsMarketings.stream().map(GoodsMarketingDTO::getGoodsInfoId).collect(Collectors.toList());

            for (Map.Entry<Long, List<String>> map : marketingGoodsMap.entrySet()) {
                if (CollectionUtils.isEmpty(map.getValue())) {
                    continue;
                }
                List<GoodsInfoVO> goodsInfoList = map.getValue().stream()
                        .map(goodsInfoVOMap::get)
                        .collect(Collectors.toList());
                goodsInfoList.removeIf(Objects::isNull);
                PurchaseMarketingCalcResponse response = new PurchaseMarketingCalcResponse();

                // 查询该营销活动参与的商品列表
                MarketingForEndVO marketingVOResponse = marketingMap.get(map.getKey());

           /* if (!validMarketing(marketingVOResponse, levelMap)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, marketingVOResponse.getMarketingName() + "无效！");
            }*/
                //校验营销活动
                if (marketingVOResponse.getIsPause() == BoolFlag.YES || marketingVOResponse.getDelFlag() == DeleteFlag.YES || marketingVOResponse.getBeginTime().isAfter(LocalDateTime.now())
                        || marketingVOResponse.getEndTime().isBefore(LocalDateTime.now())) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, marketingVOResponse.getMarketingName() + "无效！");
                }

                long lackCount = 0L;
                BigDecimal lackAmount = BigDecimal.ZERO;
                BigDecimal discount = BigDecimal.ZERO;

                // 采购单勾选的商品中没有参与该营销的情况，取营销的最低等级
                if (isPurchase && (map.getValue() == null || map.getValue().isEmpty())) {
                    // 满金额
                    if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {

                        BigDecimal levelAmount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                levelAmount = marketingVOResponse.getFullReductionLevelList().get(0).getFullAmount();
                                discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));

                                break;
                            case DISCOUNT:
                                levelAmount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullAmount();
                                discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));

                                break;
                            case GIFT:
                                levelAmount = marketingVOResponse.getFullGiftLevelList().get(0).getFullAmount();
                                response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));

                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelAmount = BigDecimal.ZERO;
                        }

                        response.setLack(levelAmount);

                    } else if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                        // 满数量
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));

                                break;
                            case DISCOUNT:
                                levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));

                                break;
                            case GIFT:
                                levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));

                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelCount = 0L;
                        }

                        response.setLack(BigDecimal.valueOf(levelCount));

                    } else if (marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER || marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_ORDER ||  marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER ) {

                        // 订单满赠
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
                                levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                break;
                            case DISCOUNT:
                                levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                break;
                            case GIFT:
                                levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                break;
                            default:
                                levelCount = 0L;
                        }
                        response.setLack(BigDecimal.valueOf(levelCount));
                    } else {
                        throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
                    }

                    response.setGoodsInfoList(goodsInfos);
                    response.setDiscount(discount);
                } else {
                    List<GoodsInfoVO> newGoodsInfos = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(checkGoodsInfoVOS)) {
                        //订单满赠不过滤营销商品
                        if (isGifFullOrder(map.getValue())) {
                            // 过滤出参加营销的商品
                            newGoodsInfos = goodsInfoList.stream()
                                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                            && checkSkuIds.contains(goodsInfo.getGoodsInfoId()))
                                    .collect(Collectors.toList());
                            List<GoodsMarketingDTO> goodsMarketingDTOS = goodsMarketings;
                            newGoodsInfos = newGoodsInfos.stream().filter(good ->
                                    goodsMarketingDTOS.stream().filter(g ->
                                            g.getMarketingId().equals(map.getKey())
                                                    && good.getGoodsInfoId().equals(g.getGoodsInfoId())).findFirst().isPresent()).collect(Collectors.toList());

                        } else {
                            // 过滤出参加营销的商品
                            List<MarketingScopeVO> marketingScopeList = marketingVOResponse.getMarketingScopeList();
                            newGoodsInfos = goodsInfoList.stream()
                                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                            && marketingScopeList.stream().anyMatch(scope -> scope.getScopeId().equals(goodsInfo.getGoodsInfoId())))
                                    .collect(Collectors.toList());
                        }
                    }

                    // 计算商品总额
                    List<GoodsIntervalPriceVO> newIntervalPrices = goodsIntervalPrices;
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    for (GoodsInfoVO goodsInfo : newGoodsInfos) {
                        if(marketingsGoodsIds.contains(goodsInfo.getGoodsInfoId()) && isGifFullOrder(map.getValue())){//满订单营销过滤其他营销价格
                            continue;
                        }
                        if (goodsInfo.getPriceType() == GoodsPriceType.STOCK.toValue()) {
                            // 按区间设价，获取满足的多个等级的区间价里的最大价格
                            Optional<GoodsIntervalPriceVO> optional = newIntervalPrices.stream().filter(goodsIntervalPrice
                                    -> goodsIntervalPrice.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                                    .filter(goodsIntervalPrice -> goodsInfo.getBuyCount().compareTo(goodsIntervalPrice.getCount()) >= 0).max(Comparator.comparingLong(GoodsIntervalPriceVO::getCount));

                            if (optional.isPresent()) {
                                totalAmount = totalAmount.add(optional.get().getPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                            } else {
                                totalAmount = BigDecimal.ZERO;
                            }
                        } else if (goodsInfo.getPriceType() == GoodsPriceType.CUSTOMER.toValue()) {
                            // 按级别设价，获取级别价
                            totalAmount = totalAmount.add(goodsInfo.getSalePrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                        } else {
                            if (Objects.nonNull(customer) && Objects.equals(customer.getEnterpriseStatusXyy(), EnterpriseCheckState.CHECKED)
                                    && null != goodsInfo.getVipPrice() && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                                totalAmount = totalAmount.add(goodsInfo.getVipPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                            } else {
                                totalAmount = totalAmount.add(goodsInfo.getMarketPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                            }
                        }
                    }

                    // 计算商品总数
                    Long totalCount = 0L;
                    for (GoodsInfoVO goodsInfo : newGoodsInfos) {
                        totalCount = totalCount + goodsInfo.getBuyCount();
                    }

                    // 满金额
                    if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {
                        // 计算达到营销级别的金额
                        BigDecimal levelAmount;

                        // 根据不用的营销类型，计算满足条件的营销等级里最大的一个，如果不满足营销等级里任意一个，则默认取最低等级
                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
//                            Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
//                                    marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount));
                                MarketingFullReductionLevelVO fullReductionLevelVO = new MarketingFullReductionLevelVO();
                                for (MarketingFullReductionLevelVO level : marketingVOResponse.getFullReductionLevelList()) {
                                    if (level.getFullAmount().compareTo(totalAmount) <= 0 &&
                                            (Objects.isNull(fullReductionLevelVO.getFullAmount()) ||
                                                    fullReductionLevelVO.getFullAmount().compareTo(level.getFullAmount()) < 0)) {
                                        fullReductionLevelVO = level;
                                    }
                                }
                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullReductionLevelList())){
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                }
                                // 满足条件的最大的等级
                                if (Objects.nonNull(fullReductionLevelVO.getFullAmount())) {
                                    levelAmount = fullReductionLevelVO.getFullAmount();
                                    discount = fullReductionLevelVO.getReduction();
                                    response.setFullReductionLevel(fullReductionLevelVO);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelAmount = marketingVOResponse.getFullReductionLevelList().get(0).getFullAmount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                }
                                // 是否满足最大等级
                                MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount)).get();
                                boolean isMax = maxLevel.getFullAmount().compareTo(totalAmount) < 0;
                                // 满金额减最大等级且可叠加计算折扣
                                if (MarketingSubType.REDUCTION_FULL_AMOUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                    discount = totalAmount.divide(levelAmount, 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                }

                                break;
                            case DISCOUNT:
//                            Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
//                                    marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullAmount));
                                MarketingFullDiscountLevelVO fullDiscountLevelVO = new MarketingFullDiscountLevelVO();
                                for (MarketingFullDiscountLevelVO level : marketingVOResponse.getFullDiscountLevelList()) {
                                    if (level.getFullAmount().compareTo(totalAmount) <= 0 &&
                                            (Objects.isNull(fullDiscountLevelVO.getFullAmount()) ||
                                                    fullDiscountLevelVO.getFullAmount().compareTo(level.getFullAmount()) < 0)) {
                                        fullDiscountLevelVO = level;
                                    }
                                }
                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullDiscountLevelList())){
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());
                                }
                                // 满足条件的最大的等级
                                if (Objects.nonNull(fullDiscountLevelVO.getFullAmount())) {
                                    levelAmount = fullDiscountLevelVO.getFullAmount();
                                    discount = fullDiscountLevelVO.getDiscount();
                                    response.setFullDiscountLevel(fullDiscountLevelVO);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelAmount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullAmount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                }

                                break;
                            case GIFT:
//                            List<MarketingFullGiftLevelVO> levels =
//                                    marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0).collect(Collectors.toList());

                                MarketingFullGiftLevelVO giftLevelVO = new MarketingFullGiftLevelVO();
                                for (MarketingFullGiftLevelVO level : marketingVOResponse.getFullGiftLevelList()) {
                                    if (level.getFullAmount().compareTo(totalAmount) <= 0 &&
                                            (Objects.isNull(giftLevelVO.getFullAmount()) ||
                                                    giftLevelVO.getFullAmount().compareTo(level.getFullAmount()) < 0)) {
                                        giftLevelVO = level;
                                    }
                                }
                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullGiftLevelList())){
                                        response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                }
//                            Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
//                                    levels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount));

                                // 满足条件的最大的等级
                                if (Objects.nonNull(giftLevelVO.getFullAmount())) {
                                    levelAmount = giftLevelVO.getFullAmount();
                                    response.setFullGiftLevel(giftLevelVO);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelAmount = marketingVOResponse.getFullGiftLevelList().get(0).getFullAmount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                }
                                // 最大赠级别
                                MarketingFullGiftLevelVO maxLevelVO = marketingVOResponse.getFullGiftLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount)).get();
                                // 是否满足最大等级
                                boolean isMaxLevel = maxLevelVO.getFullAmount().compareTo(totalAmount) < 0;
                                // 满金额赠可叠加且是最大级别计算赠品数量
                                if (MarketingSubType.GIFT_FULL_AMOUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMaxLevel) {
                                    // 如果是赠品数量是叠加倍数，赠品营销只取最大级别,计算赠品数量
                                    Long multiple = totalAmount.divide(maxLevelVO.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                                    if (multiple.compareTo(1L) > 0) {
                                        for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                            // 计算赠品数量
                                            detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                        }
                                        List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                        levelVOS.add(maxLevelVO);
                                        marketingVOResponse.setFullGiftLevelList(levelVOS);
                                    }
                                }
                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelAmount = BigDecimal.ZERO;
                        }

                        // 计算达到营销级别的差额
                        lackAmount = levelAmount.compareTo(totalAmount) <= 0 ? BigDecimal.ZERO :
                                levelAmount.subtract(totalAmount);

                        response.setLack(lackAmount);

                    } else if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                        // 满数量
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:
//                            Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
//                                    marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount));
                                MarketingFullReductionLevelVO marketingFullReductionLevelVO = new MarketingFullReductionLevelVO();
                                for (MarketingFullReductionLevelVO level : marketingVOResponse.getFullReductionLevelList()) {
                                    if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                            (Objects.isNull(marketingFullReductionLevelVO.getFullCount()) ||
                                                    marketingFullReductionLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                        marketingFullReductionLevelVO = level;
                                    }
                                }
                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullReductionLevelList())){
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                }

                                // 满足条件的最大的等级
                                if (Objects.nonNull(marketingFullReductionLevelVO.getFullCount())) {
                                    levelCount = marketingFullReductionLevelVO.getFullCount();
                                    discount = marketingFullReductionLevelVO.getReduction();
                                    response.setFullReductionLevel(marketingFullReductionLevelVO);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                }
                                // 是否满足最大等级
                                MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                                boolean isMax = maxLevel.getFullCount().compareTo(totalCount) < 0;
                                // 满数量减可叠加且满足最大级计算折扣
                                if (MarketingSubType.REDUCTION_FULL_COUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                    discount = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(levelCount), 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                }

                                break;
                            case DISCOUNT:
//                            Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
//                                    marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount));

                                MarketingFullDiscountLevelVO discountLevelVO = new MarketingFullDiscountLevelVO();
                                for (MarketingFullDiscountLevelVO level : marketingVOResponse.getFullDiscountLevelList()) {
                                    if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                            (Objects.isNull(discountLevelVO.getFullCount()) ||
                                                    discountLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                        discountLevelVO = level;
                                    }
                                }
                                // 满足条件的最大的等级
                                if (Objects.nonNull(discountLevelVO.getFullCount())) {
                                    levelCount = discountLevelVO.getFullCount();
                                    discount = discountLevelVO.getDiscount();
                                    response.setFullDiscountLevel(discountLevelVO);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                }

                                break;
                            case GIFT:
//                            List<MarketingFullGiftLevelVO> giftLevels =
//                                    marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());
//
//                            Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
//                                    giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));
                                MarketingFullGiftLevelVO giftLevel = new MarketingFullGiftLevelVO();
                                for (MarketingFullGiftLevelVO level : marketingVOResponse.getFullGiftLevelList()) {
                                    if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                            (Objects.isNull(giftLevel.getFullCount()) ||
                                                    giftLevel.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                        giftLevel = level;
                                    }
                                }

                                // 满足条件的最大的等级
                                if (Objects.nonNull(giftLevel.getFullCount())) {
                                    levelCount = giftLevel.getFullCount();
                                    response.setFullGiftLevel(giftLevel);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                }
                                // 最大赠级别
                                MarketingFullGiftLevelVO maxLevelVO = marketingVOResponse.getFullGiftLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount)).get();
                                // 是否满足最大等级
                                boolean isMaxLevel = maxLevelVO.getFullCount().compareTo(totalCount) < 0;
                                // 满数量赠可叠加且满足最大级优惠计算赠品数量
                                if (MarketingSubType.GIFT_FULL_COUNT.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMaxLevel) {
                                    // 如果是赠品数量是叠加倍数，赠品营销只取最大级别, 计算赠品数量
                                    Long multiple = BigDecimal.valueOf(totalCount)
                                            .divide(BigDecimal.valueOf(maxLevelVO.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                                    if (multiple.compareTo(1L) > 0) {
                                        for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                            // 计算赠品数量
                                            detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                        }
                                        List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                        levelVOS.add(maxLevelVO);
                                        marketingVOResponse.setFullGiftLevelList(levelVOS);
                                    }
                                }

                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                break;
                            default:
                                levelCount = 0L;
                        }

                        // 计算达到营销级别缺少的数量
                        lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                                levelCount.longValue() - totalCount.longValue();

                        response.setLack(BigDecimal.valueOf(lackCount));

                    } else if (marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER || marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_ORDER ||  marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER) {
                        // 订单满系
                        // 计算达到营销级别的数量
                        Long levelCount;

                        switch (marketingVOResponse.getMarketingType()) {
                            case REDUCTION:     //订单满减
//                            Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
//                                    marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount));
                                MarketingFullReductionLevelVO marketingFullReductionLevelVO = new MarketingFullReductionLevelVO();
                                for (MarketingFullReductionLevelVO level : marketingVOResponse.getFullReductionLevelList()) {
                                    if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                            (Objects.isNull(marketingFullReductionLevelVO.getFullCount()) ||
                                                    marketingFullReductionLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                        marketingFullReductionLevelVO = level;
                                    }
                                }
                                if(CollectionUtils.isNotEmpty(marketingVOResponse.getFullReductionLevelList())){
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                }
                                // 满足条件的最大的等级
                                if (Objects.nonNull(marketingFullReductionLevelVO.getFullCount())) {
                                    levelCount = marketingFullReductionLevelVO.getFullCount();
                                    discount = marketingFullReductionLevelVO.getReduction();
                                    response.setFullReductionLevel(marketingFullReductionLevelVO);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                }
                                // 是否满足最大等级
                                MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                        .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                                boolean isMax = maxLevel.getFullCount().compareTo(totalCount) < 0;
                                // 满数量减可叠加且满足最大级计算折扣
                                if (MarketingSubType.REDUCTION_FULL_ORDER.equals(marketingVOResponse.getSubType()) &&
                                        BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                    discount = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(levelCount), 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                }

                                break;
                            case DISCOUNT:  //订单满折
//                            Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
//                                    marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount));

                                MarketingFullDiscountLevelVO discountLevelVO = new MarketingFullDiscountLevelVO();
                                for (MarketingFullDiscountLevelVO level : marketingVOResponse.getFullDiscountLevelList()) {
                                    if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                            (Objects.isNull(discountLevelVO.getFullCount()) ||
                                                    discountLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                        discountLevelVO = level;
                                    }
                                }
                                // 满足条件的最大的等级
                                if (Objects.nonNull(discountLevelVO.getFullCount())) {
                                    levelCount = discountLevelVO.getFullCount();
                                    discount = discountLevelVO.getDiscount();
                                    response.setFullDiscountLevel(discountLevelVO);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                }
                                break;
                            case GIFT:
//                            List<MarketingFullGiftLevelVO> giftLevels =
//                                    marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());
//
//                            Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
//                                    giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));
                                MarketingFullGiftLevelVO giftLevel = new MarketingFullGiftLevelVO();
                                for (MarketingFullGiftLevelVO level : marketingVOResponse.getFullGiftLevelList()) {
                                    if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                            (Objects.isNull(giftLevel.getFullCount()) ||
                                                    giftLevel.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                        giftLevel = level;
                                    }
                                }
                                // 满足条件的最大的等级
                                if (Objects.nonNull(giftLevel.getFullCount())) {
                                    levelCount = giftLevel.getFullCount();
                                    response.setFullGiftLevel(giftLevel);
                                } else {
                                    // 没有满足条件，默认取最低等级
                                    levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                }
                                response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                break;
                            default:
                                levelCount = 0L;
                        }
                        // 计算达到营销级别缺少的数量
                        lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                                levelCount.longValue() - totalCount.longValue();
                        response.setLack(BigDecimal.valueOf(lackCount));
                    } else {
                        throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
                    }

                    response.setGoodsInfoList(newGoodsInfos);
                    response.setTotalCount(totalCount);
                    response.setTotalAmount(totalAmount);
                    response.setDiscount(discount);
//            }
                }

                sb.append(",marketingGoodsMap.foreach end time=");
                sb.append(System.currentTimeMillis()-sTm);

                // 如果有赠品，则查询赠品商品的详细信息
//            if (response.getFullGiftLevelList() != null && !response.getFullGiftLevelList().isEmpty()) {
//                List<String> skuIds =
//                        response.getFullGiftLevelList().stream().flatMap(marketingFullGiftLevel -> marketingFullGiftLevel.getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId)).distinct().collect(Collectors.toList());
//                GoodsInfoViewByIdsRequest goodsInfoViewRequest = GoodsInfoViewByIdsRequest.builder()
//                        .goodsInfoIds(skuIds)
//                        .isHavSpecText(Constants.yes)
//                        .wareId(wareId)
//                        .build();
//                Long currentTime5 = System.currentTimeMillis();
////                response.setGiftGoodsInfoResponse(KsBeanUtil.convert(goodsInfoQueryProvider.listViewByIds(goodsInfoViewRequest).getContext(), PurchaseGoodsViewVO.class));
//                System.out.println("dddddddddddddd 查询赠品:" + marketingVOResponse.getMarketingId() + (System.currentTimeMillis() - currentTime5));
//            }

                response.setStoreId(marketingVOResponse.getStoreId());
                response.setMarketingId(marketingVOResponse.getMarketingId());
                response.setMarketingType(marketingVOResponse.getMarketingType());
                response.setSubType(marketingVOResponse.getSubType());
                response.setIsOverlap(marketingVOResponse.getIsOverlap());
                response.setGoodsInfoIds(map.getValue());


                responseMap.put(marketingVOResponse.getMarketingId(), response);
            }
            return responseMap;
        }finally {
            log.info(sb.toString());
        }

    }

    /**
     * 获取采购营销信息及同步商品营销Init
     * @param goodsInfoIdList
     * @param goodsInfos
     * @param customer
     * @param wareId
     * @return
     */
    public PurchaseMarketingResponse getPurchasesMarketingInit(List<String> goodsInfoIdList, List<GoodsInfoVO> goodsInfos, CustomerVO customer, Long wareId) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase getPurchasesMarketing start");

        try {
            //获取商品营销信息
            PurchaseGetGoodsMarketingResponse goodsMarketing = this.getGoodsMarketing(goodsInfos, customer, wareId);

            sb.append(",getGoodsMarketing end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            PurchaseMarketingResponse marketingResponse = KsBeanUtil.convert(goodsMarketing, PurchaseMarketingResponse.class);
            // 同步商品使用的营销
            List<GoodsMarketingVO> goodsMarketingVOS = this.syncGoodsMarketings(goodsMarketing.getMap(), customer.getCustomerId());

            sb.append(",syncGoodsMarketings end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            marketingResponse.setGoodsMarketings(goodsMarketingVOS);
            List<GoodsMarketingDTO> allMarketing=new ArrayList<>(40);
            for (Map.Entry<String, List<MarketingViewVO>> entry :  marketingResponse.getMap().entrySet()) {
                List<MarketingViewVO> value = entry.getValue();
                for (MarketingViewVO marketingViewVO : value) {
                    GoodsMarketingDTO goodsMarketingVO = new GoodsMarketingDTO();
                    goodsMarketingVO.setMarketingId(marketingViewVO.getMarketingId());
                    goodsMarketingVO.setGoodsInfoId(entry.getKey());
                    allMarketing.add(goodsMarketingVO);
                }
            }
            //商品选中的营销
            Map<String, GoodsMarketingVO> selectMarketingGoodsMap = goodsMarketingVOS.stream().collect(Collectors.toMap(GoodsMarketingVO::getGoodsInfoId, g -> g));
            // 获取店铺营销信息
            if (CollectionUtils.isNotEmpty(allMarketing) ) {
//                List<GoodsInfoVO> reqGoodsInfo = goodsInfos.stream().filter(param -> goodsInfoIdList.contains(param.getGoodsInfoId())).collect(Collectors.toList());
               // List<GoodsInfoVO> reqGoodsInfosFilter = goodsInfos.stream().filter(param -> param.getGoodsInfoType() == 0).collect(Collectors.toList());
                List<String> reqGoodsInfoIds = goodsInfos.stream().map(GoodsInfoVO::getGoodsInfoId)
                        .collect(Collectors.toList());
                Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketing = this.getStoreMarketingBaseInit(allMarketing,
                        customer, new PurchaseFrontRequest(), reqGoodsInfoIds, wareId, goodsInfos,goodsInfoIdList,selectMarketingGoodsMap);


                sb.append(",getStoreMarketingBase end time=");
                sb.append(System.currentTimeMillis()-sTm);

                HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                storeMarketing.forEach((k, v) -> {
                    map.put(k, KsBeanUtil.convertList(v, PurchaseMarketingCalcVO.class));
                });
                marketingResponse.setStoreMarketingMap(map);
            }
            return marketingResponse;
        }finally {
            log.info(sb.toString());
        }

    }

    /**
     * [公共方法]获取店铺营销信息
     * 同时处理未登录 / 已登录时的逻辑
     *
     * @param goodsMarketings 商品营销信息
     * @param customer        登录人信息
     * @param frontReq        前端传入的采购单信息
     * @param goodsInfoIdList 勾选的skuIdList
     * @return
     */
    private Map<Long, List<PurchaseMarketingCalcResponse>> getStoreMarketingBaseInit(List<GoodsMarketingDTO> goodsMarketings,
                                                                                CustomerVO customer,
                                                                                PurchaseFrontRequest frontReq,
                                                                                List<String> goodsInfoIdList,
                                                                                Long wareId, List<GoodsInfoVO> goodsInfoVOS,
                                                                                     List<String> selectSkuids,
                                                                                     Map<String, GoodsMarketingVO> selectMarketingGoodsMap) {
        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase getStoreMarketingBase start");

        try {
            goodsInfoIdList.add("all");
            Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketingMap = new HashMap<>();

            if (CollectionUtils.isEmpty(goodsMarketings)) {
                return storeMarketingMap;
            }

            // 参加同种营销的商品列表
            Map<Long, List<String>> marketingGoodsesMap = new HashMap<>();
            Map<Long, List<GoodsMarketingDTO>> longListMap = IteratorUtils.groupBy(goodsMarketings, GoodsMarketingDTO::getMarketingId);
            for (Map.Entry<Long, List<GoodsMarketingDTO>> set : longListMap.entrySet()) {
                List<String> filterGoodsInfoIds = new ArrayList<>();
                for (GoodsMarketingDTO goodsMarketing : set.getValue()) {
                    if (goodsInfoIdList != null && goodsInfoIdList.contains(goodsMarketing.getGoodsInfoId())) {
                        filterGoodsInfoIds.add(goodsMarketing.getGoodsInfoId());
                    }
                }
                marketingGoodsesMap.put(set.getKey(), filterGoodsInfoIds);
            }
            // 查询所有营销信息
            List<Long> marketingIds = new ArrayList<>(marketingGoodsesMap.keySet());
            MarketingQueryByIdsRequest marketingQueryByIdsRequest = new MarketingQueryByIdsRequest();
            marketingQueryByIdsRequest.setMarketingIds(marketingIds);

            sb.append(",marketingQueryProvider.getByIdsForCustomer start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            Map<Long, MarketingForEndVO> marketingMap = marketingQueryProvider.getByIdsForCustomer(marketingQueryByIdsRequest).getContext().getMarketingForEndVOS().stream()
                    .collect(Collectors.toMap(MarketingVO::getMarketingId, Function.identity()));

            sb.append(",marketingQueryProvider.getByIdsForCustomer end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();


            List<String> goodsInfoIds = new ArrayList<>();
            // 根据同种营销的商品列表，计算营销
            for(List<String> mapGoodsInfoIds : marketingGoodsesMap.values()) {
                for (String mapInfoId : mapGoodsInfoIds) {
                    goodsInfoIds.add(mapInfoId);
                }
            }
            //选择默认营销，排除已参加其他活动的商品的营销
            Map<Long, Map<String,List<String>>> visitMarketingGoodsesMap = new HashMap<>();
            for (Map.Entry<Long, List<String>> entry:marketingGoodsesMap.entrySet()){//
                List<String> skus = entry.getValue();
                List<String> visitSkus=new ArrayList<>(10);
                List<String> noVisitSkus=new ArrayList<>(10);
                for (String s : skus) {
                    if (Objects.nonNull(selectMarketingGoodsMap.get(s))){
                        GoodsMarketingVO goodsMarketingVO = selectMarketingGoodsMap.get(s);
                        if (goodsMarketingVO.getMarketingId().equals(entry.getKey())){
                            visitSkus.add(s);
                        }else {
                            noVisitSkus.add(s);
                        }
                    }
                }
                Map<String,List<String>> visitMap=new HashedMap();
                if (CollectionUtils.isNotEmpty(visitSkus)){
                    visitMap.put(AbstractOrderConstant.VISIT,visitSkus);
                }
                if (CollectionUtils.isNotEmpty(noVisitSkus)){
                    visitMap.put(AbstractOrderConstant.NOT_VISIT,noVisitSkus);
                }
                visitMarketingGoodsesMap.put(entry.getKey(), visitMap);
            }
            Map<Long, PurchaseMarketingCalcResponse> responseMap = new HashedMap();
            if (customer != null) {
              /*  if (Objects.nonNull(goodsInfoVOS) && goodsInfoVOS.size() > 0) {*/
                    responseMap = this.simpCalcMarketingByMarketingIdInit(visitMarketingGoodsesMap, customer, null,
                            goodsInfoIds, true, goodsInfoIdList, marketingMap, goodsInfoVOS, goodsMarketings,selectSkuids,marketingGoodsesMap);

                    sb.append(",simpCalcMarketingByMarketingId end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
               /* } else {
                    responseMap = this.calcMarketingByMarketingIdBaseList(marketingGoodsesMap, customer, null,
                            goodsInfoIds, true, goodsInfoIdList, wareId, marketingMap);

                    sb.append(",customer nonNull calcMarketingByMarketingIdBaseList end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                }*/


            } else {
                responseMap = this.calcMarketingByMarketingIdBaseList(marketingGoodsesMap, customer, frontReq,
                        goodsInfoIds, true, goodsInfoIdList, wareId, marketingMap);

                sb.append(",customer null calcMarketingByMarketingIdBaseList end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();
            }
            for(Map.Entry<Long, List<String>> set : marketingGoodsesMap.entrySet()) {
                if(CollectionUtils.isNotEmpty(set.getValue())) {
//                if (customer != null) {
//                    response = this.calcMarketingByMarketingIdBase(Long.valueOf(set.getKey()), customer, null,
//                            set.getValue(), true,goodsInfoIdList, wareId );
//                } else {
//                    response = this.calcMarketingByMarketingIdBase(Long.valueOf(set.getKey()), customer, frontReq,
//                            set.getValue(), true,goodsInfoIdList, wareId);
//                }
                    PurchaseMarketingCalcResponse response = responseMap.get(set.getKey());
                   if (Objects.nonNull(response)){
                       if (storeMarketingMap.get(response.getStoreId()) == null) {
                           List<PurchaseMarketingCalcResponse> calcResponses = new ArrayList<>();
                           calcResponses.add(response);

                           storeMarketingMap.put(response.getStoreId(), calcResponses);
                       } else {
                           storeMarketingMap.get(response.getStoreId()).add(response);
                       }
                   }
                }
            }

            // 营销按减>折>赠排序
            for (Map.Entry<Long, List<PurchaseMarketingCalcResponse>> set : storeMarketingMap.entrySet()) {
                set.getValue().sort(Comparator.comparing(PurchaseMarketingCalcResponse::getMarketingType));
            }

            sb.append(",getStoreMarketingBase end time=");
            sb.append(System.currentTimeMillis()-sTm);

            return storeMarketingMap;
        }finally {
            log.info(sb.toString());
        }

    }

    private Map<Long, PurchaseMarketingCalcResponse> simpCalcMarketingByMarketingIdInit(Map<Long, Map<String,List<String>>> marketingGoodsMap,
                                                                                   CustomerVO customer,
                                                                                   PurchaseFrontRequest frontReq,
                                                                                   List<String> goodsInfoIds,
                                                                                   boolean isPurchase,
                                                                                   List<String> checkSkuIds,
                                                                                   Map<Long, MarketingForEndVO> marketingMap,
                                                                                   List<GoodsInfoVO> checkGoodsInfoVOS,
                                                                                   List<GoodsMarketingDTO> goodsMarketings,
                                                                                        List<String> selectSkuids, Map<Long, List<String>> marketingGoodsesMap) {
        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase simpCalcMarketingByMarketingId start");

        /* List<Long> storeIds = marketingMap.values().stream()
                .map(MarketingVO::getStoreId)
                .collect(Collectors.toList());*/
        // 获取用户在店铺里的等级
        /* Map<Long, CommonLevelVO> levelMap = this.getLevelMapByStoreIds(storeIds, customer);*/

        try {
            List<GoodsIntervalPriceVO> goodsIntervalPrices = new ArrayList<>();
            List<GoodsInfoVO> goodsInfos = Collections.EMPTY_LIST;

                if (customer != null) {
                    //这里增加了一个逻辑，所有购物车的商品都可参加订单满赠的活动
                    List<String> skuIds = goodsInfoIds;
                    if (isGifFullOrder(goodsInfoIds)) {
                        skuIds = Arrays.asList();
                    }
                    PurchaseRequest request = PurchaseRequest.builder()
                            .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
                            .goodsInfoIds(skuIds)
                            .build();
                    List<Purchase> follows;
                    Sort sort = request.getSort();
                    if (Objects.nonNull(sort)) {
                        follows = purchaseRepository.findAll(request.getWhereCriteria(), sort);
                    } else {
                        follows = purchaseRepository.findAll(request.getWhereCriteria());
                    }

                    sb.append(",purchaseRepository.findAll end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();

                    //填充SKU的购买数
                    this.fillBuyCountInit(checkGoodsInfoVOS, follows,selectSkuids);

                    sb.append(",fillBuyCount end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    if (checkGoodsInfoVOS != null) {
                        for (GoodsInfoVO goodsInfo : checkGoodsInfoVOS) {
                            goodsInfo.setBuyCount(0L);
                            List<PurchaseGoodsInfoDTO> dtoList = new ArrayList<>();
                            for (PurchaseGoodsInfoDTO purchase : frontReq.getGoodsInfoDTOList()) {
                                if (goodsInfo.getGoodsInfoId().equals(purchase.getGoodsInfoId())) {
                                    dtoList.add(purchase);
                                }
                            }
                            if (CollectionUtils.isNotEmpty(dtoList)) {
                                goodsInfo.setBuyCount((dtoList.get(0)).getGoodsNum());
                            }
                        }
                    }
                }
                goodsInfos = checkGoodsInfoVOS;
                //设定SKU状态
                fillGoodsStatus(checkGoodsInfoVOS);

                sb.append(",fillGoodsStatus end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                if (CollectionUtils.isNotEmpty(checkGoodsInfoVOS) && customer == null) {
                    goodsMarketings = frontReq.getGoodsMarketingDTOList();
                }

                //计算区间价
                GoodsIntervalPriceByCustomerIdResponse intervalPriceResponse =
                        goodsIntervalPriceProvider.putByCustomerId(
                                GoodsIntervalPriceByCustomerIdRequest.builder()
                                        .goodsInfoDTOList(KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class))
                                        .customerId(customer != null ? customer.getCustomerId() : null).build()).getContext();

                sb.append(",goodsIntervalPriceProvider.putByCustomerId end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                goodsIntervalPrices = intervalPriceResponse.getGoodsIntervalPriceVOList();
                goodsInfos = intervalPriceResponse.getGoodsInfoVOList();

                //计算级别价格
               /* goodsInfos = marketingLevelPluginProvider.goodsListFilter(
                        MarketingLevelGoodsListFilterRequest.builder()
                                .customerDTO(customer != null ? KsBeanUtil.convert(customer, CustomerDTO.class) : null)
                                .goodsInfos(KsBeanUtil.convert(goodsInfos, GoodsInfoDTO.class)).build())
                        .getContext().getGoodsInfoVOList();*/

                sb.append(",marketingLevelPluginProvider.goodsListFilter end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

            Map<String, GoodsInfoVO> goodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
            Map<Long, PurchaseMarketingCalcResponse> responseMap = new HashedMap();

            sb.append(",marketingGoodsMap.foreach start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<String> marketingsGoodsIds=goodsMarketings.stream().map(GoodsMarketingDTO::getGoodsInfoId).collect(Collectors.toList());

            for (Map.Entry<Long, Map<String,List<String>>> outMap : marketingGoodsMap.entrySet()) {
                Map<String, List<String>> value = outMap.getValue();
                if (Objects.isNull(value)){
                    continue;
                }
                for (Map.Entry<String,List<String>> map : value.entrySet()) {
                    if (CollectionUtils.isEmpty(map.getValue())){
                        continue;
                    }
                    List<GoodsInfoVO> goodsInfoListInit = map.getValue().stream()
                            .map(goodsInfoVOMap::get)
                            .collect(Collectors.toList());
                    goodsInfoListInit.removeIf(Objects::isNull);
                    List<GoodsInfoVO> goodsInfoList = KsBeanUtil.convert(goodsInfoListInit, GoodsInfoVO.class);
                    PurchaseMarketingCalcResponse response = new PurchaseMarketingCalcResponse();
                    if (map.getKey().equals(AbstractOrderConstant.NOT_VISIT)){
                        goodsInfoList.forEach(param->{
                            param.setBuyCount(0L);
                        });
                    }
                    // 查询该营销活动参与的商品列表
                    MarketingForEndVO marketingVOResponse = marketingMap.get(outMap.getKey());

           /* if (!validMarketing(marketingVOResponse, levelMap)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, marketingVOResponse.getMarketingName() + "无效！");
            }*/
                    //校验营销活动
                    if (marketingVOResponse.getIsPause() == BoolFlag.YES || marketingVOResponse.getDelFlag() == DeleteFlag.YES || marketingVOResponse.getBeginTime().isAfter(LocalDateTime.now())
                            || marketingVOResponse.getEndTime().isBefore(LocalDateTime.now())) {
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, marketingVOResponse.getMarketingName() + "无效！");
                    }

                    long lackCount = 0L;
                    BigDecimal lackAmount = BigDecimal.ZERO;
                    BigDecimal discount = BigDecimal.ZERO;

                    // 采购单勾选的商品中没有参与该营销的情况，取营销的最低等级
                    if (isPurchase && (map.getValue() == null || map.getValue().isEmpty())) {
                        // 满金额
                        if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {

                            BigDecimal levelAmount;

                            switch (marketingVOResponse.getMarketingType()) {
                                case REDUCTION:
                                    levelAmount = marketingVOResponse.getFullReductionLevelList().get(0).getFullAmount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());

                                    break;
                                case DISCOUNT:
                                    levelAmount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullAmount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());

                                    break;
                                case GIFT:
                                    levelAmount = marketingVOResponse.getFullGiftLevelList().get(0).getFullAmount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                    response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                    break;
                                default:
                                    levelAmount = BigDecimal.ZERO;
                            }

                            response.setLack(levelAmount);

                        } else if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                            // 满数量
                            // 计算达到营销级别的数量
                            Long levelCount;

                            switch (marketingVOResponse.getMarketingType()) {
                                case REDUCTION:
                                    levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                    break;
                                case DISCOUNT:
                                    levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());
                                    break;
                                case GIFT:
                                    levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));

                                    response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                    break;
                                default:
                                    levelCount = 0L;
                            }

                            response.setLack(BigDecimal.valueOf(levelCount));
                        } else if (marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER || marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_ORDER ||  marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER ) {

                            // 订单满赠
                            // 计算达到营销级别的数量
                            Long levelCount;

                            switch (marketingVOResponse.getMarketingType()) {
                                case REDUCTION:
                                    levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                    response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                    break;
                                case DISCOUNT:
                                    levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                    discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                    response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());
                                    break;
                                case GIFT:
                                    levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                    response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                    response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                    break;
                                default:
                                    levelCount = 0L;
                            }
                            response.setLack(BigDecimal.valueOf(levelCount));
                        } else {
                            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
                        }

                        response.setGoodsInfoList(goodsInfos);
                        response.setDiscount(discount);
                    } else {
                        List<GoodsInfoVO> newGoodsInfos = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(checkGoodsInfoVOS)) {
                            //订单满赠不过滤营销商品
                            if (isGifFullOrder(map.getValue())) {
                                // 过滤出参加营销的商品
                                newGoodsInfos = goodsInfoList.stream()
                                        .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                                && checkSkuIds.contains(goodsInfo.getGoodsInfoId()))
                                        .collect(Collectors.toList());
                                List<GoodsMarketingDTO> goodsMarketingDTOS = goodsMarketings;
                                newGoodsInfos = newGoodsInfos.stream().filter(good ->
                                        goodsMarketingDTOS.stream().filter(g ->
                                                g.getMarketingId().equals(outMap.getKey())
                                                        && good.getGoodsInfoId().equals(g.getGoodsInfoId())).findFirst().isPresent()).collect(Collectors.toList());

                            } else {
                                // 过滤出参加营销的商品
                                List<MarketingScopeVO> marketingScopeList = marketingVOResponse.getMarketingScopeList();
                                newGoodsInfos = goodsInfoList.stream()
                                        .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                                                && marketingScopeList.stream().anyMatch(scope -> scope.getScopeId().equals(goodsInfo.getGoodsInfoId())))
                                        .collect(Collectors.toList());
                            }
                        }

                        // 计算商品总额
                        List<GoodsIntervalPriceVO> newIntervalPrices = goodsIntervalPrices;
                        BigDecimal totalAmount = BigDecimal.ZERO;
                        for (GoodsInfoVO goodsInfo : newGoodsInfos) {
                            if(marketingsGoodsIds.contains(goodsInfo.getGoodsInfoId()) && isGifFullOrder(map.getValue())){//满订单营销过滤其他营销价格
                                continue;
                            }
                            if (goodsInfo.getPriceType() == GoodsPriceType.STOCK.toValue()) {
                                // 按区间设价，获取满足的多个等级的区间价里的最大价格
                                Optional<GoodsIntervalPriceVO> optional = newIntervalPrices.stream().filter(goodsIntervalPrice
                                        -> goodsIntervalPrice.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                                        .filter(goodsIntervalPrice -> goodsInfo.getBuyCount().compareTo(goodsIntervalPrice.getCount()) >= 0).max(Comparator.comparingLong(GoodsIntervalPriceVO::getCount));

                                if (optional.isPresent()) {
                                    totalAmount = totalAmount.add(optional.get().getPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                                } else {
                                    totalAmount = BigDecimal.ZERO;
                                }
                            } else if (goodsInfo.getPriceType() == GoodsPriceType.CUSTOMER.toValue()) {
                                // 按级别设价，获取级别价
                                totalAmount = totalAmount.add(goodsInfo.getSalePrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                            } else {
                                if (Objects.nonNull(customer) && Objects.equals(customer.getEnterpriseStatusXyy(), EnterpriseCheckState.CHECKED)
                                        && null != goodsInfo.getVipPrice() && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                                    totalAmount = totalAmount.add(goodsInfo.getVipPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                                } else {
                                    totalAmount = totalAmount.add(goodsInfo.getMarketPrice().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())));
                                }
                            }
                        }

                        // 计算商品总数
                        Long totalCount = 0L;
                        for (GoodsInfoVO goodsInfo : newGoodsInfos) {
                            totalCount = totalCount + goodsInfo.getBuyCount();
                        }

                        // 满金额
                        if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {
                            // 计算达到营销级别的金额
                            BigDecimal levelAmount;

                            // 根据不用的营销类型，计算满足条件的营销等级里最大的一个，如果不满足营销等级里任意一个，则默认取最低等级
                            switch (marketingVOResponse.getMarketingType()) {
                                case REDUCTION:
//                            Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
//                                    marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount));
                                    MarketingFullReductionLevelVO fullReductionLevelVO = new MarketingFullReductionLevelVO();
                                    for (MarketingFullReductionLevelVO level : marketingVOResponse.getFullReductionLevelList()) {
                                        if (level.getFullAmount().compareTo(totalAmount) <= 0 &&
                                                (Objects.isNull(fullReductionLevelVO.getFullAmount()) ||
                                                        fullReductionLevelVO.getFullAmount().compareTo(level.getFullAmount()) < 0)) {
                                            fullReductionLevelVO = level;
                                        }
                                    }

                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(fullReductionLevelVO.getFullAmount())) {
                                        levelAmount = fullReductionLevelVO.getFullAmount();
                                        discount = fullReductionLevelVO.getReduction();
                                        response.setFullReductionLevel(fullReductionLevelVO);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelAmount = marketingVOResponse.getFullReductionLevelList().get(0).getFullAmount();
                                        discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                        response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                    }
                                    // 是否满足最大等级
                                    MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount)).get();
                                    boolean isMax = maxLevel.getFullAmount().compareTo(totalAmount) < 0;
                                    // 满金额减最大等级且可叠加计算折扣
                                    if (MarketingSubType.REDUCTION_FULL_AMOUNT.equals(marketingVOResponse.getSubType()) &&
                                            BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                        discount = totalAmount.divide(levelAmount, 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                    }
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                    break;
                                case DISCOUNT:
//                            Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
//                                    marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullAmount));
                                    MarketingFullDiscountLevelVO fullDiscountLevelVO = new MarketingFullDiscountLevelVO();
                                    for (MarketingFullDiscountLevelVO level : marketingVOResponse.getFullDiscountLevelList()) {
                                        if (level.getFullAmount().compareTo(totalAmount) <= 0 &&
                                                (Objects.isNull(fullDiscountLevelVO.getFullAmount()) ||
                                                        fullDiscountLevelVO.getFullAmount().compareTo(level.getFullAmount()) < 0)) {
                                            fullDiscountLevelVO = level;
                                        }
                                    }
                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(fullDiscountLevelVO.getFullAmount())) {
                                        levelAmount = fullDiscountLevelVO.getFullAmount();
                                        discount = fullDiscountLevelVO.getDiscount();
                                        response.setFullDiscountLevel(fullDiscountLevelVO);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelAmount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullAmount();
                                        discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                        response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                    }
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());
                                    break;
                                case GIFT:
//                            List<MarketingFullGiftLevelVO> levels =
//                                    marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullAmount().compareTo(totalAmount) <= 0).collect(Collectors.toList());

                                    MarketingFullGiftLevelVO giftLevelVO = new MarketingFullGiftLevelVO();
                                    for (MarketingFullGiftLevelVO level : marketingVOResponse.getFullGiftLevelList()) {
                                        if (level.getFullAmount().compareTo(totalAmount) <= 0 &&
                                                (Objects.isNull(giftLevelVO.getFullAmount()) ||
                                                        giftLevelVO.getFullAmount().compareTo(level.getFullAmount()) < 0)) {
                                            giftLevelVO = level;
                                        }
                                    }

//                            Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
//                                    levels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount));

                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(giftLevelVO.getFullAmount())) {
                                        levelAmount = giftLevelVO.getFullAmount();
                                        response.setFullGiftLevel(giftLevelVO);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelAmount = marketingVOResponse.getFullGiftLevelList().get(0).getFullAmount();
                                        response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                    }
                                    // 最大赠级别
                                    MarketingFullGiftLevelVO maxLevelVO = marketingVOResponse.getFullGiftLevelList().stream()
                                            .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount)).get();
                                    // 是否满足最大等级
                                    boolean isMaxLevel = maxLevelVO.getFullAmount().compareTo(totalAmount) < 0;
                                    // 满金额赠可叠加且是最大级别计算赠品数量
                                    if (MarketingSubType.GIFT_FULL_AMOUNT.equals(marketingVOResponse.getSubType()) &&
                                            BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMaxLevel) {
                                        // 如果是赠品数量是叠加倍数，赠品营销只取最大级别,计算赠品数量
                                        Long multiple = totalAmount.divide(maxLevelVO.getFullAmount(), 0, BigDecimal.ROUND_DOWN).longValue();
                                        if (multiple.compareTo(1L) > 0) {
                                            for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                                // 计算赠品数量
                                                detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                            }
                                            List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                            levelVOS.add(maxLevelVO);
                                            marketingVOResponse.setFullGiftLevelList(levelVOS);
                                        }
                                    }
                                    response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                    break;
                                default:
                                    levelAmount = BigDecimal.ZERO;
                            }

                            // 计算达到营销级别的差额
                            lackAmount = levelAmount.compareTo(totalAmount) <= 0 ? BigDecimal.ZERO :
                                    levelAmount.subtract(totalAmount);

                            response.setLack(lackAmount);

                        } else if (marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT || marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                            // 满数量
                            // 计算达到营销级别的数量
                            Long levelCount;

                            switch (marketingVOResponse.getMarketingType()) {
                                case REDUCTION:
//                            Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
//                                    marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount));
                                    MarketingFullReductionLevelVO marketingFullReductionLevelVO = new MarketingFullReductionLevelVO();
                                    for (MarketingFullReductionLevelVO level : marketingVOResponse.getFullReductionLevelList()) {
                                        if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                                (Objects.isNull(marketingFullReductionLevelVO.getFullCount()) ||
                                                        marketingFullReductionLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                            marketingFullReductionLevelVO = level;
                                        }
                                    }

                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(marketingFullReductionLevelVO.getFullCount())) {
                                        levelCount = marketingFullReductionLevelVO.getFullCount();
                                        discount = marketingFullReductionLevelVO.getReduction();
                                        response.setFullReductionLevel(marketingFullReductionLevelVO);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                        discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                        response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                    }
                                    // 是否满足最大等级
                                    MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                                    boolean isMax = maxLevel.getFullCount().compareTo(totalCount) < 0;
                                    // 满数量减可叠加且满足最大级计算折扣
                                    if (MarketingSubType.REDUCTION_FULL_COUNT.equals(marketingVOResponse.getSubType()) &&
                                            BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                        discount = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(levelCount), 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                    }
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                    break;
                                case DISCOUNT:
//                            Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
//                                    marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount));

                                    MarketingFullDiscountLevelVO discountLevelVO = new MarketingFullDiscountLevelVO();
                                    for (MarketingFullDiscountLevelVO level : marketingVOResponse.getFullDiscountLevelList()) {
                                        if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                                (Objects.isNull(discountLevelVO.getFullCount()) ||
                                                        discountLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                            discountLevelVO = level;
                                        }
                                    }
                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(discountLevelVO.getFullCount())) {
                                        levelCount = discountLevelVO.getFullCount();
                                        discount = discountLevelVO.getDiscount();
                                        response.setFullDiscountLevel(discountLevelVO);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                        discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                        response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                    }
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());
                                    break;
                                case GIFT:
//                            List<MarketingFullGiftLevelVO> giftLevels =
//                                    marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());
//
//                            Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
//                                    giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));
                                    MarketingFullGiftLevelVO giftLevel = new MarketingFullGiftLevelVO();
                                    for (MarketingFullGiftLevelVO level : marketingVOResponse.getFullGiftLevelList()) {
                                        if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                                (Objects.isNull(giftLevel.getFullCount()) ||
                                                        giftLevel.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                            giftLevel = level;
                                        }
                                    }

                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(giftLevel.getFullCount())) {
                                        levelCount = giftLevel.getFullCount();
                                        response.setFullGiftLevel(giftLevel);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                        response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                    }
                                    // 最大赠级别
                                    MarketingFullGiftLevelVO maxLevelVO = marketingVOResponse.getFullGiftLevelList().stream()
                                            .max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount)).get();
                                    // 是否满足最大等级
                                    boolean isMaxLevel = maxLevelVO.getFullCount().compareTo(totalCount) < 0;
                                    // 满数量赠可叠加且满足最大级优惠计算赠品数量
                                    if (MarketingSubType.GIFT_FULL_COUNT.equals(marketingVOResponse.getSubType()) &&
                                            BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMaxLevel) {
                                        // 如果是赠品数量是叠加倍数，赠品营销只取最大级别, 计算赠品数量
                                        Long multiple = BigDecimal.valueOf(totalCount)
                                                .divide(BigDecimal.valueOf(maxLevelVO.getFullCount()), 0, BigDecimal.ROUND_DOWN).longValue();
                                        if (multiple.compareTo(1L) > 0) {
                                            for (MarketingFullGiftDetailVO detailVO : maxLevelVO.getFullGiftDetailList()) {
                                                // 计算赠品数量
                                                detailVO.setProductNum(detailVO.getProductNum() * multiple);
                                            }
                                            List<MarketingFullGiftLevelVO> levelVOS = new ArrayList<>();
                                            levelVOS.add(maxLevelVO);
                                            marketingVOResponse.setFullGiftLevelList(levelVOS);
                                        }
                                    }

                                    response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());

                                    break;
                                default:
                                    levelCount = 0L;
                            }

                            // 计算达到营销级别缺少的数量
                            lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                                    levelCount.longValue() - totalCount.longValue();

                            response.setLack(BigDecimal.valueOf(lackCount));
                        } else if (marketingVOResponse.getSubType() == MarketingSubType.GIFT_FULL_ORDER || marketingVOResponse.getSubType() == MarketingSubType.REDUCTION_FULL_ORDER ||  marketingVOResponse.getSubType() == MarketingSubType.DISCOUNT_FULL_ORDER  ) {
                            // 订单满赠
                            // 计算达到营销级别的数量
                            Long levelCount;

                            switch (marketingVOResponse.getMarketingType()) {
                                case REDUCTION:
//                            Optional<MarketingFullReductionLevelVO> fullReductionLevelOptional =
//                                    marketingVOResponse.getFullReductionLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount));
                                    MarketingFullReductionLevelVO marketingFullReductionLevelVO = new MarketingFullReductionLevelVO();
                                    for (MarketingFullReductionLevelVO level : marketingVOResponse.getFullReductionLevelList()) {
                                        if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                                (Objects.isNull(marketingFullReductionLevelVO.getFullCount()) ||
                                                        marketingFullReductionLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                            marketingFullReductionLevelVO = level;
                                        }
                                    }

                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(marketingFullReductionLevelVO.getFullCount())) {
                                        levelCount = marketingFullReductionLevelVO.getFullCount();
                                        discount = marketingFullReductionLevelVO.getReduction();
                                        response.setFullReductionLevel(marketingFullReductionLevelVO);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelCount = marketingVOResponse.getFullReductionLevelList().get(0).getFullCount();
                                        discount = marketingVOResponse.getFullReductionLevelList().get(0).getReduction();
                                        response.setFullReductionLevel(marketingVOResponse.getFullReductionLevelList().get(0));
                                    }
                                    // 是否满足最大等级
                                    MarketingFullReductionLevelVO maxLevel = marketingVOResponse.getFullReductionLevelList().stream()
                                            .max(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount)).get();
                                    boolean isMax = maxLevel.getFullCount().compareTo(totalCount) < 0;
                                    // 满数量减可叠加且满足最大级计算折扣
                                    if (MarketingSubType.REDUCTION_FULL_ORDER.equals(marketingVOResponse.getSubType()) &&
                                            BoolFlag.YES.equals(marketingVOResponse.getIsOverlap()) && isMax) {
                                        discount = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(levelCount), 0, BigDecimal.ROUND_DOWN).multiply(discount);
                                    }
                                    response.setFullReductionLevelList(marketingVOResponse.getFullReductionLevelList());
                                    break;
                                case DISCOUNT:
//                            Optional<MarketingFullDiscountLevelVO> fullDiscountLevelOptional =
//                                    marketingVOResponse.getFullDiscountLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0)
//                                            .max(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount));

                                    MarketingFullDiscountLevelVO discountLevelVO = new MarketingFullDiscountLevelVO();
                                    for (MarketingFullDiscountLevelVO level : marketingVOResponse.getFullDiscountLevelList()) {
                                        if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                                (Objects.isNull(discountLevelVO.getFullCount()) ||
                                                        discountLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                            discountLevelVO = level;
                                        }
                                    }
                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(discountLevelVO.getFullCount())) {
                                        levelCount = discountLevelVO.getFullCount();
                                        discount = discountLevelVO.getDiscount();
                                        response.setFullDiscountLevel(discountLevelVO);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelCount = marketingVOResponse.getFullDiscountLevelList().get(0).getFullCount();
                                        discount = marketingVOResponse.getFullDiscountLevelList().get(0).getDiscount();
                                        response.setFullDiscountLevel(marketingVOResponse.getFullDiscountLevelList().get(0));
                                    }
                                    response.setFullDiscountLevelList(marketingVOResponse.getFullDiscountLevelList());
                                    break;
                                case GIFT:
//                            List<MarketingFullGiftLevelVO> giftLevels =
//                                    marketingVOResponse.getFullGiftLevelList().stream().filter(level -> level.getFullCount().compareTo(totalCount) <= 0).collect(Collectors.toList());
//
//                            Optional<MarketingFullGiftLevelVO> fullGiftLevelOptional =
//                                    giftLevels.stream().max(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount));
                                    MarketingFullGiftLevelVO giftLevel = new MarketingFullGiftLevelVO();
                                    for (MarketingFullGiftLevelVO level : marketingVOResponse.getFullGiftLevelList()) {
                                        if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                                (Objects.isNull(giftLevel.getFullCount()) ||
                                                        giftLevel.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                            giftLevel = level;
                                        }
                                    }
                                    // 满足条件的最大的等级
                                    if (Objects.nonNull(giftLevel.getFullCount())) {
                                        levelCount = giftLevel.getFullCount();
                                        response.setFullGiftLevel(giftLevel);
                                    } else {
                                        // 没有满足条件，默认取最低等级
                                        levelCount = marketingVOResponse.getFullGiftLevelList().get(0).getFullCount();
                                        response.setFullGiftLevel(marketingVOResponse.getFullGiftLevelList().get(0));
                                    }
                                    response.setFullGiftLevelList(marketingVOResponse.getFullGiftLevelList());
                                    break;
                                default:
                                    levelCount = 0L;
                            }
                            // 计算达到营销级别缺少的数量
                            lackCount = levelCount.compareTo(totalCount) <= 0 ? 0L :
                                    levelCount.longValue() - totalCount.longValue();
                            response.setLack(BigDecimal.valueOf(lackCount));
                        } else {
                            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
                        }

                        response.setGoodsInfoList(newGoodsInfos);
                        response.setTotalCount(totalCount);
                        response.setTotalAmount(totalAmount);
                        response.setDiscount(discount);
//            }
                    }

                    sb.append(",marketingGoodsMap.foreach end time=");
                    sb.append(System.currentTimeMillis()-sTm);

                    // 如果有赠品，则查询赠品商品的详细信息
//            if (response.getFullGiftLevelList() != null && !response.getFullGiftLevelList().isEmpty()) {
//                List<String> skuIds =
//                        response.getFullGiftLevelList().stream().flatMap(marketingFullGiftLevel -> marketingFullGiftLevel.getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId)).distinct().collect(Collectors.toList());
//                GoodsInfoViewByIdsRequest goodsInfoViewRequest = GoodsInfoViewByIdsRequest.builder()
//                        .goodsInfoIds(skuIds)
//                        .isHavSpecText(Constants.yes)
//                        .wareId(wareId)
//                        .build();
//                Long currentTime5 = System.currentTimeMillis();
////                response.setGiftGoodsInfoResponse(KsBeanUtil.convert(goodsInfoQueryProvider.listViewByIds(goodsInfoViewRequest).getContext(), PurchaseGoodsViewVO.class));
//                System.out.println("dddddddddddddd 查询赠品:" + marketingVOResponse.getMarketingId() + (System.currentTimeMillis() - currentTime5));
//            }

                    response.setStoreId(marketingVOResponse.getStoreId());
                    response.setMarketingId(marketingVOResponse.getMarketingId());
                    response.setMarketingType(marketingVOResponse.getMarketingType());
                    response.setSubType(marketingVOResponse.getSubType());
                    response.setIsOverlap(marketingVOResponse.getIsOverlap());
                    response.setGoodsInfoIds(marketingGoodsesMap.get(outMap.getKey()));
                    responseMap.put(marketingVOResponse.getMarketingId(), response);
                }


            }
            return responseMap;
        }finally {
            log.info(sb.toString());
        }

    }
    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: 填充商品状态
     * @Return: void
     * @Author: yxb
     * @Date: 2021/2/2 14:24
     */
    private void  fillGoodsStatus(List<GoodsInfoVO> goodsInfoVOS){
        goodsInfoVOS.forEach(goodsInfo -> {
            if (DeleteFlag.NO.equals(goodsInfo.getDelFlag())){
                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())
                        && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            }else {
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });
    }

    /**
     * 填充客户购买数
     *
     * @param goodsInfoList SKU商品
     * @param followList    SKU商品
     */
    public void fillBuyCountInit(List<GoodsInfoVO> goodsInfoList, List<Purchase> followList,List<String> selectSkuids) {
        if (CollectionUtils.isEmpty(goodsInfoList) || CollectionUtils.isEmpty(followList)) {
            return;
        }
        //填充SKU的购买数
        goodsInfoList.stream().forEach(goodsInfo -> {
            goodsInfo.setBuyCount(0L);
            if (CollectionUtils.isNotEmpty(selectSkuids)){
                if (selectSkuids.contains(goodsInfo.getGoodsInfoId())){
                    Optional<Purchase> first = followList.stream().filter(purchase -> goodsInfo.getGoodsInfoId().equals(purchase.getGoodsInfoId())).findFirst();
                    first.ifPresent(purchase -> {goodsInfo.setBuyCount(purchase.getGoodsNum());});
                }
            }
        });
    }

    /**
     * 将实体包装成VO
     * @author zhanglingke
     */
    public PurchaseVO wrapperVo(Purchase purchase) {
        if (purchase != null){
            PurchaseVO purchaseVO=new PurchaseVO();
            KsBeanUtil.copyPropertiesThird(purchase,purchaseVO);
            return purchaseVO;
        }
        return null;
    }

    //获取满系营销订单活动（满折，满减）
    @Deprecated
    public TradeMarketingDTO getOrderMarketing(List<TradeItemDTO> tradeItems,List<GoodsInfoVO> goodsInfoVOS, List<String> skuIds) {
        //查看是否有满订单系列
        //订单价格
        Long totalCount = tradeItems.stream().collect(Collectors.summingLong(TradeItemDTO::getNum));
        List<GoodsInfoDTO> goodsInfoList = KsBeanUtil.convert(goodsInfoVOS, GoodsInfoDTO.class);
        Map<String, List<MarketingViewVO>> marketingMap = marketingPluginQueryProvider.getByGoodsInfoListAndCustomer(
                MarketingPluginByGoodsInfoListAndCustomerRequest.builder().goodsInfoList(goodsInfoList).build()).getContext().getMarketingMap();
        if (Objects.nonNull(marketingMap)) {
            List<MarketingViewVO> marketingViewVOS = marketingMap.get(Constant.FULL_GIT_ORDER_GOODS);
            if(CollectionUtils.isEmpty(marketingViewVOS)){
                return null;
            }
            MarketingViewVO orderByMarketingVO = marketingViewVOS.stream().findFirst().orElse(null);
            if (Objects.nonNull(orderByMarketingVO)) {
                if (MarketingType.DISCOUNT.equals(orderByMarketingVO.getMarketingType())) {
                    MarketingFullDiscountLevelVO discountLevelVO = new MarketingFullDiscountLevelVO();
                    List<MarketingFullDiscountLevelVO> levelVOList = marketingFullDiscountQueryProvider.listByMarketingId
                            (new MarketingFullDiscountByMarketingIdRequest(orderByMarketingVO.getMarketingId())).getContext().getMarketingFullDiscountLevelVOList();
                    if (CollectionUtils.isNotEmpty(levelVOList)) {
                        for (MarketingFullDiscountLevelVO level : levelVOList) {
                            if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                    (Objects.isNull(discountLevelVO.getFullCount()) ||
                                            discountLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                discountLevelVO = level;
                            }
                        }
                        // 满足条件的最大的等级
                        if (Objects.nonNull(discountLevelVO.getFullCount())) {
                            TradeMarketingDTO orderDTO = new TradeMarketingDTO();
                            orderDTO.setMarketingId(orderByMarketingVO.getMarketingId());
                            orderDTO.setSkuIds(skuIds);
                            orderDTO.setMarketingLevelId(discountLevelVO.getDiscountLevelId());
                            return orderDTO;
                        }
                    }
                } else if (MarketingType.REDUCTION.equals(orderByMarketingVO.getMarketingType())) {
                    List<MarketingFullReductionLevelVO> levelVOList = marketingFullReductionQueryProvider.listByMarketingId
                            (new MarketingFullReductionByMarketingIdRequest(orderByMarketingVO.getMarketingId())).getContext().getMarketingFullReductionLevelVOList();
                    if (CollectionUtils.isNotEmpty(levelVOList)) {
                        MarketingFullReductionLevelVO marketingFullReductionLevelVO = new MarketingFullReductionLevelVO();
                        for (MarketingFullReductionLevelVO level : levelVOList) {
                            if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                    (Objects.isNull(marketingFullReductionLevelVO.getFullCount()) ||
                                            marketingFullReductionLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                marketingFullReductionLevelVO = level;
                            }
                        }
                        // 满足条件的最大的等级
                        if (Objects.nonNull(marketingFullReductionLevelVO.getFullCount())) {
                            TradeMarketingDTO orderDTO = new TradeMarketingDTO();
                            orderDTO.setMarketingId(orderByMarketingVO.getMarketingId());
                            orderDTO.setSkuIds(skuIds);
                            orderDTO.setMarketingLevelId(marketingFullReductionLevelVO.getReductionLevelId());
                            return orderDTO;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查询采购车配置
     * @return
     */
    public BaseResponse<ProcurementConfigResponse> getProcurementType(){
        try {
            List<ProcurementConfig> list = procurementConfigRepository.findAll();
            if (list.size() > 0){
                ProcurementConfigResponse response = KsBeanUtil.convert(list.get(0),ProcurementConfigResponse.class);
                return BaseResponse.success(response);
            }
        }catch (Exception e){
            return BaseResponse.FAILED();
        }
        return BaseResponse.FAILED();
    }

    //sku购物车数量
    public Map<String,Long> getSkuPurchaseNum(PurchaseQueryDTO request){
        Map<String,Long> data = new HashMap<>();
        try{
            if(request.getGoodsInfoIds() != null && request.getGoodsInfoIds().size() > 0){
                List<Object[]> list = purchaseRepository.querySkuCountGoodsNum(request.getGoodsInfoIds(),request.getCustomerId());
                if(list != null && list.size() > 0){
                    list.forEach(item -> {
                        String goodsInfoId = item[0].toString();
                        if(StringUtils.isNotEmpty(goodsInfoId)){
                            Long goodsNum = Long.valueOf(item[1].toString());
                            data.put(goodsInfoId,goodsNum);
                        }
                    });
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return data;
    }

    public StockAndPureChainNodeRsponse checkStockPurchase(StockAndPureChainNodeRequeest requeest){
             Map<Long,Object> map = new HashMap<>();
             if (Objects.isNull(requeest.getSubType())){
                 requeest.setSubType(0);
             }
             if (requeest.getSubType()==0){    //0是非囤货
                 return getStockAndPureChainNodeRsponse(requeest, map,checkList);
             }else if (requeest.getSubType()==1){    //1是囤货
                 return getStockAndPureChainNodeRsponse(requeest, map,tunhuocheckList);
             }
             else if (requeest.getSubType()==3){  //3是散批校验只校验库存和限购
                 return getStockAndPureChainNodeRsponse(requeest, map, stockAndAreaNodeList);
             }else if (requeest.getSubType()==4){   //4是散批
                 Map<String, Object> mapForBulk = new HashMap<>();
                 return getStockAndPureChainNodeResponseForBulk(requeest, mapForBulk, bulkGoodsInfoStockAndAreaNodeList);
             }
             else {       //2是提货
                 return getStockAndPureChainNodeRsponse(requeest, map,tihuostockList);
             }


    }

    private StockAndPureChainNodeRsponse getStockAndPureChainNodeResponseForBulk(StockAndPureChainNodeRequeest requeest, Map<String, Object> map,List<StockAndPureChainNode> list) {
        if (CollectionUtils.isEmpty(list)){
            throw new RuntimeException("系统异常---检查链为空");
        }
        for (StockAndPureChainNode checkNode : list) {
            StockAndPureChainNodeRsponse stockAndPureChainNodeRsponse = checkNode.checkStockPure(requeest);
            if (CollectionUtils.isNotEmpty(stockAndPureChainNodeRsponse.getCheckPure())){
                stockAndPureChainNodeRsponse.getCheckPure().forEach(v->{
                    if (Objects.nonNull(v.getType())){
                        //使用map新值覆盖旧值
                        map.put(v.getGoodsInfoId(), v);
                    }
                });
            }
        }
        List<Object> collect = new ArrayList<>(map.values());
        List<DevanningGoodsInfoPureVO> convert = KsBeanUtil.convert(collect, DevanningGoodsInfoPureVO.class);
        return StockAndPureChainNodeRsponse.builder().CheckPure(convert).build();
    }

    private StockAndPureChainNodeRsponse getStockAndPureChainNodeRsponse(StockAndPureChainNodeRequeest requeest, Map<Long, Object> map,List<StockAndPureChainNode> list) {
        if (CollectionUtils.isEmpty(list)){
            throw new RuntimeException("系统异常---检查链为空");
        }
        for (StockAndPureChainNode checkNode : list) {
            StockAndPureChainNodeRsponse stockAndPureChainNodeRsponse = checkNode.checkStockPure(requeest);
            if (CollectionUtils.isNotEmpty(stockAndPureChainNodeRsponse.getCheckPure())){
                stockAndPureChainNodeRsponse.getCheckPure().forEach(v->{
                    if (Objects.nonNull(v.getType())){
                        //使用map新值覆盖旧值
                        Object InfoPureVOObj = map.get(v.getDevanningId());
                        if(null==InfoPureVOObj) {
                            map.put(v.getDevanningId(), v);
                        }else{
                            DevanningGoodsInfoPureVO goodsInfoPureVO = KsBeanUtil.convert(InfoPureVOObj, DevanningGoodsInfoPureVO.class);
                            if(Objects.isNull(goodsInfoPureVO.getMaxPurchase())){
                                map.put(v.getDevanningId(), v);
                            }else{
                                if(goodsInfoPureVO.getMaxPurchase().compareTo(v.getMaxPurchase())==1){
                                    map.put(v.getDevanningId(), v);
                                }
                            }
                        }
                    }

                });
            }
        }
        List<Object> collect = new ArrayList<>(map.values());
        List<DevanningGoodsInfoPureVO> convert = KsBeanUtil.convert(collect, DevanningGoodsInfoPureVO.class);
        return StockAndPureChainNodeRsponse.builder().CheckPure(convert).build();
    }

}
