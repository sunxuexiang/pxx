package com.wanmi.sbc.returnorder.shopcart;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.github.yitter.idgen.YitIdHelper;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsPriceAssistProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsInfoSpecDetailRelQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByParentIdRequest;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoVerifyRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingListByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingModifyRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingSyncRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCacheProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullDiscountQueryProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullReductionQueryProvider;
import com.wanmi.sbc.marketing.api.provider.distribution.DistributionSettingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGetDetailByIdAndStoreIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCacheListForGoodsGoodInfoListRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingQueryByIdsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingLevelGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginByGoodsInfoListAndCustomerRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGetCustomerLevelsByStoreIdsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGetCustomerLevelsRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCacheListForGoodsDetailResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.MarketingErrorCode;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseGoodsInfoDTO;
import com.wanmi.sbc.returnorder.bean.dto.PurchaseQueryDTO;
import com.wanmi.sbc.returnorder.bean.dto.TradeItemDTO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseGoodsViewVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseVO;
import com.wanmi.sbc.returnorder.api.request.purchase.CheckedBulkCartRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.PurchaseFrontRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.PurchaseStoreMarketingRequest;
import com.wanmi.sbc.returnorder.api.response.purchase.*;
import com.wanmi.sbc.returnorder.follow.model.root.GoodsCustomerFollow;
import com.wanmi.sbc.returnorder.follow.repository.GoodsCustomerFollowRepository;
import com.wanmi.sbc.returnorder.follow.request.GoodsCustomerFollowQueryRequest;
import com.wanmi.sbc.returnorder.historytownshiporder.response.TrueStock;
import com.wanmi.sbc.returnorder.historytownshiporder.service.HistoryTownShipOrderService;
import com.wanmi.sbc.returnorder.mq.OrderProducerService;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseService;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseAction;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRepository;
import com.wanmi.sbc.returnorder.purchase.model.ProcurementConfig;
import com.wanmi.sbc.returnorder.purchase.repository.ProcurementConfigRepository;
import com.wanmi.sbc.returnorder.redis.*;
import com.wanmi.sbc.returnorder.customer.service.CustomerCommonService;
import com.wanmi.sbc.returnorder.shopcart.ChainHandle.StockAndPureChainNode;
import com.wanmi.sbc.returnorder.shopcart.cache.BulkShopCartCacheSupport;
import com.wanmi.sbc.returnorder.shopcart.request.BulkShopCartRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.StockAndPureChainNodeRequeest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * 采购单服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Transactional
@Log4j2
public class BulkShopCartService {

    @Autowired
    private GoodsCustomerFollowRepository goodsCustomerFollowRepository;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private BulkGoodsInfoProvider bulkGoodsInfoProvider;

    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;

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
    private BulkShopCartRepository bulkShopCartRepository;

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


    @Resource
    private DistributorGoodsInfoQueryProvider distributorGoodsInfoQueryProvider;

    @Autowired
    private GoodsPriceAssistProvider goodsPriceAssistProvider;

    @Autowired
    private DistributionSettingQueryProvider distributionSettingQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

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
    private CouponActivityQueryProvider couponActivityQueryProvider;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private HistoryTownShipOrderService historyTownShipOrderService;

    /**
     * 注入消费记录生产者service
     */
    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private RedisService redisService;

    /**
     * 通过 service-Order-app >> resource >> spring-plugin.xml 文件注入
     */
    @Resource(name = "stockAndPureChainNodeList")
    private List<StockAndPureChainNode> checkList;

    /**
     * 登陆后,查询采购单列表 缓存
     *
     * @param request 参数
     * @return 采购单数据
     * @throws SbcRuntimeException
     */
    public PurchaseResponse pageListAndCache(BulkShopCartRequest request) throws SbcRuntimeException {

        // 获取商品快照
        List<BulkShopCart> shopCarCache = this.queryCart(request.getCustomerId(), request.getWareId());
        if(CollectionUtils.isEmpty(shopCarCache)){
            return PurchaseResponse.builder().build();
        }

        List<String> goodsInfoIds = shopCarCache.stream().map(BulkShopCart::getGoodsInfoId).collect(Collectors.toList());
        GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(goodsInfoIds)
                .isHavSpecText(Constants.yes)
                .wareId(request.getWareId())
                .matchWareHouseFlag(request.getMatchWareHouseFlag())
                .build();
        GoodsInfoViewByIdsResponse response = bulkGoodsInfoQueryProvider.listViewByIdsByMatchFlagNoStock(goodsInfoRequest).getContext();

        List<String> goodsIds = shopCarCache.stream().map(BulkShopCart::getGoodsId).distinct().collect(Collectors.toList());
        List<GoodsVO> goodsList = response.getGoodses().stream()
                .filter(goods -> goodsIds.contains(goods.getGoodsId()))
                .map(goods -> {
                    goods.setGoodsDetail("");
                    return goods;
                }).collect(Collectors.toList());

        List<GoodsInfoVO> goodsInfoList = IteratorUtils.zip(
                response.getGoodsInfos(),
                shopCarCache,
                (GoodsInfoVO sku, BulkShopCart f) -> sku.getGoodsInfoId().equals(f.getGoodsInfoId()),
                (GoodsInfoVO sku, BulkShopCart f) -> {
                    sku.setCreateTime(f.getCreateTime());   // 加购时间
                    sku.setIsCheck(f.getIsCheck());         // 选中状态
                    sku.setBuyCount(f.getGoodsNum());       // 购买数量
                }
        );
        log.info("data====================="+goodsInfoList);

        //建立商户->SPU的扁平化结构
        List<Long> storeList = goodsInfoList.stream()
                .map(GoodsInfoVO::getStoreId)
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(storeList) && storeList.size()>1){
            throw new SbcRuntimeException("k-250008","店铺id大于一");
        }
        StoreVO storeVO = new StoreVO();
        storeVO.setStoreId(storeList.stream().findAny().orElse(null));
        List<StoreVO> stores =new LinkedList<>();
        stores.add(storeVO);

        return PurchaseResponse.builder()
                .stores(stores)
                .goodses(goodsList)
                .goodsInfos(goodsInfoList)
                .build();
    }

    /**
     * 查询购物车
     *
     * 1. 如果缓存中存在，那么就从缓存中查询到后返回
     *
     * 2. 如果缓存中不存在，那么就添加分布式锁，然后再查询MySQL，查询到数据后更新到缓存中，最后返回
     *
     * 3. 如果缓存、MySQL都不存在，那么就再查询MySQL后，给缓存设置一个空值，设置一个随机的过期时间，最后返回一个空数据
     * 当下次再来查询购物车时，会先判断缓存中的空值是否存在，如果存在就不查数据库了
     *
     * @param customerId
     * @param wareId
     * @return
     */
    public List<BulkShopCart> queryCart(String customerId, Long wareId) {
        // 从缓存中获取购物车数据
        List<BulkShopCart> bulkShopCarts = this.queryBulkShopCartByCache(customerId, wareId);

        // 如果缓存中没有就从数据库中获取
        if(CollectionUtils.isEmpty(bulkShopCarts)){
            bulkShopCarts = queryCartNoCache(customerId, wareId);
        }
        if(CollectionUtils.isNotEmpty(bulkShopCarts)){
            bulkShopCarts.forEach(var->{
                if(Objects.isNull(var.getCreateTime())){
                    Optional<BulkShopCart> byId = bulkShopCartRepository.findById(var.getCartId());
                    if(byId.isPresent()){
                        var.setCreateTime(byId.get().getCreateTime());
                    }else{
                        var.setCreateTime(LocalDateTime.now());
                    }
                }
            });
        }
        return bulkShopCarts;
    }

    /**
     * 从缓存中获取购物车数据
     * @param customerId
     * @param wareId
     * @return
     */
    public List<BulkShopCart> queryBulkShopCartByCache(String customerId, Long wareId){
        // 从缓存中查询出购物车商品集合
        Map<String, String> totalShopCartMap = redisCache.hGetAll(BulkShopCartCacheSupport.buildExtraKey(customerId, wareId));

        // 如果缓存中没有就返回empty
        List<BulkShopCart> bulkShopCarts = Lists.newArrayList();
        if (MapUtils.isEmpty(totalShopCartMap)) {
            return Lists.newArrayList();
        }

        totalShopCartMap.forEach((k, v)->{
            BulkShopCart shopCart = JSON.parseObject(v, BulkShopCart.class);

            // 购买数量
            shopCart.setGoodsNum(getShopCarCacheNum(customerId, wareId, shopCart.getGoodsInfoId()));

            // 选中状态
            String key  = BulkShopCartCacheSupport.buildKey(customerId, wareId);
            String nKey = BulkShopCartCacheSupport.buildHashKeyOfIsCheck(shopCart.getGoodsInfoId());
            Object s = redisCache.HashGet(key, nKey);
            if(Objects.isNull(s)){
                return;
            }
            shopCart.setIsCheck(DefaultFlag.YES);
            if (s.toString().equalsIgnoreCase("0")){
                shopCart.setIsCheck(DefaultFlag.NO);
            }

            bulkShopCarts.add(shopCart);
        });
        return bulkShopCarts;
    }

    /**
     * 从数据库中获取购物车数据
     *
     * @param customerId
     * @param wareId
     * @return
     */
    private List<BulkShopCart> queryCartNoCache(String customerId, Long wareId){
        // 判断是否存在空缓存
        String emptyKey = RedisKeyConstant.BULK_SHOPPING_CART_EMPTY + customerId + ":" + wareId;
        if (redisCache.hasKey(emptyKey)) {
            log.warn("购物车查询到空缓存，禁止查询MySQL, key: {}", emptyKey);
            return Lists.newArrayList();
        }

        String key = RedisLockKeyConstants.BULK_SHOPPING_CART_PERSISTENCE_KEY + customerId + ":" + wareId;
        List<BulkShopCart> bulkShopCarts = Lists.newArrayList();
        try {
            boolean lock = redisLock.lock(key);
            if (!lock) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "未获取到分布式锁");
            }

            // 从数据库中查询到购物车的商品集合
            BulkShopCartRequest request = BulkShopCartRequest.builder()
                    .customerId(customerId).inviteeId(Constants.PURCHASE_DEFAULT)
                    .wareId(wareId)
                    .build();
            Specification<BulkShopCart> whereCriteria = request.getWhereCriteria();
            bulkShopCarts = bulkShopCartRepository.findAll(whereCriteria);

            // 更新Redis缓存中的购物车商品
            this.syncCacheFromPersistence(customerId, wareId, bulkShopCarts);

            log.warn("购物车缓存数据查询为空, 添加分布式锁查询MySQL, userId: {}", customerId);
        } finally {
            redisLock.unlock(key);
        }
        // 构造购物车返回值
        return bulkShopCarts;
    }

    /**
     * 更新Redis缓存
     *
     */
    private void  syncCacheFromPersistence(String customerId, Long wareId, List<BulkShopCart> bulkShopCarts){
        if(CollectionUtils.isEmpty(bulkShopCarts)){
            String shoppingEmptyKey = RedisKeyConstant.BULK_SHOPPING_CART_EMPTY + customerId +":"+ wareId;
            String shoppingEmptyValue = RedisKeyConstant.SHOPPING_CART_EMPTY_CACHE_IDENTIFY;
            int expireTime = new Random().nextInt(100-30)+30;
            // 在Redis中写入空缓存
            redisCache.set(shoppingEmptyKey, shoppingEmptyValue, expireTime);
            log.warn("购物车和缓存中都没有查到请求的购物车数据, 写入空缓存, key: {}, value: {}, expire: {}秒", shoppingEmptyKey, shoppingEmptyValue, expireTime);
            return;
        }

        // 更新Redis缓存中的购物车商品
        for (BulkShopCart bulkShopCart : bulkShopCarts){
            this.setShopCarCache(customerId, wareId, bulkShopCart.getGoodsInfoId(), bulkShopCart);
            this.setShopCarCacheNum(customerId,wareId, bulkShopCart.getGoodsInfoId(), BigDecimal.valueOf(bulkShopCart.getGoodsNum()));
            this.setShopCarCacheCheck(customerId, wareId, bulkShopCart.getGoodsInfoId(), 1==bulkShopCart.getIsCheck().toValue() ? 0 : 1);
        }
    }


    /**
     *
     * @param request 请求实体
     * @param goodsInfos 拆箱id
     * @param type 类型  3全选中 4全未选中  0部分选中 1部分未选中
     */
    public void updateDevanIsCheckAndCache(CheckedBulkCartRequest request, List<String> goodsInfos, String type ) {
        List<BulkShopCart> shopCarCache = this.queryCart(request.getCustomerId(), request.getWareId());
        List<String> allGoodsInfoIds = shopCarCache.stream().map(BulkShopCart::getGoodsInfoId).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();

        if (type.equalsIgnoreCase("3")){ // 3代表全选中
            allGoodsInfoIds.forEach(goodsInfoId->{
                map.put(BulkShopCartCacheSupport.buildHashKeyOfIsCheck(goodsInfoId), DefaultFlag.YES.toValue());
            });
        }else if (type.equalsIgnoreCase("4")){ // 4代表全未选中
            allGoodsInfoIds.forEach(goodsInfoId->{
                map.put(BulkShopCartCacheSupport.buildHashKeyOfIsCheck(goodsInfoId), DefaultFlag.NO.toValue());
            });
        } else if (type.equalsIgnoreCase("1") || type.equalsIgnoreCase("0")){ // 0部分选中 1部分未选中
            if (CollectionUtils.isEmpty(goodsInfos)){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "参数错误");
            }
            goodsInfos.forEach(v->{
                // 0部分选中 1部分未选中
                Integer isChecked = Objects.equals(type, "1") ? DefaultFlag.NO.toValue() : DefaultFlag.YES.toValue();
                map.put(BulkShopCartCacheSupport.buildHashKeyOfIsCheck(v), String.valueOf(isChecked));
            });
        }
        String key = BulkShopCartCacheSupport.buildKey(request.getCustomerId(), request.getWareId());
        if (CollectionUtils.isNotEmpty(map.keySet())){
            redisCache.setHashAll(key, map);
        }
    }



    /**
     * 新增采购单缓存
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void saveAndCache(BulkShopCartRequest request,Long markeingId) {
        // TODO 这里没有格式化
        //查询商品信息
        GoodsInfoVO goodsInfo = bulkGoodsInfoQueryProvider.getBulkById(GoodsInfoByIdRequest.builder()
                .goodsInfoId(request.getGoodsInfoId())
                .wareId(request.getWareId())
                .build()).getContext();
        if (Objects.isNull(goodsInfo)){
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        //查询该用户加购数量

        Set set = redisCache.HashKeys(BulkShopCartCacheSupport.buildKey(request.getCustomerId(), request.getWareId()));
        int countNum = set.size()/2;
        if (countNum+1 >= Constants.PURCHASE_MAX_SIZE) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_050121, new Object[]{Constants.PURCHASE_MAX_SIZE});
        }
        long shopCarCacheNum = this.getShopCarCacheNum(request.getCustomerId(), request.getWareId(), request.getGoodsInfoId().toString());
        long re = shopCarCacheNum+request.getGoodsNum();
        //判断限购
        if (CollectionUtils.isEmpty(checkList)){
            throw new RuntimeException("系统异常---检查链为空");
        }
        List<String> goodsInfolist =new LinkedList<>();
        goodsInfolist.add(request.getGoodsInfoId());
        GoodsInfoListByIdsRequest build = GoodsInfoListByIdsRequest.builder()
                .goodsInfoIds(goodsInfolist)
                .wareId(request.getWareId())
                .build();
        List<GoodsInfoVO> goodsInfos = bulkGoodsInfoQueryProvider.listByIds(build)
                .getContext()
                .getGoodsInfos();
        List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = KsBeanUtil.convertList(goodsInfos, DevanningGoodsInfoVO.class);
        if (CollectionUtils.isEmpty(devanningGoodsInfoVOS)){
            throw new RuntimeException("未查询到拆箱数据");
        }
        Map<String, DevanningGoodsInfoVO> devanningGoodsInfoVOMap = devanningGoodsInfoVOS.stream().collect(Collectors.toMap(DevanningGoodsInfoVO::getGoodsInfoId, Function.identity(), (a, b) -> a));

        //购物车存在数据
        List<BulkShopCart> shopCarCache = this.getShopCarCache(request.getCustomerId(), request.getWareId());
        List<DevanningGoodsInfoMarketingVO> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(shopCarCache)){
            shopCarCache=shopCarCache.stream().filter(v->{
                if (v.getIsCheck().equals(DefaultFlag.YES)){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            shopCarCache.forEach(pa->{
                if (pa.getGoodsInfoId().equalsIgnoreCase(request.getGoodsInfoId()) ){
                    if (Objects.isNull(devanningGoodsInfoVOMap.get(pa.getGoodsInfoId()))   ){
                        throw new RuntimeException("未匹配到数据");
                    }
                    DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO =new DevanningGoodsInfoMarketingVO();
                    devanningGoodsInfoMarketingVO.setBuyCount(pa.getGoodsNum());
                    devanningGoodsInfoMarketingVO.setGoodsInfoId(pa.getGoodsInfoId());
                    devanningGoodsInfoMarketingVO.setSaleType(2);
                    list.add(devanningGoodsInfoMarketingVO);
                }
            });
        }
        DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO =new DevanningGoodsInfoMarketingVO();
        devanningGoodsInfoMarketingVO.setMarketingId(markeingId);
        devanningGoodsInfoMarketingVO.setBuyCount(re);
        devanningGoodsInfoMarketingVO.setDivisorFlag(request.getDivisorFlag());
        devanningGoodsInfoMarketingVO.setGoodsInfoId(request.getGoodsInfoId());
        devanningGoodsInfoMarketingVO.setSaleType(2);
        list.add(devanningGoodsInfoMarketingVO);

        for (StockAndPureChainNode checkNode : checkList) {
            StockAndPureChainNodeRsponse result = checkNode.checkStockPure(StockAndPureChainNodeRequeest.builder()
                    .cityId(request.getCityId()).provinceId(request.getProvinceId()).customerId(request.getCustomerId())
                    .wareId(request.getWareId()).checkPure(list).build());
            if (CollectionUtils.isNotEmpty(result.getCheckPure())){
                DevanningGoodsInfoPureVO devanningGoodsInfoPureVO = result.getCheckPure().stream().findAny().orElse(null);
                if (Objects.nonNull(devanningGoodsInfoPureVO)){
                    if (Objects.nonNull(devanningGoodsInfoPureVO.getType())){
                        switch (devanningGoodsInfoPureVO.getType()){
                            case 0:
                                throw new SbcRuntimeException("k-250001");
                            case 1:
                                throw new SbcRuntimeException("k-250002");
                            case 2:
                                throw new SbcRuntimeException("k-250003");
                            case 3:
                                throw new SbcRuntimeException("k-250004");
                            case -1:
                                throw new SbcRuntimeException("k-250005");
                        }
                    }
                }
            }
        }
        extracted(request, markeingId, goodsInfo, re);


    }


    @Async("extracted")
    private void extracted(BulkShopCartRequest request, Long markeingId, GoodsInfoVO goodsInfo, long re) {
        //去缓存查询是否存在
        BulkShopCart shopCart =new BulkShopCart();

        String key = BulkShopCartCacheSupport.buildExtraKey(request.getCustomerId(), request.getWareId());
        String nKey = request.getGoodsInfoId().toString(); // 内Key
        Boolean cacheExists = redisCache.HashHasKey(key, nKey);

        if (Boolean.FALSE.equals(cacheExists)){
            //不存在新增实体
            //如果为空还加加购物车缓存  使用mq存储到mysql
            shopCart.setCartId(YitIdHelper.nextId());
            shopCart.setGoodsId(goodsInfo.getGoodsId());
            shopCart.setGoodsNum(re);
            shopCart.setCompanyInfoId(goodsInfo.getCompanyInfoId());
            shopCart.setGoodsInfoId(goodsInfo.getGoodsInfoId());
            shopCart.setInviteeId(request.getInviteeId());
            shopCart.setCustomerId(request.getCustomerId());
            shopCart.setWareId(request.getWareId());
            shopCart.setCreateTime(Objects.nonNull(request.getCreateTime()) ? request.getCreateTime() :
                    LocalDateTime.now());
            this.setShopCarCache(request.getCustomerId(), request.getWareId(), request.getGoodsInfoId(), shopCart);
            this.setShopCarCacheCheck(request.getCustomerId(), request.getWareId(), request.getGoodsInfoId(), DefaultFlag.YES.toValue());
        }else {
            Object extraHashString = redisCache.HashGet(key, nKey);
            shopCart = JSON.parseObject(extraHashString.toString(), BulkShopCart.class);
            log.info("ShopCart Cache: {}",shopCart);
            shopCart.setGoodsNum(re);

            // 如果购物车选中缓存不存在，才插入用户选中状态。
            String shopCarCacheCheck = this.getShopCarCacheCheck(request.getCustomerId(), request.getGoodsInfoId(), request.getWareId());
            if(Objects.isNull(shopCarCacheCheck)){
                this.setShopCarCacheCheck(request.getCustomerId(), request.getWareId(), request.getGoodsInfoId(),DefaultFlag.YES.toValue());
            }
        }
        //发送rabbitmq添加mysql
        orderProducerService.sendMQForOrderBulkShopCar(KsBeanUtil.convert(shopCart, BulkShopCartVO.class));
        this.setShopCarCacheNum(request.getCustomerId(), request.getWareId(), request.getGoodsInfoId(), BigDecimal.valueOf(re));
    }
    // ***************************



    public void  cacheBatchSaveDevanning(BulkShopCartRequest request){
        List<String> goodsInfoIds = request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList());
        //获取购物车数量
        Map<String, GoodsInfoDTO> collect2 = request.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoDTO::getGoodsInfoId, Function.identity()));

        Long wareId = request.getWareId();
        String customerId = request.getCustomerId();

        checkCartProductThreshold(customerId, wareId);

        List<GoodsInfoVO> goodsInfos = bulkGoodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder().
                goodsInfoIds(request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList())).wareId(request.getWareId())
                .build()).getContext().getGoodsInfos();

        Map<String, GoodsInfoVO> devanningGoodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity(), (a, b) -> a));
        
        if(CollectionUtils.isNotEmpty(goodsInfos)){
            for (GoodsInfoVO devanningGoodsInfoVO : goodsInfos) {
                Long devanningGoodsInfoVOWareId = Optional.ofNullable(devanningGoodsInfoVO).map(GoodsInfoVO::getWareId).orElse(null);
                if(Objects.nonNull(devanningGoodsInfoVOWareId) && !Objects.equals(wareId, devanningGoodsInfoVOWareId)){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前定位区域不支持销售该商品");
                }
            }
        }

        //缓存获取用户购物车数据
        List<BulkShopCart> shopCarCache = this.getShopCarCache(customerId, wareId, goodsInfoIds);
        List<BulkShopCart> shopCarCacheAll = this.getShopCarCache(customerId, wareId);
        List<BulkShopCart> shopCartCheckList = shopCarCacheAll.stream().filter(v -> {
            if (DefaultFlag.YES.equals(v.getIsCheck())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        log.info("购物车选中数据========"+shopCartCheckList);

        Map<String, BulkShopCart> devaningidItem = shopCarCache.stream().collect(Collectors.toMap(BulkShopCart::getGoodsInfoId, Function.identity(), (a, b) -> a));
        goodsInfos.forEach(goods1->{
            GoodsInfoVO goods = KsBeanUtil.convert(goods1, GoodsInfoVO.class);
            if (request.getForceUpdate()){
                goods1.setBuyCount(collect2.get(goods.getGoodsInfoId()).getBuyCount());
            }else {
                goods1.setBuyCount(collect2.get(goods.getGoodsInfoId()).getBuyCount()
                        +(Objects.isNull(devaningidItem.get(goods1.getGoodsInfoId()))? 0L :devaningidItem.get(goods1.getGoodsInfoId()).getGoodsNum()));
            }

            //缓存库存
            String key = RedisKeyConstants.GOODS_INFO_STOCK_HASH.concat(goods.getGoodsInfoId());
            if (!redisCache.hasKey(key)){
                List<String> list =new LinkedList<>();
                list.add(goods.getGoodsInfoId());
                this.refreshgoodswarestock(list,wareId);
            }
            if (CollectionUtils.isEmpty(checkList)){
                throw new RuntimeException("系统异常---检查链为空");
            }
            DevanningGoodsInfoMarketingVO convert = KsBeanUtil.convert(goods1, DevanningGoodsInfoMarketingVO.class);
            convert.setMarketingId(collect2.get(goods.getGoodsInfoId()).getMarketingId());
            List<DevanningGoodsInfoMarketingVO> list =new LinkedList<>();
            list.add(convert);
            //查询购物车是否有选中的拆箱商品
            if (!request.getForceUpdate()){
                shopCartCheckList.forEach(pa->{
                    if (pa.getGoodsInfoId().equalsIgnoreCase(convert.getGoodsInfoId()) && !Objects.equals(pa.getGoodsInfoId(), convert.getGoodsInfoId())){

                        if (Objects.isNull(devanningGoodsInfoVOMap.get(pa.getGoodsInfoId()))){
                            throw new RuntimeException("未匹配到数据");
                        }
                        DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO =new DevanningGoodsInfoMarketingVO();
                        devanningGoodsInfoMarketingVO.setBuyCount(pa.getGoodsNum());
                        devanningGoodsInfoMarketingVO.setDivisorFlag(devanningGoodsInfoVOMap.get(pa.getGoodsInfoId()).getDivisorFlag());
                        devanningGoodsInfoMarketingVO.setGoodsInfoId(pa.getGoodsInfoId());
                        devanningGoodsInfoMarketingVO.setSaleType(2);
                        list.add(devanningGoodsInfoMarketingVO);
                    }
                });
            }
            //加入购物车
            extracted(request, wareId, customerId, devaningidItem, goods1, goods);
        });

    }
    @Async("extractedpost")
    private void extracted(BulkShopCartRequest request, Long wareId, String customerId, Map<String, BulkShopCart> devaningidItem, GoodsInfoVO goods1, GoodsInfoVO goods) {
        BulkShopCart shopCart =new BulkShopCart();
        if (Objects.isNull(devaningidItem.get(goods.getGoodsInfoId()))){
            //如果为空还加加购物车缓存  使用mq存储到mysql
            shopCart.setCartId(YitIdHelper.nextId());
            shopCart.setGoodsId(goods.getGoodsId());
            shopCart.setGoodsNum(goods.getBuyCount());
            shopCart.setCompanyInfoId(goods.getCompanyInfoId());
            shopCart.setGoodsInfoId(goods.getGoodsInfoId());
            shopCart.setInviteeId(request.getInviteeId());
            shopCart.setCustomerId(request.getCustomerId());
            shopCart.setGoodsId(goods.getGoodsId());
            shopCart.setGoodsNum(goods1.getBuyCount());
            shopCart.setWareId(goods.getWareId());
            shopCart.setCreateTime(Objects.nonNull(request.getCreateTime()) ? request.getCreateTime() :
                    LocalDateTime.now());
            this.setShopCarCache(customerId, wareId, goods.getGoodsInfoId(),shopCart);
            this.setShopCarCacheCheck(customerId, wareId, goods.getGoodsInfoId(),DefaultFlag.YES.toValue());
        } else {
            shopCart = devaningidItem.get(goods.getGoodsInfoId());
            log.info("ShopCart Cache: {}",shopCart);
            shopCart.setGoodsNum(goods1.getBuyCount());

            String shopCarCacheCheck = this.getShopCarCacheCheck(request.getCustomerId(), request.getGoodsInfoId(), request.getWareId());
            if(Objects.isNull(shopCarCacheCheck)){
                this.setShopCarCacheCheck(customerId, wareId, goods.getGoodsInfoId(), DefaultFlag.YES.toValue());
            }
        }
        //发送rabbitmq添加mysql
        orderProducerService.sendMQForOrderBulkShopCar(KsBeanUtil.convert(shopCart, BulkShopCartVO.class));
        this.setShopCarCacheNum(customerId, wareId, goods.getGoodsInfoId(), BigDecimal.valueOf(goods1.getBuyCount()));
    }

    public void refreshgoodswarestock(List<String>stringList,Long wareId){
        if (CollectionUtils.isNotEmpty(stringList)){
        List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoIds(GoodsWareStockByGoodsForIdsRequest
                .builder()
                .goodsForIdList(stringList)
                .wareId(wareId)
                .build()).getContext().getGoodsWareStockVOList();
        List<TrueStock> jiYastock = historyTownShipOrderService.getskusJiYastock(stringList);
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)){
            GoodsWareStockVO goodsWareStockVO = goodsWareStockVOList.stream().findAny().orElse(null);
            if (Objects.nonNull(goodsWareStockVO)){
                redisCache.put(RedisKeyConstants.GOODS_INFO_STOCK_HASH.concat(stringList.stream().findFirst().get()),RedisKeyConstants.GOODS_INFO_WMS_STOCK,goodsWareStockVO.getStock().doubleValue());
            }else {
                throw new SbcRuntimeException("未查询到库存商品id"+stringList.stream().findFirst().get());
            }
        }
        if (CollectionUtils.isNotEmpty(jiYastock)){
            TrueStock trueStock = jiYastock.stream().findAny().orElse(null);
            BigDecimal stock = BigDecimal.ZERO;
            if (Objects.nonNull(trueStock)){
                stock=trueStock.getStock();
            }
            redisCache.put(RedisKeyConstants.GOODS_INFO_STOCK_HASH.concat(stringList.stream().findFirst().get()),RedisKeyConstants.GOODS_INFO_YK_STOCK,stock.doubleValue());
        }
        }
    }

    /**
     * 购物车添加缓存方法
     * @param customerId
     * @param wareId
     * @param goodsInfoId
     * @param shopCart
     */
    public void setShopCarCache(String customerId, Long wareId, String goodsInfoId, BulkShopCart shopCart){
        redisCache.put(
                BulkShopCartCacheSupport.buildExtraKey(customerId, wareId),
                goodsInfoId,
                JSONObject.toJSONString(shopCart)
        );
    }

    /**
     * 购物车添加缓存数量方法
     * @param customerId
     * @param wareId
     * @param goodsInfoId
     * @param num
     */
    public void setShopCarCacheNum(String customerId, Long wareId, String goodsInfoId, BigDecimal num){
        redisCache.put(
                BulkShopCartCacheSupport.buildKey(customerId, wareId),
                BulkShopCartCacheSupport.buildHashKeyOfGoodNum(goodsInfoId),
                num
        );
    }

    /**
     *  购物车添加缓存是否选中方法
     * @param customerId
     * @param wareId
     * @param goodsInfoId
     * @param flag
     */
    public void setShopCarCacheCheck(String customerId, Long wareId, String goodsInfoId, int flag){
        redisCache.put(
                BulkShopCartCacheSupport.buildKey(customerId, wareId),
                BulkShopCartCacheSupport.buildHashKeyOfIsCheck(goodsInfoId),
                flag
        );
    }

    /**
     * 获取商品再购物车中的选中状态
     * @param customerId
     * @param devaningId
     * @param wareId
     * @return
     */
    public String getShopCarCacheCheck(String customerId, String devaningId, Long wareId){
        String key = BulkShopCartCacheSupport.buildKey(customerId, wareId);
        String nKey = BulkShopCartCacheSupport.buildHashKeyOfIsCheck(devaningId);
        Object o = redisCache.HashGet(key, nKey);
        if(Objects.isNull(o)){
            return null;
        }
        return o.toString();
    }

    /**
     *
     * @param type 类型 1是增 -1 是减
     * @param customerId 用户id
     * @param wareId 仓库id
     * @param devaningId 拆箱表id
     * @param num 数量
     */
    public void updateShopCarCacheNum(String type, String customerId, Long wareId, String devaningId, int num){
        if (!type.equalsIgnoreCase("1")){
            num= (-num);
        }
        redisCache.incrementMap(
                BulkShopCartCacheSupport.buildKey(customerId, wareId),
                BulkShopCartCacheSupport.buildHashKeyOfGoodNum(devaningId),
                num
        );
    }

    public long getShopCarCacheNum(String customerId, Long wareId, String devaningId){
        String key = BulkShopCartCacheSupport.buildKey(customerId, wareId);
        String nkey = BulkShopCartCacheSupport.buildHashKeyOfGoodNum(devaningId);

        if (redisCache.HashHasKey(key, nkey)){
            try {
                long aLong = Long.valueOf(Double.valueOf(redisCache.HashGet(key, nkey).toString()).intValue());
                return aLong;
            }catch (Exception e){
                log.error("数据转化错误"+redisCache.HashGet(key, nkey));
                log.error("数据转化错误"+e.getMessage());
                return 0L;
            }
        }
        return 0L;
   }

    /**
     * 用户购物车缓存数据 需要传入devanningId
     * @param customerId
     * @param wareId
     * @return
     */
    public List<BulkShopCart> getShopCarCache(String customerId, Long wareId, List<String> goodsInfoIds){
        List<BulkShopCart> list2 =new LinkedList<>();
        String cacheKey = BulkShopCartCacheSupport.buildExtraKey(customerId, wareId);

        List<String> list = redisCache.multiGet(cacheKey, goodsInfoIds);
        if (CollectionUtils.isNotEmpty(list)){
            List<BulkShopCart> finalList = list2;
            list.forEach(v->{
                finalList.add(JSON.parseObject(v, BulkShopCart.class));
            });
            //赋值数量 和选中状态
            list2.forEach(v->{
                v.setGoodsNum(getShopCarCacheNum(customerId,wareId,v.getGoodsInfoId()));

                Object o = redisCache.HashGet(
                        BulkShopCartCacheSupport.buildKey(customerId, wareId),
                        BulkShopCartCacheSupport.buildHashKeyOfIsCheck(v.getGoodsInfoId())
                );
                if (o.toString().equalsIgnoreCase("1")){
                    v.setIsCheck(DefaultFlag.YES);
                }else {
                    v.setIsCheck(DefaultFlag.NO);
                }
            });
            return list2;
        }
        //缓存获取并刷新缓存
        BulkShopCartRequest request = BulkShopCartRequest.builder()
                .customerId(customerId).inviteeId(Constants.PURCHASE_DEFAULT)
                .goodsInfoIds(goodsInfoIds)
                .build();
        list2 = bulkShopCartRepository.findAll(request.getWhereCriteria());
        if (CollectionUtils.isNotEmpty(list2)){
            //刷新购物车缓存 todo
            for (BulkShopCart shopCart : list2){
                this.setShopCarCache(customerId,wareId,shopCart.getGoodsInfoId(), shopCart);
                this.setShopCarCacheNum(customerId,wareId,shopCart.getGoodsInfoId(), BigDecimal.valueOf(shopCart.getGoodsNum()));
                this.setShopCarCacheCheck(customerId,wareId,shopCart.getGoodsInfoId(), 1==shopCart.getIsCheck().toValue()?0:1);
            }

        }
        return list2;
    }

    /**
     * 获取散批购物车的商品数量
     * @param customerId
     * @param wareId
     * @return
     */
    public Long getBulkShopCartNum(String customerId, Long wareId){
        List<BulkShopCart> list2 =new LinkedList<>();
        String key = BulkShopCartCacheSupport.buildExtraKey(customerId, wareId);
        Map map = redisCache.HashGetAll(key);
        if (map.isEmpty()){
            BulkShopCartRequest request = BulkShopCartRequest.builder()
                    .customerId(customerId).inviteeId(Constants.PURCHASE_DEFAULT)
                    .wareId(wareId)
                    .build();
            list2 = bulkShopCartRepository.findAll(request.getWhereCriteria());
            if (CollectionUtils.isNotEmpty(list2)){
                for (BulkShopCart shopCart : list2){
                    this.setShopCarCache(customerId,wareId,shopCart.getGoodsInfoId(), shopCart);
                    this.setShopCarCacheNum(customerId,wareId,shopCart.getGoodsInfoId(), BigDecimal.valueOf(shopCart.getGoodsNum()));
                    this.setShopCarCacheCheck(customerId,wareId,shopCart.getGoodsInfoId(), 1==shopCart.getIsCheck().toValue()?0:1);
                }
            }
        } else {
            for(Object o : map.keySet()){
                BulkShopCart shopCart = JSON.parseObject(map.get(o).toString(), BulkShopCart.class);
                //赋值数量 和选中状态
                shopCart.setGoodsNum(getShopCarCacheNum(customerId, wareId, shopCart.getGoodsInfoId()));
                list2.add(shopCart);
            }
        }
        if(CollectionUtils.isEmpty(list2)){
            return NumberUtils.LONG_ZERO;
        }
        return  list2.stream().map(BulkShopCart::getGoodsNum).reduce(Long::sum).orElse(0L);
    }

    /**
     * 不需要传devanningId
     * @param customerId
     * @param wareId
     * @return
     */
    public List<BulkShopCart> getShopCarCache(String customerId, Long wareId){
        List<BulkShopCart> list2 =new LinkedList<>();

        String cacheKey = BulkShopCartCacheSupport.buildExtraKey(customerId, wareId);
        Map<String, String> map = redisCache.hGetAll(cacheKey);

        if (map.isEmpty()){
            BulkShopCartRequest request = BulkShopCartRequest.builder()
                    .customerId(customerId).inviteeId(Constants.PURCHASE_DEFAULT)
                    .wareId(wareId)
                    .build();
            Specification<BulkShopCart> whereCriteria = request.getWhereCriteria();
            list2 = bulkShopCartRepository.findAll(whereCriteria);
            if (CollectionUtils.isNotEmpty(list2)){
                //刷新购物车缓存
                for (BulkShopCart bulkshopCart : list2){
                    this.setShopCarCache(customerId, wareId, bulkshopCart.getGoodsInfoId(), bulkshopCart);
                    this.setShopCarCacheNum(customerId,wareId, bulkshopCart.getGoodsInfoId(), BigDecimal.valueOf(bulkshopCart.getGoodsNum()));
                    this.setShopCarCacheCheck(customerId, wareId, bulkshopCart.getGoodsInfoId(), 1==bulkshopCart.getIsCheck().toValue()?0:1);
                }
            }
            return list2;
        }
        map = redisCache.hGetAll(cacheKey);
        for(String o : map.keySet()){
            BulkShopCart shopCart = JSON.parseObject(map.get(o), BulkShopCart.class);
            //赋值数量 和选中状态
            shopCart.setGoodsNum(getShopCarCacheNum(customerId,wareId,shopCart.getGoodsInfoId()));

            String key = BulkShopCartCacheSupport.buildKey(customerId, wareId);
            String nKey = BulkShopCartCacheSupport.buildHashKeyOfIsCheck(shopCart.getGoodsInfoId());

            Object o1 = redisCache.HashGet(key, nKey);
            if(Objects.isNull(o1)){
                continue;
            }
            if (o1.toString().equalsIgnoreCase("0")){
                shopCart.setIsCheck(DefaultFlag.NO);
            }else {
                shopCart.setIsCheck(DefaultFlag.YES);
            }
            list2.add(shopCart);
        }
        return list2;
    }

    public void updateList(List<BulkShopCart> shopCartList) {
        bulkShopCartRepository.saveAll(shopCartList);
    }
    /**
     * 异步更新选择状态
     *
     * @param request
     * @param goodsInfoIds
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateIsCheck(BulkShopCartRequest request, List<String> goodsInfoIds) throws SbcRuntimeException {
        List<BulkShopCart> shopCartList = bulkShopCartRepository.findAll(request.getWhereCriteria());
        List<BulkShopCart> saveShopCartList = new ArrayList<>();
        for (BulkShopCart shopCart : shopCartList) {
            if (goodsInfoIds.contains(shopCart.getGoodsInfoId()) && DefaultFlag.NO.equals(shopCart.getIsCheck())) {
                shopCart.setIsCheck(DefaultFlag.YES);
                saveShopCartList.add(shopCart);
            }
            if (!(goodsInfoIds.contains(shopCart.getGoodsInfoId())) && DefaultFlag.YES.equals(shopCart.getIsCheck())) {
                shopCart.setIsCheck(DefaultFlag.NO);
                saveShopCartList.add(shopCart);
            }
        }
        if (saveShopCartList.size() > 0) {
            bulkShopCartRepository.saveAll(saveShopCartList);
        }
    }

    /**
     * 异步更新选择状态
     *
     * @param request
     * @param devanningIdList
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateDevanIsCheck(BulkShopCartRequest request, List<Long> devanningIdList) throws SbcRuntimeException {
        List<BulkShopCart> shopCartList = bulkShopCartRepository.findAll(request.getWhereCriteria());
        List<BulkShopCart> saveShopCartList = new ArrayList<>();
        for (BulkShopCart shopCart : shopCartList) {
            if (devanningIdList.contains(shopCart.getGoodsInfoId()) && DefaultFlag.NO.equals(shopCart.getIsCheck())) {
                shopCart.setIsCheck(DefaultFlag.YES);
                saveShopCartList.add(shopCart);
            }
            if (!(devanningIdList.contains(shopCart.getGoodsInfoId())) && DefaultFlag.YES.equals(shopCart.getIsCheck())) {
                shopCart.setIsCheck(DefaultFlag.NO);
                saveShopCartList.add(shopCart);
            }
        }
        if (saveShopCartList.size() > 0) {
            bulkShopCartRepository.saveAll(saveShopCartList);
        }
    }



    /**
     * 查询采购单
     *
     * @param customerId
     * @param goodsInfoIds
     * @return
     */
    public List<BulkShopCart> queryPurchase(String customerId, List<String> goodsInfoIds, String inviteeId) {
        //分页查询SKU信息列表 mysql 逻辑
        BulkShopCartRequest request = BulkShopCartRequest.builder()
                .customerId(customerId)
                .goodsInfoIds(goodsInfoIds)
                .inviteeId(inviteeId)
                .build();
        Sort sort = request.getSort();
        List<BulkShopCart> list;
        if (Objects.nonNull(sort)) {
            list = bulkShopCartRepository.findAll(request.getWhereCriteria(), sort);
        } else {
            list = bulkShopCartRepository.findAll(request.getWhereCriteria());
        }

        return list;
    }

    /**
     * 获取采购单商品数量
     *
     * @param userId
     * @return
     */
    public Integer countGoods(String userId, String inviteeId) {
        return bulkShopCartRepository.countByCustomerIdAndInviteeId(userId, inviteeId);
    }

    /**
     * 获取采购单商品总数
     *
     * @param userId
     * @return
     */
    public Long queryGoodsNum(String userId, String inviteeId,Long wareId) {
        return bulkShopCartRepository.queryGoodsNum(userId, inviteeId,wareId);
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
        return bulkShopCartRepository.countByCustomerIdAndInviteeIdAndCompanyInfoId(userId, inviteeId, companyInfoId);
    }

    /**
     * 商品收藏调整商品数量
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateNum(BulkShopCartRequest request) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(
                GoodsInfoByIdRequest.builder().goodsInfoId(request.getGoodsInfoId()).wareId(request.getWareId()).build()
        ).getContext();
        if (goodsInfo == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        List<BulkShopCart> shopCartList = bulkShopCartRepository.findAll(BulkShopCartRequest.builder()
                .customerId(request.getCustomerId())
                .goodsInfoId(request.getGoodsInfoId())
                .inviteeId(request.getInviteeId())
                .build().getWhereCriteria());
        //如果存在，更新标识和数量
        if (CollectionUtils.isNotEmpty(shopCartList)) {
            BulkShopCart shopCart = shopCartList.get(0);
            //更新标识：收藏->all
            if (request.getVerifyStock() && goodsInfo.getStock().compareTo(BigDecimal.valueOf(request.getGoodsNum())) < 0 && shopCart.getGoodsNum() < request.getGoodsNum()) {
                throw new SbcRuntimeException("K-030302", new Object[]{goodsInfo.getStock()});
            }
            shopCart.setGoodsNum(request.getGoodsNum());
            bulkShopCartRepository.save(shopCart);
        } else {
            //如果数据不存在，自动加入采购单
            BulkShopCart shopCart = new BulkShopCart();
            BeanUtils.copyProperties(request, shopCart);
            shopCart.setCreateTime(LocalDateTime.now());
            shopCart.setCompanyInfoId(goodsInfo.getCompanyInfoId());
            shopCart.setGoodsId(goodsInfo.getGoodsId());
            shopCart.setIsCheck(DefaultFlag.YES);
            bulkShopCartRepository.save(shopCart);
        }
    }

    /**
     * 商品收藏调整商品数量
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateNumDevanning(BulkShopCartRequest request) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(
                GoodsInfoByIdRequest.builder().goodsInfoId(request.getGoodsInfoId()).wareId(request.getWareId()).build()
        ).getContext();
        if (goodsInfo == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        List<BulkShopCart> shopCartList = bulkShopCartRepository.findAll(BulkShopCartRequest.builder()
                .customerId(request.getCustomerId())
                .goodsInfoId(request.getGoodsInfoId())
                .wareId(request.getWareId())
                .inviteeId(request.getInviteeId())
                .build().getWhereCriteria());
        //如果存在，更新标识和数量
        if (CollectionUtils.isNotEmpty(shopCartList)) {
            BulkShopCart shopCart = shopCartList.get(0);
            //更新标识：收藏->all
            if (request.getVerifyStock() && goodsInfo.getStock().compareTo(BigDecimal.valueOf(request.getGoodsNum())) < 0 && shopCart.getGoodsNum() < request.getGoodsNum()) {
                throw new SbcRuntimeException("K-030302", new Object[]{goodsInfo.getStock()});
            }
            shopCart.setGoodsNum(request.getGoodsNum());
            bulkShopCartRepository.save(shopCart);
        } else {
//            if (countNum >= Constants.PURCHASE_MAX_SIZE) {
//                throw new SbcRuntimeException(SiteResultCode.ERROR_050121, new Object[]{Constants.PURCHASE_MAX_SIZE});
//            }
            //如果数据不存在，自动加入采购单
            BulkShopCart shopCart = new BulkShopCart();
            BeanUtils.copyProperties(request, shopCart);
            shopCart.setCreateTime(LocalDateTime.now());
            shopCart.setCompanyInfoId(goodsInfo.getCompanyInfoId());
            shopCart.setGoodsId(goodsInfo.getGoodsId());
            shopCart.setIsCheck(DefaultFlag.YES);
            shopCart.setWareId(request.getWareId());
            shopCart.setParentGoodsInfoId(goodsInfo.getParentGoodsInfoId());
            bulkShopCartRepository.save(shopCart);
        }
    }


    /**
     * 购物车调整数量
     *
     * @param request 参数
     * @param markeingId 商品选择的营销
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateNumDevanningAndCache(BulkShopCartRequest request, Long markeingId) {

        // 检查购物车商品数量是否达到上限
        checkCartProductThreshold(request.getCustomerId(), request.getWareId());

        GoodsInfoVO goodsInfo = bulkGoodsInfoQueryProvider.getBulkById(
                GoodsInfoByIdRequest.builder().goodsInfoId(request.getGoodsInfoId()).wareId(request.getWareId()).build()
        ).getContext();
        if (goodsInfo == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        // 校验商品可售状态
        checkSkuSaleableStatus(request, markeingId);

        //去缓存查询是否存在
        String key =  BulkShopCartCacheSupport.buildExtraKey(request.getCustomerId(), request.getWareId());
        String nKey = request.getGoodsInfoId(); // 内Key
        Boolean cacheExists = redisCache.HashHasKey(key, nKey);

        BulkShopCart shopCart = new BulkShopCart();
        if (Boolean.FALSE.equals(cacheExists)){
            //不存在新增实体
            //如果为空还加加购物车缓存  使用mq存储到mysql
            shopCart.setCartId(YitIdHelper.nextId());
            shopCart.setGoodsId(goodsInfo.getGoodsId());
            shopCart.setGoodsNum(request.getGoodsNum());
            shopCart.setCompanyInfoId(goodsInfo.getCompanyInfoId());
            shopCart.setGoodsInfoId(goodsInfo.getGoodsInfoId());
            shopCart.setInviteeId(request.getInviteeId());
            shopCart.setCustomerId(request.getCustomerId());
            shopCart.setWareId(request.getWareId());
            shopCart.setCreateTime(Objects.nonNull(request.getCreateTime()) ? request.getCreateTime() :
                    LocalDateTime.now());
            this.setShopCarCache(request.getCustomerId(),request.getWareId(), request.getGoodsInfoId(), shopCart);
            this.setShopCarCacheCheck(request.getCustomerId(), request.getWareId(), request.getGoodsInfoId(), DefaultFlag.YES.toValue());
        } else {
            Object o = redisCache.HashGet(key, nKey);
            shopCart = JSON.parseObject(o.toString(), BulkShopCart.class);
            log.info("ShopCart Cache: {}",shopCart);
            shopCart.setGoodsNum(request.getGoodsNum());

            // 如果购物车选中缓存不存在，才插入用户选中状态。
            String shopCarCacheCheck = this.getShopCarCacheCheck(request.getCustomerId(), request.getGoodsInfoId(), request.getWareId());
            if(Objects.isNull(shopCarCacheCheck)){
                this.setShopCarCacheCheck(request.getCustomerId(), request.getWareId(), request.getGoodsInfoId().toString(),DefaultFlag.YES.toValue());
            }
        }
        //发送rabbitmq添加mysql
        orderProducerService.sendMQForOrderBulkShopCar(KsBeanUtil.convert(shopCart,BulkShopCartVO.class));
        this.setShopCarCacheNum(request.getCustomerId(),request.getWareId(),request.getGoodsInfoId().toString(),BigDecimal.valueOf(request.getGoodsNum()));
    }

    /**
     * 校验购物车商品数量是否超过阈值
     * @param customerId
     * @param wareId
     */
    private void checkCartProductThreshold(String customerId, Long wareId) {
        // 从缓存中获取当前购物车sku数量
        String key = BulkShopCartCacheSupport.buildExtraKey(customerId, wareId);
        Long countNum = redisCache.hLen(key) >> 1;
        if (countNum >= Constants.PURCHASE_MAX_SIZE) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_050121, new Object[]{Constants.PURCHASE_MAX_SIZE});
        }
    }

    /**
     * 校验sku可售状态
     */
    private void checkSkuSaleableStatus(BulkShopCartRequest request, Long markeingId){
        //判断限购
        if (CollectionUtils.isEmpty(checkList)){
            throw new RuntimeException("系统异常---检查链为空");
        }
        List<String> goodsInfolist = Arrays.asList(request.getGoodsInfoId());
        List<GoodsInfoVO> goodsInfos = bulkGoodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().
                goodsInfoIds(goodsInfolist)
                .wareId(request.getWareId())
                .build()).getContext().getGoodsInfos();
        if (CollectionUtils.isEmpty(goodsInfos)){
            throw new RuntimeException("未查询到商品数据");
        }
        Map<String, GoodsInfoVO> devanningGoodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity(), (a, b) -> a));
        //购物车存在数据
        List<DevanningGoodsInfoMarketingVO>list = new LinkedList<>();
        List<BulkShopCart> shopCarCache = this.getShopCarCache(request.getCustomerId(), request.getWareId());
        if (CollectionUtils.isNotEmpty(shopCarCache)){
            shopCarCache=shopCarCache.stream().filter(v->{
                if (v.getIsCheck().equals(DefaultFlag.YES)){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            shopCarCache.forEach(pa->{
                if (pa.getGoodsInfoId().equalsIgnoreCase(request.getGoodsInfoId()) && !Objects.equals(request.getGoodsInfoId(), pa.getGoodsInfoId())){
                    if (Objects.isNull(devanningGoodsInfoVOMap.get(pa.getGoodsInfoId()))   ){
                        throw new RuntimeException("未匹配到数据");
                    }
                    DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO =new DevanningGoodsInfoMarketingVO();
                    devanningGoodsInfoMarketingVO.setBuyCount(pa.getGoodsNum());
                    devanningGoodsInfoMarketingVO.setGoodsInfoId(pa.getGoodsInfoId());
                    devanningGoodsInfoMarketingVO.setSaleType(2);
                    list.add(devanningGoodsInfoMarketingVO);
                }
            });
        }

        DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO =new DevanningGoodsInfoMarketingVO();
        devanningGoodsInfoMarketingVO.setMarketingId(markeingId);
        devanningGoodsInfoMarketingVO.setBuyCount(request.getGoodsNum());
        devanningGoodsInfoMarketingVO.setDivisorFlag(request.getDivisorFlag());
        devanningGoodsInfoMarketingVO.setGoodsInfoId(request.getGoodsInfoId());
        devanningGoodsInfoMarketingVO.setSaleType(2);
        list.add(devanningGoodsInfoMarketingVO);

        for (StockAndPureChainNode checkNode : checkList) {
            StockAndPureChainNodeRsponse result = checkNode.checkStockPure(StockAndPureChainNodeRequeest.builder()
                    .cityId(request.getCityId()).provinceId(request.getProvinceId()).customerId(request.getCustomerId())
                    .wareId(request.getWareId()).needCheack(request.getNeedCheack()).checkPure(list).build());
            if (CollectionUtils.isNotEmpty(result.getCheckPure())){
                DevanningGoodsInfoPureVO devanningGoodsInfoPureVO = result.getCheckPure().stream().findAny().orElse(null);
                if (Objects.nonNull(devanningGoodsInfoPureVO)){
                    if (Objects.nonNull(devanningGoodsInfoPureVO.getType())){
                        switch (devanningGoodsInfoPureVO.getType()){
                            case 0:
                                throw new SbcRuntimeException("k-250001");
                            case 1:
                                throw new SbcRuntimeException("k-250002");
                            case 2:
                                throw new SbcRuntimeException("k-250003");
                            case 3:
                                throw new SbcRuntimeException("k-250004");
                            case -1:
                                throw new SbcRuntimeException("k-250005");
                        }
                    }
                }
            }
        }
    }


    public void devanningNumMergin(BulkShopCartRequest request){
        this.updateNumDevanning(request);
    }


    /**
     * 商品收藏调整商品数量
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void updateNumByIds(BulkShopCartRequest request) {
        log.info("=================================="+request.getGoodsInfos());
        List<BulkShopCart> shopCartList = bulkShopCartRepository.findAll(BulkShopCartRequest.builder()
                .customerId(request.getCustomerId())
                .goodsInfoIds(request.getGoodsInfoIds())
                .inviteeId(request.getInviteeId())
                .build().getWhereCriteria());
        //如果存在，更新标识和数量
        if (CollectionUtils.isNotEmpty(shopCartList)) {
            boolean updateFlag=false;
            for (BulkShopCart param : shopCartList) {
                Optional<GoodsInfoDTO> goodsInfoDTO = request.getGoodsInfos().stream().filter(goods -> goods.getGoodsInfoId()
                        .equals(param.getGoodsInfoId())).findFirst();
                if (goodsInfoDTO.isPresent()) {
                    updateFlag = true;
                    param.setGoodsNum(goodsInfoDTO.get().getStock().compareTo(BigDecimal.ZERO) == 0 ? 0 : goodsInfoDTO.get().getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());
                }
            }
            if (updateFlag){
                bulkShopCartRepository.saveAll(shopCartList);
            }
        }
    }

    /**
     * 删除采购单
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(BulkShopCartRequest request) {

        bulkShopCartRepository.deleteByGoodsInfoids(request.getGoodsInfoIds(), request.getCustomerId(), request.getInviteeId());
        GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest = new GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest();
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setCustomerId(request.getCustomerId());
        goodsMarketingProvider.deleteByCustomerIdAndGoodsInfoIds(goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest);
    }

    /**
     * 采购单商品移入收藏夹
     *
     * @param queryRequest
     * @return
     */
    @Transactional
    public void addFollow(BulkShopCartRequest queryRequest) {
        //获取所有收藏商品
        GoodsCustomerFollowQueryRequest followQueryRequest = GoodsCustomerFollowQueryRequest.builder()
                .customerId(queryRequest.getCustomerId())
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

        List<BulkShopCart> shopCartList = bulkShopCartRepository.queryPurchaseByGoodsIdsAndCustomerId(queryRequest.getGoodsInfoIds(),
                queryRequest.getCustomerId(), queryRequest.getInviteeId());
        List<String> newSkuIdsBack = newSkuIds;
        shopCartList.stream().forEach(info -> {
            GoodsCustomerFollow follow = new GoodsCustomerFollow();
            BeanUtils.copyProperties(info, follow);
            follow.setFollowTime(LocalDateTime.now());
            if (CollectionUtils.isNotEmpty(newSkuIdsBack) && newSkuIdsBack.contains(follow.getGoodsInfoId())) {
                goodsCustomerFollowRepository.save(follow);
            }

            // 非赠品才删除
            if (!queryRequest.getIsGift()) {
                bulkShopCartRepository.delete(info);
            }
        });
    }

    /**
     * 清除失效商品
     *
     * @param userId
     */
    @Transactional
    public void clearLoseGoods(String userId, DistributeChannel distributeChannel) {
        List<BulkShopCart> shopCartList = bulkShopCartRepository.queryPurchaseByCustomerIdAndInviteeId(userId,
                distributeChannel.getInviteeId());
        //筛选商品id
        List<String> goodsIds = shopCartList.stream().map(BulkShopCart::getGoodsInfoId).collect(Collectors.toList());
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
        shopCartList.forEach(item -> {
            GoodsInfoVO goodsInfo = goodsInfoList.stream().filter(goods -> goods.getGoodsInfoId().equals(item
                    .getGoodsInfoId())).findFirst().orElse(null);
            StoreVO store =
                    storeList.stream().filter(store1 -> store1.getStoreId().longValue() == goodsInfo.getStoreId().longValue()).findFirst().orElse(null);
            if (Objects.nonNull(goodsInfo) && Objects.nonNull(store)) {
                Duration duration = Duration.between(store.getContractEndDate(), LocalDateTime.now());
                if (goodsInfo.getAddedFlag() == 0 || goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAuditStatus() == CheckStatus.FORBADE || goodsInfo.getGoodsStatus() == GoodsStatus.INVALID
                        || store.getStoreState() == StoreState.CLOSED || duration.toMinutes() >= 0) {
                    bulkShopCartRepository.delete(item);
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
        List<BulkShopCart> shopCartList = bulkShopCartRepository.findAll(BulkShopCartRequest.builder()
                .customerId(customerId).inviteeId(inviteeId)
                .build().getWhereCriteria());

        this.fillBuyCount(goodsInfoList, shopCartList);
        return goodsInfoList;
    }

    /**
     * 填充客户购买数
     *
     * @param goodsInfoList SKU商品
     * @param followList    SKU商品
     */
    public void fillBuyCount(List<GoodsInfoVO> goodsInfoList, List<BulkShopCart> followList) {
        if (CollectionUtils.isEmpty(goodsInfoList) || CollectionUtils.isEmpty(followList)) {
            return;
        }
        //填充SKU的购买数
        goodsInfoList.stream().forEach(goodsInfo -> {
            goodsInfo.setBuyCount(0L);
            //TODO:填充购买数量
            BulkShopCart shopCart = followList.stream().filter(p -> goodsInfo.getGoodsInfoId().equals(p.getGoodsInfoId())).findFirst().orElse(null);
            if (Objects.nonNull(shopCart)) {
                goodsInfo.setBuyCount(shopCart.getGoodsNum());
            }
        });
    }


    /**
     * 填充客户购买数
     *
     * @param goodsInfoList SKU商品
     * @param followList    SKU商品
     */
    public void fillBuyCountDevanning(List<DevanningGoodsInfoVO> goodsInfoList, List<BulkShopCart> followList) {
        if (CollectionUtils.isEmpty(goodsInfoList) || CollectionUtils.isEmpty(followList)) {
            return;
        }
        //填充SKU的购买数
        goodsInfoList.stream().forEach(goodsInfo -> {
            goodsInfo.setBuyCount(0L);
            //TODO:填充购买数量
            BulkShopCart shopCart = followList.stream().filter(p -> goodsInfo.getGoodsInfoId().equals(p.getGoodsInfoId())).findFirst().orElse(null);
            if (Objects.nonNull(shopCart)) {
                goodsInfo.setBuyCount(shopCart.getGoodsNum());
            }
        });
    }

    /**
     * 填充客户购买数
     *
     * @param devanningGoodsInfoVOList SKU商品
     * @param followList    SKU商品
     */
    public void fillBuyDevanningCount(List<DevanningGoodsInfoVO> devanningGoodsInfoVOList, List<BulkShopCart> followList) {
        if (CollectionUtils.isEmpty(devanningGoodsInfoVOList) || CollectionUtils.isEmpty(followList)) {
            return;
        }
        //填充SKU的购买数
        devanningGoodsInfoVOList.stream().forEach(devanningGoodsInfoVO -> {
            devanningGoodsInfoVO.setBuyCount(0L);
            BulkShopCart shopCart = followList.stream().filter(p -> devanningGoodsInfoVO.getGoodsInfoId().equals(p.getGoodsInfoId())).findFirst().orElse(null);
            if (Objects.nonNull(shopCart)) {
                devanningGoodsInfoVO.setBuyCount(shopCart.getGoodsNum());
            }
        });
    }

    /**
     * 获取采购单中 参加同种活动的商品列表/总额/优惠
     * @param activityId
     * @param customer
     * @param wareId
     * @return
     */
    public PurchaseMarketingCalcResponse calcCouponActivityByActivityIdBase(String activityId, CustomerVO customer, Long wareId) {
        PurchaseMarketingCalcResponse response = new PurchaseMarketingCalcResponse();
        long lackCount = 0L;
        BigDecimal lackAmount = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        List<GoodsInfoVO> goodsInfoList = Collections.EMPTY_LIST;

        //查询该活动的详情
        CouponActivityGetDetailByIdAndStoreIdRequest couponActivityRequest = new CouponActivityGetDetailByIdAndStoreIdRequest();
        couponActivityRequest.setId(activityId);
        CouponActivityDetailResponse couponActivityResponse = couponActivityQueryProvider
                .getDetailByIdAndStoreId(couponActivityRequest).getContext();

        //如果未登录 取最低等级
        if (Objects.isNull(customer)) {
            //满金额
            BigDecimal levelAmount;
            // 满数量
            // 计算达到营销级别的数量
            Long levelCount;
            switch (couponActivityResponse.getCouponActivity().getCouponActivityFullType()) {
                case ALL:
                    levelCount = (long) couponActivityResponse.getGoodsInfoVOS().size();
                    break;
                case ANY_ONE:
                    levelCount = 1L;
                    break;
                case FULL_AMOUNT:
                    levelAmount = couponActivityResponse.getCouponActivityLevelVOS().get(0).getFullAmount();
                    response.setCouponActivityLevelVO(couponActivityResponse.getCouponActivityLevelVOS().get(0));
                    response.setCouponActivityLevelVOS(couponActivityResponse.getCouponActivityLevelVOS());
                    break;
                case FULL_COUNT:
                    levelCount = couponActivityResponse.getCouponActivityLevelVOS().get(0).getFullCount();
                    response.setCouponActivityLevelVO(couponActivityResponse.getCouponActivityLevelVOS().get(0));
                    response.setCouponActivityLevelVOS(couponActivityResponse.getCouponActivityLevelVOS());
                    break;
                default:
                    levelAmount = BigDecimal.ZERO;
            }
        } else {
            GoodsInfoViewByIdsResponse goodsInfoResponse;
            GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();

            List<BulkShopCart> follows = this.getShopCarCache(customer.getCustomerId(),wareId);

            goodsInfoRequest.setGoodsInfoIds(follows.stream().map(BulkShopCart::getGoodsInfoId).collect(Collectors.toList()));
            goodsInfoRequest.setWareId(wareId);
            goodsInfoResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
            //填充SKU的购买数
            this.fillBuyCount(goodsInfoResponse.getGoodsInfos(), follows);

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
                if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())
                        && Objects.nonNull(g) && Objects.nonNull(g.getVipPrice())
                        && g.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                    g.setMarketPrice(g.getVipPrice());
                }
            });

            // 过滤出参加活动的商品
            List<GoodsInfoVO> couponActivityGoodsVOS = couponActivityResponse.getGoodsInfoVOS();
            goodsInfoList = goodsInfoResponse.getGoodsInfos().stream()
                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK
                            && couponActivityGoodsVOS.stream().anyMatch(activityGoods -> activityGoods.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())))
                    .collect(Collectors.toList());

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

            //满金额
            BigDecimal levelAmount;
            // 满数量
            // 计算达到营销级别的数量
            Long levelCount = 0L;
            // 根据不痛的优惠券活动类型，计算满足条件的营销等级里最小的一个，如果不满足营销等级里任意一个，则默认取最低等级
            switch (couponActivityResponse.getCouponActivity().getCouponActivityFullType()) {
                case ALL:
                    //是否全满足每件商品一件
                    if (goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList())
                            .containsAll(couponActivityGoodsVOS.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()))) {
                        response.setLack(BigDecimal.ZERO);
                    } else {
                        response.setLack(BigDecimal.valueOf(couponActivityGoodsVOS.size() - goodsInfoList.size()));
                    }
                    break;
                case ANY_ONE:
                    if (totalCount > 0) {
                        response.setLack(BigDecimal.ZERO);
                    } else {
                        response.setLack(BigDecimal.ONE);
                    }

                    break;
                case FULL_AMOUNT:
                    response.setLack(BigDecimal.ONE);
                    couponActivityResponse.getCouponActivityLevelVOS()
                            .sort(Comparator.comparing(CouponActivityLevelVO::getFullAmount));
                    // 计算达到营销级别的差额
                    lackAmount = couponActivityResponse.getCouponActivityLevelVOS().get(0).getFullAmount().compareTo(totalAmount) <= 0 ? BigDecimal.ZERO :
                            couponActivityResponse.getCouponActivityLevelVOS().get(0).getFullAmount().subtract(totalAmount);

                    response.setLack(lackAmount);
                    break;
                case FULL_COUNT:
                    response.setLack(BigDecimal.ONE);
                    couponActivityResponse.getCouponActivityLevelVOS()
                            .sort(Comparator.comparing(CouponActivityLevelVO::getFullCount));
                    // 计算达到营销级别缺少的数量
                    lackCount = couponActivityResponse.getCouponActivityLevelVOS().get(0).getFullCount().compareTo(totalCount) <= 0 ? 0L :
                            couponActivityResponse.getCouponActivityLevelVOS().get(0).getFullCount() - totalCount.longValue();

                    response.setLack(BigDecimal.valueOf(lackCount));
                    break;
                default:
                    response.setLack(BigDecimal.ONE);
            }

            response.setGoodsInfoList(goodsInfoList);
            response.setTotalCount(totalCount);
            response.setTotalAmount(totalAmount);
        }
        response.setCouponActivityVO(couponActivityResponse.getCouponActivity());
        return response;
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
        log.info("ShopCartService calcMarketingByMarketingIdBase start");
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
                List<BulkShopCart> follows = this.getShopCarCache(customer.getCustomerId(),wareId);


                goodsInfoRequest.setGoodsInfoIds(follows.stream().map(BulkShopCart::getGoodsInfoId).collect(Collectors.toList()));
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
                if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())
                        && Objects.nonNull(g) && Objects.nonNull(g.getVipPrice())
                        && g.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                    g.setMarketPrice(g.getVipPrice());
                }
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
                                    GoodsMarketingVO::getMarketingId,(a,b)->a));

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
            response.getFullGiftLevelList().forEach(v->{
                List<MarketingFullGiftDetailVO> fullGiftDetailList = new LinkedList<>();
                v.getFullGiftDetailList().forEach(q->{
                    fullGiftDetailList.add(q);
                    if (q.getTerminationFlag().equals(BoolFlag.YES)){
                    fullGiftDetailList.remove(q);
                    }
                });
                v.setFullGiftDetailList(fullGiftDetailList);
            });


            List<String> skuIds =
                    response.getFullGiftLevelList().stream().flatMap(marketingFullGiftLevel -> marketingFullGiftLevel.getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId)).distinct().collect(Collectors.toList());
            GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                    .goodsInfoIds(skuIds)
                    .isHavSpecText(Constants.yes)
                    .wareId(wareId)
                    .build();

            GoodsInfoViewByIdsResponse context = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
            //给赠品赋值状态
            if (CollectionUtils.isNotEmpty(context.getGoodsInfos())){
                for (MarketingFullGiftLevelVO i:response.getFullGiftLevelList()){
                    for (MarketingFullGiftDetailVO q : i.getFullGiftDetailList()){
                        if (skuIds.contains(q.getProductId())){
                            for (GoodsInfoVO n:context.getGoodsInfos()){
                                if (n.getGoodsInfoId().equalsIgnoreCase(q.getProductId())){
                                    Long marketingId1 = q.getMarketingId();
                                    Long giftLevelId = q.getGiftLevelId();
                                    String productId = q.getProductId();
                                    String key = marketingId1.toString()+giftLevelId.toString()+productId;
                                    String o = redisService.getString(key);
                                    log.info("赠品库存数量：{}->{}", key, o);
                                    if (Objects.nonNull(o)){
                                        Long num = Long.parseLong(o); // 赠品剩余可赠数量
                                        log.info("赠品{}在营销活动{}下的库存数量：{}", productId, marketingId1, num);
                                        if (num.compareTo(0l)<=0){
                                            n.setGoodsStatus(GoodsStatus.OUT_GIFTS_STOCK);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                List<GoodsInfoVO> collect = context.getGoodsInfos().stream().sorted
                        (Comparator.comparing(GoodsInfoVO::getGoodsStatus)
                                .thenComparing(GoodsInfoVO::getMarketPrice, Comparator.reverseOrder())).collect(Collectors.toList());
                context.setGoodsInfos(collect);
            }

            response.setGiftGoodsInfoResponse(KsBeanUtil.convert(context, PurchaseGoodsViewVO.class));
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
            Map<String, List<MarketingViewVO>> marketingMap = marketingPluginQueryProvider.getByGoodsInfoListAndCustomer(
                    MarketingPluginByGoodsInfoListAndCustomerRequest.builder().goodsInfoList(goodsInfoList)
                            .customerDTO(customerDTO).build()).getContext().getMarketingMap();

            sb.append(",marketingPluginQueryProvider.getByGoodsInfoListAndCustomer end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();
//            Map<String, List<MarketingViewVO>> marketingMap= new HashMap<>();
//            //过滤限购营销
//            this.filterMarketing(marketingMap,marketingMapCount,goodsInfoList);
//            if (Objects.isNull(marketingMap)) {
//                marketingResponse.setMap(resMap);
//                marketingResponse.setGoodsInfos(goodsInfos);
//                return marketingResponse;
//            }
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
//            ShopCartRequest request = ShopCartRequest.builder()
//                    .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
//                    .goodsInfoIds(skuIds)
//                    .build();
//            List<Purchase> follows;
//            Sort sort = request.getSort();
//            if (Objects.nonNull(sort)) {
//                follows = shopCartRepository.findAll(request.getWhereCriteria(), sort);
//            } else {
//                follows = shopCartRepository.findAll(request.getWhereCriteria());
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
                                                                                    Long wareId,
                                                                                    List<GoodsInfoVO> goodsInfoVOS,
                                                                                    List<GoodsInfoVO> goodsInfoVOList,
                                                                                    PurchaseStoreMarketingRequest request) {
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
    public Map<Long, List<PurchaseMarketingCalcResponse>> getDevanningStoreMarketingBaseCopy(List<GoodsMarketingDTO> goodsMarketings,
                                                                                             CustomerVO customer,
                                                                                             PurchaseFrontRequest frontReq,
                                                                                             List<String> goodsInfoIdList,
                                                                                             Long wareId,
                                                                                             List<DevanningGoodsInfoVO> goodsInfoVOS,
                                                                                             List<GoodsInfoVO> goodsInfoVOList,
                                                                                             List<GoodsInfoVO> goodsInfoVOListenty,
                                                                                             PurchaseStoreMarketingRequest request) {
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
                    responseMap = this.devanningSimpCalcMarketingByMarketingId(marketingGoodsesMap, customer, null,
                            goodsInfoIds, true, goodsInfoIdList, marketingMap, goodsInfoVOS, goodsInfoVOListenty,goodsMarketings);

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
     * 计算拆箱采购单金额
     *
     * @param response
     * @param devanningIds
     */
    public PurchaseListResponse calcDevanningAmount(PurchaseListResponse response, CustomerVO customerVO, List<Long> devanningIds) {

        // 没有勾选商品，默认金额为0
        if (devanningIds == null || devanningIds.isEmpty()) {
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
                response.getDevanningGoodsInfoVOS().stream().filter(goodsInfo ->(devanningIds.contains(goodsInfo.getGoodsInfoId())
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
                        }
                        else if (storeMarketing.getMarketingType() == MarketingType.DISCOUNT) {
                            // 满折
                            log.info("计算总金额========================"+totalAmount);
                            log.info("计算折扣========================"+storeMarketing.getDiscount());

                            discountPrice = totalAmount.multiply(BigDecimal.ONE.subtract(storeMarketing.getDiscount()));
                            log.info("计算优惠金额========================"+discountPrice);
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
                response.getDevanningGoodsInfoVOS().stream()
                        .filter(goodsInfo -> devanningIds.contains(goodsInfo.getGoodsInfoId()) && Objects.equals(goodsInfo.getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED))
                        .map(goodsInfo -> goodsInfo.getDistributionCommission().multiply(BigDecimal.valueOf(goodsInfo.getBuyCount())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalPrice(totalPrice);
        response.setTradePrice(tradePrice);
        response.setDiscountPrice(discountTotalPrice);
        response.setDistributeCommission(distributeCommission);
        return response;
    }



    /**
     * 计算采购单金额
     *
     * @param response
     * @param goodsInfoIds
     */
    public PurchaseListResponse calcAmountDetailed(PurchaseListResponse response, CustomerVO customerVO, List<String> goodsInfoIds) {
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

        AtomicReference<BigDecimal> fullReduce = new AtomicReference<>(BigDecimal.valueOf(0));//满减
        AtomicReference<BigDecimal> nowReduce = new AtomicReference<>(BigDecimal.valueOf(0));//立减
        AtomicReference<BigDecimal> discountReduce = new AtomicReference<>(BigDecimal.valueOf(0));//满折
        AtomicReference<BigDecimal> nowDiscountReduce = new AtomicReference<>(BigDecimal.valueOf(0));//立折
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
                            }
                            else {
                                discountPrice = storeMarketing.getDiscount();
                            }
                            Long buynum = storeMarketing.getGoodsInfoList().stream().map(GoodsInfoVO::getBuyCount).reduce((one, two) ->
                                    one + two).get();
                            // 只有参与营销活动的商品总金额不小于优惠金额时，才返回优惠金额，否则返回商品总额
                            if (totalAmount.compareTo(discountPrice) >= 0) {
                                discountPrice =discountPrice;
                            } else {
                                discountPrice =totalAmount;
                            }
                            if (buynum==1L){
                                //这个就是立减优惠的
                                nowReduce.set(nowReduce.get().add(discountPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
                            }else {
                                fullReduce.set(fullReduce.get().add(discountPrice).setScale(2, BigDecimal.ROUND_HALF_UP));
                            }
                        }
                        else if (storeMarketing.getMarketingType() == MarketingType.DISCOUNT) {
                            // 满折
                            discountPrice = totalAmount.multiply(BigDecimal.ONE.subtract(storeMarketing.getDiscount()));

                            Long buynum = storeMarketing.getGoodsInfoList().stream().map(GoodsInfoVO::getBuyCount).reduce((one, two) ->
                                    one + two).get();

                            // 只有参与营销活动的商品总金额不小于优惠金额时，才返回优惠金额，否则返回商品总额
                            if (totalAmount.compareTo(discountPrice) >= 0) {
                                discountPrice =discountPrice;
                            } else {
                                discountPrice =totalAmount;
                            }
                            if (buynum==1L){
                                //这个是立折优惠
                                nowDiscountReduce.set(nowDiscountReduce.get().add(discountPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
                            }else{
                                discountReduce.set(discountReduce.get().add(discountPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
                            }
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
        response.setMarketingDiscountDetails(MarketingDiscountDetailsVO.builder().discountAmount(discountReduce.get())
                .fullMinusAmount(fullReduce.get()).NowdiscountAmount(nowDiscountReduce.get()).NowReductionAmount(nowReduce.get()).build());
        return response;
    }


    /**
     * 计算采购单金额
     *
     * @param response
     * @param goodsInfoIds
     */
    public PurchaseListResponse calcAmountDetailedDevannng(PurchaseListResponse response, CustomerVO customerVO, List<String> goodsInfoIds,List<Long> devanningIds) {
        log.info("计算采购单金额========================》response：{}",response.getDevanningGoodsInfoVOS());
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




//        BigDecimal marketingGoodsTotalPrice = response.getStoreMarketingMap().values().parallelStream()
//                .flatMap(storeMarketings -> storeMarketings.parallelStream())
//                .map(PurchaseMarketingCalcVO::getTotalAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //todo marketingGoodsTotalPrice 这个就是计算有营销活动的总额没减去优惠金额 自己算
        AtomicReference<BigDecimal> marketingGoodsTotalPrice = new AtomicReference<>(BigDecimal.ZERO);
        response.getDevanningGoodsInfoVOS().forEach(devanningGoodsInfoVO -> {
            marketingGoodsInfoIds.forEach(mkgoodsinfoid->{
                if ( devanningGoodsInfoVO.getGoodsInfoId().equalsIgnoreCase(mkgoodsinfoid) && devanningIds.contains(devanningGoodsInfoVO.getDevanningId())){
                    marketingGoodsTotalPrice.set(marketingGoodsTotalPrice.get().add(BigDecimal.valueOf(devanningGoodsInfoVO.getBuyCount()).multiply(Objects.isNull(devanningGoodsInfoVO.getMarketPrice()) ? BigDecimal.ZERO : devanningGoodsInfoVO.getMarketPrice())));
                }


            });



        });
        // 勾选的商品中没有参与营销的商品总额
        BigDecimal noMarketingGoodsTotalPrice =
                response.getGoodsInfos().stream().filter(goodsInfo ->(devanningIds.contains(goodsInfo.getDevanningId())
                        && !marketingGoodsInfoIds.contains(goodsInfo.getGoodsInfoId()))).map(goodsInfo -> {
                    goodsInfo.setMarketPrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO : goodsInfo
                            .getMarketPrice());
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

        AtomicReference<BigDecimal> fullReduce = new AtomicReference<>(BigDecimal.valueOf(0));//满减
        AtomicReference<BigDecimal> nowReduce = new AtomicReference<>(BigDecimal.valueOf(0));//立减
        AtomicReference<BigDecimal> discountReduce = new AtomicReference<>(BigDecimal.valueOf(0));//满折
        AtomicReference<BigDecimal> nowDiscountReduce = new AtomicReference<>(BigDecimal.valueOf(0));//立折
        // 优惠总额
        BigDecimal discountTotalPrice =
                response.getStoreMarketingMap().values().parallelStream()
                        .flatMap(storeMarketings -> storeMarketings.parallelStream())
                        .map(storeMarketing -> {
                            if (BigDecimal.ZERO.compareTo(storeMarketing.getLack()) == 0) {
                                BigDecimal discountPrice = BigDecimal.ZERO;
                                BigDecimal totalAmount = storeMarketing.getTotalAmount();
                                // BigDecimal totalAmount =marketingGoodsTotalPrice.get();
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
                                    }
                                    else {
                                        discountPrice = storeMarketing.getDiscount();
                                    }
                                    Long buynum = storeMarketing.getGoodsInfoList().stream().map(GoodsInfoVO::getBuyCount).reduce((one, two) ->
                                            one + two).get();
                                    // 只有参与营销活动的商品总金额不小于优惠金额时，才返回优惠金额，否则返回商品总额
                                    if (totalAmount.compareTo(discountPrice) >= 0) {
                                        discountPrice =discountPrice;
                                    } else {
                                        discountPrice =totalAmount;
                                    }
                                    if (buynum==1L){
                                        //这个就是立减优惠的
                                        nowReduce.set(nowReduce.get().add(discountPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
                                    }else {
                                        fullReduce.set(fullReduce.get().add(discountPrice).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    }

                                }



                                else if (storeMarketing.getMarketingType() == MarketingType.DISCOUNT) {
                                    // 满折
                                    discountPrice = totalAmount.multiply(BigDecimal.ONE.subtract(storeMarketing.getDiscount()));
                                    Long buynum = storeMarketing.getGoodsInfoList().stream().map(GoodsInfoVO::getBuyCount).reduce((one, two) ->
                                            one + two).get();

                                    // 只有参与营销活动的商品总金额不小于优惠金额时，才返回优惠金额，否则返回商品总额
                                    if (totalAmount.compareTo(discountPrice) >= 0) {
                                        discountPrice =discountPrice;
                                    } else {
                                        discountPrice =totalAmount;
                                    }
                                    if (buynum==1L){
                                        //这个是立折优惠
                                        nowDiscountReduce.set(nowDiscountReduce.get().add(discountPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
                                    }else{
                                        discountReduce.set(discountReduce.get().add(discountPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
                                    }
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
        BigDecimal totalPrice = marketingGoodsTotalPrice.get().add(noMarketingGoodsTotalPrice);

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
        response.setMarketingDiscountDetails(MarketingDiscountDetailsVO.builder().discountAmount(discountReduce.get())
                .fullMinusAmount(fullReduce.get()).NowdiscountAmount(nowDiscountReduce.get()).NowReductionAmount(nowReduce.get()).build());
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
        log.info("ShopCartService syncGoodsMarketings goodsMarketingSyncRequest:{} \n",goodsMarketingSyncRequest);
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
    public void modifyGoodsMarketing(String goodsInfoId, Long marketingId, CustomerVO customer) {
//        GoodsInfoEditResponse response = goodsInfoService.findById(goodsInfoId);
        GoodsInfoViewByIdResponse response = goodsInfoQueryProvider.getViewById(
                GoodsInfoViewByIdRequest.builder().goodsInfoId(goodsInfoId).build()
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
                    BulkShopCartRequest request = BulkShopCartRequest.builder()
                            .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
                            .goodsInfoIds(skuIds)
                            .build();
                    List<BulkShopCart> follows;
                    Sort sort = request.getSort();
                    if (Objects.nonNull(sort)) {
                        follows = bulkShopCartRepository.findAll(request.getWhereCriteria(), sort);
                    } else {
                        follows = bulkShopCartRepository.findAll(request.getWhereCriteria());
                    }

                    sb.append(",shopCartRepository.findAll end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();

                    goodsInfoRequest.setGoodsInfoIds(follows.stream().map(BulkShopCart::getGoodsInfoId).collect(Collectors.toList()));
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
                    BulkShopCartRequest request = BulkShopCartRequest.builder()
                            .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
                            .goodsInfoIds(skuIds)
                            .build();
                    List<BulkShopCart> follows;
                    Sort sort = request.getSort();
                    if (Objects.nonNull(sort)) {
                        follows = bulkShopCartRepository.findAll(request.getWhereCriteria(), sort);
                    } else {
                        follows = bulkShopCartRepository.findAll(request.getWhereCriteria());
                    }

                    sb.append(",shopCartRepository.findAll end time=");
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

    public Map<Long, PurchaseMarketingCalcResponse> devanningSimpCalcMarketingByMarketingId(Map<Long, List<String>> marketingGoodsMap,
                                                                                            CustomerVO customer,
                                                                                            PurchaseFrontRequest frontReq,
                                                                                            List<String> goodsInfoIds,
                                                                                            boolean isPurchase,
                                                                                            List<String> checkSkuIds,
                                                                                            Map<Long, MarketingForEndVO> marketingMap,
                                                                                            List<DevanningGoodsInfoVO> checkGoodsInfoVOS,
                                                                                            List<GoodsInfoVO> goodsInfoVOListenty,
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
            List<DevanningGoodsInfoVO> goodsInfos = Collections.EMPTY_LIST;
            List<GoodsInfoVO> realyGoodsInfos = Collections.EMPTY_LIST;
            if (!(goodsInfoIds == null || goodsInfoIds.isEmpty())) {
                if (customer != null) {
                    //这里增加了一个逻辑，所有购物车的商品都可参加订单满赠的活动
                    List<String> skuIds = goodsInfoIds;
                    if (isGifFullOrder(goodsInfoIds)) {
                        skuIds = Arrays.asList();
                    }
                    BulkShopCartRequest request = BulkShopCartRequest.builder()
                            .customerId(customer.getCustomerId()).inviteeId(Constants.PURCHASE_DEFAULT)
                            .goodsInfoIds(skuIds)
                            .build();
                    List<BulkShopCart> follows;
                    Sort sort = request.getSort();
                    if (Objects.nonNull(sort)) {
                        follows = bulkShopCartRepository.findAll(request.getWhereCriteria(), sort);
                    } else {
                        follows = bulkShopCartRepository.findAll(request.getWhereCriteria());
                    }

                    sb.append(",shopCartRepository.findAll end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();

                    //填充SKU的购买数
                    this.fillBuyCountDevanning(checkGoodsInfoVOS, follows);

                    sb.append(",fillBuyCount end time=");
                    sb.append(System.currentTimeMillis()-sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    if (checkGoodsInfoVOS != null) {
                        for (DevanningGoodsInfoVO goodsInfo : checkGoodsInfoVOS) {
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
                realyGoodsInfos=goodsInfoVOListenty;
                //设定SKU状态
                devaningFillGoodsStatus(checkGoodsInfoVOS);
                fillGoodsStatus(goodsInfoVOListenty);


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
                                        .goodsInfoDTOList(KsBeanUtil.convert(realyGoodsInfos, GoodsInfoDTO.class))
                                        .customerId(customer != null ? customer.getCustomerId() : null).build()).getContext();

                sb.append(",goodsIntervalPriceProvider.putByCustomerId end time=");
                sb.append(System.currentTimeMillis()-sTm);
                sTm = System.currentTimeMillis();

                goodsIntervalPrices = intervalPriceResponse.getGoodsIntervalPriceVOList();
                realyGoodsInfos = KsBeanUtil.convert(intervalPriceResponse.getGoodsInfoVOList(), GoodsInfoVO.class);

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
            Map<String, GoodsInfoVO> goodsInfoVOMap = realyGoodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
            Map<Long, PurchaseMarketingCalcResponse> responseMap = new HashedMap();

            sb.append(",marketingGoodsMap.foreach start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<String> marketingsGoodsIds=goodsMarketings.stream().map(GoodsMarketingDTO::getGoodsInfoId).collect(Collectors.toList());

            for (Map.Entry<Long, List<String>> map : marketingGoodsMap.entrySet()) {
                if (CollectionUtils.isEmpty(map.getValue())) {
                    continue;
                }
                List<GoodsInfoVO> devanningInfoVOList = map.getValue().stream()
                        .map(goodsInfoVOMap::get)
                        .collect(Collectors.toList());
                devanningInfoVOList.removeIf(Objects::isNull);
                List<GoodsInfoVO> goodsInfoList = KsBeanUtil.convertList(devanningInfoVOList, GoodsInfoVO.class);
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

                    response.setDevanningGoodsInfoVOS(goodsInfos);
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
                    //todo重新合并数据  newGoodsInfos现在为goodsinfo的数据  现在吧购买数量填充  goodsInfos

                    goodsInfos.stream().forEach(v->{
                        v.setBuyCount(v.getDivisorFlag().multiply(BigDecimal.valueOf(v.getBuyCount())).setScale(0,BigDecimal.ROUND_DOWN).longValue());
                    });
                    Map<@NotBlank String, Long> skuidSumNum = goodsInfos.stream().collect(Collectors.groupingBy(DevanningGoodsInfoVO::getGoodsInfoId, Collectors.summingLong(DevanningGoodsInfoVO::getBuyCount)));
                    goodsInfos= goodsInfos.stream().filter(distinctByKey((p) -> (p.getGoodsInfoId()))).collect(Collectors.toList());
                    goodsInfos.forEach(v->{
                        v.setBuyCount(skuidSumNum.get(v.getGoodsInfoId()));
                    });
                    newGoodsInfos.forEach(v->{
                        v.setBuyCount(skuidSumNum.get(v.getGoodsInfoId()));
                    });


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



    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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



    private void  devaningFillGoodsStatus(List<DevanningGoodsInfoVO> goodsInfoVOS){
        goodsInfoVOS.forEach(goodsInfo -> {
            if (DeleteFlag.NO.equals(goodsInfo.getDelFlag())){
                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())
                        && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
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
    public void fillBuyCountInit(List<GoodsInfoVO> goodsInfoList, List<ShopCart> followList, List<String> selectSkuids) {
        if (CollectionUtils.isEmpty(goodsInfoList) || CollectionUtils.isEmpty(followList)) {
            return;
        }
        //填充SKU的购买数
        goodsInfoList.stream().forEach(goodsInfo -> {
            goodsInfo.setBuyCount(0L);
            if (CollectionUtils.isNotEmpty(selectSkuids)){
                if (selectSkuids.contains(goodsInfo.getGoodsInfoId())){
                    Optional<ShopCart> first = followList.stream().filter(purchase -> goodsInfo.getGoodsInfoId().equals(purchase.getGoodsInfoId())).findFirst();
                    first.ifPresent(purchase -> {goodsInfo.setBuyCount(purchase.getGoodsNum());});
                }
            }
        });
    }

    /**
     * 填充客户购买数
     *
     * @param goodsInfoList SKU商品
     * @param followList    SKU商品
     */
    public void devanningFillBuyCountInit(List<DevanningGoodsInfoVO> goodsInfoList, List<BulkShopCart> followList) {
        if (CollectionUtils.isEmpty(goodsInfoList) || CollectionUtils.isEmpty(followList)) {
            return;
        }



        //填充SKU的购买数
        goodsInfoList.stream().forEach(goodsInfo -> {
            goodsInfo.setBuyCount(0L);
            followList.stream().forEach(shopCart -> {
                if (goodsInfo.getGoodsInfoId().equalsIgnoreCase(shopCart.getGoodsInfoId())){
                    goodsInfo.setBuyCount(shopCart.getGoodsNum());
                }

            });
        });
    }


    /**
     * 将实体包装成VO
     * @author zhanglingke
     */
    public PurchaseVO wrapperVo(BulkShopCart shopCart) {
        if (shopCart != null){
            PurchaseVO purchaseVO=new PurchaseVO();
            KsBeanUtil.copyPropertiesThird(shopCart,purchaseVO);
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
                List<Object[]> list = bulkShopCartRepository.querySkuCountGoodsNum(request.getGoodsInfoIds(),request.getCustomerId());
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

    /**
     * 删除采购单
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void devanningDelete(BulkShopCartRequest request) {
        GoodsInfoListByIdsRequest goodsInfoListByIdsRequest = GoodsInfoListByIdsRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).devanningIds(request.getDevanningIds()).build();
        log.info("=============goodsInfoListByIdsRequest:{}",JSONObject.toJSONString(goodsInfoListByIdsRequest));
        List<DevanningGoodsInfoVO> infoVOs = devanningGoodsInfoQueryProvider.listByIds(goodsInfoListByIdsRequest).getContext().getDevanningGoodsInfoVOS();
        //要删除购物车，相同的商品其他仓的也要删除
        if(infoVOs != null && !infoVOs.isEmpty()){
            for (DevanningGoodsInfoVO infoVO : infoVOs) {
                List<DevanningGoodsInfoVO> devanningGoodsInfoVOS =   devanningGoodsInfoQueryProvider.listByParentId(DevanningGoodsInfoListByParentIdRequest.builder().
                        parentGoodsInfoId(infoVO.getParentGoodsInfoId()).build()).getContext().getDevanningGoodsInfoVOS();
                if(devanningGoodsInfoVOS == null || devanningGoodsInfoVOS.isEmpty()){
                    continue;
                }
                for (DevanningGoodsInfoVO vo : devanningGoodsInfoVOS) {
                    if(vo.getGoodsInfoId().equals(infoVO.getGoodsInfoId())){
                        continue;
                    }
                    request.getDevanningIds().add(vo.getDevanningId());
                    request.getGoodsInfoIds().add(vo.getGoodsInfoId());
                }
            }
        }
        bulkShopCartRepository.deleteByGoodsInfoidsAAndDevanningId(request.getCustomerId(),
                request.getInviteeId(),request.getDevanningIds());
        GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest = new GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest();
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setCustomerId(request.getCustomerId());
        goodsMarketingProvider.deleteByCustomerIdAndGoodsInfoIds(goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest);

    }


    /**
     * 删除采购单
     *
     * @param request 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void devanningDeleteCache(BulkShopCartRequest request) {
        List<BulkShopCart> list = new ArrayList<>();
        //查询其他仓库是该商品的devaingid
        GoodsInfoListByIdsRequest goodsInfoListByIdsRequest = GoodsInfoListByIdsRequest.builder()
                .goodsInfoIds(request.getGoodsInfoIds())
                .build();
        List<GoodsInfoVO> goodsInfos = bulkGoodsInfoQueryProvider.listByIds(goodsInfoListByIdsRequest).getContext().getGoodsInfos();
        //要删除购物车，相同的商品其他仓的也要删除
        if(CollectionUtils.isNotEmpty(goodsInfos)){
            for (GoodsInfoVO infoVO : goodsInfos) {
//                DevanningGoodsInfoListByParentIdRequest build = DevanningGoodsInfoListByParentIdRequest.builder()
//                        .parentGoodsInfoId(infoVO.getParentGoodsInfoId())
//                        .build();
//                List<DevanningGoodsInfoVO> devanningGoodsInfoVOS =  bulkGoodsInfoQueryProvider
//                        .listByParentId(build)
//                        .getContext()
//                        .getDevanningGoodsInfoVOS();
//                if(CollectionUtils.isEmpty(devanningGoodsInfoVOS)){
//                    continue;
//                }
//                for (DevanningGoodsInfoVO vo : devanningGoodsInfoVOS) {
                    BulkShopCart shopCart = new BulkShopCart();
                    shopCart.setGoodsInfoId(infoVO.getGoodsInfoId());
                    shopCart.setWareId(infoVO.getWareId());
                    list.add(shopCart);
//                }
            }
        }
        //查询缓存是否存在数据
        list.stream().forEach(v->{
            String key  = BulkShopCartCacheSupport.buildExtraKey(request.getCustomerId(), v.getWareId());
            String nKey = BulkShopCartCacheSupport.buildKey(request.getCustomerId(), v.getWareId());

            if (redisCache.HashHasKey(key,v.getGoodsInfoId())){
                Object shopCartStr = redisCache.HashGet(key, v.getGoodsInfoId());
                ShopCart shopCart = JSON.parseObject(shopCartStr.toString(), ShopCart.class);
                redisCache.hashDel(key, v.getGoodsInfoId());
                redisCache.hashDel(nKey,  BulkShopCartCacheSupport.buildHashKeyOfIsCheck(v.getGoodsInfoId()));
                redisCache.hashDel(nKey,  BulkShopCartCacheSupport.buildHashKeyOfGoodNum(v.getGoodsInfoId()));
                v.setCartId(shopCart.getCartId());
                v.setIsDelFlag(true);
                orderProducerService.sendMQForOrderBulkShopCar(KsBeanUtil.convert(v,BulkShopCartVO.class));
            }
        });

        //删除mongo 商品对应营销表
        GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest = new GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest();
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest.setCustomerId(request.getCustomerId());
        goodsMarketingProvider.deleteByCustomerIdAndGoodsInfoIds(goodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest);
    }

}
