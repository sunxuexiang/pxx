package com.wanmi.sbc.es.elastic;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.EsConstants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.es.elastic.model.root.*;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsBaseResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.es.elastic.response.EsSearchResponse;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsattributekey.GoodsAttributeKeyQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodslabel.GoodsLabelQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodslabelrela.GoodsLabelRelaQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsCustomerPriceQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsLevelPriceQueryProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsInfoSpecDetailRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsSpecQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateGoodsRelaQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelByIdRequest;
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
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListByStoreIdRequest;
import com.wanmi.sbc.goods.api.response.goodsattributekey.GoodsAttributeKeyListResponse;
import com.wanmi.sbc.goods.api.response.goodslabelrela.GoodsLabelRelaByGoodsIdsResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.distribution.DistributionSettingQueryProvider;
import com.wanmi.sbc.marketing.api.request.distribution.DistributionStoreSettingListByStoreIdsRequest;
import com.wanmi.sbc.marketing.bean.vo.DistributionStoreSettingVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortOrder;
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
 * @description: 零售商品 es service
 * @author: XinJiang
 * @time: 2022/4/14 10:33
 */
@Slf4j
@Service
public class EsRetailGoodsInfoElasticService {

    @Autowired
    private RetailGoodsQueryProvider retailGoodsQueryProvider;

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;

    @Autowired
    private BulkGoodsQueryProvider bulkGoodsQueryProvider;


    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private GoodsLabelQueryProvider goodsLabelQueryProvider;

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private GoodsLabelRelaQueryProvider goodsLabelRelaQueryProvider;

    @Autowired
    private GoodsIntervalPriceQueryProvider goodsIntervalPriceQueryProvider;

    @Autowired
    private GoodsLevelPriceQueryProvider goodsLevelPriceQueryProvider;

    @Autowired
    private GoodsCustomerPriceQueryProvider goodsCustomerPriceQueryProvider;

    @Autowired
    private GoodsSpecQueryProvider goodsSpecQueryProvider;

    @Autowired
    private GoodsInfoSpecDetailRelQueryProvider goodsInfoSpecDetailRelQueryProvider;

    @Autowired
    private StoreCateGoodsRelaQueryProvider storeCateGoodsRelaQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private DistributionSettingQueryProvider distributionSettingQueryProvider;

    @Autowired
    private EsCateBrandService esCateBrandService;

    @Autowired
    private CateBrandSortRelQueryProvider cateBrandSortRelQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private ResultsMapper resultsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private EsRetailGoodsInfoElasticRepository esRetailGoodsInfoElasticRepository;

    @Autowired
    private EsRetailGoodsElasticRepository esRetailGoodsElasticRepository;
    @Autowired
    private GoodsAttributeKeyQueryProvider goodsAttributeKeyQueryProvider;

    @Autowired
    private Client client;

    /**
     * 分页查询ES商品(实现WEB的商品列表)
     *
     * @param queryRequest
     * @return
     */
    public EsGoodsResponse pageByGoods(EsGoodsInfoQueryRequest queryRequest) {
        EsGoodsResponse goodsResponse = EsGoodsResponse.builder().build();
        if (StringUtils.isNotBlank(queryRequest.getKeywords())) {
            queryRequest.setKeywords(esGoodsInfoElasticService.analyze(queryRequest.getKeywords()));
        }

        if (queryRequest.getCateId() != null) {
            //分类商品
            queryRequest = esGoodsInfoElasticService.wrapperGoodsCateToEsGoodsInfoQueryRequest(queryRequest);
        }

        //店铺分类，加入所有子分类
        if (CollectionUtils.isNotEmpty(queryRequest.getStoreCateIds())) {
            queryRequest = esGoodsInfoElasticService.wrapperStoreCateToEsGoodsInfoQueryRequest(queryRequest);
        }

        //设定排序
        if (queryRequest.getSortFlag() != null) {
            queryRequest = esGoodsInfoElasticService.wrapperSortToEsGoodsInfoQueryRequest(queryRequest);
        }

        //聚合品牌
        //queryRequest.putAgg(AggregationBuilders.terms("brand_group").field("goodsBrand.brandId").size(2000));

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
        log.info("pageByGoods esGoods query for sortFlag:"+queryRequest.getSortFlag());
        log.info("pageByGoods esGoods query for cateIds:"+queryRequest.getCateIds());
        if (Objects.nonNull(queryRequest.getSortFlag()) && queryRequest.getSortFlag() == 11){
            queryRequest.getSorts().removeAll(queryRequest.getSorts());
            queryRequest.putSort("goodsSeqNum", SortOrder.ASC);
        }
        log.info("pageByGoods esGoods query for sorts:"+queryRequest.getSorts());
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getRetailSearchCriteria(),
                searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));

        if (CollectionUtils.isEmpty(response.getGoodsData())) {
            goodsResponse.setEsGoods(new PageImpl<>(response.getGoodsData(),
                    PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), response.getTotal()));
            return goodsResponse;
        }
        List<String> goodsIds = response.getGoodsData().stream().map(EsGoods::getId).collect(Collectors.toList());
        List<String> skuIds = response.getGoodsData().stream().map(EsGoods::getGoodsInfos).flatMap(Collection::stream)
                .map(GoodsInfoNest::getGoodsInfoId).distinct().collect(Collectors.toList());
        //批量查询SPU数据
        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        goodsQueryRequest.setGoodsSource(1);
        List<GoodsVO> goodses = retailGoodsQueryProvider.retailListByCondition(goodsQueryRequest).getContext().getGoodsVOList();

        Map<String, GoodsInfoVO> goodsInfoMap = retailGoodsInfoQueryProvider.listGoodsInfoAndStcokByIds(
                GoodsInfoAndStockListByIdsRequest.builder().goodsInfoIds(skuIds).matchWareHouseFlag(queryRequest.getMatchWareHouseFlag()).build()
        ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));

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

                goodsInfo.setGoodsInfoImg(goodsInfoMap.getOrDefault(goodsInfo.getGoodsId(), new GoodsInfoVO()).getGoodsInfoImg());

//                //为空，则以商品主图
//                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
////                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
//                    goodsInfo.setGoodsInfoImg(goodsInfoMap.getOrDefault(goodsInfo.getGoodsId(), new GoodsInfoVO()).getGoodsInfoImg());
//                }
//
//                //如果不是特殊请求则使用商品主图
//                if (!queryRequest.getImageFlag()) {
//                    goodsInfo.setGoodsInfoImg(goodsInfoMap.getOrDefault(goodsInfo.getGoodsId(), new GoodsInfoVO()).getGoodsInfoImg());
//                }
                goodsInfo.setGoodsInfoImg(goodsInfoMap.getOrDefault(goodsInfo.getGoodsId(), new GoodsInfoVO()).getGoodsInfoImg());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
//                    goodsInfo.setGoodsInfoImg(goodsInfoMap.getOrDefault(goodsInfo.getGoodsId(), new GoodsInfoVO()).getGoodsInfoImg());
                }

//                //如果不是特殊请求则使用商品主图
//                if (!queryRequest.getImageFlag()) {
//                    goodsInfo.setGoodsInfoImg(goodsInfoMap.getOrDefault(goodsInfo.getGoodsId(), new GoodsInfoVO()).getGoodsInfoImg());
//                }

                //填充规格值
                if (Constants.yes.equals(goods.getMoreSpecFlag()) && MapUtils.isNotEmpty(detailRels) && detailRels.containsKey(goodsInfo.getGoodsInfoId())) {
                    goodsInfo.setSpecText(detailRels.get(goodsInfo.getGoodsInfoId()).stream().map(GoodsInfoSpecDetailRelVO::getDetailName).collect(Collectors.joining(" ")));
                }
                goodsInfo.setStock(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getStock());
                //填充库存列表
                if (CollectionUtils.isNotEmpty(goodsInfoMap.get(goodsInfo.getGoodsInfoId()).getGoodsWareStocks())) {
                    goodsInfo.setGoodsWareStockVOS(goodsInfoMap.get(goodsInfo.getGoodsInfoId()).getGoodsWareStocks());
                }
                goodsInfo.setInquiryFlag(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getInquiryFlag());
                if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        }
        //提取聚合数据
        EsGoodsBaseResponse baseResponse = esGoodsInfoElasticService.extractBrands(response);
        goodsResponse.setBrands(baseResponse.getBrands());

        if (queryRequest.isCateAggFlag()) {
            //提取聚合数据
            baseResponse = esGoodsInfoElasticService.extractGoodsCate(response);
            goodsResponse.setCateList(baseResponse.getCateList());
        }

        //提取规格与规格值聚合数据
        baseResponse = esGoodsInfoElasticService.extractGoodsSpecsAndSpecDetails(response);
        goodsResponse.setGoodsSpecs(baseResponse.getGoodsSpecs());
        goodsResponse.setGoodsSpecDetails(baseResponse.getGoodsSpecDetails());

        goodsResponse.setEsGoods(new PageImpl<>(response.getGoodsData(), PageRequest.of(queryRequest.getPageNum(),
                queryRequest.getPageSize()), response.getTotal()));
        goodsResponse.setGoodsList(goodses);
        return goodsResponse;
    }



    /**
     * 初始化SKU持化于ES
     * @param request
     */
    public void initEsBulkGoodsInfo(EsGoodsInfoRequest request) {
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
        Long totalCount = bulkGoodsInfoQueryProvider.countByCondition(infoQueryRequest).getContext().getCount();
        if (totalCount <= 0) {
            return;
        }

        //是否删除索引
        boolean isClear = request.isClearEsIndex();
        log.info("新散批商品索引开始");
        long startTime = System.currentTimeMillis();

        if (isClear) {
            if (elasticsearchTemplate.indexExists(EsConstants.DOC_BULK_GOODS_TYPE)) {
                log.info("新散批商品spu->删除索引");
                elasticsearchTemplate.deleteIndex(EsConstants.DOC_BULK_GOODS_TYPE);
            }
            if (elasticsearchTemplate.indexExists(EsConstants.DOC_BULK_GOODS_INFO_TYPE)) {
                log.info("新散批商品sku->删除索引");
                elasticsearchTemplate.deleteIndex(EsConstants.DOC_BULK_GOODS_INFO_TYPE);
            }
            //重建商品索引
            elasticsearchTemplate.getClient().admin().indices()
                    .prepareCreate(EsConstants.DOC_BULK_GOODS_TYPE).execute().actionGet();
            elasticsearchTemplate.putMapping(EsBulkGoods.class);
            elasticsearchTemplate.getClient().admin().indices()
                    .prepareCreate(EsConstants.DOC_BULK_GOODS_INFO_TYPE).execute().actionGet();
            elasticsearchTemplate.putMapping(EsBulkGoodsInfo.class);
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
                    bulkGoodsInfoQueryProvider.page(pageRequest).getContext().getGoodsInfoPage();
            if (CollectionUtils.isNotEmpty(goodsInfopage.getContent())) {
                //批量查询相应SPU信息列表
                List<String> goodsIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsId).distinct().collect(Collectors.toList());
                List<String> goodsInfoIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsInfoId).distinct().collect(Collectors.toList());
                //分页查询SPU
                goodsQueryRequest.setGoodsIds(goodsIds);
                goodses.putAll(bulkGoodsQueryProvider.bulkListByCondition(goodsQueryRequest).getContext().getGoodsVOList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, goods -> goods)));


                //#4
                BaseResponse<GoodsLabelRelaByGoodsIdsResponse> byGoodsIds =
                        goodsLabelRelaQueryProvider.findByGoodsIds(GoodsLabelRelaByGoodsIdsRequest.builder().goodsIds(goodsIds).build());
                List<GoodsLabelRelaVO> goodsLabelRelaVOList = byGoodsIds.getContext().getGoodsLabelRelaVOList();
                //根据GoodsId进行分组
                //Goodsid +labelIds
                Map<String, List<GoodsLabelRelaVO>> goodsLabelRealMap = goodsLabelRelaVOList.stream().collect(Collectors.groupingBy(GoodsLabelRelaVO::getGoodsId));

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
//                List<EsCateBrand> esCateBrands = new ArrayList<>();
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
                    esGoodsInfo.setId(goodsInfo.getGoodsInfoId());

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
                            log.info(e.getMessage());
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
                    if (Objects.nonNull(goodsInfoNest.getShelflife()) && goodsInfoNest.getShelflife() == 9999) {
                        goodsInfoNest.setShelflife(0);
                    }

                    esGoodsInfo.setGoodsInfo(goodsInfoNest);
                    esGoodsInfo.setAddedTime(goodsInfo.getAddedTime());
                    esGoodsInfo.setGoodsLevelPrices(levelPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setCustomerPrices(customerPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setLowGoodsName(StringUtils.lowerCase(goodsInfo.getGoodsInfoName()));

                    //分配规格值
                    if (CollectionUtils.isNotEmpty(detailRels)) {
                        detailRels.forEach(specDetailRel -> specDetailRel.setAllDetailName(specDetailRel.getDetailName()));
                        esGoodsInfo.setSpecDetails(detailRels.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).collect(Collectors.toList()));
                    }

                    //分配属性值
                    if (CollectionUtils.isNotEmpty(goodsPropDetailMap.get(goodsInfo.getGoodsId()))) {
                        esGoodsInfo.setPropDetails(goodsPropDetailMap.get(goodsInfo.getGoodsId()).stream().map(rel -> {
                            GoodsPropDetailRelVO relES = new GoodsPropDetailRelVO();
                            relES.setPropId(rel.getPropId());
                            relES.setDetailId(rel.getDetailId());
                            return relES;
                        }).collect(Collectors.toList()));
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
                    iq.setIndexName(EsConstants.DOC_BULK_GOODS_INFO_TYPE);
                    iq.setType(EsConstants.DOC_BULK_GOODS_INFO_TYPE);
                    esGoodsInfos.add(iq);
                });

                //持久化分类品牌(零售不需要，使用的还是批发的品牌)
//                if (CollectionUtils.isNotEmpty(esCateBrands)) {
//                    esCateBrandRepository.saveAll(esCateBrands);
//                    elasticsearchTemplate.refresh(EsCateBrand.class);
//                }

                //持久化商品
                elasticsearchTemplate.bulkIndex(esGoodsInfos);
                elasticsearchTemplate.refresh(EsBulkGoodsInfo.class);

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

        log.info(String.format("散批商品索引结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));
        //TODO
        this.initEsBulkGoods(request);
    }

    /**
     * 初始化SPU持化于ES
     */
    public void initEsBulkGoods(EsGoodsInfoRequest request) {
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
            List<GoodsInfoVO> goodsInfos =
                    bulkGoodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos();
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
        if (StringUtils.isNotBlank(request.getGoodsId())) {
            if (goodsCountQueryRequest.getGoodsIds() == null) {
                goodsCountQueryRequest.setGoodsIds(Collections.singletonList(request.getGoodsId()));
            } else {
                goodsCountQueryRequest.getGoodsIds().add(request.getGoodsId());
            }
        }

        Long totalCount = bulkGoodsQueryProvider.countByCondition(goodsCountQueryRequest).getContext().getCount();

        if (totalCount <= 0) {
            return;
        }

        log.info("散批商品spu索引开始");
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


//        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
//        KsBeanUtil.copyPropertiesThird(goodsCountQueryRequest, goodsQueryRequest);
//        goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
//        goodsQueryRequest.setGoodsIds(goodsCountQueryRequest.getGoodsIds());

        GoodsPageRequest goodsPageRequest = new GoodsPageRequest();
        KsBeanUtil.copyPropertiesThird(goodsCountQueryRequest, goodsPageRequest);
        goodsPageRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsPageRequest.setGoodsIds(goodsCountQueryRequest.getGoodsIds());
        goodsPageRequest.setGoodsSource(1);
        for (int i = 0; i < pageCount; i++) {

            goodsPageRequest.setPageNum(i);
            goodsPageRequest.setPageSize(pageSize);
            List<GoodsVO> goodsList =
                    bulkGoodsQueryProvider.bulkpage(goodsPageRequest).getContext().getGoodsPage().getContent();
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
                        bulkGoodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos();
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
                    //商品排序序号
                    esGoods.setGoodsSeqNum(goods.getGoodsSeqNum());
                    esGoods.setGoodsSubtitle(goods.getGoodsSubtitle());
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
                            log.info(e.getMessage());
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
                    if (CollectionUtils.isNotEmpty(detailRels)) {
                        detailRels.forEach(specDetailRel -> specDetailRel.setAllDetailName(specDetailRel.getDetailName()));
                        esGoods.setSpecDetails(detailRels.stream().filter(specDetailRel -> specDetailRel.getGoodsId().equals(goods.getGoodsId())).collect(Collectors.toList()));
                    }

                    //分配属性值
                    if (CollectionUtils.isNotEmpty(goodsPropDetailMap.get(goods.getGoodsId()))) {
                        esGoods.setPropDetails(goodsPropDetailMap.get(goods.getGoodsId()).stream().map(rel -> {
                            GoodsPropDetailRelVO relES = new GoodsPropDetailRelVO();
                            relES.setPropId(rel.getPropId());
                            relES.setDetailId(rel.getDetailId());
                            return relES;
                        }).collect(Collectors.toList()));
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
                        goodsInfoNests.add(goodsInfoNest);
                    });
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
                    iq.setIndexName(EsConstants.DOC_BULK_GOODS_TYPE);
                    iq.setType(EsConstants.DOC_BULK_GOODS_TYPE);
                    esGoodsInfos.add(iq);
                });

                //持久化分类品牌
//                if (CollectionUtils.isNotEmpty(esCateBrands)) {
//                    esCateBrandRepository.saveAll(esCateBrands);
//                    elasticsearchTemplate.refresh(EsCateBrand.class);
//                }

                //持久化商品
                elasticsearchTemplate.bulkIndex(esGoodsInfos);
                elasticsearchTemplate.refresh(EsBulkGoods.class);

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
        log.info(String.format("散批商品spu索引结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));
    }

    /**
     * 初始化SKU持化于ES
     * @param request
     */
    public void initEsRetailGoodsInfo(EsGoodsInfoRequest request) {
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
        Long totalCount = retailGoodsInfoQueryProvider.countByCondition(infoQueryRequest).getContext().getCount();
        if (totalCount <= 0) {
            return;
        }

        //是否删除索引
        boolean isClear = request.isClearEsIndex();
        log.info("散批商品索引开始");
        long startTime = System.currentTimeMillis();

        if (isClear) {
            if (elasticsearchTemplate.indexExists(EsConstants.DOC_RETAIL_GOODS_TYPE)) {
                log.info("散批商品spu->删除索引");
                elasticsearchTemplate.deleteIndex(EsConstants.DOC_RETAIL_GOODS_TYPE);
            }
            if (elasticsearchTemplate.indexExists(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE)) {
                log.info("散批商品sku->删除索引");
                elasticsearchTemplate.deleteIndex(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE);
            }
            //重建商品索引
            elasticsearchTemplate.getClient().admin().indices()
                    .prepareCreate(EsConstants.DOC_RETAIL_GOODS_TYPE).execute().actionGet();
            elasticsearchTemplate.putMapping(EsRetailGoods.class);
            elasticsearchTemplate.getClient().admin().indices()
                    .prepareCreate(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE).execute().actionGet();
            elasticsearchTemplate.putMapping(EsRetailGoodsInfo.class);
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
                    retailGoodsInfoQueryProvider.page(pageRequest).getContext().getGoodsInfoPage();
            if (CollectionUtils.isNotEmpty(goodsInfopage.getContent())) {
                //批量查询相应SPU信息列表
                List<String> goodsIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsId).distinct().collect(Collectors.toList());
                List<String> goodsInfoIds =
                        goodsInfopage.getContent().stream().map(GoodsInfoVO::getGoodsInfoId).distinct().collect(Collectors.toList());
                //分页查询SPU
                goodsQueryRequest.setGoodsIds(goodsIds);
                goodses.putAll(retailGoodsQueryProvider.retailListByCondition(goodsQueryRequest).getContext().getGoodsVOList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, goods -> goods)));


                //#4
                BaseResponse<GoodsLabelRelaByGoodsIdsResponse> byGoodsIds =
                        goodsLabelRelaQueryProvider.findByGoodsIds(GoodsLabelRelaByGoodsIdsRequest.builder().goodsIds(goodsIds).build());
                List<GoodsLabelRelaVO> goodsLabelRelaVOList = byGoodsIds.getContext().getGoodsLabelRelaVOList();
                //根据GoodsId进行分组
                //Goodsid +labelIds
                Map<String, List<GoodsLabelRelaVO>> goodsLabelRealMap = goodsLabelRelaVOList.stream().collect(Collectors.groupingBy(GoodsLabelRelaVO::getGoodsId));

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
//                List<EsCateBrand> esCateBrands = new ArrayList<>();
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
                    esGoodsInfo.setId(goodsInfo.getGoodsInfoId());

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
                            log.info(e.getMessage());
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
                    if (Objects.nonNull(goodsInfoNest.getShelflife()) && goodsInfoNest.getShelflife() == 9999) {
                        goodsInfoNest.setShelflife(0);
                    }

                    esGoodsInfo.setGoodsInfo(goodsInfoNest);
                    esGoodsInfo.setAddedTime(goodsInfo.getAddedTime());
                    esGoodsInfo.setGoodsLevelPrices(levelPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setCustomerPrices(customerPriceMap.get(goodsInfo.getGoodsInfoId()));
                    esGoodsInfo.setLowGoodsName(StringUtils.lowerCase(goodsInfo.getGoodsInfoName()));

                    //分配规格值
                    if (CollectionUtils.isNotEmpty(detailRels)) {
                        detailRels.forEach(specDetailRel -> specDetailRel.setAllDetailName(specDetailRel.getDetailName()));
                        esGoodsInfo.setSpecDetails(detailRels.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).collect(Collectors.toList()));
                    }

                    //分配属性值
                    if (CollectionUtils.isNotEmpty(goodsPropDetailMap.get(goodsInfo.getGoodsId()))) {
                        esGoodsInfo.setPropDetails(goodsPropDetailMap.get(goodsInfo.getGoodsId()).stream().map(rel -> {
                            GoodsPropDetailRelVO relES = new GoodsPropDetailRelVO();
                            relES.setPropId(rel.getPropId());
                            relES.setDetailId(rel.getDetailId());
                            return relES;
                        }).collect(Collectors.toList()));
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
                    iq.setIndexName(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE);
                    iq.setType(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE);
                    esGoodsInfos.add(iq);
                });

                //持久化分类品牌(零售不需要，使用的还是批发的品牌)
//                if (CollectionUtils.isNotEmpty(esCateBrands)) {
//                    esCateBrandRepository.saveAll(esCateBrands);
//                    elasticsearchTemplate.refresh(EsCateBrand.class);
//                }

                //持久化商品
                elasticsearchTemplate.bulkIndex(esGoodsInfos);
                elasticsearchTemplate.refresh(EsRetailGoodsInfo.class);

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

        log.info(String.format("散批商品索引结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));

        this.initEsRetailGoods(request);
    }

    /**
     * 初始化SPU持化于ES
     */
    public void initEsRetailGoods(EsGoodsInfoRequest request) {
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
            List<GoodsInfoVO> goodsInfos =
                    retailGoodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos();
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
        if (StringUtils.isNotBlank(request.getGoodsId())) {
            if (goodsCountQueryRequest.getGoodsIds() == null) {
                goodsCountQueryRequest.setGoodsIds(Collections.singletonList(request.getGoodsId()));
            } else {
                goodsCountQueryRequest.getGoodsIds().add(request.getGoodsId());
            }
        }

        Long totalCount = retailGoodsQueryProvider.countByCondition(goodsCountQueryRequest).getContext().getCount();

        if (totalCount <= 0) {
            return;
        }

        log.info("散批商品spu索引开始");
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


//        GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
//        KsBeanUtil.copyPropertiesThird(goodsCountQueryRequest, goodsQueryRequest);
//        goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
//        goodsQueryRequest.setGoodsIds(goodsCountQueryRequest.getGoodsIds());

        GoodsPageRequest goodsPageRequest = new GoodsPageRequest();
        KsBeanUtil.copyPropertiesThird(goodsCountQueryRequest, goodsPageRequest);
        goodsPageRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsPageRequest.setGoodsIds(goodsCountQueryRequest.getGoodsIds());
        goodsPageRequest.setGoodsSource(1);
        for (int i = 0; i < pageCount; i++) {

            goodsPageRequest.setPageNum(i);
            goodsPageRequest.setPageSize(pageSize);
            List<GoodsVO> goodsList =
                    retailGoodsQueryProvider.retailpage(goodsPageRequest).getContext().getGoodsPage().getContent();
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
                        retailGoodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos();
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
                StoreCateGoodsRelaListByGoodsIdsRequest storeCateGoodsRelaListByGoodsIdsRequest=new StoreCateGoodsRelaListByGoodsIdsRequest();
                storeCateGoodsRelaListByGoodsIdsRequest.setGoodsIds(goodsIds);
               //商品店铺分类Map
                storeCateGoodsMap.putAll(storeCateGoodsRelaQueryProvider.listByGoodsIds(storeCateGoodsRelaListByGoodsIdsRequest).getContext().getStoreCateGoodsRelaVOList().stream().collect(Collectors.groupingBy(StoreCateGoodsRelaVO::getGoodsId)));

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
                    //商品排序序号
                    esGoods.setGoodsSeqNum(goods.getGoodsSeqNum());
                    esGoods.setGoodsSubtitle(goods.getGoodsSubtitle());
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
                            log.info(e.getMessage());
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
                    if (CollectionUtils.isNotEmpty(detailRels)) {
                        detailRels.forEach(specDetailRel -> specDetailRel.setAllDetailName(specDetailRel.getDetailName()));
                        esGoods.setSpecDetails(detailRels.stream().filter(specDetailRel -> specDetailRel.getGoodsId().equals(goods.getGoodsId())).collect(Collectors.toList()));
                    }

                    //分配属性值
                    if (CollectionUtils.isNotEmpty(goodsPropDetailMap.get(goods.getGoodsId()))) {
                        esGoods.setPropDetails(goodsPropDetailMap.get(goods.getGoodsId()).stream().map(rel -> {
                            GoodsPropDetailRelVO relES = new GoodsPropDetailRelVO();
                            relES.setPropId(rel.getPropId());
                            relES.setDetailId(rel.getDetailId());
                            return relES;
                        }).collect(Collectors.toList()));
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
                        goodsInfoNests.add(goodsInfoNest);
                    });
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
                    iq.setIndexName(EsConstants.DOC_RETAIL_GOODS_TYPE);
                    iq.setType(EsConstants.DOC_RETAIL_GOODS_TYPE);
                    esGoodsInfos.add(iq);
                });

                //持久化分类品牌
//                if (CollectionUtils.isNotEmpty(esCateBrands)) {
//                    esCateBrandRepository.saveAll(esCateBrands);
//                    elasticsearchTemplate.refresh(EsCateBrand.class);
//                }

                //持久化商品
                elasticsearchTemplate.bulkIndex(esGoodsInfos);
                elasticsearchTemplate.refresh(EsRetailGoods.class);

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
        log.info(String.format("散批商品spu索引结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));
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
     * 根据商品spu批量获取商品属性关键Map
     *
     * @param goodsIds 商品id
     * @return 商品属性关键Map内容<商品id, 商品属性关联list>
     */
    private Map<String, List<GoodsPropDetailRelVO>> getPropDetailRelList(List<String> goodsIds) {
        GoodsPropDetailRelByIdsRequest relByIdsRequest = new GoodsPropDetailRelByIdsRequest();
        relByIdsRequest.setGoodsIds(goodsIds);
        List<GoodsPropDetailRelVO> goodsPropDetailRelVOList =
                retailGoodsQueryProvider.getRefByGoodIds(relByIdsRequest).getContext().getGoodsPropDetailRelVOList();
        return goodsPropDetailRelVOList.stream().collect(Collectors.groupingBy(GoodsPropDetailRelVO::getGoodsId));
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
        Iterable<EsRetailGoodsInfo> esGoodsInfoList = esRetailGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        if (esGoodsInfoList != null) {
            esGoodsInfoList.forEach(esGoodsInfo ->
                    client.prepareDelete()
                            .setIndex(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE)
//                            .setIndex(EsConstants.INDEX_NAME)
                            .setType(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE)
//                            .setParent(esGoodsInfo.getCateBrandId())
                            .setId(esGoodsInfo.getId()).execute().actionGet());
        }
        queryRequest.setQueryGoods(true);
        Iterable<EsRetailGoods> esGoodsList = esRetailGoodsElasticRepository.search(queryRequest.getWhereCriteria());
        elasticsearchTemplate.refresh(EsRetailGoodsInfo.class);
        if (esGoodsList != null) {
            esGoodsList.forEach(esGoods -> {
                client.prepareDelete()
                        .setIndex(EsConstants.DOC_RETAIL_GOODS_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_RETAIL_GOODS_TYPE)
//                        .setParent(esGoods.getCateBrandId())
                        .setId(esGoods.getId()).execute().actionGet();
            });
        }
        elasticsearchTemplate.refresh(EsRetailGoods.class);
    }

    /**
     * 编辑商品排序
     *
     * @param goodsVO
     */
    public void modifyGoodsSeqNum(GoodsVO goodsVO) {
        if (Objects.nonNull(goodsVO)) {
            Client client = elasticsearchTemplate.getClient();

            UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQuery = updateByQuery.source(EsConstants.DOC_RETAIL_GOODS_TYPE)
                    //查询要修改的结果集
                    .filter(QueryBuilders.termQuery("id", goodsVO.getGoodsId()))
                    //修改操作
                    .script(new Script("ctx._source.goodsSeqNum=" + goodsVO.getGoodsSeqNum()));
            BulkByScrollResponse response = updateByQuery.get();
            response.getUpdated();

            UpdateByQueryRequestBuilder updateByQuery2 = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQuery2 = updateByQuery2.source(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE)
                    //查询要修改的结果集
                    .filter(QueryBuilders.termQuery("goodsInfo.goodsId", goodsVO.getGoodsId()))
                    //修改操作
                    .script(new Script("ctx._source.goodsInfo.goodsSeqNum=" + goodsVO.getGoodsSeqNum()));
            BulkByScrollResponse response2 = updateByQuery2.get();
            response2.getUpdated();
        }
    }

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

        Iterable<EsRetailGoodsInfo> esGoodsInfoList = esRetailGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        LocalDateTime now = LocalDateTime.now();

        Map<String, GoodsInfoVO> goodsInfoMap =
                retailGoodsInfoQueryProvider.listByCondition(infoQueryRequest).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, c -> c));
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
                        .setIndex(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE)
//                        .setIndex(EsConstants.INDEX_NAME)
                        .setType(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE)
//                        .setParent(esGoodsInfo.getCateBrandId())
                        .setId(esGoodsInfo.getId()).setDoc(esGoodsInfoMap).execute().actionGet();
            });
        }
        queryRequest.setQueryGoods(true);
        Iterable<EsRetailGoods> esGoodsList = esRetailGoodsElasticRepository.search(queryRequest.getWhereCriteria());
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
                iq.setIndexName(EsConstants.DOC_RETAIL_GOODS_TYPE);
//                iq.setIndexName(EsConstants.INDEX_NAME);
                iq.setType(EsConstants.DOC_RETAIL_GOODS_TYPE);
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
     * 删除店铺分类时更新es数据
     *
     * @param storeCateIds
     * @param storeId
     */
    public void delStoreCateIds(List<Long> storeCateIds, Long storeId) {
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setStoreCateIds(storeCateIds);
        queryRequest.setStoreId(storeId);
        Iterable<EsRetailGoodsInfo> esGoodsInfoList = esRetailGoodsInfoElasticRepository.search(queryRequest.getWhereCriteria());
        if (!esGoodsInfoList.iterator().hasNext()) {
            return;
        }
        List<IndexQuery> esGoodsInfos = new ArrayList<>();
        esGoodsInfoList.forEach(item -> {
                    item.setStoreCateIds(null);
                    IndexQuery iq = new IndexQuery();
                    iq.setObject(item);
                    iq.setIndexName(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE);
//                    iq.setIndexName(EsConstants.INDEX_NAME);
                    iq.setType(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE);
//                    iq.setParentId(item.getCateBrandId());
                    esGoodsInfos.add(iq);
                }
        );
    }

    /**
     * 删除品牌时，更新es数据
     */
    public void delBrandIds(List<Long> brandIds, Long storeId) {
        deleteBrandNameCommon(brandIds, EsConstants.DOC_RETAIL_GOODS_TYPE, storeId);
        deleteBrandNameCommon(brandIds, EsConstants.DOC_RETAIL_GOODS_INFO_TYPE, storeId);
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
        String queryStoreName = StringUtils.equals(EsConstants.DOC_RETAIL_GOODS_TYPE, indexName) ? "goodsInfos.storeId" :
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

            updateByQuery = updateByQuery.source(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE, EsConstants.DOC_RETAIL_GOODS_TYPE)
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

            updateByQuery = updateByQuery.source(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE, EsConstants.DOC_RETAIL_GOODS_TYPE)
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
            updateByQuery = updateByQuery.source(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE, EsConstants.DOC_RETAIL_GOODS_TYPE)
                    //查询要修改的结果集
                    .filter(boolQueryBuilder)
                    //修改操作
                    .script(new Script("ctx._source.goodsBrand.brandRelSeqNum="+brandSeqNum));
            BulkByScrollResponse response = updateByQuery.get();
            resCount = response.getUpdated();
            elasticsearchTemplate.refresh(EsRetailGoodsInfo.class);
            elasticsearchTemplate.refresh(EsRetailGoods.class);
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
            updateByQuery = updateByQuery.source(EsConstants.DOC_RETAIL_GOODS_INFO_TYPE, EsConstants.DOC_RETAIL_GOODS_TYPE)
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
     * 批量更新
     *
     * @param goodsVOS
     */
    public void batchGoodsSeqNum(List<GoodsVO> goodsVOS) {
        goodsVOS.forEach(this::modifyGoodsSeqNum);
    }

}
