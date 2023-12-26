package com.wanmi.sbc.es.elastic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageRequest;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.es.elastic.model.root.*;
import com.wanmi.sbc.es.elastic.request.*;
import com.wanmi.sbc.es.elastic.response.EsGoodsBaseResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsInfoResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.es.elastic.response.EsSearchResponse;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsattributekey.GoodsAttributeKeyQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodslabel.GoodsLabelQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodslabelrela.GoodsLabelRelaQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsCustomerPriceQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsLevelPriceQueryProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsInfoSpecDetailRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsSpecQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateGoodsRelaQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelByIdRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelPageRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsPropDetailRelByIdsRequest;
import com.wanmi.sbc.goods.api.request.goodsattributekey.GoodsAttributeKeyQueryRequest;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelListRequest;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.request.price.GoodsCustomerPriceBySkuIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceListBySkuIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsLevelPriceBySkuIdsRequest;
import com.wanmi.sbc.goods.api.request.spec.GoodsInfoSpecDetailRelBySkuIdsRequest;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateGoodsRelaListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByStoreCateIdAndIsHaveSelfRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListByStoreIdRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.api.response.goodsattributekey.GoodsAttributeKeyListResponse;
import com.wanmi.sbc.goods.api.response.goodslabelrela.GoodsLabelRelaByGoodsIdsResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByStoreCateIdAndIsHaveSelfResponse;
import com.wanmi.sbc.goods.bean.dto.BatchEnterPrisePriceDTO;
import com.wanmi.sbc.goods.bean.dto.DistributionGoodsInfoModifyDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.distribution.DistributionSettingQueryProvider;
import com.wanmi.sbc.marketing.api.request.distribution.DistributionStoreSettingListByStoreIdsRequest;
import com.wanmi.sbc.marketing.bean.vo.DistributionStoreSettingVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * ES商品信息数据源操作
 * Created by daiyitian on 2017/4/21.
 */
@Service
@Slf4j
public class EsGoodsInfoElasticService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private EsGoodsInfoElasticRepository esGoodsInfoElasticRepository;

    @Autowired
    private EsGoodsElasticRepository esGoodsElasticRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private GoodsIntervalPriceQueryProvider goodsIntervalPriceQueryProvider;

    @Autowired
    private GoodsLevelPriceQueryProvider goodsLevelPriceQueryProvider;

    @Autowired
    private GoodsCustomerPriceQueryProvider goodsCustomerPriceQueryProvider;

    @Autowired
    private GoodsInfoSpecDetailRelQueryProvider goodsInfoSpecDetailRelQueryProvider;

    @Autowired
    private GoodsSpecQueryProvider goodsSpecQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private ResultsMapper resultsMapper;

    @Autowired
    private EsCateBrandService esCateBrandService;

    @Autowired
    private EsCateBrandRepository esCateBrandRepository;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private StoreCateGoodsRelaQueryProvider storeCateGoodsRelaQueryProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private Client client;

    @Autowired
    private DistributionSettingQueryProvider distributionSettingQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private GoodsLabelQueryProvider goodsLabelQueryProvider;

    @Autowired
    private GoodsLabelRelaQueryProvider goodsLabelRelaQueryProvider;
    @Autowired
    private CateBrandSortRelQueryProvider cateBrandSortRelQueryProvider;
    @Autowired
    private GoodsAttributeKeyQueryProvider goodsAttributeKeyQueryProvider;
    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;
    /**
     * ES价格排序脚本
     */
    private final String PRICE_ORDER_SCRIPT =
            "def priceType=doc['goodsInfo.priceType'].value;" +
                    "def levelDiscountFlag=doc['goodsInfo.levelDiscountFlag'].value;" +
                    "def marketPrice=doc['goodsInfo.marketPrice'].value;" +
                    "if(priceType&&priceType==1){" +
                    "if(levelDiscountFlag && levelDiscountFlag == 1 && levelDiscount){" +
                    "return doc['goodsInfo.intervalMinPrice'].value * levelDiscount;" +
                    "};" +
                    "return doc['goodsInfo.intervalMinPrice'].value;" +
                    "};" +
                    "def customerPrices=_source.customerPrices;" +
                    "if(customerPrices && customerPrices.size()>0){" +
                    "for (cp in customerPrices){" +
                    "if(cp.customerId==customerId){return cp.price;};" +
                    "};" +
                    "};" +
                    "def goodsLevelPrices=_source.goodsLevelPrices;" +
                    "if(goodsLevelPrices && goodsLevelPrices.size()>0){" +
                    "for (lp in goodsLevelPrices){" +
                    "if(lp.levelId==levelId){return lp.price;};" +
                    "};" +
                    "};" +
                    "if(levelDiscount && levelDiscount > 0){" +
                    "return marketPrice * levelDiscount;" +
                    "};" +
                    "return marketPrice";

    /**
     * 分页查询ES商品(实现WEB的商品列表)
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsInfoResponse page(EsGoodsInfoQueryRequest queryRequest) {
        EsGoodsInfoResponse goodsInfoResponse = EsGoodsInfoResponse.builder().build();
        EsSearchResponse response = getEsBaseInfoByParams(queryRequest);
        if (response.getData().size() < 1) {
            goodsInfoResponse.setEsGoodsInfoPage(new PageImpl<>(response.getData(),
                    PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
            return goodsInfoResponse;
        }

        List<String> goodsIds =
                response.getData().stream().map(EsGoodsInfo::getGoodsInfo).map(GoodsInfoNest::getGoodsId).distinct().collect(Collectors.toList());
        List<String> skuIds =
                response.getData().stream().map(EsGoodsInfo::getGoodsInfo).map(GoodsInfoNest::getGoodsInfoId).distinct().collect(Collectors.toList());
        //批量查询SPU数据
        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        goodsQueryRequest.setGoodsSource(1);
        List<GoodsVO> goodses = goodsQueryProvider.listByCondition(goodsQueryRequest).getContext().getGoodsVOList();


        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
                GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
        ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));


        //批量查询规格值表
        Map<String, List<GoodsInfoSpecDetailRelVO>> detailRels =
                goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(skuIds)).getContext().getGoodsInfoSpecDetailRelVOList().stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRelVO::getGoodsInfoId));
        List<EsGoodsInfo> esGoodsInfoList = new LinkedList<>();
        for (EsGoodsInfo esGoodsInfo : response.getData()) {
            GoodsInfoNest goodsInfo = esGoodsInfo.getGoodsInfo();
            //排除分销商品，错误数据
            if (Objects.nonNull(goodsInfo.getDistributionGoodsAudit()) && goodsInfo.getDistributionGoodsAudit() == DistributionGoodsAudit.CHECKED && Objects.isNull(goodsInfo.getDistributionCommission())) {
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                continue;
            }
            goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO :
                    goodsInfo.getMarketPrice());
            Optional<GoodsVO> goodsOptional =
                    goodses.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst();
            if (goodsOptional.isPresent()) {
                GoodsVO goods = goodsOptional.get();
                goodsInfo.setPriceType(goods.getPriceType());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                //填充规格值
                if (Constants.yes.equals(goods.getMoreSpecFlag()) && MapUtils.isNotEmpty(detailRels) && detailRels.containsKey(goodsInfo.getGoodsInfoId())) {
                    goodsInfo.setSpecText(detailRels.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsInfoSpecDetailRelVO::getDetailName).collect(Collectors.joining(" ")));
                }
                goodsInfo.setStock(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getStock());
                //填充询问底价标志
                goodsInfo.setInquiryFlag(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getInquiryFlag());
                if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ZERO) < 1) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }

            //设置库存 删除 在上面已查询商品库存中间表填充商品库存
            if(CollectionUtils.isNotEmpty(esGoodsInfo.getGoodsInfo().getGoodsWareStockVOS())){
                List<GoodsWareStockVO> goodsWareStockVOS = esGoodsInfo.getGoodsInfo().getGoodsWareStockVOS();
                //聚合库存数据
                if (CollectionUtils.isNotEmpty(goodsWareStockVOS)){
                    BigDecimal stockSum = BigDecimal.ZERO;
                    if(queryRequest.getWareIdApp() != null){
                        stockSum = goodsWareStockVOS.stream().filter(g->g.getWareId().equals(queryRequest.getWareId()))
                                .map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }else{
                        stockSum = goodsWareStockVOS.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }

                    if (Objects.nonNull(queryRequest.getMatchWareHouseFlag())&&!queryRequest.getMatchWareHouseFlag()){
                        List<GoodsWareStockVO> collect = goodsWareStockVOS.stream().filter(param ->
                                WareHouseType.STORRWAREHOUSE.equals(param.getWareHouseType())).collect(Collectors.toList());
                        stockSum=collect.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    goodsInfo.setStock(stockSum);
                    if (Objects.nonNull(goodsInfo.getStock()) && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    }else{
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                }else{
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            }
            esGoodsInfoList.add(esGoodsInfo);
        }
        response.setData(esGoodsInfoList);

        //提取聚合数据
        EsGoodsBaseResponse baseResponse = extractBrands(response);
        goodsInfoResponse.setBrands(baseResponse.getBrands());

        if (queryRequest.isCateAggFlag()) {
            //提取聚合数据
            baseResponse = extractGoodsCate(response);
            goodsInfoResponse.setCateList(baseResponse.getCateList());
        }

        //提取规格与规格值聚合数据
        baseResponse = extractGoodsSpecsAndSpecDetails(response);
        goodsInfoResponse.setGoodsSpecs(baseResponse.getGoodsSpecs());
        goodsInfoResponse.setGoodsSpecDetails(baseResponse.getGoodsSpecDetails());
        goodsInfoResponse.setEsGoodsInfoPage(new PageImpl<>(response.getData(),
                PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
        goodsInfoResponse.setGoodsList(goodses);
        return goodsInfoResponse;
    }

    /**
     * 我的店铺（店铺精选页）
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsInfoResponse distributorGoodsListByCustomerId(EsGoodsInfoQueryRequest queryRequest) {
        EsGoodsInfoResponse goodsInfoResponse = EsGoodsInfoResponse.builder().build();

        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("brand_group").field("goodsBrand.brandId"));
        log.info("Es参数distributorGoodsListByCustomerId: {}", JSON.toJSONString(queryRequest));
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.build(searchResponse, resultsMapper));

        if (response.getData().size() < 1) {
            goodsInfoResponse.setEsGoodsInfoPage(new PageImpl<>(response.getData(),
                    PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
            return goodsInfoResponse;
        }

        List<String> goodsInfoIds = queryRequest.getGoodsInfoIds();

        List<String> goodsIds =
                response.getData().stream().map(EsGoodsInfo::getGoodsInfo).map(GoodsInfoNest::getGoodsId).distinct().collect(Collectors.toList());
        List<String> skuIds =
                response.getData().stream().map(EsGoodsInfo::getGoodsInfo).map(GoodsInfoNest::getGoodsInfoId).distinct().collect(Collectors.toList());
        //批量查询SPU数据
        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        goodsQueryRequest.setGoodsSource(1);
        List<GoodsVO> goodses = goodsQueryProvider.listByCondition(goodsQueryRequest).getContext().getGoodsVOList();

        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
                GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
        ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));

        //批量查询规格值表
        Map<String, List<GoodsInfoSpecDetailRelVO>> detailRels =
                goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(skuIds)).getContext().getGoodsInfoSpecDetailRelVOList().stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRelVO::getGoodsInfoId));
        List<EsGoodsInfo> data = response.getData();
        List<EsGoodsInfo> resultList = new LinkedList<>();
        for (String goodsInfoId : goodsInfoIds) {
            for (EsGoodsInfo esGoodsInfo : data) {
                GoodsInfoNest goodsInfo = esGoodsInfo.getGoodsInfo();
                if (Objects.isNull(goodsInfo) || Objects.nonNull(goodsInfo) && !goodsInfoId.equals(goodsInfo.getGoodsInfoId())) {
                    continue;
                }
                goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO :
                        goodsInfo.getMarketPrice());
                Optional<GoodsVO> goodsOptional =
                        goodses.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst();
                if (goodsOptional.isPresent()) {
                    GoodsVO goods = goodsOptional.get();
                    //为空，则以商品主图
                    if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                        goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                    }
                    //填充规格值
                    if (Constants.yes.equals(goods.getMoreSpecFlag()) && MapUtils.isNotEmpty(detailRels) && detailRels.containsKey(goodsInfo.getGoodsInfoId())) {
                        goodsInfo.setSpecText(detailRels.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsInfoSpecDetailRelVO::getDetailName).collect(Collectors.joining(" ")));
                    }
                    goodsInfo.setStock(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getStock());

                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {//不存在，则做为删除标记
                    goodsInfo.setDelFlag(DeleteFlag.YES);
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
                resultList.add(esGoodsInfo);
            }
        }

        response.setData(resultList);

        //提取聚合数据
        EsGoodsBaseResponse baseResponse = extractBrands(response);
        goodsInfoResponse.setBrands(baseResponse.getBrands());

        goodsInfoResponse.setEsGoodsInfoPage(new PageImpl<>(response.getData(),
                PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
        goodsInfoResponse.setGoodsList(goodses);
        return goodsInfoResponse;
    }

    /**
     * 分销员-我的店铺-选品功能
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsInfoResponse distributorGoodsList(EsGoodsInfoQueryRequest queryRequest, List<String> goodsIdList) {
        EsGoodsInfoResponse goodsInfoResponse = EsGoodsInfoResponse.builder().build();
        EsSearchResponse response = getEsBaseInfoByParams(queryRequest);
        if (response.getData().size() < 1) {
            goodsInfoResponse.setEsGoodsInfoPage(new PageImpl<>(response.getData(),
                    PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
            return goodsInfoResponse;
        }

        List<String> goodsIds =
                response.getData().stream().map(EsGoodsInfo::getGoodsInfo).map(GoodsInfoNest::getGoodsId).distinct().collect(Collectors.toList());
        List<String> skuIds =
                response.getData().stream().map(EsGoodsInfo::getGoodsInfo).map(GoodsInfoNest::getGoodsInfoId).distinct().collect(Collectors.toList());
        //批量查询SPU数据
        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<GoodsVO> goodses = goodsQueryProvider.listByCondition(goodsQueryRequest).getContext().getGoodsVOList();


        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
                GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
        ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));


        //批量查询规格值表
        Map<String, List<GoodsInfoSpecDetailRelVO>> detailRels =
                goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(skuIds)).getContext().getGoodsInfoSpecDetailRelVOList().stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRelVO::getGoodsInfoId));
        List<EsGoodsInfo> esGoodsInfoList = new LinkedList<>();
        Boolean hideSelectedDistributionGoods = queryRequest.isHideSelectedDistributionGoods();
        for (EsGoodsInfo esGoodsInfo : response.getData()) {
            GoodsInfoNest goodsInfo = esGoodsInfo.getGoodsInfo();
            Boolean anyMatch = goodsIdList.stream().anyMatch(id -> id.equals(goodsInfo.getGoodsInfoId()));

            goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO :
                    goodsInfo.getMarketPrice());
            Optional<GoodsVO> goodsOptional =
                    goodses.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst();
            if (goodsOptional.isPresent()) {
                GoodsVO goods = goodsOptional.get();
                goodsInfo.setPriceType(goods.getPriceType());
                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }
                if (anyMatch) {
                    goodsInfo.setJoinDistributior(1);
                } else {
                    goodsInfo.setJoinDistributior(0);
                }
                //填充规格值
                if (Constants.yes.equals(goods.getMoreSpecFlag()) && MapUtils.isNotEmpty(detailRels) && detailRels.containsKey(goodsInfo.getGoodsInfoId())) {
                    goodsInfo.setSpecText(detailRels.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsInfoSpecDetailRelVO::getDetailName).collect(Collectors.joining(" ")));
                }
                goodsInfo.setStock(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getStock());

                if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ZERO) < 1) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
            esGoodsInfoList.add(esGoodsInfo);
        }
        response.setData(esGoodsInfoList);
        //提取聚合数据
        EsGoodsBaseResponse baseResponse = extractBrands(response);
        goodsInfoResponse.setBrands(baseResponse.getBrands());
        if (queryRequest.isCateAggFlag()) {
            //提取聚合数据
            baseResponse = extractGoodsCate(response);
            goodsInfoResponse.setCateList(baseResponse.getCateList());
        }

        //提取规格与规格值聚合数据
        baseResponse = extractGoodsSpecsAndSpecDetails(response);
        goodsInfoResponse.setGoodsSpecs(baseResponse.getGoodsSpecs());
        goodsInfoResponse.setGoodsSpecDetails(baseResponse.getGoodsSpecDetails());

        goodsInfoResponse.setEsGoodsInfoPage(new PageImpl<>(response.getData(),
                PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));

        return goodsInfoResponse;
    }

    /**
     * 根据不同条件查询ES商品信息
     *
     * @param queryRequest
     * @return
     */
    public EsSearchResponse getEsBaseInfoByParams(EsGoodsInfoQueryRequest queryRequest) {
        if (StringUtils.isNotBlank(queryRequest.getKeywords())) {
            queryRequest.setKeywords(this.analyze(queryRequest.getKeywords()));
        }
        if (queryRequest.getCateId() != null) {
            //分类商品
            queryRequest = wrapperGoodsCateToEsGoodsInfoQueryRequest(queryRequest);
        }

        //店铺分类，加入所有子分类
        if (CollectionUtils.isNotEmpty(queryRequest.getStoreCateIds())) {
            queryRequest = wrapperStoreCateToEsGoodsInfoQueryRequest(queryRequest);
        }

        //设定排序
        if (queryRequest.getSortFlag() != null) {
            queryRequest = wrapperSortToEsGoodsInfoQueryRequest(queryRequest);
        }
        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("brand_group").field("goodsBrand.brandId").size(2000));

        if (queryRequest.isCateAggFlag()) {
            //聚合分类
            queryRequest.putAgg(AggregationBuilders.terms("cate_group").field("goodsCate.cateId"));
        }
        log.info("Es参数getEsBaseInfoByParams: {}", JSON.toJSONString(queryRequest));
        //嵌套聚合规格-规格值
//        queryRequest.putAgg(
//                AggregationBuilders.nested("specDetails", "specDetails")
//                        .subAggregation(AggregationBuilders.terms("spec_group").field("specDetails.specName")
//                                .subAggregation(AggregationBuilders.terms("spec_detail_group").field("specDetails" +
//                                        ".allDetailName"))
//                        )
//        );
        return elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.build(searchResponse, resultsMapper));

    }

    /**
     * 根据不同条件查询ES商品信息
     *
     * @param queryRequest
     * @return
     */
    public EsSearchResponse getEsGoodsInfoByParams(EsGoodsInfoQueryRequest queryRequest) {
        if (StringUtils.isNotBlank(queryRequest.getKeywords())) {
            queryRequest.setKeywords(this.analyze(queryRequest.getKeywords()));
        }
        if (queryRequest.getCateId() != null) {
            //分类商品
            queryRequest = wrapperGoodsCateToEsGoodsInfoQueryRequest(queryRequest);
        }

        //店铺分类，加入所有子分类
        if (CollectionUtils.isNotEmpty(queryRequest.getStoreCateIds())) {
            queryRequest = wrapperStoreCateToEsGoodsInfoQueryRequest(queryRequest);
        }

        //设定排序
        if (queryRequest.getSortFlag() != null) {
            queryRequest = wrapperSortToEsGoodsInfoQueryRequest(queryRequest);
        }
        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("brand_group").field("goodsBrand.brandId").size(2000));

        if (queryRequest.isCateAggFlag()) {
            //聚合分类
            queryRequest.putAgg(AggregationBuilders.terms("cate_group").field("goodsCate.cateId"));
        }
        logger.info("marketing esGoods query for sortFlag:"+queryRequest.getSortFlag());
        logger.info("marketing esGoods query for cateIds:"+queryRequest.getCateIds());
        if (Objects.nonNull(queryRequest.getSortFlag()) && queryRequest.getSortFlag() == 11){
            queryRequest.getSorts().removeAll(queryRequest.getSorts());
            queryRequest.putSort("goodsSeqNum",SortOrder.ASC);
        }
        logger.info("marketing esGoods query for sorts:"+queryRequest.getSorts());

        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));

        if (CollectionUtils.isEmpty(response.getGoodsData()) || response.getGoodsData().size() < 1) {
            if(response.getData() == null || CollectionUtils.isEmpty(response.getData())){
                return response;
            }
//            List<String> skuIds = response.getGoodsData().stream().map(EsGoods::getGoodsInfos).flatMap(Collection::stream)
//                    .map(GoodsInfoNest::getGoodsInfoId).distinct().collect(Collectors.toList());
//
//            Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
//                    GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
//            ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));

            //批量查询规格值表
//            Map<String, List<GoodsInfoSpecDetailRelVO>> detailRels =
//                    goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(skuIds)).getContext().getGoodsInfoSpecDetailRelVOList().stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRelVO::getGoodsInfoId));
//            List<GoodsInfoNest> goodsInfoVOList =
//                    response.getGoodsData().stream().map(EsGoods::getGoodsInfos).flatMap(Collection::stream).collect(Collectors.toList());
//            for (GoodsInfoNest goodsInfo : goodsInfoVOList) {
//                goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO :
//                        goodsInfo.getMarketPrice());
//                Optional<GoodsVO> goodsOptional =
//                        goodses.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst();
//                if (goodsOptional.isPresent()) {
//                    GoodsVO goods = goodsOptional.get();
//                    goodsInfo.setPriceType(goods.getPriceType());
//
//                    //为空，则以商品主图
//                    if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
//                        goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
//                    }
//
//                    //填充规格值
//                    if (Constants.yes.equals(goods.getMoreSpecFlag()) && MapUtils.isNotEmpty(detailRels) && detailRels.containsKey(goodsInfo.getGoodsInfoId())) {
//                        goodsInfo.setSpecText(detailRels.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsInfoSpecDetailRelVO::getDetailName).collect(Collectors.joining(" ")));
//                    }
//                    goodsInfo.setStock(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getStock());
//                    goodsInfo.setInquiryFlag(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getInquiryFlag());
//                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock() < 1) {
//                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                    }
//                } else {//不存在，则做为删除标记
//                    goodsInfo.setDelFlag(DeleteFlag.YES);
//                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
//                }
//            }

            return response;
        }
        return response;
    }


    /**
     * 提取商品品牌聚合数据
     *
     * @param response
     * @return
     */
    public EsGoodsBaseResponse extractBrands(EsSearchResponse response) {
        //TODO 测试打印日志上线删除
        // log.info("es结果"+response);
        EsGoodsBaseResponse baseResponse = new EsGoodsBaseResponse();
        if (Objects.nonNull(response.getAggResultMap())){
            List<? extends EsSearchResponse.AggregationResultItem> brandBucket = response.getAggResultMap().get(
                    "brand_group");

            if (CollectionUtils.isNotEmpty(brandBucket)) {
                List<Long> brandIds =
                        brandBucket.stream().map(EsSearchResponse.AggregationResultItem<String>::getKey).map(NumberUtils::toLong).collect(Collectors.toList());
                baseResponse.setBrands(goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().delFlag(DeleteFlag.NO.toValue()).brandIds(brandIds).build()).getContext().getGoodsBrandVOList());
            }
        }

        return baseResponse;
    }

    /**
     * 提取商品分类聚合数据
     *
     * @param response
     * @return
     */
    public EsGoodsBaseResponse extractGoodsCate(EsSearchResponse response) {
        List<? extends EsSearchResponse.AggregationResultItem> cateBucket = response.getAggResultMap().get(
                "cate_group");
        EsGoodsBaseResponse baseResponse = new EsGoodsBaseResponse();
        if (CollectionUtils.isNotEmpty(cateBucket)) {
            List<Long> cateIds =
                    cateBucket.stream().map(EsSearchResponse.AggregationResultItem<String>::getKey).map(NumberUtils::toLong).collect(Collectors.toList());
            GoodsCateListByConditionRequest goodsCateQueryRequest = new GoodsCateListByConditionRequest();
            goodsCateQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            goodsCateQueryRequest.setCateIds(cateIds);
            GoodsCateListByConditionResponse goodsCateListByConditionResponse =
                    goodsCateQueryProvider.listByCondition(goodsCateQueryRequest).getContext();
            baseResponse.setCateList(Objects.nonNull(goodsCateListByConditionResponse) ?
                    goodsCateListByConditionResponse.getGoodsCateVOList() : Collections.emptyList());
        }
        return baseResponse;
    }

    /**
     * 提取规格与规格值聚合数据
     *
     * @param response
     * @return
     */
    public EsGoodsBaseResponse extractGoodsSpecsAndSpecDetails(EsSearchResponse response) {
        EsGoodsBaseResponse baseResponse = new EsGoodsBaseResponse();
        //TODO 测试打印日志上线删除
        // log.info("es查询获取数据"+response);
        if (Objects.nonNull(response.getAggResultMap())){
            List<? extends EsSearchResponse.AggregationResultItem> specGroup = response.getAggResultMap().get(
                    "specDetails");
            if (CollectionUtils.isNotEmpty(specGroup)) {
                List<GoodsSpecVO> goodsSpecs = new ArrayList<>();
                List<GoodsSpecDetailVO> goodsSpecDetails = new ArrayList<>();
                long i = 0;
                long j = 0;
                for (EsSearchResponse.AggregationResultItem spec : specGroup) {
                    GoodsSpecVO goodsSpec = new GoodsSpecVO();
                    goodsSpec.setSpecId(i);
                    goodsSpec.setSpecName(spec.getKey().toString());
                    goodsSpecs.add(goodsSpec);
                    List<EsSearchResponse.AggregationResultItem> childs = spec.getChilds();
                    for (EsSearchResponse.AggregationResultItem specDetail : childs) {
                        GoodsSpecDetailVO goodsSpecDetail = new GoodsSpecDetailVO();
                        goodsSpecDetail.setSpecDetailId(j);
                        goodsSpecDetail.setSpecId(i);
                        goodsSpecDetail.setDetailName(specDetail.getKey().toString());
                        goodsSpecDetails.add(goodsSpecDetail);
                        j++;
                    }
                    i++;
                }
                baseResponse.setGoodsSpecs(goodsSpecs);
                baseResponse.setGoodsSpecDetails(goodsSpecDetails);
            }
        }

        return baseResponse;
    }

    /**
     * 包装排序字段到EsGoodsInfoQueryRequest查询对象中
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsInfoQueryRequest wrapperSortToEsGoodsInfoQueryRequest(EsGoodsInfoQueryRequest queryRequest) {
//        if (StringUtils.isNotBlank(queryRequest.getKeywords())) {
//            queryRequest.putScoreSort();
//        }
        if (queryRequest.getSortFlag()!=11 && queryRequest.getSortFlag() != 2 && queryRequest.getSortFlag() != 3){
            if(queryRequest.isQueryGoods()){
                //根据设置的排序值排序
                if(BooleanUtils.isTrue(queryRequest.getIsKeywords())){
                    queryRequest.putSort("goodsInfos.sortNumKey", SortOrder.DESC);
                }else if(Objects.nonNull(queryRequest.getCateId()) && Objects.nonNull(queryRequest.getSortFlag()) && queryRequest.getSortFlag()!=11){
                    queryRequest.putSort("goodsInfos.sortNumCate", SortOrder.DESC);
                }
            }else{
                if(queryRequest.getIsKeywords()!=null && queryRequest.getIsKeywords()) {
                    queryRequest.putSort("goodsInfo.sortNumKey", SortOrder.DESC);
                }else if(Objects.nonNull(queryRequest.getCateId()) && Objects.nonNull(queryRequest.getSortFlag()) && queryRequest.getSortFlag()!=11){
                    queryRequest.putSort("goodsInfo.sortNumCate", SortOrder.DESC);
                }
            }
        }

        if (StringUtils.isNotBlank(queryRequest.getKeywords()) && queryRequest.getSortFlag() != 2 && queryRequest.getSortFlag() != 3 && queryRequest.getSortFlag() != 4) {
            queryRequest.putScoreSort();
        }

        switch (queryRequest.getSortFlag()) {
            case 0:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("addedTime", SortOrder.DESC);
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                } else {
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("addedTime", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                }
                break;
            case 1:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("addedTime", SortOrder.DESC);
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                } else {
                    queryRequest.putSort("addedTime", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                }
                break;
            case 2:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                } else {
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                }

                break;
            case 3:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.ASC);
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                } else {
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.ASC);
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                }
                break;
            case 4:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                } else {
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                }
                break;
            case 5:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("goodsEvaluateNum", SortOrder.DESC);
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                } else {
                    queryRequest.putSort("goodsInfo.goodsEvaluateNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                }
                break;
            case 6:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("goodsFeedbackRate", SortOrder.DESC);
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                } else {
                    queryRequest.putSort("goodsInfo.goodsFeedbackRate", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                }
                break;
            case 7:
                if (queryRequest.isQueryGoods()) {
                    queryRequest.putSort("goodsCollectNum", SortOrder.DESC);
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                } else {
                    queryRequest.putSort("goodsInfo.goodsCollectNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                }
                break;
            case 8:
                // 按照佣金降序排序
                queryRequest.putSort("goodsInfo.distributionCommission", SortOrder.DESC);
                queryRequest.putSort("addedTime", SortOrder.DESC);
                break;
            case 9:
                // 按照佣金升序排序
                queryRequest.putSort("goodsInfo.distributionCommission", SortOrder.ASC);
                queryRequest.putSort("addedTime", SortOrder.DESC);
                break;
            case 10:
                //默认排序
                if (queryRequest.isQueryGoods()) {
                    //非三级类目下的排序走品牌排序
//                    if (Objects.isNull(queryRequest.getCateId())) {
//                        queryRequest.putSort("goodsBrand.brandSeqNum", SortOrder.ASC);
//                    }

                    queryRequest.putSort("goodsBrand.brandSeqNum", SortOrder.ASC);
                    queryRequest.putSort("goodsSeqNum", SortOrder.ASC);
                    queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("addedTime", SortOrder.DESC);
                    queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                } else {
                    //非三级类目下的排序走品牌排序
//                    if (Objects.isNull(queryRequest.getCateId())) {
//                        queryRequest.putSort("goodsBrand.brandSeqNum", SortOrder.ASC);
//                    }
                    queryRequest.putSort("goodsBrand.brandSeqNum", SortOrder.ASC);
                    queryRequest.putSort("goodsInfo.goodsSeqNum", SortOrder.ASC);
                    queryRequest.putSort("goodsInfo.goodsSalesNum", SortOrder.DESC);
                    queryRequest.putSort("addedTime", SortOrder.DESC);
                    queryRequest.putSort("goodsInfo.marketPrice", SortOrder.DESC);
                }
                break;
            case 11:
                //按照商品编辑排序升序
                queryRequest.getSorts().removeAll(queryRequest.getSorts());
                queryRequest.putSort("goodsSeqNum",SortOrder.ASC);
                break;
            case 12:
                //按照商品编辑排序升序
                queryRequest.getSorts().removeAll(queryRequest.getSorts());
                queryRequest.putSort("goodsInfos.recommendSort",SortOrder.ASC);
                break;
            case 13:
            	// app按分类查询接口专用
                // 店铺内商品序号优先级最高
                queryRequest.getSorts().add(0, new FieldSortBuilder("storeGoodsSeqNum").order(SortOrder.ASC));

                queryRequest.putSort("goodsBrand.brandSeqNum", SortOrder.ASC);
                queryRequest.putSort("goodsSeqNum", SortOrder.ASC);
                queryRequest.putSort("goodsSalesNum", SortOrder.DESC);
                queryRequest.putSort("addedTime", SortOrder.DESC);
                queryRequest.putSort("goodsInfos.marketPrice", SortOrder.DESC);
                break;
            case 15:
                // 商品销量倒序
                // 上架时间倒序
                queryRequest.getSorts().removeAll(queryRequest.getSorts());
                queryRequest.putSort("goodsSalesNum",SortOrder.DESC);
                queryRequest.putSort("addedTime", SortOrder.DESC);
                break;
            default:
                break;
        }

        return queryRequest;
    }

    /**
     * 包装店铺分类信息到EsGoodsInfoQueryRequest查询对象中
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsInfoQueryRequest wrapperStoreCateToEsGoodsInfoQueryRequest(EsGoodsInfoQueryRequest queryRequest) {
        BaseResponse<StoreCateListByStoreCateIdAndIsHaveSelfResponse> baseResponse =
                storeCateQueryProvider.listByStoreCateIdAndIsHaveSelf(new StoreCateListByStoreCateIdAndIsHaveSelfRequest(queryRequest.getStoreCateIds().get(0), false));
        StoreCateListByStoreCateIdAndIsHaveSelfResponse storeCateListByStoreCateIdAndIsHaveSelfResponse =
                baseResponse.getContext();
        if (Objects.nonNull(storeCateListByStoreCateIdAndIsHaveSelfResponse)) {
            List<StoreCateVO> storeCateVOList = storeCateListByStoreCateIdAndIsHaveSelfResponse.getStoreCateVOList();
            queryRequest.getStoreCateIds().addAll(storeCateVOList.stream().map(StoreCateVO::getStoreCateId).collect(Collectors.toList()));
        }
        return queryRequest;
    }

    /**
     * 包装分类商品信息到EsGoodsInfoQueryRequest查询对象中
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsInfoQueryRequest wrapperGoodsCateToEsGoodsInfoQueryRequest(EsGoodsInfoQueryRequest queryRequest) {
        GoodsCateByIdResponse cate =
                goodsCateQueryProvider.getById(new GoodsCateByIdRequest(queryRequest.getCateId())).getContext();
        if (cate != null) {
            GoodsCateListByConditionRequest goodsCateListByConditionRequest = new GoodsCateListByConditionRequest();
            goodsCateListByConditionRequest.setLikeCatePath(ObjectUtils.toString(cate.getCatePath()).concat(String.valueOf(cate.getCateId())).concat("|"));
            List<GoodsCateVO> t_cateList = null;
            BaseResponse<GoodsCateListByConditionResponse> baseResponse =
                    goodsCateQueryProvider.listByCondition(goodsCateListByConditionRequest);
            GoodsCateListByConditionResponse goodsCateListByConditionResponse = baseResponse.getContext();
            if (Objects.nonNull(goodsCateListByConditionResponse)) {
                t_cateList = goodsCateListByConditionResponse.getGoodsCateVOList();
            }
            if (CollectionUtils.isNotEmpty(t_cateList)) {
                queryRequest.setCateIds(t_cateList.stream().map(GoodsCateVO::getCateId).collect(Collectors.toList()));
                queryRequest.getCateIds().add(queryRequest.getCateId());
                queryRequest.setCateId(null);
            }
        }
        return queryRequest;
    }

    /**
     * 分页查询ES商品(实现WEB的商品列表)
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsResponse pageByGoods(EsGoodsInfoQueryRequest queryRequest) {
        EsGoodsResponse goodsResponse = EsGoodsResponse.builder().build();
        if (StringUtils.isNotBlank(queryRequest.getKeywords())) {
            queryRequest.setKeywords(this.analyze(queryRequest.getKeywords()));
        }

        if (queryRequest.getCateId() != null) {
            //分类商品
            queryRequest = wrapperGoodsCateToEsGoodsInfoQueryRequest(queryRequest);
        }

        //店铺分类，加入所有子分类
        if (CollectionUtils.isNotEmpty(queryRequest.getStoreCateIds())) {
            logger.info("//店铺分类，加入所有子分类");
            queryRequest = wrapperStoreCateToEsGoodsInfoQueryRequest(queryRequest);
            logger.info("//店铺分类，加入所有子分类"+queryRequest);
        }

        //设定排序
        if (queryRequest.getSortFlag() != null) {
            queryRequest = wrapperSortToEsGoodsInfoQueryRequest(queryRequest);
        }

        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("brand_group").field("goodsBrand.brandId").size(2000));

        if (queryRequest.isCateAggFlag()) {
            //聚合分类
            queryRequest.putAgg(AggregationBuilders.terms("cate_group").field("goodsCate.cateId"));
        }

        //嵌套聚合规格-规格值
//        queryRequest.putAgg(
//                AggregationBuilders.nested("specDetails", "specDetails")
//                        .subAggregation(AggregationBuilders.terms("spec_group").field("specDetails.specName")
//                                .subAggregation(AggregationBuilders.terms("spec_detail_group").field("specDetails" +
//                                        ".allDetailName"))
//                        )
//        );
//        logger.info("pageByGoods esGoods query for sortFlag:"+queryRequest.getSortFlag());
//        logger.info("pageByGoods esGoods query for cateIds:"+queryRequest.getCateIds());
        if (Objects.nonNull(queryRequest.getSortFlag()) && queryRequest.getSortFlag() == 11){
            queryRequest.getSorts().removeAll(queryRequest.getSorts());
            queryRequest.putSort("goodsSeqNum",SortOrder.ASC);
        }

        // logger.info("pageByGoods esGoods query for sorts:" + queryRequest.getSorts());
//        log.info("pageByGoods esGoods query for sorts: {}", JSON.toJSONString(queryRequest.getSorts()));
        log.info("Es参数pageByGoods: {}", JSON.toJSONString(queryRequest.getSearchCriteria()));
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));
        if (CollectionUtils.isEmpty(response.getGoodsData())) {
            goodsResponse.setEsGoods(new PageImpl<>(response.getGoodsData(),
                    PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
            return goodsResponse;
        }

        // log.info("pageByGoods esGoods query result : {}", JSON.toJSONString(response.getGoodsData()));

        List<String> goodsIds = response.getGoodsData().stream().map(EsGoods::getId).collect(Collectors.toList());
        List<String> skuIds = response.getGoodsData().stream().map(EsGoods::getGoodsInfos).flatMap(Collection::stream)
                .map(GoodsInfoNest::getGoodsInfoId).distinct().collect(Collectors.toList());
        log.info("Es结果pageByGoods,goodsIds: {}", JSON.toJSONString(goodsIds));
        //批量查询SPU数据
        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        goodsQueryRequest.setGoodsSource(1);
        //根据skuids查询暂时不需要仓库ID
        List<GoodsVO> goodses = goodsQueryProvider.listByCondition(goodsQueryRequest).getContext().getGoodsVOList();

        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listGoodsInfoAndStcokByIds(
                GoodsInfoAndStockListByIdsRequest.builder().goodsInfoIds(skuIds).matchWareHouseFlag(queryRequest.getMatchWareHouseFlag()).build()
        ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g, (x, y) -> x));

        //批量查询规格值表
        Map<String, List<GoodsInfoSpecDetailRelVO>> detailRels =
                goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(skuIds)).getContext().getGoodsInfoSpecDetailRelVOList().stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRelVO::getGoodsInfoId));
        List<GoodsInfoNest> goodsInfoVOList =
                response.getGoodsData().stream().map(EsGoods::getGoodsInfos).flatMap(Collection::stream).collect(Collectors.toList());
        for (GoodsInfoNest goodsInfo : goodsInfoVOList) {
            //填充酒水保质期
            Long shelfLife = goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getShelflife();
            if(Objects.nonNull(shelfLife) && shelfLife == 9999){
                goodsInfo.setShelflife(0);
            }
            goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO :
                    goodsInfo.getMarketPrice());
            Optional<GoodsVO> goodsOptional =
                    goodses.stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst();
            if (goodsOptional.isPresent()) {
                GoodsVO goods = goodsOptional.get();
                goodsInfo.setPriceType(goods.getPriceType());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                //填充规格值
                if (Constants.yes.equals(goods.getMoreSpecFlag()) && MapUtils.isNotEmpty(detailRels) && detailRels.containsKey(goodsInfo.getGoodsInfoId())) {
                    goodsInfo.setSpecText(detailRels.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsInfoSpecDetailRelVO::getDetailName).collect(Collectors.joining(" ")));
                }
                goodsInfo.setStock(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getStock());
                //填充库存列表
                if (null != goodsInfoMap.get(goodsInfo.getGoodsInfoId()) &&
                        CollectionUtils.isNotEmpty(goodsInfoMap.get(goodsInfo.getGoodsInfoId()).getGoodsWareStocks())) {
                    goodsInfo.setGoodsWareStockVOS(goodsInfoMap.get(goodsInfo.getGoodsInfoId()).getGoodsWareStocks());
                }
                goodsInfo.setInquiryFlag(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getInquiryFlag());
                if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ZERO) <=0) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
                //预售虚拟库存
                goodsInfo.setPresellStock(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getPresellStock());
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        }
        //提取聚合数据
        EsGoodsBaseResponse baseResponse = extractBrands(response);
        goodsResponse.setBrands(baseResponse.getBrands());

        if (queryRequest.isCateAggFlag()) {
            //提取聚合数据
            baseResponse = extractGoodsCate(response);
            goodsResponse.setCateList(baseResponse.getCateList());
        }

        //提取规格与规格值聚合数据
        baseResponse = extractGoodsSpecsAndSpecDetails(response);
        goodsResponse.setGoodsSpecs(baseResponse.getGoodsSpecs());
        goodsResponse.setGoodsSpecDetails(baseResponse.getGoodsSpecDetails());

        Map<String, GoodsVO> goodsMap = goodses.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, g -> g, (x, y) -> x));
        List<EsGoods> goodsData = response.getGoodsData();
        for (EsGoods goodsDatum : goodsData) {
            GoodsVO goodsVO = goodsMap.get(goodsDatum.getId());
            if(Objects.nonNull(goodsVO) && CollectionUtils.isNotEmpty(goodsDatum.getGoodsInfos())){
                goodsDatum.getGoodsInfos().forEach(var->{
                    if (null == var.getGoodsSubtitle()){
                        var.setGoodsSubtitle(goodsVO.getGoodsSubtitle());
                    }
                    if (null == var.getGoodsSubtitleNew()){
                        var.setGoodsSubtitleNew(goodsVO.getGoodsSubtitleNew());
                    }
                });
            }
        }
        goodsResponse.setEsGoods(new PageImpl<>(response.getGoodsData(), PageRequest.of(queryRequest.getPageNum(),
                queryRequest.getPageSize()), response.getTotal()));
        goodsResponse.setGoodsList(goodses);
        return goodsResponse;
    }


    public EsGoodsResponse pageByGoodsBySimple(EsGoodsInfoQueryRequest queryRequest) {
        queryRequest.setSortFlag(15);
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        queryRequest.setContractStartDate(now);
        queryRequest.setContractEndDate(now);
        queryRequest.setQueryGoods(true);
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        queryRequest.setStoreState(StoreState.OPENING.toValue());
        EsGoodsResponse goodsResponse = EsGoodsResponse.builder().build();
        if (StringUtils.isNotBlank(queryRequest.getKeywords())) {
            queryRequest.setKeywords(this.analyze(queryRequest.getKeywords()));
        }
        if (queryRequest.getCateId() != null) {
            //分类商品
            queryRequest = wrapperGoodsCateToEsGoodsInfoQueryRequest(queryRequest);
        }
        //店铺分类，加入所有子分类
        if (CollectionUtils.isNotEmpty(queryRequest.getStoreCateIds())) {
            logger.info("//店铺分类，加入所有子分类");
            queryRequest = wrapperStoreCateToEsGoodsInfoQueryRequest(queryRequest);
            logger.info("//店铺分类，加入所有子分类"+queryRequest);
        }
        //设定排序
        if (queryRequest.getSortFlag() != null) {
            queryRequest = wrapperSortToEsGoodsInfoQueryRequest(queryRequest);
        }

        if (queryRequest.isCateAggFlag()) {
            //聚合分类
            queryRequest.putAgg(AggregationBuilders.terms("cate_group").field("goodsCate.cateId"));
        }
        log.info("Es参数pageByGoods: {}", JSON.toJSONString(queryRequest.getSearchCriteria()));
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));
        goodsResponse.setEsGoods(new PageImpl<>(response.getGoodsData(),
                PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
        return goodsResponse;
    }


    /**
     * 初始化SKU持化于ES
     */
    public void initAllEsGoodsInfo(EsGoodsInfoRequest request) {
        //批量查询所有SKU信息列表
        GoodsInfoCountByConditionRequest infoQueryRequest = new GoodsInfoCountByConditionRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setCompanyInfoId(request.getCompanyInfoId());
        infoQueryRequest.setGoodsInfoIds(request.getSkuIds());
        infoQueryRequest.setStoreId(request.getStoreId());
        infoQueryRequest.setGoodsId(request.getGoodsId());
        infoQueryRequest.setGoodsIds(request.getGoodsIds());
        infoQueryRequest.setBrandIds(request.getBrandIds());
        infoQueryRequest.setGoodsSource(1);
        infoQueryRequest.setStoreIds(request.getStoreIds());
        infoQueryRequest.setAddedFlag(request.getAddedFlag());
        Long totalCount = goodsInfoQueryProvider.countByCondition(infoQueryRequest).getContext().getCount();
        if (totalCount <= 0) {
            return;
        }
        //是否删除索引
        boolean isClear = request.isClearEsIndex();
        logger.info("商品索引开始,params:{}", JSON.toJSONString(request));
        long startTime = System.currentTimeMillis();
        if (isClear) {
            if (elasticsearchTemplate.indexExists(EsConstants.DOC_GOODS_TYPE)) {
                logger.info("商品spu->删除索引");
                elasticsearchTemplate.deleteIndex(EsConstants.DOC_GOODS_TYPE);
            }
            if (elasticsearchTemplate.indexExists(EsConstants.DOC_GOODS_INFO_TYPE)) {
                logger.info("商品sku->删除索引");
                elasticsearchTemplate.deleteIndex(EsConstants.DOC_GOODS_INFO_TYPE);
            }
            //重建商品索引
            elasticsearchTemplate.getClient().admin().indices()
                    .prepareCreate(EsConstants.DOC_GOODS_TYPE).execute().actionGet();
            elasticsearchTemplate.putMapping(EsGoods.class);
            elasticsearchTemplate.getClient().admin().indices()
                    .prepareCreate(EsConstants.DOC_GOODS_INFO_TYPE).execute().actionGet();

            elasticsearchTemplate.putMapping(EsGoodsInfo.class);

        }

        //考虑到goodsIds可能比较多，采用分页读goodsIds数组，进行查询数据
        //一个ID因采用uuid有32位字符串，mysql的SQL语句最大默认限制1M
        int pageSize = 500;//每批查询500个GoodsID
        long pageCount = 0L;
        long m = totalCount % pageSize;
        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }
        GoodsLabelListRequest goodsLabelListRequest = new GoodsLabelListRequest();
        goodsLabelListRequest.setDelFlag(DeleteFlag.NO);
        //冗余标签数据备用
        List<GoodsLabelVO> goodsLabelVOList = goodsLabelQueryProvider
                .list(goodsLabelListRequest)
                .getContext().getGoodsLabelVOList();


        Long delBrandId = 0L;
        Map<String, GoodsVO> goodses = new HashMap<>();
        Map<Long, StoreVO> storeMap = new HashMap<>();
        Map<String, DefaultFlag> distributionStoreSettingMap = new HashMap<>();
        Map<String, List<GoodsLevelPriceNest>> levelPriceMap = new HashMap<>();
        Map<String, List<GoodsCustomerPriceNest>> customerPriceMap = new HashMap<>();
        Map<String, List<GoodsIntervalPriceVO>> intervalPriceMap = new HashMap<>();
        Map<String, List<GoodsInfoSpecDetailRelNest>> goodsInfoSpecDetailMap = new HashMap<>();
        Map<String, List<GoodsPropDetailRelVO>> goodsPropDetailMap = new HashMap<>();
        Map<String, List<StoreCateGoodsRelaVO>> storeCateGoodsMap = new HashMap<>();
        Map<String, List<GoodsWareStockVO>> goodsStockMap = new HashMap<>();
        Map<Long, String> specMap = new HashMap<>();


        Map<Long, GoodsCateVO> goodsCateMap =
                goodsCateQueryProvider.listByCondition(new GoodsCateListByConditionRequest()).getContext().
                        getGoodsCateVOList().stream().collect(Collectors.toMap(GoodsCateVO::getCateId,
                                goodsCate -> goodsCate));

        Map<Long, GoodsBrandVO> goodsBrandMap = goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().build())
                .getContext().getGoodsBrandVOList().stream().collect(Collectors.toMap(GoodsBrandVO::getBrandId,
                        goodsBrand -> goodsBrand));

        GoodsInfoPageRequest pageRequest = new GoodsInfoPageRequest();
        KsBeanUtil.copyPropertiesThird(infoQueryRequest, pageRequest);

        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsQueryRequest.setGoodsSource(1);
        for (int i = 0; i < pageCount; i++) {
            pageRequest.setPageNum(i);
            pageRequest.setPageSize(pageSize);
            pageRequest.setGoodsSource(1);
            MicroServicePage<GoodsInfoVO> goodsInfopage =
                    goodsInfoQueryProvider.page(pageRequest).getContext().getGoodsInfoPage();
            if (CollectionUtils.isNotEmpty(goodsInfopage.getContent())) {
                //批量查询相应SPU信息列表
                List<String> goodsIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsId).distinct().collect(Collectors.toList());
                List<String> goodsInfoIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsInfoId).distinct().collect(Collectors.toList());
                //分页查询SPU
                goodsQueryRequest.setGoodsIds(goodsIds);

                goodses.putAll(goodsQueryProvider.listByCondition(goodsQueryRequest).getContext().getGoodsVOList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, goods -> goods)));


                //#4
                BaseResponse<GoodsLabelRelaByGoodsIdsResponse> byGoodsIds =
                        goodsLabelRelaQueryProvider.findByGoodsIds(GoodsLabelRelaByGoodsIdsRequest.builder().goodsIds(goodsIds).build());
                List<GoodsLabelRelaVO> goodsLabelRelaVOList = byGoodsIds.getContext().getGoodsLabelRelaVOList();
                //根据GoodsId进行分组
                //Goodsid +labelIds
                Map<String, List<GoodsLabelRelaVO>> goodsLabelRealMap = goodsLabelRelaVOList.stream().collect(Collectors.groupingBy(GoodsLabelRelaVO::getGoodsId));

                //营销活动Map
                /*MarketingMapGetByGoodsIdRequest marketingRequest = MarketingMapGetByGoodsIdRequest.builder()
                        .goodsInfoIdList(goodsInfoIds)
                        .deleteFlag(DeleteFlag.NO)
                        .excludeStatus(MarketingStatus.ENDED).build();
                marketingMap.putAll(marketingQueryProvider.getMarketingMapByGoodsId(marketingRequest).getContext()
                .getListMap());*/

                //区间价Map
                intervalPriceMap.putAll(getIntervalPriceMapBySkuId(goodsInfoIds));

                //等级价Map
                levelPriceMap.putAll(goodsLevelPriceQueryProvider.listBySkuIds(new GoodsLevelPriceBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsLevelPriceList()
                        .stream().map(price -> KsBeanUtil.convert(price, GoodsLevelPriceNest.class))
                        .collect(Collectors.groupingBy(GoodsLevelPriceNest::getGoodsInfoId)));

                //客户价Map
                customerPriceMap.putAll(goodsCustomerPriceQueryProvider.listBySkuIds(new GoodsCustomerPriceBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsCustomerPriceVOList().stream()
                        .map(price -> KsBeanUtil.convert(price, GoodsCustomerPriceNest.class))
                        .collect(Collectors.groupingBy(GoodsCustomerPriceNest::getGoodsInfoId)));

                //规格Map
                specMap.putAll(getGoodsSpecMapByGoodsId(goodsIds));

                //规格值Map
                goodsInfoSpecDetailMap.putAll(goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsInfoSpecDetailRelVOList().stream()
                        .map(price -> KsBeanUtil.convert(price, GoodsInfoSpecDetailRelNest.class))
                        .collect(Collectors.groupingBy(GoodsInfoSpecDetailRelNest::getGoodsInfoId)));

                //属性值Map
                goodsPropDetailMap.putAll(getPropDetailRelList(goodsIds));

                //商品店铺分类Map
                storeCateGoodsMap.putAll(storeCateGoodsRelaQueryProvider.listByGoodsIds(new StoreCateGoodsRelaListByGoodsIdsRequest(goodsIds)).getContext().getStoreCateGoodsRelaVOList().stream().collect(Collectors.groupingBy(StoreCateGoodsRelaVO::getGoodsId)));

                //分仓库存
                GoodsWareStockListResponse response = goodsWareStockQueryProvider.findByGoodsInfoIdIn(
                        GoodsWareStockByGoodsForIdsRequest.builder().goodsForIdList(goodsInfoIds).build()).getContext();
                if(CollectionUtils.isNotEmpty(response.getGoodsWareStockVOList())){
                    List<GoodsWareStockVO> goodsWareStockVOS = response.getGoodsWareStockVOList();
                    List<Long> storeIds = goodsWareStockVOS.stream().map(GoodsWareStockVO::getStoreId).distinct().collect(Collectors.toList());
                    List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.listByStoreId(WareHouseListByStoreIdRequest.builder()
                            .storeIds(storeIds).build()).getContext().getWareHouseVOList();
                    Map<Long, WareHouseVO> wareHouseMap = wareHouseVOList.stream().collect(Collectors.toMap(WareHouseVO::getWareId, s -> s));
                    for (GoodsWareStockVO inner: goodsWareStockVOS){
                        WareHouseVO wareHouseVO = wareHouseMap.get(inner.getWareId());
                        if(Objects.nonNull(wareHouseVO)){
                            inner.setWareHouseType(wareHouseMap.get(inner.getWareId()).getWareHouseType());
                        }
                    }
                    Map<String, List<GoodsWareStockVO>> collect = goodsWareStockVOS.stream().collect(Collectors.groupingBy(GoodsWareStockVO::getGoodsInfoId));
                    goodsStockMap.putAll(collect);
                }

                List<Long> storeIds =
                        goodses.values().stream().map(GoodsVO::getStoreId).filter(Objects::nonNull).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(storeIds)) {
//                    storeMap.putAll(storeRepository.queryListByIds(DeleteFlag.NO, storeIds).stream().collect
//                    (Collectors.toMap(Store::getStoreId, store -> store)));
                    storeMap.putAll(storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                            (storeIds)).getContext().getStoreVOList().stream().collect(Collectors
                            .toMap
                                    (StoreVO::getStoreId, store -> store)));
                    List<String> stringList =
                            storeIds.stream().distinct().map(storeId -> String.valueOf(storeId)).collect(Collectors.toList());
                    distributionStoreSettingMap.putAll(distributionSettingQueryProvider.listByStoreIds(new DistributionStoreSettingListByStoreIdsRequest(stringList)).getContext().getList().stream().collect(Collectors.toMap(DistributionStoreSettingVO::getStoreId, DistributionStoreSettingVO::getOpenFlag)));
                }

                //遍历SKU，填充SPU、图片
                List<IndexQuery> esGoodsInfos = new ArrayList<>();
                List<EsCateBrand> esCateBrands = new ArrayList<>();
                goodsInfopage.getContent().stream().filter(goodsInfo -> goodses.containsKey(goodsInfo.getGoodsId())).forEach(goodsInfo -> {
                    GoodsVO goods = goodses.getOrDefault(goodsInfo.getGoodsId(), new GoodsVO());
                    goodsInfo.setCateId(goods.getCateId());
                    goodsInfo.setBrandId(goods.getBrandId());
                    goodsInfo.setPriceType(goods.getPriceType());
                    goodsInfo.setCompanyType(goods.getCompanyType());
                    //这里需要重新设置
                    goodsInfo.setGoodsCubage(goods.getGoodsCubage());

                    //填充规格名
                    List<GoodsInfoSpecDetailRelNest> detailRels =
                            goodsInfoSpecDetailMap.get(goodsInfo.getGoodsInfoId());
                    if (CollectionUtils.isNotEmpty(detailRels)) {
                        detailRels.forEach(specDetailRel -> specDetailRel.setSpecName(specMap.get(specDetailRel.getSpecId())));
                    }

                    //分配最小区间值和最大区间值
                    if (CollectionUtils.isNotEmpty(intervalPriceMap.get(goodsInfo.getGoodsInfoId()))) {
                        List<BigDecimal> prices =
                                intervalPriceMap.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsIntervalPriceVO::getPrice).filter(Objects::nonNull).collect(Collectors.toList());
                        goodsInfo.setIntervalMinPrice(prices.stream().filter(Objects::nonNull).min(BigDecimal::compareTo).orElse(goodsInfo.getMarketPrice()));
                        goodsInfo.setIntervalMaxPrice(prices.stream().filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(goodsInfo.getMarketPrice()));
                    }

                    //定义文档结构
                    EsGoodsInfo esGoodsInfo = new EsGoodsInfo();
                    esGoodsInfo.setHostSku(goodsInfo.getHostSku());
                    esGoodsInfo.setId(goodsInfo.getGoodsInfoId());
                    esGoodsInfo.setWareId(goodsInfo.getWareId());
                    esGoodsInfo.setParentGoodsInfoId(goodsInfo.getParentGoodsInfoId());
                    List<GoodsLabelRelaVO> goodsLabelRelaVOS = goodsLabelRealMap.get(goods.getGoodsId());
                    if (CollectionUtils.isNotEmpty(goodsLabelRelaVOS)) {
                        List<Long> labelIdList = goodsLabelRelaVOS.stream().map(GoodsLabelRelaVO::getLabelId).collect(Collectors.toList());
                        //当前商品的关联标签
                        String labelIds = StringUtils.join(labelIdList, ",");
                        List<GoodsLabelVO> goodsLabels = goodsLabelVOList.stream()
                                .filter(item -> labelIds.contains(item.getId().toString()))
                                .collect(Collectors.toList());
                        esGoodsInfo.setGoodsLabels(goodsLabels);
                    }


                    // 设置sku的品牌和分类
                    EsCateBrand esCateBrand = new EsCateBrand();
                    GoodsCateVO goodsCate = goodsCateMap.get(goodsInfo.getCateId());
                    GoodsBrandVO goodsBrand = new GoodsBrandVO();
                    goodsBrand.setBrandId(0L);
                    if (goodsInfo.getBrandId() != null) {
                        goodsBrand = goodsBrandMap.get(goodsInfo.getBrandId());
                    }

                    if (goodsCate != null) {
                        esCateBrand = esCateBrandService.putEsCateBrand(goodsCate, goodsBrand);
                    }
                    if (Objects.nonNull(esCateBrand.getGoodsCate())) {
                        esGoodsInfo.setGoodsCate(esCateBrand.getGoodsCate());
                    }
                    if (Objects.nonNull(esCateBrand.getGoodsBrand())) {
                        esGoodsInfo.setGoodsBrand(esCateBrand.getGoodsBrand());
                    }
                    // 添加品牌排序
                    if (Objects.nonNull(esCateBrand.getGoodsCate()) && Objects.nonNull(esCateBrand.getGoodsBrand())) {
                        try {
                            CateBrandSortRelVO cateBrandSortRelVO = cateBrandSortRelQueryProvider.getById(CateBrandSortRelByIdRequest.builder()
                                    .brandId(esCateBrand.getGoodsBrand().getBrandId())
                                    .cateId(esCateBrand.getGoodsCate().getCateId())
                                    .build()).getContext().getCateBrandSortRelVO();
                            if (Objects.nonNull(cateBrandSortRelVO) && Long.valueOf(0).compareTo(cateBrandSortRelVO.getSerialNo()) < 0) {
                                esGoodsInfo.getGoodsBrand().setBrandRelSeqNum(Integer.valueOf(cateBrandSortRelVO.getSerialNo().toString()));
                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }
                    GoodsInfoNest goodsInfoNest = KsBeanUtil.convert(goodsInfo, GoodsInfoNest.class);
                    goodsInfoNest.setEnterPriseAuditStatus(goodsInfo.getEnterPriseAuditState() != null ?
                            goodsInfo.getEnterPriseAuditState().toValue() : EnterpriseAuditState.INIT.toValue());
                    goodsInfoNest.setEsSortPrice();
                    goodsInfoNest.setGoodsWareStockVOS(goodsStockMap.get(goodsInfo.getGoodsInfoId()));
                    //商品的重量
                    goodsInfoNest.setGoodsWeight(goods.getGoodsWeight());
                    goodsInfoNest.setFreightTempId(goods.getFreightTempId());
                    //商品排序序号
                    goodsInfoNest.setGoodsSeqNum(goods.getGoodsSeqNum());
                    goodsInfoNest.setGoodsSubtitle(goods.getGoodsSubtitle());

                    //设置第三方单位信息
                    goodsInfoNest.setGoodsInfoUnit(goodsInfo.getGoodsInfoUnit());
                    goodsInfoNest.setGoodsInfoWeight(goodsInfo.getGoodsInfoWeight());
                    goodsInfoNest.setGoodsInfoCubage(goodsInfo.getGoodsInfoCubage());
                    //如果散称或定量为空，则默认为0，散称
                    if (goodsInfoNest.getIsScatteredQuantitative() == null) {
                        goodsInfoNest.setIsScatteredQuantitative(-1);
                    }
                    //如果虚拟库存为null，默认给0
                    if (Objects.nonNull(goodsInfoNest.getVirtualStock())){
                        goodsInfoNest.setVirtualStock(BigDecimal.ZERO);
                    }
                    if ( goodsInfoNest.getCompanyType().equals(CompanyType.SUPPLIER)){
                        goodsInfoNest.setGoodsSubtitle(goodsInfoNest.getMarketPrice()+"/"+goods.getGoodsUnit());
                        goodsInfoNest.setGoodsSubtitleNew(goodsInfoNest.getMarketPrice()+"/"+goods.getGoodsUnit());
                    }else{
                        goodsInfoNest.setGoodsSubtitle(goods.getGoodsSubtitleNew());
                        goodsInfoNest.setGoodsSubtitleNew(goods.getGoodsSubtitleNew());
                    }
                    if (null!=goods && null!=goods.getStoreId()){
                        BaseResponse<StoreInfoResponse> storeInfoResponseBaseResponse = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(goods.getStoreId()).build());
                        if (storeInfoResponseBaseResponse.getContext()!=null && storeInfoResponseBaseResponse.getContext().getStoreName()!=null){
                            goodsInfoNest.setStoreName(storeInfoResponseBaseResponse.getContext().getStoreName());
                        }
                    }


                    esGoodsInfo.setGoodsInfo(goodsInfoNest);
                    esGoodsInfo.setAddedTime(goodsInfo.getAddedTime());
                    esGoodsInfo.setGoodsLevelPrices(levelPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setCustomerPrices(customerPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setLowGoodsName(StringUtils.lowerCase(goodsInfo.getGoodsInfoName()));

                    //分配规格值
//                    if (CollectionUtils.isNotEmpty(detailRels)) {
//                        detailRels.forEach(specDetailRel -> specDetailRel.setAllDetailName(specDetailRel.getDetailName()));
//                        esGoodsInfo.setSpecDetails(detailRels.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).collect(Collectors.toList()));
//                    }

                    //分配属性值
                    if (CollectionUtils.isNotEmpty(goodsPropDetailMap.get(goodsInfo.getGoodsId()))) {
                        esGoodsInfo.setPropDetails(goodsPropDetailMap.get(goodsInfo.getGoodsId()).stream().map(rel -> {
                            GoodsPropDetailRelVO relES = new GoodsPropDetailRelVO();
                            relES.setPropId(rel.getPropId());
                            relES.setDetailId(rel.getDetailId());
                            return relES;
                        }).collect(Collectors.toList()));
                    }

                    if (null!=goodsInfo && null!=goodsInfo.getGoodsInfoId()){
                        //分配属性值值(新的属性关系)
                        BaseResponse<GoodsAttributeKeyListResponse> list = goodsAttributeKeyQueryProvider.getNotGoodsList(GoodsAttributeKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
                        List<GoodsInfoSpecDetailRelNest> specDetails=new ArrayList<>();
                        //分配规格值(新的属性关系)
                        if ( list.getContext()!=null && CollectionUtils.isNotEmpty(list.getContext().getAttributeKeyVOS())){
                            list.getContext().getAttributeKeyVOS().forEach(l->{
                                GoodsInfoSpecDetailRelNest goodsInfoSpecDetailRelNest=new GoodsInfoSpecDetailRelNest();
                                goodsInfoSpecDetailRelNest.setGoodsId(l.getGoodsId());
                                goodsInfoSpecDetailRelNest.setGoodsInfoId(l.getGoodsInfoId());
                                goodsInfoSpecDetailRelNest.setDetailName(l.getGoodsAttributeValue());
                                goodsInfoSpecDetailRelNest.setSpecName(l.getAttributeId());
                                specDetails.add(goodsInfoSpecDetailRelNest);
                            });
                            esGoodsInfo.setSpecDetails(specDetails);
                        }
                    }





                    //填充签约有效期时间
                    if (MapUtils.isNotEmpty(storeMap) && storeMap.containsKey(goods.getStoreId())) {
                        StoreVO store = storeMap.get(goods.getStoreId());
                        esGoodsInfo.setContractStartDate(store.getContractStartDate());
                        esGoodsInfo.setContractEndDate(store.getContractEndDate());
                        esGoodsInfo.setStoreState(store.getStoreState().toValue());
                    }

                    //获取店铺等级
                    if (storeCateGoodsMap.containsKey(goods.getGoodsId())) {
                        esGoodsInfo.setStoreCateIds(storeCateGoodsMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRelaVO::getStoreCateId).collect(Collectors.toList()));
                    }

                    esGoodsInfo.setAuditStatus(goods.getAuditStatus().toValue());
                    if (MapUtils.isNotEmpty(distributionStoreSettingMap) && distributionStoreSettingMap.containsKey(goods.getStoreId().toString())) {
                        esGoodsInfo.setDistributionGoodsStatus(distributionStoreSettingMap.get(goods.getStoreId().toString()) == DefaultFlag.NO ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO);
                    }
                    //填充商品销量
                    esGoodsInfo.getGoodsInfo().setGoodsSalesNum(goods.getGoodsSalesNum() == null ? 0 :
                            goods.getGoodsSalesNum());
                    //填充商品收藏量
                    esGoodsInfo.getGoodsInfo().setGoodsCollectNum(goods.getGoodsCollectNum() == null ? 0 :
                            goods.getGoodsCollectNum());
                    //填充商品评论数
                    esGoodsInfo.getGoodsInfo().setGoodsEvaluateNum(goods.getGoodsEvaluateNum() == null ? 0 :
                            goods.getGoodsEvaluateNum());
                    //填充商品好评数
                    esGoodsInfo.getGoodsInfo().setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum() == null ? 0 : goods.getGoodsFavorableCommentNum());
                    //填充好评率数据
                    Long goodsFeedbackRate = 0L;
                    if (Objects.nonNull(goods.getGoodsEvaluateNum()) && Objects.nonNull(goods.getGoodsFavorableCommentNum())
                            && goods.getGoodsEvaluateNum() > 0L) {
                        goodsFeedbackRate =
                                (long) ((double) goods.getGoodsFavorableCommentNum() / (double) goods.getGoodsEvaluateNum() * 100);
                    }
                    esGoodsInfo.getGoodsInfo().setGoodsFeedbackRate(goodsFeedbackRate);

                    IndexQuery iq = new IndexQuery();

                    iq.setObject(esGoodsInfo);
                    iq.setIndexName(EsConstants.DOC_GOODS_INFO_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_GOODS_INFO_TYPE);
//                    iq.setParentId(esGoodsInfo.getCateBrandId());
                    esGoodsInfos.add(iq);
                });

                //持久化分类品牌
                if (CollectionUtils.isNotEmpty(esCateBrands)) {
                    esCateBrandRepository.saveAll(esCateBrands);
                    elasticsearchTemplate.refresh(EsCateBrand.class);
                }

                //持久化商品

                // logger.info("持久化数据：==="+JSON.toJSONString(esGoodsInfos));
                elasticsearchTemplate.bulkIndex(esGoodsInfos);
                elasticsearchTemplate.refresh(EsGoodsInfo.class);

                //清空缓存
                goodses.clear();
                intervalPriceMap.clear();
                goodsInfoSpecDetailMap.clear();
                goodsPropDetailMap.clear();
                specMap.clear();
                levelPriceMap.clear();
                customerPriceMap.clear();
            }
        }

        logger.info(String.format("商品索引结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));

        this.initEsGoods(request);

    }

    /**
     * 初始化SKU持化于ES
     */
    public void initEsGoodsInfo(EsGoodsInfoRequest request) {
        //批量查询所有SKU信息列表
        GoodsInfoCountByConditionRequest infoQueryRequest = new GoodsInfoCountByConditionRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setCompanyInfoId(request.getCompanyInfoId());
        infoQueryRequest.setGoodsInfoIds(request.getSkuIds());
        infoQueryRequest.setStoreId(request.getStoreId());
        infoQueryRequest.setGoodsId(request.getGoodsId());
        infoQueryRequest.setGoodsIds(request.getGoodsIds());
        infoQueryRequest.setBrandIds(request.getBrandIds());
        infoQueryRequest.setGoodsSource(1);
        infoQueryRequest.setStoreIds(request.getStoreIds());
        infoQueryRequest.setAddedFlag(request.getAddedFlag());
        Long totalCount = goodsInfoQueryProvider.countByCondition(infoQueryRequest).getContext().getCount();
        if (totalCount <= 0) {
            return;
        }

        //是否删除索引
        boolean isClear = request.isClearEsIndex();
        logger.info("商品索引开始");
        long startTime = System.currentTimeMillis();
        if (isClear) {
            logger.warn("初始化es 不带任何值？"+JSON.toJSONString(request));
            throw new SbcRuntimeException("K-ES44444", "初始化es 不带任何值"+JSON.toJSONString(request));
        }

        //考虑到goodsIds可能比较多，采用分页读goodsIds数组，进行查询数据
        //一个ID因采用uuid有32位字符串，mysql的SQL语句最大默认限制1M
        int pageSize = 500;//每批查询500个GoodsID
        long pageCount = 0L;
        long m = totalCount % pageSize;
        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }
        GoodsLabelListRequest goodsLabelListRequest = new GoodsLabelListRequest();
        goodsLabelListRequest.setDelFlag(DeleteFlag.NO);
        //冗余标签数据备用
        List<GoodsLabelVO> goodsLabelVOList = goodsLabelQueryProvider
                .list(goodsLabelListRequest)
                .getContext().getGoodsLabelVOList();


        Long delBrandId = 0L;
        Map<String, GoodsVO> goodses = new HashMap<>();
        Map<Long, StoreVO> storeMap = new HashMap<>();
        Map<String, DefaultFlag> distributionStoreSettingMap = new HashMap<>();
        Map<String, List<GoodsLevelPriceNest>> levelPriceMap = new HashMap<>();
        Map<String, List<GoodsCustomerPriceNest>> customerPriceMap = new HashMap<>();
        Map<String, List<GoodsIntervalPriceVO>> intervalPriceMap = new HashMap<>();
        Map<String, List<GoodsInfoSpecDetailRelNest>> goodsInfoSpecDetailMap = new HashMap<>();
        Map<String, List<GoodsPropDetailRelVO>> goodsPropDetailMap = new HashMap<>();
        Map<String, List<StoreCateGoodsRelaVO>> storeCateGoodsMap = new HashMap<>();
        Map<String, List<GoodsWareStockVO>> goodsStockMap = new HashMap<>();
        Map<Long, String> specMap = new HashMap<>();


        Map<Long, GoodsCateVO> goodsCateMap =
                goodsCateQueryProvider.listByCondition(new GoodsCateListByConditionRequest()).getContext().
                        getGoodsCateVOList().stream().collect(Collectors.toMap(GoodsCateVO::getCateId,
                                goodsCate -> goodsCate));

        Map<Long, GoodsBrandVO> goodsBrandMap = goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().build())
                .getContext().getGoodsBrandVOList().stream().collect(Collectors.toMap(GoodsBrandVO::getBrandId,
                        goodsBrand -> goodsBrand));

        GoodsInfoPageRequest pageRequest = new GoodsInfoPageRequest();
        KsBeanUtil.copyPropertiesThird(infoQueryRequest, pageRequest);

        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsQueryRequest.setGoodsSource(1);
        for (int i = 0; i < pageCount; i++) {
            pageRequest.setPageNum(i);
            pageRequest.setPageSize(pageSize);
            pageRequest.setGoodsSource(1);
            MicroServicePage<GoodsInfoVO> goodsInfopage =
                    goodsInfoQueryProvider.page(pageRequest).getContext().getGoodsInfoPage();
            if (CollectionUtils.isNotEmpty(goodsInfopage.getContent())) {
                //批量查询相应SPU信息列表
                List<String> goodsIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsId).distinct().collect(Collectors.toList());
                List<String> goodsInfoIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsInfoId).distinct().collect(Collectors.toList());
                //分页查询SPU
                goodsQueryRequest.setGoodsIds(goodsIds);

                goodses.putAll(goodsQueryProvider.listByCondition(goodsQueryRequest).getContext().getGoodsVOList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, goods -> goods)));


                //#4
                BaseResponse<GoodsLabelRelaByGoodsIdsResponse> byGoodsIds =
                        goodsLabelRelaQueryProvider.findByGoodsIds(GoodsLabelRelaByGoodsIdsRequest.builder().goodsIds(goodsIds).build());
                List<GoodsLabelRelaVO> goodsLabelRelaVOList = byGoodsIds.getContext().getGoodsLabelRelaVOList();
                //根据GoodsId进行分组
                //Goodsid +labelIds
                Map<String, List<GoodsLabelRelaVO>> goodsLabelRealMap = goodsLabelRelaVOList.stream().collect(Collectors.groupingBy(GoodsLabelRelaVO::getGoodsId));

                //营销活动Map
                /*MarketingMapGetByGoodsIdRequest marketingRequest = MarketingMapGetByGoodsIdRequest.builder()
                        .goodsInfoIdList(goodsInfoIds)
                        .deleteFlag(DeleteFlag.NO)
                        .excludeStatus(MarketingStatus.ENDED).build();
                marketingMap.putAll(marketingQueryProvider.getMarketingMapByGoodsId(marketingRequest).getContext()
                .getListMap());*/

                //区间价Map
                intervalPriceMap.putAll(getIntervalPriceMapBySkuId(goodsInfoIds));

                //等级价Map
                levelPriceMap.putAll(goodsLevelPriceQueryProvider.listBySkuIds(new GoodsLevelPriceBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsLevelPriceList()
                        .stream().map(price -> KsBeanUtil.convert(price, GoodsLevelPriceNest.class))
                        .collect(Collectors.groupingBy(GoodsLevelPriceNest::getGoodsInfoId)));

                //客户价Map
                customerPriceMap.putAll(goodsCustomerPriceQueryProvider.listBySkuIds(new GoodsCustomerPriceBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsCustomerPriceVOList().stream()
                        .map(price -> KsBeanUtil.convert(price, GoodsCustomerPriceNest.class))
                        .collect(Collectors.groupingBy(GoodsCustomerPriceNest::getGoodsInfoId)));

                //规格Map
                specMap.putAll(getGoodsSpecMapByGoodsId(goodsIds));

                //规格值Map
                goodsInfoSpecDetailMap.putAll(goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsInfoSpecDetailRelVOList().stream()
                        .map(price -> KsBeanUtil.convert(price, GoodsInfoSpecDetailRelNest.class))
                        .collect(Collectors.groupingBy(GoodsInfoSpecDetailRelNest::getGoodsInfoId)));

                //属性值Map
                goodsPropDetailMap.putAll(getPropDetailRelList(goodsIds));

                //商品店铺分类Map
                storeCateGoodsMap.putAll(storeCateGoodsRelaQueryProvider.listByGoodsIds(new StoreCateGoodsRelaListByGoodsIdsRequest(goodsIds)).getContext().getStoreCateGoodsRelaVOList().stream().collect(Collectors.groupingBy(StoreCateGoodsRelaVO::getGoodsId)));

                //分仓库存
                GoodsWareStockListResponse response = goodsWareStockQueryProvider.findByGoodsInfoIdIn(
                        GoodsWareStockByGoodsForIdsRequest.builder().goodsForIdList(goodsInfoIds).build()).getContext();
                if(CollectionUtils.isNotEmpty(response.getGoodsWareStockVOList())){
                    List<GoodsWareStockVO> goodsWareStockVOS = response.getGoodsWareStockVOList();
                    List<Long> storeIds = goodsWareStockVOS.stream().map(GoodsWareStockVO::getStoreId).distinct().collect(Collectors.toList());
                    List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.listByStoreId(WareHouseListByStoreIdRequest.builder()
                            .storeIds(storeIds).build()).getContext().getWareHouseVOList();
                    Map<Long, WareHouseVO> wareHouseMap = wareHouseVOList.stream().collect(Collectors.toMap(WareHouseVO::getWareId, s -> s));
                    for (GoodsWareStockVO inner: goodsWareStockVOS){
                        WareHouseVO wareHouseVO = wareHouseMap.get(inner.getWareId());
                        if(Objects.nonNull(wareHouseVO)){
                            inner.setWareHouseType(wareHouseMap.get(inner.getWareId()).getWareHouseType());
                        }
                    }
                    Map<String, List<GoodsWareStockVO>> collect = goodsWareStockVOS.stream().collect(Collectors.groupingBy(GoodsWareStockVO::getGoodsInfoId));
                    goodsStockMap.putAll(collect);
                }

                List<Long> storeIds =
                        goodses.values().stream().map(GoodsVO::getStoreId).filter(Objects::nonNull).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(storeIds)) {
//                    storeMap.putAll(storeRepository.queryListByIds(DeleteFlag.NO, storeIds).stream().collect
//                    (Collectors.toMap(Store::getStoreId, store -> store)));
                    storeMap.putAll(storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                            (storeIds)).getContext().getStoreVOList().stream().collect(Collectors
                            .toMap
                                    (StoreVO::getStoreId, store -> store)));
                    List<String> stringList =
                            storeIds.stream().distinct().map(storeId -> String.valueOf(storeId)).collect(Collectors.toList());
                    distributionStoreSettingMap.putAll(distributionSettingQueryProvider.listByStoreIds(new DistributionStoreSettingListByStoreIdsRequest(stringList)).getContext().getList().stream().collect(Collectors.toMap(DistributionStoreSettingVO::getStoreId, DistributionStoreSettingVO::getOpenFlag)));
                }

                //遍历SKU，填充SPU、图片
                List<IndexQuery> esGoodsInfos = new ArrayList<>();
                List<EsCateBrand> esCateBrands = new ArrayList<>();
                goodsInfopage.getContent().stream().filter(goodsInfo -> goodses.containsKey(goodsInfo.getGoodsId())).forEach(goodsInfo -> {
                    GoodsVO goods = goodses.getOrDefault(goodsInfo.getGoodsId(), new GoodsVO());
                    goodsInfo.setCateId(goods.getCateId());
                    goodsInfo.setBrandId(goods.getBrandId());
                    goodsInfo.setPriceType(goods.getPriceType());
                    goodsInfo.setCompanyType(goods.getCompanyType());
                    goodsInfo.setGoodsCubage(goods.getGoodsCubage());

                    //填充规格名
                    List<GoodsInfoSpecDetailRelNest> detailRels =
                            goodsInfoSpecDetailMap.get(goodsInfo.getGoodsInfoId());
                    if (CollectionUtils.isNotEmpty(detailRels)) {
                        detailRels.forEach(specDetailRel -> specDetailRel.setSpecName(specMap.get(specDetailRel.getSpecId())));
                    }

                    //分配最小区间值和最大区间值
                    if (CollectionUtils.isNotEmpty(intervalPriceMap.get(goodsInfo.getGoodsInfoId()))) {
                        List<BigDecimal> prices =
                                intervalPriceMap.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsIntervalPriceVO::getPrice).filter(Objects::nonNull).collect(Collectors.toList());
                        goodsInfo.setIntervalMinPrice(prices.stream().filter(Objects::nonNull).min(BigDecimal::compareTo).orElse(goodsInfo.getMarketPrice()));
                        goodsInfo.setIntervalMaxPrice(prices.stream().filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(goodsInfo.getMarketPrice()));
                    }

                    //定义文档结构
                    EsGoodsInfo esGoodsInfo = new EsGoodsInfo();
                    esGoodsInfo.setHostSku(goodsInfo.getHostSku());
                    esGoodsInfo.setId(goodsInfo.getGoodsInfoId());
                    esGoodsInfo.setWareId(goodsInfo.getWareId());
                    esGoodsInfo.setParentGoodsInfoId(goodsInfo.getParentGoodsInfoId());
                    List<GoodsLabelRelaVO> goodsLabelRelaVOS = goodsLabelRealMap.get(goods.getGoodsId());
                    if (CollectionUtils.isNotEmpty(goodsLabelRelaVOS)) {
                        List<Long> labelIdList = goodsLabelRelaVOS.stream().map(GoodsLabelRelaVO::getLabelId).collect(Collectors.toList());
                        //当前商品的关联标签
                        String labelIds = StringUtils.join(labelIdList, ",");
                        List<GoodsLabelVO> goodsLabels = goodsLabelVOList.stream()
                                .filter(item -> labelIds.contains(item.getId().toString()))
                                .collect(Collectors.toList());
                        esGoodsInfo.setGoodsLabels(goodsLabels);
                    }

                    //#1
//                    if (StringUtils.isNotBlank(goods.getLabelIdStr())) {
//                        List<String> labelIds = Arrays.asList(goods.getLabelIdStr().split(","));
//                        List<GoodsLabelVO> goodsLabels = goodsLabelVOList.stream()
//                                .filter(item -> labelIds.contains(item.getId().toString()))
//                                .collect(Collectors.toList());
//                        esGoodsInfo.setGoodsLabels(goodsLabels);
//                    }
                    // 设置sku的品牌和分类
                    EsCateBrand esCateBrand = new EsCateBrand();
                    GoodsCateVO goodsCate = goodsCateMap.get(goodsInfo.getCateId());
                    GoodsBrandVO goodsBrand = new GoodsBrandVO();
                    goodsBrand.setBrandId(0L);
                    if (goodsInfo.getBrandId() != null) {
                        goodsBrand = goodsBrandMap.get(goodsInfo.getBrandId());
                    }

                    if (goodsCate != null) {
                        esCateBrand = esCateBrandService.putEsCateBrand(goodsCate, goodsBrand);
                    }
                    if (Objects.nonNull(esCateBrand.getGoodsCate())) {
                        esGoodsInfo.setGoodsCate(esCateBrand.getGoodsCate());
                    }
                    if (Objects.nonNull(esCateBrand.getGoodsBrand())) {
                        esGoodsInfo.setGoodsBrand(esCateBrand.getGoodsBrand());
                    }
                    // 添加品牌排序
                    if (Objects.nonNull(esCateBrand.getGoodsCate()) && Objects.nonNull(esCateBrand.getGoodsBrand())) {
                        try {
                            CateBrandSortRelVO cateBrandSortRelVO = cateBrandSortRelQueryProvider.getById(CateBrandSortRelByIdRequest.builder()
                                    .brandId(esCateBrand.getGoodsBrand().getBrandId())
                                    .cateId(esCateBrand.getGoodsCate().getCateId())
                                    .build()).getContext().getCateBrandSortRelVO();
                            if (Objects.nonNull(cateBrandSortRelVO) && Long.valueOf(0).compareTo(cateBrandSortRelVO.getSerialNo()) < 0) {
                                esGoodsInfo.getGoodsBrand().setBrandRelSeqNum(Integer.valueOf(cateBrandSortRelVO.getSerialNo().toString()));
                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }
                    GoodsInfoNest goodsInfoNest = KsBeanUtil.convert(goodsInfo, GoodsInfoNest.class);
                    goodsInfoNest.setEnterPriseAuditStatus(goodsInfo.getEnterPriseAuditState() != null ?
                            goodsInfo.getEnterPriseAuditState().toValue() : EnterpriseAuditState.INIT.toValue());
                    goodsInfoNest.setEsSortPrice();
                    goodsInfoNest.setGoodsWareStockVOS(goodsStockMap.get(goodsInfo.getGoodsInfoId()));
                    //商品的重量
                    goodsInfoNest.setGoodsWeight(goods.getGoodsWeight());
                    goodsInfoNest.setFreightTempId(goods.getFreightTempId());
                    //商品排序序号
                    goodsInfoNest.setGoodsSeqNum(goods.getGoodsSeqNum());
                    goodsInfoNest.setGoodsSubtitle(goods.getGoodsSubtitle());


                    //如果散称或定量为空，则默认为0，散称
                    if (goodsInfoNest.getIsScatteredQuantitative() == null) {
                        goodsInfoNest.setIsScatteredQuantitative(-1);
                    }
                    //如果虚拟库存为null，默认给0
                    if (Objects.nonNull(goodsInfoNest.getVirtualStock())){
                        goodsInfoNest.setVirtualStock(BigDecimal.ZERO);
                    }
                    if ( goodsInfoNest.getCompanyType().equals(CompanyType.SUPPLIER)){
                        goodsInfoNest.setGoodsSubtitle(goodsInfoNest.getGoodsSubtitle());
                        goodsInfoNest.setGoodsSubtitleNew(goodsInfoNest.getGoodsSubtitleNew());
                    }else{
                        goodsInfoNest.setGoodsSubtitle(goods.getGoodsSubtitleNew());
                        goodsInfoNest.setGoodsSubtitleNew(goods.getGoodsSubtitleNew());
                    }
                    if (null!=goods && null!=goods.getStoreId()){
                        BaseResponse<StoreInfoResponse> storeInfoResponseBaseResponse = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(goods.getStoreId()).build());
                        if (storeInfoResponseBaseResponse.getContext()!=null && storeInfoResponseBaseResponse.getContext().getStoreName()!=null){
                            goodsInfoNest.setStoreName(storeInfoResponseBaseResponse.getContext().getStoreName());
                        }
                    }


                    esGoodsInfo.setGoodsInfo(goodsInfoNest);
                    esGoodsInfo.setAddedTime(goodsInfo.getAddedTime());
                    esGoodsInfo.setGoodsLevelPrices(levelPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setCustomerPrices(customerPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setLowGoodsName(StringUtils.lowerCase(goodsInfo.getGoodsInfoName()));

                    //分配规格值
//                    if (CollectionUtils.isNotEmpty(detailRels)) {
//                        detailRels.forEach(specDetailRel -> specDetailRel.setAllDetailName(specDetailRel.getDetailName()));
//                        esGoodsInfo.setSpecDetails(detailRels.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).collect(Collectors.toList()));
//                    }

                    //分配属性值
                    if (CollectionUtils.isNotEmpty(goodsPropDetailMap.get(goodsInfo.getGoodsId()))) {
                        esGoodsInfo.setPropDetails(goodsPropDetailMap.get(goodsInfo.getGoodsId()).stream().map(rel -> {
                            GoodsPropDetailRelVO relES = new GoodsPropDetailRelVO();
                            relES.setPropId(rel.getPropId());
                            relES.setDetailId(rel.getDetailId());
                            return relES;
                        }).collect(Collectors.toList()));
                    }

                    if (null!=goodsInfo && null!=goodsInfo.getGoodsInfoId()){
                        //分配属性值值(新的属性关系)
                        BaseResponse<GoodsAttributeKeyListResponse> list = goodsAttributeKeyQueryProvider.getNotGoodsList(GoodsAttributeKeyQueryRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build());
                        List<GoodsInfoSpecDetailRelNest> specDetails=new ArrayList<>();
                        //分配规格值(新的属性关系)
                        if ( list.getContext()!=null && CollectionUtils.isNotEmpty(list.getContext().getAttributeKeyVOS())){
                            list.getContext().getAttributeKeyVOS().forEach(l->{
                                GoodsInfoSpecDetailRelNest goodsInfoSpecDetailRelNest=new GoodsInfoSpecDetailRelNest();
                                goodsInfoSpecDetailRelNest.setGoodsId(l.getGoodsId());
                                goodsInfoSpecDetailRelNest.setGoodsInfoId(l.getGoodsInfoId());
                                goodsInfoSpecDetailRelNest.setDetailName(l.getGoodsAttributeValue());
                                goodsInfoSpecDetailRelNest.setSpecName(l.getAttributeId());
                                specDetails.add(goodsInfoSpecDetailRelNest);
                            });
                            esGoodsInfo.setSpecDetails(specDetails);
                        }
                    }





                    //填充签约有效期时间
                    if (MapUtils.isNotEmpty(storeMap) && storeMap.containsKey(goods.getStoreId())) {
                        StoreVO store = storeMap.get(goods.getStoreId());
                        esGoodsInfo.setContractStartDate(store.getContractStartDate());
                        esGoodsInfo.setContractEndDate(store.getContractEndDate());
                        esGoodsInfo.setStoreState(store.getStoreState().toValue());
                    }

                    //获取店铺等级
                    if (storeCateGoodsMap.containsKey(goods.getGoodsId())) {
                        esGoodsInfo.setStoreCateIds(storeCateGoodsMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRelaVO::getStoreCateId).collect(Collectors.toList()));
                    }

                    esGoodsInfo.setAuditStatus(goods.getAuditStatus().toValue());
                    if (MapUtils.isNotEmpty(distributionStoreSettingMap) && distributionStoreSettingMap.containsKey(goods.getStoreId().toString())) {
                        esGoodsInfo.setDistributionGoodsStatus(distributionStoreSettingMap.get(goods.getStoreId().toString()) == DefaultFlag.NO ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO);
                    }
                    //填充商品销量
                    esGoodsInfo.getGoodsInfo().setGoodsSalesNum(goods.getGoodsSalesNum() == null ? 0 :
                            goods.getGoodsSalesNum());
                    //填充商品收藏量
                    esGoodsInfo.getGoodsInfo().setGoodsCollectNum(goods.getGoodsCollectNum() == null ? 0 :
                            goods.getGoodsCollectNum());
                    //填充商品评论数
                    esGoodsInfo.getGoodsInfo().setGoodsEvaluateNum(goods.getGoodsEvaluateNum() == null ? 0 :
                            goods.getGoodsEvaluateNum());
                    //填充商品好评数
                    esGoodsInfo.getGoodsInfo().setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum() == null ? 0 : goods.getGoodsFavorableCommentNum());
                    //填充好评率数据
                    Long goodsFeedbackRate = 0L;
                    if (Objects.nonNull(goods.getGoodsEvaluateNum()) && Objects.nonNull(goods.getGoodsFavorableCommentNum())
                            && goods.getGoodsEvaluateNum() > 0L) {
                        goodsFeedbackRate =
                                (long) ((double) goods.getGoodsFavorableCommentNum() / (double) goods.getGoodsEvaluateNum() * 100);
                    }
                    esGoodsInfo.getGoodsInfo().setGoodsFeedbackRate(goodsFeedbackRate);

                    IndexQuery iq = new IndexQuery();

                    iq.setObject(esGoodsInfo);
                    iq.setIndexName(EsConstants.DOC_GOODS_INFO_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_GOODS_INFO_TYPE);
//                    iq.setParentId(esGoodsInfo.getCateBrandId());
                    esGoodsInfos.add(iq);
                });

                //持久化分类品牌
                if (CollectionUtils.isNotEmpty(esCateBrands)) {
                    esCateBrandRepository.saveAll(esCateBrands);
                    elasticsearchTemplate.refresh(EsCateBrand.class);
                }

                //持久化商品

                // logger.info("持久化数据：==="+JSON.toJSONString(esGoodsInfos));
                elasticsearchTemplate.bulkIndex(esGoodsInfos);
                elasticsearchTemplate.refresh(EsGoodsInfo.class);

                //清空缓存
                goodses.clear();
                intervalPriceMap.clear();
                goodsInfoSpecDetailMap.clear();
                goodsPropDetailMap.clear();
                specMap.clear();
                levelPriceMap.clear();
                customerPriceMap.clear();
            }
        }

        logger.info(String.format("商品索引结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));

        this.initEsGoods(request);

    }


    /**
     * 初始化SPU持化于ES
     */
    public void initEsGoods(EsGoodsInfoRequest request) {
        if (CollectionUtils.isNotEmpty(request.getSkuIds())) {
            //批量查询所有SKU信息列表
            GoodsInfoListByConditionRequest infoQueryRequest = new GoodsInfoListByConditionRequest();
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            infoQueryRequest.setCompanyInfoId(request.getCompanyInfoId());
            infoQueryRequest.setGoodsInfoIds(request.getSkuIds());
            infoQueryRequest.setStoreId(request.getStoreId());
            infoQueryRequest.setGoodsId(request.getGoodsId());
            infoQueryRequest.setGoodsIds(request.getGoodsIds());
            infoQueryRequest.setBrandIds(request.getBrandIds());
            infoQueryRequest.setGoodsSource(1);
            infoQueryRequest.setStoreIds(request.getStoreIds());
            infoQueryRequest.setAddedFlag(request.getAddedFlag());
            //仅仅是同步长沙
            List<GoodsInfoVO> goodsInfos =
                    goodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos();
            List<String> goodsIds =
                    goodsInfos.stream().map(GoodsInfoVO::getGoodsId).distinct().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(request.getGoodsIds())) {
                request.getGoodsIds().addAll(goodsIds);
            } else {
                request.setGoodsIds(goodsIds);
            }
        }
        //批量查询所有SPU信息列表

        GoodsCountByConditionRequest goodsCountQueryRequest = new GoodsCountByConditionRequest();
        goodsCountQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsCountQueryRequest.setCompanyInfoId(request.getCompanyInfoId());
        goodsCountQueryRequest.setGoodsIds(request.getGoodsIds());
        goodsCountQueryRequest.setStoreId(request.getStoreId());
        goodsCountQueryRequest.setBrandIds(request.getBrandIds());
        goodsCountQueryRequest.setGoodsSource(1);
        goodsCountQueryRequest.setStoreIds(request.getStoreIds());
        goodsCountQueryRequest.setAddedFlag(request.getAddedFlag());
        if (StringUtils.isNotBlank(request.getGoodsId())) {
            if (goodsCountQueryRequest.getGoodsIds() == null) {
                goodsCountQueryRequest.setGoodsIds(Collections.singletonList(request.getGoodsId()));
            } else {
                goodsCountQueryRequest.getGoodsIds().add(request.getGoodsId());
            }
        }

        Long totalCount = goodsQueryProvider.countByCondition(goodsCountQueryRequest).getContext().getCount();

        if (totalCount <= 0) {
            return;
        }

        logger.info("商品spu索引开始");
        long startTime = System.currentTimeMillis();

        //考虑到goodsIds可能比较多，采用分页读goodsIds数组，进行查询数据
        //一个ID因采用uuid有32位字符串，mysql的SQL语句最大默认限制1M
        int pageSize = 500;//每批查询500个GoodsID
        long pageCount = 0L;
        long m = totalCount % pageSize;
        if (m > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }
        Long delBrandId = 0L;
        Map<String, GoodsVO> goodses = new HashMap<>();
        Map<Long, StoreVO> storeMap = new HashMap<>();
        Map<String, DefaultFlag> distributionStoreSettingMap = new HashMap<>();
        Map<String, List<GoodsLevelPriceNest>> levelPriceMap = new HashMap<>();
        Map<String, List<GoodsCustomerPriceNest>> customerPriceMap = new HashMap<>();
        Map<String, List<GoodsIntervalPriceVO>> intervalPriceMap = new HashMap<>();
        Map<String, List<GoodsInfoSpecDetailRelNest>> goodsInfoSpecDetailMap = new HashMap<>();
        Map<String, List<GoodsPropDetailRelVO>> goodsPropDetailMap = new HashMap<>();
        Map<String, List<StoreCateGoodsRelaVO>> storeCateGoodsMap = new HashMap<>();
        Map<String, List<GoodsWareStockVO>> goodsStockMap = new HashMap<>();
        Map<Long,Long> storeMallMap = new HashMap<>();
        Map<Long,Long> storeMarketMap = new HashMap<>();
//        Map<String, List<MarketingForEndVO>> marketingMap = new HashMap<>();
        Map<Long, String> specMap = new HashMap<>();
        GoodsCateListByConditionRequest goodsCateListByConditionRequest = new GoodsCateListByConditionRequest();
        goodsCateListByConditionRequest.setDelFlag(DeleteFlag.NO.toValue());
        Map<Long, GoodsCateVO> goodsCateMap =
                goodsCateQueryProvider.listByCondition(goodsCateListByConditionRequest).getContext().getGoodsCateVOList().stream().collect(Collectors.toMap(GoodsCateVO::getCateId, goodsCate -> goodsCate));
        Map<Long, GoodsBrandVO> goodsBrandMap = goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().delFlag(DeleteFlag.NO.toValue()).build())

                .getContext().getGoodsBrandVOList().stream().collect(Collectors.toMap(GoodsBrandVO::getBrandId,
                        goodsBrand -> goodsBrand));
        GoodsLabelListRequest goodsLabelListRequest = new GoodsLabelListRequest();
        goodsLabelListRequest.setDelFlag(DeleteFlag.NO);
        //冗余标签数据备用
        List<GoodsLabelVO> goodsLabelVOList = goodsLabelQueryProvider
                .list(goodsLabelListRequest)
                .getContext().getGoodsLabelVOList();


        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        KsBeanUtil.copyPropertiesThird(goodsCountQueryRequest, goodsQueryRequest);
        goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsQueryRequest.setGoodsIds(goodsCountQueryRequest.getGoodsIds());

        GoodsPageRequest goodsPageRequest = new GoodsPageRequest();
        KsBeanUtil.copyPropertiesThird(goodsCountQueryRequest, goodsPageRequest);
        goodsPageRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsPageRequest.setGoodsIds(goodsCountQueryRequest.getGoodsIds());
        goodsPageRequest.setGoodsSource(1);
        for (int i = 0; i < pageCount; i++) {

            goodsPageRequest.setPageNum(i);
            goodsPageRequest.setPageSize(pageSize);
            List<GoodsVO> goodsList =
                    goodsQueryProvider.page(goodsPageRequest).getContext().getGoodsPage().getContent();
            //List<GoodsVO> goodsList = goodsQueryProvider.listByConditionForPage(goodsQueryRequest).getContext()
            // .getGoodsVOList();
            if (CollectionUtils.isNotEmpty(goodsList)) {
                List<String> goodsIds = goodsList.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
                goodses.putAll(goodsList.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, goods -> goods)));

                GoodsInfoListByConditionRequest infoQueryRequest = new GoodsInfoListByConditionRequest();
                infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
                infoQueryRequest.setGoodsIds(goodsIds);
                infoQueryRequest.setGoodsSource(1);
                List<GoodsInfoVO> goodsinfos =
                        goodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos();
                List<String> goodsInfoIds =
                        goodsinfos.stream().map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
                List<String> skuIds = goodsinfos.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());


                BaseResponse<GoodsLabelRelaByGoodsIdsResponse> byGoodsIds =
                        goodsLabelRelaQueryProvider.findByGoodsIds(GoodsLabelRelaByGoodsIdsRequest.builder().goodsIds(goodsIds).build());
                List<GoodsLabelRelaVO> goodsLabelRelaVOList = byGoodsIds.getContext().getGoodsLabelRelaVOList();
                //根据GoodsId进行分组
                //Goodsid +labelIds
                Map<String, List<GoodsLabelRelaVO>> goodsLabelRealMap = goodsLabelRelaVOList.stream().collect(Collectors.groupingBy(GoodsLabelRelaVO::getGoodsId));

                //营销活动Map
                /*MarketingMapGetByGoodsIdRequest marketingRequest = MarketingMapGetByGoodsIdRequest.builder()
                        .goodsInfoIdList(goodsInfoIds)
                        .deleteFlag(DeleteFlag.NO)
                        .excludeStatus(MarketingStatus.ENDED).build();
                marketingMap.putAll(marketingQueryProvider.getMarketingMapByGoodsId(marketingRequest).getContext()
                .getListMap());*/

                //区间价Map
                intervalPriceMap.putAll(getIntervalPriceMapBySkuId(goodsInfoIds));

                //等级价Map
                levelPriceMap.putAll(goodsLevelPriceQueryProvider.listBySkuIds(new GoodsLevelPriceBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsLevelPriceList()
                        .stream().map(price -> KsBeanUtil.convert(price, GoodsLevelPriceNest.class))
                        .collect(Collectors.groupingBy(GoodsLevelPriceNest::getGoodsInfoId)));

                //客户价Map
                customerPriceMap.putAll(goodsCustomerPriceQueryProvider.listBySkuIds(new GoodsCustomerPriceBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsCustomerPriceVOList().stream()
                        .map(price -> KsBeanUtil.convert(price, GoodsCustomerPriceNest.class))
                        .collect(Collectors.groupingBy(GoodsCustomerPriceNest::getGoodsInfoId)));

                //规格Map
                specMap.putAll(getGoodsSpecMapByGoodsId(goodsIds));

                //规格值Map
                goodsInfoSpecDetailMap.putAll(goodsInfoSpecDetailRelQueryProvider.listBySkuIds(new GoodsInfoSpecDetailRelBySkuIdsRequest(goodsInfoIds))
                        .getContext().getGoodsInfoSpecDetailRelVOList().stream()
                        .map(price -> KsBeanUtil.convert(price, GoodsInfoSpecDetailRelNest.class))
                        .collect(Collectors.groupingBy(GoodsInfoSpecDetailRelNest::getGoodsInfoId)));

                //属性值Map
                goodsPropDetailMap.putAll(getPropDetailRelList(goodsIds));
                //属性值Map
                goodsPropDetailMap.putAll(getPropDetailRelList(goodsIds));
                StoreCateGoodsRelaListByGoodsIdsRequest storeCateGoodsRelaListByGoodsIdsRequest=new StoreCateGoodsRelaListByGoodsIdsRequest();
                storeCateGoodsRelaListByGoodsIdsRequest.setGoodsIds(goodsIds);
                //商品店铺分类Map
                storeCateGoodsMap.putAll(storeCateGoodsRelaQueryProvider.listByGoodsIds(new StoreCateGoodsRelaListByGoodsIdsRequest(goodsIds)).getContext().getStoreCateGoodsRelaVOList().stream().collect(Collectors.groupingBy(StoreCateGoodsRelaVO::getGoodsId)));

                //分仓库存
                GoodsWareStockListResponse response = goodsWareStockQueryProvider.findByGoodsInfoIdIn(
                        GoodsWareStockByGoodsForIdsRequest.builder().goodsForIdList(skuIds).build()).getContext();
                if(CollectionUtils.isNotEmpty(response.getGoodsWareStockVOList())){
                    List<GoodsWareStockVO> goodsWareStockVOS = response.getGoodsWareStockVOList();
                    List<Long> storeIds = goodsWareStockVOS.stream().map(GoodsWareStockVO::getStoreId).distinct().collect(Collectors.toList());
                    List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.listByStoreId(WareHouseListByStoreIdRequest.builder()
                            .storeIds(storeIds).build()).getContext().getWareHouseVOList();
                    Map<Long, WareHouseVO> wareHouseMap = wareHouseVOList.stream().collect(Collectors.toMap(WareHouseVO::getWareId, s -> s));
                    for (GoodsWareStockVO inner: goodsWareStockVOS){
                        if(Objects.nonNull(wareHouseMap.get(inner.getWareId()))){
                            inner.setWareHouseType(wareHouseMap.get(inner.getWareId()).getWareHouseType());
                        }
                    }
                    goodsStockMap.putAll(goodsWareStockVOS.stream().collect(Collectors.groupingBy(GoodsWareStockVO::getGoodsInfoId)));
                }

                List<Long> storeIds =
                        goodses.values().stream().map(GoodsVO::getStoreId).filter(Objects::nonNull).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(storeIds)) {
                    storeMap.putAll(storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                            (storeIds)).getContext().getStoreVOList().stream().collect(Collectors
                            .toMap
                                    (StoreVO::getStoreId, store -> store)));
                    List<String> stringList =
                            storeIds.stream().distinct().map(storeId -> String.valueOf(storeId)).collect(Collectors.toList());
                    distributionStoreSettingMap.putAll(distributionSettingQueryProvider.listByStoreIds(new DistributionStoreSettingListByStoreIdsRequest(stringList)).getContext().getList().stream().collect(Collectors.toMap(DistributionStoreSettingVO::getStoreId, DistributionStoreSettingVO::getOpenFlag)));

                    warpStoreMallMarket(storeMallMap, storeMarketMap, storeIds);
                }

                //遍历SKU，填充SPU、图片
                List<IndexQuery> esGoodsInfos = new ArrayList<>();
                List<EsCateBrand> esCateBrands = new ArrayList<>();
                goodsList.forEach(goods -> {

                    //填充规格名
                    List<GoodsInfoSpecDetailRelNest> detailRels = goodsInfoSpecDetailMap.get(goods.getGoodsId());
                    if (CollectionUtils.isNotEmpty(detailRels)) {
                        detailRels.forEach(specDetailRel -> specDetailRel.setSpecName(specMap.get(specDetailRel.getSpecId())));
                    }

                    EsGoods esGoods = new EsGoods();
                    esGoods.setId(goods.getGoodsId());
                    esGoods.setMallId(storeMallMap.get(goods.getStoreId()));
                    esGoods.setMarketId(storeMarketMap.get(goods.getStoreId()));
                    //商品排序序号
                    esGoods.setGoodsSeqNum(goods.getGoodsSeqNum());
                    esGoods.setGoodsSubtitle(goods.getGoodsSubtitle());
                    esGoods.setGoodsSubtitleNew(goods.getGoodsSubtitleNew());
                    
                    // 店铺内商品排序
                    esGoods.setStoreGoodsSeqNum(goods.getStoreGoodsSeqNum());
                    
                    // 设置spu的分类和品牌
                    EsCateBrand esCateBrand = new EsCateBrand();
                    GoodsCateVO goodsCate = goodsCateMap.get(goods.getCateId());
                    GoodsBrandVO goodsBrand = new GoodsBrandVO();
                    goodsBrand.setBrandId(0L);
                    if (goods.getBrandId() != null) {
                        goodsBrand = goodsBrandMap.get(goods.getBrandId());

                    }
                    //#2
//                    if (StringUtils.isNotBlank(goods.getLabelIdStr())) {
//                        List<String> labelIds = Arrays.asList(goods.getLabelIdStr().split(","));
//                        List<GoodsLabelVO> goodsLabels = goodsLabelVOList.stream()
//                                .filter(item -> labelIds.contains(item.getId().toString()))
//                                .collect(Collectors.toList());
//                        esGoods.setGoodsLabels(goodsLabels);
//                    }

                    List<GoodsLabelRelaVO> goodsLabelRelaVOS = goodsLabelRealMap.get(goods.getGoodsId());
                    if (CollectionUtils.isNotEmpty(goodsLabelRelaVOS)) {
                        List<Long> labelIdList = goodsLabelRelaVOS.stream().map(GoodsLabelRelaVO::getLabelId).collect(Collectors.toList());
                        //当前商品的关联标签
                        String labelIds = StringUtils.join(labelIdList, ",");
                        List<GoodsLabelVO> goodsLabels = goodsLabelVOList.stream()
                                .filter(item -> labelIds.contains(item.getId().toString()))
                                .collect(Collectors.toList());
                        esGoods.setGoodsLabels(goodsLabels);
                    }

                    if (goodsCate != null) {
                        esCateBrand = esCateBrandService.putEsCateBrand(goodsCate, goodsBrand);
                    }

                    if (Objects.nonNull(esCateBrand.getGoodsBrand())) {
                        esGoods.setGoodsBrand(esCateBrand.getGoodsBrand());
                    }

                    if (Objects.nonNull(esCateBrand.getGoodsCate())) {
                        esGoods.setGoodsCate(esCateBrand.getGoodsCate());
                    }
                    // 添加品牌排序
                    if (Objects.nonNull(esCateBrand.getGoodsCate()) && Objects.nonNull(esCateBrand.getGoodsBrand())) {
                        try {
                            CateBrandSortRelVO cateBrandSortRelVO = cateBrandSortRelQueryProvider.getById(CateBrandSortRelByIdRequest.builder()
                                    .brandId(esCateBrand.getGoodsBrand().getBrandId())
                                    .cateId(esCateBrand.getGoodsCate().getCateId())
                                    .build()).getContext().getCateBrandSortRelVO();
                            if (Objects.nonNull(cateBrandSortRelVO) && Long.valueOf(0).compareTo(cateBrandSortRelVO.getSerialNo()) < 0) {
                                esGoods.getGoodsBrand().setBrandRelSeqNum(Integer.valueOf(cateBrandSortRelVO.getSerialNo().toString()));
                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }

                    }
//                    esGoods.setCateBrandId(
//                            String.valueOf(goods.getCateId()).concat("_")
//                                    .concat(String.valueOf(
//                                            Objects.nonNull(goods.getBrandId()) ? goods.getBrandId() : delBrandId)));

                    esGoods.setAddedTime(goods.getAddedTime());
                    esGoods.setLowGoodsName(StringUtils.lowerCase(goods.getGoodsName()));
                    levelPriceMap.keySet().forEach(goodsInfoId -> {
                        if (goodsIds.contains(goodsInfoId)) {
                            esGoods.getGoodsLevelPrices().addAll(levelPriceMap.get(goodsInfoId));
                        }
                    });
                    customerPriceMap.keySet().forEach(goodsInfoId -> {
                        if (goodsInfoIds.contains(goodsInfoId)) {
                            esGoods.getCustomerPrices().addAll(customerPriceMap.get(goodsInfoId));
                        }
                    });

                    //分配规格值
//                    if (CollectionUtils.isNotEmpty(detailRels)) {
//                        detailRels.forEach(specDetailRel -> specDetailRel.setAllDetailName(specDetailRel.getDetailName()));
//                        esGoods.setSpecDetails(detailRels.stream().filter(specDetailRel -> specDetailRel.getGoodsId().equals(goods.getGoodsId())).collect(Collectors.toList()));
//                    }

                    //分配属性值
                    if (CollectionUtils.isNotEmpty(goodsPropDetailMap.get(goods.getGoodsId()))) {
                        esGoods.setPropDetails(goodsPropDetailMap.get(goods.getGoodsId()).stream().map(rel -> {
                            GoodsPropDetailRelVO relES = new GoodsPropDetailRelVO();
                            relES.setPropId(rel.getPropId());
                            relES.setDetailId(rel.getDetailId());
                            return relES;
                        }).collect(Collectors.toList()));
                    }
                    if (null!=request && null!=request.getGoodsId()){
                        //分配属性值值(新的属性关系)
                        BaseResponse<GoodsAttributeKeyListResponse> attributeKeyListResponseBaseResponse = goodsAttributeKeyQueryProvider.getNotGoodsList(GoodsAttributeKeyQueryRequest.builder().goodsId(request.getGoodsId()).build());
                        List<GoodsInfoSpecDetailRelNest> specDetails=new ArrayList<>();
                        //分配规格值(新的属性关系)
                        if ( attributeKeyListResponseBaseResponse.getContext()!=null && CollectionUtils.isNotEmpty(attributeKeyListResponseBaseResponse.getContext().getAttributeKeyVOS())){

                            attributeKeyListResponseBaseResponse.getContext().getAttributeKeyVOS().forEach(l->{
                                GoodsInfoSpecDetailRelNest goodsInfoSpecDetailRelNest=new GoodsInfoSpecDetailRelNest();
                                goodsInfoSpecDetailRelNest.setGoodsId(l.getGoodsId());
                                goodsInfoSpecDetailRelNest.setGoodsInfoId(l.getGoodsInfoId());
                                goodsInfoSpecDetailRelNest.setDetailName(l.getGoodsAttributeValue());
                                goodsInfoSpecDetailRelNest.setSpecName(l.getAttributeId());
                                specDetails.add(goodsInfoSpecDetailRelNest);
                            });
                            esGoods.setSpecDetails(specDetails);
                        }
                    }



                    //填充签约有效期时间
                    if (MapUtils.isNotEmpty(storeMap) && storeMap.containsKey(goods.getStoreId())) {
                        StoreVO store = storeMap.get(goods.getStoreId());
                        esGoods.setContractStartDate(store.getContractStartDate());
                        esGoods.setContractEndDate(store.getContractEndDate());
                        esGoods.setStoreState(store.getStoreState().toValue());
                    }

                    //获取店铺等级
                    if (storeCateGoodsMap.containsKey(goods.getGoodsId())) {
                        esGoods.setStoreCateIds(storeCateGoodsMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRelaVO::getStoreCateId).collect(Collectors.toList()));
                    }


                    esGoods.setAuditStatus(goods.getAuditStatus().toValue());
                    if (MapUtils.isNotEmpty(distributionStoreSettingMap) && distributionStoreSettingMap.containsKey(goods.getStoreId().toString())) {
                        esGoods.setDistributionGoodsStatus(distributionStoreSettingMap.get(goods.getStoreId().toString()) == DefaultFlag.NO ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO);
                    }
                    List<GoodsInfoNest> goodsInfoNests = new ArrayList<>();
                    goodsinfos.stream().filter(goodsInfoVO -> goods.getGoodsId().equals(goodsInfoVO.getGoodsId())).forEach(goodsInfoVO -> {
                        GoodsInfoNest goodsInfoNest = KsBeanUtil.convert(goodsInfoVO, GoodsInfoNest.class);
                        goodsInfoNest.setEsSortPrice();
                        goodsInfoNest.setEnterPriseAuditStatus(goodsInfoVO.getEnterPriseAuditState() != null ?
                                goodsInfoVO.getEnterPriseAuditState().toValue() : EnterpriseAuditState.INIT.toValue());
                        goodsInfoNest.setGoodsWareStockVOS(goodsStockMap.get(goodsInfoVO.getGoodsInfoId()));
                        goodsInfoNest.setGoodsWeight(goods.getGoodsWeight());
                        goodsInfoNest.setFreightTempId(goods.getFreightTempId());
                        goodsInfoNest.setWareId(goodsInfoVO.getWareId());
                        goodsInfoNest.setParentGoodsInfoId(goodsInfoVO.getParentGoodsInfoId());
                        goodsInfoNest.setIsSurprisePrice(goodsInfoVO.getIsSurprisePrice());
                        //第三方商家需要重新判断
                        if ( goodsInfoNest.getCompanyType().equals(CompanyType.SUPPLIER)){
                            goodsInfoNest.setGoodsSubtitle(goodsInfoVO.getGoodsSubtitle());
                            goodsInfoNest.setGoodsSubtitleNew(goodsInfoVO.getGoodsSubtitleNew());
                        }else{
                            goodsInfoNest.setGoodsSubtitle(goods.getGoodsSubtitleNew());
                            goodsInfoNest.setGoodsSubtitleNew(goods.getGoodsSubtitleNew());
                        }
                        if (null!=goods && null!=goods.getStoreId()){
                            BaseResponse<StoreInfoResponse> storeInfoResponseBaseResponse = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(goods.getStoreId()).build());
                            if (storeInfoResponseBaseResponse.getContext()!=null && storeInfoResponseBaseResponse.getContext().getStoreName()!=null){
                                goodsInfoNest.setStoreName(storeInfoResponseBaseResponse.getContext().getStoreName());
                            }
                        }
                        goodsInfoNests.add(goodsInfoNest);
                    });
                    Optional<GoodsInfoNest> any = goodsInfoNests.stream().findAny();
                    if (any.isPresent()){
                        esGoods.setWareId(any.get().getWareId());
                    }
                    esGoods.setGoodsInfos(goodsInfoNests);
                    //填充商品销量
                    esGoods.setGoodsSalesNum(goods.getGoodsSalesNum() == null ? 0 : goods.getGoodsSalesNum());
                    //填充商品收藏量
                    esGoods.setGoodsCollectNum(goods.getGoodsCollectNum() == null ? 0 : goods.getGoodsCollectNum());
                    //填充商品评论数
                    esGoods.setGoodsEvaluateNum(goods.getGoodsEvaluateNum() == null ? 0 : goods.getGoodsEvaluateNum());
                    //填充商品好评数
                    esGoods.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum() == null ? 0 :
                            goods.getGoodsFavorableCommentNum());
                    //填充好评率数据
                    Long goodsFeedbackRate = 0L;
                    if (Objects.nonNull(goods.getGoodsEvaluateNum()) && Objects.nonNull(goods.getGoodsFavorableCommentNum())
                            && goods.getGoodsEvaluateNum() > 0L) {
                        goodsFeedbackRate =
                                (long) ((double) goods.getGoodsFavorableCommentNum() / (double) goods.getGoodsEvaluateNum() * 100);
                    }
                    esGoods.setGoodsFeedbackRate(goodsFeedbackRate);
                    IndexQuery iq = new IndexQuery();
                    iq.setObject(esGoods);
                    iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_GOODS_TYPE);
//                    iq.setParentId(esGoods.getCateBrandId());
                    esGoodsInfos.add(iq);
                });

                //持久化分类品牌
                if (CollectionUtils.isNotEmpty(esCateBrands)) {
                    esCateBrandRepository.saveAll(esCateBrands);
                    elasticsearchTemplate.refresh(EsCateBrand.class);
                }

                //持久化商品
                elasticsearchTemplate.bulkIndex(esGoodsInfos);
                elasticsearchTemplate.refresh(EsGoods.class);

                //清空缓存
                goodses.clear();
                intervalPriceMap.clear();
                goodsInfoSpecDetailMap.clear();
                goodsPropDetailMap.clear();
                specMap.clear();
                levelPriceMap.clear();
                customerPriceMap.clear();
            }

        }
//        this.initEsGoodsBrandRelSeqNo(0);
        //同步到库存到ES中
//        List<GoodsWareStockVO> stockVOList = goodsWareStockQueryProvider.
//                list(new GoodsWareStockListRequest().setGoodsInfoIds(request.getSkuIds())).getContext().getGoodsWareStockVOList();
//        this.updateStockGoodsInfo(stockVOList);
        logger.info(String.format("商品spu索引结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));
    }

    private void warpStoreMallMarket(Map<Long, Long> storeMallMap,Map<Long, Long> storeMarketMap, List<Long> storeIds) {
        try {
            final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
            relationPageRequest.setStoreIds(storeIds);
            relationPageRequest.setPageNum(0);
            relationPageRequest.setPageSize(Integer.MAX_VALUE);
            relationPageRequest.setDeleteFlag(DeleteFlag.NO);
            final BaseResponse<CompanyMallContractRelationPageResponse> storeMallRes = companyIntoPlatformQueryProvider.pageContractRelation(relationPageRequest);
            final List<CompanyMallContractRelationVO> storeMallList = storeMallRes.getContext().getPage().getContent();
            if (CollectionUtils.isNotEmpty(storeMallList)){
                storeMallList.forEach(t -> {
                    if (Objects.equals(t.getRelationType(), MallContractRelationType.TAB.getValue())){
                        storeMallMap.put(t.getStoreId(), Long.valueOf(t.getRelationValue()));
                    }else if (Objects.equals(t.getRelationType(), MallContractRelationType.MARKET.getValue())){
                        storeMarketMap.put(t.getStoreId(), Long.valueOf(t.getRelationValue()));
                    }
                });
            }
        } catch (Exception e) {
            logger.error("warpStoreMallMarket error,storeIds:{}", JSON.toJSONString(storeIds), e);
        }
    }


    private void initEsGoodsBrandRelSeqNo(int page) {
        CateBrandSortRelPageRequest pageReq = new CateBrandSortRelPageRequest();
        pageReq.setPageNum(page);
        pageReq.setPageSize(500);
        MicroServicePage<CateBrandSortRelVO> cateBrandSortRelVOPage =
                cateBrandSortRelQueryProvider.page(pageReq).getContext().getCateBrandSortRelVOPage();
        if (Objects.nonNull(cateBrandSortRelVOPage)) {
            List<CateBrandSortRelVO> content = cateBrandSortRelVOPage.getContent();
            content.stream().forEach(entity -> {
                GoodsBrandVO goodsBrandVO = new GoodsBrandVO();
                goodsBrandVO.setBrandId(entity.getBrandId());
                if (entity.getSerialNo() != null) {
                    goodsBrandVO.setBrandSeqNum(Integer.parseInt(String.valueOf(entity.getSerialNo())));
                } else {
                    goodsBrandVO.setBrandSeqNum(null);
                }
                updateBrandSerialNoCommon(goodsBrandVO, entity.getCateId());
            });

//            updateBrandSerialNo(content);
            if (cateBrandSortRelVOPage.getTotalPages() > page) {
                initEsGoodsBrandRelSeqNo(page + 1);
            }
        }

    }
//    private void initEsGoodsBrandRelSeqNo(int page) {
//        CateBrandSortRelPageRequest pageReq = new CateBrandSortRelPageRequest();
//        pageReq.setPageNum(page);
//        pageReq.setPageSize(500);
//        MicroServicePage<CateBrandSortRelVO> cateBrandSortRelVOPage =
//                cateBrandSortRelQueryProvider.page(pageReq).getContext().getCateBrandSortRelVOPage();
//        if (Objects.nonNull(cateBrandSortRelVOPage)) {
//            List<CateBrandSortRelVO> content = cateBrandSortRelVOPage.getContent();
//            content.stream().forEach(entity -> {
//                GoodsBrandVO goodsBrandVO = new GoodsBrandVO();
//                goodsBrandVO.setBrandId(entity.getBrandId());
//                if (entity.getSerialNo() != null) {
//                    goodsBrandVO.setBrandSeqNum(Integer.parseInt(String.valueOf(entity.getSerialNo())));
//                } else {
//                    goodsBrandVO.setBrandSeqNum(null);
//                }
//                updateBrandSerialNoCommon(goodsBrandVO, entity.getCateId());
//            });
//
////            updateBrandSerialNo(content);
//            if (cateBrandSortRelVOPage.getTotalPages() > page) {
//                initEsGoodsBrandRelSeqNo(page + 1);
//            }
//        }
//
//    }

    /**
     * 上下架
     *
     * @param addedFlag    上下架状态
     * @param goodsIds     商品id列表
     * @param goodsInfoIds 商品skuId列表
     */
    public void updateAddedStatus(Integer addedFlag, List<String> goodsIds, List<String> goodsInfoIds,Integer goodsInfoType) {

        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        GoodsInfoListByConditionRequest infoQueryRequest = new GoodsInfoListByConditionRequest();
        if (goodsIds != null) { // 如果传了goodsIds，则按spu查
            queryRequest.setGoodsIds(goodsIds);
            infoQueryRequest.setGoodsIds(goodsIds);
        }
        if (goodsInfoIds != null) { // 如果传了goodsInfoIds，则按sku查
            queryRequest.setGoodsInfoIds(goodsInfoIds);
            infoQueryRequest.setGoodsInfoIds(goodsInfoIds);
        }
        if (goodsInfoType !=null){
            queryRequest.setGoodsInfoType(goodsInfoType);
        }

        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        LocalDateTime now = LocalDateTime.now();

        Map<String, GoodsInfoVO> goodsInfoMap =
                goodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, c -> c));
        if (esGoodsInfoList != null) {
            esGoodsInfoList.forEach(esGoodsInfo -> {
                esGoodsInfo.getGoodsInfo().setAddedFlag(addedFlag);
                if (goodsInfoMap.containsKey(esGoodsInfo.getGoodsInfo().getGoodsInfoId())) {
                    GoodsInfoVO info = goodsInfoMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId());
                    esGoodsInfo.getGoodsInfo().setAddedTime(info.getAddedTime());
                    esGoodsInfo.setAddedTime(info.getAddedTime());
                } else {
                    esGoodsInfo.getGoodsInfo().setAddedTime(now);
                    esGoodsInfo.setAddedTime(now);
                }
                esGoodsInfo.getGoodsInfo().setEsSortPrice();

                // EsGoodsInfo里的addedTime
                Map<String, Object> esGoodsInfoMap = new HashMap<>();
                esGoodsInfoMap.put("addedTime", esGoodsInfo.getAddedTime().format(DateTimeFormatter.ofPattern("yyyy" +
                        "-MM-dd HH:mm:ss.SSS")));

                // EsGoodsInfo里的GoodsInfo
                Map<String, String> map = new HashMap<>();
                map.put("addedFlag", addedFlag == null ? "" : addedFlag.toString());
                map.put("addedTime", esGoodsInfo.getGoodsInfo().getAddedTime().format(DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss.SSS")));
                esGoodsInfoMap.put("goodsInfo", map);

                client.prepareUpdate()
                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
            });
        }
        queryRequest.setQueryGoods(true);
        Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
        List<IndexQuery> esGoodsQuery = new ArrayList<>();
        if (esGoodsList != null) {
            esGoodsList.forEach(esGoods -> {
                esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                    esGoodsInfo.setAddedFlag(addedFlag);
                    if (goodsInfoMap.containsKey(esGoodsInfo.getGoodsInfoId())) {
                        GoodsInfoVO info = goodsInfoMap.get(esGoodsInfo.getGoodsInfoId());
                        esGoodsInfo.setAddedTime(info.getAddedTime());
                        esGoods.setAddedTime(info.getAddedTime());
                    } else {
                        esGoods.setAddedTime(now);
                        esGoodsInfo.setAddedTime(now);
                    }
                    esGoodsInfo.setEsSortPrice();
                });

                IndexQuery iq = new IndexQuery();
                iq.setId(esGoods.getId());
                iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
//                iq.setIndexName(EsConstants.INDEX_NAME);
                iq.setType(EsConstants.DOC_GOODS_TYPE);
//                iq.setParentId(esGoods.getCateBrandId());
                iq.setObject(esGoods);
                esGoodsQuery.add(iq);

            });
            if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                elasticsearchTemplate.bulkIndex(esGoodsQuery);
            }
        }
    }
    /**
     * 上下架
     *
     * @param addedFlag    上下架状态
     * @param goodsIds     商品id列表
     * @param goodsInfoIds 商品skuId列表
     */
    public void storeUpdateAddedStatus(Integer addedFlag, List<String> goodsIds, List<String> goodsInfoIds,Integer goodsInfoType) {

        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        GoodsInfoListByConditionRequest infoQueryRequest = new GoodsInfoListByConditionRequest();
        if (goodsIds != null) { // 如果传了goodsIds，则按spu查
            queryRequest.setGoodsIds(goodsIds);
            infoQueryRequest.setGoodsIds(goodsIds);
        }
        if (goodsInfoIds != null) { // 如果传了goodsInfoIds，则按sku查
            queryRequest.setGoodsInfoIds(goodsInfoIds);
            infoQueryRequest.setGoodsInfoIds(goodsInfoIds);
        }
        if (goodsInfoType !=null){
            queryRequest.setGoodsInfoType(goodsInfoType);
        }

        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        LocalDateTime now = LocalDateTime.now();

        Map<String, GoodsInfoVO> goodsInfoMap =
                goodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, c -> c));
        if (esGoodsInfoList != null) {
            esGoodsInfoList.forEach(esGoodsInfo -> {
                //直接取数据库
                BaseResponse<GoodsInfoByIdResponse> goodsInfoByIdResponseBaseResponse = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(esGoodsInfo.getGoodsInfo().getGoodsInfoId()).build());
                if (goodsInfoByIdResponseBaseResponse.getContext()!=null){
                    esGoodsInfo.getGoodsInfo().setAddedFlag(goodsInfoByIdResponseBaseResponse.getContext().getAddedFlag());
                }else{
                    esGoodsInfo.getGoodsInfo().setAddedFlag(addedFlag);
                }
                if (goodsInfoMap.containsKey(esGoodsInfo.getGoodsInfo().getGoodsInfoId())) {
                    GoodsInfoVO info = goodsInfoMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId());
                    esGoodsInfo.getGoodsInfo().setAddedTime(info.getAddedTime());
                    esGoodsInfo.setAddedTime(info.getAddedTime());
                } else {
                    esGoodsInfo.getGoodsInfo().setAddedTime(now);
                    esGoodsInfo.setAddedTime(now);
                }
                esGoodsInfo.getGoodsInfo().setEsSortPrice();

                // EsGoodsInfo里的addedTime
                Map<String, Object> esGoodsInfoMap = new HashMap<>();
                esGoodsInfoMap.put("addedTime", esGoodsInfo.getAddedTime().format(DateTimeFormatter.ofPattern("yyyy" +
                        "-MM-dd HH:mm:ss.SSS")));

                // EsGoodsInfo里的GoodsInfo
                Map<String, String> map = new HashMap<>();
                map.put("addedFlag", addedFlag == null ? "" : addedFlag.toString());
                map.put("addedTime", esGoodsInfo.getGoodsInfo().getAddedTime().format(DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss.SSS")));
                esGoodsInfoMap.put("goodsInfo", map);

                client.prepareUpdate()
                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
            });
        }
        queryRequest.setQueryGoods(true);
        Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
        List<IndexQuery> esGoodsQuery = new ArrayList<>();
        if (esGoodsList != null) {
            esGoodsList.forEach(esGoods -> {
                esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                    BaseResponse<GoodsInfoByIdResponse> goodsInfoByIdResponseBaseResponse = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(esGoodsInfo.getGoodsInfoId()).build());
                    if (goodsInfoByIdResponseBaseResponse.getContext()!=null){
                        esGoodsInfo.setAddedFlag(goodsInfoByIdResponseBaseResponse.getContext().getAddedFlag());
                    }else{
                        esGoodsInfo.setAddedFlag(addedFlag);
                    }

                    if (goodsInfoMap.containsKey(esGoodsInfo.getGoodsInfoId())) {
                        GoodsInfoVO info = goodsInfoMap.get(esGoodsInfo.getGoodsInfoId());
                        esGoodsInfo.setAddedTime(info.getAddedTime());
                        esGoods.setAddedTime(info.getAddedTime());
                    } else {
                        esGoods.setAddedTime(now);
                        esGoodsInfo.setAddedTime(now);
                    }
                    esGoodsInfo.setEsSortPrice();
                });

                IndexQuery iq = new IndexQuery();
                iq.setId(esGoods.getId());
                iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
//                iq.setIndexName(EsConstants.INDEX_NAME);
                iq.setType(EsConstants.DOC_GOODS_TYPE);
//                iq.setParentId(esGoods.getCateBrandId());
                iq.setObject(esGoods);
                esGoodsQuery.add(iq);

            });
            if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                elasticsearchTemplate.bulkIndex(esGoodsQuery);
            }
        }
    }
    /**
     * 根据商品批量删除
     *
     * @param goodsIds
     */
    public void deleteByGoods(List<String> goodsIds) {
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setGoodsIds(goodsIds);
        this.delete(queryRequest);
    }

    /**
     * 批量删除
     *
     * @param skuIds SKU编号
     */
    public void delete(List<String> skuIds) {
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(skuIds);
        this.delete(queryRequest);
    }

    /**
     * 批量删除
     *
     * @param queryRequest 参数
     */
    public void delete(EsGoodsInfoQueryRequest queryRequest) {
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        if (esGoodsInfoList != null) {
            esGoodsInfoList.forEach(esGoodsInfo ->
                    client.prepareDelete()
                            .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                            .setIndex(EsConstants.INDEX_NAME)
                            .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                            .setParent(esGoodsInfo.getCateBrandId())
                            .setId(esGoodsInfo.getId()).execute().actionGet());
        }
        queryRequest.setQueryGoods(true);
        Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
        elasticsearchTemplate.refresh(EsGoodsInfo.class);
        if (esGoodsList != null) {
            esGoodsList.forEach(esGoods -> {
                client.prepareDelete()
                        .setIndex(EsConstants.DOC_GOODS_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_TYPE)
//                        .setParent(esGoods.getCateBrandId())
                        .setId(esGoods.getId()).execute().actionGet();
            });
        }
        elasticsearchTemplate.refresh(EsGoods.class);
    }


    /**
     * 使用标准分词对字符串分词
     *
     * @param text 待分词文本
     * @return 分此后的词条
     */
    public String analyze(String text) {
        return analyze(text, EsConstants.DEF_ANALYZER);
    }

    /**
     * 根据给定的分词器对字符串进行分词
     *
     * @param text     要分词的文本
     * @param analyzer 指定分词器
     * @return 分词后的词条列表
     */
    private String analyze(String text, String analyzer) {
        final String fAnalyzer = StringUtils.isBlank(analyzer) ? "simple" : analyzer;
        AnalyzeRequestBuilder requestBuilder = client.admin().indices().prepareAnalyze(text).setAnalyzer(fAnalyzer);
        AnalyzeResponse response = client.admin().indices().analyze(requestBuilder.request()).actionGet();
        List<String> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(response.getTokens())) {
            res.addAll(response.getTokens().stream().map(AnalyzeResponse.AnalyzeToken::getTerm).collect(Collectors.toList()));
        }
        res.addAll(Arrays.asList(text.split("[^0-9]+")));
        res.add(text);
        return StringUtils.join(res, " ");
    }

    /**
     * 根据商品spu批量获取商品属性关键Map
     *
     * @param goodsIds 商品id
     * @return 商品属性关键Map内容<商品id, 商品属性关联list>
     */
    private Map<String, List<GoodsPropDetailRelVO>> getPropDetailRelList(List<String> goodsIds) {
        GoodsPropDetailRelByIdsRequest relByIdsRequest = new GoodsPropDetailRelByIdsRequest();
        relByIdsRequest.setGoodsIds(goodsIds);
        List<GoodsPropDetailRelVO> goodsPropDetailRelVOList =
                goodsQueryProvider.getRefByGoodIds(relByIdsRequest).getContext().getGoodsPropDetailRelVOList();
        return goodsPropDetailRelVOList.stream().collect(Collectors.groupingBy(GoodsPropDetailRelVO::getGoodsId));
    }

    /**
     * 根据商品spu批量获取规格键值Map
     *
     * @param goodsIds 商品id
     * @return 规格键值Map内容<规格id, 规格名称>
     */
    private Map<Long, String> getGoodsSpecMapByGoodsId(List<String> goodsIds) {
        List<GoodsSpecVO> voList = goodsSpecQueryProvider.listByGoodsIds(
                GoodsSpecListByGoodsIdsRequest.builder().goodsIds(goodsIds).build()).getContext().getGoodsSpecVOList();
        return voList.stream().collect(Collectors.toMap(GoodsSpecVO::getSpecId, GoodsSpecVO::getSpecName));
    }

    /**
     * 根据商品sku批量获取区间价键值Map
     *
     * @param skuIds 商品skuId
     * @return 区间价键值Map内容<商品skuId, 区间价列表>
     */
    private Map<String, List<GoodsIntervalPriceVO>> getIntervalPriceMapBySkuId(List<String> skuIds) {
        List<GoodsIntervalPriceVO> voList = goodsIntervalPriceQueryProvider.listByGoodsIds(
                GoodsIntervalPriceListBySkuIdsRequest.builder().skuIds(skuIds).build()).getContext().getGoodsIntervalPriceVOList();
        return voList.stream().collect(Collectors.groupingBy(GoodsIntervalPriceVO::getGoodsInfoId));
    }

    /**
     * 删除品牌时，更新es数据
     */
    public void delBrandIds(List<Long> brandIds, Long storeId) {
//        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
//        queryRequest.setBrandIds(brandIds);
//        queryRequest.setStoreId(storeId);
//        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
//        if (!esGoodsInfoList.iterator().hasNext()) {
//            return;
//        }

//        esGoodsInfoList.forEach(esGoodsInfo ->
//                client.prepareDelete()
//                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
////                        .setIndex(EsConstants.INDEX_NAME)
////                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
////                        .setParent(esGoodsInfo.getCateBrandId())
//                        .setId(esGoodsInfo.getId()).execute().actionGet());


//        //重新建立cateBrand索引
//        List<Long> cateIds = new ArrayList<>();
//        List<IndexQuery> esGoodsInfos = new ArrayList<>();
//        esGoodsInfoList.forEach(item -> {
//            GoodsBrandNest brand = new GoodsBrandNest();
//            brand.setBrandId(0L);
//            item.setGoodsBrand(brand);
//            item.getGoodsInfo().setBrandId(null);
//            cateIds.add(item.getGoodsInfo().getCateId());
//            IndexQuery iq = new IndexQuery();
//
//            iq.setObject(item);
//            iq.setIndexName(EsConstants.DOC_GOODS_INFO_TYPE);
////            iq.setParentId(item.getCateBrandId());
////            iq.setIndexName(EsConstants.INDEX_NAME);
////            iq.setType(EsConstants.DOC_GOODS_INFO_TYPE);
//            esGoodsInfos.add(iq);
//        });

//        List<EsCateBrand> esCateBrands = new ArrayList<>();
//        GoodsCateListByConditionRequest request = new GoodsCateListByConditionRequest();
//        request.setCateIds(cateIds);
//        List<GoodsCateVO> voList = goodsCateQueryProvider.listByCondition(request).getContext().getGoodsCateVOList();
//        if (CollectionUtils.isNotEmpty(voList)) {
//            voList.forEach(goodsCate -> {
//                EsCateBrand esCateBrand = new EsCateBrand();
//                GoodsBrandNest brand = new GoodsBrandNest();
//                brand.setBrandId(0L);
//                esCateBrand.setGoodsBrand(brand);
//
//                esCateBrand.setGoodsCate(KsBeanUtil.convert(goodsCate, GoodsCateNest.class));
//                esCateBrand.setId(String.valueOf(goodsCate.getCateId()).concat("_").concat(String.valueOf(brand
//                .getBrandId())));
//                esCateBrands.add(esCateBrand);
//            });
//        }
//
//        esCateBrandRepository.saveAll(esCateBrands);
//        //生成新数据
//        elasticsearchTemplate.bulkIndex(esGoodsInfos);
        deleteBrandNameCommon(brandIds, EsConstants.DOC_GOODS_TYPE, storeId);
        deleteBrandNameCommon(brandIds, EsConstants.DOC_GOODS_INFO_TYPE, storeId);

    }

    /**
     * 删除店铺分类时更新es数据
     *
     * @param storeCateIds
     * @param storeId
     */
    public void delStoreCateIds(List<Long> storeCateIds, Long storeId) {
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setStoreCateIds(storeCateIds);
        queryRequest.setStoreId(storeId);
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        if (!esGoodsInfoList.iterator().hasNext()) {
            return;
        }
        List<IndexQuery> esGoodsInfos = new ArrayList<>();
        esGoodsInfoList.forEach(item -> {
                    item.setStoreCateIds(null);
                    IndexQuery iq = new IndexQuery();
                    iq.setObject(item);
                    iq.setIndexName(EsConstants.DOC_GOODS_INFO_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_GOODS_INFO_TYPE);
//                    iq.setParentId(item.getCateBrandId());
                    esGoodsInfos.add(iq);
                }
        );

        //生成新数据
        elasticsearchTemplate.bulkIndex(esGoodsInfos);
    }

    /**
     * 更新分销佣金、分销商品审核状态（添加分销商品时）
     *
     * @param esGoodsInfoDistributionRequest
     * @return true:批量更新成功，false:批量更新失败（部分更新成功）
     */
    public Boolean modifyDistributionCommission(EsGoodsInfoModifyDistributionCommissionRequest esGoodsInfoDistributionRequest) {
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        List<DistributionGoodsInfoModifyDTO> distributionGoodsInfoDTOList =
                esGoodsInfoDistributionRequest.getDistributionGoodsInfoDTOList();
        List<String> goodsInfoIds =
                distributionGoodsInfoDTOList.stream().map(DistributionGoodsInfoModifyDTO::getGoodsInfoId).collect(Collectors.toList());
        Map<String, BigDecimal> stringBigDecimalMap = distributionGoodsInfoDTOList.stream().collect(Collectors.toMap
                (DistributionGoodsInfoModifyDTO::getGoodsInfoId, g -> g.getCommissionRate()));
        Map<String, BigDecimal> commssionlMap = distributionGoodsInfoDTOList.stream().collect(Collectors.toMap
                (DistributionGoodsInfoModifyDTO::getGoodsInfoId, g -> g.getDistributionCommission()));
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        Integer result = 0;
        if (esGoodsInfoList != null) {
            Map<String, Object> esGoodsInfoMap;
            Map<String, Object> map;
            for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
                esGoodsInfoMap = new HashMap<>(4);
                map = new HashMap<>(4);
                BigDecimal commission = commssionlMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId());
                BigDecimal commissionRate = stringBigDecimalMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId());
                if (Objects.nonNull(commissionRate)) {
                    map.put("commissionRate", commissionRate.doubleValue());
                }
                if (Objects.nonNull(commission)) {
                    map.put("distributionCommission", commission.doubleValue());
                }
                map.put("distributionGoodsAudit", esGoodsInfoDistributionRequest.getDistributionGoodsAudit().toValue());
                esGoodsInfoMap.put("goodsInfo", map);
                if (Objects.nonNull(esGoodsInfoDistributionRequest.getDistributionGoodsStatus())) {
                    esGoodsInfoMap.put("distributionGoodsStatus",
                            esGoodsInfoDistributionRequest.getDistributionGoodsStatus());
                }
                client.prepareUpdate()
                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
                result++;
            }
            queryRequest.setQueryGoods(true);
            Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
            List<IndexQuery> esGoodsQuery = new ArrayList<>();
            if (esGoodsList != null) {
                esGoodsList.forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                        BigDecimal commissionRate = stringBigDecimalMap.get(esGoodsInfo.getGoodsInfoId());
                        BigDecimal commission = commssionlMap.get(esGoodsInfo.getGoodsInfoId());
                        if (Objects.nonNull(commissionRate)) {
                            esGoodsInfo.setCommissionRate(commissionRate);
                            esGoodsInfo.setDistributionCommission(commission);
                        }
                        esGoodsInfo.setDistributionGoodsAudit(esGoodsInfoDistributionRequest.getDistributionGoodsAudit());
                    });
                    if (Objects.nonNull(esGoodsInfoDistributionRequest.getDistributionGoodsStatus())) {
                        esGoods.setDistributionGoodsStatus(esGoodsInfoDistributionRequest.getDistributionGoodsStatus());
                    }
                    IndexQuery iq = new IndexQuery();
                    iq.setId(esGoods.getId());
                    iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_GOODS_TYPE);
//                    iq.setParentId(esGoods.getCateBrandId());
                    iq.setObject(esGoods);
                    esGoodsQuery.add(iq);

                });
                if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                    elasticsearchTemplate.bulkIndex(esGoodsQuery);
                }
            }
        }
        return result.equals(goodsInfoIds.size()) ? Boolean.TRUE : Boolean.FALSE;
    }


    /**
     * 更新分销商品审核状态（平台端审核时）
     *
     * @param request
     * @return true:批量更新成功，false:批量更新失败（部分更新成功）
     */
    public Boolean modifyDistributionGoodsAudit(EsGoodsInfoModifyDistributionGoodsAuditRequest request) {
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        List<String> goodsInfoIds = request.getGoodsInfoIds();
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        Integer result = 0;
        if (esGoodsInfoList != null) {
            Integer distributionGoodsAudit = request.getDistributionGoodsAudit();
            DistributionGoodsAudit goodsAudit = DistributionGoodsAudit.values()[distributionGoodsAudit];
            Map<String, Object> esGoodsInfoMap;
            Map<String, Object> map;
            for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
                esGoodsInfoMap = new HashMap<>(4);
                map = new HashMap<>(4);
                map.put("distributionGoodsAuditReason",
                        distributionGoodsAudit.equals(DistributionGoodsAudit.CHECKED.toValue()) ? "" :
                                request.getDistributionGoodsAuditReason());
                map.put("distributionGoodsAudit", distributionGoodsAudit);
                esGoodsInfoMap.put("goodsInfo", map);
                client.prepareUpdate()
                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();

                result++;
            }
            queryRequest.setQueryGoods(true);
            Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
            List<IndexQuery> esGoodsQuery = new ArrayList<>();
            if (esGoodsList != null) {
                esGoodsList.forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                        Boolean aBoolean =
                                goodsInfoIds.stream().anyMatch(goodsInfoId -> goodsInfoId.equals(esGoodsInfo.getGoodsInfoId()));
                        if (aBoolean) {
                            esGoodsInfo.setDistributionGoodsAudit(goodsAudit);
                            esGoodsInfo.setDistributionGoodsAuditReason(distributionGoodsAudit.equals(DistributionGoodsAudit.CHECKED.toValue()) ? "" : request.getDistributionGoodsAuditReason());

                        }
                    });

                    IndexQuery iq = new IndexQuery();
                    iq.setId(esGoods.getId());
                    iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_GOODS_TYPE);
//                    iq.setParentId(esGoods.getCateBrandId());
                    iq.setObject(esGoods);
                    esGoodsQuery.add(iq);

                });
                if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                    elasticsearchTemplate.bulkIndex(esGoodsQuery);
                }
            }
        }
        return result.equals(goodsInfoIds.size()) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 商家-社交分销开关设置，更新分销商品状态
     *
     * @param request
     * @return
     */
    public Boolean modifyDistributionGoodsStatus(EsGoodsInfoModifyDistributionGoodsStatusRequest request) {
        Long storeId = request.getStoreId();
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setDistributionGoodsAudit(DistributionGoodsAudit.CHECKED.toValue());
        queryRequest.setStoreId(storeId);
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        if (esGoodsInfoList != null) {
            Integer distributionGoodsStatus = request.getDistributionGoodsStatus();
            for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
                Map<String, Object> map = new HashMap<>(4);
                map.put("distributionGoodsStatus", distributionGoodsStatus);
                client.prepareUpdate()
                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getId()).setDoc(map).execute().actionGet();
                client.prepareUpdate()
                        .setIndex(EsConstants.DOC_GOODS_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getGoodsInfo().getGoodsId()).setDoc(map).execute().actionGet();
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 更新分销商品审核状态（修改商品销售模式：零售->批发）
     *
     * @param spuId
     * @return true:批量更新成功，false:批量更新失败（部分更新成功）
     */
    public Boolean modifyDistributionGoodsAudit(String spuId) {
        EsGoodsInfoQueryRequest request = new EsGoodsInfoQueryRequest();
        request.setGoodsIds(Collections.singletonList(spuId));
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(request.getWhereCriteria());
        if (esGoodsInfoList != null) {
            Map<String, Object> esGoodsInfoMap;
            Map<String, Object> map;
            for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
                esGoodsInfoMap = new HashMap<>(4);
                map = new HashMap<>(4);
                map.put("distributionGoodsAuditReason", "");
                map.put("distributionGoodsAudit", DistributionGoodsAudit.COMMON_GOODS.toValue());
                esGoodsInfoMap.put("goodsInfo", map);
                client.prepareUpdate()
                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
            }
            request.setQueryGoods(true);
            Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(request.getWhereCriteria());
            List<IndexQuery> esGoodsQuery = new ArrayList<>();
            if (esGoodsList != null) {
                esGoodsList.forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                        esGoodsInfo.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                        esGoodsInfo.setDistributionGoodsAuditReason("");
                    });

                    IndexQuery iq = new IndexQuery();
                    iq.setId(esGoods.getId());
                    iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_GOODS_TYPE);
//                    iq.setParentId(esGoods.getCateBrandId());
                    iq.setObject(esGoods);
                    esGoodsQuery.add(iq);

                });
                if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                    elasticsearchTemplate.bulkIndex(esGoodsQuery);
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 修改spu和sku的商品分类索引信息
     *
     * @param goodsCateVO
     */
    public void updateCateName(GoodsCateVO goodsCateVO) {
        updateCateNameCommon(goodsCateVO);
    }

    /**
     * 修改spu和sku的商品品牌索引信息
     *
     * @param goodsBrandVO
     */
    public void updateBrandName(GoodsBrandVO goodsBrandVO) {
        updateBrandNameCommon(goodsBrandVO);
    }


    /**
     * 修改spu和sku的商品品牌索引排序信息
     *
     * @param cateId
     * @param cateBrandSortRelVOList
     */
    public void updateBrandSerialNoList(Long cateId, List<CateBrandSortRelVO> cateBrandSortRelVOList) {
        // 删除cateId下面的三级类目排序信息
        updateBrandSerialNoByCateId(cateId, null);

        // 根据三级类目id和品牌id，设置排序
        for (CateBrandSortRelVO cateBrandSortRelVO : cateBrandSortRelVOList) {
            GoodsBrandVO goodsBrandVO = new GoodsBrandVO();
            goodsBrandVO.setBrandId(cateBrandSortRelVO.getBrandId());
            goodsBrandVO.setBrandSeqNum(Integer.parseInt(String.valueOf(cateBrandSortRelVO.getSerialNo())));
            updateBrandSerialNoCommon(goodsBrandVO, cateId);

        }

    }

    /**
     * 修改spu和sku的商品品牌索引排序信息
     *
     * @param goodsBrandVO
     */
    public void updateBrandSerialNo(GoodsBrandVO goodsBrandVO, Long cateId) {
        updateBrandSerialNoCommon(goodsBrandVO, cateId);
    }

    /**
     * 修改商品品牌
     *
     * @param goodsBrandVO 品牌bean
     * @return
     */
    private Long updateBrandSerialNoCommon(GoodsBrandVO goodsBrandVO, Long cateId) {
        String queryName = "goodsBrand.brandId";
        String cateIdName = "goodsCate.cateId";
        Integer brandSeqNum = goodsBrandVO.getBrandSeqNum();
        if (brandSeqNum != null && brandSeqNum == 0) {
            brandSeqNum = null;
        }
        Long resCount = 0L;
        if (Objects.nonNull(goodsBrandVO)) {
            Client client = elasticsearchTemplate.getClient();
            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(termQuery(queryName, goodsBrandVO.getBrandId()));
            boolQueryBuilder.must(termQuery(cateIdName, cateId));
            updateByQuery = updateByQuery.source(EsConstants.DOC_GOODS_INFO_TYPE, EsConstants.DOC_GOODS_TYPE)
                    //查询要修改的结果集
                    .filter(boolQueryBuilder)
                    //修改操作
                    .script(new Script("ctx._source.goodsBrand.brandRelSeqNum="+brandSeqNum));
            BulkByScrollResponse response = updateByQuery.get();
            resCount = response.getUpdated();
            elasticsearchTemplate.refresh(EsGoodsInfo.class);
            elasticsearchTemplate.refresh(EsGoods.class);
        }
        return resCount;
    }

    /**
     * 修改三级类目下的品牌排序序号
     *
     * @param cateId 三级类目id
     * @return
     */
    public Long updateBrandSerialNoByCateId(Long cateId, Integer brandRelSeqNum) {
        String cateIdName = "goodsCate.cateId";
        Long resCount = 0L;
        if (Objects.nonNull(cateId)) {
            Client client = elasticsearchTemplate.getClient();
            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQuery = updateByQuery.source(EsConstants.DOC_GOODS_INFO_TYPE, EsConstants.DOC_GOODS_TYPE)
                    //查询要修改的结果集
                    .filter(QueryBuilders.termQuery(cateIdName, cateId))
                    //修改操作
                    .script(new Script("ctx._source.goodsBrand.brandRelSeqNum="+brandRelSeqNum));
            BulkByScrollResponse response = updateByQuery.get();
            resCount = response.getUpdated();
        }
        return resCount;
    }


    /**
     * 修改商品分类
     *
     * @param goodsCateVO 分类bean
     * @return
     */
    private Long updateCateNameCommon(GoodsCateVO goodsCateVO) {
        String queryName = "goodsCate.cateId";
        String updateName = "goodsCate.cateName";
        Long resCount = 0L;
        if (Objects.nonNull(goodsCateVO)) {
            Client client = elasticsearchTemplate.getClient();

            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);

            updateByQuery = updateByQuery.source(EsConstants.DOC_GOODS_INFO_TYPE, EsConstants.DOC_GOODS_TYPE)
                    //查询要修改的结果集
                    .filter(QueryBuilders.termQuery(queryName, goodsCateVO.getCateId()))
                    //修改操作
                    .script(new Script("ctx._source." + updateName + "='" + goodsCateVO.getCateName() + "'"));
            BulkByScrollResponse response = updateByQuery.get();
            resCount = response.getUpdated();
        }
        return resCount;
    }

    /**
     * 修改商品品牌
     *
     * @param goodsBrandVO 品牌bean
     * @return
     */
    private Long updateBrandNameCommon(GoodsBrandVO goodsBrandVO) {
        String queryName = "goodsBrand.brandId";
        String updateName = "goodsBrand.brandName";
        Long resCount = 0L;
        if (Objects.nonNull(goodsBrandVO)) {
            Client client = elasticsearchTemplate.getClient();

            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);

            updateByQuery = updateByQuery.source(EsConstants.DOC_GOODS_INFO_TYPE, EsConstants.DOC_GOODS_TYPE)
                    //查询要修改的结果集
                    .filter(QueryBuilders.termQuery(queryName, goodsBrandVO.getBrandId()))
                    //修改操作
                    .script(new Script("ctx._source." + updateName + "='" + goodsBrandVO.getBrandName() + "';" +
                            "ctx._source.goodsBrand.brandSeqNum="+goodsBrandVO.getBrandSeqNum()));
            BulkByScrollResponse response = updateByQuery.get();
            resCount = response.getUpdated();
        }
        return resCount;
    }

    /**
     * 删除商品品牌之后同步es
     *
     * @param ids       删除的品牌Id
     * @param indexName 索引名称
     * @param storeId   店铺Id
     * @return
     */
    private Long deleteBrandNameCommon(List<Long> ids, String indexName, Long storeId) {
        String queryName = "goodsBrand.brandId";
        String queryStoreName = StringUtils.equals(EsConstants.DOC_GOODS_TYPE, indexName) ? "goodsInfos.storeId" :
                "goodsInfo.storeId";
        Long resCount = 0L;
        if (CollectionUtils.isNotEmpty(ids)) {
            Client client = elasticsearchTemplate.getClient();
            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery(queryName, ids));
            if (Objects.nonNull(storeId)) {
                boolQueryBuilder.must(termQuery(queryStoreName, storeId));
            }
            updateByQuery = updateByQuery.source(indexName)
                    //查询要修改的结果集
                    .filter(boolQueryBuilder)
                    //修改操作
                    .script(new Script("ctx._source.goodsBrand.brandName='';ctx._source.goodsBrand.brandId='';ctx" +
                            "._source.goodsBrand.pinyin=''"));
            BulkByScrollResponse response = updateByQuery.get();
            resCount = response.getUpdated();
        }
        return resCount;
    }

    /**
     * 新增不需要审核的企业购商品时 刷新es
     *
     * @param batchEnterPrisePriceDTOS
     */
    public Boolean updateEnterpriseGoodsInfo(List<BatchEnterPrisePriceDTO> batchEnterPrisePriceDTOS, EnterpriseAuditState enterpriseAuditState) {
        List<String> goodsInfoIds = batchEnterPrisePriceDTOS.stream().map(BatchEnterPrisePriceDTO::getGoodsInfoId).collect(Collectors.toList());
        Map<String, BigDecimal> skuIdEnterprisePriceMap = new HashMap<>();
        batchEnterPrisePriceDTOS.forEach(b -> skuIdEnterprisePriceMap.put(b.getGoodsInfoId(), b.getEnterPrisePrice()));
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        Integer result = 0;
        if (esGoodsInfoList != null) {
            Map<String, Object> esGoodsInfoMap;
            Map<String, Object> map;
            queryRequest.setQueryGoods(true);
            Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
            List<IndexQuery> esGoodsQuery = new ArrayList<>();
            if (esGoodsList != null) {
                for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
                    esGoodsInfoMap = new HashMap<>(4);
                    map = new HashMap<>(4);
                    map.put("enterPriseAuditStatus", enterpriseAuditState.toValue());
                    map.put("enterPrisePrice", String.valueOf(skuIdEnterprisePriceMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId())));
                    esGoodsInfoMap.put("goodsInfo", map);
                    if (EnterpriseAuditState.CHECKED.equals(enterpriseAuditState)) {
                        map.put("esSortPrice", String.valueOf(skuIdEnterprisePriceMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId())));
                    }
                    client.prepareUpdate()
                            .setType(EsConstants.DOC_GOODS_INFO_TYPE)
                            .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
                            .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
                    result++;
                }
                esGoodsList.forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                        Boolean aBoolean =
                                goodsInfoIds.stream().anyMatch(goodsInfoId -> goodsInfoId.equals(esGoodsInfo.getGoodsInfoId()));
                        if (aBoolean) {
                            esGoodsInfo.setEnterPriseAuditStatus(enterpriseAuditState.toValue());
                            esGoodsInfo.setEnterPrisePrice(skuIdEnterprisePriceMap.get(esGoodsInfo.getGoodsInfoId()));
                            esGoodsInfo.setEsSortPrice();
                        }
                    });

                    IndexQuery iq = new IndexQuery();
                    iq.setId(esGoods.getId());
                    iq.setType(EsConstants.DOC_GOODS_TYPE);
                    iq.setObject(esGoods);
                    iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
                    esGoodsQuery.add(iq);

                });
                if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                    elasticsearchTemplate.bulkIndex(esGoodsQuery);
                }
            }
        }
        return result.equals(goodsInfoIds.size()) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 新增库存信息时 刷新es
     *
     * @param
     */
    public Boolean updateStockGoodsInfo(List<GoodsWareStockVO> standardGoodsList){
        List<String> goodsInfoIds = standardGoodsList.stream().map(GoodsWareStockVO::getGoodsInfoId).collect(Collectors.toList());
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        Integer result = 0;
        if (esGoodsInfoList != null) {
            Map<String, Object> esGoodsInfoMap;
            Map<String, List<Map<String, Object>>> goodsWareStockMap;
            List<Map<String, Object>> maps;
            queryRequest.setQueryGoods(true);
            Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
            List<IndexQuery> esGoodsQuery = new ArrayList<>();
            if (esGoodsList != null) {
                maps = new ArrayList<>(standardGoodsList.size());
                goodsWareStockMap = new HashMap<>();
                for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
                    esGoodsInfoMap = new HashMap<>(4);
                    standardGoodsList.forEach(stockVO -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("goodsInfoId",stockVO.getGoodsInfoId());
                        map.put("goodsInfoNo",stockVO.getGoodsInfoNo());
                        map.put("goodsId",stockVO.getGoodsId());
                        map.put("storeId",stockVO.getStoreId());
                        map.put("wareId",stockVO.getWareId());
                        map.put("stock",stockVO.getStock());
                        maps.add(map);
                    });
                    goodsWareStockMap.put("goodsWareStockVOS", maps);
                    esGoodsInfoMap.put("goodsInfo", goodsWareStockMap);
                    client.prepareUpdate()
                            .setType(EsConstants.DOC_GOODS_INFO_TYPE)
                            .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
                            .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
                    result++;
                }
                esGoodsList.forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                        Boolean aBoolean =
                                goodsInfoIds.stream().anyMatch(goodsInfoId -> goodsInfoId.equals(esGoodsInfo.getGoodsInfoId()));
                        if (aBoolean) {
                            esGoodsInfo.setGoodsWareStockVOS(standardGoodsList);
                        }
                    });

                    IndexQuery iq = new IndexQuery();
                    iq.setId(esGoods.getId());
                    iq.setType(EsConstants.DOC_GOODS_TYPE);
                    iq.setObject(esGoods);
                    iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
                    esGoodsQuery.add(iq);

                });
                if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                    elasticsearchTemplate.bulkIndex(esGoodsQuery);
                }
            }
        }
        return result.equals(goodsInfoIds.size()) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 更新企业购商品（平台端审核时）
     *
     * @param request
     * @return true:批量更新成功，false:批量更新失败（部分更新成功）
     */
    public Boolean modifyEnterpriseAuditStatus(EsGoodsInfoEnterpriseAuditRequest request) {
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        List<String> goodsInfoIds = request.getGoodsInfoIds();
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        Iterable<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        Integer result = 0;
        if (esGoodsInfoList != null) {
            Map<String, Object> esGoodsInfoMap;
            Map<String, Object> map;
            for (EsGoodsInfo esGoodsInfo : esGoodsInfoList) {
                esGoodsInfoMap = new HashMap<>(4);
                map = new HashMap<>(4);
                map.put("enterPriseGoodsAuditReason",
                        request.getEnterPriseAuditStatus().equals(EnterpriseAuditState.NOT_PASS) ? request.getEnterPriseGoodsAuditReason() : "");
                map.put("enterPriseAuditStatus", request.getEnterPriseAuditStatus().toValue());
                if (EnterpriseAuditState.CHECKED.equals(request.getEnterPriseAuditStatus())) {
                    map.put("esSortPrice", String.valueOf(esGoodsInfo.getGoodsInfo().getEnterPrisePrice()));
                }
                esGoodsInfoMap.put("goodsInfo", map);
                client.prepareUpdate()
                        .setType(EsConstants.DOC_GOODS_INFO_TYPE)
                        .setIndex(EsConstants.DOC_GOODS_INFO_TYPE)
                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
                result++;
            }
            queryRequest.setQueryGoods(true);
            Iterable<EsGoods> esGoodsList = esGoodsElasticRepository.search(queryRequest.getWhereCriteria());
            List<IndexQuery> esGoodsQuery = new ArrayList<>();
            if (esGoodsList != null) {
                esGoodsList.forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(esGoodsInfo -> {
                        Boolean aBoolean =
                                goodsInfoIds.stream().anyMatch(goodsInfoId -> goodsInfoId.equals(esGoodsInfo.getGoodsInfoId()));
                        if (aBoolean) {
                            esGoodsInfo.setEnterPriseAuditStatus(request.getEnterPriseAuditStatus().toValue());
                            esGoodsInfo.setEnterPriseGoodsAuditReason(request.getEnterPriseAuditStatus().equals(EnterpriseAuditState.NOT_PASS)
                                    ? request.getEnterPriseGoodsAuditReason() : "");
                            esGoodsInfo.setEsSortPrice();
                        }
                    });

                    IndexQuery iq = new IndexQuery();
                    iq.setId(esGoods.getId());
                    iq.setObject(esGoods);
                    iq.setIndexName(EsConstants.DOC_GOODS_TYPE);
                    iq.setType(EsConstants.DOC_GOODS_TYPE);
                    esGoodsQuery.add(iq);

                });
                if (CollectionUtils.isNotEmpty(esGoodsQuery)) {
                    elasticsearchTemplate.bulkIndex(esGoodsQuery);
                }
            }
        }
        return result.equals(goodsInfoIds.size()) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 更新特价商品（同步特价仓）
     *
     */
    public void modifySpecialGoods() {
        GoodsInfoListByIdsResponse goodsInfoListByIdsResponse = goodsInfoQueryProvider.findAllSpecialGoods().getContext();
        List<String> goodsIds = goodsInfoListByIdsResponse.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(goodsIds)) return;
        this.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsIds(goodsIds).build());
    }

    /**
     * 编辑商品排序
     *
     * @param goodsVO
     */
    public void modifyGoodsSeqNum(GoodsVO goodsVO) {
        if (Objects.nonNull(goodsVO)) {
            Client client = elasticsearchTemplate.getClient();

			String goodsScriptStr = "ctx._source.goodsSeqNum=";
			String goodsInfoScriptStr = "ctx._source.goodsInfo.goodsSeqNum=";
			if (goodsVO.getStoreId() != null) {
				goodsScriptStr = "ctx._source.storeGoodsSeqNum=";
				goodsInfoScriptStr = "ctx._source.goodsInfo.storeGoodsSeqNum=";
			}
            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQuery = updateByQuery.source(EsConstants.DOC_GOODS_TYPE)
                    //查询要修改的结果集
                    .filter(QueryBuilders.termQuery("id", goodsVO.getGoodsId()))
                    //修改操作
                    .script(new Script(goodsScriptStr + goodsVO.getGoodsSeqNum()));
            BulkByScrollResponse response = updateByQuery.get();
            response.getUpdated();

            UpdateByQueryRequestBuilder updateByQuery2 = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQuery2 = updateByQuery2.source(EsConstants.DOC_GOODS_INFO_TYPE)
                    //查询要修改的结果集
                    .filter(QueryBuilders.termQuery("goodsInfo.goodsId", goodsVO.getGoodsId()))
                    //修改操作
                    .script(new Script(goodsInfoScriptStr+ goodsVO.getGoodsSeqNum()));
            BulkByScrollResponse response2 = updateByQuery2.get();
            response2.getUpdated();
        }
    }

    /**
     * 编辑推荐商品排序
     * @param goodsInfoVO
     */
    public void modifyRecommendSort(GoodsInfoVO goodsInfoVO) {
        if (Objects.nonNull(goodsInfoVO)) {
            Client client = elasticsearchTemplate.getClient();

            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQuery = updateByQuery.source(EsConstants.DOC_GOODS_INFO_TYPE)
                    //查询需要修改的结果集
                    .filter(QueryBuilders.termQuery("id", goodsInfoVO.getGoodsInfoId()))
                    //修改操作
                    .script(new Script("ctx._source.goodsInfo.recommendSort=" + goodsInfoVO.getRecommendSort()));
            BulkByScrollResponse response = updateByQuery.get();
            response.getUpdated();
        }
    }

    /**
     * 批量更新
     *
     * @param goodsVOS
     */
    public void batchGoodsSeqNum(List<GoodsVO> goodsVOS) {
        goodsVOS.forEach(this::modifyGoodsSeqNum);
    }

    /**
     * 根据不同条件查询ES商品信息
     *
     * @param queryRequest
     * @return
     */
    public List<GoodsBrandVO> getEsBaseInfoByCateId(EsGoodsInfoQueryRequest queryRequest) {
        queryRequest.setPageNum(0);
        queryRequest.setPageSize(10);
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        queryRequest.setStoreState(StoreState.OPENING.toValue());
        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("brand_group").field("goodsBrand.brandId").size(2000));
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.build(searchResponse, resultsMapper));
        List<? extends EsSearchResponse.AggregationResultItem> brandBucket = response.getAggResultMap().get("brand_group");
        if (CollectionUtils.isNotEmpty(brandBucket)) {
            List<Long> brandIds = brandBucket.stream().map(EsSearchResponse.AggregationResultItem<String>::getKey).map(NumberUtils::toLong).collect(Collectors.toList());
            return goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().delFlag(DeleteFlag.NO.toValue()).brandIds(brandIds).build()).getContext().getGoodsBrandVOList();
        }
        return new ArrayList<>();
    }


    /**
     * 更新es商品特价价格
     *
     * @param goodsInfoVOList
     * @return
     */
    public void modifySpeciaPriceGoods(List<GoodsInfoVO> goodsInfoVOList) {
        Client client = elasticsearchTemplate.getClient();
        UpdateByQueryRequestBuilder goodsInfoQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        goodsInfoQuery = goodsInfoQuery.source(EsConstants.DOC_GOODS_INFO_TYPE);
        UpdateByQueryRequestBuilder goodsQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        goodsQuery = goodsQuery.source(EsConstants.DOC_GOODS_TYPE);


        Map<String, List<GoodsInfoVO>> goodsMap =
                goodsInfoVOList.stream().collect(Collectors.groupingBy(GoodsInfoVO::getGoodsId));

        for (Map.Entry<String, List<GoodsInfoVO>> stringListEntry : goodsMap.entrySet()) {
            String key = stringListEntry.getKey();


            String SciprtStr = "";
            List<GoodsInfoVO> stringListEntryValue = stringListEntry.getValue();
            for (int i = 0; i < stringListEntryValue.size(); i++) {
                SciprtStr +="ctx._source.goodsInfos[" + i + "].specialPrice=" + stringListEntryValue.get(i).getSpecialPrice().doubleValue() + ";";
            }

            Script script = new Script(SciprtStr);

            //查询要修改的结果集
            goodsQuery.filter(QueryBuilders.termQuery("id", key))
                    .script(script);
            BulkByScrollResponse goodsResponse = goodsQuery.get();
            goodsResponse.getUpdated();
            elasticsearchTemplate.refresh(EsGoods.class);
        }


        for (GoodsInfoVO goodsInfoVO : goodsInfoVOList) {



            //查询要修改的结果集
            goodsInfoQuery.filter(termQuery("goodsInfo.goodsInfoId", goodsInfoVO.getGoodsInfoId()))
                    .script(new Script("ctx._source.goodsInfo.specialPrice=" + goodsInfoVO.getSpecialPrice().doubleValue()));
            BulkByScrollResponse response = goodsInfoQuery.get();
            long resCount = response.getUpdated();
            elasticsearchTemplate.refresh(EsGoodsInfo.class);


        }
    }

    public List<GoodsBrandVO> listBrands(EsGoodsInfoQueryRequest queryRequest) {
        if(queryRequest.getCate3Id() != null){
            queryRequest.setCateId(queryRequest.getCate3Id());
        }else {
            // 获取分类树
            String result = goodsCateQueryProvider.getByCache().getContext().getResult();
            List<GoodsCateVO> goodsCateVOS = JSONArray.parseArray(result, GoodsCateVO.class);

            List<Long> cate3IdList = new ArrayList<>();
            if(queryRequest.getCate2Id() != null){
                GoodsCateVO goodsCateVO2 = null;
                for (GoodsCateVO goodsCateVO1 : goodsCateVOS) {
                    goodsCateVO2 = goodsCateVO1.getGoodsCateList().stream().filter(item -> item.getCateId().equals(queryRequest.getCate2Id()))
                            .findFirst().orElse(null);
                    if(goodsCateVO2 != null){
                        break;
                    }
                }
                if(goodsCateVO2 == null){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "分类信息错误！");
                }
                cate3IdList.addAll(goodsCateVO2.getGoodsCateList().stream().map(GoodsCateVO::getCateId).collect(Collectors.toList()));
            }else {
                if(queryRequest.getCate1Id() != null){
                    GoodsCateVO goodsCateVO1 = goodsCateVOS.stream().filter(item -> item.getCateId().equals(queryRequest.getCate1Id()))
                            .findFirst().orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "分类信息错误！"));
                    goodsCateVO1.getGoodsCateList().forEach(item-> cate3IdList.addAll(item.getGoodsCateList()
                            .stream().map(GoodsCateVO::getCateId).collect(Collectors.toList())));
                }else {
                    for (GoodsCateVO goodsCateVO1 : goodsCateVOS) {
                        goodsCateVO1.getGoodsCateList().forEach(item->{
                            cate3IdList.addAll(item.getGoodsCateList().stream().map(GoodsCateVO::getCateId).collect(Collectors.toList()));
                        });
                    }
                }
            }
            queryRequest.setCateIds(cate3IdList);
        }
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        queryRequest.setStoreState(StoreState.OPENING.toValue());
        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("brand_group").field("goodsBrand.brandId").size(2000));
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.build(searchResponse, resultsMapper));
        List<? extends EsSearchResponse.AggregationResultItem> brandBucket = response.getAggResultMap().get("brand_group");
        if (CollectionUtils.isNotEmpty(brandBucket)) {
            List<Long> brandIds = brandBucket.stream().map(EsSearchResponse.AggregationResultItem<String>::getKey).map(NumberUtils::toLong).collect(Collectors.toList());
            // 根据称量类型过滤品牌
            if(queryRequest.getClassifyType() != null){
                BaseResponse<List<Long>> longBaseResponse = goodsBrandQueryProvider.listByClassifyType(queryRequest.getClassifyType());
                List<Long> typeBrandIds = longBaseResponse.getContext();
                brandIds = brandIds.stream().filter(typeBrandIds::contains).collect(Collectors.toList());
            }
            if(CollectionUtils.isEmpty(brandIds)){
                return new ArrayList<>();
            }
            return goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().delFlag(DeleteFlag.NO.toValue()).brandIds(brandIds).build()).getContext().getGoodsBrandVOList();
        }
        return new ArrayList<>();
    }
}
