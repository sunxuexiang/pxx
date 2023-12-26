package com.wanmi.sbc.goods.info.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.constant.SigningClassErrorCode;
import com.wanmi.sbc.customer.api.constant.StoreCateErrorCode;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.goods.api.constant.GoodsBrandErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsCateErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateGoodsRelaQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelQueryRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockAddListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockAddRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForNoRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockQueryRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemUpdateRequest;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.stockoutdetail.StockoutDetailQueryRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateGoodsRelaListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsInsidePageResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsStoreOnSaleResponse;
import com.wanmi.sbc.goods.ares.GoodsAresService;
import com.wanmi.sbc.goods.bean.dto.SpecialGoodsAddDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.ContractBrandRepository;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRepository;
import com.wanmi.sbc.goods.brand.request.ContractBrandQueryRequest;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.repository.ContractCateRepository;
import com.wanmi.sbc.goods.cate.repository.GoodsCateRepository;
import com.wanmi.sbc.goods.cate.request.ContractCateQueryRequest;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.common.GoodsCommonService;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import com.wanmi.sbc.goods.distributor.goods.repository.DistributiorGoodsInfoRepository;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoods;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateGoodsRepository;
import com.wanmi.sbc.goods.goodsattributekey.request.GoodsAtrrKeyQueryRequest;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import com.wanmi.sbc.goods.goodsattributekey.service.GoodsAttributeKeyService;
import com.wanmi.sbc.goods.goodsimagestype.repository.GoodsImagestypeRepository;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodslabel.service.GoodsLabelService;
import com.wanmi.sbc.goods.goodslabelrela.model.root.GoodsLabelRela;
import com.wanmi.sbc.goods.goodslabelrela.repository.GoodsLabelRelaRepository;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import com.wanmi.sbc.goods.goodsrecommendgoods.service.GoodsRecommendGoodsService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStockVillages;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockVillagesService;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import com.wanmi.sbc.goods.goodswarestockdetail.service.GoodsWareStockDetailService;
import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.images.service.GoodsImageService;
import com.wanmi.sbc.goods.info.model.root.*;
import com.wanmi.sbc.goods.info.reponse.GoodsDetailResponse;
import com.wanmi.sbc.goods.info.reponse.GoodsEditResponse;
import com.wanmi.sbc.goods.info.reponse.GoodsQueryResponse;
import com.wanmi.sbc.goods.info.reponse.GoodsResponse;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.GoodsPropDetailRelRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsSaveRequest;
import com.wanmi.sbc.goods.mapping.model.root.GoodsInfoMapping;
import com.wanmi.sbc.goods.mapping.service.GoodsInfoMappingService;
import com.wanmi.sbc.goods.merchantconfig.root.MerchantRecommendGoods;
import com.wanmi.sbc.goods.merchantconfig.service.MerchantConfigGoodsService;
import com.wanmi.sbc.goods.mq.GoodsProducerService;
import com.wanmi.sbc.goods.pointsgoods.model.root.PointsGoods;
import com.wanmi.sbc.goods.pointsgoods.repository.PointsGoodsRepository;
import com.wanmi.sbc.goods.pointsgoods.service.PointsGoodsWhereCriteriaBuilder;
import com.wanmi.sbc.goods.price.model.root.GoodsCustomerPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsLevelPrice;
import com.wanmi.sbc.goods.price.repository.GoodsCustomerPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsIntervalPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsLevelPriceRepository;
import com.wanmi.sbc.goods.redis.RedisService;
import com.wanmi.sbc.goods.relationgoodsimages.model.root.RelationGoodsImages;
import com.wanmi.sbc.goods.relationgoodsimages.repository.RelationGoodsImagesRepository;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpec;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecDetailRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecRepository;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import com.wanmi.sbc.goods.standard.model.root.StandardSku;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRepository;
import com.wanmi.sbc.goods.standard.repository.StandardSkuRepository;
import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
import com.wanmi.sbc.goods.stockoutdetail.service.StockoutDetailService;
import com.wanmi.sbc.goods.storecate.model.root.StoreCate;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.repository.StoreCateGoodsRelaRepository;
import com.wanmi.sbc.goods.storecate.repository.StoreCateRepository;
import com.wanmi.sbc.goods.storecate.request.StoreCateQueryRequest;
import com.wanmi.sbc.goods.storecate.service.StoreCateService;
import com.wanmi.sbc.goods.storegoodstab.model.root.GoodsTabRela;
import com.wanmi.sbc.goods.storegoodstab.model.root.StoreGoodsTab;
import com.wanmi.sbc.goods.storegoodstab.repository.GoodsTabRelaRepository;
import com.wanmi.sbc.goods.storegoodstab.repository.StoreGoodsTabRepository;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import com.wanmi.sbc.goods.warehouse.service.WareHouseWhereCriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class GoodsService {
    @Autowired
    GoodsAresService goodsAresService;

    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private DevanningGoodsInfoRepository devanningGoodsInfoRepository;

    @Autowired
    private GoodsSpecRepository goodsSpecRepository;

    @Autowired
    private GoodsSpecDetailRepository goodsSpecDetailRepository;

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    @Autowired
    private GoodsImagestypeRepository goodsImagestypeRepository;

    @Autowired
    private RelationGoodsImagesRepository relationGoodsImagesRepository;

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
    private GoodsTabRelaRepository goodsTabRelaRepository;

    @Autowired
    private StoreCateGoodsRelaRepository storeCateGoodsRelaRepository;

    @Autowired
    private StoreCateRepository storeCateRepository;
    @Autowired
    private StoreCateGoodsRelaQueryProvider storeCateGoodsRelaQueryProvider;
    @Autowired
    private StoreGoodsTabRepository storeGoodsTabRepository;

    @Autowired
    private ContractCateRepository contractCateRepository;

    @Autowired
    private ContractBrandRepository contractBrandRepository;

    @Autowired
    private GoodsPropDetailRelRepository goodsPropDetailRelRepository;

    @Autowired
    private StandardGoodsRelRepository standardGoodsRelRepository;

    @Autowired
    private StandardGoodsRepository standardGoodsRepository;

    @Autowired
    private StandardSkuRepository standardSkuRepository;

    @Autowired
    private GoodsCateService goodsCateService;

    @Autowired
    private GoodsCommonService goodsCommonService;

    @Autowired
    private FreightTemplateGoodsRepository freightTemplateGoodsRepository;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private DistributiorGoodsInfoRepository distributiorGoodsInfoRepository;

    @Autowired
    private PointsGoodsRepository pointsGoodsRepository;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private GoodsWareStockVillagesService goodsWareStockVillagesService;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GoodsLabelService goodsLabelService;

    @Autowired
    private GoodsLabelRelaRepository goodsLabelRelaRepository;

    @Autowired
    private StockoutDetailService stockoutDetailService;

    @Autowired
    private GoodsProducerService goodsProducerService;

    @Autowired
    private DevanningGoodsInfoService devanningGoodsInfoService;

    @Autowired
    private GoodsRecommendGoodsService goodsRecommendGoodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private GoodsImageService goodsImageService;
    @Autowired
    private GoodsAttributeKeyService goodsAttributeKeyService;

    @Autowired
    private GoodsInfoMappingService goodsInfoMappingService;
    @Autowired
    private GoodsWareStockDetailService goodsWareStockDetailService;
    @Autowired
    private MerchantConfigGoodsService merchantConfigGoodsService;
    @Autowired
    private MerchantConfigGoodsQueryProvider merchantConfigGoodsQueryProvider;
    @Autowired
    private StoreCateService storeCateService;

    private final static String GOODS_ARRIVAL="GOODS_ARRIVAL";//到货通知标识
    private final static int ARRIVAL_NODE=40; //到货通知node标识

    /**
     * 分页查询商品
     *
     * @param request 参数
     * @return list
     */
    public GoodsQueryResponse page(GoodsQueryRequest request) {
        GoodsQueryResponse response = new GoodsQueryResponse();

        List<GoodsInfo> goodsInfos = new ArrayList<>();
        List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsCate> goodsCateList = new ArrayList<>();

        //根据SKU模糊查询SKU，获取SKU编号
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setStockUp(request.getStockUp());
        //是否先查询SKU信息，获取SKU编号(默认不查询)
        Boolean checkSKU = false;
        if (StringUtils.isNotBlank(request.getLikeGoodsInfoNo()) || StringUtils.isNotBlank(request.getLikeErpNo())) {
            infoQueryRequest.setLikeGoodsInfoNo(request.getLikeGoodsInfoNo());
            infoQueryRequest.setLikeErpNo(request.getLikeErpNo());
            checkSKU = true;
        }
        if (Objects.nonNull(request.getSpecialPriceFirst()) || Objects.nonNull(request.getSpecialPriceLast())
                || StringUtils.isNotEmpty(request.getGoodsInfoBatchNo())) {
            infoQueryRequest.setSpecialPriceFirst(request.getSpecialPriceFirst());
            infoQueryRequest.setSpecialPriceLast(request.getSpecialPriceLast());
            infoQueryRequest.setGoodsInfoBatchNo(request.getGoodsInfoBatchNo());
            checkSKU = true;
        }
        if (checkSKU) {
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            infoQueryRequest.setGoodsInfoType(request.getGoodsInfoType());
            List<GoodsInfo> infos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
            if (CollectionUtils.isNotEmpty(infos)) {
                request.setGoodsIds(infos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList()));
            } else {
                return response;
            }
        }

        //获取该分类的所有子分类
        if (request.getCateId() != null) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }

        if (Objects.isNull(request.getStockUp())) {
            Page<Goods> goodsPage = goodsRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
            if (CollectionUtils.isNotEmpty(goodsPage.getContent())) {
                List<String> goodsIds = goodsPage.getContent().stream().map(Goods::getGoodsId).collect(Collectors.toList());
                //查询所有SKU
                infoQueryRequest.setLikeGoodsInfoNo(null);
                infoQueryRequest.setGoodsIds(goodsIds);
                infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
                goodsInfos.addAll(goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria()));

                // 商户库存转供应商库存
                // goodsInfoService.turnProviderStock(goodsInfos);

                //查询所有SKU规格值关联
                goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsIds(goodsIds));

                //填充每个SKU的规格关系
                goodsInfos.forEach(goodsInfo -> {
                    //为空，则以商品主图
                    if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                        goodsInfo.setGoodsInfoImg(goodsPage.getContent().stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new Goods()).getGoodsImg());
                    }
                    //补充对应的属性信息
                    List<GoodsAttributeKey> query = goodsAttributeKeyService.query(GoodsAtrrKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
                    goodsInfo.setGoodsAttributeKeys(query);
                    goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
                });

                //填充每个SKU的SKU关系
                goodsPage.getContent().forEach(goods -> {
                    goods.setGoodsInfoIds(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId())).map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
                    //合计库存
                    goods.setStock(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getStock())).map(GoodsInfo::getStock).reduce(BigDecimal.ZERO,BigDecimal::add));
                    //取SKU最小市场价
                    goods.setMarketPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getMarketPrice())).map(GoodsInfo::getMarketPrice).min(BigDecimal::compareTo).orElse(goods.getMarketPrice()));
                    //取SKU最小供货价
                    goods.setSupplyPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSupplyPrice())).map(GoodsInfo::getSupplyPrice).min(BigDecimal::compareTo).orElse(goods.getSupplyPrice()));
                    //取商品类型是否特价
                    List<GoodsInfo> goodsInfoList =
                            goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && goodsInfo.getGoodsInfoType() == 1).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(goodsInfoList)) {
                        goods.setGoodsType(2);//特价
                    }
                    //取商品特价价格
                    goods.setSpecialPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSpecialPrice())).map(GoodsInfo::getSpecialPrice).min(BigDecimal::compareTo).orElse(goods.getSpecialPrice()));

                    goods.setGoodsInfoBatchNo(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && !Objects.equals(goodsInfo.getGoodsInfoType(), 0)).filter(goodsInfo -> Objects.nonNull(goodsInfo.getGoodsInfoBatchNo())).map(GoodsInfo::getGoodsInfoBatchNo).findFirst().orElse(""));

                    goods.setVipPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(
                            goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getVipPrice()) && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0)
                            .map(GoodsInfo::getVipPrice).min(BigDecimal::compareTo).orElse(goods.getVipPrice()));
                    goods.setMarketingId(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(GoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(GoodsInfo::getPurchaseNum).findFirst().orElse(0L));
                });

                //获取所有品牌
                GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
                brandRequest.setDelFlag(DeleteFlag.NO.toValue());
                brandRequest.setBrandIds(goodsPage.getContent().stream().filter
                        (goods -> Objects.nonNull(goods.getBrandId())).map(Goods::getBrandId).distinct().collect(Collectors.toList()));
                goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

                //获取所有分类
                GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
                cateRequest.setCateIds(goodsPage.getContent().stream().filter(goods -> Objects.nonNull(goods.getCateId())).map(Goods::getCateId).distinct().collect(Collectors.toList()));
                goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));
            }
            response.setGoodsPage(goodsPage);
            response.setGoodsInfoList(goodsInfos);
            response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
            response.setGoodsBrandList(goodsBrandList);
            response.setGoodsCateList(goodsCateList);
        }else {
            List<Goods> goodsList = new ArrayList<>();
            Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria(),request.getPageRequest());
            if (CollectionUtils.isNotEmpty(goodsInfoPage.getContent())){
                List<GoodsInfo> goodsInfoList = goodsInfoPage.getContent();
                List<String> goodsIds = goodsInfoPage.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
                GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
                goodsQueryRequest.setGoodsIds(goodsIds);
                goodsQueryRequest.setAuditStatus(CheckStatus.CHECKED);
                goodsList.addAll(goodsRepository.findAll(goodsQueryRequest.getWhereCriteria()));
                goodsList.stream().forEach(goods -> {
                    goods.setMarketingId(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(GoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(GoodsInfo::getPurchaseNum).findFirst().orElse(0L));
                });
            }
            response.setGoodsPage(new PageImpl<>(goodsList,goodsInfoPage.getPageable(),goodsInfoPage.getTotalElements()));
        }

        return response;
    }


    /**
     * 分页查询商品
     *
     * @param request 参数
     * @return list
     */
    public GoodsQueryResponse pagedevanning(GoodsQueryRequest request) {
        GoodsQueryResponse response = new GoodsQueryResponse();

        List<GoodsInfo> goodsInfos = new ArrayList<>();
        List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsCate> goodsCateList = new ArrayList<>();
        List<GoodsAttributeKey> goodsAttributeKeys=new ArrayList<>();
        //根据SKU模糊查询SKU，获取SKU编号
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setStoreIds(request.getStoreIds());
        infoQueryRequest.setStockUp(request.getStockUp());
        infoQueryRequest.setCompanyType(request.getCompanyType());
        //是否先查询SKU信息，获取SKU编号(默认不查询)
        Boolean checkSKU = false;
        if (StringUtils.isNotBlank(request.getLikeGoodsInfoNo()) || StringUtils.isNotBlank(request.getLikeErpNo())) {
            infoQueryRequest.setLikeGoodsInfoNo(request.getLikeGoodsInfoNo());
            infoQueryRequest.setLikeErpNo(request.getLikeErpNo());
            checkSKU = true;
        }
        if (Objects.nonNull(request.getSpecialPriceFirst()) || Objects.nonNull(request.getSpecialPriceLast())
                || StringUtils.isNotEmpty(request.getGoodsInfoBatchNo())) {
            infoQueryRequest.setSpecialPriceFirst(request.getSpecialPriceFirst());
            infoQueryRequest.setSpecialPriceLast(request.getSpecialPriceLast());
            infoQueryRequest.setGoodsInfoBatchNo(request.getGoodsInfoBatchNo());
            checkSKU = true;
        }
        if (checkSKU) {
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            infoQueryRequest.setGoodsInfoType(request.getGoodsInfoType());
            List<GoodsInfo> infos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
            if (CollectionUtils.isNotEmpty(infos)) {
                request.setGoodsIds(infos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList()));
            } else {
                return response;
            }
        }

        //获取该分类的所有子分类
        if (request.getCateId() != null) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }

        if (Objects.isNull(request.getStockUp())) {
            Integer pageNum = request.getPageNum();
            Integer pageSize = request.getPageSize();
            List<Goods> all = goodsRepository.findAll(request.getWhereCriteria());
            request.setPageNum(0);
            request.setPageSize(all.size()<=0?10:all.size());
            Page<Goods> goodsPage1 = goodsRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
            all=goodsPage1.getContent();
            int allsize=all.size();
            GoodsInsidePageResponse goodsPage = new GoodsInsidePageResponse();
            if (CollectionUtils.isNotEmpty(all)){
                goodsPage.setContent(KsBeanUtil.convert(all, GoodsVO.class));
            }else {
                goodsPage.setContent(new ArrayList<>());
            }
            if (Objects.nonNull(request.getManySpecs()) && 0!=request.getManySpecs()){
                goodsPage= devanningGoodsInfoService.getGoodsNorms(goodsPage,request.getManySpecs(),pageNum,pageSize);
            }else {
                all=all.stream().skip(pageNum * pageSize).limit(pageSize).collect(Collectors.toList());
                goodsPage.setContent(KsBeanUtil.convert(all, GoodsVO.class));
                goodsPage.setTotal(allsize);
                goodsPage.setTotalElements(allsize);
                goodsPage.setTotalPages(allsize% pageSize >0?allsize/ pageSize +1:allsize/ pageSize);
            }

            if (CollectionUtils.isNotEmpty(goodsPage.getContent())) {
                List<String> goodsIds = goodsPage.getContent().stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
                //查询所有SKU
                infoQueryRequest.setLikeGoodsInfoNo(null);
                infoQueryRequest.setGoodsIds(goodsIds);
                infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
//                goodsInfos.addAll(goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria()));


                List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().goodsIds(goodsIds).delFlag(DeleteFlag.NO.toValue()).build()).getContext()
                        .getDevanningGoodsInfoVOS();
                devanningGoodsInfoVOS.forEach(v->{
                    //设置库存
                    List<GoodsWareStock> goodsWareStockList = goodsWareStockService.list(GoodsWareStockQueryRequest.builder()
                            .goodsInfoId(v.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());

                    if(CollectionUtils.isNotEmpty(goodsWareStockList)&&Objects.nonNull(v)){
                        BigDecimal sumStock = goodsWareStockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                        v.setStock(sumStock.divide(v.getDivisorFlag()));
                    }
                });

                List<GoodsInfo> convert = KsBeanUtil.convert(devanningGoodsInfoVOS, GoodsInfo.class);
                goodsInfos.addAll(convert );

                // 商户库存转供应商库存
                // goodsInfoService.turnProviderStock(goodsInfos);

                //查询所有SKU规格值关联
                goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsIds(goodsIds));

                //填充每个SKU的规格关系
                GoodsInsidePageResponse finalGoodsPage = goodsPage;
                goodsInfos.forEach(goodsInfo -> {
                    //为空，则以商品主图
                    if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                        goodsInfo.setGoodsInfoImg(finalGoodsPage.getContent().stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new GoodsVO()).getGoodsImg());
                    }
                    goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
                   //补充对应的属性信息
                    List<GoodsAttributeKey> query = goodsAttributeKeyService.query(GoodsAtrrKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
                    goodsInfo.setGoodsAttributeKeys(query);


                });

                //填充每个SKU的SKU关系
                goodsPage.getContent().forEach(goods -> {
                    goods.setGoodsInfoIds(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId())).map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
                    //合计库存
                    goods.setStock(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId())
                            && Objects.nonNull(goodsInfo.getStock())).map(GoodsInfo::getStock).reduce(BigDecimal.ZERO,BigDecimal::add));

                               //这里需要判断商家

                        if ( request.getCompanyType()!=CompanyType.SUPPLIER) {
                        //取SKU最小市场价（自营）
                        goods.setMarketPrice(goodsInfos.stream()
                                .filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()))
                                .filter(goodsInfo -> Objects.nonNull(goodsInfo.getMarketPrice()))
                                .map(GoodsInfo::getMarketPrice).max(BigDecimal::compareTo).orElse(goods.getMarketPrice()));
                        //取SKU最小供货价
                        goods.setSupplyPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSupplyPrice())).map(GoodsInfo::getSupplyPrice).max(BigDecimal::compareTo).orElse(goods.getSupplyPrice()));

                            //取商品类型是否特价
                            List<GoodsInfo> goodsInfoList =
                                    goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && goodsInfo.getGoodsInfoType() == 1).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(goodsInfoList)) {
                                goods.setGoodsType(2);//特价
                            }
                            //取商品特价价格
                            goods.setSpecialPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSpecialPrice())).map(GoodsInfo::getSpecialPrice).min(BigDecimal::compareTo).orElse(goods.getSpecialPrice()));
                            goods.setGoodsInfoBatchNo(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && !Objects.equals(goodsInfo.getGoodsInfoType(), 0)).filter(goodsInfo -> Objects.nonNull(goodsInfo.getGoodsInfoBatchNo())).map(GoodsInfo::getGoodsInfoBatchNo).findFirst().orElse(""));

//                    goodsInfos.stream()
//                            .filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()))
////                          .filter(goodsInfo -> Objects.nonNull(goodsInfo.getVipPrice()) && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0)
//                            .filter(goodsInfo -> goodsInfo.getDivisorFlag().compareTo(BigDecimal.ONE)==0)
//                            .map(GoodsInfo::getVipPrice).max(BigDecimal::compareTo).orElse(goods.getVipPrice())
                            BigDecimal bigDecimal = goodsInfos.stream()
                                    .filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()))
//                          .filter(goodsInfo -> Objects.nonNull(goodsInfo.getVipPrice()) && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0)
                                    .filter(goodsInfo -> goodsInfo.getDivisorFlag().compareTo(BigDecimal.ONE) == 0)
                                    .map(GoodsInfo::getVipPrice).findFirst().orElse(BigDecimal.ZERO);
                            if (bigDecimal.compareTo(goods.getMarketPrice())>0){
                                bigDecimal=goods.getMarketPrice();
                            }else if (bigDecimal.compareTo(BigDecimal.ZERO)==0){
                                bigDecimal=goods.getMarketPrice();
                            }
                            goods.setVipPrice(bigDecimal);
                      }else{
                            Goods goodsVip = goodsRepository.findById(goods.getGoodsId()).get();
                            goods.setMarketPrice(goodsVip.getMarketPrice());
                            goods.setSupplyPrice(goodsVip.getSupplyPrice());
                            goods.setVipPrice(goodsVip.getMarketPrice());
                        }



                    goods.setMarketingId(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(GoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(GoodsInfo::getPurchaseNum).findFirst().orElse(0L));
                });

                //获取所有品牌
                GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
                brandRequest.setDelFlag(DeleteFlag.NO.toValue());
                brandRequest.setBrandIds(goodsPage.getContent().stream().filter
                        (goods -> Objects.nonNull(goods.getBrandId())).map(GoodsVO::getBrandId).distinct().collect(Collectors.toList()));
                goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

                //获取所有分类
                GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
                cateRequest.setCateIds(goodsPage.getContent().stream().filter(goods -> Objects.nonNull(goods.getCateId())).map(GoodsVO::getCateId).distinct().collect(Collectors.toList()));
                goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));

               goodsAttributeKeys = goodsAttributeKeyService.queryList(goodsIds);
            }
            //填充仓库名称
            List<GoodsVO> content = goodsPage.getContent();
            if (CollectionUtils.isNotEmpty(content)){
                List<WareHouse> wareHouseList = wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(WareHouseQueryRequest.builder().build()));
                content.forEach(goodsVO -> {
                    if(Objects.nonNull(goodsVO.getWareId())){
                        goodsVO.setWareName(wareHouseList.stream().filter(wareHouse -> wareHouse.getWareId().equals(goodsVO.getWareId())).findFirst().get().getWareName());
                    }
                });
            }


            response.setGoodsPages(goodsPage);
            response.setGoodsInfoList(goodsInfos);
            response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
            response.setGoodsBrandList(goodsBrandList);
            response.setGoodsCateList(goodsCateList);
            response.setGoodsAttributeKeyVOList(goodsAttributeKeys);
        }else {
            List<Goods> goodsList = new ArrayList<>();
            Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria(),request.getPageRequest());
            if (CollectionUtils.isNotEmpty(goodsInfoPage.getContent())){
                List<GoodsInfo> goodsInfoList = goodsInfoPage.getContent();
                List<String> goodsIds = goodsInfoPage.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
                GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
                goodsQueryRequest.setGoodsIds(goodsIds);
                goodsQueryRequest.setAuditStatus(CheckStatus.CHECKED);
                goodsAttributeKeys = goodsAttributeKeyService.queryList(goodsIds);
                goodsList.addAll(goodsRepository.findAll(goodsQueryRequest.getWhereCriteria()));
                goodsList.stream().forEach(goods -> {
                    goods.setMarketingId(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(GoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(GoodsInfo::getPurchaseNum).findFirst().orElse(0L));

                });
            }
            response.setGoodsAttributeKeyVOList(goodsAttributeKeys);
            response.setGoodsPage(new PageImpl<>(goodsList,goodsInfoPage.getPageable(),goodsInfoPage.getTotalElements()));
        }


        return response;
    }


    /**
     * 分页查询商品
     *
     * @param request 参数
     * @return list
     */
    public GoodsQueryResponse bpagedevanning(GoodsQueryRequest request) {
        GoodsQueryResponse response = new GoodsQueryResponse();

        List<GoodsInfo> goodsInfos = new ArrayList<>();
        List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsCate> goodsCateList = new ArrayList<>();

        //根据SKU模糊查询SKU，获取SKU编号
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setStockUp(request.getStockUp());
        //是否先查询SKU信息，获取SKU编号(默认不查询)
        Boolean checkSKU = false;
        if (StringUtils.isNotBlank(request.getLikeGoodsInfoNo()) || StringUtils.isNotBlank(request.getLikeErpNo())) {
            infoQueryRequest.setLikeGoodsInfoNo(request.getLikeGoodsInfoNo());
            infoQueryRequest.setLikeErpNo(request.getLikeErpNo());
            checkSKU = true;
        }
        if (Objects.nonNull(request.getSpecialPriceFirst()) || Objects.nonNull(request.getSpecialPriceLast())
                || StringUtils.isNotEmpty(request.getGoodsInfoBatchNo())) {
            infoQueryRequest.setSpecialPriceFirst(request.getSpecialPriceFirst());
            infoQueryRequest.setSpecialPriceLast(request.getSpecialPriceLast());
            infoQueryRequest.setGoodsInfoBatchNo(request.getGoodsInfoBatchNo());
            checkSKU = true;
        }
        if (checkSKU) {
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            infoQueryRequest.setGoodsInfoType(request.getGoodsInfoType());
            List<GoodsInfo> infos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
            if (CollectionUtils.isNotEmpty(infos)) {
                request.setGoodsIds(infos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList()));
            } else {
                return response;
            }
        }

        //获取该分类的所有子分类
        if (request.getCateId() != null) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }

        if (Objects.isNull(request.getStockUp())) {
            Integer pageNum = request.getPageNum();
            Integer pageSize = request.getPageSize();
            List<Goods> all = goodsRepository.findAll(request.getWhereCriteria());
            request.setPageNum(0);
            request.setPageSize(all.size()<=0?10:all.size());
            Page<Goods> goodsPage1 = goodsRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
            all=goodsPage1.getContent();
            int allsize=all.size();
            GoodsInsidePageResponse goodsPage = new GoodsInsidePageResponse();
            if (CollectionUtils.isNotEmpty(all)){
                goodsPage.setContent(KsBeanUtil.convert(all, GoodsVO.class));
            }else {
                goodsPage.setContent(new ArrayList<>());
            }
            if (Objects.nonNull(request.getManySpecs()) && 0!=request.getManySpecs()){
                goodsPage= devanningGoodsInfoService.getGoodsNorms(goodsPage,request.getManySpecs(),pageNum,pageSize);
            }else {
                all=all.stream().skip(pageNum * pageSize).limit(pageSize).collect(Collectors.toList());
                goodsPage.setContent(KsBeanUtil.convert(all, GoodsVO.class));
                goodsPage.setTotal(allsize);
                goodsPage.setTotalElements(allsize);
                goodsPage.setTotalPages(allsize% pageSize >0?allsize/ pageSize +1:allsize/ pageSize);
            }

            if (CollectionUtils.isNotEmpty(goodsPage.getContent())) {
                List<String> goodsIds = goodsPage.getContent().stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
                //查询所有SKU
                infoQueryRequest.setLikeGoodsInfoNo(null);
                infoQueryRequest.setGoodsIds(goodsIds);
//                infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
//                goodsInfos.addAll(goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria()));


                List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = devanningGoodsInfoProvider.getBQueryList(DevanningGoodsInfoPageRequest.builder().goodsIds(goodsIds).build()).getContext()
                        .getDevanningGoodsInfoVOS();
                devanningGoodsInfoVOS.forEach(v->{
                    //设置库存
                    List<GoodsWareStock> goodsWareStockList = goodsWareStockService.list(GoodsWareStockQueryRequest.builder()
                            .goodsInfoId(v.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());

                    if(CollectionUtils.isNotEmpty(goodsWareStockList)&&Objects.nonNull(v)){
                        BigDecimal sumStock = goodsWareStockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                        v.setStock(sumStock.divide(v.getDivisorFlag()));
                    }
                });


                goodsInfos.addAll( KsBeanUtil.convert(devanningGoodsInfoVOS,GoodsInfo.class));

                // 商户库存转供应商库存
                // goodsInfoService.turnProviderStock(goodsInfos);

                //查询所有SKU规格值关联
                goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsIds(goodsIds));

                //填充每个SKU的规格关系
                GoodsInsidePageResponse finalGoodsPage = goodsPage;
                goodsInfos.forEach(goodsInfo -> {
                    //为空，则以商品主图
                    if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                        goodsInfo.setGoodsInfoImg(finalGoodsPage.getContent().stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new GoodsVO()).getGoodsImg());
                    }

                    goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
                });

                //填充每个SKU的SKU关系
                goodsPage.getContent().forEach(goods -> {
                    //设置图片

                    //查询商品图片
                    goods.setImages(KsBeanUtil.convert(goodsImageRepository.findByGoodsId(goods.getGoodsId()),GoodsImageVO.class));
                    //查询商品促销图片和合成图片
                    goods.setGoodsImageStypeVOS(KsBeanUtil.convert(goodsImagestypeRepository.findByGoodsId(goods.getGoodsId()),GoodsImageStypeVO.class));
                    //查询商品合成图片关联关系
                    List<RelationGoodsImages> byGoodsId1 = relationGoodsImagesRepository.findByGoodsId(goods.getGoodsId());
                    if (CollectionUtils.isNotEmpty(byGoodsId1)){
                        goods.setRelationGoodsImagesVO(KsBeanUtil.convert(byGoodsId1.get(0),RelationGoodsImagesVO.class));
                    }


                    goods.setGoodsInfoIds(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId())).map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
                    //合计库存
                    goods.setStock(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId())
                            && Objects.nonNull(goodsInfo.getStock())).map(GoodsInfo::getStock).reduce(BigDecimal.ZERO,BigDecimal::add));
                    //取SKU最小市场价
                    goods.setMarketPrice(goodsInfos.stream()
                            .filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()))
                            .filter(goodsInfo -> Objects.nonNull(goodsInfo.getMarketPrice()))
                            .map(GoodsInfo::getMarketPrice).max(BigDecimal::compareTo).orElse(goods.getMarketPrice()));
                    //取SKU最小供货价
                    goods.setSupplyPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSupplyPrice())).map(GoodsInfo::getSupplyPrice).max(BigDecimal::compareTo).orElse(goods.getSupplyPrice()));
                    //取商品类型是否特价
                    List<GoodsInfo> goodsInfoList =
                            goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && goodsInfo.getGoodsInfoType() == 1).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(goodsInfoList)) {
                        goods.setGoodsType(2);//特价
                    }
                    //取商品特价价格
                    goods.setSpecialPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSpecialPrice())).map(GoodsInfo::getSpecialPrice).min(BigDecimal::compareTo).orElse(goods.getSpecialPrice()));
                    goods.setGoodsInfoBatchNo(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && !Objects.equals(goodsInfo.getGoodsInfoType(), 0)).filter(goodsInfo -> Objects.nonNull(goodsInfo.getGoodsInfoBatchNo())).map(GoodsInfo::getGoodsInfoBatchNo).findFirst().orElse(""));

//                    goodsInfos.stream()
//                            .filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()))
////                          .filter(goodsInfo -> Objects.nonNull(goodsInfo.getVipPrice()) && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0)
//                            .filter(goodsInfo -> goodsInfo.getDivisorFlag().compareTo(BigDecimal.ONE)==0)
//                            .map(GoodsInfo::getVipPrice).max(BigDecimal::compareTo).orElse(goods.getVipPrice())
                    BigDecimal bigDecimal = goodsInfos.stream()
                            .filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()))
//                          .filter(goodsInfo -> Objects.nonNull(goodsInfo.getVipPrice()) && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0)
                            .filter(goodsInfo -> goodsInfo.getDivisorFlag().compareTo(BigDecimal.ONE) == 0)
                            .map(GoodsInfo::getVipPrice).findFirst().orElse(BigDecimal.ZERO);
                    if (bigDecimal.compareTo(goods.getMarketPrice())>0){
                        bigDecimal=goods.getMarketPrice();
                    }else if (bigDecimal.compareTo(BigDecimal.ZERO)==0){
                        bigDecimal=goods.getMarketPrice();
                    }
                    goods.setVipPrice(bigDecimal);
                    goods.setMarketingId(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(GoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(GoodsInfo::getPurchaseNum).findFirst().orElse(0L));
                });

                //获取所有品牌
                GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
                brandRequest.setDelFlag(DeleteFlag.NO.toValue());
                brandRequest.setBrandIds(goodsPage.getContent().stream().filter
                        (goods -> Objects.nonNull(goods.getBrandId())).map(GoodsVO::getBrandId).distinct().collect(Collectors.toList()));
                goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

                //获取所有分类
                GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
                cateRequest.setCateIds(goodsPage.getContent().stream().filter(goods -> Objects.nonNull(goods.getCateId())).map(GoodsVO::getCateId).distinct().collect(Collectors.toList()));
                goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));
            }
            //填充仓库名称
            List<GoodsVO> content = goodsPage.getContent();
            if (CollectionUtils.isNotEmpty(content)){
                List<WareHouse> wareHouseList = wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(WareHouseQueryRequest.builder().build()));
                content.forEach(goodsVO -> {
                    if(Objects.nonNull(goodsVO.getWareId())){
                        goodsVO.setWareName(wareHouseList.stream().filter(wareHouse -> wareHouse.getWareId().equals(goodsVO.getWareId())).findFirst().get().getWareName());
                    }
                });
            }
            response.setGoodsPages(goodsPage);
            response.setGoodsInfoList(goodsInfos);
            response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
            response.setGoodsBrandList(goodsBrandList);
            response.setGoodsCateList(goodsCateList);
        }else {
            List<Goods> goodsList = new ArrayList<>();
            Page<GoodsInfo> goodsInfoPage = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria(),request.getPageRequest());
            if (CollectionUtils.isNotEmpty(goodsInfoPage.getContent())){
                List<GoodsInfo> goodsInfoList = goodsInfoPage.getContent();
                List<String> goodsIds = goodsInfoPage.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList());
                GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
                goodsQueryRequest.setGoodsIds(goodsIds);
                goodsQueryRequest.setAuditStatus(CheckStatus.CHECKED);
                goodsList.addAll(goodsRepository.findAll(goodsQueryRequest.getWhereCriteria()));
                goodsList.stream().forEach(goods -> {
                    goods.setMarketingId(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(GoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(GoodsInfo::getPurchaseNum).findFirst().orElse(0L));
                });
            }
            response.setGoodsPage(new PageImpl<>(goodsList,goodsInfoPage.getPageable(),goodsInfoPage.getTotalElements()));
        }

        return response;
    }


    /**
     * 根据ID查询商品
     *
     * @param goodsId 商品ID
     * @return list
     */
    public GoodsEditResponse findInfoById(String goodsId,Long wareId,Boolean matchWareHouseFlag) throws SbcRuntimeException {
        GoodsEditResponse response = new GoodsEditResponse();
        Goods goods = goodsRepository.findById(goodsId).orElse(null);

        List<GoodsLabel> labels = new ArrayList<>();

        //从关联表获取标签数据
        Optional<List<GoodsLabelRela>> byGoodsId = goodsLabelRelaRepository.findByGoodsId(goods.getGoodsId());
        if (byGoodsId.isPresent()) {
            List<GoodsLabelRela> goodsLabelRelas = byGoodsId.get();
            List<Long> idList = goodsLabelRelas.stream().map(GoodsLabelRela::getLabelId).collect(Collectors.toList());
            labels = goodsLabelService.list(GoodsLabelQueryRequest.builder()
                    .idList(idList)
                    .visible(1)
                    .delFlag(DeleteFlag.NO).build());
            goods.setGoodsLabels(labels);
            if (CollectionUtils.isNotEmpty(labels)) {
                String join = StringUtils.join(idList, ",");
                goods.setLabelIdStr(join);
            }
        } else {
            goods.setLabelIdStr(null);
        }

        if (Objects.isNull(goods)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        if (Objects.nonNull(goods.getFreightTempId())) {
            FreightTemplateGoods goodsTemplate = freightTemplateGoodsRepository.queryById(goods.getFreightTempId());
            if (Objects.nonNull(goodsTemplate)) {
                goods.setFreightTempName(goodsTemplate.getFreightTempName());
            }
        }
        response.setGoods(goods);

        // 获取商家所绑定的模板列表
        List<StoreGoodsTab> storeGoodsTabs = storeGoodsTabRepository.queryTabByStoreId(goods.getStoreId());
        response.setStoreGoodsTabs(storeGoodsTabs);
        if (CollectionUtils.isNotEmpty(storeGoodsTabs)) {
            storeGoodsTabs.sort(Comparator.comparingInt(StoreGoodsTab::getSort));
            List<GoodsTabRela> goodsTabRelas = goodsTabRelaRepository.queryListByTabIds(goodsId, storeGoodsTabs.stream().map(StoreGoodsTab::getTabId).collect(Collectors.toList()));
            response.setGoodsTabRelas(goodsTabRelas);
        }

        //查询商品图片
        List<GoodsImage> collect = goodsImageRepository.findByGoodsId(goods.getGoodsId());
        List<GoodsImage> collect2 = collect.stream().filter(v -> {
            if (Objects.isNull(v.getSort())) {
                return false;
            }
            return true;
        }).sorted(Comparator.comparing(GoodsImage::getSort)).collect(Collectors.toList());
        List<GoodsImage> collect1 = collect.stream().filter(v -> {
            if (Objects.isNull(v.getSort())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        collect2.addAll(collect1);
        response.setImages(collect2);
        //查询商品促销图片和合成图片
        response.setGoodsImageStypeVOS(KsBeanUtil.convert(goodsImagestypeRepository.findByGoodsId(goods.getGoodsId()),GoodsImageStypeVO.class));
        //查询商品合成图片关联关系
        List<RelationGoodsImages> byGoodsId1 = relationGoodsImagesRepository.findByGoodsId(goods.getGoodsId());
        if (CollectionUtils.isNotEmpty(byGoodsId1)){
            response.setRelationGoodsImagesVO(KsBeanUtil.convert(byGoodsId1.get(0),RelationGoodsImagesVO.class));
        }


        //查询SKU列表
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setGoodsId(goods.getGoodsId());
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());

        // 商户库存转供应商库存
       // goodsInfoService.turnProviderStock(goodsInfos);

        List<GoodsLabel> finalLabels = labels;
        goodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            goodsInfo.setPriceType(goods.getPriceType());
            goodsInfo.setBrandId(goods.getBrandId());
            goodsInfo.setCateId(goods.getCateId());
            goodsInfo.setGoodsLabels(finalLabels);
            List<GoodsAttributeKey> query = goodsAttributeKeyService.query(GoodsAtrrKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
            if (CollectionUtils.isNotEmpty(query)){
                goodsInfo.setGoodsAttributeKeys(query);
            }

        });

        //查询sku库存信息
        List<String> goodsInfoIds = goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
//        Map<String, Long> goodsNumsMap = goodsInfoService.getGoodsPileNumBySkuIds(goodsInfoIds);
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIds);
        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(goodsInfoIds);
        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfoIds);
        List<Long> wareIds = goodsWareStocks.stream().map(GoodsWareStock::getWareId).collect(Collectors.toList());
        List<WareHouse> wareHouses = wareHouseRepository.findByWareIdIn(wareIds);
        goodsWareStocks.forEach(goodsWareStock -> {
            wareHouses.forEach(wareHouse -> {
                if (goodsWareStock.getWareId().equals(wareHouse.getWareId())) {
                    goodsWareStock.setWareName(wareHouse.getWareName());
                }
            });
        });
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(matchWareHouseFlag)&&!matchWareHouseFlag){
            List<WareHouse> onlineWareHouses = wareHouseService
                    .list(WareHouseQueryRequest.builder().delFlag(DeleteFlag.NO).wareHouseType(WareHouseType.STORRWAREHOUSE).storeId(goods.getStoreId()).build());
            List<Long> onlineWareIds= onlineWareHouses.stream().map(WareHouse::getWareId).collect(Collectors.toList());
            goodsWareStocks = goodsWareStocks.stream().filter(param -> onlineWareIds.contains(param.getWareId())).collect(Collectors.toList());
        }
        List<GoodsWareStock> finalGoodsWareStocks = goodsWareStocks;
        goodsInfos.forEach(goodsInfo -> {
            List<GoodsWareStock> stockList = finalGoodsWareStocks.stream().
                    filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                            && goodsInfo.getWareId().equals(goodsWareStock.getWareId())).
                    collect(Collectors.toList());
            List<GoodsWareStockVillages> villagesList = goodsWareStockVillagesList.stream()
                    .filter(goodsWareStockVillages -> goodsInfo.getGoodsInfoId().equals(goodsWareStockVillages.getGoodsInfoId()))
                    .collect(Collectors.toList());
           if (CollectionUtils.isNotEmpty(stockList)){
               BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
               if (CollectionUtils.isNotEmpty(villagesList)) {
                   BigDecimal sumVillagesStock = villagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                   if (sumStock.subtract(sumVillagesStock).compareTo(BigDecimal.ZERO) <= 0) {
                       goodsInfo.setStock(BigDecimal.ZERO);
                   } else {
                       goodsInfo.setStock(sumStock.subtract(sumVillagesStock));
                   }
               } else {
                   goodsInfo.setStock(sumStock);
               }
           }else {
               goodsInfo.setStock(BigDecimal.ZERO);
           }
            goodsInfo.setStock(getskusstock.getOrDefault(goodsInfo.getGoodsInfoId(),BigDecimal.ZERO));

            List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.getByInfoId(goodsInfo.getGoodsInfoId());
            if (CollectionUtils.isNotEmpty(devanningGoodsInfos)){
                goodsInfo.setDevanningUnit(devanningGoodsInfos.get(Constants.no).getDevanningUnit());
            }

           //计算商品虚拟库存
//           goodsInfoService.calGoodsInfoStock(goodsInfo,goodsNumsMap);
           goodsInfo.setGoodsWareStocks(stockList);
        });

        //商品属性
        response.setGoodsPropDetailRels(goodsPropDetailRelRepository.queryByGoodsId(goods.getGoodsId()));

        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            response.setGoodsSpecs(goodsSpecRepository.findByGoodsId(goods.getGoodsId()));
            response.setGoodsSpecDetails(goodsSpecDetailRepository.findByGoodsId(goods.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().forEach(goodsSpec -> {
                goodsSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(goodsSpec.getSpecId())).map(GoodsSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            Map<String, List<GoodsInfoSpecDetailRel>> goodsInfoSpecDetailRels = goodsInfoSpecDetailRelRepository.findByGoodsId(goods.getGoodsId()).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId));
            goodsInfos.forEach(goodsInfo -> {
                goodsInfo.setMockSpecIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecId).collect(Collectors.toList()));
                goodsInfo.setMockSpecDetailIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            });
        }

        response.setGoodsInfos(goodsInfos);

        //商品按订货区间，查询订货区间
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
            response.setGoodsIntervalPrices(goodsIntervalPriceRepository.findByGoodsId(goods.getGoodsId()));
        } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(goods.getPriceType())) {
            response.setGoodsLevelPrices(goodsLevelPriceRepository.findByGoodsId(goods.getGoodsId()));
            //如果是按单独客户定价
            if (Constants.yes.equals(goods.getCustomFlag())) {
                response.setGoodsCustomerPrices(goodsCustomerPriceRepository.findByGoodsId(goods.getGoodsId()));
            }
        }

        return response;
    }

    /**
     * 根据积分商品ID查询商品
     *
     * @param pointsGoodsId 积分商品ID
     * @return list
     */
    public GoodsEditResponse findInfoByPointsGoodsId(String pointsGoodsId,Long wareId) throws SbcRuntimeException {
        GoodsEditResponse response = new GoodsEditResponse();
        //查看积分商品信息
        PointsGoods pointsGoods = pointsGoodsRepository.findById(pointsGoodsId).orElse(new PointsGoods());
        Goods goods = goodsRepository.findById(pointsGoods.getGoodsId()).orElse(null);
        if (Objects.isNull(goods)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        if (Objects.nonNull(goods.getFreightTempId())) {
            FreightTemplateGoods goodsTemplate = freightTemplateGoodsRepository.queryById(goods.getFreightTempId());
            if (Objects.nonNull(goodsTemplate)) {
                goods.setFreightTempName(goodsTemplate.getFreightTempName());
            }
        }
        response.setGoods(goods);

        // 获取商家所绑定的模板列表
        List<StoreGoodsTab> storeGoodsTabs = storeGoodsTabRepository.queryTabByStoreId(goods.getStoreId());
        response.setStoreGoodsTabs(storeGoodsTabs);
        if (CollectionUtils.isNotEmpty(storeGoodsTabs)) {
            storeGoodsTabs.sort(Comparator.comparingInt(StoreGoodsTab::getSort));
            List<GoodsTabRela> goodsTabRelas = goodsTabRelaRepository.queryListByTabIds(pointsGoods.getGoodsId(), storeGoodsTabs.stream().map(StoreGoodsTab::getTabId).collect(Collectors.toList()));
            response.setGoodsTabRelas(goodsTabRelas);
        }

        //查询商品图片
        response.setImages(goodsImageRepository.findByGoodsId(goods.getGoodsId()));

        //查询SKU列表
        //验证积分商品(校验积分商品库存，删除，启用停用状态，兑换时间)
        if (Objects.isNull(pointsGoods)
                || Objects.equals(DeleteFlag.YES, pointsGoods.getDelFlag())
                || (!Objects.equals(EnableStatus.ENABLE, pointsGoods.getStatus()))) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        //去积分商品表里查 当前积分商品下其他的可用积分商品（未删除、已启用、在当前兑换时间、库存不为0）
        PointsGoodsQueryRequest queryReq = new PointsGoodsQueryRequest();
        queryReq.setGoodsId(pointsGoods.getGoodsId());
        queryReq.setDelFlag(DeleteFlag.NO);
        queryReq.setStatus(EnableStatus.ENABLE);
        queryReq.setBeginTimeEnd(LocalDateTime.now());
        queryReq.setEndTimeBegin(LocalDateTime.now());
        List<PointsGoods> pointsGoodsList = pointsGoodsRepository.findAll(PointsGoodsWhereCriteriaBuilder.build(queryReq));
        List<String> goodsInfoIds = pointsGoodsList.stream().map(PointsGoods::getGoodsInfoId).collect(Collectors.toList());
        //喜丫丫库存查询中间表
        if (!CollectionUtils.isEmpty(goodsInfoIds)&&Objects.isNull(wareId)){
            List<GoodsWareStock> stock = goodsWareStockService.findByGoodsInfoNoIn(GoodsWareStockByGoodsForNoRequest.builder()
                    .wareId(wareId)
                    .goodsForIdList(goodsInfoIds)
                    .build());
            pointsGoodsList.forEach(param->{
                    Optional<GoodsWareStock> first = stock.stream().filter(inner -> inner.getGoodsInfoId().equals(param.getGoodsInfoId())).findFirst();
                    first.ifPresent(wareStock -> param.setStock(wareStock.getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue()));
            });
        }

        pointsGoodsList = pointsGoodsList.stream().filter(pointsGoodsVO -> pointsGoodsVO.getStock() > 0).collect(Collectors.toList());
        response.setPointsGoodsList(pointsGoodsList);

        //查询积分商品对应的商品sku信息
        List<String> skuIds = pointsGoodsList.stream().map(PointsGoods::getGoodsInfoId).collect(Collectors.toList());
        List<GoodsInfo> goodsInfoList = goodsInfoRepository.findByGoodsInfoIds(skuIds);

        // 商户库存转供应商库存
       // goodsInfoService.turnProviderStock(goodsInfoList);

        //商品属性
        response.setGoodsPropDetailRels(goodsPropDetailRelRepository.queryByGoodsId(goods.getGoodsId()));

        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            response.setGoodsSpecs(goodsSpecRepository.findByGoodsId(goods.getGoodsId()));
            response.setGoodsSpecDetails(goodsSpecDetailRepository.findByGoodsId(goods.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().forEach(goodsSpec -> {
                goodsSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(goodsSpec.getSpecId())).map(GoodsSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            Map<String, List<GoodsInfoSpecDetailRel>> goodsInfoSpecDetailRels = goodsInfoSpecDetailRelRepository.findAllByGoodsId(goods.getGoodsId()).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId));
            goodsInfoList.forEach(goodsInfo -> {
                goodsInfo.setMockSpecIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecId).collect(Collectors.toList()));
                goodsInfo.setMockSpecDetailIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            });
        }

        response.setGoodsInfos(goodsInfoList);

        return response;
    }

    /**
     * 店铺精选页-进入商品详情接口
     *
     * @param goodsId
     * @param skuIds
     * @return
     * @throws SbcRuntimeException
     */
    public GoodsEditResponse findInfoByIdAndSkuIds(String goodsId, List<String> skuIds) throws SbcRuntimeException {
        GoodsEditResponse response = new GoodsEditResponse();
        Goods goods = goodsRepository.findById(goodsId).orElse(null);
        if (Objects.isNull(goods)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        if (Objects.nonNull(goods.getFreightTempId())) {
            FreightTemplateGoods goodsTemplate = freightTemplateGoodsRepository.queryById(goods.getFreightTempId());
            if (Objects.nonNull(goodsTemplate)) {
                goods.setFreightTempName(goodsTemplate.getFreightTempName());
            }
        }
        response.setGoods(goods);

        // 获取商家所绑定的模板列表
        List<StoreGoodsTab> storeGoodsTabs = storeGoodsTabRepository.queryTabByStoreId(goods.getStoreId());
        response.setStoreGoodsTabs(storeGoodsTabs);
        if (CollectionUtils.isNotEmpty(storeGoodsTabs)) {
            storeGoodsTabs.sort(Comparator.comparingInt(StoreGoodsTab::getSort));
            List<GoodsTabRela> goodsTabRelas = goodsTabRelaRepository.queryListByTabIds(goodsId, storeGoodsTabs.stream().map(StoreGoodsTab::getTabId).collect(Collectors.toList()));
            response.setGoodsTabRelas(goodsTabRelas);
        }

        //查询商品图片
        response.setImages(goodsImageRepository.findByGoodsId(goods.getGoodsId()));

        //查询SKU列表
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setGoodsId(goods.getGoodsId());
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
        List<GoodsInfo> goodsInfoList = new ArrayList<>(goodsInfos.size());
        for (GoodsInfo goodsInfo : goodsInfos) {
            if (skuIds.stream().anyMatch(skuId -> goodsInfo.getGoodsInfoId().equals(skuId)) && DistributionGoodsAudit.CHECKED.equals(goodsInfo.getDistributionGoodsAudit())) {
                goodsInfoList.add(goodsInfo);
            }
        }

        // 商户库存转供应商库存
        //goodsInfoService.turnProviderStock(goodsInfoList);

        goodsInfoList.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            goodsInfo.setPriceType(goods.getPriceType());
        });

        //商品属性
        response.setGoodsPropDetailRels(goodsPropDetailRelRepository.queryByGoodsId(goods.getGoodsId()));

        //如果是多规格
        checkMoreSpecFlag(goods, response, goodsInfoList);

        response.setGoodsInfos(goodsInfoList);

        return response;
    }

    private void checkMoreSpecFlag(Goods goods, GoodsEditResponse response, List<GoodsInfo> goodsInfoList) {
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            response.setGoodsSpecs(goodsSpecRepository.findByGoodsId(goods.getGoodsId()));
            response.setGoodsSpecDetails(goodsSpecDetailRepository.findByGoodsId(goods.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().forEach(goodsSpec -> {
                goodsSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(goodsSpec.getSpecId())).map(GoodsSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            Map<String, List<GoodsInfoSpecDetailRel>> goodsInfoSpecDetailRels = goodsInfoSpecDetailRelRepository.findByGoodsId(goods.getGoodsId()).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId));
            goodsInfoList.forEach(goodsInfo -> {
                goodsInfo.setMockSpecIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecId).collect(Collectors.toList()));
                goodsInfo.setMockSpecDetailIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            });
        }
    }

    /**
     * 商品新增
     *
     * @param saveRequest
     * @return SPU编号
     * @throws SbcRuntimeException
     */
    @Transactional
    public String add(GoodsSaveRequest saveRequest) throws SbcRuntimeException {
        List<GoodsImage> goodsImages = saveRequest.getImages();
        List<GoodsInfo> goodsInfos = saveRequest.getGoodsInfos();
        Goods goods = saveRequest.getGoods();

        //验证SPU编码重复
        GoodsQueryRequest queryRequest = new GoodsQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsNo(goods.getGoodsNo());
        if (goodsRepository.count(queryRequest.getWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsErrorCode.SPU_NO_EXIST);
        }

        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsInfoNos(goodsInfos.stream().map(GoodsInfo::getGoodsInfoNo).distinct().collect(Collectors.toList()));

        //如果SKU数据有重复
        if (goodsInfos.size() != infoQueryRequest.getGoodsInfoNos().size()) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_EXIST);
        }

        //验证SKU编码重复
        List<GoodsInfo> goodsInfosList = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
        if (CollectionUtils.isNotEmpty(goodsInfosList)) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_H_EXIST, new Object[]{StringUtils.join(goodsInfosList.stream().map(GoodsInfo::getGoodsInfoNo).collect(Collectors.toList()), ",")});
        }

        //验证商品相关基础数据
        this.checkBasic(goods);

        goods.setDelFlag(DeleteFlag.NO);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(goods.getCreateTime());
        goods.setAddedTime(goods.getCreateTime());
        goodsCommonService.setCheckState(goods);

        if (Objects.isNull(goods.getPriceType())) {
            goods.setPriceType(GoodsPriceType.MARKET.toValue());
        }
        if (goods.getAddedFlag() == null) {
            goods.setAddedFlag(AddedFlag.YES.toValue());
        }
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            goods.setGoodsImg(goodsImages.get(0).getArtworkUrl());
        }
        if (goods.getMoreSpecFlag() == null) {
            goods.setMoreSpecFlag(Constants.no);
        }
        if (goods.getCustomFlag() == null) {
            goods.setCustomFlag(Constants.no);
        }
        if (goods.getGoodsViewNum() == null) {
            goods.setGoodsViewNum(Long.valueOf(Constants.no));
        }
        if (goods.getProviderId() == null) {
            goods.setProviderId(Long.valueOf(Constants.no));
        }
        if (goods.getProviderGoodsId() == null) {
            goods.setProviderGoodsId("");
        }
        if (goods.getLevelDiscountFlag() == null) {
            goods.setLevelDiscountFlag(Constants.no);
        }
        if (goods.getGoodsCollectNum() == null) {
            goods.setGoodsCollectNum(0L);
        }
        if (goods.getGoodsSalesNum() == null) {
            goods.setGoodsSalesNum(0L);
        }
        if (goods.getGoodsEvaluateNum() == null) {
            goods.setGoodsEvaluateNum(0L);
        }
        if (goods.getGoodsFavorableCommentNum() == null) {
            goods.setGoodsFavorableCommentNum(0L);
        }

        final String goodsId = goodsRepository.save(goods).getGoodsId();

        //新增图片
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            goodsImages.forEach(goodsImage -> {
                goodsImage.setCreateTime(goods.getCreateTime());
                goodsImage.setUpdateTime(goods.getUpdateTime());
                goodsImage.setGoodsId(goodsId);
                goodsImage.setDelFlag(DeleteFlag.NO);
                goodsImage.setImageId(goodsImageRepository.save(goodsImage).getImageId());
            });
        }

        if (osUtil.isS2b() && CollectionUtils.isNotEmpty(goods.getStoreCateIds())) {
            goods.getStoreCateIds().forEach(cateId -> {
                StoreCateGoodsRela rela = new StoreCateGoodsRela();
                rela.setGoodsId(goodsId);
                rela.setStoreCateId(cateId);
                storeCateGoodsRelaRepository.save(rela);
            });
        }

        //保存商品属性
        List<GoodsPropDetailRel> goodsPropDetailRels = saveRequest.getGoodsPropDetailRels();
        if (CollectionUtils.isNotEmpty(goodsPropDetailRels)) {

            //如果是修改则设置修改时间，如果是新增则设置创建时间，
            goodsPropDetailRels.forEach(goodsPropDetailRel -> {
                goodsPropDetailRel.setDelFlag(DeleteFlag.NO);
                goodsPropDetailRel.setCreateTime(LocalDateTime.now());
                goodsPropDetailRel.setGoodsId(goodsId);
            });

            goodsPropDetailRelRepository.saveAll(goodsPropDetailRels);
        }

        List<GoodsSpec> specs = saveRequest.getGoodsSpecs();
        List<GoodsSpecDetail> specDetails = saveRequest.getGoodsSpecDetails();

        List<GoodsInfoSpecDetailRel> specDetailRels = new ArrayList<>();
        List<GoodsTabRela> goodsTabRelas = saveRequest.getGoodsTabRelas();
        if (CollectionUtils.isNotEmpty(goodsTabRelas)) {
            goodsTabRelas.forEach(info -> {
                info.setGoodsId(goodsId);
                goodsTabRelaRepository.save(info);
            });
        }
        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            /**
             * 填放可用SKU相应的规格\规格值
             * （主要解决SKU可以前端删除，但相应规格值或规格仍然出现的情况）
             */
            Map<Long, Integer> isSpecEnable = new HashMap<>();
            Map<Long, Integer> isSpecDetailEnable = new HashMap<>();
            goodsInfos.forEach(goodsInfo -> {
                goodsInfo.getMockSpecIds().forEach(specId -> {
                    isSpecEnable.put(specId, Constants.yes);
                });
                goodsInfo.getMockSpecDetailIds().forEach(detailId -> {
                    isSpecDetailEnable.put(detailId, Constants.yes);
                });
            });

            //新增规格
            specs.stream()
                    .filter(goodsSpec -> Constants.yes.equals(isSpecEnable.get(goodsSpec.getMockSpecId()))) //如果SKU有这个规格
                    .forEach(goodsSpec -> {
                        goodsSpec.setCreateTime(goods.getCreateTime());
                        goodsSpec.setUpdateTime(goods.getUpdateTime());
                        goodsSpec.setGoodsId(goodsId);
                        goodsSpec.setDelFlag(DeleteFlag.NO);
                        goodsSpec.setSpecId(goodsSpecRepository.save(goodsSpec).getSpecId());
                    });
            //新增规格值
            specDetails.stream()
                    .filter(goodsSpecDetail -> Constants.yes.equals(isSpecDetailEnable.get(goodsSpecDetail.getMockSpecDetailId()))) //如果SKU有这个规格值
                    .forEach(goodsSpecDetail -> {
                        Optional<GoodsSpec> specOpt = specs.stream().filter(goodsSpec -> goodsSpec.getMockSpecId().equals(goodsSpecDetail.getMockSpecId())).findFirst();
                        if (specOpt.isPresent()) {
                            goodsSpecDetail.setCreateTime(goods.getCreateTime());
                            goodsSpecDetail.setUpdateTime(goods.getUpdateTime());
                            goodsSpecDetail.setGoodsId(goodsId);
                            goodsSpecDetail.setDelFlag(DeleteFlag.NO);
                            goodsSpecDetail.setSpecId(specOpt.get().getSpecId());
                            goodsSpecDetail.setSpecDetailId(goodsSpecDetailRepository.save(goodsSpecDetail).getSpecDetailId());
                        }
                    });
        }

        for (GoodsInfo sku : goodsInfos) {
            if (sku.getStock() == null) {
                sku.setStock(BigDecimal.ZERO);
            }
            sku.setCateId(goods.getCateId());
            sku.setBrandId(goods.getBrandId());
            sku.setGoodsId(goodsId);
            sku.setGoodsInfoName(goods.getGoodsName());
            if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(goods.getPriceType())
                    && goods.getMarketPrice() != null) {
                // XXX 后面商家boss新增商品页优化后，可以删除这部分逻辑
                sku.setMarketPrice(goods.getMarketPrice());
            }
            sku.setCostPrice(goods.getCostPrice());
            sku.setCreateTime(goods.getCreateTime());
            sku.setUpdateTime(goods.getUpdateTime());
            sku.setAddedTime(goods.getAddedTime());
            sku.setDelFlag(goods.getDelFlag());
            if (!goods.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                sku.setAddedFlag(goods.getAddedFlag());
            }
            sku.setCompanyInfoId(goods.getCompanyInfoId());
            sku.setPriceType(goods.getPriceType());
            sku.setLevelDiscountFlag(goods.getLevelDiscountFlag());
            sku.setCustomFlag(goods.getCustomFlag());
            sku.setStoreId(goods.getStoreId());
            sku.setAuditStatus(goods.getAuditStatus());
            sku.setCompanyType(goods.getCompanyType());
            sku.setAloneFlag(Boolean.FALSE);
            sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
            sku.setSaleType(goods.getSaleType());
            sku.setGoodsSource(goods.getGoodsSource());
            sku.setProviderId(goods.getProviderId());
            sku.setProviderGoodsInfoId(goods.getProviderGoodsId());
            //新增商品询价标志，默认否；add by jiangxin 20210903
            if (sku.getInquiryFlag()==null){
                sku.setInquiryFlag(Constants.no);
            }
            String goodsInfoId = goodsInfoRepository.save(sku).getGoodsInfoId();
            sku.setGoodsInfoId(goodsInfoId);

            //如果是多规格,新增SKU与规格明细值的关联表
            if (Constants.yes.equals(goods.getMoreSpecFlag())) {
                if (CollectionUtils.isNotEmpty(specs)) {
                    for (GoodsSpec spec : specs) {
                        if (sku.getMockSpecIds().contains(spec.getMockSpecId())) {
                            for (GoodsSpecDetail detail : specDetails) {
                                if (spec.getMockSpecId().equals(detail.getMockSpecId()) && sku.getMockSpecDetailIds().contains(detail.getMockSpecDetailId())) {
                                    GoodsInfoSpecDetailRel detailRel = new GoodsInfoSpecDetailRel();
                                    detailRel.setGoodsId(goodsId);
                                    detailRel.setGoodsInfoId(goodsInfoId);
                                    detailRel.setSpecId(spec.getSpecId());
                                    detailRel.setSpecDetailId(detail.getSpecDetailId());
                                    detailRel.setDetailName(detail.getDetailName());
                                    detailRel.setCreateTime(detail.getCreateTime());
                                    detailRel.setUpdateTime(detail.getUpdateTime());
                                    detailRel.setDelFlag(detail.getDelFlag());
                                    detailRel.setSpecName(spec.getSpecName());
                                    specDetailRels.add(goodsInfoSpecDetailRelRepository.save(detailRel));
                                }
                            }
                        }
                    }
                }
            }
        }
        return goodsId;
    }


    /**
     * 新增特价商品
     *
     * @return SPU编号
     * @throws SbcRuntimeException
     */
    @Transactional
    public String addSpecial(GoodsSpecialRequest specialRequest) throws SbcRuntimeException {
        //普通的商品和货品
        GoodsInfo goodsInfoSource = goodsInfoRepository.findByGoodsInfoNo(specialRequest.getErpGoodsInfoNo());
        if(Objects.isNull(goodsInfoSource)){
            return "";
        }
        Goods goodsSource = goodsRepository.getOne(goodsInfoSource.getGoodsId());
        List<StoreCateGoodsRela> storeCateGoodsRelas = storeCateGoodsRelaRepository.selectByGoodsId(Arrays.asList(goodsSource.getGoodsId()));
        Goods goodsSave = KsBeanUtil.convert(goodsSource,Goods.class);
        String sourceGoodsId = goodsSave.getGoodsId();
        List<SpecialGoodsAddDTO> specialGoodsAddDTOS = specialRequest.getSpecialGoodsList();
        // 1.新增一个同样的商品
        String goodsNo = 'P' + String.valueOf(System.currentTimeMillis()).substring(4, 10) +
                String.valueOf(Math.random()).substring(2, 5);
        goodsSave.setGoodsNo(goodsNo);
        goodsSave.setGoodsId(null);
        goodsSave.setMoreSpecFlag(1);
        //验证SPU编码重复
        GoodsQueryRequest queryRequest = new GoodsQueryRequest();
        queryRequest.setGoodsNo(goodsSave.getGoodsNo());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        if (goodsRepository.count(queryRequest.getWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsErrorCode.SPU_NO_EXIST);
        }
        List<GoodsInfo> goodsInfos = new ArrayList<>(specialGoodsAddDTOS.size());
        specialGoodsAddDTOS.stream().forEach(g->{
            String goodsInfoNo =  '8' + String.valueOf(System.currentTimeMillis()).substring(4, 10)
                    + String.valueOf(Math.random()).substring(2, 5);
            GoodsInfo goodsInfo = KsBeanUtil.convert(goodsInfoSource,GoodsInfo.class);
            goodsInfo.setGoodsInfoNo(goodsInfoNo);
            goodsInfos.add(goodsInfo);
        });

        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsInfoNos(goodsInfos.stream().map(GoodsInfo::getGoodsInfoNo).distinct().collect(Collectors.toList()));
        //如果SKU数据有重复
        if (goodsInfos.size() != infoQueryRequest.getGoodsInfoNos().size()) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_EXIST);
        }
        //验证SKU编码重复
        List<GoodsInfo> goodsInfosList = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
        if (CollectionUtils.isNotEmpty(goodsInfosList)) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_H_EXIST, new Object[]{StringUtils.join(goodsInfosList.stream().map(GoodsInfo::getGoodsInfoNo).collect(Collectors.toList()), ",")});
        }

        //验证商品相关基础数据
//        this.checkBasic(goodsSave);
        goodsSave.setGoodsType(2);
        final String goodsId = goodsRepository.save(goodsSave).getGoodsId();
        //新增图片
        List<GoodsImage> goodsImages = goodsImageRepository.findByGoodsId(sourceGoodsId);
        if(CollectionUtils.isNotEmpty(goodsImages)){
            List<GoodsImage> goodsImageList = KsBeanUtil.convertList(goodsImages,GoodsImage.class);
            goodsImageList.stream().forEach(g->{
                g.setGoodsId(goodsId);
                g.setImageId(null);
            });
            goodsImageRepository.saveAll(goodsImages);
        }

        if (osUtil.isS2b() && CollectionUtils.isNotEmpty(goodsSave.getStoreCateIds())) {
            goodsSave.getStoreCateIds().forEach(cateId -> {
                StoreCateGoodsRela rela = new StoreCateGoodsRela();
                rela.setStoreCateId(cateId);
                rela.setGoodsId(goodsId);
                storeCateGoodsRelaRepository.save(rela);
            });
        }

        //保存商品属性
        List<GoodsPropDetailRel> goodsPropDetailRels = goodsPropDetailRelRepository.queryByGoodsId(sourceGoodsId);
        if (CollectionUtils.isNotEmpty(goodsPropDetailRels)) {
            //如果是修改则设置修改时间，如果是新增则设置创建时间，
            goodsPropDetailRels.forEach(goodsPropDetailRel -> {
                goodsPropDetailRel.setCreateTime(LocalDateTime.now());
                goodsPropDetailRel.setDelFlag(DeleteFlag.NO);
                goodsPropDetailRel.setGoodsId(goodsId);
            });
            goodsPropDetailRelRepository.saveAll(goodsPropDetailRels);
        }
        //保存商品模板
        List<GoodsInfoSpecDetailRel> specDetailRels = new ArrayList<>();
        List<GoodsTabRela> goodsTabRelas =  goodsTabRelaRepository.queryListByGoodsId(sourceGoodsId);
        if (CollectionUtils.isNotEmpty(goodsTabRelas)) {
            goodsTabRelas.forEach(info -> {
                info.setGoodsId(goodsId);
                goodsTabRelaRepository.save(info);
            });
        }
        //保存spu的门店分类
        List<StoreCateGoodsRela> storeCateGoodsRelasAdd = new ArrayList<>();
        storeCateGoodsRelas.stream().forEach(s->{
            StoreCateGoodsRela storeCateGoodsRela = KsBeanUtil.copyPropertiesThird(s,StoreCateGoodsRela.class);
            storeCateGoodsRela.setGoodsId(goodsId);
            storeCateGoodsRelasAdd.add(storeCateGoodsRela);
        });
        storeCateGoodsRelaRepository.saveAll(storeCateGoodsRelasAdd);
        List<GoodsSpecDetail> specDetails = new ArrayList<>();
        GoodsSpec spec = new GoodsSpec();
        //如果是多规格
        if (Constants.yes.equals(goodsSave.getMoreSpecFlag())) {
            //新增规格
            spec.setCreateTime(goodsSave.getCreateTime());
            spec.setUpdateTime(goodsSave.getUpdateTime());
            spec.setDelFlag(DeleteFlag.NO);
            spec.setGoodsId(goodsId);
            spec.setSpecName("生产日期");
            spec.setSpecId(goodsSpecRepository.save(spec).getSpecId());
            //新增规格值
            specialGoodsAddDTOS.stream().forEach(specialGoodsAddDTO ->{
                GoodsSpecDetail goodsSpecDetail = new GoodsSpecDetail();
                goodsSpecDetail.setCreateTime(goodsSave.getCreateTime());
                goodsSpecDetail.setUpdateTime(goodsSave.getUpdateTime());
                goodsSpecDetail.setDelFlag(DeleteFlag.NO);
                goodsSpecDetail.setGoodsId(goodsId);
                goodsSpecDetail.setSpecId(spec.getSpecId());
                goodsSpecDetail.setDetailName(specialGoodsAddDTO.getGoodsInfoBatchNo());
                goodsSpecDetail.setSpecDetailId(goodsSpecDetailRepository.save(goodsSpecDetail).getSpecDetailId());
                specDetails.add(goodsSpecDetail);
            });
        }

        //sku处理
        int index = 0;
        for (GoodsInfo sku : goodsInfos) {
            if (sku.getStock() == null) {
                sku.setStock(BigDecimal.ZERO);
            }
            sku.setDelFlag(goodsSave.getDelFlag());
            if (!goodsSave.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                sku.setAddedFlag(goodsSave.getAddedFlag());
            }
            sku.setGoodsInfoType(1);
            sku.setGoodsInfoId(null);
            sku.setGoodsId(goodsId);
            sku.setErpGoodsInfoNo(specialRequest.getErpGoodsInfoNo());
            sku.setGoodsInfoBatchNo(specialGoodsAddDTOS.get(index).getGoodsInfoBatchNo());
            String goodsInfoId = goodsInfoRepository.save(sku).getGoodsInfoId();
            ++index;
            sku.setGoodsInfoId(goodsInfoId);
            //如果是多规格,新增SKU与规格明细值的关联表
            if (Constants.yes.equals(goodsSave.getMoreSpecFlag())) {
                for (GoodsSpecDetail detail : specDetails) {
                    if(detail.getDetailName().equals(sku.getGoodsInfoBatchNo())){
                        GoodsInfoSpecDetailRel detailRel = new GoodsInfoSpecDetailRel();
                        detailRel.setGoodsId(goodsId);
                        detailRel.setGoodsInfoId(goodsInfoId);
                        detailRel.setSpecId(spec.getSpecId());
                        detailRel.setDetailName(detail.getDetailName());
                        detailRel.setSpecDetailId(detail.getSpecDetailId());
                        detailRel.setCreateTime(detail.getCreateTime());
                        detailRel.setUpdateTime(detail.getUpdateTime());
                        detailRel.setSpecName(spec.getSpecName());
                        detailRel.setDelFlag(detail.getDelFlag());
                        specDetailRels.add(goodsInfoSpecDetailRelRepository.save(detailRel));
                    }
                }
            }
        }
        this.addGoodsStock(goodsInfos,specialRequest);
        return goodsId;
    }


    @Transactional
    public String modifySpecialGoods(GoodsSpecialRequest request) throws SbcRuntimeException{
        List<SpecialGoodsAddDTO> specialGoodsList = request.getSpecialGoodsList();
        //1.查询特价商品
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findSpecialGoodsByErpNo(request.getErpGoodsInfoNo());
        if(CollectionUtils.isEmpty(goodsInfos)){
            log.info("erp编号：{} 的特价商品不存在！！",request.getErpGoodsInfoNo());
            //新增该商品
            return this.addSpecial(request);
        }
        if(request.getSpecialGoodsList().size() == goodsInfos.size()){
            Goods goods = goodsInfos.get(0).getGoods();
            //2.对比更新
            //2.1 修改规格值
            List<GoodsSpecDetail> goodsSpecDetails = goodsSpecDetailRepository.findByGoodsId(goods.getGoodsId());
            goodsSpecDetails.stream().forEach(g->{
                int index = 0;
                g.setDetailName(specialGoodsList.get(index).getGoodsInfoBatchNo());
                ++index;
            });
            goodsSpecDetailRepository.saveAll(goodsSpecDetails);
            //2.2 修改sku的批次号
            goodsInfos.stream().forEach(g->{
                int index = 0;
                g.setGoodsInfoBatchNo(specialGoodsList.get(index).getGoodsInfoBatchNo());
                if(Objects.nonNull(specialGoodsList.get(index))
                        && Objects.nonNull(specialGoodsList.get(index).getStock())
                        && specialGoodsList.get(index).getStock() > 0){
                    g.setAddedFlag(1);
                }else{
                    g.setAddedFlag(0);
                }
                ++index;
            });
            goodsInfoRepository.saveAll(goodsInfos);
        }else{
            //3.删除该商品的所有信息，从新添加该商品
            Goods goods = goodsInfos.get(0).getGoods();
            this.delete(Arrays.asList(goods.getGoodsId()));
            return this.addSpecial(request);
        }
        this.modifySpecialGoodsStock(goodsInfos,request);
        return goodsInfos.get(0).getGoodsId();
    }



    /**
     * spu新增
     *
     * @param specialGoodsSaveRequest
     * @return SPU编号
     * @throws SbcRuntimeException
     */
    @Transactional
    public List<Goods> addSpu(SpecialGoodsSaveRequest specialGoodsSaveRequest) throws SbcRuntimeException {
        List<Goods> goodsList = new ArrayList<>();
        specialGoodsSaveRequest.getGoodsVOs().stream().forEach(goodsVO -> {
            goodsRepository.save(KsBeanUtil.copyPropertiesThird(goodsVO,Goods.class));
            goodsList.add(KsBeanUtil.copyPropertiesThird(goodsVO,Goods.class));
        });
        return goodsList;
    }



    /**
     * 商品更新
     *
     * @param saveRequest
     * @throws SbcRuntimeException
     */
    @Transactional
    public Map<String, Object> edit(GoodsSaveRequest saveRequest) throws SbcRuntimeException {
        AtomicReference<Long> checkImageId = new AtomicReference<>(0L);
        Goods newGoods = saveRequest.getGoods();

        Goods oldGoods = goodsRepository.findById(newGoods.getGoodsId()).orElse(null);
        if (oldGoods == null || oldGoods.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        //如果S2B模式下，商品已审核无法编辑分类
        /*if (osUtil.isS2b() && CheckStatus.CHECKED.toValue() == oldGoods.getAuditStatus().toValue() && (!oldGoods.getCateId().equals(newGoods.getCateId()))) {
            throw new SbcRuntimeException(GoodsErrorCode.EDIT_GOODS_CATE);
        }*/

        //验证SPU编码重复
        GoodsQueryRequest queryRequest = new GoodsQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsNo(newGoods.getGoodsNo());
        queryRequest.setNotGoodsId(newGoods.getGoodsId());
        if (goodsRepository.count(queryRequest.getWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsErrorCode.SPU_NO_EXIST);
        }

        //更新关联的商品标签数据
        goodsLabelRelaRepository.deleteByGoodsId(oldGoods.getGoodsId());
        if (StringUtils.isNotBlank(newGoods.getLabelIdStr())) {
            List<GoodsLabelRela> goodsLabelRelasList = Arrays.asList(newGoods.getLabelIdStr().split(",")).stream().map(labelId -> {
                GoodsLabelRela goodsLabelRela = new GoodsLabelRela();
                goodsLabelRela.setGoodsId(oldGoods.getGoodsId());
                goodsLabelRela.setLabelId(Long.parseLong(labelId));
                return goodsLabelRela;
            }).collect(Collectors.toList());
            goodsLabelRelaRepository.saveAll(goodsLabelRelasList);
        }


        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsInfoNos(saveRequest.getGoodsInfos().stream().map(GoodsInfo::getGoodsInfoNo).distinct().collect(Collectors.toList()));
        infoQueryRequest.setNotGoodsId(newGoods.getGoodsId());
        //如果SKU数据有重复
        if (saveRequest.getGoodsInfos().size() != infoQueryRequest.getGoodsInfoNos().size()) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_EXIST);
        }

        //验证SKU编码重复
        List<GoodsInfo> goodsInfosList = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
        if (CollectionUtils.isNotEmpty(goodsInfosList)) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_H_EXIST, new Object[]{StringUtils.join(goodsInfosList.stream().map(GoodsInfo::getGoodsInfoNo).collect(Collectors.toList()), ",")});
        }

        if (Objects.isNull(newGoods.getPriceType())) {
            newGoods.setPriceType(GoodsPriceType.MARKET.toValue());
        }

        //验证商品相关基础数据
        newGoods.setStoreId(oldGoods.getStoreId());
        this.checkBasic(newGoods);


        List<GoodsImage> goodsImages = saveRequest.getImages();
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            newGoods.setGoodsImg(goodsImages.get(0).getArtworkUrl());
        } else {
            newGoods.setGoodsImg(null);
        }
        if (newGoods.getMoreSpecFlag() == null) {
            newGoods.setMoreSpecFlag(Constants.no);
        }
        if (newGoods.getCustomFlag() == null) {
            newGoods.setCustomFlag(Constants.no);
        }
        if (newGoods.getLevelDiscountFlag() == null) {
            newGoods.setLevelDiscountFlag(Constants.no);
        }

        LocalDateTime currDate = LocalDateTime.now();
        //更新商品
        newGoods.setUpdateTime(currDate);

        //上下架状态是否发生变化
        boolean isChgAddedTime = false;
        if (!oldGoods.getAddedFlag().equals(newGoods.getAddedFlag())) {
            isChgAddedTime = true;
        }

        //更新上下架时间
        if (isChgAddedTime) {
            newGoods.setAddedTime(newGoods.getUpdateTime());
        } else {
            newGoods.setAddedTime(oldGoods.getAddedTime());
        }

        //设价类型是否发生变化 -> 影响sku的独立设价状态为false
        boolean isChgPriceType = false;
        if (!newGoods.getPriceType().equals(oldGoods.getPriceType())) {
            isChgPriceType = true;
        }

        //如果设价方式变化为非按客户设价，则将spu市场价清空
        if (isChgPriceType
                && !Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(newGoods.getPriceType())) {
            newGoods.setMarketPrice(null);
            newGoods.setCustomFlag(Constants.no);
        }
        if (SaleType.RETAIL.toValue() == newGoods.getSaleType() && GoodsPriceType.STOCK.toValue() == newGoods.getPriceType()) {
            newGoods.setPriceType(GoodsPriceType.MARKET.toValue());
        }

        KsBeanUtil.copyProperties(newGoods, oldGoods);
        goodsCommonService.setCheckState(oldGoods);

        //新增逻辑
        boolean flag=false;
        if (CollectionUtils.isNotEmpty(saveRequest.getAddGoodsInfos())){
            //重复复制
            flag = merchantGoodsInfoAdd(saveRequest.getAddGoodsInfos(),oldGoods);
        }


        goodsRepository.save(oldGoods);

        //更新图片
        List<GoodsImage> oldImages = goodsImageRepository.findByGoodsId(newGoods.getGoodsId());
        if (CollectionUtils.isNotEmpty(oldImages)) {
            for (GoodsImage oldImage : oldImages) {
                if (CollectionUtils.isNotEmpty(goodsImages)) {
                    Optional<GoodsImage> imageOpt = goodsImages.stream().filter(goodsImage -> oldImage.getImageId().equals(goodsImage.getImageId())).findFirst();
                    //如果图片存在，更新
                    if (imageOpt.isPresent()) {
                        KsBeanUtil.copyProperties(imageOpt.get(), oldImage);
                    } else {
                        oldImage.setDelFlag(DeleteFlag.YES);
                    }
                } else {
                    oldImage.setDelFlag(DeleteFlag.YES);
                }
                oldImage.setUpdateTime(currDate);
                goodsImageRepository.saveAll(oldImages);
            }
        }
        //赋值
        Map<Long, GoodsImage> collect1 = oldImages.stream().collect(Collectors.toMap(GoodsImage::getImageId, Function.identity(), (a, b) -> a));
        //新增图片
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            goodsImages.forEach(goodsImage->{
                if (Objects.isNull(goodsImage.getImageId())){
                    if (goodsImage.getCheckFlag()==1){
                        //选中图片为新增图片
                        //            	// 修改主图时 即时生成水印图
//            	goodsImage.setWatermarkUrl(goodsImageService.watermark(goodsImage.getArtworkUrl()));
                        goodsImage.setCreateTime(currDate);
                        goodsImage.setUpdateTime(currDate);
                        goodsImage.setGoodsId(newGoods.getGoodsId());
                        goodsImage.setDelFlag(DeleteFlag.NO);
                        GoodsImage save = goodsImageRepository.save(goodsImage);
                        checkImageId.set(save.getImageId());
                        goodsImage.setImageId(save.getImageId());

                    }
                    goodsImage.setCreateTime(currDate);
                    goodsImage.setUpdateTime(currDate);
                    goodsImage.setGoodsId(newGoods.getGoodsId());
                    goodsImage.setDelFlag(DeleteFlag.NO);
                    GoodsImage save = goodsImageRepository.save(goodsImage);
                    goodsImage.setImageId(save.getImageId());
                }
                else {
                    //如果有id和数据数据做对比赋值
                    GoodsImage goodsImage1 = collect1.get(goodsImage.getImageId());
                    if (Objects.nonNull(goodsImage1)){
                        goodsImage.setCreateTime(goodsImage1.getCreateTime());
                        goodsImage.setUpdateTime(goodsImage1.getUpdateTime());
                        goodsImage.setGoodsId(goodsImage1.getGoodsId());
                        goodsImage.setDelFlag(goodsImage1.getDelFlag());
                        goodsImage.setImageId(goodsImage1.getImageId());
                    }
                }

            });

            //更新排序
            for (int i =0 ;i<goodsImages.size();i++){
                GoodsImage goodsImage = goodsImages.get(i);
                goodsImage.setSort(i);
                goodsImageRepository.save(goodsImage);
            }

        }
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            if (checkImageId.get()==0L){
                //说明选中图片是原本
                List<GoodsImage> collect = goodsImages.stream().filter(v -> {
                    if (v.getCheckFlag() == 1) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                GoodsImage goodsImage = collect.stream().findFirst().orElse(null);
                if (Objects.isNull(goodsImage)){
                    checkImageId.set(null);
                }else {
                    checkImageId.set(goodsImage.getImageId());
                }

            }
        }
        //保存商品属性
        List<GoodsPropDetailRel> goodsPropDetailRels = saveRequest.getGoodsPropDetailRels();
        if (CollectionUtils.isNotEmpty(goodsPropDetailRels)) {
            //修改设置修改时间
            goodsPropDetailRels.forEach(goodsPropDetailRel -> {
                goodsPropDetailRel.setDelFlag(DeleteFlag.NO);
                if (goodsPropDetailRel.getRelId() != null) {
                    goodsPropDetailRel.setUpdateTime(LocalDateTime.now());
                }
            });
            //  先获取商品下所有的属性id，与前端传来的对比，id存在的做更新操作反之做保存操作
            List<GoodsPropDetailRel> oldPropList = goodsPropDetailRelRepository.queryByGoodsId(newGoods.getGoodsId());
            List<GoodsPropDetailRel> insertList = new ArrayList<>();
            if (oldPropList.isEmpty()) {
                goodsPropDetailRelRepository.saveAll(goodsPropDetailRels);
            } else {
                oldPropList.forEach(value -> {
                    goodsPropDetailRels.forEach(goodsProp -> {
                        if (value.getPropId().equals(goodsProp.getPropId())) {
                            goodsPropDetailRelRepository.updateByGoodsIdAndPropId(goodsProp.getDetailId(), goodsProp.getGoodsId(), goodsProp.getPropId());
                        } else {
                            goodsProp.setCreateTime(LocalDateTime.now());
                            insertList.add(goodsProp);
                        }
                    });
                });
                goodsPropDetailRelRepository.saveAll(insertList);
            }
        }
        //店铺分类
        if (osUtil.isS2b() && CollectionUtils.isNotEmpty(newGoods.getStoreCateIds())) {
            storeCateGoodsRelaRepository.deleteByGoodsId(newGoods.getGoodsId());
            newGoods.getStoreCateIds().forEach(cateId -> {
                StoreCateGoodsRela rela = new StoreCateGoodsRela();
                rela.setGoodsId(newGoods.getGoodsId());
                rela.setStoreCateId(cateId);
                storeCateGoodsRelaRepository.save(rela);
            });
        }

        List<GoodsSpec> specs = saveRequest.getGoodsSpecs();
        List<GoodsSpecDetail> specDetails = saveRequest.getGoodsSpecDetails();
        List<GoodsTabRela> goodsTabRelas = saveRequest.getGoodsTabRelas();
        if (CollectionUtils.isNotEmpty(goodsTabRelas)) {
            goodsTabRelas.forEach(info -> {
                goodsTabRelaRepository.save(info);
            });
        }

        //如果是多规格
        if (Constants.yes.equals(newGoods.getMoreSpecFlag())) {

            /**
             * 填放可用SKU相应的规格\规格值
             * （主要解决SKU可以前端删除，但相应规格值或规格仍然出现的情况）
             */
            Map<Long, Integer> isSpecEnable = new HashMap<>();
            Map<Long, Integer> isSpecDetailEnable = new HashMap<>();
            saveRequest.getGoodsInfos().forEach(goodsInfo -> {
                goodsInfo.getMockSpecIds().forEach(specId -> {
                    isSpecEnable.put(specId, Constants.yes);
                });
                goodsInfo.getMockSpecDetailIds().forEach(detailId -> {
                    isSpecDetailEnable.put(detailId, Constants.yes);
                });
            });

            if (Constants.yes.equals(oldGoods.getMoreSpecFlag())) {
                //更新规格
                List<GoodsSpec> goodsSpecs = goodsSpecRepository.findByGoodsId(oldGoods.getGoodsId());
                if (CollectionUtils.isNotEmpty(goodsSpecs)) {
                    for (GoodsSpec oldSpec : goodsSpecs) {
                        if (CollectionUtils.isNotEmpty(specs)) {
                            Optional<GoodsSpec> specOpt = specs.stream().filter(spec -> oldSpec.getSpecId().equals(spec.getSpecId())).findFirst();
                            //如果规格存在且SKU有这个规格，更新
                            if (specOpt.isPresent() && Constants.yes.equals(isSpecEnable.get(specOpt.get().getMockSpecId()))) {
                                KsBeanUtil.copyProperties(specOpt.get(), oldSpec);
                            } else {
                                oldSpec.setDelFlag(DeleteFlag.YES);
                            }
                        } else {
                            oldSpec.setDelFlag(DeleteFlag.YES);
                        }
                        oldSpec.setUpdateTime(currDate);
                        goodsSpecRepository.save(oldSpec);
                    }
                }

                //更新规格值
                List<GoodsSpecDetail> goodsSpecDetails = goodsSpecDetailRepository.findByGoodsId(oldGoods.getGoodsId());
                if (CollectionUtils.isNotEmpty(goodsSpecDetails)) {

                    for (GoodsSpecDetail oldSpecDetail : goodsSpecDetails) {
                        if (CollectionUtils.isNotEmpty(specDetails)) {
                            Optional<GoodsSpecDetail> specDetailOpt = specDetails.stream().filter(specDetail -> oldSpecDetail.getSpecDetailId().equals(specDetail.getSpecDetailId())).findFirst();
                            //如果规格值存在且SKU有这个规格值，更新
                            if (specDetailOpt.isPresent() && Constants.yes.equals(isSpecDetailEnable.get(specDetailOpt.get().getMockSpecDetailId()))) {
                                KsBeanUtil.copyProperties(specDetailOpt.get(), oldSpecDetail);

                                //更新SKU规格值表的名称备注
                                goodsInfoSpecDetailRelRepository.updateNameBySpecDetail(specDetailOpt.get().getDetailName(), oldSpecDetail.getSpecDetailId(), oldGoods.getGoodsId());
                            } else {
                                oldSpecDetail.setDelFlag(DeleteFlag.YES);
                            }
                        } else {
                            oldSpecDetail.setDelFlag(DeleteFlag.YES);
                        }
                        oldSpecDetail.setUpdateTime(currDate);
                        goodsSpecDetailRepository.save(oldSpecDetail);
                    }
                }
            }

            //新增规格
            if (CollectionUtils.isNotEmpty(specs)) {
                specs.stream().filter(goodsSpec -> goodsSpec.getSpecId() == null && Constants.yes.equals(isSpecEnable.get(goodsSpec.getMockSpecId()))).forEach(goodsSpec -> {
                    goodsSpec.setCreateTime(currDate);
                    goodsSpec.setUpdateTime(currDate);
                    goodsSpec.setGoodsId(newGoods.getGoodsId());
                    goodsSpec.setDelFlag(DeleteFlag.NO);
                    goodsSpec.setSpecId(goodsSpecRepository.save(goodsSpec).getSpecId());
                });
            }
            //新增规格值
            if (CollectionUtils.isNotEmpty(specDetails)) {
                specDetails.stream().filter(goodsSpecDetail -> goodsSpecDetail.getSpecDetailId() == null && Constants.yes.equals(isSpecDetailEnable.get(goodsSpecDetail.getMockSpecDetailId()))).forEach(goodsSpecDetail -> {
                    Optional<GoodsSpec> specOpt = specs.stream().filter(goodsSpec -> goodsSpec.getMockSpecId().equals(goodsSpecDetail.getMockSpecId())).findFirst();
                    if (specOpt.isPresent()) {
                        goodsSpecDetail.setCreateTime(currDate);
                        goodsSpecDetail.setUpdateTime(currDate);
                        goodsSpecDetail.setGoodsId(newGoods.getGoodsId());
                        goodsSpecDetail.setDelFlag(DeleteFlag.NO);
                        goodsSpecDetail.setSpecId(specOpt.get().getSpecId());
                        goodsSpecDetail.setSpecDetailId(goodsSpecDetailRepository.save(goodsSpecDetail).getSpecDetailId());
                    }
                });
            }
        } else {//修改为单规格
            //如果老数据为多规格
            if (Constants.yes.equals(oldGoods.getMoreSpecFlag())) {
                //删除规格
                goodsSpecRepository.deleteByGoodsId(newGoods.getGoodsId());

                //删除规格值
                goodsSpecDetailRepository.deleteByGoodsId(newGoods.getGoodsId());

                //删除商品规格值
                goodsInfoSpecDetailRelRepository.deleteByGoodsId(newGoods.getGoodsId());
            }
        }

        //只存储新增的SKU数据，用于当修改价格及订货量设置为否时，只为新SKU增加相关的价格数据
        List<GoodsInfo> newGoodsInfo = new ArrayList<>();//需要被添加的sku信息

        //更新原有的SKU列表
        List<GoodsInfo> goodsInfos = saveRequest.getGoodsInfos();
        List<GoodsInfo> oldGoodsInfos = new ArrayList<>();//需要被更新的sku信息
        List<String> delInfoIds = new ArrayList<>();//需要被删除的sku信息
        if (CollectionUtils.isNotEmpty(goodsInfos)) {
            infoQueryRequest = new GoodsInfoQueryRequest();
            infoQueryRequest.setGoodsId(newGoods.getGoodsId());
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            List<GoodsInfo> oldInfos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());

            if (CollectionUtils.isNotEmpty(oldInfos)) {
                for (GoodsInfo oldInfo : oldInfos) {
                    if (Objects.isNull(oldInfo.getStock())) {
                        oldInfo.setStock(BigDecimal.ZERO);
                    }
                    GoodsInfo goodsInfo1 = goodsInfos.stream().filter(goodsInfo -> oldInfo.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).findFirst().orElse(null);
                    if (null==goodsInfo1){
                        break;
                    }
                    Optional<GoodsInfo> infoOpt = goodsInfos.stream().filter(goodsInfo -> oldInfo.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).findFirst();
                    if (!oldInfo.getCompanyType().equals(CompanyType.SUPPLIER)){
                        //如果SKU存在，更新
                        if (infoOpt.isPresent()) {
                            infoOpt.get().setAddedFlag(oldInfo.getAddedFlag());

                            //如果上下架不是部分上下架，以SPU为准
                            if (!newGoods.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                                infoOpt.get().setAddedFlag(newGoods.getAddedFlag());
                            }

                            //更新上下架时间
                            if (isChgAddedTime) {
                                infoOpt.get().setAddedTime(newGoods.getAddedTime());
                            }

                            if (Objects.isNull(infoOpt.get().getStock())) {
                                infoOpt.get().setStock(BigDecimal.ZERO);
                            }

                            //如果发生设价类型变化，原SKU的独立设价设为FALSE
                            if (isChgPriceType) {
                                oldInfo.setAloneFlag(Boolean.FALSE);
                            }

                            //非独立设价SKU的叠加等级价、自定义客户都要以SPU为准
                            if (Objects.isNull(oldInfo.getAloneFlag())
                                    || Boolean.FALSE.equals(oldInfo.getAloneFlag())) {
                                infoOpt.get().setCustomFlag(newGoods.getCustomFlag());
                                infoOpt.get().setLevelDiscountFlag(newGoods.getLevelDiscountFlag());
                            }
                            //市场价刷新非独立设价的SKU
                            if (!oldInfo.getCompanyType().equals(CompanyType.SUPPLIER)){
                                if (Objects.nonNull(newGoods.getMarketPrice()) && Boolean.FALSE.equals(oldInfo
                                        .getAloneFlag())) {
                                    infoOpt.get().setMarketPrice(newGoods.getMarketPrice());
                                }
                            }

                            //不允许独立设价 sku的叠加客户等级折扣  与spu同步
                            if (newGoods.getPriceType() == 1 && newGoods.getAllowPriceSet() == 0) {
                                infoOpt.get().setLevelDiscountFlag(newGoods.getLevelDiscountFlag());
                            }
                            infoOpt.get().setCostPrice(newGoods.getCostPrice());
                            infoOpt.get().setAuditStatus(oldGoods.getAuditStatus());
                            infoOpt.get().setBrandId(oldGoods.getBrandId());
                            infoOpt.get().setCateId(oldGoods.getCateId());
                            infoOpt.get().setSaleType(oldGoods.getSaleType());
                            if (infoOpt.get().getGoodsInfoQrcode() == null) {
                                oldInfo.setGoodsInfoQrcode(null);
                            }
                            KsBeanUtil.copyProperties(infoOpt.get(), oldInfo);
                            //修改预估佣金
                            if (Objects.nonNull(oldInfo.getMarketPrice()) && Objects.nonNull(oldInfo.getCommissionRate())) {
                                oldInfo.setDistributionCommission(oldInfo.getMarketPrice().multiply(oldInfo
                                        .getCommissionRate()));
                            }
                            oldGoodsInfos.add(oldInfo);//修改前后都存在的数据--加入需要被更新的sku中
                        } else {
                            oldInfo.setDelFlag(DeleteFlag.YES);
                            delInfoIds.add(oldInfo.getGoodsInfoId());//修改后不存在的数据--加入需要被删除的sku中
                        }
                    }else{
                        if (Objects.nonNull(infoOpt) && Objects.nonNull(infoOpt.get())){
                            KsBeanUtil.copyProperties(infoOpt.get(), oldInfo);
                        }
                        oldInfo.setVipPrice(oldInfo.getMarketPrice());

                    }
                    oldInfo.setGoodsInfoName(newGoods.getGoodsName());
                    oldInfo.setUpdateTime(currDate);
                    if (null != infoOpt.get().getPresellStock()){
                        oldInfo.setPresellStock(infoOpt.get().getPresellStock());//预售虚拟库存
                    }
                    if (flag){
                        oldInfo.setHostSku(0);
                    }
                    goodsInfoRepository.save(oldInfo);

                    //查询 拆箱表
                    List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.getByInfoId(oldInfo.getGoodsInfoId());
                    for (DevanningGoodsInfo devanningGoodsInfo : devanningGoodsInfos){
                        DevanningGoodsInfo devanningGoodsInfo1 = KsBeanUtil.convert(oldInfo, DevanningGoodsInfo.class);
                            //拆箱表私有属性在次赋值

                            devanningGoodsInfo1.setDevanningId(devanningGoodsInfo.getDevanningId());//id
                            devanningGoodsInfo1.setDivisorFlag(devanningGoodsInfo.getDivisorFlag());//falg
                            devanningGoodsInfo1.setDevanningUnit(devanningGoodsInfo.getDevanningUnit());//单位
                            devanningGoodsInfo1.setVipPrice(oldInfo.getVipPrice());//vip价格
                            devanningGoodsInfo1.setGoodsInfoSubtitle(devanningGoodsInfo.getGoodsInfoSubtitle());//商品副标题
                            devanningGoodsInfo1.setMarketPrice(oldInfo.getMarketPrice());//市场价格
                            devanningGoodsInfo1.setAddStep(devanningGoodsInfo.getAddStep());//拆箱步长
                            //开始封装标题
                        if (oldInfo.getCompanyType().equals(CompanyType.SUPPLIER)){
                            BigDecimal subtitlePrice = oldInfo.getMarketPrice().divide(oldInfo.getAddStep(), 2, BigDecimal.ROUND_UP);
                            String goodsSubtitle = "1"+oldInfo.getGoodsInfoUnit()+"=" + oldInfo.getAddStep() + oldInfo.getDevanningUnit() + "x" + subtitlePrice + "元/" +  oldInfo.getDevanningUnit();
                            String goodsSubtitleNew = subtitlePrice + "/" + oldInfo.getDevanningUnit();
                            devanningGoodsInfo1.setGoodsInfoSubtitle(goodsSubtitle);
                            devanningGoodsInfo1.setGoodsSubtitleNew(goodsSubtitleNew);
                            devanningGoodsInfo1.setMarketPrice(oldInfo.getMarketPrice());
                            devanningGoodsInfo1.setVipPrice(oldInfo.getVipPrice());
                            if (Objects.nonNull(devanningGoodsInfo1) && Objects.nonNull(devanningGoodsInfo1.getCostPrice())){
                                devanningGoodsInfo1.setCostPrice(devanningGoodsInfo1.getCostPrice());
                            }
                            devanningGoodsInfo1.setDevanningUnit(oldInfo.getDevanningUnit());
                            devanningGoodsInfo1.setGoodsWeight(oldInfo.getGoodsInfoWeight());
                            devanningGoodsInfo1.setGoodsCubage(oldInfo.getGoodsInfoCubage());
                            devanningGoodsInfo1.setIsScatteredQuantitative(oldInfo.getIsScatteredQuantitative()==null?0:oldInfo.getIsScatteredQuantitative());
                            devanningGoodsInfo1.setSupplyPrice(BigDecimal.ZERO);
                            devanningGoodsInfo1.setVipPrice(oldInfo.getVipPrice());
                            devanningGoodsInfo1.setProviderId(oldInfo.getProviderId());
                            devanningGoodsInfo1.setGoodsInfoBatchNo(oldInfo.getGoodsInfoBatchNo());
                            devanningGoodsInfo1.setSingleOrderPurchaseNum(oldInfo.getSingleOrderPurchaseNum());
                            devanningGoodsInfo1.setPurchaseNum(oldInfo.getPurchaseNum());
                            devanningGoodsInfo1.setWareId(oldInfo.getWareId());
                            devanningGoodsInfo1.setHostSku(oldInfo.getHostSku());
                            devanningGoodsInfo1.setAddStep(oldInfo.getAddStep());
                            devanningGoodsInfo1.setHostSku(oldInfo.getHostSku());
                        }
                     devanningGoodsInfoRepository.save(devanningGoodsInfo1);
                    }
                }
                //更新最新库存信息
                List<GoodsInfo> goodsInfoList = goodsInfos.stream().
                        filter(goodsInfo -> StringUtils.isNotEmpty(goodsInfo.getGoodsInfoId())).collect(Collectors.toList());
                goodsInfoList.forEach(goodsInfo -> {
                    List<GoodsWareStock> stockList = goodsInfo.getGoodsWareStocks();
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        stockList.forEach(stock -> {
                            goodsWareStockService.updateByGoodsInfoId(goodsInfo.getGoodsInfoId(), stock.getStock(), stock.getWareId());
                        });
                    }
                });

                //删除SKU相关的规格关联表
                if (!delInfoIds.isEmpty()) {
                    goodsInfoSpecDetailRelRepository.deleteByGoodsInfoIds(delInfoIds, newGoods.getGoodsId());
                    //批量删除积分商城中该sku，避免前台出现脏数据
                    pointsGoodsRepository.deleteByGoodInfoIdList(delInfoIds);
                }
            }

//            //只保存新SKU
//            for (GoodsInfo sku : goodsInfos) {
//                sku.setCateId(newGoods.getCateId());
//                sku.setBrandId(newGoods.getBrandId());
//                sku.setGoodsId(newGoods.getGoodsId());
//                sku.setGoodsInfoName(newGoods.getGoodsName());
//                sku.setCreateTime(currDate);
//                sku.setUpdateTime(currDate);
//                sku.setDelFlag(DeleteFlag.NO);
//                sku.setCompanyInfoId(oldGoods.getCompanyInfoId());
//                sku.setPriceType(oldGoods.getPriceType());
//                sku.setStoreId(oldGoods.getStoreId());
//                sku.setAuditStatus(oldGoods.getAuditStatus());
//                sku.setCompanyType(oldGoods.getCompanyType());
//                //只处理新增的SKU
//                if (sku.getGoodsInfoId() != null) {
//                    continue;
//                }
//                if (sku.getStock() == null) {
//                    sku.setStock(BigDecimal.ZERO);
//                }
//                sku.setCustomFlag(oldGoods.getCustomFlag());
//                sku.setLevelDiscountFlag(oldGoods.getLevelDiscountFlag());
//
//                //新商品会采用SPU市场价
//                if (newGoods.getMarketPrice() != null) {
//                    sku.setMarketPrice(newGoods.getMarketPrice());
//                }
//                sku.setCostPrice(oldGoods.getCostPrice());
//
//                //如果SPU选择部分上架，新增SKU的上下架状态为上架
//                sku.setAddedFlag(oldGoods.getAddedFlag().equals(AddedFlag.PART.toValue()) ? AddedFlag.YES.toValue() : newGoods.getAddedFlag());
//                sku.setAddedTime(oldGoods.getAddedTime());
//                sku.setAloneFlag(Boolean.FALSE);
//                sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
//                sku.setSaleType(newGoods.getSaleType());
//                sku.setGoodsSource(oldGoods.getGoodsSource());
//
//                String goodsInfoId = goodsInfoRepository.save(sku).getGoodsInfoId();
//
//
//                //查询 拆箱表
//                List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.getByInfoId(goodsInfoId);
//                for (DevanningGoodsInfo devanningGoodsInfo : devanningGoodsInfos){
//                    DevanningGoodsInfo devanningGoodsInfo1 = KsBeanUtil.convert(sku, DevanningGoodsInfo.class);
//                    //拆箱表私有属性在次赋值
//                    devanningGoodsInfo1.setDevanningId(devanningGoodsInfo.getDevanningId());//id
//                    devanningGoodsInfo1.setDivisorFlag(devanningGoodsInfo.getDivisorFlag());//falg
//                    devanningGoodsInfo1.setDevanningUnit(devanningGoodsInfo.getDevanningUnit());//单位
//                    devanningGoodsInfo1.setVipPrice(devanningGoodsInfo.getVipPrice());//vip价格
//                    devanningGoodsInfo1.setGoodsInfoSubtitle(devanningGoodsInfo.getGoodsInfoSubtitle());//商品副标题
//                    devanningGoodsInfo1.setMarketPrice(devanningGoodsInfo.getMarketPrice());//市场价格
//                    devanningGoodsInfo1.setAddStep(devanningGoodsInfo.getAddStep());//拆箱步长
//                    devanningGoodsInfoRepository.save(devanningGoodsInfo1);
//                }
//
//
//                sku.setGoodsInfoId(goodsInfoId);
//
//                //如果是多规格,新增SKU与规格明细值的关联表
//                if (Constants.yes.equals(newGoods.getMoreSpecFlag())) {
//                    if (CollectionUtils.isNotEmpty(specs)) {
//                        for (GoodsSpec spec : specs) {
//                            if (sku.getMockSpecIds().contains(spec.getMockSpecId())) {
//                                for (GoodsSpecDetail detail : specDetails) {
//                                    if (spec.getMockSpecId().equals(detail.getMockSpecId()) && sku.getMockSpecDetailIds().contains(detail.getMockSpecDetailId())) {
//                                        GoodsInfoSpecDetailRel detailRel = new GoodsInfoSpecDetailRel();
//                                        detailRel.setGoodsId(newGoods.getGoodsId());
//                                        detailRel.setGoodsInfoId(goodsInfoId);
//                                        detailRel.setSpecId(spec.getSpecId());
//                                        detailRel.setSpecDetailId(detail.getSpecDetailId());
//                                        detailRel.setDetailName(detail.getDetailName());
//                                        detailRel.setCreateTime(currDate);
//                                        detailRel.setUpdateTime(currDate);
//                                        detailRel.setDelFlag(DeleteFlag.NO);
//                                        goodsInfoSpecDetailRelRepository.save(detailRel);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                newGoodsInfo.add(sku);//修改后才存在(新出现)的数据--加入需要被添加的sku中
//            }
        }

        //为新增加的SKU补充设价数据
        if (CollectionUtils.isNotEmpty(newGoodsInfo)) {
            if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(oldGoods.getPriceType())) {
                saveRequest.setGoodsIntervalPrices(goodsIntervalPriceRepository.findByGoodsId(oldGoods.getGoodsId()));
            } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(oldGoods.getPriceType())) {
                saveRequest.setGoodsLevelPrices(goodsLevelPriceRepository.findByGoodsId(oldGoods.getGoodsId()));
                //按客户单独定价
                if (Constants.yes.equals(oldGoods.getCustomFlag())) {
                    saveRequest.setGoodsCustomerPrices(goodsCustomerPriceRepository.findByGoodsId(oldGoods.getGoodsId()));
                }
            }
            this.saveGoodsPrice(newGoodsInfo, oldGoods, saveRequest);
        }

        //如果spu是部分上架,当相关Sku都是上架时，自动设为上架
        if (AddedFlag.PART.toValue() == oldGoods.getAddedFlag()) {
            long addedCount = goodsInfos.stream().filter(g -> DeleteFlag.NO.equals(g.getDelFlag()) && AddedFlag.YES.toValue() == g.getAddedFlag()).count();
            long count = goodsInfos.stream().filter(g -> DeleteFlag.NO.equals(g.getDelFlag())).count();
            if (addedCount == count) {
                oldGoods.setAddedFlag(AddedFlag.YES.toValue());
               goodsRepository.save(oldGoods);
            }
        }
        if (oldGoods.getCompanyType().equals(CompanyType.SUPPLIER)) {
            //最后更新goods 表
            Optional<Goods> byId = goodsRepository.findById(oldGoods.getGoodsId());
            List<String> goodsIdList =new ArrayList<>();
            goodsIdList.add(byId.get().getGoodsId());
            List<GoodsInfo> byGoodsIds = goodsInfoRepository.findByGoodsIds(goodsIdList);
            for (GoodsInfo goodsInfo:byGoodsIds) {
                Goods goods = byId.get();
                if (Objects.equals(goodsInfo.getHostSku(),Constants.yes)){
                    List<DevanningGoodsInfo> byInfoId = devanningGoodsInfoRepository.getByInfoId(goodsInfo.getGoodsInfoId());
                    goodsInfo.setDevanningUnit(byInfoId.get(Constants.no).getDevanningUnit());
                    BigDecimal addStep = goodsInfo.getAddStep();//步长
                    if (Objects.isNull(addStep)){
                        addStep=BigDecimal.ONE;
                    }
                    BigDecimal subtitlePrice = goodsInfo.getMarketPrice().divide(addStep, 2, BigDecimal.ROUND_UP);
                    //1箱(30.00杯x0.00元/杯)
                    String goodsSubtitle = "1"+goodsInfo.getGoodsInfoUnit()+"=" + addStep + goodsInfo.getDevanningUnit() + "x" + subtitlePrice + "元/" +  goodsInfo.getDevanningUnit();
                    String goodsSubtitleNew = subtitlePrice + "/" + goodsInfo.getDevanningUnit();
                    goods.setGoodsSubtitle(goodsSubtitle);
                    goods.setGoodsSubtitleNew(goodsSubtitleNew);
                    if (Objects.nonNull(goodsInfo) && Objects.nonNull(goodsInfo.getCostPrice())){
                        goods.setCostPrice(goodsInfo.getCostPrice());
                    }else {
                        goods.setCostPrice(goodsInfo.getMarketPrice());
                    }
                    goods.setGoodsWeight(goodsInfo.getGoodsInfoWeight());
                    goods.setGoodsCubage(goodsInfo.getGoodsInfoCubage());
                    goods.setGoodsUnit(goodsInfo.getGoodsInfoUnit());
                    goods.setMarketPrice(goodsInfo.getMarketPrice());
                    goods.setVipPrice(goodsInfo.getVipPrice());
                    goodsRepository.save(goods);
                }
                // 只有一个规格更新属性值为副标题
                final GoodsAtrrKeyQueryRequest goodsAtrrKeyQueryRequest = new GoodsAtrrKeyQueryRequest();
                goodsAtrrKeyQueryRequest.setGoodsId(goods.getGoodsId());
                final List<GoodsAttributeKey> attributeKeys = goodsAttributeKeyService.query(goodsAtrrKeyQueryRequest);
                if (CollectionUtils.isNotEmpty(attributeKeys) && attributeKeys.size() == 1){
                    final GoodsAttributeKey goodsAttributeKeyUpdate = attributeKeys.get(0);
                    goodsAttributeKeyUpdate.setGoodsAttributeValue(goods.getGoodsSubtitle());
                    goodsAttributeKeyService.updateAttribute(goodsAttributeKeyUpdate);
                }

            }

        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("newGoodsInfo", newGoodsInfo);
        returnMap.put("delInfoIds", delInfoIds);
        returnMap.put("oldGoodsInfos", oldGoodsInfos);
        returnMap.put("checkImageId",checkImageId.get());
        return returnMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(GoodsInfo goodsInfo, BigDecimal stock) {
        List<StockoutDetail> stockoutDetailList = stockoutDetailService.list(StockoutDetailQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());
        List<String> params = new ArrayList<>();
        params.add("超级大白鲸");
        params.add(goodsInfo.getGoodsInfoName());
        if (CollectionUtils.isNotEmpty(stockoutDetailList)) {
            List<StockoutDetail> detailList = new ArrayList<>();
            for (StockoutDetail stockoutDetail : stockoutDetailList) {
                if (BigDecimal.valueOf(stockoutDetail.getStockoutNum()).compareTo(stock) <=0 ) {
                    stockoutDetail.setDelFlag(DeleteFlag.YES);
                    detailList.add(stockoutDetail);
                    MessageMQRequest messageMQRequest = new MessageMQRequest();
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("type", NodeType.DISTRIBUTION.toValue());
                    messageMap.put("id", goodsInfo.getGoodsInfoId());
                    messageMap.put("node", ARRIVAL_NODE);
                    messageMQRequest.setNodeCode(GOODS_ARRIVAL);
                    messageMQRequest.setNodeType(NodeType.DISTRIBUTION.toValue());
                    messageMQRequest.setParams(params);
                    messageMQRequest.setPic(goodsInfo.getGoodsInfoImg());
                    messageMQRequest.setRouteParam(messageMap);
                    messageMQRequest.setCustomerId(stockoutDetail.getCustomerId());
                    goodsProducerService.sendMessage(messageMQRequest);
                }
            }
            if (CollectionUtils.isNotEmpty(detailList)) {
                stockoutDetailService.deleteByIdList(stockoutDetailList);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(RetailGoodsInfo goodsInfo, Long stock) {
        List<StockoutDetail> stockoutDetailList = stockoutDetailService.list(StockoutDetailQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());
        List<String> params = new ArrayList<>();
        params.add("超级大白鲸");
        params.add(goodsInfo.getGoodsInfoName());
        if (CollectionUtils.isNotEmpty(stockoutDetailList)) {
            List<StockoutDetail> detailList = new ArrayList<>();
            for (StockoutDetail stockoutDetail : stockoutDetailList) {
                if (stockoutDetail.getStockoutNum() <= stock) {
                    stockoutDetail.setDelFlag(DeleteFlag.YES);
                    detailList.add(stockoutDetail);
                    MessageMQRequest messageMQRequest = new MessageMQRequest();
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("type", NodeType.DISTRIBUTION.toValue());
                    messageMap.put("id", goodsInfo.getGoodsInfoId());
                    messageMap.put("node", ARRIVAL_NODE);
                    messageMQRequest.setNodeCode(GOODS_ARRIVAL);
                    messageMQRequest.setNodeType(NodeType.DISTRIBUTION.toValue());
                    messageMQRequest.setParams(params);
                    messageMQRequest.setPic(goodsInfo.getGoodsInfoImg());
                    messageMQRequest.setRouteParam(messageMap);
                    messageMQRequest.setCustomerId(stockoutDetail.getCustomerId());
                    goodsProducerService.sendMessage(messageMQRequest);
                }
            }
            if (CollectionUtils.isNotEmpty(detailList)) {
                stockoutDetailService.deleteByIdList(stockoutDetailList);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendBulkMessage(BulkGoodsInfo goodsInfo, Long stock) {
        List<StockoutDetail> stockoutDetailList = stockoutDetailService.list(StockoutDetailQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());
        List<String> params = new ArrayList<>();
        params.add("超级大白鲸");
        params.add(goodsInfo.getGoodsInfoName());
        if (CollectionUtils.isNotEmpty(stockoutDetailList)) {
            List<StockoutDetail> detailList = new ArrayList<>();
            for (StockoutDetail stockoutDetail : stockoutDetailList) {
                if (stockoutDetail.getStockoutNum() <= stock) {
                    stockoutDetail.setDelFlag(DeleteFlag.YES);
                    detailList.add(stockoutDetail);
                    MessageMQRequest messageMQRequest = new MessageMQRequest();
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("type", NodeType.DISTRIBUTION.toValue());
                    messageMap.put("id", goodsInfo.getGoodsInfoId());
                    messageMap.put("node", ARRIVAL_NODE);
                    messageMQRequest.setNodeCode(GOODS_ARRIVAL);
                    messageMQRequest.setNodeType(NodeType.DISTRIBUTION.toValue());
                    messageMQRequest.setParams(params);
                    messageMQRequest.setPic(goodsInfo.getGoodsInfoImg());
                    messageMQRequest.setRouteParam(messageMap);
                    messageMQRequest.setCustomerId(stockoutDetail.getCustomerId());
                    goodsProducerService.sendMessage(messageMQRequest);
                }
            }
            if (CollectionUtils.isNotEmpty(detailList)) {
                stockoutDetailService.deleteByIdList(stockoutDetailList);
            }
        }
    }
    /**
     * 第二步，商品保存设价
     *
     * @param saveRequest
     */
    @Transactional
    public void savePrice(GoodsSaveRequest saveRequest) throws SbcRuntimeException {
        Goods newGoods = saveRequest.getGoods();
        Goods oldGoods = goodsRepository.findById(newGoods.getGoodsId()).orElse(null);
        if (oldGoods == null || oldGoods.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        goodsIntervalPriceRepository.deleteByGoodsId(oldGoods.getGoodsId());
        goodsLevelPriceRepository.deleteByGoodsId(oldGoods.getGoodsId());
        goodsCustomerPriceRepository.deleteByGoodsId(oldGoods.getGoodsId());

        //按订货量设价，保存订货区间
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(newGoods.getPriceType())) {
            if (CollectionUtils.isEmpty(saveRequest.getGoodsIntervalPrices()) || saveRequest.getGoodsIntervalPrices().stream().filter(intervalPrice -> intervalPrice.getCount() == 1).count() == 0) {
                GoodsIntervalPrice intervalPrice = new GoodsIntervalPrice();
                intervalPrice.setCount(1L);
                intervalPrice.setPrice(newGoods.getMarketPrice());
                if (saveRequest.getGoodsIntervalPrices() == null) {
                    saveRequest.setGoodsLevelPrices(new ArrayList<>());
                }
                saveRequest.getGoodsIntervalPrices().add(intervalPrice);
            }

            saveRequest.getGoodsIntervalPrices().forEach(intervalPrice -> {
                intervalPrice.setGoodsId(newGoods.getGoodsId());
                intervalPrice.setType(PriceType.SPU);
            });
            goodsIntervalPriceRepository.saveAll(saveRequest.getGoodsIntervalPrices());
        } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(newGoods.getPriceType())) {
            //按客户等级
            saveRequest.getGoodsLevelPrices().forEach(goodsLevelPrice -> {
                goodsLevelPrice.setGoodsId(newGoods.getGoodsId());
                goodsLevelPrice.setType(PriceType.SPU);
            });
            goodsLevelPriceRepository.saveAll(saveRequest.getGoodsLevelPrices());

            //按客户单独定价
            if (Constants.yes.equals(newGoods.getCustomFlag()) && CollectionUtils.isNotEmpty(saveRequest.getGoodsCustomerPrices())) {
                saveRequest.getGoodsCustomerPrices().forEach(price -> {
                    price.setGoodsId(newGoods.getGoodsId());
                    price.setType(PriceType.SPU);
                });
                goodsCustomerPriceRepository.saveAll(saveRequest.getGoodsCustomerPrices());
            }
        }
        if (!Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(newGoods.getPriceType()) || newGoods.getSaleType() == 1) {
            oldGoods.setAllowPriceSet(0);
        }
        oldGoods.setPriceType(newGoods.getPriceType());
        oldGoods.setCustomFlag(newGoods.getCustomFlag());
        oldGoods.setLevelDiscountFlag(newGoods.getLevelDiscountFlag());
        goodsRepository.save(oldGoods);

        //存储SKU相关的设价数据
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setGoodsId(newGoods.getGoodsId());
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
//        for (GoodsInfo sku : goodsInfos) {
//            sku.setPriceType(newGoods.getPriceType());
//            sku.setCustomFlag(newGoods.getCustomFlag());
//            sku.setLevelDiscountFlag(newGoods.getLevelDiscountFlag());
//        }
//        goodsInfoRepository.save(goodsInfos);

        this.saveGoodsPrice(goodsInfos, newGoods, saveRequest);
    }

    /**
     * 同时保存商品基本信息/设价信息
     *
     * @param saveRequest 参数
     */
    @Transactional
    public String addAll(GoodsSaveRequest saveRequest) {
        saveRequest.getGoods().setGoodsId(this.add(saveRequest));
        this.savePrice(saveRequest);
        return saveRequest.getGoods().getGoodsId();
    }

    /**
     * 同时更新商品基本信息/设价信息
     */
    @Transactional
    public Map<String, Object> editAll(GoodsSaveRequest saveRequest) {
        Map<String, Object> res = this.edit(saveRequest);
        this.savePrice(saveRequest);
        return res;
    }

    /**
     * 储存商品相关设价信息
     *
     * @param goodsInfos  sku集
     * @param goods       同一个spu信息
     * @param saveRequest 请求封装参数
     */
    private void saveGoodsPrice(List<GoodsInfo> goodsInfos, Goods goods, GoodsSaveRequest saveRequest) {
        List<String> skuIds = new ArrayList<>();
        //提取非独立设价的Sku编号,进行清理设价数据
        if (goods.getPriceType() == 1 && goods.getAllowPriceSet() == 0) {
            skuIds = goodsInfos.stream()
                    .map(GoodsInfo::getGoodsInfoId)
                    .collect(Collectors.toList());
        } else {
            skuIds = goodsInfos.stream()
                    .filter(sku -> Objects.isNull(sku.getAloneFlag()) || !sku.getAloneFlag())
                    .map(GoodsInfo::getGoodsInfoId)
                    .collect(Collectors.toList());
        }

        if (skuIds.size() > 0) {
            goodsIntervalPriceRepository.deleteByGoodsInfoIds(skuIds);
            goodsLevelPriceRepository.deleteByGoodsInfoIds(skuIds);
            goodsCustomerPriceRepository.deleteByGoodsInfoIds(skuIds);
        }

        for (GoodsInfo sku : goodsInfos) {
            //如果SKU是保持独立，则不更新
            if (!(goods.getPriceType() == 1 && goods.getAllowPriceSet() == 0) && Objects.nonNull(sku.getAloneFlag())
                    && sku.getAloneFlag()) {
                continue;
            }

            //按订货量设价，保存订货区间
            if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
                if (CollectionUtils.isNotEmpty(saveRequest.getGoodsIntervalPrices())) {
                    List<GoodsIntervalPrice> newGoodsInterValPrice = new ArrayList<>();
                    saveRequest.getGoodsIntervalPrices().forEach(intervalPrice -> {
                        GoodsIntervalPrice newIntervalPrice = new GoodsIntervalPrice();
                        newIntervalPrice.setGoodsId(sku.getGoodsId());
                        newIntervalPrice.setGoodsInfoId(sku.getGoodsInfoId());
                        newIntervalPrice.setType(PriceType.SKU);
                        newIntervalPrice.setCount(intervalPrice.getCount());
                        newIntervalPrice.setPrice(intervalPrice.getPrice());
                        newGoodsInterValPrice.add(newIntervalPrice);
                    });
                    goodsIntervalPriceRepository.saveAll(newGoodsInterValPrice);
                }
            } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(goods.getPriceType())) {
                //按客户等级
                if (CollectionUtils.isNotEmpty(saveRequest.getGoodsLevelPrices())) {
                    List<GoodsLevelPrice> newLevelPrices = new ArrayList<>();
                    saveRequest.getGoodsLevelPrices().forEach(goodsLevelPrice -> {
                        GoodsLevelPrice newLevelPrice = new GoodsLevelPrice();
                        newLevelPrice.setLevelId(goodsLevelPrice.getLevelId());
                        newLevelPrice.setGoodsId(sku.getGoodsId());
                        newLevelPrice.setGoodsInfoId(sku.getGoodsInfoId());
                        newLevelPrice.setPrice(goodsLevelPrice.getPrice());
                        newLevelPrice.setCount(goodsLevelPrice.getCount());
                        newLevelPrice.setMaxCount(goodsLevelPrice.getMaxCount());
                        newLevelPrice.setType(PriceType.SKU);
                        newLevelPrices.add(newLevelPrice);
                    });
                    goodsLevelPriceRepository.saveAll(newLevelPrices);
                }

                //按客户单独定价
                if (Constants.yes.equals(goods.getCustomFlag()) && CollectionUtils.isNotEmpty(saveRequest.getGoodsCustomerPrices())) {
                    List<GoodsCustomerPrice> newCustomerPrices = new ArrayList<>();
                    saveRequest.getGoodsCustomerPrices().forEach(price -> {
                        GoodsCustomerPrice newCustomerPrice = new GoodsCustomerPrice();
                        newCustomerPrice.setGoodsInfoId(sku.getGoodsInfoId());
                        newCustomerPrice.setGoodsId(sku.getGoodsId());
                        newCustomerPrice.setCustomerId(price.getCustomerId());
                        newCustomerPrice.setMaxCount(price.getMaxCount());
                        newCustomerPrice.setCount(price.getCount());
                        newCustomerPrice.setType(PriceType.SKU);
                        newCustomerPrice.setPrice(price.getPrice());
                        newCustomerPrices.add(newCustomerPrice);
                    });
                    goodsCustomerPriceRepository.saveAll(newCustomerPrices);
                }
            }
        }
    }

    /**
     * 商品删除
     *
     * @param goodsIds 多个商品
     * @throws SbcRuntimeException
     */
    @Transactional
    public void delete(List<String> goodsIds) throws SbcRuntimeException {

//
        List<StandardGoodsRel> standardGoodsRels = standardGoodsRelRepository.findByGoodsIds(goodsIds);
        List<String> standardIds = standardGoodsRels.stream().map(StandardGoodsRel::getStandardId).collect(Collectors.toList());
        // 关联商品下架
        goodsRepository.updateAddedFlagByPrividerGoodsIds(AddedFlag.NO.toValue(),goodsIds);

        goodsRepository.deleteByGoodsIds(goodsIds);
        goodsInfoRepository.deleteByGoodsIds(goodsIds);
        goodsPropDetailRelRepository.deleteByGoodsIds(goodsIds);
        goodsSpecRepository.deleteByGoodsIds(goodsIds);
        goodsSpecDetailRepository.deleteByGoodsIds(goodsIds);
        goodsInfoSpecDetailRelRepository.deleteByGoodsIds(goodsIds);
        standardGoodsRelRepository.deleteByGoodsIds(goodsIds);
        pointsGoodsRepository.deleteByGoodsIdList(goodsIds);
        //删除拆箱表
        devanningGoodsInfoRepository.deleteByGoodsIdList(goodsIds);
        goodsIds.forEach(goodsID->{
            distributiorGoodsInfoRepository.deleteByGoodsId(goodsID);
        });

        this.delRecommendGoods(0,goodsIds);

        //ares埋点-商品-后台批量删除商品spu
        goodsAresService.dispatchFunction("delGoodsSpu", goodsIds);
    }

    /**
     * 更新商品上下架状态
     *
     * @param addedFlag 状态
     * @param goodsIds  多个商品
     * @throws SbcRuntimeException
     */
    @Transactional
    public void updateAddedStatus(Integer addedFlag, List<String> goodsIds) throws SbcRuntimeException {
        goodsRepository.updateAddedFlagByGoodsIds(addedFlag, goodsIds);
        goodsInfoRepository.updateAddedFlagByGoodsIds(addedFlag, goodsIds);
        devanningGoodsInfoRepository.updateAddedFlagByGoodsIds(addedFlag, goodsIds);

        if (0 == addedFlag) {
            goodsIds.forEach(goodsID->{
                distributiorGoodsInfoRepository.deleteByGoodsId(goodsID);
            });
        }
        this.delRecommendGoods(addedFlag,goodsIds);
    }
    /**
     * 更新商品上下架状态
     *
     * @param addedFlag 状态
     * @param goodsIds  多个商品
     * @throws SbcRuntimeException
     */
    @Transactional
    public void updateAddedFlagByGoodsInfoIds(Integer addedFlag, List<String> goodsIds) throws SbcRuntimeException {
        devanningGoodsInfoRepository.updateAddedFlagByGoodsInfoIds(addedFlag, goodsIds);

        if (0 == addedFlag) {
            goodsIds.forEach(goodsID->{
                distributiorGoodsInfoRepository.deleteByGoodsInfoId(goodsID);
            });
        }
       this.delRecommendGoods(addedFlag,goodsIds);
    }
    /**
     * 删除商户推荐商品
     * @param addedFlag
     * @param goodsIds
     */
    public void delStoreRecommendGoods(Integer addedFlag, List<String> goodsIds) {
        //下架商品如果有推荐商品则删除
        if (0 == addedFlag) {
            List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll((GoodsInfoQueryRequest.builder().goodsIds(goodsIds).build().getWhereCriteria()));
            MerchantRecommendGoods goods=new MerchantRecommendGoods();
            if (CollectionUtils.isNotEmpty(goodsInfos)) {
                goodsInfos.forEach(goodsInfoIds->{
                    goods.setGoodsInfoId(goodsInfoIds.getGoodsInfoId());
                    goods.setCompanyInfoId(goodsInfoIds.getCompanyInfoId());
                    merchantConfigGoodsService.deleteByGoodsInfoId(goods);
                });
            }
        }
    }
    /**
     * 删除推荐商品
     * @param addedFlag
     * @param goodsIds
     */
    public void delRecommendGoods(Integer addedFlag, List<String> goodsIds) {
        //下架商品如果有推荐商品则删除
        if (0 == addedFlag) {
            List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll((GoodsInfoQueryRequest.builder().goodsIds(goodsIds).build().getWhereCriteria()));
            if (CollectionUtils.isNotEmpty(goodsInfos)) {
                List<GoodsRecommendGoods> goodsRecommendGoods = goodsRecommendGoodsService.list(GoodsRecommendGoodsQueryRequest
                        .builder()
                        .goodsInfoIds(goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()))
                        .build());
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
                //删除
                delStoreRecommendGoods(addedFlag,goodsIds);
            }
        }
    }

    /**
     * 批量更新商品分类
     *
     * @param goodsIds
     * @param storeCateIds
     */
    @Transactional
    public void updateCate(List<String> goodsIds, List<Long> storeCateIds) {

        // 删除商品分类
        storeCateGoodsRelaRepository.deleteByGoodsIds(goodsIds);

        // 添加商品分类
        List<StoreCateGoodsRela> relas = storeCateIds.stream()
                .flatMap(storeCateId -> // 遍历分类
                        goodsIds.stream().map(goodsId -> // 遍历每个分类下的商品
                                StoreCateGoodsRela.builder().storeCateId(storeCateId).goodsId(goodsId).build()))
                .collect(Collectors.toList());

        storeCateGoodsRelaRepository.saveAll(relas);

    }

    /**
     * 检测商品公共基础类
     * 如分类、品牌、店铺分类
     *
     * @param goods 商品信息
     */
    public void checkBasic(Goods goods) {
//        GoodsCate cate = this.goodsCateRepository.findById(goods.getCateId()).orElse(null);
//        if (Objects.isNull(cate) || Objects.equals(DeleteFlag.YES, cate.getDelFlag())) {
//            throw new SbcRuntimeException(GoodsCateErrorCode.NOT_EXIST);
//        }

        if (goods.getBrandId() != null) {
            GoodsBrand brand = this.goodsBrandRepository.findById(goods.getBrandId()).orElse(null);
            if (Objects.isNull(brand) || Objects.equals(DeleteFlag.YES, brand.getDelFlag())) {
                throw new SbcRuntimeException(GoodsBrandErrorCode.NOT_EXIST);
            }
        }

        if (osUtil.isS2b()) {
            //验证是否签约分类
//            ContractCateQueryRequest cateQueryRequest = new ContractCateQueryRequest();
//            cateQueryRequest.setStoreId(goods.getStoreId());
//            cateQueryRequest.setCateId(goods.getCateId());
//            if (contractCateRepository.count(cateQueryRequest.getWhereCriteria()) < 1) {
//                throw new SbcRuntimeException(SigningClassErrorCode.CONTRACT_CATE_NOT_EXIST);
//            }

            //验证是否签约品牌
            if (goods.getBrandId() != null) {
                ContractBrandQueryRequest brandQueryRequest = new ContractBrandQueryRequest();
                brandQueryRequest.setStoreId(goods.getStoreId());
                brandQueryRequest.setGoodsBrandIds(Collections.singletonList(goods.getBrandId()));
                if (contractBrandRepository.count(brandQueryRequest.getWhereCriteria()) < 1) {
                    throw new SbcRuntimeException(GoodsBrandErrorCode.NOT_EXIST);
                }
            }

            //验证店铺分类存在性
            StoreCateQueryRequest request = new StoreCateQueryRequest();
            request.setStoreId(goods.getStoreId());
            request.setStoreCateIds(goods.getStoreCateIds());
            request.setDelFlag(DeleteFlag.NO);
            if (goods.getStoreCateIds().size() != storeCateRepository.count(request.getWhereCriteria())) {
                throw new SbcRuntimeException(StoreCateErrorCode.NOT_EXIST);
            }
        }
    }

    /**
     * 根据商家编号批量更新spu商家名称
     *
     * @param supplierName
     * @param companyInfoId
     */
    @LcnTransaction
    @Transactional
    public void updateSupplierName(String supplierName, Long companyInfoId) {
        goodsRepository.updateSupplierName(supplierName, companyInfoId);
    }

    /**
     * 根据多个SpuID查询属性关联
     *
     * @param goodsIds
     * @return
     */
    public List<GoodsPropDetailRel> findRefByGoodIds(List<String> goodsIds) {
        List<Object> objectList = goodsPropDetailRelRepository.findRefByGoodIds(goodsIds);
        if (objectList != null && objectList.size() > 0) {
            List<GoodsPropDetailRel> rels = new ArrayList<>();
            objectList.stream().forEach(obj -> {
                GoodsPropDetailRel rel = new GoodsPropDetailRel();
                Object[] object = (Object[]) obj;
                Long propId = ((BigInteger) object[0]).longValue();
                Long detailId = ((BigInteger) object[1]).longValue();
                String goodsId = String.valueOf(object[2]);
                rel.setPropId(propId);
                rel.setDetailId(detailId);
                rel.setGoodsId(goodsId);
                rels.add(rel);
            });
            return rels;
        }
        return Collections.emptyList();
    }

    @Transactional
    public void updateFreight(Long freightTempId, List<String> goodsIds) throws SbcRuntimeException {
        goodsRepository.updateFreightTempIdByGoodsIds(freightTempId, goodsIds);
    }


    public List<Goods> findAll(GoodsQueryRequest goodsQueryRequest){
      return goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
    }



    /**
     * 根据商品id批量查询Goods
     * @param goodsIds
     * @return
     */

    public List<Goods> listByGoodsIdsNoValid(List<String> goodsIds) {
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);

        return goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
    }

    /**
     * 根据商品id批量查询Goods
     * @param goodsIds
     * @return
     */
    public List<Goods> listByGoodsIds(List<String> goodsIds) {
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);

        List<Goods> goodsList = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());

        for (Goods goods : goodsList) {
            // 商品上架，带T/t,校验是否有库存，没有库存给出提示
            if (isErpGoods(goods.getGoodsName())) {
                //查询SKU列表
                GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
                infoQueryRequest.setGoodsId(goods.getGoodsId());
                infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
                List<GoodsInfo> goodsInfos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());


                //查询sku库存信息
                List<String> goodsInfoIds = goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
                List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIds);
                //如果不能匹配到仓，需要去除线上仓
//                List<WareHouse> onlineWareHouses = wareHouseService
//                        .list(WareHouseQueryRequest.builder().delFlag(DeleteFlag.NO).wareHouseType(WareHouseType.STORRWAREHOUSE).storeId(goods.getStoreId()).build());
//                List<Long> onlineWareIds = onlineWareHouses.stream().map(WareHouse::getWareId).collect(Collectors.toList());
//                goodsWareStocks = goodsWareStocks.stream().filter(param -> onlineWareIds.contains(param.getWareId())).collect(Collectors.toList());
                List<GoodsWareStock> finalGoodsWareStocks = goodsWareStocks;

                BigDecimal sumStock = goodsInfos.stream().map(goodsInfo -> {
                    List<GoodsWareStock> stockList = finalGoodsWareStocks.stream().
                            filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        return stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    return BigDecimal.ZERO;
                }).reduce(BigDecimal.ZERO,BigDecimal::add);
                if (sumStock.compareTo(BigDecimal.ZERO) == 0) {
                    throw new SbcRuntimeException("K-180004");
                }
            }
        }
        return goodsList;
    }




    /**
     *
     * @param goodsIds
     * @return
     */
    public List<Goods> listByProviderGoodsIds(List<String> goodsIds) {
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setProviderGoodsIds(goodsIds);
        return goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
    }

    /**
     * 根据商品id查询Goods
     * @param goodsId
     * @return
     */
    public Goods getGoodsById(String goodsId) {
        return goodsRepository.findById(goodsId).orElse(null);
    }

    /**
     * 按条件查询数量
     * @return
     */
    public long countByCondition(GoodsQueryRequest request){
        return goodsRepository.count(request.getWhereCriteria());
    }

    /**
     * @Author lvzhenwei
     * @Description 更新商品收藏量
     * @Date 15:54 2019/4/11
     * @Param [goodsModifyCollectNumRequest]
     * @return void
     **/
    @Transactional
    public void updateGoodsCollectNum(GoodsModifyCollectNumRequest goodsModifyCollectNumRequest){
        goodsRepository.updateGoodsCollectNum(goodsModifyCollectNumRequest.getGoodsCollectNum(),goodsModifyCollectNumRequest.getGoodsId());
    }

    /**
     * @Author lvzhenwei
     * @Description 更新商品销量
     * @Date 16:06 2019/4/11
     * @Param [goodsModifySalesNumRequest]
     * @return void
     **/
    @Transactional
    public void updateGoodsSalesNum(GoodsModifySalesNumRequest goodsModifySalesNumRequest){
        goodsRepository.updateGoodsSalesNum(goodsModifySalesNumRequest.getGoodsSalesNum(),goodsModifySalesNumRequest.getGoodsId());
    }

    /**
     * @Author lvzhenwei
     * @Description 更新商品评论数据
     * @Date 16:09 2019/4/11
     * @Param [goodsModifyPositiveFeedbackRequest]
     * @return void
     **/
    @Transactional
    public void updateGoodsFavorableCommentNum(GoodsModifyEvaluateNumRequest goodsModifyPositiveFeedbackRequest){
        if(goodsModifyPositiveFeedbackRequest.getEvaluateScore()==5 ||
                goodsModifyPositiveFeedbackRequest.getEvaluateScore()==4){
            //如果评论为五星好评，则好评数量加1
            goodsRepository.updateGoodsFavorableCommentNum(1L,goodsModifyPositiveFeedbackRequest.getGoodsId());
        }
        goodsRepository.updateGoodsEvaluateNum(goodsModifyPositiveFeedbackRequest.getGoodsId());
    }

    public List<Goods> findByProviderGoodsId(String providerGoodsId) {
        return goodsRepository.findAllByProviderGoodsId(providerGoodsId);
    }


    /**
     * 同步商家商品和商品库sku 里的supplyPrice
     * @param goodsSaveRequest
     * @param providerGoods
     */
    @Transactional
    public void synStoreGoodsInfoAndStandardSkuForSupplyPrice(GoodsSaveRequest goodsSaveRequest, List<Goods> providerGoods) {
        //同步商家商品的供货价
        providerGoods.forEach(s->{s.setSupplyPrice(goodsSaveRequest.getGoods().getSupplyPrice());});
        goodsRepository.saveAll(providerGoods);

        List<String> goodIds = providerGoods.stream().map(Goods::getGoodsId).collect(Collectors.toList());

        //供应商商品goodsInfoId->supplyPrice
        HashMap<String, BigDecimal> providerMapSupplyPrice = new HashMap<>();
        //供应商商品goodsInfoId->stock
        HashMap<String, BigDecimal> providerMapStock = new HashMap<>();
        List<GoodsInfo> providerGoodsInfos = goodsSaveRequest.getGoodsInfos();
        providerGoodsInfos.forEach(goodsInfo->{
            providerMapSupplyPrice.put(goodsInfo.getGoodsInfoId(), goodsInfo.getSupplyPrice());
            providerMapStock.put(goodsInfo.getGoodsInfoId(),goodsInfo.getStock());
        });

        //商家商品goodsInfoId->供应商商品goodsInfoId
        HashMap<String, String> storeMapSupplyPrice = new HashMap<>();
        List<GoodsInfo> storeGoodsInfos = goodsInfoRepository.findByGoodsIdIn(goodIds);
        storeGoodsInfos.forEach(goodsInfo->{storeMapSupplyPrice.put(goodsInfo.getGoodsInfoId(), goodsInfo.getProviderGoodsInfoId());});

        //商品库skuId->供应商goodsInfoId
        HashMap<String, String> standardMapSupplyPrice = new HashMap<>();
        List<StandardGoodsRel> standardGoodsRels = standardGoodsRelRepository.findByGoodsIds(goodIds);
        List<String> standardGoodsIds = standardGoodsRels.stream().map(StandardGoodsRel::getStandardId).collect(Collectors.toList());
        List<StandardSku> standardGoodsInfos = standardSkuRepository.findByGoodsIdIn(standardGoodsIds);
        standardGoodsInfos.forEach(standardSku->{standardMapSupplyPrice.put(standardSku.getGoodsId(), standardSku.getProviderGoodsInfoId());});

        //更新商家商品supplyPrice 和stock
        storeGoodsInfos.forEach(goodsInfo->{
            goodsInfo.setSupplyPrice(providerMapSupplyPrice.get(storeMapSupplyPrice.get(goodsInfo.getGoodsInfoId())));
            goodsInfo.setStock(providerMapStock.get(storeMapSupplyPrice.get(goodsInfo.getGoodsInfoId())));
        });
        goodsInfoRepository.saveAll(storeGoodsInfos);

        //更新商品库suppliPrice 和sotck
        standardGoodsInfos.forEach(standardSku->{
            standardSku.setSupplyPrice(providerMapSupplyPrice.get(standardMapSupplyPrice.get(standardSku.getGoodsInfoId())));
            standardSku.setStock(providerMapStock.get(standardMapSupplyPrice.get(standardSku.getGoodsInfoId())).setScale(0,BigDecimal.ROUND_DOWN).longValue());
        });
        standardSkuRepository.saveAll(standardGoodsInfos);
    }

    @Transactional
    public List<String> synDeleteStoreGoodsInfoAndStandardSku(List<String> delInfoIds) {
        if(delInfoIds != null){
            List<GoodsInfo> byProviderGoodsInfoIdIn = goodsInfoRepository.findByProviderGoodsInfoIdIn(delInfoIds);
            goodsInfoRepository.deleteByProviderGoodsInfoId(delInfoIds);
            standardSkuRepository.deleteByGoodsIds(delInfoIds);
            return byProviderGoodsInfoIdIn.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 新增库存
     * @param goodsInfos
     * @param request
     */
    @Async
    public void addGoodsStock(List<GoodsInfo> goodsInfos, GoodsSpecialRequest request ){

        List<SpecialGoodsAddDTO> specialGoodsAddDTOS = request.getSpecialGoodsList();
        List<GoodsWareStockAddRequest> goodsWareStockAddRequests = new ArrayList<>();
        goodsInfos.stream().forEach(g->{
            GoodsWareStockAddRequest goodsWareStockAddRequest = new GoodsWareStockAddRequest();
            Optional<SpecialGoodsAddDTO> optional = specialGoodsAddDTOS.stream().filter(s->s.getGoodsInfoBatchNo().equals(g.getGoodsInfoBatchNo())).findFirst();
            if(optional.isPresent()){
                Long stockWMS = optional.get().getStock() == null ? 0L : optional.get().getStock();
                BigDecimal addStep = g.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);;
                BigDecimal stock = BigDecimal.valueOf(stockWMS).divide(addStep,2,BigDecimal.ROUND_DOWN);
                goodsWareStockAddRequest.setDelFlag(DeleteFlag.NO);
                goodsWareStockAddRequest.setGoodsId(g.getGoodsId());
                goodsWareStockAddRequest.setGoodsInfoNo(g.getGoodsInfoNo());
                goodsWareStockAddRequest.setStoreId(g.getStoreId());
                goodsWareStockAddRequest.setGoodsInfoId(g.getGoodsInfoId());
                goodsWareStockAddRequest.setStock(stock);
                goodsWareStockAddRequest.setWareId(request.getWareId());
                goodsWareStockAddRequest.setCreatePerson(null);
                goodsWareStockAddRequest.setUpdatePerson(null);
                goodsWareStockAddRequests.add(goodsWareStockAddRequest);
            }
        });
        //批量增加库存
        goodsWareStockService.addList(GoodsWareStockAddListRequest.builder().goodsWareStockAddRequestList(goodsWareStockAddRequests).build());
    }

    /**
     * 新增库存
     * @param goodsInfos
     * @param request
     */
    @Async
    public void addSkuGoodsStock(List<GoodsInfo> goodsInfos, GoodsSpecialRequest request ){
        System.out.println();
        List<SpecialGoodsAddDTO> specialGoodsAddDTOS = request.getSpecialGoodsList();
        List<GoodsWareStockAddRequest> goodsWareStockAddRequests = new ArrayList<>();
        goodsInfos.stream().forEach(g->{
            GoodsWareStockAddRequest goodsWareStockAddRequest = new GoodsWareStockAddRequest();
           // Optional<SpecialGoodsAddDTO> optional = specialGoodsAddDTOS.stream().filter(s->s.getGoodsInfoBatchNo().equals(g.getGoodsInfoBatchNo())).findFirst();
         //   if(optional.isPresent()){
            //   Long stockWMS = optional.get().getStock() == null ? 0L : optional.get().getStock();
            //  BigDecimal addStep = g.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);;
              //  BigDecimal stock = BigDecimal.valueOf(stockWMS).divide(addStep,2,BigDecimal.ROUND_DOWN);
                 goodsWareStockAddRequest.setDelFlag(DeleteFlag.NO);
                goodsWareStockAddRequest.setGoodsId(g.getGoodsId());
                goodsWareStockAddRequest.setGoodsInfoNo(g.getGoodsInfoNo());
                goodsWareStockAddRequest.setStoreId(g.getStoreId());
                goodsWareStockAddRequest.setGoodsInfoId(g.getGoodsInfoId());
                goodsWareStockAddRequest.setStock(g.getStock());
                goodsWareStockAddRequest.setWareId(request.getWareId());
                goodsWareStockAddRequest.setCreatePerson(null);
                goodsWareStockAddRequest.setUpdatePerson(null);

                goodsWareStockAddRequests.add(goodsWareStockAddRequest);
           // }
        });
        //批量增加库存
        goodsWareStockService.addList(GoodsWareStockAddListRequest.builder().goodsWareStockAddRequestList(goodsWareStockAddRequests).build());
    }
    /**
     * 修改库存
     * @param goodsInfos
     * @param request
     */
    @Transactional
    public void modifySpecialGoodsStock(List<GoodsInfo> goodsInfos, GoodsSpecialRequest request){
        List<SpecialGoodsAddDTO> specialGoodsAddDTOS = request.getSpecialGoodsList();
        AtomicInteger index = new AtomicInteger();
        goodsInfos.stream().forEach(g->{
            Long stockWms = specialGoodsAddDTOS.get(index.get()).getStock();
            BigDecimal addStep = g.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);;
            BigDecimal stock = BigDecimal.valueOf(stockWms).divide(addStep,0,BigDecimal.ROUND_DOWN);
            goodsWareStockService.updateByGoodsInfoId(g.getGoodsInfoId(),stock,request.getWareId());
            index.incrementAndGet();
        });
    }

    /**
     * 修改库存
     */
    @Transactional
    public void updateBatch(GoodsQueryRequest goodsQueryRequest, Map<String, IcitemUpdateRequest> icitemUpdateRequestMap,
                            List<String> standardStr,Map<String, IcitemUpdateRequest> standarMap){
        if (Objects.nonNull(icitemUpdateRequestMap)){
            List<Goods> all = this.findAll(goodsQueryRequest);
            for (Goods inner:all){
                if (Objects.nonNull(icitemUpdateRequestMap.get(inner.getGoodsId()))){
                    inner.setGoodsWeight(icitemUpdateRequestMap.get(inner.getGoodsId()).getGoodsWeight());
                    inner.setGoodsCubage(icitemUpdateRequestMap.get(inner.getGoodsId()).getGoodsCubage());
                }
            }
            goodsRepository.saveAll(all);
        }
        if (Objects.nonNull(standarMap)){
            List<StandardGoods> byGoodsIdIn = standardGoodsRepository.findByGoodsIdIn(standardStr);
            byGoodsIdIn.forEach(param->{
                if (Objects.nonNull(param.getGoodsId())){
                    param.setGoodsWeight(standarMap.get(param.getGoodsId()).getGoodsWeight());
                    param.setGoodsCubage(standarMap.get(param.getGoodsId()).getGoodsCubage());
                }
            });
            standardGoodsRepository.saveAll(byGoodsIdIn);
        }
    }

    /**
     * @discription 查询商品属性和图文信息
     * @author yangzhen
     * @date 2020/9/3 11:17
     * @param
     * @return
     */
    public GoodsDetailResponse findGoodsDetail(String skuId) {
        List<GoodsInfo> goodsInfo = goodsInfoRepository.findByGoodsInfoIds(Arrays.asList(skuId));
        GoodsDetailResponse response = new GoodsDetailResponse();
        if(CollectionUtils.isNotEmpty(goodsInfo)){
            String goodsDetail = goodsRepository.getGoodsDetail(goodsInfo.get(0).getGoodsId());
            response.setGoodsDetail(goodsDetail);
            response.setGoodsPropDetailRels(goodsPropDetailRelRepository.queryByGoodsId(goodsInfo.get(0).getGoodsId()));
        }
        return response;
    }

    /**
     * 查询商品信息
     *
     * @param goodsId 商品ID
     * @return list
     */
    public GoodsResponse findGoodsSimple(String goodsId) throws SbcRuntimeException {
        GoodsResponse response = new GoodsResponse();
        Goods goods = goodsRepository.findById(goodsId).orElse(null);
        if (Objects.isNull(goods)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        goods.setGoodsDetail(null);
        response.setGoods(goods);
        return response;
    }


    public List<GoodsExportVO> getExportGoods(GoodsQueryRequest request) {
        List<GoodsExportVO> goodsExports = new ArrayList<>();

        List<GoodsInfo> goodsInfos = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();

        //根据SKU模糊查询SKU，获取SKU编号
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        if (StringUtils.isNotBlank(request.getLikeGoodsInfoNo()) || StringUtils.isNotBlank(request.getLikeErpNo())) {
            infoQueryRequest.setLikeGoodsInfoNo(request.getLikeGoodsInfoNo());
            infoQueryRequest.setLikeErpNo(request.getLikeErpNo());
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            infoQueryRequest.setGoodsInfoType(request.getGoodsInfoType());
            List<GoodsInfo> infos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
            if (CollectionUtils.isNotEmpty(infos)) {
                request.setGoodsIds(infos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList()));
            } else {
                return goodsExports;
            }
        }

        //获取该分类的所有子分类
        if (request.getCateId() != null) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }

        Page<Goods> goodsPage = goodsRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
        List<Goods> goodsList = goodsPage.getContent();
        if (CollectionUtils.isNotEmpty(goodsList)) {
            List<String> goodsIds = goodsList.stream().map(Goods::getGoodsId).collect(Collectors.toList());
            //查询所有SKU
            infoQueryRequest.setLikeGoodsInfoNo(null);
            infoQueryRequest.setGoodsIds(goodsIds);
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            goodsInfos.addAll(goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria()));

            //获取所有品牌
            GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
            brandRequest.setDelFlag(DeleteFlag.NO.toValue());
            brandRequest.setBrandIds(goodsList.stream().filter
                    (goods -> Objects.nonNull(goods.getBrandId())).map(Goods::getBrandId).distinct().collect(Collectors.toList()));
            goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

            //遍历spu，符合商品列表查询顺序
            goodsList.stream().forEach(goods->{
                List<GoodsInfo> goodsInfoList = goodsInfos.stream().filter(
                        goodsInfo->goodsInfo.getGoodsId().equals(goods.getGoodsId())).collect(Collectors.toList());
                Optional<GoodsBrand> goodsBrandOptional=goodsBrandList.stream().filter(
                        goodsBrand->goodsBrand.getBrandId().equals(goods.getBrandId())).findFirst();
                goodsInfoList.stream().forEach(goodsInfo->{
                    GoodsExportVO goodsExportVO = new GoodsExportVO();
                    goodsExportVO.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                    goodsExportVO.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
                    goodsExportVO.setGoodsInfoName(goodsInfo.getGoodsInfoName());
                    goodsExportVO.setErpGoodsInfoNo(goodsInfo.getErpGoodsInfoNo());
                    goodsExportVO.setGoodsNo(goods.getGoodsNo());
                    goodsExportVO.setGoodsSeqNum(goods.getGoodsSeqNum());
                    if(goodsBrandOptional.isPresent()){
                        goodsExportVO.setBrandName(goodsBrandOptional.get().getBrandName());
                    }
                    goodsExports.add(goodsExportVO);
                });
            });
        }
        return goodsExports;
    }
    public List<StoreGoodsExportVO> getStoreExportGoods(GoodsQueryRequest request) {
        List<StoreGoodsExportVO> goodsExports = new ArrayList<>();

        List<GoodsInfo> goodsInfos = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();

        //根据SKU模糊查询SKU，获取SKU编号
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        if (StringUtils.isNotBlank(request.getLikeGoodsInfoNo()) || StringUtils.isNotBlank(request.getLikeErpNo())) {
            infoQueryRequest.setLikeGoodsInfoNo(request.getLikeGoodsInfoNo());
            infoQueryRequest.setLikeErpNo(request.getLikeErpNo());
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            infoQueryRequest.setGoodsInfoType(request.getGoodsInfoType());
            List<GoodsInfo> infos = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
            if (CollectionUtils.isNotEmpty(infos)) {
                request.setGoodsIds(infos.stream().map(GoodsInfo::getGoodsId).collect(Collectors.toList()));
            } else {
                return goodsExports;
            }
        }

        //获取该分类的所有子分类
        if (request.getCateId() != null) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }



        Page<Goods> goodsPage = goodsRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
        List<Goods> goodsList = goodsPage.getContent();
        if (CollectionUtils.isNotEmpty(goodsList)) {

            List<String> goodsIds = goodsList.stream().map(Goods::getGoodsId).collect(Collectors.toList());


            //查询所有SKU
            infoQueryRequest.setLikeGoodsInfoNo(null);
            infoQueryRequest.setGoodsIds(goodsIds);
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            goodsInfos.addAll(goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria()));

            //获取所有品牌
            GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
            brandRequest.setDelFlag(DeleteFlag.NO.toValue());
            brandRequest.setBrandIds(goodsList.stream().filter
                    (goods -> Objects.nonNull(goods.getBrandId())).map(Goods::getBrandId).distinct().collect(Collectors.toList()));
            goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));
            BaseResponse<StoreInfoResponse> response;
            if (Objects.nonNull(request) && request.getStoreId()!=null){
                response= storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(request.getStoreId()).build());

            } else {
                response = null;
            }
            //遍历spu，符合商品列表查询顺序
            goodsList.stream().forEach(goods->{
                List<String> storeCateNames;
                List<GoodsInfo> goodsInfoList = goodsInfos.stream().filter(
                        goodsInfo->goodsInfo.getGoodsId().equals(goods.getGoodsId())).collect(Collectors.toList());
                Optional<GoodsBrand> goodsBrandOptional=goodsBrandList.stream().filter(
                        goodsBrand->goodsBrand.getBrandId().equals(goods.getBrandId())).findFirst();
                goodsInfoList.stream().forEach(goodsInfo->{
                    StoreGoodsExportVO goodsExportVO = new StoreGoodsExportVO();
                    goodsExportVO.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                    goodsExportVO.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
                    goodsExportVO.setGoodsInfoName(goodsInfo.getGoodsInfoName());
                    StoreCateGoodsRelaListByGoodsIdsRequest storeCateGoodsRelaListByGoodsIdsRequest=new StoreCateGoodsRelaListByGoodsIdsRequest();
                    storeCateGoodsRelaListByGoodsIdsRequest.setGoodsIds(goodsIds);

                    // 填充店铺分类
                    List<String> goodsIdss =new ArrayList<>();
                    goodsIdss.add(goods.getGoodsId());
                    List<StoreCateGoodsRela> storeCateGoodsRelas = storeCateService.getStoreCateByGoods(goodsIdss);
                    if (CollectionUtils.isNotEmpty(storeCateGoodsRelas)) {
                        List<Long> collect =storeCateGoodsRelas.stream().map(StoreCateGoodsRela::getStoreCateId).collect(Collectors.toList());
                        List<StoreCate> byIdsList = storeCateService.findByIds(collect);

                        goodsExportVO.setStoreCateNames(byIdsList.stream().map(StoreCate::getCateName).collect(Collectors.toList()));
                    } else {
                        goodsExportVO.setStoreCateNames(null);
                    }
                    //加入第三方商家规格
                    List<GoodsAttributeKey> query = goodsAttributeKeyService.query(GoodsAtrrKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
                    if (CollectionUtils.isNotEmpty(query)){
                        List<String> arr =query.stream().map(GoodsAttributeKey::getGoodsAttributeValue).collect(Collectors.toList());
                        String result = String.join(" ", arr);
                        goodsExportVO.setSpecText(result);
                    }else{
                        goodsExportVO.setSpecText("_");
                    }



                    Optional<GoodsCate> goodsCate = goodsCateRepository.findById(goods.getCateId());
                    if (!goodsCate.isPresent()) return;
                    goodsExportVO.setCateName(goodsCate.get().getCateName());

                    if (goods!=null & null!=goods.getGoodsType()){
                        if (goods.getGoodsType()==0){
                            goodsExportVO.setGoodsType("实体商品");
                        }
                        if (goods.getGoodsType()==1){
                            goodsExportVO.setGoodsType("虚拟商品");
                        }
                        if (goods.getGoodsType()==2){
                            goodsExportVO.setGoodsType("特价商品");
                        }
                    }else{
                        goodsExportVO.setGoodsType("无");
                    }


                        if (Objects.nonNull(response)&& Objects.nonNull(response.getContext())){
                            if (Objects.nonNull(response.getContext().getPileState())&&response.getContext().getPileState().toValue()==1){
                                goodsExportVO.setPileState("开启");
                            }else{
                                goodsExportVO.setPileState("关闭");
                            }
                        }


                    if (goods!=null & null!=goods.getSaleType()){
                    if (goods.getSaleType()==0){
                        goodsExportVO.setSaleType("批发");
                    }else{
                        goodsExportVO.setSaleType("零售");
                    }
                    }else{
                        goodsExportVO.setSaleType("批发");
                    }
                    goodsExportVO.setCostPrice(goodsInfo.getCostPrice());
                    goodsExportVO.setMarketPrice(goodsInfo.getMarketPrice());
                    goodsExportVO.setErpNo(goodsInfo.getErpGoodsInfoNo());
                    goodsExportVO.setVipPrice(goodsInfo.getVipPrice());
                    goodsExportVO.setSpecialPrice(goodsInfo.getSpecialPrice());
                    goodsExportVO.setGoodsInfoBatchNo(goodsInfo.getGoodsInfoBatchNo());
                    goodsExportVO.setShelflife(goodsInfo.getShelflife().toString());

                    if(goodsBrandOptional.isPresent()){
                        goodsExportVO.setBrandName(goodsBrandOptional.get().getBrandName());
                    }
                    if (goods!=null & null!=goods.getAddedFlag()){
                        if (goods.getAddedFlag()==0){
                            goodsExportVO.setAddedFlagInfo("未上架");
                        }else  if (goods.getAddedFlag()==1){
                            goodsExportVO.setAddedFlagInfo("上架");
                        }
                        else{
                            goodsExportVO.setAddedFlagInfo("部分上架");
                        }
                    }else{
                        goodsExportVO.setAddedFlagInfo("未上架");
                    }
                    goodsExportVO.setAddedFlag(goods.getAddedFlag());
                    WareHouse oneById = wareHouseService.getOneById(goods.getWareId());
                    goodsExportVO.setWareName(oneById.getWareName());
                //    goodsExportVO.setShelflife(goodsInfo.getShelflife().toString());
                    if (Objects.nonNull(goodsInfo) && Objects.nonNull(goodsInfo.getIsScatteredQuantitative())){
                        if (goodsInfo.getIsScatteredQuantitative().toString().equals(Constants.ONE)){
                            goodsExportVO.setIsScatteredQuantitative("定量");
                        } if (goodsInfo.getIsScatteredQuantitative().toString().equals(Constants.PURCHASE_DEFAULT)){
                            goodsExportVO.setIsScatteredQuantitative("散称");
                        }else {
                            goodsExportVO.setIsScatteredQuantitative("其他");
                        }
                    }else{
                        goodsExportVO.setIsScatteredQuantitative("-");
                    }


                    DevanningGoodsInfoQueryRequest devanningGoodsInfoQueryRequest =new DevanningGoodsInfoQueryRequest();
                    List<String> goodsInfoIds=new ArrayList<>();
                    goodsInfoIds.add(goodsInfo.getGoodsInfoId());
                    devanningGoodsInfoQueryRequest.setGoodsInfoIds(goodsInfoIds);
                    List<DevanningGoodsInfo> allList = devanningGoodsInfoRepository.findAll(devanningGoodsInfoQueryRequest.getWhereCriteria());
                    if (CollectionUtils.isNotEmpty(allList)){
                        goodsExportVO.setGoodsInfoSubtitle(allList.get(Constants.no).getGoodsInfoSubtitle());
                        goodsExportVO.setDevanningUnit(allList.get(Constants.no).getDevanningUnit());
                    }
                    List <String> strings= new ArrayList<>();
                    strings.add(goodsInfo.getGoodsInfoId());
                    List<GoodsWareStock> byGoodsInfoIdIn = goodsWareStockService.findByGoodsInfoIdIn(strings);
                    if (CollectionUtils.isNotEmpty(byGoodsInfoIdIn)){
                        goodsExportVO.setStock(byGoodsInfoIdIn.get(Constants.no).getStock().toString());
                    }
                    if (Objects.nonNull(goodsInfo.getGoodsInfoCubage())){
                        goodsExportVO.setGoodsInfoCubage(goodsInfo.getGoodsInfoCubage().toString());
                    }else {
                        goodsExportVO.setGoodsInfoCubage("-");
                    }
                    if (Objects.nonNull(goodsInfo.getGoodsInfoWeight())){
                        goodsExportVO.setGoodsInfoWeight(goodsInfo.getGoodsInfoWeight().toString());
                    }else{
                        goodsExportVO.setGoodsInfoWeight("-");
                    }

                    goodsExportVO.setAddStep(goodsInfo.getAddStep());
                    goodsExportVO.setGoodsInfoUnit(goodsInfo.getGoodsInfoUnit());
                    goodsExportVO.setGoodsInfoBarcode(goodsInfo.getGoodsInfoBarcode());
                    if (Objects.nonNull(goodsInfo) && Objects.nonNull(goodsInfo.getFreightTempId())){
                        FreightTemplateGoods one = freightTemplateGoodsRepository.getOne(goodsInfo.getFreightTempId());
                        goodsExportVO.setFreightTemp(one.getFreightTempName());
                    }else{
                        goodsExportVO.setFreightTemp("默认模板");
                    }

                    goodsExports.add(goodsExportVO);
                });
            });
        }
        return goodsExports;
    }
    /**
     * 报表数据通过创建时间和状态查询
     * @param request
     * @return
     */
    public List<GoodsExportByTimeAndStausVO> getExportGoodsbyCreatTimeAndStuas(GoodsQueryRequest request) {
        List<Object> goodlist=goodsRepository.findgoodsByCreatTimeAndStaus(request.getAdded_flag(),request.getCreate_timeStart(),request.getCreate_timeEnd());
        List<GoodsExportByTimeAndStausVO> list1=null;
        if (CollectionUtils.isNotEmpty(goodlist)){
            list1=resultToGoodsInfoList(goodlist);
        }
        return list1;
    }

    private List<GoodsExportByTimeAndStausVO> resultToGoodsInfoList(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            GoodsExportByTimeAndStausVO goodsExportByTimeAndStausVO = new GoodsExportByTimeAndStausVO();
            Object[] results = StringUtil.cast(item, Object[].class);
            goodsExportByTimeAndStausVO.setCate_name(StringUtil.cast(results, 0, String.class));
            goodsExportByTimeAndStausVO.setErp_goods_info_no(StringUtil.cast(results, 1, String.class));
            goodsExportByTimeAndStausVO.setGoods_info_name(StringUtil.cast(results, 2, String.class));
            goodsExportByTimeAndStausVO.setShelflife(String.valueOf(StringUtil.cast(results,3,Short.class)));
            goodsExportByTimeAndStausVO.setGoods_subtitle(StringUtil.cast(results,4,String.class));
            goodsExportByTimeAndStausVO.setMarket_price(String.valueOf(StringUtil.cast(results,5,BigDecimal.class)));
            goodsExportByTimeAndStausVO.setBrand_name(StringUtil.cast(results,6,String.class));
            goodsExportByTimeAndStausVO.setDel_flag(StringUtil.cast(results,7,String.class));
            goodsExportByTimeAndStausVO.setWarestock(String.valueOf(StringUtil.cast(results,8,BigDecimal.class)));

            return goodsExportByTimeAndStausVO;
        }).collect(Collectors.toList());
    }

    /**
     * 编辑商品排序
     * @param goodsId
     * @param goodsSeqNum
     */
    @Transactional
    public void modifyGoodsSeqNum(String goodsId, Integer goodsSeqNum) {
        //验证商品排序序号是否重复
        if(goodsSeqNum!=null){
            List<Goods> _goodsList = goodsRepository.getExistByGoodsSeqNum(goodsSeqNum, goodsId);
            if (CollectionUtils.isNotEmpty(_goodsList)) {
                throw new SbcRuntimeException(GoodsErrorCode.SEQ_NUM_ALREADY_EXISTS);
            }
        }
        goodsRepository.modifyGoodsSeqNum(goodsSeqNum, goodsId);
    }

	@Transactional
	public void modifyStoreGoodsSeqNum(String goodsId, Integer goodsSeqNum, Long storeId) {
		// 验证商品排序序号是否重复
		if (goodsSeqNum != null) {
			String res = goodsRepository.checkStoreGoodsSeqNumExist(goodsSeqNum, goodsId, storeId);
			String existStr = "1";
			if (existStr.equals(res)) {
				throw new SbcRuntimeException(GoodsErrorCode.SEQ_NUM_ALREADY_EXISTS);
			}
		}
		goodsRepository.modifyStoreGoodsSeqNum(goodsSeqNum, goodsId);
	}

    private String subStringLastChar(String str) {
        return str.substring(str.length() - 1, str.length());
    }


    private Boolean isErpGoods(String goodsName) {
        String lastChar = subStringLastChar(goodsName);
        if ("t".equals(lastChar) || "T".equals(lastChar)) {
            return true;
        }else {
            return false;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void batchModifyCate(GoodsBatchModifyCateRequest request) {
        goodsRepository.batchModifyCate(request.getCateId(), request.getGoodsIds());
        goodsInfoRepository.batchModifyCate(request.getCateId(), request.getGoodsIds());
    }
    /**
     * 商品新增
     *
     * @param goodsInfos
     * @return SPU编号
     * @throws SbcRuntimeException
     */

    @Transactional
    public boolean merchantGoodsInfoAdd(List<GoodsInfoVO> goodsInfos,Goods goods) throws SbcRuntimeException {

        boolean flag=false;
        String goodsId=goods.getGoodsId();
        goods.setWareId(Long.valueOf(Constants.yes));

        int count=1;
        for (GoodsInfoVO sku : goodsInfos) {
            //为了支持erp 下单需要不同erp
            String erpNo = getErpNo(goods.getCateId(), goods.getStoreId(),count);
            if (sku.getStock() == null) {
                sku.setStock(BigDecimal.ZERO);
            }

            sku.setCateId(goods.getCateId());
            sku.setBrandId(goods.getBrandId());
            sku.setGoodsId(goodsId);
            sku.setWareId(goods.getWareId());
            sku.setGoodsInfoName(goods.getGoodsName());
            if (Objects.nonNull(sku) && Objects.nonNull(sku.getCostPrice())){
                sku.setCostPrice(sku.getCostPrice());
            }else{
                sku.setCostPrice(sku.getMarketPrice());
            }

            sku.setCreateTime(goods.getCreateTime());
            sku.setUpdateTime(goods.getUpdateTime());
            sku.setAddedTime(goods.getAddedTime());
            sku.setDelFlag(goods.getDelFlag());
            if (!goods.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                sku.setAddedFlag(goods.getAddedFlag());
            }
            sku.setCompanyInfoId(goods.getCompanyInfoId());
            sku.setPriceType(goods.getPriceType());
            sku.setLevelDiscountFlag(goods.getLevelDiscountFlag());
            sku.setCustomFlag(goods.getCustomFlag());
            sku.setStoreId(goods.getStoreId());
            sku.setAuditStatus(goods.getAuditStatus());
            sku.setCompanyType(goods.getCompanyType());
            sku.setAloneFlag(Boolean.FALSE);
            sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
            sku.setSaleType(goods.getSaleType());
            sku.setGoodsSource(goods.getGoodsSource());
            sku.setProviderId(goods.getProviderId());
            sku.setProviderGoodsInfoId(goods.getProviderGoodsId());
            sku.setGoodsSource(1);
            sku.setCreateTime(LocalDateTime.now());
            sku.setUpdateTime(LocalDateTime.now());
            sku.setAddedTime(LocalDateTime.now());
            sku.setAddedFlag(AddedFlag.YES.toValue());
            sku.setCompanyInfoId(goods.getCompanyInfoId());
            sku.setLevelDiscountFlag(Constants.no);
            sku.setCustomFlag(Constants.no);
            sku.setStoreId(goods.getStoreId());
            sku.setAuditStatus(CheckStatus.CHECKED);
            sku.setCompanyType(goods.getCompanyType());
            sku.setAloneFlag(Boolean.FALSE);
            sku.setSupplyPrice(goods.getSupplyPrice());
            sku.setGoodsInfoType(Integer.valueOf(0));
            sku.setSortNumKey(0);
            sku.setSortNumCate(0);

            // sku.setGoodsInfoBarcode(standardSku.getErpGoodsInfoNo());

            sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
            //默认销售类型 批发
            sku.setSaleType(SaleType.WHOLESALE.toValue());
            sku.setHostSku(sku.getHostSku());

            if (sku.getStock() == null) {
                sku.setStock(BigDecimal.ZERO);
            }
            sku.setDelFlag(goods.getDelFlag());
            if (!goods.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                sku.setAddedFlag(goods.getAddedFlag());
            }
            //新增商品询价标志，默认否；add by jiangxin 20210903
            if (sku.getInquiryFlag()==null){
                sku.setInquiryFlag(Constants.no);
            }
            //补齐erp编号
            sku.setErpGoodsInfoNo(erpNo);
            if (sku.getGoodsInfoBarcode().isEmpty()){
                sku.setGoodsInfoBarcode(sku.getErpGoodsInfoNo());
            }
            sku.setVipPrice(sku.getMarketPrice());
            sku.setSingleOrderPurchaseNum(sku.getSingleOrderPurchaseNum()==null?0:sku.getSingleOrderPurchaseNum());
            sku.setPurchaseNum(sku.getPurchaseNum()==null?-1:sku.getPurchaseNum());
            if (Objects.nonNull(goods) && null!=goods.getGoodDate()){
                sku.setGoodsInfoBatchNo(DateUtil.format(goods.getGoodDate(),DateUtil.FMT_DATE_1));
            }

            sku.setInquiryFlag(0);
            sku.setSortNumKey(0);
            sku.setSortNumCate(0);

            sku.setMarketingId(Long.valueOf(-1));

            if (null!=sku &&Objects.isNull(sku.getGoodsInfoUnit())){
                //如果不设置sku 的商品单位
                sku.setGoodsInfoUnit(goods.getGoodsUnit());
            }
            if (null!=sku &&Objects.isNull(sku.getGoodsInfoWeight())){
                //如果不设置sku 的商品重量
                sku.setGoodsInfoWeight(goods.getGoodsWeight());
            }
            if (null!=sku &&Objects.isNull(sku.getGoodsInfoCubage())){
                //如果不设置sku 的商品体积
                sku.setGoodsInfoCubage(goods.getGoodsCubage());
            }
            //判空
            if (Constants.yes.equals(sku.getHostSku())){
                BigDecimal addStep = sku.getAddStep();//步长
                if (Objects.isNull(addStep)){
                    addStep=BigDecimal.ONE;
                }
                BigDecimal subtitlePrice = sku.getMarketPrice().divide(addStep, 2, BigDecimal.ROUND_UP);
                //1箱(30.00杯x0.00元/杯)
                String goodsSubtitle = "1"+sku.getGoodsInfoUnit()+"=" + addStep + sku.getDevanningUnit() + "x" + subtitlePrice + "元/" +  sku.getDevanningUnit();
                String goodsSubtitleNew = subtitlePrice + "/" + sku.getDevanningUnit();
                goods.setGoodsSubtitle(goodsSubtitle);
                goods.setGoodsSubtitleNew(goodsSubtitleNew);
                if (Objects.nonNull(sku) && Objects.nonNull(sku.getCostPrice())){
                    goods.setCostPrice(sku.getCostPrice());
                }else {
                    goods.setCostPrice(sku.getMarketPrice());
                }
                goods.setGoodsWeight(sku.getGoodsInfoWeight());
                goods.setGoodsCubage(sku.getGoodsInfoCubage());
                goods.setGoodsUnit(sku.getGoodsInfoUnit());
                goods.setMarketPrice(sku.getMarketPrice());
                //此处更新goodsinfo信息
                goodsRepository.save(goods);
                flag=true;
            }
            String goodsInfoId = goodsInfoRepository.save( KsBeanUtil.copyPropertiesThird(sku,GoodsInfo.class)).getGoodsInfoId();
            sku.setGoodsInfoId(goodsInfoId);
            //更新映射关系
            GoodsInfoMapping byErpGoodsInfoNo = goodsInfoMappingService.findByErpGoodsInfoNo(sku.getErpGoodsInfoNo());
            if(Objects.nonNull(byErpGoodsInfoNo) && !goodsInfoId.equals(byErpGoodsInfoNo.getGoodsInfoId())){
                goodsInfoMappingService.updateGoodsInfoIdByErpNo(goodsInfoId,byErpGoodsInfoNo.getErpGoodsInfoNo());
                GoodsInfo updateSku = new GoodsInfo();
                updateSku.setGoodsInfoId(goodsInfoId);
                updateSku.setParentGoodsInfoId(byErpGoodsInfoNo.getParentGoodsInfoId());
                sku.setParentGoodsInfoId(byErpGoodsInfoNo.getParentGoodsInfoId());
            }else{
                //添加映射关系
                if (Objects.isNull(byErpGoodsInfoNo)){
                    GoodsInfoMapping mapping =new GoodsInfoMapping();
                    mapping.setGoodsInfoId(goodsInfoId);
                    mapping.setErpGoodsInfoNo(erpNo);
                    mapping.setWareId(sku.getWareId());
                    GoodsInfoMapping integer = goodsInfoMappingService.saveGoodsInfoMapping(mapping);
                    if (Objects.nonNull(integer)){
                        GoodsInfoMapping goodsInfoSku = goodsInfoMappingService.findByErpGoodsInfoNo(sku.getErpGoodsInfoNo());
                        GoodsInfo updateSku = new GoodsInfo();
                        updateSku.setGoodsInfoId(goodsInfoId);
                        updateSku.setParentGoodsInfoId(goodsInfoSku.getParentGoodsInfoId());
                        sku.setParentGoodsInfoId(goodsInfoSku.getParentGoodsInfoId());
                    }
                }

            }
            goodsInfoRepository.save(KsBeanUtil.copyPropertiesThird(sku,GoodsInfo.class));
            //拆箱商品更新
            DevanningGoodsInfo convert = KsBeanUtil.convert(sku, DevanningGoodsInfo.class);
            convert.setDivisorFlag(BigDecimal.ONE);
            //开始封装标题
            BigDecimal subtitlePrice = sku.getMarketPrice().divide(sku.getAddStep(), 2, BigDecimal.ROUND_UP);
            String goodsSubtitle = "1"+sku.getGoodsInfoUnit()+"=" + sku.getAddStep() + sku.getDevanningUnit() + "x" + subtitlePrice + "元/" +  sku.getDevanningUnit();
            String goodsSubtitleNew = subtitlePrice + "/" + sku.getDevanningUnit();
            convert.setGoodsInfoSubtitle(goodsSubtitle);
            convert.setGoodsSubtitleNew(goodsSubtitleNew);
            convert.setDevanningUnit(sku.getDevanningUnit());
            convert.setGoodsWeight(sku.getGoodsInfoWeight());
            convert.setGoodsCubage(sku.getGoodsInfoCubage());
            convert.setIsScatteredQuantitative(sku.getIsScatteredQuantitative()==null?0:sku.getIsScatteredQuantitative());
            convert.setSupplyPrice(BigDecimal.ZERO);
            convert.setVipPrice(sku.getVipPrice());
            convert.setProviderId(goods.getProviderId());
            convert.setGoodsInfoBatchNo(sku.getGoodsInfoBatchNo());
            convert.setSingleOrderPurchaseNum(sku.getSingleOrderPurchaseNum());
            convert.setPurchaseNum(sku.getPurchaseNum());
            convert.setWareId(sku.getWareId());
            convert.setHostSku(sku.getHostSku());
            convert.setAddStep(sku.getAddStep());
            devanningGoodsInfoRepository.save(convert);

            if (CollectionUtils.isNotEmpty(sku.getGoodsAttributeKeys())) {
                for (GoodsAttributeKeyVO g : sku.getGoodsAttributeKeys()) {
                    GoodsAttributeKey goodsAttributeKey =new GoodsAttributeKey();
                    goodsAttributeKey.setAttributeId(g.getAttributeId());
                    goodsAttributeKey.setGoodsInfoId(goodsInfoId);
                    goodsAttributeKey.setGoodsId(goodsId);
                    goodsAttributeKey.setGoodsAttributeValue(g.getGoodsAttributeValue());
                    goodsAttributeKey.setGoodsAttributeId(g.getAttributeId());
                    goodsAttributeKeyService.add(goodsAttributeKey);
                }
            }else{
                throw new SbcRuntimeException(GoodsErrorCode.ATTR_NO_EXIST,"规格信息不能为空！");
            }

            //新增商品库存信息
            GoodsWareStock goodsWareStock = new GoodsWareStock();
            goodsWareStock.setGoodsInfoId(goodsInfoId);
            goodsWareStock.setGoodsInfoNo(sku.getGoodsInfoNo());
            goodsWareStock.setGoodsId(sku.getGoodsId());
            goodsWareStock.setStoreId(sku.getStoreId());
            goodsWareStock.setStock(sku.getStock());
            goodsWareStock.setCreateTime(LocalDateTime.now());
            goodsWareStock.setUpdateTime(LocalDateTime.now());
            goodsWareStock.setWareId(sku.getWareId());
            goodsWareStock.setDelFlag(DeleteFlag.NO);
            goodsWareStock.setGoodsInfoWareId(goodsWareStock.getGoodsInfoId()+"_"+goodsWareStock.getWareId());
            goodsWareStock.setSaleType(sku.getSaleType());//销售类别(0:批发,1:零售,2散批)
            goodsWareStock.setAddStep(sku.getAddStep());
            if(SaleType.WHOLESALE.toValue()==goodsWareStock.getSaleType()){
                goodsWareStock.setMainAddStep(BigDecimal.ONE);
            }
            GoodsWareStock addStock = goodsWareStockService.add(goodsWareStock);
            if (Objects.nonNull(addStock)){
                GoodsWareStockDetail goodsWareStockDetail=new GoodsWareStockDetail();
                goodsWareStockDetail.setGoodsWareStockId(addStock.getId());
                goodsWareStockDetail.setStockImportNo(new StringBuilder("IM").append(RandomStringUtils.randomNumeric(6)).toString());
                goodsWareStockDetail.setGoodsInfoId(sku.getGoodsInfoId().toString());
                goodsWareStockDetail.setGoodsInfoNo(sku.getGoodsInfoNo());
                goodsWareStockDetail.setStock(sku.getStock().longValue());
                goodsWareStockDetail.setWareId(sku.getWareId());
                goodsWareStockDetail.setImportType(GoodsWareStockImportType.EDIT);
                goodsWareStockDetail.setOperateStock(sku.getStock().longValue());
                goodsWareStockDetail.setDelFlag(DeleteFlag.NO);
                //  goodsWareStockDetail.setCreatePerson();
                goodsWareStockDetail.setCreateTime(LocalDateTime.now());
                goodsWareStockDetailService.add(goodsWareStockDetail);
            }
            count++;
        }

        return flag;
    }

    /**
     * 商品新增
     *
     * @param saveRequest
     * @return SPU编号
     * @throws SbcRuntimeException
     */

    @Transactional
    public String merchantGoodsAdd(GoodsSaveRequest saveRequest) throws SbcRuntimeException {
        List<GoodsImage> goodsImages = saveRequest.getImages();
        List<GoodsInfo> goodsInfos = saveRequest.getGoodsInfos();
        Goods goods = saveRequest.getGoods();

        //验证SPU编码重复
        GoodsQueryRequest queryRequest = new GoodsQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsNo(goods.getGoodsNo());
        if (goodsRepository.count(queryRequest.getWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsErrorCode.SPU_NO_EXIST);
        }

        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsInfoNos(goodsInfos.stream().map(GoodsInfo::getGoodsInfoNo).distinct().collect(Collectors.toList()));

        //如果SKU数据有重复
        if (goodsInfos.size() != infoQueryRequest.getGoodsInfoNos().size()) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_EXIST);
        }

        //验证SKU编码重复
        List<GoodsInfo> goodsInfosList = goodsInfoRepository.findAll(infoQueryRequest.getWhereCriteria());
        if (CollectionUtils.isNotEmpty(goodsInfosList)) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_H_EXIST, new Object[]{StringUtils.join(goodsInfosList.stream().map(GoodsInfo::getGoodsInfoNo).collect(Collectors.toList()), ",")});
        }

        if (CollectionUtils.isEmpty(goods.getStoreCateIds())){
            //店铺分类未配置
            List<StoreCate> storeCateList = storeCateRepository.findAll(
                    StoreCateQueryRequest.builder()
                            .storeId(goods.getStoreId())
                            .delFlag(DeleteFlag.NO)
                            .isDefault(DefaultFlag.YES).build().getWhereCriteria());
            if(CollectionUtils.isEmpty(storeCateList)){
                throw new SbcRuntimeException(GoodsCateErrorCode.DEFAULT_CATE_NOT_EXIST);
            }
            Long defaultStoreCateId = storeCateList.get(0).getStoreCateId();
            goods.setStoreCateIds(Collections.singletonList(defaultStoreCateId));
        }
        //验证商品相关基础数据
        this.checkBasic(goods);
        LocalDateTime now = LocalDateTime.now();
        goods.setDelFlag(DeleteFlag.NO);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(goods.getCreateTime());
        goods.setAddedTime(goods.getCreateTime());
        goods.setDelFlag(DeleteFlag.NO);
        goods.setCreateTime(now);
        goods.setUpdateTime(now);
        goods.setAddedTime(now);
        goods.setSubmitTime(now);
        goods.setAuditStatus(CheckStatus.CHECKED);
        goods.setCustomFlag(Constants.no);
        goods.setLevelDiscountFlag(Constants.no);
        //仓库默认设置为1
        goods.setWareId(Constants.yes.longValue());
        goods.setPurchaseNum(goods.getPurchaseNum());
        goods.setSupplierName(goods.getSupplierName());

        goods.setSaleType(SaleType.WHOLESALE.toValue());


        if (Objects.isNull(goods.getPriceType())) {
            goods.setPriceType(GoodsPriceType.MARKET.toValue());
        }
        if (goods.getAddedFlag() == null) {
            goods.setAddedFlag(AddedFlag.YES.toValue());
        }
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            goods.setGoodsImg(goodsImages.get(0).getArtworkUrl());
        }
        if (goods.getMoreSpecFlag() == null) {
            goods.setMoreSpecFlag(Constants.no);
        }
        if (goods.getCustomFlag() == null) {
            goods.setCustomFlag(Constants.no);
        }
        if (goods.getGoodsViewNum() == null) {
            goods.setGoodsViewNum(Long.valueOf(Constants.no));
        }
        if (goods.getProviderId() == null) {
            goods.setProviderId(Long.valueOf(Constants.no));
        }
        if (goods.getProviderGoodsId() == null) {
            goods.setProviderGoodsId("");
        }
        if (goods.getLevelDiscountFlag() == null) {
            goods.setLevelDiscountFlag(Constants.no);
        }
        if (goods.getGoodsCollectNum() == null) {
            goods.setGoodsCollectNum(0L);
        }
        if (goods.getGoodsSalesNum() == null) {
            goods.setGoodsSalesNum(0L);
        }
        if (goods.getGoodsEvaluateNum() == null) {
            goods.setGoodsEvaluateNum(0L);
        }
        if (goods.getGoodsFavorableCommentNum() == null) {
            goods.setGoodsFavorableCommentNum(0L);
        }
        goods.setPriceType(GoodsPriceType.MARKET.toValue());
        goods.setAllowPriceSet(1);
        //初始化商品对应的数量（收藏、销量、评论数、好评数）
        if(goods.getGoodsCollectNum() == null){
            goods.setGoodsCollectNum(0L);
        }
        if(goods.getGoodsSalesNum() == null){
            goods.setGoodsSalesNum(0L);
        }
        if(goods.getGoodsEvaluateNum() == null){
            goods.setGoodsEvaluateNum(0L);
        }
        if(goods.getGoodsFavorableCommentNum() == null){
            goods.setGoodsFavorableCommentNum(0L);
        }
        goods.setVipPrice(goods.getMarketPrice());
        goodsCommonService.setStoreCheckState(goods);

        //spu 显示 主spu 的价格
        goodsInfos.forEach(goodsInfo -> {
            if (goodsInfo.getHostSku()==Constants.yes){
                BigDecimal addStep = goodsInfo.getAddStep();//步长
                if (Objects.isNull(addStep)){
                    addStep=BigDecimal.ONE;
                }

                BigDecimal subtitlePrice = goodsInfo.getMarketPrice().divide(addStep, 2, BigDecimal.ROUND_UP);
                //1箱(30.00杯x0.00元/杯)
                String goodsSubtitle = "1"+goodsInfo.getGoodsInfoUnit()+"=" + addStep + goodsInfo.getDevanningUnit() + "x" + subtitlePrice + "元/" +  goodsInfo.getDevanningUnit();
                String goodsSubtitleNew = subtitlePrice + "/" + goodsInfo.getDevanningUnit();
                goods.setGoodsSubtitle(goodsSubtitle);
                goods.setGoodsSubtitleNew(goodsSubtitleNew);
                if (Objects.nonNull(goodsInfo) && Objects.nonNull(goodsInfo.getCostPrice())){
                    goods.setCostPrice(goodsInfo.getCostPrice());
                }else {
                    goods.setCostPrice(goodsInfo.getMarketPrice());
                }
                goods.setGoodsWeight(goodsInfo.getGoodsInfoWeight());
                goods.setGoodsCubage(goodsInfo.getGoodsInfoCubage());
                goods.setGoodsUnit(goodsInfo.getGoodsInfoUnit());
                goods.setMarketPrice(goodsInfo.getMarketPrice());

            }
        });

        //erp编码只要一个sku相同
        //String erpNo = getErpNo(goods.getCateId(), goods.getStoreId());

        final String goodsId = goodsRepository.save(goods).getGoodsId();

        //新增图片
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            goodsImages.forEach(goodsImage -> {
                goodsImage.setCreateTime(goods.getCreateTime());
                goodsImage.setUpdateTime(goods.getUpdateTime());
                goodsImage.setGoodsId(goodsId);
                goodsImage.setDelFlag(DeleteFlag.NO);
                goodsImage.setImageId(goodsImageRepository.save(goodsImage).getImageId());
            });
        }

        if (osUtil.isS2b() && CollectionUtils.isNotEmpty(goods.getStoreCateIds())) {
            goods.getStoreCateIds().forEach(cateId -> {
                StoreCateGoodsRela rela = new StoreCateGoodsRela();
                rela.setGoodsId(goodsId);
                rela.setStoreCateId(cateId);
                storeCateGoodsRelaRepository.save(rela);
            });
        }
        int count=1;
        for (GoodsInfo sku : goodsInfos) {
            //为了支持erp 下单需要不同erp
           String erpNo = getErpNo(goods.getCateId(), goods.getStoreId(),count);
            if (sku.getStock() == null) {
                sku.setStock(BigDecimal.ZERO);
            }

            sku.setCateId(goods.getCateId());
            sku.setBrandId(goods.getBrandId());
            sku.setGoodsId(goodsId);
            sku.setWareId(goods.getWareId());
            sku.setGoodsInfoName(goods.getGoodsName());
            if (Objects.nonNull(sku) && Objects.nonNull(sku.getCostPrice())){
                sku.setCostPrice(sku.getCostPrice());
            }else {
                sku.setCostPrice(sku.getMarketPrice());
            }

            sku.setCreateTime(goods.getCreateTime());
            sku.setUpdateTime(goods.getUpdateTime());
            sku.setAddedTime(goods.getAddedTime());
            sku.setDelFlag(goods.getDelFlag());
            if (!goods.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                sku.setAddedFlag(goods.getAddedFlag());
            }
            sku.setCompanyInfoId(goods.getCompanyInfoId());
            sku.setPriceType(goods.getPriceType());
            sku.setLevelDiscountFlag(goods.getLevelDiscountFlag());
            sku.setCustomFlag(goods.getCustomFlag());
            sku.setStoreId(goods.getStoreId());
            sku.setAuditStatus(goods.getAuditStatus());
            sku.setCompanyType(goods.getCompanyType());
            sku.setAloneFlag(Boolean.FALSE);
            sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
            sku.setSaleType(goods.getSaleType());
            sku.setGoodsSource(goods.getGoodsSource());
            sku.setProviderId(goods.getProviderId());
            sku.setProviderGoodsInfoId(goods.getProviderGoodsId());
            sku.setGoodsSource(1);
            sku.setCreateTime(now);
            sku.setUpdateTime(now);
            sku.setAddedTime(now);
            sku.setAddedFlag(AddedFlag.NO.toValue());
            sku.setCompanyInfoId(goods.getCompanyInfoId());
            sku.setLevelDiscountFlag(Constants.no);
            sku.setCustomFlag(Constants.no);
            sku.setStoreId(goods.getStoreId());
            sku.setAuditStatus(CheckStatus.CHECKED);
            sku.setCompanyType(goods.getCompanyType());
            sku.setAloneFlag(Boolean.FALSE);
            sku.setSupplyPrice(goods.getSupplyPrice());
            sku.setGoodsInfoType(Integer.valueOf(0));
            sku.setSortNumKey(0);
            sku.setSortNumCate(0);

          // sku.setGoodsInfoBarcode(standardSku.getErpGoodsInfoNo());

            sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
            //默认销售类型 批发
            sku.setSaleType(SaleType.WHOLESALE.toValue());
            sku.setHostSku(sku.getHostSku());

            if (sku.getStock() == null) {
                sku.setStock(BigDecimal.ZERO);
            }
            sku.setDelFlag(goods.getDelFlag());
            if (!goods.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                sku.setAddedFlag(goods.getAddedFlag());
            }
            //新增商品询价标志，默认否；add by jiangxin 20210903
            if (sku.getInquiryFlag()==null){
                sku.setInquiryFlag(Constants.no);
            }
            //补齐erp编号
            sku.setErpGoodsInfoNo(erpNo);
            if (sku.getGoodsInfoBarcode().isEmpty()){
                sku.setGoodsInfoBarcode(sku.getErpGoodsInfoNo());
            }
            sku.setVipPrice(sku.getMarketPrice());
            sku.setSingleOrderPurchaseNum(sku.getSingleOrderPurchaseNum()==null?0:sku.getSingleOrderPurchaseNum());
            sku.setPurchaseNum(sku.getPurchaseNum()==null?-1:sku.getPurchaseNum());
            if (Objects.nonNull(goods) && null!=goods.getGoodDate()){
                sku.setGoodsInfoBatchNo(DateUtil.format(goods.getGoodDate(),DateUtil.FMT_DATE_1));
            }

            sku.setInquiryFlag(0);
            sku.setSortNumKey(0);
            sku.setSortNumCate(0);

            sku.setMarketingId(Long.valueOf(-1));
//            if (Objects.nonNull(sku) && Objects.nonNull(sku.getCostPrice())){
//                sku.setCostPrice(new BigDecimal(Math.round(sku.getCostPrice().doubleValue())));
//            }else {
//                sku.setCostPrice(new BigDecimal(Math.round(sku.getMarketPrice().doubleValue())));
//            }
            if (null!=sku &&Objects.isNull(sku.getGoodsInfoUnit())){
                //如果不设置sku 的商品单位
                sku.setGoodsInfoUnit(goods.getGoodsUnit());
            }
            if (null!=sku &&Objects.isNull(sku.getGoodsInfoWeight())){
                //如果不设置sku 的商品重量
                sku.setGoodsInfoWeight(goods.getGoodsWeight());
            }
            if (null!=sku &&Objects.isNull(sku.getGoodsInfoCubage())){
                //如果不设置sku 的商品体积
                sku.setGoodsInfoCubage(goods.getGoodsCubage());
            }
            sku.setIsScatteredQuantitative(sku.getIsScatteredQuantitative()==null?0:sku.getIsScatteredQuantitative());
            String goodsInfoId = goodsInfoRepository.save(sku).getGoodsInfoId();
            sku.setGoodsInfoId(goodsInfoId);
            //更新映射关系
            GoodsInfoMapping byErpGoodsInfoNo = goodsInfoMappingService.findByErpGoodsInfoNo(sku.getErpGoodsInfoNo());
            if(Objects.nonNull(byErpGoodsInfoNo) && !goodsInfoId.equals(byErpGoodsInfoNo.getGoodsInfoId())){
                goodsInfoMappingService.updateGoodsInfoIdByErpNo(goodsInfoId,byErpGoodsInfoNo.getErpGoodsInfoNo());
                GoodsInfo updateSku = new GoodsInfo();
                updateSku.setGoodsInfoId(goodsInfoId);
                updateSku.setParentGoodsInfoId(byErpGoodsInfoNo.getParentGoodsInfoId());
                sku.setParentGoodsInfoId(byErpGoodsInfoNo.getParentGoodsInfoId());
            }else{
                //添加映射关系
                if (Objects.isNull(byErpGoodsInfoNo)){
                    GoodsInfoMapping mapping =new GoodsInfoMapping();
                    mapping.setGoodsInfoId(goodsInfoId);
                    mapping.setErpGoodsInfoNo(erpNo);
                    mapping.setWareId(sku.getWareId());
                    GoodsInfoMapping integer = goodsInfoMappingService.saveGoodsInfoMapping(mapping);
                    if (Objects.nonNull(integer)){
                        GoodsInfoMapping goodsInfoSku = goodsInfoMappingService.findByErpGoodsInfoNo(sku.getErpGoodsInfoNo());
                        GoodsInfo updateSku = new GoodsInfo();
                        updateSku.setGoodsInfoId(goodsInfoId);
                        updateSku.setParentGoodsInfoId(goodsInfoSku.getParentGoodsInfoId());
                        sku.setParentGoodsInfoId(goodsInfoSku.getParentGoodsInfoId());
                    }
                }
            }

            //拆箱商品更新
            DevanningGoodsInfo convert = KsBeanUtil.convert(sku, DevanningGoodsInfo.class);
                convert.setDivisorFlag(BigDecimal.ONE);
                //开始封装标题
//                String  goodsSubtitle= getGoodsSubtitle(sku.getGoodsAttributeKeys());
//                if (Objects.nonNull(goodsSubtitle)){
//                    convert.setGoodsInfoSubtitle(goodsSubtitle);
//                }

//                 convert.setGoodsSubtitleNew(sku.getMarketPrice()+"/"+sku.getGoodsInfoUnit());
//                 convert.setGoodsUnit(sku.getGoodsInfoUnit());
            //1箱(30.00杯x0.00元/杯)
            BigDecimal subtitlePrice = sku.getMarketPrice().divide(sku.getAddStep(), 2, BigDecimal.ROUND_UP);
                String goodsSubtitle = "1"+sku.getGoodsInfoUnit()+"=" + sku.getAddStep() + sku.getDevanningUnit() + "x" + subtitlePrice + "元/" +  sku.getDevanningUnit();
               String goodsSubtitleNew = subtitlePrice + "/" + sku.getDevanningUnit();
                 convert.setDevanningUnit(sku.getDevanningUnit());
                 convert.setGoodsWeight(sku.getGoodsInfoWeight());
                 convert.setGoodsCubage(sku.getGoodsInfoCubage());
                 convert.setIsScatteredQuantitative(sku.getIsScatteredQuantitative()==null?0:sku.getIsScatteredQuantitative());
                 convert.setSupplyPrice(BigDecimal.ZERO);
                 convert.setVipPrice(sku.getVipPrice());
                 convert.setProviderId(goods.getProviderId());
                 convert.setGoodsInfoBatchNo(sku.getGoodsInfoBatchNo());
                 convert.setSingleOrderPurchaseNum(sku.getSingleOrderPurchaseNum());
                  convert.setPurchaseNum(sku.getPurchaseNum());
                  convert.setWareId(sku.getWareId());
                  convert.setHostSku(sku.getHostSku());
                 convert.setAddStep(sku.getAddStep());
                 convert.setGoodsInfoSubtitle(goodsSubtitle);
                convert.setGoodsSubtitleNew(goodsSubtitleNew);
                devanningGoodsInfoRepository.save(convert);

                if (CollectionUtils.isNotEmpty(sku.getGoodsAttributeKeys())) {
                    for (GoodsAttributeKey spec : sku.getGoodsAttributeKeys()) {
                        spec.setGoodsInfoId(goodsInfoId);
                        spec.setGoodsId(goodsId);
                        spec.setGoodsAttributeId(spec.getAttributeId());
                        spec.setAttributeId(null);
                        spec.setGoodsAttributeValue(goodsSubtitle);
                        goodsAttributeKeyService.add(spec);
                    }
                }else{
                    throw new SbcRuntimeException(GoodsErrorCode.ATTR_NO_EXIST,"规格信息不能为空！");
                }

                //新增商品库存信息
                GoodsWareStock goodsWareStock = new GoodsWareStock();
                goodsWareStock.setGoodsInfoId(goodsInfoId);
                goodsWareStock.setGoodsInfoNo(sku.getGoodsInfoNo());
                goodsWareStock.setGoodsId(sku.getGoodsId());
                goodsWareStock.setStoreId(sku.getStoreId());
                goodsWareStock.setStock(sku.getStock());
                goodsWareStock.setCreateTime(now);
                goodsWareStock.setUpdateTime(now);
                goodsWareStock.setWareId(sku.getWareId());
                goodsWareStock.setDelFlag(DeleteFlag.NO);
                goodsWareStock.setGoodsInfoWareId(goodsWareStock.getGoodsInfoId()+"_"+goodsWareStock.getWareId());
                goodsWareStock.setAddStep(sku.getAddStep());//相对最小单位的换算率
                goodsWareStock.setSaleType(sku.getSaleType());//销售类别(0:批发,1:零售,2散批)
                goodsWareStock.setAddStep(sku.getAddStep());
                if(SaleType.WHOLESALE.toValue()==goodsWareStock.getSaleType()){
                    goodsWareStock.setMainAddStep(BigDecimal.ONE);
                }
                GoodsWareStock addStock = goodsWareStockService.add(goodsWareStock);
                if (Objects.nonNull(addStock)){
                    GoodsWareStockDetail goodsWareStockDetail=new GoodsWareStockDetail();
                    goodsWareStockDetail.setGoodsWareStockId(addStock.getId());
                    goodsWareStockDetail.setStockImportNo(new StringBuilder("IM").append(RandomStringUtils.randomNumeric(6)).toString());
                    goodsWareStockDetail.setGoodsInfoId(sku.getGoodsInfoId().toString());
                    goodsWareStockDetail.setGoodsInfoNo(sku.getGoodsInfoNo());
                    goodsWareStockDetail.setStock(sku.getStock().longValue());
                    goodsWareStockDetail.setWareId(sku.getWareId());
                    goodsWareStockDetail.setImportType(GoodsWareStockImportType.EDIT);
                    goodsWareStockDetail.setOperateStock(sku.getStock().longValue());
                    goodsWareStockDetail.setDelFlag(DeleteFlag.NO);
                  //  goodsWareStockDetail.setCreatePerson();
                    goodsWareStockDetail.setCreateTime(LocalDateTime.now());
                    goodsWareStockDetailService.add(goodsWareStockDetail);
                }
            count++;
        }

        return goodsId;
    }


    /**
     *
     * 获取副标题信息
     * */
    private String getGoodsSubtitle(List<GoodsAttributeKey> goodsAttributeKeys) {

        StringBuilder builder=new StringBuilder();
        if (CollectionUtils.isNotEmpty(goodsAttributeKeys)){
            goodsAttributeKeys.forEach(goodsAttributeKey -> {
                builder.append(goodsAttributeKey.getGoodsAttributeValue()+" ");
            });
            return builder.toString().trim();
        }
        return null;
    }
    /**
     *
     * 本地生产erp编码
     * 一级分类-二级分类-商品数量-店铺id
     * */

   private String getErpNo(long cateId,long storeId,int count){

        StringBuffer buffer=new StringBuffer();

       GoodsCate info = goodsCateService.findById(cateId);

       String[] catePath = info.getCatePath().split(Constants.CATE_PATH_SPLITTER);

           //添加两级分类
           buffer.append(catePath[Constants.yes]);
           buffer.append(Constants.STRING_SLASH_HENG);
           buffer.append(catePath[Constants.IMPORT_GOODS_MAX_SIZE]);
           buffer.append(Constants.STRING_SLASH_HENG);
          List<GoodsInfo> allList = goodsInfoRepository.findAll((GoodsInfoQueryRequest.builder().storeId(storeId).build().getWhereCriteria()));
           String newADD = String.valueOf(String.format("%0"+3+"d",allList.size()+count));
           buffer.append(newADD);
           buffer.append(Constants.STRING_SLASH_HENG);
           buffer.append(storeId);

       return buffer.toString();
   }

    public List<GoodsStoreOnSaleResponse> listStoreOnSaleGoodsNum(List<Long> storeIds) {
        List<GoodsStoreOnSaleResponse> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(storeIds)) return list;
        final List<Map<String, Object>> objects = goodsRepository.listStoreOnSaleGoodsNum(storeIds);
        if (CollectionUtils.isNotEmpty(objects)) {
            list = objects.stream().map(o ->
                    new GoodsStoreOnSaleResponse(Long.valueOf(o.get("store_id").toString()), Integer.valueOf(o.get("num").toString()))
            ).collect(Collectors.toList());
        }
        return list;
    }

    public List<String> listRecentAddedNewGoods() {
        List<String> goodsIds = new ArrayList<>();
        int recentDay = 3;
        Integer limitNum = 200;
        final List<Map<String, Object>> objects = goodsRepository.listRecentAddedNewGoods(LocalDateTime.of(LocalDate.now().plusDays(-recentDay), LocalTime.MIN), limitNum);
        if (CollectionUtils.isNotEmpty(objects)) {
            objects.forEach(o -> {
                final Object o1 = o.get("goods_id");
                if (Objects.nonNull(o1)) {
                    goodsIds.add(o1.toString());
                }
            });
        }
        return goodsIds;
    }


//   /**
//    * 下架的情况
//    * @param sku
//    * */
//   private void recommendGoodsOrder(String sku){
//       String key1 = CacheKeyConstant.SCREEN_ORDER_ADD_LAST_USERID+sku+SpecialSymbols.UNDERLINE.toValue();
//       String userId = redisService.getString(key1);
//       if (Objects.nonNull(userId)){
//           String key = CacheKeyConstant.SCREEN_ORDER_ADD_LAST_TIME+userId+ SpecialSymbols.UNDERLINE.toValue();
//       }
//
//
//   }

}
