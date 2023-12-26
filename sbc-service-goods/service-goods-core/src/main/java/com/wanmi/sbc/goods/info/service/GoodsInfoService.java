package com.wanmi.sbc.goods.info.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoQueryByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoQueryByIdsResponse;
import com.wanmi.sbc.customer.api.response.store.ListNoDeleteStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsStockErrorCode;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.enterprise.goods.EnterpriseAuditCheckRequest;
import com.wanmi.sbc.goods.api.request.enterprise.goods.EnterpriseAuditStatusBatchRequest;
import com.wanmi.sbc.goods.api.request.enterprise.goods.EnterprisePriceUpdateRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.request.merchantconfig.MerchantConfigGoodsQueryRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoAreaLimitPageResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoOnlyShelflifeResponse;
import com.wanmi.sbc.goods.bean.dto.*;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRepository;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.repository.GoodsCateRepository;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.distributor.goods.repository.DistributiorGoodsInfoRepository;
import com.wanmi.sbc.goods.distributor.goods.service.DistributorGoodsInfoService;
import com.wanmi.sbc.goods.goodsattributekey.request.GoodsAtrrKeyQueryRequest;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import com.wanmi.sbc.goods.goodsattributekey.service.GoodsAttributeKeyService;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodslabel.service.GoodsLabelService;
import com.wanmi.sbc.goods.goodslockrecords.model.root.GoodsLockRecords;
import com.wanmi.sbc.goods.goodslockrecords.repository.GoodsLockRecordsRepository;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import com.wanmi.sbc.goods.goodsrecommendgoods.service.GoodsRecommendGoodsService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStockVillages;
import com.wanmi.sbc.goods.goodswarestock.repository.GoodsWareStockRepository;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockVillagesService;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.info.model.entity.GoodsInfoParams;
import com.wanmi.sbc.goods.info.model.entity.OrderSalesRanking;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.GoodsInfoPresellRecord;
import com.wanmi.sbc.goods.info.reponse.*;
import com.wanmi.sbc.goods.info.repository.GoodsInfoPresellRecordRepository;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.GoodsPropDetailRelRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsInfoRequest;
import com.wanmi.sbc.goods.info.request.*;
import com.wanmi.sbc.goods.merchantconfig.root.MerchantRecommendGoods;
import com.wanmi.sbc.goods.merchantconfig.service.MerchantConfigGoodsService;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.repository.GoodsCustomerPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsIntervalPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsLevelPriceRepository;
import com.wanmi.sbc.goods.redis.RedisCache;
import com.wanmi.sbc.goods.redis.RedisKeyConstants;
import com.wanmi.sbc.goods.redis.RedisService;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecDetailRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecRepository;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRepository;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.service.StoreCateService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.purchase.PilePurchaseRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.trade.TradeGetByIdResponse;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Log4j2
public class GoodsInfoService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private GoodsSpecRepository goodsSpecRepository;

    @Autowired
    private GoodsSpecDetailRepository goodsSpecDetailRepository;

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;

    @Autowired
    private GoodsIntervalPriceRepository goodsIntervalPriceRepository;

    @Autowired
    private GoodsLevelPriceRepository goodsLevelPriceRepository;

    @Autowired
    private GoodsCustomerPriceRepository goodsCustomerPriceRepository;

    @Autowired
    private GoodsBrandRepository goodsBrandRepository;

    @Autowired
    private GoodsCateRepository goodsCateRepository;

    @Autowired
    private GoodsPropDetailRelRepository goodsPropDetailRelRepository;

    @Autowired
    private StandardGoodsRelRepository standardGoodsRelRepository;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private StoreCateService storeCateService;

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    @Autowired
    private GoodsCateService goodsCateService;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private DistributorGoodsInfoService distributorGoodsInfoService;

    @Autowired
    private StandardGoodsRepository standardGoodsRepository;

    @Autowired
    private GoodsWareStockRepository goodsWareStockRepository;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private GoodsWareStockVillagesService goodsWareStockVillagesService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GoodsLabelService goodsLabelService;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsRecommendGoodsService goodsRecommendGoodsService;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private DevanningGoodsInfoRepository devanningGoodsInfoRepository;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private GoodsLockRecordsRepository goodsLockRecordsRepository;
    @Autowired
    private MerchantConfigGoodsService merchantConfigGoodsService;
    @Autowired
    private MerchantConfigGoodsSaveProvider merchantConfigGoodsSaveProvider;
    @Autowired
    private DistributiorGoodsInfoRepository distributiorGoodsInfoRepository;

    @Autowired
    private GoodsAttributeKeyService goodsAttributeKeyService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private GoodsInfoPresellRecordRepository goodsInfoPresellRecordRepository;

    /**
     * SKU分页
     *
     * @param queryRequest 查询请求
     * @return 商品分页响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public GoodsInfoResponse pageView(GoodsInfoQueryRequest queryRequest) {
        if (StringUtils.isNotBlank(queryRequest.getLikeGoodsNo())
                || queryRequest.getStoreCateId() != null
                || CollectionUtils.isNotEmpty(queryRequest.getBrandIds())
                || CollectionUtils.isNotEmpty(queryRequest.getCateIds())
                || (queryRequest.getCateId() != null && queryRequest.getCateId() > 0)
                || queryRequest.getBrandId() != null && queryRequest.getBrandId() > 0) {
            GoodsQueryRequest goodsQueryRequest = GoodsQueryRequest.builder()
                    .likeGoodsNo(queryRequest.getLikeGoodsNo())
                    .brandId(queryRequest.getBrandId())
                    .brandIds(queryRequest.getBrandIds())
                    .cateId(queryRequest.getCateId())
                    .cateIds(queryRequest.getCateIds()).build();

            //获取该分类的所有子分类
            if (queryRequest.getCateId() != null && queryRequest.getCateId() > 0) {
                goodsQueryRequest.setCateIds(goodsCateService.getChlidCateId(queryRequest.getCateId()));
                goodsQueryRequest.getCateIds().add(queryRequest.getCateId());
            }
            //批量分类
            if (CollectionUtils.isNotEmpty(queryRequest.getCateIds())) {
                List<Long> cateIds = new ArrayList<>();
                for (Long cateId : queryRequest.getCateIds()) {
                    List<Long> childCateIds = goodsCateService.getChlidCateId(cateId);
                    if (CollectionUtils.isNotEmpty(childCateIds)) {
                        cateIds.addAll(childCateIds);
                    }
                    cateIds.add(cateId);
                }
                goodsQueryRequest.setCateIds(cateIds.stream().distinct().collect(Collectors.toList()));
            }

            //获取该店铺分类下的所有spuIds
            if (queryRequest.getStoreCateId() != null && queryRequest.getStoreCateId() > 0) {
                List<StoreCateGoodsRela> relas = storeCateService.findAllChildRela(queryRequest.getStoreCateId(), true);
                if (CollectionUtils.isNotEmpty(relas)) {
                    goodsQueryRequest.setStoreCateGoodsIds(relas.stream().map(StoreCateGoodsRela::getGoodsId).collect(Collectors.toList()));
                } else {
                    return GoodsInfoResponse.builder().goodsInfoPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageRequest(), 0)).build();
                }
            }

            log.info("魔方查询打印日志1----------->{}",JSONObject.toJSONString(goodsQueryRequest));
            List<Goods> goods = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
            if (CollectionUtils.isEmpty(goods)) {
                return GoodsInfoResponse.builder().goodsInfoPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageRequest(), 0)).build();
            }
            queryRequest.setGoodsIds(goods.stream().map(Goods::getGoodsId).collect(Collectors.toList()));
        }

        //分页查询SKU信息列表
        log.info("魔方查询打印日志2----------->{}",JSONObject.toJSONString(queryRequest));
        Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(queryRequest.getWhereCriteria(), queryRequest.getPageRequest());

        MicroServicePage<GoodsInfo> microPage = KsBeanUtil.convertPage(goodsInfoPage, GoodsInfo.class);
        if (Objects.isNull(microPage) || microPage.getTotalElements() < 1 || CollectionUtils.isEmpty(microPage.getContent())) {

            return GoodsInfoResponse.builder().goodsInfoPage(microPage).build();
        }
        log.info("魔方查询打印日志3----------->{}",JSONObject.toJSONString(microPage.getContent().size()));
        // 商户库存转供应商库存
        //this.turnProviderStock(microPage.getContent());

        //查询SPU
        List<String> goodsIds = microPage.getContent().stream().map(GoodsInfo::getGoodsId).distinct().collect(Collectors.toList());
        log.info("魔方查询打印日志4----------->{}",JSONObject.toJSONString(microPage.getContent().size()));
        List<Goods> goodses = goodsRepository.findAll(GoodsQueryRequest.builder().goodsIds(goodsIds).build().getWhereCriteria());
        log.info("魔方查询打印日志5----------->{}",JSONObject.toJSONString(microPage.getContent().size()));
        Map<String, Goods> goodsMap = goodses.stream().collect(Collectors.toMap(Goods::getGoodsId, goods -> goods));
        //查询规格明细关联表
        List<String> skuIds = microPage.getContent().stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        log.info("魔方查询打印日志6----------->{}",JSONObject.toJSONString(microPage.getContent().size()));
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId));
        //分仓信息重新查询库存
        if (Objects.nonNull(queryRequest.getWareId()) && CollectionUtils.isNotEmpty(skuIds)) {
            List<GoodsWareStock> goodsWareStocks = goodsWareStockRepository.findByGoodsInfoIdIn(skuIds);
            List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(skuIds);

            Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(skuIds);

            microPage.getContent().forEach(inner -> {
                BigDecimal orDefault = getskusstock.getOrDefault(inner.getGoodsInfoId(), BigDecimal.ZERO);
                Optional<GoodsWareStock> first = goodsWareStocks.stream().filter(param -> param.getWareId().equals(queryRequest.getWareId())
                        && param.getGoodsInfoId().equals(inner.getGoodsInfoId())).findFirst();
                Optional<GoodsWareStockVillages> optionalVillages = goodsWareStockVillagesList.stream()
                        .filter(f -> f.getWareId().equals(queryRequest.getWareId()) && f.getGoodsInfoId().equals(inner.getGoodsInfoId())).findFirst();
                inner.setStock(orDefault);
//                if (first.isPresent()) {
//                    if (optionalVillages.isPresent()) {
//                        if (first.get().getStock().subtract(optionalVillages.get().getStock()).compareTo(BigDecimal.ZERO)   <= 0) {
//                            inner.setStock(BigDecimal.ZERO);
//                        } else {
//                            inner.setStock(first.get().getStock().subtract(optionalVillages.get().getStock())  );
//                        }
//                    } else {
//                        inner.setStock(first.get().getStock());
//                    }
////                    if(Objects.nonNull(inner.getMarketingId()) && inner.getPurchaseNum() > 0){
////                        inner.setStock(inner.getPurchaseNum());
////                    }
//                } else {
//                    inner.setStock(BigDecimal.ZERO);
////                    if(Objects.nonNull(inner.getMarketingId()) && inner.getPurchaseNum() > 0){
////                        inner.setStock(inner.getPurchaseNum());
////                    }
//                }
            });
        }
        log.info("魔方查询打印日志7----------->{}",JSONObject.toJSONString(microPage.getContent().size()));
        List<GoodsBrand> brands = goodsBrandRepository.findAll(GoodsBrandQueryRequest.builder().delFlag(DeleteFlag.NO.toValue()).brandIds(goodses.stream().map(Goods::getBrandId).collect(Collectors.toList())).build().getWhereCriteria());
        log.info("魔方查询打印日志8----------->{}",JSONObject.toJSONString(microPage.getContent().size()));
        List<GoodsCate> cates = goodsCateRepository.findAll(GoodsCateQueryRequest.builder().cateIds(goodses.stream().map(Goods::getCateId).collect(Collectors.toList())).build().getWhereCriteria());

        microPage.getContent().forEach(goodsInfo -> {
            //明细组合->，规格值1 规格值2
            if (MapUtils.isNotEmpty(specDetailMap)) {
                goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                        .map(GoodsInfoSpecDetailRel::getDetailName)
                        .collect(Collectors.toList()), " "));
            }

            //原价
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());

            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            if (goods != null) {
                goodsInfo.setSaleType(goods.getSaleType());
                goodsInfo.setAllowPriceSet(goods.getAllowPriceSet());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                goodsInfo.setFreightTempId(goods.getFreightTempId());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            } else {
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }

            //补充对应的属性信息
            List<GoodsAttributeKey> query = goodsAttributeKeyService.query(GoodsAtrrKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
            goodsInfo.setGoodsAttributeKeys(query);

        });

        return GoodsInfoResponse.builder().goodsInfoPage(microPage)
                .goodses(goodses)
                .cates(cates)
                .brands(brands).build();
    }

    /**
     * 根据ID批量查询商品SKU
     *
     * @param infoRequest 查询参数
     * @return 商品列表响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public GoodsInfoResponse findSkuByIds(GoodsInfoRequest infoRequest) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase findSkuByIds start");

        try {
            if (CollectionUtils.isEmpty(infoRequest.getGoodsInfoIds())) {
                return GoodsInfoResponse.builder().goodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
            }
            //批量查询SKU信息列表
            GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
            queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
            queryRequest.setStoreId(infoRequest.getStoreId());
            //查询分仓所属商品
            queryRequest.setWareId(infoRequest.getWareId());
            if (infoRequest.getDeleteFlag() != null) {
                queryRequest.setDelFlag(infoRequest.getDeleteFlag().toValue());
            }
            //关联goodsLabels
            List<GoodsLabel> goodsLabelList = goodsLabelService.findTopByDelFlag();
            // log.info("===============findSkuByIds.goodsInfos:{}", JSONObject.toJSONString(queryRequest));
            List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(queryRequest.getWhereCriteria());

            sb.append(",goodsInfoRepository.findAll end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

            if (CollectionUtils.isEmpty(goodsInfos)) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{goodsInfos});
            }



            Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfos
                    .stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));

            sb.append(",goodsWareStockService.findByGoodsInfoIdIn end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();


                for (GoodsInfo goodsInfo : goodsInfos) {
                    goodsInfo.setStock(getskusstock.getOrDefault(goodsInfo.getGoodsInfoId(),BigDecimal.ZERO));
                }


            //批量查询SPU信息列表
            List<String> goodsIds = goodsInfos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
            GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
            goodsQueryRequest.setGoodsIds(goodsIds);
            List<Goods> goodses = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());


            //标签组装进goods
            goodses.stream().forEach(item -> {
                if (StringUtils.isNotBlank(item.getLabelIdStr())) {
                    List<Long> goodsLabelIds = Arrays.stream(item.getLabelIdStr().split(",")).map(Long::parseLong)
                            .collect(Collectors.toList());
                    //过滤除符合条件的标签对象
                    item.setGoodsLabels(goodsLabelList.stream()
                            .filter(label -> goodsLabelIds.contains(label.getId()))
                            .collect(Collectors.toList()));

                }
            });

            HashMap<String, List<GoodsLabel>> goodsLabelMap = new HashMap<>();
            goodses.forEach(item -> {
                goodsLabelMap.put(item.getGoodsId(), item.getGoodsLabels());
            });

            sb.append(",goodsRepository.findAll end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

            if (CollectionUtils.isEmpty(goodses)) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
            }
            Map<String, Goods> goodsMap = goodses.stream().collect(Collectors.toMap(Goods::getGoodsId, g -> g));


            List<String> skuIds = goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
            //对每个SKU填充规格和规格值关系
            Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
            //如果需要规格值，则查询
            if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
                specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));

                sb.append(",goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds end time=");
                sb.append(System.currentTimeMillis() - sTm);
                sTm = System.currentTimeMillis();
            }

            //查询拆箱信息
            DevanningGoodsInfoPageRequest request = DevanningGoodsInfoPageRequest.builder().goodsInfoIds(Lists.newArrayList(skuIds)).build();
            DevanningGoodsInfoResponse context = devanningGoodsInfoProvider.getQueryList(request).getContext();
            Map<String, DevanningGoodsInfoVO> devanningMap = Collections.EMPTY_MAP;
            //TODO: 拆箱数据有重复？ sku: 2c9afcf181687ba8018169586aff2ad3
            if (CollectionUtils.isNotEmpty(context.getDevanningGoodsInfoVOS())) {
                devanningMap = context.getDevanningGoodsInfoVOS().stream().collect(Collectors.toMap(DevanningGoodsInfoVO::getGoodsInfoId, v -> v, (k1, k2) -> k1));
            }

            //遍历SKU，填充销量价、商品状态
            Map<String, DevanningGoodsInfoVO> finalDevanningMap = devanningMap;
            goodsInfos.forEach(goodsInfo -> {
                goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
                Goods goods = goodsMap.get(goodsInfo.getGoodsId());
                if (goods != null) {
                    //建立扁平化数据
                    if (goods.getGoodsInfoIds() == null) {
                        goods.setGoodsInfoIds(new ArrayList<>());
                    }
                    goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                    goodsInfo.setPriceType(goods.getPriceType());
                    goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                    goodsInfo.setCateId(goods.getCateId());
                    goodsInfo.setBrandId(goods.getBrandId());
                    goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                    goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                    goodsInfo.setFreightTempId(goods.getFreightTempId());
                    goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                    goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                    goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                    goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                    //sku标签
                    goodsInfo.setGoodsLabels(goodsLabelMap.get(goodsInfo.getGoodsId()));

                    //为空，则以商品主图
                    if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                        goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                    }

                    //填充规格值
                    if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                        goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                                .map(GoodsInfoSpecDetailRel::getDetailName)
                                .collect(Collectors.toList()), " "));
                    }

                    //填充副标题
                    goodsInfo.setGoodsSubtitle(goods.getGoodsSubtitle());
                    //填充拆箱id
                    goodsInfo.setDevanningId(finalDevanningMap.getOrDefault(goodsInfo.getGoodsInfoId(),new DevanningGoodsInfoVO()).getDevanningId());

                    if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OK);
                        if ((Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) && (Objects.isNull(goodsInfo.getMarketingId()) || Objects.isNull(goodsInfo.getPurchaseNum()) || goodsInfo.getPurchaseNum() == -1)) {
                            goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        }
                        //限购商品
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() != -1){
//                            if(goodsInfo.getPurchaseNum() < 1){
//                                goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                            }
//                        }

                    } else {
                        goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                    }
                } else {//不存在，则做为删除标记
                    goodsInfo.setDelFlag(DeleteFlag.YES);
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }

                //补充对应的属性信息
                List<GoodsAttributeKey> query = goodsAttributeKeyService.query(GoodsAtrrKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
                goodsInfo.setGoodsAttributeKeys(query);
            });

            sb.append(",goodsInfos.forEach end time=");
            sb.append(System.currentTimeMillis() - sTm);

            //定义响应结果
            GoodsInfoResponse responses = new GoodsInfoResponse();
            responses.setGoodsInfos(goodsInfos);
            responses.setGoodses(goodses);
            return responses;
        } finally {
            log.info(sb.toString());
        }

    }

    /**
     * 根据ID批量查询商品SKU
     *
     * @param infoRequest 查询参数
     * @return 商品列表响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public GoodsInfoResponse findSkuByIdsLimitWareId(GoodsInfoRequest infoRequest) {
        if (CollectionUtils.isEmpty(infoRequest.getGoodsInfoIds())) {
            return GoodsInfoResponse.builder().goodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
        }
        //批量查询SKU信息列表
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
        queryRequest.setStoreId(infoRequest.getStoreId());
        if (infoRequest.getDeleteFlag() != null) {
            queryRequest.setDelFlag(infoRequest.getDeleteFlag().toValue());
        }
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(queryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(goodsInfos)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{goodsInfos});
        }
        //设置仓库的库存
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.getGoodsStockByAreaIdAndGoodsInfoIds(
                goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()),
                infoRequest.getWareId());

        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.getGoodsStockByAreaIdAndGoodsInfoIds(
                goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()), infoRequest.getWareId());
        if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
            Map<String, GoodsWareStock> goodsWareStockMap = goodsWareStocks.stream().collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId, g -> g));
            goodsInfos.stream().forEach(g -> {
                g.setStock(getskusstock.getOrDefault(g.getGoodsInfoId(),BigDecimal.ZERO));

//                if (Objects.nonNull(goodsWareStockMap.get(g.getGoodsInfoId()))) {
//
//
//
//                    g.setStock(goodsWareStockMap.get(g.getGoodsInfoId()).getStock());
//                    //设置限购库存
////                    if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
////                        g.setStock(g.getPurchaseNum());
////                    }
//                }
            });
        }
//        if (CollectionUtils.isNotEmpty(goodsWareStockVillagesList)) {
//            Map<String, GoodsWareStockVillages> villagesMap = goodsWareStockVillagesList.stream().collect(Collectors.toMap(GoodsWareStockVillages::getGoodsInfoId, g->g));
//            goodsInfos.stream().forEach(g -> {
//                if (Objects.nonNull(villagesMap.get(g.getGoodsInfoId()))) {
//                    if (g.getStock().subtract(villagesMap.get(g.getGoodsInfoId()).getStock()).compareTo(BigDecimal.ZERO)  <= 0) {
//                        g.setStock(BigDecimal.ZERO);
//                    } else {
//                        g.setStock(g.getStock().subtract(villagesMap.get(g.getGoodsInfoId()).getStock()));
//                    }
//                }
//            });
//        }

        //批量查询SPU信息列表
        List<String> goodsIds = goodsInfos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<Goods> goodses = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(goodses)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        Map<String, Goods> goodsMap = goodses.stream().collect(Collectors.toMap(Goods::getGoodsId, g -> g));


        List<String> skuIds = goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        //对每个SKU填充规格和规格值关系
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
        //如果需要规格值，则查询
        if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
            specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));
        }

        //遍历SKU，填充销量价、商品状态
        goodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            if (goods != null) {
                //建立扁平化数据
                if (goods.getGoodsInfoIds() == null) {
                    goods.setGoodsInfoIds(new ArrayList<>());
                }
                goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                goodsInfo.setFreightTempId(goods.getFreightTempId());
                goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }
                //填充规格值
                if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                    goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                            .map(GoodsInfoSpecDetailRel::getDetailName)
                            .collect(Collectors.toList()), " "));
                }

                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        //定义响应结果
        GoodsInfoResponse responses = new GoodsInfoResponse();
        responses.setGoodsInfos(goodsInfos);
        responses.setGoodses(goodses);
        return responses;
    }

    /**
     * 根据ID批量查询商品SKU(过滤线上仓)
     *
     * @param infoRequest 查询参数
     * @return 商品列表响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public GoodsInfoResponse findSkuByIdsAndMatchFlag(GoodsInfoRequest infoRequest) {
        if (CollectionUtils.isEmpty(infoRequest.getGoodsInfoIds())) {
            return GoodsInfoResponse.builder().goodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
        }
        //批量查询SKU信息列表
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
        queryRequest.setStoreId(infoRequest.getStoreId());
        if (infoRequest.getDeleteFlag() != null) {
            queryRequest.setDelFlag(infoRequest.getDeleteFlag().toValue());
        }
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(queryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(goodsInfos)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{goodsInfos});
        }

        //设置仓库的库存
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfos.stream()
                .map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(goodsInfos
                .stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));

        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfos
                .stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));

        List<Long> unOnline = new ArrayList<>(10);
        if (Objects.nonNull(infoRequest.getMatchWareHouseFlag()) && !infoRequest.getMatchWareHouseFlag()) {
            List<Long> collect = goodsWareStocks.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(collect, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
            List<GoodsWareStock> stockList;
            List<GoodsWareStockVillages> villagesList;
            for (GoodsInfo g : goodsInfos) {
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStocks.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStocks.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(stockList)) {
                    g.setStock(getskusstock.getOrDefault(g.getGoodsInfoId(),BigDecimal.ZERO));


//                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
//                    if (CollectionUtils.isNotEmpty(villagesList)) {
//                        BigDecimal sumVillagesStock = villagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
//                        if (sumStock.subtract(sumVillagesStock).compareTo(BigDecimal.ZERO)  <= 0) {
//                            g.setStock(BigDecimal.ZERO);
//                        } else {
//                            g.setStock(sumStock.subtract(sumVillagesStock) );
//                        }
//                    } else {
//                        g.setStock(sumStock);
//                    }
//                    if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                        g.setStock(g.getPurchaseNum());
//                    }
                } else {
                    g.setStock(BigDecimal.ZERO);
//                    if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                        g.setStock(g.getPurchaseNum());
//                    }
                }
            }
        }
        //批量查询SPU信息列表
        List<String> goodsIds = goodsInfos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<Goods> goodses = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(goodses)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        Map<String, Goods> goodsMap = goodses.stream().collect(Collectors.toMap(Goods::getGoodsId, g -> g));


        List<String> skuIds = goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        //对每个SKU填充规格和规格值关系
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
        //如果需要规格值，则查询
        if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
            specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));
        }

        //遍历SKU，填充销量价、商品状态
        goodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            if (goods != null) {
                //建立扁平化数据
                if (goods.getGoodsInfoIds() == null) {
                    goods.setGoodsInfoIds(new ArrayList<>());
                }
                goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                goodsInfo.setFreightTempId(goods.getFreightTempId());
                goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                //填充规格值
                if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                    goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                            .map(GoodsInfoSpecDetailRel::getDetailName)
                            .collect(Collectors.toList()), " "));
                }

                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        //定义响应结果
        GoodsInfoResponse responses = new GoodsInfoResponse();
        responses.setGoodsInfos(goodsInfos);
        responses.setGoodses(goodses);
        return responses;
    }


    /**
     * 根据ID查询商品SKU
     *
     * @param goodsInfoId 商品SKU编号
     * @return 商品SKU详情
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public GoodsInfoEditResponse findById(String goodsInfoId,Long wareId) {
        GoodsInfoEditResponse response = new GoodsInfoEditResponse();
//        GoodsInfo goodsInfo = goodsInfoRepository.findById(goodsInfoId).orElse(null);
        GoodsInfoQueryRequest goodsInfoQueryRequest =new GoodsInfoQueryRequest();
        List<String> goodsinfolist =new LinkedList<>();
        goodsinfolist.add(goodsInfoId);
        goodsInfoQueryRequest.setGoodsInfoIds(goodsinfolist);
        goodsInfoQueryRequest.setWareId(wareId);
        GoodsInfo goodsInfo = goodsInfoRepository.findAll(goodsInfoQueryRequest.getWhereCriteria()).stream().findFirst().orElse(null);
        if (goodsInfo == null || DeleteFlag.YES.toValue() == goodsInfo.getDelFlag().toValue()) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        //填充本品信息
        if (Objects.nonNull(goodsInfo) && DefaultFlag.YES.equals(goodsInfo.getIsSuitGoods())
                && Objects.nonNull(goodsInfo.getChoseProductSkuId())) {
            response.setChoseProductGoodsInfo(goodsInfoRepository.findById(goodsInfo.getChoseProductSkuId()).orElse(null));
        }

        // 经营商户库存转供应商库存
        this.turnSingleProviderStock(goodsInfo);

        Goods goods = goodsRepository.findById(goodsInfo.getGoodsId()).orElseThrow(() -> new SbcRuntimeException(GoodsErrorCode.NOT_EXIST));

        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            response.setGoodsSpecs(goodsSpecRepository.findByGoodsId(goods.getGoodsId()));
            response.setGoodsSpecDetails(goodsSpecDetailRepository.findByGoodsId(goods.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().forEach(goodsSpec -> {
                goodsSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(goodsSpec.getSpecId())).map(GoodsSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            List<GoodsInfoSpecDetailRel> goodsInfoSpecDetailRels = goodsInfoSpecDetailRelRepository.findByGoodsId(goods.getGoodsId());
            goodsInfo.setMockSpecIds(goodsInfoSpecDetailRels.stream().filter(detailRel -> detailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecId).collect(Collectors.toList()));
            goodsInfo.setMockSpecDetailIds(goodsInfoSpecDetailRels.stream().filter(detailRel -> detailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            goodsInfo.setSpecText(StringUtils.join(goodsInfoSpecDetailRels.stream().filter(specDetailRel -> goodsInfo.getGoodsInfoId().equals(specDetailRel.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getDetailName).collect(Collectors.toList()), " "));
        }

        //查询sku库存信息
        List<GoodsWareStock> goodsWareStocks = goodsWareStockRepository.findByGoodsInfoId(goodsInfo.getGoodsInfoId());
        List<Long> wareIds = goodsWareStocks.stream().map(GoodsWareStock::getWareId).collect(Collectors.toList());
        List<WareHouse> wareHouses = wareHouseRepository.findByWareIdIn(wareIds);
        goodsWareStocks.forEach(goodsWareStock -> {
            wareHouses.forEach(wareHouse -> {
                if (goodsWareStock.getWareId().equals(wareHouse.getWareId())) {
                    goodsWareStock.setWareName(wareHouse.getWareName());
                }
            });
        });

        List<GoodsWareStock> stockList = goodsWareStocks.stream().
                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                && goodsInfo.getWareId().equals(goodsWareStock.getWareId())).
                collect(Collectors.toList());
        goodsInfo.setGoodsWareStocks(stockList);

        response.setGoodsInfo(goodsInfo);
        response.setGoods(goods);

        //商品按订货区间，查询订货区间
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
            response.setGoodsIntervalPrices(goodsIntervalPriceRepository.findSkuByGoodsInfoId(goodsInfo.getGoodsInfoId()));
        } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(goods.getPriceType())) {
            response.setGoodsLevelPrices(goodsLevelPriceRepository.findSkuByGoodsInfoId(goodsInfo.getGoodsInfoId()));
            //如果是按单独客户定价
            if (Constants.yes.equals(goodsInfo.getCustomFlag())) {
                response.setGoodsCustomerPrices(goodsCustomerPriceRepository.findSkuByGoodsInfoId(goodsInfo.getGoodsInfoId()));
            }
        }
        response.setImages(goodsImageRepository.findByGoodsId(goods.getGoodsId()));

        //查询拆箱信息
        DevanningGoodsInfoPageRequest request = DevanningGoodsInfoPageRequest.builder().goodsInfoIds(Lists.newArrayList(goodsInfoId)).build();
        DevanningGoodsInfoResponse context = devanningGoodsInfoProvider.getQueryList(request).getContext();

        if(Objects.nonNull(context) && CollectionUtils.isNotEmpty(context.getDevanningGoodsInfoVOS())){
            List<GoodsWareStockVO> goodsWareStockVOS = KsBeanUtil.convertList(stockList, GoodsWareStockVO.class);
            context.getDevanningGoodsInfoVOS().forEach(d->{
                d.setGoodsWareStocks(goodsWareStockVOS);
            });
            response.setDevanningGoodsInfoVOS(context.getDevanningGoodsInfoVOS());
        }

        return response;
    }

    /**
     * 单表ID查询goodsInfo
     */
    public GoodsInfoResponse findGoodInfoByIds(SpecialGoodsModifyRequest request) {
        GoodsInfoResponse response = new GoodsInfoResponse();
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.findByGoodsInfoIds(request.getGoodsInfoIdList());
        response.setGoodsInfos(goodsInfoList);
        return response;

    }

    /**
     * 传批量goodsInfoIds 查询保质期
     */
    public GoodsInfoOnlyShelflifeResponse findGoodInfoByIds(List<String> goodsInfoIds) {
        GoodsInfoOnlyShelflifeResponse response = new GoodsInfoOnlyShelflifeResponse();
        List<GoodsInfoOnlyShelflifeDTO> byGoodsInfoIdsShelflife = goodsInfoRepository.findByGoodsInfoIdsShelflife(goodsInfoIds);
        response.setGoodsInfos(byGoodsInfoIdsShelflife);
        return response;
    }



    /**
     * 商品删除
     *
     * @param goodsInfoIds 商品skuId列表
     */
    @Transactional
    public void delete(List<String> goodsInfoIds) {

        // 1.删除sku相关信息
        goodsInfoRepository.deleteByGoodsInfoIds(goodsInfoIds);

        // 2.查询仅包含当前skus的规格值，并删除
        // 2.1.按规格值分组删除的商品总数
        List<GoodsInfoSpecDetailRel> specDetailRels = goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(specDetailRels)) {
            Map<Long, Long> specDetailMap = specDetailRels.stream()
                    .collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getSpecDetailId, Collectors.counting()));
            // 2.2.查询各规格值下的商品总数
            List<GoodsInfoSpecDetailRel> specDetailAllRels = goodsInfoSpecDetailRelRepository.findBySpecDetailIds(specDetailMap.keySet());
            Map<Long, Long> specDetailAllMap = specDetailAllRels.stream()
                    .collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getSpecDetailId, Collectors.counting()));
            // 2.3.判断规格值商品数是否相等，相等则删除规格值
            List<Long> specDetailIds = specDetailMap.keySet().stream().filter(specDetailId ->
                    specDetailMap.get(specDetailId).equals(specDetailAllMap.get(specDetailId))
            ).collect(Collectors.toList());
            if (specDetailIds.size() != 0) {
                goodsSpecDetailRepository.deleteBySpecDetailIds(specDetailIds);
            }
        }


        // 3.删除sku和规格值的关联关系
        goodsInfoSpecDetailRelRepository.deleteByGoodsInfoIds(goodsInfoIds);

        // 4.查找不完整包含的spu
        List<String> goodsIds = this.findByIds(goodsInfoIds, null).stream()
                .map(GoodsInfo::getGoodsId)
                .distinct().collect(Collectors.toList());
        List<String> partContainGoodsIds = goodsInfoRepository.findByGoodsIds(goodsIds).stream()
                .filter(goodsInfo -> !goodsInfoIds.contains(goodsInfo.getGoodsInfoId()))
                .map(GoodsInfo::getGoodsId)
                .distinct().collect(Collectors.toList());
        // 5.反找出完整包含的spu
        goodsIds.removeAll(partContainGoodsIds);


        // 6.删除完整包含的spu
        if (goodsIds.size() != 0) {
            goodsRepository.deleteByGoodsIds(goodsIds);
            goodsPropDetailRelRepository.deleteByGoodsIds(goodsIds);
            goodsSpecRepository.deleteByGoodsIds(goodsIds);
            goodsSpecDetailRepository.deleteByGoodsIds(goodsIds);
            standardGoodsRelRepository.deleteByGoodsIds(goodsIds);
        }
        // 7.删除库存信息
        goodsWareStockService.deleteByGoodsInfoIds(goodsInfoIds);

        // 8.更新不完整包含的spu的上下架状态
        this.updateGoodsAddedFlag(partContainGoodsIds);

    }


    /**
     * 商品SKU更新
     *
     * @param saveRequest sku信息
     * @return 商品信息
     */
    @Transactional
    public GoodsInfo edit(GoodsInfoSaveRequest saveRequest) {
        GoodsInfo newGoodsInfo = saveRequest.getGoodsInfo();
        GoodsInfo oldGoodsInfo = goodsInfoRepository.findById(newGoodsInfo.getGoodsInfoId()).orElse(null);
        if (oldGoodsInfo == null || oldGoodsInfo.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        Goods goods = goodsRepository.findById(oldGoodsInfo.getGoodsId()).orElse(null);
        if (goods == null) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsInfoNos(Collections.singletonList(newGoodsInfo.getGoodsInfoNo()));
        infoQueryRequest.setNotGoodsInfoId(oldGoodsInfo.getGoodsInfoId());
        //验证SKU编码重复
        if (goodsInfoRepository.count(infoQueryRequest.getWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_EXIST);
        }
        //价格变动修改
        if(Objects.nonNull(goods.getGoodsSubtitle()) &&oldGoodsInfo.getMarketPrice()!=newGoodsInfo.getMarketPrice()){
            String orgSubtitle = goods.getGoodsSubtitle();
            String s1 = formartTitle(orgSubtitle);
            if (Objects.nonNull(s1)) {
                orgSubtitle = s1;
            }
            char unit = orgSubtitle.charAt(orgSubtitle.length() - 2);
            // 1箱(24盒 x 11.50元/盒)
            // 箱
            char unitAll = goods.getGoodsSubtitle().charAt(1);
            BigDecimal marketPrice = newGoodsInfo.getMarketPrice();//拆箱价格
            BigDecimal addStep = newGoodsInfo.getAddStep();//步长
            BigDecimal subtitlePrice = marketPrice.divide(addStep, 2, RoundingMode.UP);
            String s = String.valueOf(unit);
             String goodsSubtitle = "1"+unitAll+"=" + addStep + s + "x" + subtitlePrice + "元/" + s ;
//            String goodsSubtitle = "1" + unitAll + "(" + addStep + s + "x" + subtitlePrice + "元/" + s + ")";
            String goodsSubtitleNew = subtitlePrice + "/" + s;


            //更新对应字段
            goods.setGoodsSubtitle(goodsSubtitle);
            goods.setGoodsSubtitleNew(goodsSubtitleNew);
            goods.setMarketPrice(newGoodsInfo.getMarketPrice());
            goodsRepository.save(goods);
        }




        LocalDateTime currDate = LocalDateTime.now();

        //分析同一SPU的SKU上下架状态，去更新SPU上下架状态
        //累计状态值
        Integer addedCount = 0;
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsId(oldGoodsInfo.getGoodsId());
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(queryRequest.getWhereCriteria());
        for (GoodsInfo goodsInfo : goodsInfos) {
            if (goodsInfo.getGoodsInfoId().equals(newGoodsInfo.getGoodsInfoId())) {
                addedCount += newGoodsInfo.getAddedFlag() == null ? AddedFlag.NO.toValue() : newGoodsInfo.getAddedFlag();
            } else {
                addedCount += goodsInfo.getAddedFlag() == null ? AddedFlag.NO.toValue() : goodsInfo.getAddedFlag();
            }
        }

        Integer oldGoodsAddedFalg = goods.getAddedFlag();
        //如果是0，则所有SKU的上下架状态都是下架
        if (addedCount == 0) {
            goods.setAddedFlag(AddedFlag.NO.toValue());
        } else if (addedCount == goodsInfos.size()) {//累计等于同一SPU的SKU个数，上架
            goods.setAddedFlag(AddedFlag.YES.toValue());
        } else {//部分上架
            goods.setAddedFlag(AddedFlag.PART.toValue());
        }

        //更新商品
        goods.setUpdateTime(currDate);
        //当上下架状态不一致，更新上下架时间
        if (!oldGoodsAddedFalg.equals(goods.getAddedFlag())) {
            goods.setAddedTime(currDate);
        }



        goodsRepository.save(goods);

        //更新商品SKU
        if (newGoodsInfo.getStock() == null) {
            newGoodsInfo.setStock(BigDecimal.ZERO);
        }
        //当上下架状态不一致，更新上下架时间
        if (!oldGoodsInfo.getAddedFlag().equals(newGoodsInfo.getAddedFlag())) {
            newGoodsInfo.setAddedTime(currDate);
        }
        newGoodsInfo.setUpdateTime(currDate);
        if (Objects.nonNull(oldGoodsInfo.getCommissionRate())) {
            newGoodsInfo.setDistributionCommission(newGoodsInfo.getMarketPrice().multiply(oldGoodsInfo.getCommissionRate()));
        }
        if (newGoodsInfo.getGoodsInfoQrcode() == null) {
            oldGoodsInfo.setGoodsInfoQrcode(null);
        }
        if (Objects.isNull(newGoodsInfo.getInquiryFlag())){
            newGoodsInfo.setInquiryFlag(Constants.no);
        }
        if (newGoodsInfo.getAllowedPurchaseArea() == null){
            oldGoodsInfo.setAllowedPurchaseArea(null);
        }
        if (newGoodsInfo.getAllowedPurchaseAreaName() == null){
            oldGoodsInfo.setAllowedPurchaseAreaName(null);
        }
        if (newGoodsInfo.getSingleOrderAssignArea() == null){
            oldGoodsInfo.setSingleOrderAssignArea(null);
        }
        if (newGoodsInfo.getSingleOrderAssignAreaName() == null){
            oldGoodsInfo.setSingleOrderAssignAreaName(null);
        }
        if (newGoodsInfo.getSingleOrderPurchaseNum() == null){
            oldGoodsInfo.setSingleOrderPurchaseNum(null);
        }
        if (newGoodsInfo.getVirtualStock() == null){
            oldGoodsInfo.setVirtualStock(BigDecimal.ZERO);
        }
        if (newGoodsInfo.getIsSurprisePrice() == null){
            oldGoodsInfo.setVirtualStock(BigDecimal.ZERO);
        }
        if (Objects.isNull(newGoodsInfo.getIsSuitGoods())) {
            oldGoodsInfo.setIsSuitGoods(DefaultFlag.NO);
        } else {
            if (Objects.isNull(newGoodsInfo.getChoseProductSkuId()) && newGoodsInfo.getIsSuitGoods().equals(DefaultFlag.YES)) {
                throw new SbcRuntimeException(GoodsErrorCode.GOODS_PRODUCT_NOT_EXISTS);
            }
        }

        //散称或定量字段为空，默认给-1
        if (Objects.isNull(newGoodsInfo.getIsScatteredQuantitative())){
            oldGoodsInfo.setIsScatteredQuantitative(-1);
        }
        KsBeanUtil.copyProperties(newGoodsInfo, oldGoodsInfo);
        goodsInfoRepository.save(oldGoodsInfo);
        //修改goodsinfo就要修改拆箱表
        List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.getByInfoId(oldGoodsInfo.getGoodsInfoId());
        if (CollectionUtils.isNotEmpty(devanningGoodsInfos)){
            //存在就修改
            for (DevanningGoodsInfo devanningGoodsInfo:devanningGoodsInfos){
                DevanningGoodsInfo devanningGoodsInfo1 =new DevanningGoodsInfo();
                devanningGoodsInfo1=KsBeanUtil.convert(oldGoodsInfo,DevanningGoodsInfo.class);
                //拆箱表私有属性在次赋值
                devanningGoodsInfo1.setIsSurprisePrice(oldGoodsInfo.getIsSurprisePrice());
                devanningGoodsInfo1.setDevanningId(devanningGoodsInfo.getDevanningId());//id
                devanningGoodsInfo1.setDivisorFlag(devanningGoodsInfo.getDivisorFlag());//falg
                devanningGoodsInfo1.setDevanningUnit(devanningGoodsInfo.getDevanningUnit());//单位
                devanningGoodsInfo1.setVipPrice(devanningGoodsInfo.getVipPrice());//vip价格
                devanningGoodsInfo1.setGoodsInfoSubtitle(devanningGoodsInfo.getGoodsInfoSubtitle());//商品副标题
                devanningGoodsInfo1.setMarketPrice(devanningGoodsInfo.getMarketPrice());//市场价格
                devanningGoodsInfo1.setAddStep(devanningGoodsInfo.getAddStep());//拆箱步长
                devanningGoodsInfo1.setGoodsSubtitleNew(devanningGoodsInfo.getGoodsSubtitleNew());
                devanningGoodsInfoRepository.save(devanningGoodsInfo1);
            }
        }else {
            //不存在就添加 整箱的规格
            DevanningGoodsInfo devanningGoodsInfo = KsBeanUtil.convert(oldGoodsInfo, DevanningGoodsInfo.class);
            Goods goods1 = goodsRepository.findById(devanningGoodsInfo.getGoodsId()).get();
            devanningGoodsInfo.setDivisorFlag(BigDecimal.ONE);
            devanningGoodsInfo.setDevanningUnit(goods1.getGoodsUnit());
            devanningGoodsInfo.setGoodsInfoSubtitle(goods1.getGoodsSubtitle());
            devanningGoodsInfo.setIsSurprisePrice(oldGoodsInfo.getIsSurprisePrice());
            devanningGoodsInfoRepository.save(devanningGoodsInfo);
        }


        //更新最新库存信息
        List<GoodsWareStock> goodsWareStocks = newGoodsInfo.getGoodsWareStocks();
        if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
            goodsWareStocks.forEach(stock -> {
                goodsWareStockRepository.updateByGoodsInfoId(newGoodsInfo.getGoodsInfoId(), stock.getStock(), stock.getWareId());
            });
        }

        //更新标准库商品库供货价
        String goodsId = oldGoodsInfo.getGoodsId();
        StandardGoodsRel standardGoodsRel = standardGoodsRelRepository.findByGoodsId(goodsId);
        if (standardGoodsRel != null) {
            String standardId = standardGoodsRel.getStandardId();
            List<StandardGoods> standardGoodsList = standardGoodsRepository.findByGoodsIdIn(Arrays.asList(standardId));

            if (CollectionUtils.isNotEmpty(standardGoodsList)) {
                for (StandardGoods standardGoods : standardGoodsList) {
                    standardGoods.setSupplyPrice(oldGoodsInfo.getSupplyPrice());
                    standardGoodsRepository.save(standardGoods);
                }
            }
        }
        //更新商家商品库供货价等
        GoodsInfo supplierGoodsInfo = goodsInfoRepository.findByProviderGoodsInfoId(oldGoodsInfo.getGoodsInfoId());
        if (supplierGoodsInfo != null) {
            supplierGoodsInfo.setSupplyPrice(oldGoodsInfo.getSupplyPrice());
            supplierGoodsInfo.setStock(oldGoodsInfo.getStock());
            supplierGoodsInfo.setAddedFlag(oldGoodsInfo.getAddedFlag());
            supplierGoodsInfo.setGoodsInfoBarcode(oldGoodsInfo.getGoodsInfoBarcode());
            supplierGoodsInfo.setGoodsInfoNo(oldGoodsInfo.getGoodsInfoNo());
            goodsInfoRepository.save(supplierGoodsInfo);
        }

        return oldGoodsInfo;
    }

    public static String formartTitle(String originalStr) {
        // 定义正则表达式
        String regex = "(\\d+)([^\\d]+)=(\\d+)([^\\d]+)x([\\d.]+)元/([^\\d]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalStr);

        if (matcher.find()) {
            String total1 = matcher.group(1);
            String unit1 = matcher.group(2);
            String total2 = matcher.group(3);
            String unit2 = matcher.group(4);
            String pricePerUnit = matcher.group(5);
            String unit3 = matcher.group(6);

            // 构建新的格式化字符串
            return total1 + unit1 + "(" + total2 + unit2 + "x" + pricePerUnit + "元/" + unit3 + ")";
        } else {
            return null;
        }
    }

    /**
     * 更新SKU设价
     *
     * @param saveRequest sku设价信息
     */
    @Transactional
    public GoodsInfo editPrice(GoodsInfoSaveRequest saveRequest) {
        GoodsInfo newGoodsInfo = saveRequest.getGoodsInfo();
        GoodsInfo oldGoodsInfo = goodsInfoRepository.findById(newGoodsInfo.getGoodsInfoId()).orElse(null);
        if (oldGoodsInfo == null || oldGoodsInfo.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        Goods goods = goodsRepository.findById(oldGoodsInfo.getGoodsId()).orElseThrow(() -> new SbcRuntimeException(GoodsErrorCode.NOT_EXIST));

        //传递设价内部值
        oldGoodsInfo.setCustomFlag(newGoodsInfo.getCustomFlag());
        oldGoodsInfo.setLevelDiscountFlag(newGoodsInfo.getLevelDiscountFlag());
        goodsInfoRepository.save(oldGoodsInfo);

        //先删除设价数据
        goodsIntervalPriceRepository.deleteByGoodsInfoId(newGoodsInfo.getGoodsInfoId());
        goodsLevelPriceRepository.deleteByGoodsInfoId(newGoodsInfo.getGoodsInfoId());
        goodsCustomerPriceRepository.deleteByGoodsInfoId(newGoodsInfo.getGoodsInfoId());

        //按订货量设价，保存订货区间
        List<GoodsIntervalPrice> goodsIntervalPrices = null;
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
            goodsIntervalPrices = saveRequest.getGoodsIntervalPrices();
            if (CollectionUtils.isEmpty(saveRequest.getGoodsIntervalPrices()) || saveRequest.getGoodsIntervalPrices().stream().filter(intervalPrice -> intervalPrice.getCount() == 1).count() == 0) {
                GoodsIntervalPrice intervalPrice = new GoodsIntervalPrice();
                intervalPrice.setCount(1L);
                intervalPrice.setPrice(newGoodsInfo.getMarketPrice());
                if (saveRequest.getGoodsIntervalPrices() == null) {
                    saveRequest.setGoodsLevelPrices(new ArrayList<>());
                }
                saveRequest.getGoodsIntervalPrices().add(intervalPrice);
            }

            if (CollectionUtils.isNotEmpty(goodsIntervalPrices)) {
                goodsIntervalPrices.forEach(intervalPrice -> {
                    intervalPrice.setGoodsId(goods.getGoodsId());
                    intervalPrice.setGoodsInfoId(newGoodsInfo.getGoodsInfoId());
                    intervalPrice.setType(PriceType.SKU);
                });
                goodsIntervalPriceRepository.saveAll(goodsIntervalPrices);
            }
        } else {
            //否则按客户
            if (CollectionUtils.isNotEmpty(saveRequest.getGoodsLevelPrices())) {
                saveRequest.getGoodsLevelPrices().forEach(goodsLevelPrice -> {
                    goodsLevelPrice.setGoodsId(goods.getGoodsId());
                    goodsLevelPrice.setGoodsInfoId(newGoodsInfo.getGoodsInfoId());
                    goodsLevelPrice.setType(PriceType.SKU);
                });
                goodsLevelPriceRepository.saveAll(saveRequest.getGoodsLevelPrices());
            }

            //按客户单独定价
            if (Constants.yes.equals(newGoodsInfo.getCustomFlag()) && CollectionUtils.isNotEmpty(saveRequest.getGoodsCustomerPrices())) {
                saveRequest.getGoodsCustomerPrices().forEach(price -> {
                    price.setGoodsId(goods.getGoodsId());
                    price.setGoodsInfoId(newGoodsInfo.getGoodsInfoId());
                    price.setType(PriceType.SKU);
                });
                goodsCustomerPriceRepository.saveAll(saveRequest.getGoodsCustomerPrices());
            }
        }

        return this.edit(saveRequest);
    }

    /**
     * 更新SKU特价
     *
     * @param saveRequest sku设价信息
     */

    public List<GoodsInfo> editSpeciaPrice(SpecialGoodsInfoSaveRequest saveRequest) {
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.saveAll(saveRequest.getGoodsInfoList());
        return goodsInfoList;

    }

    /**
     * 更新商品上下架状态
     *
     * @param addedFlag    上下架状态
     * @param goodsInfoIds 商品skuId列表
     */
    @Transactional
    public void updateAddedStatus(Integer addedFlag, List<String> goodsInfoIds) {
        // 1.修改sku上下架状态
        goodsInfoRepository.updateAddedFlagByGoodsInfoIds(addedFlag, goodsInfoIds);
        devanningGoodsInfoRepository.updateAddedFlagByGoodsInfoIds(addedFlag, goodsInfoIds);
        if (0 == addedFlag) {
            goodsInfoIds.forEach(goodsID->{
                distributiorGoodsInfoRepository.deleteByGoodsInfoId(goodsID);
            });
        }
        // 2.修改spu上下架状态
        List<String> goodsIds = this.findByIds(goodsInfoIds, null).stream()
                .map(GoodsInfo::getGoodsId)
                .distinct().collect(Collectors.toList());
        this.updateGoodsAddedFlag(goodsIds);

        //下架商品如果有推荐商品则删除
        if (0 == addedFlag) {
            List<GoodsRecommendGoods> goodsRecommendGoods = goodsRecommendGoodsService
                    .list(GoodsRecommendGoodsQueryRequest.builder().goodsInfoIds(goodsInfoIds).build());
            if (CollectionUtils.isNotEmpty(goodsRecommendGoods)) {
                List<String> recommendIds = goodsRecommendGoods
                        .stream().map(GoodsRecommendGoods::getRecommendId).collect(Collectors.toList());
                List<String> skuIds = goodsRecommendGoods
                        .stream().map(GoodsRecommendGoods::getGoodsInfoId).collect(Collectors.toList());
                //删除推荐商品
                goodsRecommendGoodsService.deleteByIdList(recommendIds);
                //清除推荐商品 排序顺序
                goodsInfoRepository.clearRecommendSort(skuIds);
                //删除推荐商品设置缓存
                redisService.delete(RedisKeyConstant.GOODS_RECOMMEND_GOODS);
                //删除推荐商品移动端列表缓存
                redisService.delete(RedisKeyConstant.RECOMMEND_PAGE_SETTING);
            }
            //删除新推荐;
            List<MerchantRecommendGoods> list = merchantConfigGoodsService.list(MerchantConfigGoodsQueryRequest.builder().goodsInfoIds(goodsInfoIds).build());
            if (CollectionUtils.isNotEmpty(list)) {
                List<String> recommendIds = list
                        .stream().map(MerchantRecommendGoods::getRecommendId).collect(Collectors.toList());
                //删除推荐商品
                merchantConfigGoodsService.deleteByIdList(recommendIds);
                //删除推荐商品设置缓存
                redisService.delete(RedisKeyConstant.MERCHANT_GOODS_RECOMMEND_GOODS);
                //删除推荐商品移动端列表缓存
                redisService.delete(RedisKeyConstant.STORE_RECOMMEND_PAGE_SETTING);
                // 这里recommendRedis方法有@Async注解，直接调用null异常
//                merchantConfigGoodsSaveProvider.recommendRedis(MerchantConfigGoodsQueryRequest.builder().companyInfoId(list.get(Constants.no).getCompanyInfoId()).storeId(list.get(Constants.no).getStoreId()).build());
                merchantConfigGoodsService.recommendRedis(MerchantConfigGoodsQueryRequest.builder().companyInfoId(list.get(Constants.no).getCompanyInfoId()).storeId(list.get(Constants.no).getStoreId()).build());
            }

        }

    }

    /**
     * 根据SKU编号加库存
     *
     * @param stock       库存数
     * @param goodsInfoId SKU编号
     */
    @Transactional
    public void addStockById(BigDecimal stock, String goodsInfoId) {
        // 商户skuid转供应商skuid
        goodsInfoId = this.turnProviderGoodsInfoId(goodsInfoId);
        goodsInfoRepository.addStockById(stock, goodsInfoId);
        devanningGoodsInfoRepository.addStockById(stock, goodsInfoId);
    }

    /**
     * 批量加库存
     *
     * @param dtoList 增量库存参数
     */
    @Transactional
    @LcnTransaction
    public void batchAddStock(List<GoodsInfoPlusStockDTO> dtoList) {
        // 更新商品库存时，判断其中商品是否来自供应商，来自供应商的商品，要改为更新供应商商品库存
        List<String> goodsIdList = dtoList.stream().map(GoodsInfoPlusStockDTO::getGoodsInfoId).collect(Collectors.toList());
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.findAllById(goodsIdList);
        if (CollectionUtils.isNotEmpty(goodsInfoList)) {
            List<GoodsInfo> providerGoodsInfos =
                    goodsInfoList.stream().filter(goodsInfo -> StringUtils.isNotEmpty(goodsInfo.getProviderGoodsInfoId())).collect(Collectors.toList());
            dtoList.forEach(dto -> providerGoodsInfos.forEach(providerGoodsInfo -> {
                if (dto.getGoodsInfoId().equals(providerGoodsInfo.getGoodsInfoId())) {
                    // 商户skuid改为供应商skuid
                    dto.setGoodsInfoId(providerGoodsInfo.getProviderGoodsInfoId());
                }
            }));
        }

        dtoList.forEach(dto -> {
            goodsInfoRepository.addStockById(dto.getStock(), dto.getGoodsInfoId());
            devanningGoodsInfoRepository.addStockById(dto.getStock(), dto.getGoodsInfoId());
        });
    }

    /**
     * 根据SKU编号减库存
     *
     * @param stock       库存数
     * @param goodsInfoId SKU编号
     */
    @Transactional
    public void subStockById(BigDecimal stock, String goodsInfoId) {
        // 更新商品库存时，判断其中商品是否来自供应商，来自供应商的商品，要改为更新供应商商品库存
        goodsInfoId = this.turnProviderGoodsInfoId(goodsInfoId);
        int updateCount = goodsInfoRepository.subStockById(stock, goodsInfoId);
        devanningGoodsInfoRepository.subStockById(stock, goodsInfoId);
        if (updateCount <= 0) {
            throw new SbcRuntimeException(GoodsStockErrorCode.LACK_ERROR);
        }
    }

    /**
     * 批量减库存
     *
     * @param dtoList 减量库存参数
     */
    @Transactional
    @LcnTransaction
    public void batchSubStock(List<GoodsInfoMinusStockDTO> dtoList) {
        // 更新商品库存时，判断其中商品是否来自供应商，来自供应商的商品，要改为更新供应商商品库存
        List<String> goodsIdList = dtoList.stream().map(GoodsInfoMinusStockDTO::getGoodsInfoId).collect(Collectors.toList());
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.findAllById(goodsIdList);
        if (CollectionUtils.isNotEmpty(goodsInfoList)) {
            List<GoodsInfo> providerGoodsInfos =
                    goodsInfoList.stream().filter(goodsInfo -> StringUtils.isNotEmpty(goodsInfo.getProviderGoodsInfoId())).collect(Collectors.toList());
            dtoList.forEach(dto -> providerGoodsInfos.forEach(providerGoodsInfo -> {
                if (dto.getGoodsInfoId().equals(providerGoodsInfo.getGoodsInfoId())) {
                    // 商户skuid改为供应商skuid
                    dto.setGoodsInfoId(providerGoodsInfo.getProviderGoodsInfoId());
                }
            }));
        }

        dtoList.forEach(dto -> subStockById(dto.getStock(), dto.getGoodsInfoId()));
    }

    /**
     * 获取SKU详情
     *
     * @param skuId 商品skuId
     * @return 商品sku详情
     */
    public GoodsInfo findOne(String skuId) {
        GoodsInfo goodsInfo = this.goodsInfoRepository.findById(skuId).orElse(null);

        return goodsInfo;
    }

    public GoodsInfo findByGoodsInfoIdAndStoreIdAndDelFlag(String skuId, Long storeId) {
        GoodsInfo goodsInfo = this.goodsInfoRepository.findByGoodsInfoIdAndStoreIdAndDelFlag(skuId, storeId, DeleteFlag.NO).orElse(null);
        return goodsInfo;
    }


    public GoodsInfo findByGoodsInfoIdAndParentGoodsInfoIdAndWareId(String skuId, Long wareId,String parentSkuId) {
        GoodsInfo goodsInfo = this.goodsInfoRepository.findByGoodsInfoIdAndParentGoodsInfoIdAndWareId(skuId, parentSkuId, wareId).orElse(null);
        return goodsInfo;
    }

    public GoodsInfo findByParentGoodsInfoIdAndWareId( String parentSkuId,Long wareId) {
        GoodsInfo goodsInfo = this.goodsInfoRepository.findByParentGoodsInfoIdAndWareId(parentSkuId, wareId).orElse(null);
        return goodsInfo;
    }




    /**
     * 根据id批量查询SKU数据
     *
     * @param skuIds 商品skuId
     * @return 商品sku列表
     */
    public List<GoodsInfo> findByIds(List<String> skuIds, Long wareId) {
        List<GoodsInfo> goodsInfoList = this.goodsInfoRepository.findAllById(skuIds);
        //设置库存
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.getGoodsStockByAreaIdAndGoodsInfoIds(skuIds, wareId);

        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(skuIds);

        if (Objects.nonNull(wareId) && CollectionUtils.isNotEmpty(goodsWareStocks)) {
            Map<String, GoodsWareStock> goodsWareStockMap = goodsWareStocks.stream()
                    .collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId, g -> g, (x, y)->x));
            goodsInfoList.forEach(g -> g.setStock(getskusstock.getOrDefault(g.getGoodsInfoId(),BigDecimal.ZERO)));
        }



        //减去乡镇件库存
//        this.reduceVillagesStock(goodsInfoList,skuIds,wareId);
//        Map<String, Long> goodsNumsMap = getGoodsPileNumBySkuIds(skuIds);
//        goodsInfoList.forEach(g -> {
//            if (Objects.isNull(g.getStock())) {
//                g.setStock(0L);
//                if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                    g.setStock(g.getPurchaseNum());
//                }
//            }

//            if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                g.setStock(g.getPurchaseNum());
//            }else{
                //计算库存 加上虚拟库存 减去囤货数量
//                calGoodsInfoStock(g,goodsNumsMap);
//            }
//        });
        return goodsInfoList;
    }


    /**
     * 根据id批量查询SKU数据
     *
     * @param goodsInfoParentIds
     * @return 商品sku列表
     */
    public List<GoodsInfo> listByParentIds(List<String> goodsInfoParentIds, Long wareId) {

        List<GoodsInfo> goodsInfoList = this.goodsInfoRepository.findByParentGoodsInfoIdInAndWareId(goodsInfoParentIds,wareId);
        if(CollectionUtils.isEmpty(goodsInfoList)){
            return new ArrayList<>();
        }
        List<String> skuIds = goodsInfoList.stream().map(o -> o.getGoodsInfoId()).collect(Collectors.toList());
        //设置库存
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.getGoodsStockByAreaIdAndGoodsInfoIds(skuIds, wareId);

        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(skuIds);

        if (Objects.nonNull(wareId) && CollectionUtils.isNotEmpty(goodsWareStocks)) {
            Map<String, GoodsWareStock> goodsWareStockMap = goodsWareStocks.stream()
                    .collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId, g -> g));
            goodsInfoList.forEach(g -> g.setStock(getskusstock.getOrDefault(g.getGoodsInfoId(),BigDecimal.ZERO)));
        }
        return goodsInfoList;
    }



    /**
     * 根据id批量查询SKU数据
     *
     * @param skuIds 商品skuId
     * @return 商品sku列表
     */
    public List<GoodsInfo> findGoodsInfoAndStockByIds(List<String> skuIds, Boolean matchWareHouseFlag) {
        List<GoodsInfo> goodsInfoList = this.goodsInfoRepository.findAllById(skuIds);
//        Map<String, Long> goodsNumsMap = getGoodsPileNumBySkuIds(skuIds);
        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList = goodsWareStockService.findByGoodsInfoIdIn(skuIds);
        //获取对应的商品乡镇件库存
        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(skuIds);


        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(skuIds);

        Map<String, BigDecimal> stockMap = new HashMap<>();
        Map<String, List<GoodsWareStock>> goodsWareStockMap = new HashMap<>();
        List<Long> unOnline = new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(matchWareHouseFlag) && !matchWareHouseFlag) {
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            for (String goodsInfoId : skuIds) {
                List<GoodsWareStock> stockList;
                List<GoodsWareStockVillages> villagesList;
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    stockMap.put(goodsInfoId, sumStock);
//                    long sumStock = stockList.stream().mapToLong(GoodsWareStock::getStock).sum();
                    if (CollectionUtils.isNotEmpty(villagesList)) {
                        BigDecimal sumVillagesStock = villagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                        if (sumStock.subtract(sumVillagesStock).compareTo(BigDecimal.ZERO)  <= 0) {
                            stockMap.put(goodsInfoId, BigDecimal.ZERO);
                        } else {
                            stockMap.put(goodsInfoId, sumStock.subtract(sumVillagesStock) );
                        }
                    } else {
                        stockMap.put(goodsInfoId, sumStock);
                    }
                    goodsWareStockMap.put(goodsInfoId,stockList);
                } else {
                    stockMap.put(goodsInfoId, BigDecimal.ZERO);
                }
            }
        }
        goodsInfoList.forEach(goodsInfo -> {
            if (Objects.nonNull(getskusstock.get(goodsInfo.getGoodsInfoId()))) {
                goodsInfo.setStock(getskusstock.get(goodsInfo.getGoodsInfoId()));
//                if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                    goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                }else{
//                    calGoodsInfoStock(goodsInfo,goodsNumsMap);
//                }
            } else {
                goodsInfo.setStock(BigDecimal.ZERO);
//                if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                    goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                }
            }
            if (CollectionUtils.isNotEmpty(goodsWareStockMap.get(goodsInfo.getGoodsInfoId()))) {
                goodsInfo.setGoodsWareStocks(goodsWareStockMap.get(goodsInfo.getGoodsInfoId()));
            }
        });
        return goodsInfoList;
    }

    /**
     * 根据erpNos查询特价商品
     *
     * @param goodsInfoNos 商品goodsInfoNos
     * @return 商品sku列表
     */
    public List<GoodsInfo> findSpecialGoodsByErpNos(List<String> goodsInfoNos) {
        List<GoodsInfo> goodsInfoList = this.goodsInfoRepository.findSpecialGoodsByErpNos(goodsInfoNos);
        return goodsInfoList;
    }

    /**
     * 根据erpNos查询所有商品
     *
     * @param goodsInfoNos 商品goodsInfoNos
     * @return 商品sku列表
     */
    public List<GoodsInfo> findAllGoodsByErpNos(List<String> goodsInfoNos) {
        List<GoodsInfo> goodsInfoList = this.goodsInfoRepository.findAllGoodsByErpNos(goodsInfoNos);
        return goodsInfoList;
    }

    /**
     * 条件查询SKU数据
     *
     * @param request 查询条件
     * @return 商品sku列表
     */
    public List<GoodsInfo> findByParams(GoodsInfoQueryRequest request) {
        List<GoodsInfo> goodsInfoList = this.goodsInfoRepository.findAll(request.getWhereCriteria());
        // 商户库存转供应商库存
        //this.turnProviderStock(goodsInfoList); //2020 5/15  喜丫丫 库存由WMS第三方提供无需转供应库存
        return goodsInfoList;
    }

    /**
     * 填充商品的有效性
     *
     * @param goodsInfoList 填充后有效状态的商品列表数据
     */
    public List<GoodsInfo> fillGoodsStatus(List<GoodsInfo> goodsInfoList) {
        //验证商品，并设为无效
        goodsInfoList.stream().filter(goodsInfo -> !Objects.equals(GoodsStatus.INVALID, goodsInfo.getGoodsStatus())).forEach(goodsInfo -> {
            if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                    && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                goodsInfo.setGoodsStatus(GoodsStatus.OK);
                //是否是限购商品
                boolean purchaseFlag = Objects.nonNull(goodsInfo.getPurchaseNum()) && goodsInfo.getPurchaseNum() != -1 ;
                //非限购商品
                if (goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0 && (Objects.isNull(goodsInfo.getPurchaseNum()) || goodsInfo.getPurchaseNum() != -1)) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
                //限购商品
                if(purchaseFlag && goodsInfo.getPurchaseNum() < 1){
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
            } else {
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        if (goodsInfoList.stream().filter(goodsInfo -> !Objects.equals(GoodsStatus.INVALID, goodsInfo.getGoodsStatus())).count() < 1) {
            return goodsInfoList;
        }

        List<Long> storeIds = goodsInfoList.stream().map(GoodsInfo::getStoreId).distinct().collect(Collectors.toList());
        ListNoDeleteStoreByIdsRequest listNoDeleteStoreByIdsRequest = new ListNoDeleteStoreByIdsRequest();
        listNoDeleteStoreByIdsRequest.setStoreIds(storeIds);
        BaseResponse<ListNoDeleteStoreByIdsResponse> listNoDeleteStoreByIdsResponseBaseResponse = storeQueryProvider.listNoDeleteStoreByIds(listNoDeleteStoreByIdsRequest);
        Map<Long, StoreVO> storeMap = listNoDeleteStoreByIdsResponseBaseResponse.getContext().getStoreVOList().stream().collect(Collectors.toMap(StoreVO::getStoreId, s -> s));
        LocalDateTime now = LocalDateTime.now();
        goodsInfoList.stream().filter(goodsInfo -> !Objects.equals(GoodsStatus.INVALID, goodsInfo.getGoodsStatus())).forEach(goodsInfo -> {
            StoreVO store = storeMap.get(goodsInfo.getStoreId());
            if (!(store != null
                    && Objects.equals(DeleteFlag.NO, store.getDelFlag())
                    && Objects.equals(StoreState.OPENING, store.getStoreState())
                    && (now.isBefore(store.getContractEndDate()) || now.isEqual(store.getContractEndDate()))
                    && (now.isAfter(store.getContractStartDate()) || now.isEqual(store.getContractStartDate()))
            )) {
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });
        return goodsInfoList;
    }

    /**
     * 刷新spu的上下架状态
     *
     * @param goodsIds 发生变化的spuId列表
     */
    private void updateGoodsAddedFlag(List<String> goodsIds) {
        // 1.查询所有的sku
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsIds(goodsIds);
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(queryRequest.getWhereCriteria());

        // 2.按spu分组
        Map<String, List<GoodsInfo>> goodsMap = goodsInfos.stream().collect(Collectors.groupingBy(GoodsInfo::getGoodsId));

        // 3.判断每个spu的上下架状态
        List<String> yesGoodsIds = new ArrayList<>(); // 上架的spu
        List<String> noGoodsIds = new ArrayList<>(); //  下架的spu
        List<String> partGoodsIds = new ArrayList<>(); // 部分上架的spu
        goodsMap.keySet().forEach(goodsId -> {
            List<GoodsInfo> skus = goodsMap.get(goodsId);
            Long skuCount = (long) (skus.size());
            Long yesCount = skus.stream().filter(sku -> sku.getAddedFlag() == AddedFlag.YES.toValue()).count();

            if (yesCount.equals(0L)) {
                // 下架
                noGoodsIds.add(goodsId);
            } else if (yesCount.equals(skuCount)) {
                // 上架
                yesGoodsIds.add(goodsId);
            } else {
                // 部分上架
                partGoodsIds.add(goodsId);
            }
        });

        // 4.修改spu上下架状态
        if (noGoodsIds.size() != 0) {
            goodsRepository.updateAddedFlagByGoodsIds(AddedFlag.NO.toValue(), noGoodsIds);
        }
        if (yesGoodsIds.size() != 0) {
            goodsRepository.updateAddedFlagByGoodsIds(AddedFlag.YES.toValue(), yesGoodsIds);
        }
        if (partGoodsIds.size() != 0) {
            goodsRepository.updateAddedFlagByGoodsIds(AddedFlag.PART.toValue(), partGoodsIds);
        }
    }

    /**
     * SKU分页
     *
     * @param queryRequest 查询请求
     * @return 商品分页列表
     */
    @Transactional(readOnly = true, timeout = 10)
    public Page<GoodsInfo> page(GoodsInfoQueryRequest queryRequest) {
        Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(queryRequest.getWhereCriteria(),
                queryRequest.getPageRequest());
        // 商户库存转供应商库存
        //this.turnProviderStock(goodsInfoPage.getContent());
        return goodsInfoPage;
    }

    /**
     * SKU统计
     *
     * @param queryRequest 查询请求
     * @return 统计个数
     */
    @Transactional(readOnly = true, timeout = 10)
    public long count(GoodsInfoQueryRequest queryRequest) {
        return goodsInfoRepository.count(queryRequest.getWhereCriteria());
    }

    /**
     * 更新小程序码
     *
     * @param request
     * @return
     */
    @Transactional
    public void updateSkuSmallProgram(GoodsInfoSmallProgramCodeRequest request) {
        goodsInfoRepository.updateSkuSmallProgram(request.getGoodsInfoId(), request.getCodeUrl());
    }

    @Transactional
    public void clearSkuSmallProgramCode() {
        goodsInfoRepository.clearSkuSmallProgramCode();
    }

    /**
     * 分页查询特价商品
     */
    public SpecialGoodsQueryResponse specialgoodsPage(SpecialGoodsInfoQueryRequest request) {
        SpecialGoodsQueryResponse response = new SpecialGoodsQueryResponse();
        List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsCate> goodsCateList = new ArrayList<>();
        List<Goods> goodsList1 = new ArrayList<>();
        //查询goodInfo表
        if (request.isPageFlag() == false) {
            List<GoodsInfo> goodsInfoList = goodsInfoRepository.findAll(request.getWhereCriteria());

            if (goodsInfoList.isEmpty()) {
                response.setGoodsInfoList(goodsInfoList);
                response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
                response.setGoodsBrandList(goodsBrandList);
                response.setGoodsCateList(goodsCateList);
                return response;
            }
            //查询所有SKU规格值关联
            List<String> goodsInfoIds =
                    goodsInfoList.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
            goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(goodsInfoIds));

            GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
            List<String> goodsIds =
                    goodsInfoList.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
            goodsQueryRequest.setGoodsIds(goodsIds);
            List<Goods> goodsList = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
            //填充每个SKU
            goodsInfoList.forEach(goodsInfo -> {
                //sku商品图片为空，则以spu商品主图
                if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goodsList.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new Goods()).getGoodsImg());
                }
                //填充每个SKU的规格关系
                goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
            });
            //获取所有品牌
            GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
            brandRequest.setDelFlag(DeleteFlag.NO.toValue());
            brandRequest.setBrandIds(goodsInfoList.stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getBrandId())).map(GoodsInfo::getBrandId).distinct().collect(Collectors.toList()));
            goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));
            //获取所有分类
            GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
            cateRequest.setCateIds(goodsInfoList.stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getCateId())).map(GoodsInfo::getCateId).distinct().collect(Collectors.toList()));
            goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));
            //获取所有spu列表
            GoodsQueryRequest goodsRequest = new GoodsQueryRequest();
            goodsRequest.setGoodsIds(goodsInfoList.stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getGoodsId())).map(GoodsInfo::getGoodsId).distinct().collect(Collectors.toList()));
            goodsList1.addAll(goodsRepository.findAll(goodsRequest.getWhereCriteria()));

            response.setGoodsInfoList(goodsInfoList);
            response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
            response.setGoodsBrandList(goodsBrandList);
            response.setGoodsCateList(goodsCateList);
            response.setGoodList(goodsList1);

        } else {
            Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
            MicroServicePage<GoodsInfo> microPage = KsBeanUtil.convertPage(goodsInfoPage, GoodsInfo.class);
            if (goodsInfoPage.isEmpty()) {
                response.setGoodsInfoPage(microPage);
                response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
                response.setGoodsBrandList(goodsBrandList);
                response.setGoodsCateList(goodsCateList);
                return response;
            }
            //查询所有SKU规格值关联
            List<String> goodsInfoIds =
                    goodsInfoPage.getContent().stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
            goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(goodsInfoIds));

            GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
            List<String> goodsIds =
                    goodsInfoPage.getContent().stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
            goodsQueryRequest.setGoodsIds(goodsIds);
            List<Goods> goodsList = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
            //填充每个SKU
            microPage.getContent().forEach(goodsInfo -> {
                //sku商品图片为空，则以spu商品主图
                if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goodsList.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new Goods()).getGoodsImg());
                }
                //填充每个SKU的规格关系
                goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
            });
            //获取所有品牌
            GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
            brandRequest.setDelFlag(DeleteFlag.NO.toValue());
            brandRequest.setBrandIds(microPage.getContent().stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getBrandId())).map(GoodsInfo::getBrandId).distinct().collect(Collectors.toList()));
            goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));
            //获取所有分类
            GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
            cateRequest.setCateIds(microPage.getContent().stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getCateId())).map(GoodsInfo::getCateId).distinct().collect(Collectors.toList()));
            goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));
            //获取所有spu列表
            GoodsQueryRequest goodsRequest = new GoodsQueryRequest();
            goodsRequest.setGoodsIds(microPage.getContent().stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getGoodsId())).map(GoodsInfo::getGoodsId).distinct().collect(Collectors.toList()));
            goodsList1.addAll(goodsRepository.findAll(goodsRequest.getWhereCriteria()));

            response.setGoodsInfoPage(microPage);
            response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
            response.setGoodsBrandList(goodsBrandList);
            response.setGoodsCateList(goodsCateList);
            response.setGoodList(goodsList1);
        }
        return response;
    }


    /**
     * 分页查询分销商品
     *
     * @param request 参数
     * @return DistributionGoodsQueryResponse
     */
    public DistributionGoodsQueryResponse distributionGoodsPage(DistributionGoodsQueryRequest request) {
        DistributionGoodsQueryResponse response = new DistributionGoodsQueryResponse();
        List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsCate> goodsCateList = new ArrayList<>();

        //获取该分类的所有子分类
        if (Objects.nonNull(request.getCateId()) && request.getCateId() > 0) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }

        //获取该店铺分类下的所有spuIds
//        List<String> goodsIdList = new ArrayList<>();
        if (Objects.nonNull(request.getStoreCateId()) && request.getStoreCateId() > 0) {
            List<StoreCateGoodsRela> relas = storeCateService.findAllChildRela(request.getStoreCateId(), true);
            if (CollectionUtils.isNotEmpty(relas)) {
                List<String> goodsIdList = relas.stream().map(StoreCateGoodsRela::getGoodsId).collect(Collectors.toList());
                request.setGoodsIds(goodsIdList);
            } else {
                return DistributionGoodsQueryResponse.builder().goodsInfoPage(new MicroServicePage<>(Collections.emptyList(), request.getPageRequest(), 0)).build();
            }
        }

//        // 查询零售模式的spu，再过滤spuId
//        GoodsQueryRequest queryRequest = GoodsQueryRequest.builder().goodsIds(goodsIdList).storeId(request.getStoreId())
//                .delFlag(DeleteFlag.NO.toValue()).saleType(request.getSaleType()).build();
//        List<Goods> goodsRepositoryAll = goodsRepository.findAll(queryRequest.getWhereCriteria());
//        if (CollectionUtils.isNotEmpty(goodsRepositoryAll)) {
//            request.setGoodsIds(goodsRepositoryAll.stream().map(Goods::getGoodsId).collect(Collectors.toList()));
//        } else {
//            return DistributionGoodsQueryResponse.builder().goodsInfoPage(new MicroServicePage<>(Collections.emptyList(), request.getPageRequest(), 0)).build();
//        }

        //分页查询分销商品sku
        request.setDelFlag(DeleteFlag.NO.toValue());
        Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
        MicroServicePage<GoodsInfo> microPage = KsBeanUtil.convertPage(goodsInfoPage, GoodsInfo.class);
        if (Objects.isNull(microPage) || microPage.getTotalElements() < 1 || CollectionUtils.isEmpty(microPage.getContent())) {
            return DistributionGoodsQueryResponse.builder().goodsInfoPage(microPage).build();
        }

        // 商户库存转供应商库存
        //this.turnProviderStock(microPage.getContent());

        //拿到商家相关消息
        List<Long> companyInfoIds =
                goodsInfoPage.getContent().stream().map(GoodsInfo::getCompanyInfoId).distinct().collect(Collectors.toList());
        CompanyInfoQueryByIdsRequest companyInfoQueryByIdsRequest = new CompanyInfoQueryByIdsRequest();
        companyInfoQueryByIdsRequest.setCompanyInfoIds(companyInfoIds);
        companyInfoQueryByIdsRequest.setDeleteFlag(DeleteFlag.NO);
        BaseResponse<CompanyInfoQueryByIdsResponse> companyInfoQueryByIdsResponseBaseResponse =
                companyInfoQueryProvider.queryByCompanyInfoIds(companyInfoQueryByIdsRequest);

        //查询所有SKU规格值关联
        List<String> goodsInfoIds =
                goodsInfoPage.getContent().stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(goodsInfoIds));

        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        List<String> goodsIds =
                goodsInfoPage.getContent().stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<Goods> goodsList = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());

        // 填充店铺分类
        List<StoreCateGoodsRela> storeCateGoodsRelas = storeCateService.getStoreCateByGoods(goodsIds);
        if (CollectionUtils.isNotEmpty(storeCateGoodsRelas)) {
            Map<String, List<StoreCateGoodsRela>> storeCateMap = storeCateGoodsRelas.stream().collect(Collectors.groupingBy(StoreCateGoodsRela::getGoodsId));
            //为每个spu填充店铺分类编号
            if (MapUtils.isNotEmpty(storeCateMap)) {
                microPage.getContent().stream()
                        .filter(goods -> storeCateMap.containsKey(goods.getGoodsId()))
                        .forEach(goods -> {
                            goods.setStoreCateIds(storeCateMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRela::getStoreCateId).filter(id -> id != null).collect(Collectors.toList()));
                        });
            }
        }

        //填充每个SKU
        microPage.getContent().forEach(goodsInfo -> {
            //sku商品图片为空，则以spu商品主图
            if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                goodsInfo.setGoodsInfoImg(goodsList.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new Goods()).getGoodsImg());
            }
            //填充每个SKU的规格关系
            goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
        });

        //获取所有品牌
        GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
        brandRequest.setDelFlag(DeleteFlag.NO.toValue());
        brandRequest.setBrandIds(microPage.getContent().stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getBrandId())).map(GoodsInfo::getBrandId).distinct().collect(Collectors.toList()));
        goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

        //获取所有分类
        GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
        cateRequest.setCateIds(microPage.getContent().stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getCateId())).map(GoodsInfo::getCateId).distinct().collect(Collectors.toList()));
        goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));

        response.setGoodsInfoPage(microPage);
        response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
        response.setGoodsBrandList(goodsBrandList);
        response.setGoodsCateList(goodsCateList);
        response.setCompanyInfoList(companyInfoQueryByIdsResponseBaseResponse.getContext().getCompanyInfoList());
        return response;
    }

    /**
     * 分销商品审核通过(单个)
     *
     * @param request
     */
    @Transactional
    public void checkDistributionGoods(DistributionGoodsCheckRequest request) {
        int checkResult = goodsInfoRepository.checkDistributionGoods(request.getGoodsInfoId());
        if (0 >= checkResult) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 批量审核分销商品
     *
     * @param request
     */
    @Transactional
    public void batchCheckDistributionGoods(DistributionGoodsBatchCheckRequest request) {
        int checkResult = goodsInfoRepository.batchCheckDistributionGoods(request.getGoodsInfoIds());
        if (0 >= checkResult) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 驳回或禁止分销商品
     *
     * @param goodsInfoId
     * @param distributionGoodsAudit
     * @param distributionGoodsAuditReason
     */
    @Transactional
    public void refuseCheckDistributionGoods(String goodsInfoId, DistributionGoodsAudit distributionGoodsAudit,
                                             String distributionGoodsAuditReason) {
        int checkResult = goodsInfoRepository.refuseCheckDistributionGoods(goodsInfoId, distributionGoodsAudit, distributionGoodsAuditReason);
        if (0 >= checkResult) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        if (distributionGoodsAudit.equals(DistributionGoodsAudit.FORBID)) {
            // 同步删除分销员与商品关联表
            distributorGoodsInfoService.deleteByGoodsInfoId(goodsInfoId);
        }
    }

    /**
     * 编辑分销商品，修改佣金和状态
     *
     * @param goodsInfoId
     * @param distributionGoodsAudit
     * @param distributionCommission
     */
    @Transactional
    public void modifyDistributionGoods(String goodsInfoId, BigDecimal distributionCommission, DistributionGoodsAudit distributionGoodsAudit) {
        int checkResult = goodsInfoRepository.modifyDistributionGoods(goodsInfoId, distributionCommission, distributionGoodsAudit);
        if (0 >= checkResult) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 编辑分销商品，修改佣金比例和状态
     *
     * @param goodsInfoId
     * @param
     */
    @Transactional
    public void modifyCommissionDistributionGoods(String goodsInfoId, BigDecimal commissionRate,
                                                  BigDecimal distributionCommission,
                                                  DistributionGoodsAudit distributionGoodsAudit) {
        int checkResult = goodsInfoRepository.modifyCommissionDistributionGoods(goodsInfoId, commissionRate,
                distributionCommission, distributionGoodsAudit);
        if (0 >= checkResult) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /**
     * 删除分销商品
     *
     * @param request
     */
    @Transactional
    public void delDistributionGoods(DistributionGoodsDeleteRequest request) {
        int checkResult = goodsInfoRepository.delDistributionGoods(request.getGoodsInfoId());
        if (0 >= checkResult) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /*
     * @Description: 商品ID<spu> 修改商品审核状态
     * @Param:  goodsId 商品ID
     * @Param:  审核状态
     * @Author: Bob
     * @Date: 2019-03-11 16:33
     */
    @Transactional
    public void modifyDistributeState(String goodsId, DistributionGoodsAudit state) {
        int i = goodsInfoRepository.modifyDistributeState(goodsId, state);
        if (0 >= i) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }

    /*
     * @Description:  商品ID<spu> 查询sku信息
     * @Param:  goodsId 商品ID
     * @Author: Bob
     * @Date: 2019-03-11 17:14
     */
    public List<GoodsInfo> queryBygoodsId(String goodsId) {
        List<String> goodsIds = new ArrayList<>();
        goodsIds.add(goodsId);
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.findByGoodsIds(goodsIds);
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
      /* if (wmsAPIFlag){
           goodsInfoList.get(0).setStock(queryStorkByWMS(InventoryQueryRequest.builder()
                   .customerID(AbstractXYYConstant.CUSTOMER_ID)
                   .sku(goodsInfoList.get(0).getGoodsInfoNo()).build()).getContext().getInventoryQueryReturnVO().getQty());
       }*/
        // 商户库存转供应商库存
        // this.turnProviderStock(goodsInfoList);
        return goodsInfoList;
    }

    public List<GoodsInfo> queryByGoodsIds(List<String> goodsId) {
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.findByGoodsIds(goodsId);
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        return goodsInfoList;
    }

    public List<GoodsInfo> queryByGoodsInfoIds(List<String> goodsInfoIds) {
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.findByGoodsInfoIds(goodsInfoIds);
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        return goodsInfoList;
    }

    /**
     * 查询sku list
     */
    public List<GoodsInfo> findByGoodsInfoIds(List<String> goodsInfoIds) {
        return goodsInfoRepository.findByGoodsInfoIds(goodsInfoIds);
    }

    /**
     * 添加分销商品前，验证所添加的sku是否符合条件
     * 条件：商品是否有效状态（商品已审核通过且未删除和上架状态）以及是否是零售商品
     *
     * @param goodsInfoIds
     * @return
     */
    public List<String> getInvalidGoodsInfoByGoodsInfoIds(List<String> goodsInfoIds) {
        List<Object> goodsInfoIdObj = goodsInfoRepository.getInvalidGoodsInfoByGoodsInfoIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(goodsInfoIdObj)) {
            return goodsInfoIdObj.stream().map(obj -> (String) obj).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 添加企业购商品前，验证所添加的sku是否符合条件
     * 条件：商品是否有效状态（商品已审核通过且未删除和上架状态）以及是否是零售商品
     *
     * @param goodsInfoIds
     * @return
     */
    public List<String> getInvalidEnterpriseByGoodsInfoIds(List<String> goodsInfoIds) {
        List<Object> goodsInfoIdObj = goodsInfoRepository.getInvalidEnterpriseByGoodsInfoIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(goodsInfoIdObj)) {
            return goodsInfoIdObj.stream().map(obj -> (String) obj).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }


    /**
     * 根据单品ids，查询商品名称、市场价、规格值
     *
     * @param goodsInfoIds 单品ids
     * @return
     */
    public List<GoodsInfoParams> findGoodsInfoParamsByIds(List<String> goodsInfoIds) {
        // 1.查询商品名称、市场价
        List<GoodsInfoParams> infos = goodsInfoRepository.findGoodsInfoParamsByIds(goodsInfoIds);

        // 2.查询规格值
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(goodsInfoIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId));
        infos.forEach(goodsInfo -> {
            if (MapUtils.isNotEmpty(specDetailMap)) {
                goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                        .map(GoodsInfoSpecDetailRel::getDetailName)
                        .collect(Collectors.toList()), " "));
            }
        });
        return infos;
    }


    /**
     * 批量修改企业价格接口
     *
     * @param batchEnterPrisePriceDTOS
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateEnterPrisePrice(List<BatchEnterPrisePriceDTO> batchEnterPrisePriceDTOS, DefaultFlag defaultFlag) {
        //商品审核开关已打开--更新状态为待审核
        if (DefaultFlag.NO.equals(defaultFlag)) {
            batchEnterPrisePriceDTOS.forEach(entity -> goodsInfoRepository
                    .updateGoodsInfoEnterPrisePrice(entity.getGoodsInfoId(), entity.getEnterPrisePrice(), EnterpriseAuditState.CHECKED));
        } else {
            //商品审核开关关闭直接更新为已审核
            batchEnterPrisePriceDTOS.forEach(entity -> goodsInfoRepository
                    .updateGoodsInfoEnterPrisePrice(entity.getGoodsInfoId(), entity.getEnterPrisePrice(), EnterpriseAuditState.WAIT_CHECK));
        }

    }

    /**
     * 修改企业价格接口
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEnterPrisePrice(EnterprisePriceUpdateRequest request) {
        //商品已审核--更新状态为待审核
        if (DefaultFlag.YES.equals(request.getEnterpriseGoodsAuditFlag())) {
            goodsInfoRepository.updateGoodsInfoEnterPrisePrice(request.getGoodsInfoId(), request.getEnterPrisePrice(), EnterpriseAuditState.CHECKED);
        } else {
            goodsInfoRepository.updateGoodsInfoEnterPrisePrice(request.getGoodsInfoId(), request.getEnterPrisePrice(), EnterpriseAuditState.WAIT_CHECK);
        }
    }

    /**
     * 删除企业购商品
     *
     * @param goodsInfoId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnterpriseGoods(String goodsInfoId) {
        Optional<GoodsInfo> goodsInfo = goodsInfoRepository.findById(goodsInfoId);
        if (goodsInfo.isPresent()) {
            GoodsInfo sku = goodsInfo.get();
            sku.setEnterPriseAuditState(EnterpriseAuditState.INIT);
            sku.setEnterPrisePrice(BigDecimal.ZERO);
            goodsInfoRepository.saveAndFlush(sku);
        }
    }

    /**
     * 删除企业购商品
     *
     * @param goodsId
     */
    @Transactional(rollbackFor = Exception.class)
    public List<String> batchDeleteEnterpriseGoods(String goodsId) {
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findByGoodsIds(Arrays.asList(goodsId));
        goodsInfos.stream().forEach(goodsInfo -> {
            goodsInfo.setEnterPriseAuditState(EnterpriseAuditState.INIT);
            goodsInfo.setEnterPrisePrice(BigDecimal.ZERO);
        });
        goodsInfoRepository.saveAll(goodsInfos);
        return goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
    }

    /**
     * 审核企业购商品
     *
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public String auditEnterpriseGoodsInfo(EnterpriseAuditCheckRequest request) {
        Optional<GoodsInfo> goodsInfoOptional = goodsInfoRepository.findById(request.getGoodsInfoId());
        if (goodsInfoOptional.isPresent()) {
            if (EnterpriseAuditState.WAIT_CHECK.equals(goodsInfoOptional.get().getEnterPriseAuditState())) {
                goodsInfoOptional.get().setEnterPriseAuditState(request.getEnterpriseAuditState());
                if (EnterpriseAuditState.NOT_PASS.equals(request.getEnterpriseAuditState())) {
                    goodsInfoOptional.get().setEnterPriseGoodsAuditReason(request.getEnterPriseGoodsAuditReason());
                }
                goodsInfoRepository.saveAndFlush(goodsInfoOptional.get());
            } else {
                return CommonErrorCode.STATUS_HAS_BEEN_CHANGED_ERROR;
            }
        }
        return CommonErrorCode.SUCCESSFUL;
    }

    /**
     * 批量审核企业购商品的价格
     *
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchAuditEnterpriseGoods(EnterpriseAuditStatusBatchRequest request) {
        //审核通过
        if (EnterpriseAuditState.CHECKED.equals(request.getEnterpriseGoodsAuditFlag())) {
            goodsInfoRepository.batchAuditEnterprise(request.getGoodsInfoIds(), request.getEnterpriseGoodsAuditFlag());
        }
        //审核被驳回
        if (EnterpriseAuditState.NOT_PASS.equals(request.getEnterpriseGoodsAuditFlag())) {
            goodsInfoRepository.batchRejectAuditEnterprise(request.getGoodsInfoIds(),
                    request.getEnterpriseGoodsAuditFlag(),
                    request.getEnterPriseGoodsAuditReason());
        }
    }

    /**
     * 分页查询企业购商品
     *
     * @param request 参数
     * @return EnterPriseGoodsQueryResponse
     */
    public EnterPriseGoodsQueryResponse enterpriseGoodsPage(EnterpriseGoodsQueryRequest request) {
        EnterPriseGoodsQueryResponse response = new EnterPriseGoodsQueryResponse();
        List<GoodsCate> goodsCateList = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();

        //获取该分类的所有子分类
        if (Objects.nonNull(request.getCateId()) && request.getCateId() > 0) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.setCateId(null);
                request.getCateIds().add(request.getCateId());
            }
        }

        //获取该店铺分类下的所有spuIds
        if (Objects.nonNull(request.getStoreCateId()) && request.getStoreCateId() > 0) {
            List<StoreCateGoodsRela> relas = storeCateService.findAllChildRela(request.getStoreCateId(), true);
            if (CollectionUtils.isNotEmpty(relas)) {
                List<String> goodsIdList = relas.stream().map(StoreCateGoodsRela::getGoodsId).collect(Collectors.toList());
                request.setGoodsIds(goodsIdList);
            } else {
                return EnterPriseGoodsQueryResponse.builder().goodsInfoPage(new MicroServicePage<>(Collections.emptyList(), request.getPageRequest(), 0)).build();
            }
        }
        //分页查询企业购商品sku
        request.setDelFlag(DeleteFlag.NO.toValue());
        Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(KsBeanUtil.copyPropertiesThird(request, CommonGoodsInfoQueryRequest.class).getWhereCriteria(), request.getPageRequest());
        MicroServicePage<GoodsInfo> microPage = KsBeanUtil.convertPage(goodsInfoPage, GoodsInfo.class);
        if (Objects.isNull(microPage) || microPage.getTotalElements() < 1 || CollectionUtils.isEmpty(microPage.getContent())) {
            return EnterPriseGoodsQueryResponse.builder().goodsInfoPage(microPage).build();
        }

        // 商户库存转供应商库存
        // this.turnProviderStock(microPage.getContent());

        //拿到商家相关消息
        List<Long> companyInfoIds =
                goodsInfoPage.getContent().stream().map(GoodsInfo::getCompanyInfoId).distinct().collect(Collectors.toList());
        CompanyInfoQueryByIdsRequest companyInfoQueryByIdsRequest = new CompanyInfoQueryByIdsRequest();
        companyInfoQueryByIdsRequest.setDeleteFlag(DeleteFlag.NO);
        companyInfoQueryByIdsRequest.setCompanyInfoIds(companyInfoIds);
        BaseResponse<CompanyInfoQueryByIdsResponse> companyInfoQueryByIdsResponseBaseResponse =
                companyInfoQueryProvider.queryByCompanyInfoIds(companyInfoQueryByIdsRequest);
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        //查询所有SKU规格值关联
        List<String> goodsInfoIds =
                goodsInfoPage.getContent().stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(goodsInfoIds));

        List<String> goodsIds =
                goodsInfoPage.getContent().stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<Goods> goodsList = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
        // 填充店铺分类
        List<StoreCateGoodsRela> storeCateGoodsRelas = storeCateService.getStoreCateByGoods(goodsIds);
        if (CollectionUtils.isNotEmpty(storeCateGoodsRelas)) {
            Map<String, List<StoreCateGoodsRela>> storeCateMap = storeCateGoodsRelas.stream().collect(Collectors.groupingBy(StoreCateGoodsRela::getGoodsId));
            //为每个spu填充店铺分类编号
            if (MapUtils.isNotEmpty(storeCateMap)) {
                microPage.getContent().stream()
                        .filter(goods -> storeCateMap.containsKey(goods.getGoodsId()))
                        .forEach(goods -> {
                            goods.setStoreCateIds(storeCateMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRela::getStoreCateId).filter(id -> id != null).collect(Collectors.toList()));
                        });
            }
        }

        //填充每个SKU
        microPage.getContent().forEach(goodsInfo -> {
            //sku商品图片为空，则以spu商品主图
            if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                goodsInfo.setGoodsInfoImg(goodsList.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new Goods()).getGoodsImg());
            }
            //填充每个SKU的规格关系
            goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
        });

        //获取所有品牌
        GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
        brandRequest.setDelFlag(DeleteFlag.NO.toValue());
        brandRequest.setBrandIds(microPage.getContent().stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getBrandId())).map(GoodsInfo::getBrandId).distinct().collect(Collectors.toList()));
        goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

        //获取所有分类
        GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
        cateRequest.setCateIds(microPage.getContent().stream().filter(goodsInfo -> Objects.nonNull(goodsInfo.getCateId())).map(GoodsInfo::getCateId).distinct().collect(Collectors.toList()));
        goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));
        response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
        response.setGoodsInfoPage(microPage);
        response.setGoodsCateList(goodsCateList);
        response.setGoodsBrandList(goodsBrandList);
        response.setCompanyInfoList(companyInfoQueryByIdsResponseBaseResponse.getContext().getCompanyInfoList());
        return response;
    }

    /**
     * 商品如果来自供应商，将商户库存批量转供应商库存
     *
     * @param goodsInfoList
     * @return
     * @modify 2020-5-15 喜丫丫项目 库存交给WMS第三方管理 无须辞操作
     */
//    public List<GoodsInfo> turnProviderStock(List<GoodsInfo> goodsInfoList) {
//        if (CollectionUtils.isNotEmpty(goodsInfoList)) {
//            // 过滤出所有来源于供应商的商品sku集合
//            List<GoodsInfo> goodsInfos = goodsInfoList.stream().filter(
//                    goodsInfo -> StringUtils.isNotEmpty(goodsInfo.getProviderGoodsInfoId())).collect(Collectors.toList());
//
//            // 存在来自于供应商的商品信息，要取供应商商品的库存信息
//            if (CollectionUtils.isNotEmpty(goodsInfos)) {
//                // sku商品所属供应商sku商品id集合
//                List<String> providerGoodsInfoIds = goodsInfos.stream().filter(goodsInfoVO ->
//                        StringUtils.isNotEmpty(goodsInfoVO.getProviderGoodsInfoId())).map(GoodsInfo::getProviderGoodsInfoId).collect(Collectors.toList());
//                List<GoodsInfo> providerGoodsInfos = goodsInfoRepository.findAllById(providerGoodsInfoIds);
//
//                goodsInfoList.forEach(vo -> providerGoodsInfos.forEach(goodsInfo -> {
//                    // 用供应商库存替换经营商户的库存
//                    if (goodsInfo.getGoodsInfoId().equals(vo.getProviderGoodsInfoId())) {
//                        vo.setStock(goodsInfo.getStock());
//                    }
//                }));
//            }
//        }
//
//        return goodsInfoList;
//    }

    /**
     * 商品如果来自供应商，将商户库存转供应商库存
     *
     * @param goodsInfo
     * @return
     */
    public GoodsInfo turnSingleProviderStock(GoodsInfo goodsInfo) {
        if (Objects.nonNull(goodsInfo)) {
            // 商户商品skuid对应的供应商商品skuid
            String providerGoodsInfoId = goodsInfo.getProviderGoodsInfoId();
            if (StringUtils.isNotEmpty(providerGoodsInfoId)) {
                GoodsInfo providerGoodsInfo = goodsInfoRepository.findById(providerGoodsInfoId).orElse(new GoodsInfo());
                goodsInfo.setStock(providerGoodsInfo.getStock());
            }
        }

        return goodsInfo;
    }

    /**
     * 商品如果来自供应商，将商户skuid转供应商skuid
     *
     * @param goodsInfoId
     */
    public String turnProviderGoodsInfoId(String goodsInfoId) {
        GoodsInfo goodsInfo = goodsInfoRepository.findById(goodsInfoId).orElse(new GoodsInfo());
        if (StringUtils.isNotEmpty(goodsInfo.getProviderGoodsInfoId())) {
            goodsInfoId = goodsInfo.getProviderGoodsInfoId();
        }

        return goodsInfoId;
    }

    /**
     * 增加特价sku
     */
    public GoodsInfo addSpecialGoods(GoodsInfo goodsInfo) {
        GoodsInfo returnGoods = new GoodsInfo();
        if (Objects.nonNull(goodsInfo)) {
            returnGoods = goodsInfoRepository.save(goodsInfo);
        }
        return returnGoods;
    }

    /**
     * 根据erpGoodsInfoNo查询goodsInfoNo
     *
     * @param erpGoodsInfoNos
     * @return
     */
    public List<String> listGoodsInfoNoByErpNos(List<String> erpGoodsInfoNos) {
        List<Object> listObjects = goodsInfoRepository.findGoodsInfoNosByeErpNos(erpGoodsInfoNos);
        if (CollectionUtils.isNotEmpty(listObjects)) {
            return listObjects.stream().map(o -> (String) o).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 获取匹配的分仓库存
     */
    private void resetStock(GoodsInfo goodsInfo, Long wareId) {
        GoodsWareStock goodsWareStock = goodsWareStockRepository.findTopByGoodsInfoIdAndWareId(goodsInfo.getGoodsInfoId(), wareId);
        if(goodsWareStock != null && goodsWareStock.getStock() != null){
            goodsInfo.setStock(goodsWareStock.getStock());
        }else{
            goodsInfo.setStock(BigDecimal.ZERO);
        }
    }

    public List<GoodsInfoVO> findAllSpecialGoods() {
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAllSpecialGoods();
        if (CollectionUtils.isNotEmpty(goodsInfos)) {
            return KsBeanUtil.convert(goodsInfos, GoodsInfoVO.class);
        }
        return new ArrayList<>();
    }


    /**
     * 批量更新商品的批次号
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse batchUpdateGoodsInfoBatchNo(List<GoodsBatchNoDTO> goodsBatchNoDTOS) {
        goodsBatchNoDTOS.stream().forEach(g ->
                this.updateGoodsInfoBatchNo(g.getGoodsInfoBatchNo(), g.getGoodsInfoId()));
        return BaseResponse.SUCCESSFUL();
    }

    //@Transactional(rollbackFor = Exception.class)
    public void updateGoodsInfoBatchNo(String goodsInfoBatchNo, String goodsInfoId) {
        try {
            log.info("更新批次号id:"+goodsInfoId+"No"+goodsInfoBatchNo);
            goodsInfoRepository.updateGoodsInfoBatchNo(goodsInfoBatchNo, goodsInfoId);
            devanningGoodsInfoRepository.updateGoodsInfoBatchNo(goodsInfoBatchNo, goodsInfoId);
        }catch (Exception e){
            log.error("更新批次号错误goodsInfoId是"+goodsInfoId);
            log.error("更新批次号错误"+e.getMessage());
            log.error("更新批次号错误"+e);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsInfoKeywordsAndSortNum(String keywords, Integer sortNum, String goodsInfoId) {
        goodsInfoRepository.updateGoodsInfoKeywords(keywords, sortNum, goodsInfoId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsInfoCateSortNum(Integer sortNum, String goodsInfoId) {
        goodsInfoRepository.updateGoodsInfoCateSortNum(sortNum, goodsInfoId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetSkuSortNumAndKeywords(String goodsInfoId) {
        goodsInfoRepository.resetSkuSortNumAndKeywords(goodsInfoId);
    }

    @Transactional
    public void saveAll(List<GoodsInfo> goodsInfos){
        goodsInfoRepository.saveAll(goodsInfos);
    }

    /**
     * @param
     * @return
     * @discription 获取storeid
     * @author yangzhen
     * @date 2020/9/2 20:37
     */
    public Long queryStoreId(String SkuId) {
        Long storeId = goodsInfoRepository.queryStoreId(SkuId);
        return storeId;
    }

    /**
     * 查询erp不为空的商品
     *
     * @return
     */
    public List<GoodsInfo> findAllByErp() {
        return goodsInfoRepository.findAllByErp();
    }

    /**
     * 查询商品信息
     *
     * @param goodsInfoIds
     * @return
     */
    public List<GoodsInfo> findGoodsInfoByIds(List<String> goodsInfoIds, Boolean matchWareHouseFlag) {
        List<Object> goodsInfoIdObj = goodsInfoRepository.findGoodsInfoByIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(goodsInfoIdObj)) {
            List<GoodsInfo> goodsInfoList = resultToGoodsInfoList(goodsInfoIdObj);
            List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIds);
            List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(goodsInfoIds);

            Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfoIds);

            if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
                List<Long> unOnline = null;
                if (Objects.nonNull(matchWareHouseFlag) && !matchWareHouseFlag) {
                    List<Long> storeList = goodsWareStocks.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
                    unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE).stream()
                            .map(WareHouseVO::getWareId).collect(Collectors.toList());
                }
                for (GoodsInfo goodsInfo : goodsInfoList) {
                    List<GoodsWareStock> stockList;
                    List<GoodsWareStockVillages> villagesList;
                    if (CollectionUtils.isNotEmpty(unOnline)) {
                        List<Long> finalUnOnline = unOnline;
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                        && finalUnOnline.contains(goodsWareStock.getWareId())).
                                collect(Collectors.toList());
                        villagesList = goodsWareStockVillagesList.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                        && finalUnOnline.contains(goodsWareStock.getWareId())).
                                collect(Collectors.toList());
                    } else {
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                                collect(Collectors.toList());
                        villagesList = goodsWareStockVillagesList.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                                collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                        if (CollectionUtils.isNotEmpty(villagesList)) {
                            BigDecimal sumVillagesStock = villagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                            if (sumStock.subtract(sumVillagesStock).compareTo(BigDecimal.ZERO)  <= 0) {
                                goodsInfo.setStock(BigDecimal.ZERO);
                            } else {
                                goodsInfo.setStock(sumStock.subtract(sumVillagesStock) );
                            }
                        } else {
                            goodsInfo.setStock(sumStock);
                        }
                        goodsInfo.setStock(getskusstock.getOrDefault(goodsInfo.getGoodsInfoId(),BigDecimal.ZERO));

//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }
                    } else {
                        goodsInfo.setStock(BigDecimal.ZERO);
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }
                    }
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
                }

                return goodsInfoList;
            }
        } else {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    /**
     * 查询商品信息库存去redis查询
     *
     * @param goodsInfoIds
     * @return
     */
    public List<GoodsInfo> findGoodsInfoByIdsAndCache(List<String> goodsInfoIds, Boolean matchWareHouseFlag) {
        List<Object> goodsInfoIdObj = goodsInfoRepository.findGoodsInfoByIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(goodsInfoIdObj)) {
            List<GoodsInfo> goodsInfoList = resultToGoodsInfoList(goodsInfoIdObj);
            if (CollectionUtils.isNotEmpty(goodsInfoList)){
                for (GoodsInfo goodsInfo : goodsInfoList){
                    String key = RedisKeyConstants.GOODS_INFO_STOCK_HASH.concat(goodsInfo.getGoodsInfoId());
//                    if (!redisCache.HashHasKey(key,RedisKeyConstants.GOODS_INFO_STOCK_HASH)){
                    if (true){
                        List<String> skuids =  new LinkedList<>();
                        skuids.add(goodsInfo.getGoodsInfoId());
                        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(skuids);
                        goodsInfo.setStock(getskusstock.getOrDefault(goodsInfo.getGoodsInfoId(),BigDecimal.ZERO));

                    }
                    else {
                        BigDecimal stock = new BigDecimal(0);
                        BigDecimal parseDouble = new BigDecimal(0);
                        stock = BigDecimal.valueOf(Double.parseDouble(redisCache.HashGet(key, RedisKeyConstants.GOODS_INFO_WMS_STOCK).toString()));
                        if (!redisCache.HashHasKey(key, RedisKeyConstants.GOODS_INFO_YK_STOCK)){
                            List<String> skuids =  new LinkedList<>();
                            skuids.add(goodsInfo.getGoodsInfoId());
                            Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusJiYastock(skuids);
                            Map<String,Object> map = new HashMap<>();
                            if (Objects.isNull(getskusstock.get(goodsInfo.getGoodsInfoId()))){
                                map.put(RedisKeyConstants.GOODS_INFO_YK_STOCK,0);
                            }else {
                                map.put(RedisKeyConstants.GOODS_INFO_YK_STOCK,getskusstock.get(goodsInfo.getGoodsInfoId()));
                            }
                            redisCache.setHashAll(key,map);
                        }else {
                            parseDouble = BigDecimal.valueOf(Double.parseDouble(redisCache.HashGet(key, RedisKeyConstants.GOODS_INFO_YK_STOCK).toString()));
                        }
                        goodsInfo.setStock(stock.subtract(parseDouble));
                    }
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
                }
                return goodsInfoList;
            }

        } else {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }



    /**
     * 功能描述: <br>
     * 〈〉组装商品信息
     *
     * @Param: [resultsObj]
     * @Return: java.util.List<com.wanmi.sbc.goods.info.model.root.GoodsInfo>
     * @Author: yxb
     * @Date: 2021/2/2 14:26
     */
    private List<GoodsInfo> resultToGoodsInfoList(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            GoodsInfo goodsInfo = new GoodsInfo();
            Object[] results = StringUtil.cast(item, Object[].class);
            goodsInfo.setGoodsId(StringUtil.cast(results, 0, String.class));
            goodsInfo.setGoodsInfoId(StringUtil.cast(results, 1, String.class));
            String goodsInfoImg = StringUtil.cast(results, 2, String.class);
            if (StringUtils.isNotBlank(goodsInfoImg)) {
                goodsInfo.setGoodsInfoImg(goodsInfoImg);
            } else {
                String goodsImg = StringUtil.cast(results, 3, String.class);
                if (StringUtils.isNotBlank(goodsImg)) {
                    goodsInfo.setGoodsInfoImg(goodsImg);
                } else {
                    goodsInfo.setGoodsInfoImg("");
                }
            }
            goodsInfo.setMarketPrice(StringUtil.cast(results, 4, BigDecimal.class));
            goodsInfo.setGoodsUnit(StringUtil.cast(results, 5, String.class));
            goodsInfo.setGoodsInfoName(StringUtil.cast(results, 6, String.class));
            int delFlag = StringUtil.cast(results, 7, Byte.class).intValue();
            goodsInfo.setDelFlag(DeleteFlag.fromValue(delFlag));
            int addFlag = StringUtil.cast(results, 8, Byte.class).intValue();
            int auditFlag = StringUtil.cast(results, 9, Byte.class).intValue();
            int checkedAddFlag = StringUtil.cast(results,10,Byte.class).intValue();
            goodsInfo.setCheckedAddedFlag(checkedAddFlag);
            goodsInfo.setAddedFlag(addFlag);
            goodsInfo.setAuditStatus(CheckStatus.fromValue(auditFlag));

            return goodsInfo;
        }).collect(Collectors.toList());
    }

    public List<String> listGoodsInfoByStock() {
        // 1. 查询库存为0，且处于上架状态的商品
        List<String> goodsInfoId = goodsInfoRepository.listGoodsInfoByStock();
        // 2. 查询带T商品信息且处于上架状态
//        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAllByTAndAddedFlag();
//        if (CollectionUtils.isNotEmpty(goodsInfos)){
//            goodsInfos.forEach(goodsInfo -> {
//                //获取商品的囤货总数
//                Long goodsPileNum = pileTradeQueryProvider.getGoodsPileNum(goodsInfo.getGoodsInfoId()).getContext();
//                //如果囤货总数大于库存，下架商品
//                if (goodsPileNum >= goodsInfo.getStock()){
//                    goodsInfoId.add(goodsInfo.getGoodsId());
//                }
//            });
//        }
//
//        List<String> spuIds = goodsInfoId.stream().distinct().collect(Collectors.toList());
        return goodsInfoId;

    }

    public List<GoodsInfo> getGoodsInfoByT(){
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAllByTAndAddedFlag();
        return goodsInfos;
    }

    /**
     * 查询正常，上架的商品
     */
    public List<GoodsInfo> queryByValidErpGoodsInfoNos(List<String> erpGoodsInfoNos, Long wareId, Boolean matchWareHouseFlag) {
        List<GoodsInfo> goodsInfoList = this.goodsInfoRepository.findValidGoodsInfoByErpGoodsInfoNos(erpGoodsInfoNos);
        //获取对应的商品分仓库存
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            return Collections.emptyList();
        }
        List<String> validGoodsInfoIds = goodsInfoList.stream().map(g -> g.getGoodsInfoId()).collect(Collectors.toList());
        List<GoodsWareStock> goodsWareStockVOList = goodsWareStockService.getGoodsWareStockByGoodsInfoIds(GoodsWareStockByGoodsForIdsRequest.builder().goodsForIdList(validGoodsInfoIds).wareId(wareId).build());
        Map<String, BigDecimal> stockMap = new HashMap<>();
        List<Long> unOnline = new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(matchWareHouseFlag) && !matchWareHouseFlag) {
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            for (String goodsInfoId : validGoodsInfoIds) {
                List<GoodsWareStock> stockList;
                if (CollectionUtils.isNotEmpty(unOnline)) {

                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    stockMap.put(goodsInfoId, sumStock);
                } else {
                    stockMap.put(goodsInfoId, BigDecimal.ZERO);
                }
            }
        }
        goodsInfoList.forEach(goodsInfo -> {
            if (Objects.nonNull(stockMap.get(goodsInfo.getGoodsInfoId()))) {
                goodsInfo.setStock(stockMap.get(goodsInfo.getGoodsInfoId()));
            } else {
                goodsInfo.setStock(BigDecimal.ZERO);
            }
        });
        return goodsInfoList;
    }

    /**
     * 查商品近30天销量排行
     * @param request
     * @return
     */
    public Map<String, Long> getSalesRanking(GoodsSalesRankingRequest request) {
        //声明收集
        Map<String, Long> finalMap = new LinkedHashMap<>();
        //mongodb中读取数据
        List<OrderSalesRanking> resultList = getOrderSalesRankings();

        //查总榜情况
        if (request.getCateId() == null || request.getCateId() == 0) {
            finalMap = resultList.stream().
                    collect(Collectors.groupingBy(OrderSalesRanking::getSkuId,Collectors.counting()));
        }else{
            //获取分类信息-from db
            GoodsCate goodsCate = goodsCateRepository.findById(request.getCateId()).orElse(null);
            if(goodsCate != null){
                if(goodsCate.getCateGrade() == 2){
                    //查询二级分类下所有3级分类-from db
                    List<GoodsCate> goodsCateList = goodsCateRepository.findByCateParentId(goodsCate.getCateParentId());
                    if(goodsCateList != null && goodsCateList.size() > 0){
                        List<Long> cate3List = goodsCateList.stream().map(GoodsCate::getCateId).collect(Collectors.toList());
                        List<OrderSalesRanking> cate3ResultList = resultList.stream().filter(f -> cate3List.contains(f.getCateId())).collect(Collectors.toList());
                        //再根据skuid分组
                        finalMap = cate3ResultList.stream().
                                collect(Collectors.groupingBy(OrderSalesRanking::getSkuId, Collectors.counting()));
                    }
                }else if(goodsCate.getCateGrade() == 3){
                    //传cateId的情况, 根据catId分组
                    Map<Long, List<OrderSalesRanking>> map = resultList.stream()
                            .collect(Collectors.groupingBy(OrderSalesRanking::getCateId));
                    for (Long cateId : map.keySet()) {
                        //查找对应分类的top排行榜
                        if (request.getCateId().equals(cateId)) {
                            List<OrderSalesRanking> orderSalesRankings = map.get(cateId);
                            //再根据skuid分组
                            finalMap = orderSalesRankings.stream().collect(Collectors.groupingBy(OrderSalesRanking::getSkuId, Collectors.counting()));
                        }
                    }
                }
            }
        }
        return finalMap;
    }

    private List<OrderSalesRanking> getOrderSalesRankings() {
        Object context = pileTradeQueryProvider.salesRanking().getContext();
        List<Map<String, Objects>> list = (List<Map<String, Objects>>) context;
        List<OrderSalesRanking> resultList = list.stream().map(x ->
        {
            long cateId = Long.parseLong(x.get("cateId") + "");
            OrderSalesRanking orderSalesRanking = OrderSalesRanking.builder()
                    .spuId(x.get("spuId") + "")
                    .skuId(x.get("skuId") + "")
                    .cateId(cateId)
                    .cateName(x.get("cateName") + "")
                    .build();
            return orderSalesRanking;
        }).collect(Collectors.toList());
        return resultList;
    }

//    private List<GoodsBySalesRankingVO> getSalesRankingByGoodsId(Map<String, Long> finalMap) {
//        List<GoodsBySalesRankingVO> result = Lists.newArrayList();
//        //查库把其它字段补齐
//        List<String> spuIdList = finalMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
//        List<Goods> allByGoodsIdIn = goodsRepository.findAllByGoodsIdIn(spuIdList);
//        for (String spuId : finalMap.keySet()) {
//            for (Goods goods : allByGoodsIdIn) {
//                if (spuId.equals(goods.getGoodsId())) {
//                    GoodsBySalesRankingVO build = GoodsBySalesRankingVO.builder()
//                            .goodsId(goods.getGoodsId())
//                            .goodsName(goods.getGoodsName())
//                            .goodsImg(goods.getGoodsImg())
//                            .goodsSubtitle(goods.getGoodsSubtitle())
//                            .marketPrice(goods.getMarketPrice())
//                            .goodsSalesRanking(finalMap.get(spuId))
//                            .cateId(goods.getCateId())
//                            .build();
//                    result.add(build);
//                }
//            }
//        }
//        return result.stream().limit(10).collect(Collectors.toList());
//    }

    public Integer getSalesRankingTop(GoodsSalesRankingTopRequest request) {
        List<OrderSalesRanking> resultList = redisService.getList("SALES_RANKING_TOP",OrderSalesRanking.class);
        if(CollectionUtils.isEmpty(resultList)){
            //mongodb中读取数据
            resultList = getOrderSalesRankings();
            redisService.setObj("SALES_RANKING_TOP",resultList,60*60*24);
        }
        //声明收集
        Map<String, Long> finalMap = new LinkedHashMap<>();
        Map<String, List<OrderSalesRanking>> collectMap = resultList.stream().
                collect(Collectors.groupingBy(OrderSalesRanking::getSpuId));
        Map<String, Long> skuIdMap = Maps.newHashMap();
//        for (String spuId : collectMap.keySet()) {
//            skuIdMap.put(spuId, Long.valueOf(collectMap.get(spuId).size()));
//        }
        collectMap.forEach((spuId,orderSalesRankings) ->{
            skuIdMap.put(spuId, Long.valueOf(orderSalesRankings.size()));
        });
        skuIdMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue()
                        .reversed()).forEachOrdered(e -> finalMap.put(e.getKey(), e.getValue()));
        //查找skuid对应spuid
        String spuId = null;
        if(CollectionUtils.isNotEmpty(resultList)){
            OrderSalesRanking salesRanking = resultList.stream().filter(orderSalesRanking ->
                    orderSalesRanking.getSkuId().equals(request.getSkuId())).findFirst().orElse(null);
            if(Objects.nonNull(salesRanking)){
                spuId = salesRanking.getSpuId();
            }
        }
//        for (OrderSalesRanking orderSalesRanking : resultList) {
//            if (orderSalesRanking.getSkuId().equals(request.getSkuId())) {
//                spuId = orderSalesRanking.getSpuId();
//            }
//        }
        if (StringUtils.isNotBlank(spuId)) {
//            AtomicInteger index = new AtomicInteger(0);
            int i = 0;
            for (String spuIdKey : finalMap.keySet()) {
                i++;
                if (spuIdKey.equals(spuId)) {
                    break;
                }
            }
            //return i > 10 ? null : i;
            return  i;
        }
        return null;
    }

    /**
     * 通过skuIds获取商品囤货数量信息
     * @param skuIds
     * @return
     */
    public Map<String, Long> getGoodsPileNumBySkuIds(List<String> skuIds){
        if (CollectionUtils.isEmpty(skuIds)){
            return null;
        }
        return pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(skuIds).build()).getContext().getGoodsNumsMap();
    }

    /**
     * 计算库存 虚拟库存 囤货数量
     * @param goodsInfo
     * @param goodsNumsMap
     */
    public void calGoodsInfoStock(GoodsInfo goodsInfo,Map<String,Long> goodsNumsMap){
        //库存不能为空
        if (Objects.nonNull(goodsInfo.getStock())){
            //如果虚拟库存不为空且大于0
            if (Objects.nonNull(goodsInfo.getVirtualStock()) && goodsInfo.getVirtualStock().compareTo(BigDecimal.ZERO) > 0){
                goodsInfo.setStock(goodsInfo.getStock().add(goodsInfo.getVirtualStock()));
            }
            //如果囤货数量不为空且大于0
            Long pileGoodsNum = goodsNumsMap.getOrDefault(goodsInfo.getGoodsInfoId(),null);
            if(Objects.nonNull(pileGoodsNum) && pileGoodsNum > 0){
                goodsInfo.setStock(goodsInfo.getStock().subtract(BigDecimal.valueOf(pileGoodsNum)));
            }
            log.info("skuId:{},stock:{},virtualStock:{},pileGoodsNum:{}",goodsInfo.getGoodsInfoId(),goodsInfo.getStock(),goodsInfo.getVirtualStock(),pileGoodsNum);
        }
    }


    /**
     * 编辑推荐商品排序
     * @param request
     */
    @Transactional
    public void modifyRecommendSort(List<GoodsInfoModifyRecommendSortRequest> request) {
        //验证商品排序序号是否重复
//        if(recommendSort!=null){
//            List<GoodsInfo> goodsInfos = goodsInfoRepository.getExistByRecommendSort(recommendSort, goodsInfoId);
//            if (CollectionUtils.isNotEmpty(goodsInfos)) {
//                throw new SbcRuntimeException(GoodsErrorCode.SEQ_NUM_ALREADY_EXISTS);
//            }
//        }
        request.forEach(r -> goodsInfoRepository.modifyRecommendSort(r.getRecommendSort(),r.getGoodsInfoId()));
//        goodsInfoRepository.modifyRecommendSort(recommendSort, goodsInfoId);
    }

    @Transactional
    public void clearAllRecommendSort(Long wareId) {
        goodsInfoRepository.clearAllRecommendSort(wareId);
    }

    /**
     * 通过条件查询sku列表
     * @param queryRequest
     * @return
     */
    public List<GoodsInfo> findAllList(GoodsInfoQueryRequest queryRequest) {
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(queryRequest.getWhereCriteria());
        return goodsInfos;
    }

    /**
     * 减去乡镇件库存
     * @param goodsInfoList
     * @param skuIds
     * @param wareId
     */
    public void reduceVillagesStock(List<GoodsInfo> goodsInfoList,List<String> skuIds,Long wareId) {
        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.getGoodsStockByAreaIdAndGoodsInfoIds(skuIds, wareId);
        if (CollectionUtils.isNotEmpty(goodsWareStockVillagesList)) {
            Map<String, GoodsWareStockVillages> villagesMap = goodsWareStockVillagesList.stream()
                    .collect(Collectors.toMap(GoodsWareStockVillages::getGoodsInfoId, g -> g));
            goodsInfoList.forEach(goodsInfo -> {
                GoodsWareStockVillages goodsWareStockVillages = villagesMap.getOrDefault(goodsInfo.getGoodsInfoId(),null);
                if (Objects.nonNull(goodsWareStockVillages)) {
                    if (goodsInfo.getStock().subtract(goodsWareStockVillages.getStock()).compareTo(BigDecimal.ZERO)  <= 0) {
                        goodsInfo.setStock(BigDecimal.ZERO);
                    } else {
                        goodsInfo.setStock(goodsInfo.getStock().subtract(goodsWareStockVillages.getStock()) );
                    }
                }
            });
        }
    }

    public List<String> listStockoutGoods() {
        return goodsInfoRepository.listStockoutGoods();
    }

    public List<String> listByCondition4PileActivity(ListByCondition4PileActivityRequest request) {
        //获取该分类的所有子分类
        if (request.getCateId() != null && request.getCateId() > 0) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            request.getCateIds().add(request.getCateId());
        }

        return goodsInfoRepository.listByCondition4PileActivity(
                request.getStoreId(),
                request.getWareId(),
                request.getGoodsId(),
                request.getGoodsInfoId(),
                request.getLikeErpNo(),
                request.getLikeGoodsName(),
                request.getGoodsInfoType(),
                request.getCateIds(),
                request.getBrandId(),
                null);
    }

    public List<String> listGoodsInfoIds(ListGoodsInfoIdsRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            return Collections.emptyList();
        }

        //获取该分类的所有子分类
        if (request.getCateId() != null && request.getCateId() > 0) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            request.getCateIds().add(request.getCateId());
        }

        int total = request.getGoodsInfoIds().size();
        int pageNum = 1;
        int pageSize = 1000;
        int fromIndex, toIndex;

        List<String> list = new ArrayList<>();
        List<String> searchIds;
        do {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = Math.min(fromIndex + pageSize, total);

            searchIds = request.getGoodsInfoIds().subList(fromIndex, toIndex);
            list.addAll(
                    goodsInfoRepository.listByCondition4PileActivity(
                            null,
                            request.getWareId(),
                            null,
                            null,
                            request.getLikeErpNo(),
                            request.getLikeGoodsName(),
                            null,
                            request.getCateIds(),
                            request.getBrandId(),
                            searchIds
                    )
            );
            pageNum++;
        } while (toIndex < total);
        return list;
    }

    public Map<String, ListGoodsInfoByGoodsInfoIdsVO> listGoodsInfoByGoodsInfoIds(List<String> goodsInfoIds) {
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAllById(goodsInfoIds);
        if (CollectionUtils.isEmpty(goodsInfos)) {
            return Collections.emptyMap();
        }

        //获取所有品牌
        GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
        brandRequest.setDelFlag(DeleteFlag.NO.toValue());
        brandRequest.setBrandIds(goodsInfos.stream().map(GoodsInfo::getBrandId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        Map<Long, String> brandMap = goodsBrandRepository.findAll(brandRequest.getWhereCriteria()).stream()
                .collect(Collectors.toMap(GoodsBrand::getBrandId, GoodsBrand::getBrandName, (k1, k2) -> k1));
        //获取所有分类
        GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
        cateRequest.setCateIds(goodsInfos.stream().map(GoodsInfo::getCateId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        Map<Long, String> cateMap = goodsCateRepository.findAll(cateRequest.getWhereCriteria()).stream()
                .collect(Collectors.toMap(GoodsCate::getCateId, GoodsCate::getCateName, (k1, k2) -> k1));

        return goodsInfos.stream().map(item -> {
            ListGoodsInfoByGoodsInfoIdsVO vo = new ListGoodsInfoByGoodsInfoIdsVO();
            vo.setGoodsInfoId(item.getGoodsInfoId());
            vo.setGoodsInfoNo(item.getGoodsInfoNo());
            vo.setErpNo(item.getErpGoodsInfoNo());
            vo.setGoodsInfoName(item.getGoodsInfoName());
            vo.setWareId(item.getWareId());
            vo.setCateId(item.getCateId());
            vo.setBrandId(item.getBrandId());
            vo.setCateName(cateMap.get(item.getCateId()));
            vo.setBrandName(brandMap.get(item.getBrandId()));
            vo.setGoodsInfoPrice(item.getMarketPrice());
            return vo;
        }).collect(Collectors.toMap(ListGoodsInfoByGoodsInfoIdsVO::getGoodsInfoId, v -> v));
    }

    public List<Long> findBrandsHasAddedSku(List<Long> brandIds, Long wareId) {
        Assert.notNull(wareId, "仓库id不能为null");
        if (CollectionUtils.isEmpty(brandIds)) {
            return Collections.emptyList();
        }
        return goodsInfoRepository.findBrandsHasAddedSku(brandIds, wareId);
    }

    public List<Long> listByClassifyType(Integer type) {
        return goodsInfoRepository.listByClassifyType(type);
    }

    public Map<String, Map<String, String>> getChangShaSkuInfoByNanChangSkuIdMap() {
        //南昌 skuId -> erpNo --> 长沙skuInfo --> 长沙spuId --> 长沙spuInfo
        //长沙skuId -> devaningInfo
        Map<String, String> nanChangErpNoBySkuIdMap = getNanChangErpNoBySkuIdMap();
        Map<String, GoodsInfo> changShaSkuInfoByErpNoMap = getChangShaSkuInfoByErpNoMap();
        Map<String, DevanningGoodsInfo> changShaDevanningInfoBySkuIdMap = getChangShaDevanningInfoBySkuIdMap();

        Map<String, Map<String, String>> resultMap = new HashMap<>();
        nanChangErpNoBySkuIdMap.forEach((nanChangSkuId, erpNo)->{
            Map<String, String> oneResult = new HashMap<>();
            //长沙skuId
            GoodsInfo goodsInfo = changShaSkuInfoByErpNoMap.getOrDefault(erpNo, new GoodsInfo());
            DevanningGoodsInfo deva = changShaDevanningInfoBySkuIdMap.getOrDefault(goodsInfo.getGoodsInfoId(),new DevanningGoodsInfo());
            if (Objects.isNull(goodsInfo.getGoodsInfoId()) || Objects.isNull(deva.getDevanningId())) {
                log.warn("Objects.isNull(goodsInfo.getGoodsInfoId()) || Objects.isNull(deva.getDevanningId()),nanChangSkuId={}", nanChangSkuId);
            }
            oneResult.put("skuId", goodsInfo.getGoodsInfoId());
            oneResult.put("skuNo", goodsInfo.getGoodsInfoNo());
            oneResult.put("spuId", goodsInfo.getGoodsId());
            oneResult.put("parentGoodsInfoId", goodsInfo.getParentGoodsInfoId());
            oneResult.put("devanningId", String.valueOf(deva.getDevanningId()));
            oneResult.put("erpSkuNo", deva.getErpGoodsInfoNo());
            resultMap.put(nanChangSkuId, oneResult);
        });
        return resultMap;
    }

    private Map<String, DevanningGoodsInfo> getChangShaDevanningInfoBySkuIdMap() {
        List<DevanningGoodsInfo> list = devanningGoodsInfoRepository.findAllByWareIdAndDelFlag(1L, DeleteFlag.NO);
        return list.stream()
                .collect(Collectors.toMap(DevanningGoodsInfo::getGoodsInfoId, v -> v, (k1, k2) -> k1));
    }

    private Map<String, GoodsInfo> getChangShaSkuInfoByErpNoMap() {
        List<GoodsInfo> list = goodsInfoRepository.findAllByWareIdAndDelFlag(1L, DeleteFlag.NO);
        return list.stream().map(item -> {
            item.setErpGoodsInfoNo(item.getErpGoodsInfoNo().replace("001-", ""));
            return item;
        }).collect(Collectors.toMap(GoodsInfo::getErpGoodsInfoNo, v -> v, (k1, k2) -> k1));
    }

    private Map<String, String> getNanChangErpNoBySkuIdMap() {
        List<GoodsInfo> list = goodsInfoRepository.findAllByWareIdAndDelFlag(46L, DeleteFlag.NO);
        return list.stream().map(item -> {
            item.setErpGoodsInfoNo(item.getErpGoodsInfoNo().replace("002-", ""));
            return item;
        }).collect(Collectors.toMap(GoodsInfo::getGoodsInfoId, GoodsInfo::getErpGoodsInfoNo, (k1, k2) -> k1));
    }

    public Map<String, Map<String, String>> getChangShaSkuInfoBySkuIdMap() {
        List<GoodsInfo> list = goodsInfoRepository.findAllByWareIdAndDelFlag(1L, DeleteFlag.NO);
        Map<String, DevanningGoodsInfo> changShaDevanningInfoBySkuIdMap = getChangShaDevanningInfoBySkuIdMap();

        Map<String, Map<String, String>> resultMap = new HashMap<>();
        list.forEach(goodsInfo->{
            Map<String, String> oneResult = new HashMap<>();
            //长沙skuId
            DevanningGoodsInfo deva = changShaDevanningInfoBySkuIdMap.getOrDefault(goodsInfo.getGoodsInfoId(),new DevanningGoodsInfo());
            if (Objects.isNull(goodsInfo.getGoodsInfoId()) || Objects.isNull(deva.getDevanningId())) {
                log.warn("Objects.isNull(goodsInfo.getGoodsInfoId()) || Objects.isNull(deva.getDevanningId()),nanChangSkuId={}", goodsInfo.getGoodsInfoId());
            }
            oneResult.put("skuId", goodsInfo.getGoodsInfoId());
            oneResult.put("skuNo", goodsInfo.getGoodsInfoNo());
            oneResult.put("spuId", goodsInfo.getGoodsId());
            oneResult.put("parentGoodsInfoId", goodsInfo.getParentGoodsInfoId());
            oneResult.put("devanningId", String.valueOf(deva.getDevanningId()));
            oneResult.put("erpSkuNo", deva.getErpGoodsInfoNo());
            resultMap.put(goodsInfo.getGoodsInfoId(), oneResult);
        });
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String,Integer> lockStock(List<GoodsInfoLockStockRequest> lockStockRequests) {

    	// 统仓统配商家会共享喜吖吖库存，所以要重新设置stockList根据erpNo锁定库存
    	resetStockList(lockStockRequests);

        //第1次锁定：成功，需创建延时任务
        //第2次锁定：已锁定，直接返回锁定成功

        //写入锁定信息表
        //执行锁定库存: update stock_table set lock_stock = lock_stock+ added_lock where skuId = ? and stock - lock_stock - added_lock >=0

        //1、第1次锁定  2、非第1次锁定
        Map<String,Integer> resultMap = new LinkedHashMap<>();
        for (GoodsInfoLockStockRequest request : lockStockRequests) {
            GoodsLockRecords goodsLockRecords = new GoodsLockRecords();
            goodsLockRecords.setBusinessId(request.getBusinessId());
//            Optional<GoodsLockRecords> recordsOptional = goodsLockRecordsRepository.findById(request.getBusinessId());
//            if (recordsOptional.isPresent()) {
//                log.info("已锁定，不再继续锁库存,业务ID={}", request.getBusinessId());
//                continue;
//            }

            int updCount = goodsLockRecordsRepository.reLock(goodsLockRecords);
            if (updCount > 0) {
                log.info("已锁定，不再继续锁库存,业务ID={}", request.getBusinessId());
                resultMap.put(request.getBusinessId(), 2);
                continue;
            }

            updCount = goodsLockRecordsRepository.reliveLock(goodsLockRecords);
            if (updCount == 0) {
                LocalDateTime createTime = LocalDateTime.now();
                goodsLockRecords.setCreateTime(createTime);
                goodsLockRecords.setUpdateTime(createTime);
                goodsLockRecords.setLockContent(JSON.toJSONString(request));
                goodsLockRecordsRepository.lock(goodsLockRecords);
            }

            //排序以防死锁
            List<GoodsInfoLockStockDTO> sortedStockList = request.getStockList().stream()
                    .sorted(Comparator.comparing(GoodsInfoLockStockDTO::getGoodsInfoId))
                    .collect(Collectors.toList());
            for (GoodsInfoLockStockDTO dto : sortedStockList) {
                dto.setWareId(request.getWareId());
                int updCountStock = goodsWareStockService.lockStock(dto);
                if (updCountStock <= 0) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "锁定库存失败：业务id=" + request.getBusinessId() + "skuId=" + dto.getGoodsInfoId() + " wareId=" + dto.getWareId());
				}
//                Assert.isTrue(updCountStock > 0, "锁定库存失败：业务id=" + request.getBusinessId() + "skuId=" + dto.getGoodsInfoId() + " wareId=" + dto.getWareId());

            }
            resultMap.put(request.getBusinessId(), 1);
        }
        return resultMap;
    }

	/**
	 * 重新设置stockList
	 * @param lockStockRequests
	 */
	private void resetStockList(List<GoodsInfoLockStockRequest> lockStockRequests) {

		for (GoodsInfoLockStockRequest request : lockStockRequests) {
        	BaseResponse<TradeGetByIdResponse> tradeResp = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(request.getBusinessId()).build());
        	TradeVO tradeVO = tradeResp.getContext().getTradeVO();
      		log.info("订单[{}]所属商家类型[{}]", request.getBusinessId(), tradeVO.getSupplier().getCompanyType());

        	if (Arrays.asList(CompanyType.PLATFORM, CompanyType.UNIFIED).contains(tradeVO.getSupplier().getCompanyType())) {
        		log.info("订单[{}]需要根据erpNo锁定库存", request.getBusinessId());
            	// 得到相关商品集合
            	List<String> goodsInfoIds = request.getStockList().stream()
            			.map(GoodsInfoLockStockDTO::getGoodsInfoId)
            			.collect(Collectors.toList());
            	List<GoodsInfo> goodsInfos = goodsInfoRepository.findByGoodsInfoIds(goodsInfoIds);

            	// 根据erpNo查出所有要锁定库存的商品
            	List<String> erpGoodsInfoNos = goodsInfos.stream().map(GoodsInfo::getErpGoodsInfoNo).collect(Collectors.toList());
            	List<GoodsInfo> erpNoGoodsInfos = goodsInfoRepository.findValidGoodsInfoByErpGoodsInfoNos(erpGoodsInfoNos);

            	// 构建key为erpNo,value为stock的Map
            	Map<String, BigDecimal> stockMap = new HashMap<>();
              	for (GoodsInfoLockStockDTO dto : request.getStockList()) {
            		Optional<GoodsInfo> findFirst = goodsInfos.stream().filter(v -> v.getGoodsInfoId().equals(dto.getGoodsInfoId())).findFirst();
            		if (findFirst.isPresent()) {
            			stockMap.put(findFirst.get().getErpGoodsInfoNo(), dto.getStock());
					}
              	}

            	// 相同erpNo的商品库存设为一致
              	List<GoodsInfoLockStockDTO> stockList = new ArrayList<>();
            	for (GoodsInfo goodsInfo : erpNoGoodsInfos) {
                    GoodsInfoLockStockDTO dto = new GoodsInfoLockStockDTO();
                    BigDecimal stock = stockMap.get(goodsInfo.getErpGoodsInfoNo());
                    if (stock == null) {
                		log.info("商品[{}]无法设置相同erpNo的商品库存,库存将设置为0", goodsInfo.getGoodsInfoId());
                    	stock = BigDecimal.ZERO;
					}
                    dto.setStock(stock);
                    dto.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                    stockList.add(dto);
				}
          		log.info("订单[{}]旧stockList[{}]新stockList[{}]", request.getBusinessId(), request.getStockList(), stockList);
            	// 给stockList重新赋值
            	request.setStockList(stockList);;
			}

        }
	}

    @Transactional(rollbackFor = Exception.class)
    public int unlockStock(List<GoodsInfoUnlockStockRequest> requests) {
        log.info("释放锁定库存开始：{}", requests.get(0).getBusinessId());
        int unlockCount = 0;
        for (GoodsInfoUnlockStockRequest request : requests) {
            int updCount = goodsLockRecordsRepository.invalidLock(request.getBusinessId());
            log.info("释放锁定库存开始:失效锁定记录 {}", updCount);
            if (updCount == 0) {
                log.info("无锁定记录，不需处理，业务id={}", request.getBusinessId());
                continue;
            }
            unlockCount++;
            //排序以防死锁
//            List<GoodsInfoUnlockStockDTO> stockList = request.getStockList();
            Optional<GoodsLockRecords> findById = goodsLockRecordsRepository.findById(request.getBusinessId());
            GoodsInfoUnlockStockRequest parseObject = JSON.parseObject(findById.get().getLockContent(), GoodsInfoUnlockStockRequest.class);
            List<GoodsInfoUnlockStockDTO> stockList = parseObject.getStockList();
            log.info("订单[{}]stockList[{}]", request.getBusinessId(), stockList);

            List<GoodsInfoUnlockStockDTO> sortedStockList = stockList.stream()
                    .sorted(Comparator.comparing(GoodsInfoUnlockStockDTO::getGoodsInfoId))
                    .collect(Collectors.toList());
            for (GoodsInfoUnlockStockDTO dto : sortedStockList) {
                dto.setWareId(request.getWareId());
                updCount = goodsWareStockService.unlockStock(dto);
                Assert.isTrue(updCount > 0, "释放锁定库存失败：解锁库存失败，业务id=" + request.getBusinessId() + "skuId=" + dto.getGoodsInfoId() + " wareId=" + dto.getWareId());
            }
        }
        log.info("释放锁定库存结束：{}", requests.get(0).getBusinessId());
        return unlockCount;
    }

    public boolean checkIsLocked(String businessId) {
        Optional<GoodsLockRecords> lockOpt = goodsLockRecordsRepository.findById(businessId);
        return lockOpt.isPresent() && DeleteFlag.NO.equals(lockOpt.get().getDelFlag());
    }

    public List<GoodsInfo> findGoodsInfoVoBySkuIds(List<String> ids) {
        return goodsInfoRepository.findAllById(ids);
    }

    public List<GoodsInfoSimpleVo> findGoodsInfoSimpleVoBySkuIds(List<String> skuIds) {
        return goodsInfoRepository.findGoodsInfoSimpleVoBySkuIds(skuIds);
    }

    /*
     * @Description:  商品ID<spu> 查询sku信息
     * @Param:  goodsId 商品ID
     * @Author: Bob
     * @Date: 2019-03-11 17:14
     */
    public List<GoodsInfo> getByGoodsIdAndAdded(String goodsId) {
        List<String> goodsIds = new ArrayList<>();
        goodsIds.add(goodsId);
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.getByGoodsIdAndAdded(goodsIds);
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }

        return goodsInfoList;
    }

    /**
     * 更新预售订单库存，并生成日志记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePresellGoodsInfoStock(List<GoodsInfoPresellRecordDTO> recordDTOList){
        //更新预售商品虚拟库存数量
        recordDTOList.forEach(item->{
            goodsInfoRepository.updateGoodsInfoPresellNum(item.getPresellCount(), item.getGoodsInfoId());
        });
        List<GoodsInfoPresellRecord> recordList = KsBeanUtil.copyListProperties(recordDTOList, GoodsInfoPresellRecord.class);

        //批量新增预售虚拟库存扣减记录
        recordList.forEach(item->{
            item.setCreateTime(LocalDateTime.now());
        });
        goodsInfoPresellRecordRepository.saveAll(recordList);
    }

    public MicroServicePage<GoodsInfoAreaLimitPageResponse> goodsInfoAreaLimitPage(GoodsInfoAreaLimitPageRequest request) {
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAreaFlag(BoolFlag.YES);
        queryRequest.putSort("areaUpdateTime", SortType.DESC.toValue());
        Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(queryRequest.getWhereCriteria(), queryRequest.getPageRequest());
        goodsInfoPage.getContent().forEach(info -> {
            if (StringUtils.isBlank(info.getGoodsInfoImg()) && Objects.nonNull(info.getGoods())) {
                info.setGoodsInfoImg(info.getGoods().getGoodsImg());
            }
        });
        return KsBeanUtil.convertPage(goodsInfoPage, GoodsInfoAreaLimitPageResponse.class);
    }

    public void goodsInfoAreaLimitAdd(GoodsInfoAreaEditRequest request) {

        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(GoodsInfoQueryRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).build().getWhereCriteria());
        goodsInfos.forEach(info -> {
            info.setAreaFlag(BoolFlag.YES);
            info.setAreaUpdateTime(LocalDateTime.now());
            info.setAllowedPurchaseArea(request.getAllowedPurchaseArea());
            info.setAllowedPurchaseAreaName(request.getAllowedPurchaseAreaName());
            info.setSingleOrderAssignArea(request.getSingleOrderAssignArea());
            info.setSingleOrderAssignAreaName(request.getSingleOrderAssignAreaName());
            info.setSingleOrderPurchaseNum(request.getSingleOrderPurchaseNum());
        });

        if (CollectionUtils.isNotEmpty(goodsInfos)) {
            goodsInfoRepository.saveAll(goodsInfos);
        }
    }

    public void goodsInfoAreaLimitDeleteById(String goodsInfoId) {
        GoodsInfo goodsInfo = goodsInfoRepository.findById(goodsInfoId).orElse(null);
        if (Objects.nonNull(goodsInfo)) {
            goodsInfo.setAreaFlag(BoolFlag.NO);
            goodsInfo.setAreaUpdateTime(LocalDateTime.now());
            goodsInfo.setAllowedPurchaseArea(null);
            goodsInfo.setAllowedPurchaseAreaName(null);
            goodsInfo.setSingleOrderAssignArea(null);
            goodsInfo.setSingleOrderAssignAreaName(null);
            goodsInfo.setSingleOrderPurchaseNum(null);
            goodsInfoRepository.saveAndFlush(goodsInfo);
        }
    }

    public GoodsInfoAreaLimitPageResponse goodsInfoAreaLimitGetById(String goodsInfoId) {
        GoodsInfo goodsInfo = goodsInfoRepository.findById(goodsInfoId).orElse(null);
        if (Objects.isNull(goodsInfo)) {
            return null;
        }
        GoodsInfoAreaLimitPageResponse response = KsBeanUtil.convert(goodsInfo, GoodsInfoAreaLimitPageResponse.class);

        String goodsInfoSubtitle = null;
        List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.getByInfoId(goodsInfoId);
        if (CollectionUtils.isNotEmpty(devanningGoodsInfos)) {
            goodsInfoSubtitle = devanningGoodsInfos.get(0).getGoodsSubtitle();
        }

        // Goods goods = goodsRepository.findById(goodsInfo.getGoodsId()).orElse(null);
        if (StringUtils.isBlank(goodsInfoSubtitle) && Objects.nonNull(goodsInfo.getGoods())) {
            /*if (Objects.nonNull(goods)) {
                goodsInfoSubtitle = goods.getGoodsSubtitle();
            }*/
            goodsInfoSubtitle = goodsInfo.getGoods().getGoodsSubtitle();
        }
        response.setGoodsInfoSubtitle(goodsInfoSubtitle);

        if (StringUtils.isBlank(response.getGoodsInfoImg()) && Objects.nonNull(goodsInfo.getGoods())) {
            response.setGoodsInfoImg(goodsInfo.getGoods().getGoodsImg());
        }

        if (Objects.nonNull(goodsInfo.getGoods())) {
            List<Long> storeCateIds = storeCateService.getStoreCateByGoods(Collections.singletonList(goodsInfo.getGoods().getGoodsId())).stream().map(StoreCateGoodsRela::getStoreCateId).filter(Objects::nonNull).collect(Collectors.toList());
            response.setStoreCateIds(storeCateIds);
        }


        return response;
    }
}
