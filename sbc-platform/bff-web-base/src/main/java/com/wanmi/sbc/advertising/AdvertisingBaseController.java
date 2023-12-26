package com.wanmi.sbc.advertising;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.advertising.request.AdvertisingBaseResquest;
import com.wanmi.sbc.advertising.response.AdvertisingBaseResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.es.elastic.response.EsSearchResponse;
import com.wanmi.sbc.goods.BulkGoodsBaseController;
import com.wanmi.sbc.goods.GoodsBaseController;
import com.wanmi.sbc.goods.RetailGoodsBaseController;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodstypeconfig.GoodsTypeConfigQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigListRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByStoreIdRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdsResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByIdsResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateVO;
import com.wanmi.sbc.goods.request.GoodsByParentCateIdQueryRequest;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingQueryProvider;
import com.wanmi.sbc.setting.api.provider.headline.HeadLineProvider;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.response.advertising.StartPageAdvertisingResponse;
import com.wanmi.sbc.setting.api.response.headline.HeadLineResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @description: 首页广告位API
 * @author: XinJiang
 * @time: 2022/2/18 17:27
 */
@Api(description = "首页广告位API", tags = "AdvertisingBaseController")
@RestController
@RequestMapping(value = "/home/page/advertising")
@Slf4j
public class AdvertisingBaseController {

    @Autowired
    private AdvertisingQueryProvider advertisingQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BulkGoodsBaseController bulkGoodsBaseController;
    @Autowired
    private RetailGoodsBaseController retailGoodsBaseController;
    @Autowired
    private GoodsBaseController goodsBaseController;
    @Autowired
    private GoodsTypeConfigQueryProvider goodsTypeConfigQueryProvider;
    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ResultsMapper resultsMapper;
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;
    @Autowired
    private HeadLineProvider headLineProvider;

    /**
     *
     * @param advertisingBaseResquest 1 批发 2零售 3 散批
     * @return
     */
    @ApiOperation(value = "列表查询首页广告页 并正序排序")
    @PostMapping("/list-by-cache")
    public BaseResponse<AdvertisingBaseResponse> listByCache(@RequestBody(required = false) AdvertisingBaseResquest advertisingBaseResquest, HttpServletRequest httpRequest) {

        AdvertisingQueryRequest request = new AdvertisingQueryRequest();
        request.putSort("sortNum", SortType.ASC.toValue());
        request.setDelFlag(DeleteFlag.NO);
        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());


        request.setWareId(wareId);

        AdvertisingBaseResponse response = new AdvertisingBaseResponse();
        response.setAdvertisingVOList(advertisingQueryProvider.listByCache(request).getContext().getAdvertisingVOList());
        List<GoodsCateVO> goodsCateVOS = JSONObject.parseArray(goodsCateQueryProvider.getByCache().getContext().getResult(), GoodsCateVO.class);
        if(Objects.nonNull(advertisingBaseResquest)) {
            if (Objects.nonNull(advertisingBaseResquest.getType())) {
                if (advertisingBaseResquest.getType() == 2) {
                    goodsCateVOS.forEach(goodsCateVO -> {
                        if(goodsCateVO.getCateId() == 1420L){
                            goodsCateVO.setGoodsCateList(goodsCateVO.getGoodsCateList().stream().filter(s-> !Objects.equals(s.getCateId(),1641L)).collect(Collectors.toList()));
                        }
                    });
                }else if(advertisingBaseResquest.getType() == 1){
                    goodsCateVOS.forEach(goodsCateVO -> {
                        if(goodsCateVO.getCateId() ==1683L){
                            goodsCateVO.getGoodsCateList().forEach(goodsCateVO1 -> {
                                if(goodsCateVO1.getCateId() == 1662L){
                                    goodsCateVO1.setGoodsCateList( goodsCateVO1.getGoodsCateList().stream().filter(s-> !Objects.equals(s.getCateId(),1668L)).collect(Collectors.toList()));
                                }
                            });
                        }
                    });
                }
            }
        }
        AtomicReference<Boolean> thireejiflag = new AtomicReference<>(true);
        //分类ids

        if (advertisingBaseResquest.getType()==3){
            goodsCateVOS.forEach(v->{
                Iterator<GoodsCateVO> iterator = v.getGoodsCateList().iterator();
                while (iterator.hasNext()){
                    GoodsCateVO next = iterator.next();
                    List<Long> collect = next.getGoodsCateList().stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());
                    GoodsByParentCateIdQueryRequest goods = new GoodsByParentCateIdQueryRequest();
                    goods.setCateIds(collect);
                    goods.setBrandIds(new ArrayList<>());
                    goods.setMatchWareHouseFlag(true);
                    BaseResponse<EsGoodsResponse> esGoodsResponseBaseResponse = bulkGoodsBaseController.cateBrandSortGoodslist(goods, httpRequest);
                    if (CollectionUtils.isEmpty(esGoodsResponseBaseResponse.getContext().getEsGoods().getContent())){
                        iterator.remove();
                    }
                }
            });
        }
        //零售
        else if (advertisingBaseResquest.getType()==2){
            goodsCateVOS.forEach(v->{
                Iterator<GoodsCateVO> iterator = v.getGoodsCateList().iterator();
                while (iterator.hasNext()){
                    GoodsCateVO next = iterator.next();
                    List<Long> collect = next.getGoodsCateList().stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());
                    GoodsByParentCateIdQueryRequest goods = new GoodsByParentCateIdQueryRequest();
                    goods.setCateIds(collect);
                    goods.setBrandIds(new ArrayList<>());
                    goods.setMatchWareHouseFlag(true);
                    BaseResponse<EsGoodsResponse> esGoodsResponseBaseResponse = retailGoodsBaseController.cateBrandSortGoodslist(goods, httpRequest);
                    if (CollectionUtils.isEmpty(esGoodsResponseBaseResponse.getContext().getEsGoods().getContent())){
                        iterator.remove();
                    }/*else {
                        // 检查三级分类隐藏
                        Iterator<GoodsCateVO> voIterator = next.getGoodsCateList().iterator();
                        while (voIterator.hasNext()) {
                            GoodsCateVO next1 = voIterator.next();
                            GoodsByParentCateIdQueryRequest goods1 = new GoodsByParentCateIdQueryRequest();
                            goods1.setCateIds(Collections.singletonList(next1.getCateId()));
                            goods1.setBrandIds(new ArrayList<>());
                            goods1.setMatchWareHouseFlag(true);
                            BaseResponse<EsGoodsResponse> esGoodsResponseBaseResponse1 = retailGoodsBaseController.cateBrandSortGoodslist(goods1, httpRequest);
                            if (CollectionUtils.isEmpty(esGoodsResponseBaseResponse1.getContext().getEsGoods().getContent())){
                                log.info("删除不存在商品的三级分类 id {}", next1.getCateId());
                                voIterator.remove();
                            }
                        }
                    }*/
                }
            });
        }
        //批发

        else if (advertisingBaseResquest.getType()==1){
            goodsCateVOS.forEach(v->{
                Iterator<GoodsCateVO> iterator = v.getGoodsCateList().iterator();
                while (iterator.hasNext()){
                    GoodsCateVO next = iterator.next();
                    List<Long> collect = next.getGoodsCateList().stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());
                    GoodsByParentCateIdQueryRequest goods = new GoodsByParentCateIdQueryRequest();
                    goods.setCateIds(collect);
                    goods.setBrandIds(new ArrayList<>());
                    goods.setMatchWareHouseFlag(true);
                    BaseResponse<EsGoodsResponse> esGoodsResponseBaseResponse = goodsBaseController.cateBrandSortGoodslist(goods, httpRequest);
                    if (CollectionUtils.isEmpty(esGoodsResponseBaseResponse.getContext().getEsGoods().getContent())){
                        iterator.remove();
                    }
                }
            });
        }



        response.setGoodsCateVOS(goodsCateVOS);
        List<GoodsCateVO> recommendGoodsCate = goodsCateQueryProvider.getRecommendByCache().getContext().getGoodsCateVOList();
        //大白鲸超市
        GoodsCateVO goodsCateVO = new GoodsCateVO();
        goodsCateVO.setCateName("零售");
        goodsCateVO.setCateId(-99L);
        if (redisService.hasKey(RedisKeyConstant.RETAIL_GOODS_CATE_IMG)) {
            String imageUrl = redisService.getString(RedisKeyConstant.RETAIL_GOODS_CATE_IMG);
            goodsCateVO.setCateImg(imageUrl);
            response.setImageUrl(imageUrl);
        }

        if (CollectionUtils.isNotEmpty(recommendGoodsCate)) {
            //仓库id如果为1，证明是长沙仓，需要散批，否则不需要。
            if (recommendGoodsCate.size() >= 10  ) {
                recommendGoodsCate = recommendGoodsCate.stream().limit(9).collect(Collectors.toList());
                recommendGoodsCate.add(goodsCateVO);
            }
            else {
                recommendGoodsCate.add(goodsCateVO);
            }
        }
        response.setRecommendGoodsCate(recommendGoodsCate);

        return BaseResponse.success(response);
    }

    /**
     *
     * @param advertisingBaseResquest 1 批发 2零售 3 散批
     * @return
     */
    @ApiOperation(value = "列表查询首页广告页 并正序排序【商家页面】")
    @PostMapping("/list-by-cache/for-store/v0")
    public BaseResponse<AdvertisingBaseResponse> listByCacheForStore(@RequestBody(required = false) AdvertisingBaseResquest advertisingBaseResquest, HttpServletRequest httpRequest) {
        final Long storeId = null == advertisingBaseResquest ? null : advertisingBaseResquest.getStoreId();
        if (null == storeId || advertisingBaseResquest.getType() == null) {
            BaseResponse.error("缺少必要参数");
        }
        AdvertisingQueryRequest request = new AdvertisingQueryRequest();
        request.putSort("sortNum", SortType.ASC.toValue());
        request.setDelFlag(DeleteFlag.NO);
        request.setStoreId(storeId);
//        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
//        request.setWareId(wareId);
        AdvertisingBaseResponse response = new AdvertisingBaseResponse();
        response.setAdvertisingVOList(advertisingQueryProvider.listStoreIdByCache(request).getContext().getAdvertisingVOList());
        List<GoodsCateVO> goodsCateVOS = JSONObject.parseArray(goodsCateQueryProvider.getByCache().getContext().getResult(), GoodsCateVO.class);
        if (Objects.nonNull(advertisingBaseResquest)) {
            if (Objects.nonNull(advertisingBaseResquest.getType())) {
                if (advertisingBaseResquest.getType() == 1) {
                    goodsCateVOS.forEach(goodsCateVO -> {
                        if (goodsCateVO.getCateId() == 1683L) {
                            goodsCateVO.getGoodsCateList().forEach(goodsCateVO1 -> {
                                if (goodsCateVO1.getCateId() == 1662L) {
                                    goodsCateVO1.setGoodsCateList(goodsCateVO1.getGoodsCateList().stream().filter(s -> !Objects.equals(s.getCateId(), 1668L)).collect(Collectors.toList()));
                                }
                            });
                        }
                    });
                }
            }
        }
        if (advertisingBaseResquest.getType() == 1) {
            goodsCateVOS.forEach(v -> {
                Iterator<GoodsCateVO> iterator = v.getGoodsCateList().iterator();
                while (iterator.hasNext()) {
                    GoodsCateVO next = iterator.next();
                    List<Long> collect = next.getGoodsCateList().stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());
                    GoodsByParentCateIdQueryRequest goods = new GoodsByParentCateIdQueryRequest();
                    goods.setStoreId(storeId);
                    goods.setCateIds(collect);
                    goods.setBrandIds(new ArrayList<>());
                    goods.setMatchWareHouseFlag(false);
                    BaseResponse<EsGoodsResponse> esGoodsResponseBaseResponse = goodsBaseController.cateBrandSortGoodslist(goods, httpRequest);
                    if (CollectionUtils.isEmpty(esGoodsResponseBaseResponse.getContext().getEsGoods().getContent())) {
                        iterator.remove();
                    }
                }
            });
        }
        Iterator<GoodsCateVO> iterator = goodsCateVOS.iterator();
        while (iterator.hasNext()) {
            GoodsCateVO element = iterator.next();
            if (CollectionUtils.isEmpty(element.getGoodsCateList())) {
                iterator.remove();
            }
        }
        response.setGoodsCateVOS(goodsCateVOS);
        List<GoodsCateVO> recommendGoodsCate = goodsCateQueryProvider.getRecommendByCache().getContext().getGoodsCateVOList();
        //大白鲸超市
        GoodsCateVO goodsCateVO = new GoodsCateVO();
        goodsCateVO.setCateName("零售");
        goodsCateVO.setCateId(-99L);
        if (redisService.hasKey(RedisKeyConstant.RETAIL_GOODS_CATE_IMG)) {
            String imageUrl = redisService.getString(RedisKeyConstant.RETAIL_GOODS_CATE_IMG);
            goodsCateVO.setCateImg(imageUrl);
            response.setImageUrl(imageUrl);
        }

        if (CollectionUtils.isNotEmpty(recommendGoodsCate)) {
            //仓库id如果为1，证明是长沙仓，需要散批，否则不需要。
            if (recommendGoodsCate.size() >= 10) {
                recommendGoodsCate = recommendGoodsCate.stream().limit(9).collect(Collectors.toList());
                recommendGoodsCate.add(goodsCateVO);
            } else {
                recommendGoodsCate.add(goodsCateVO);
            }
        }
        response.setRecommendGoodsCate(recommendGoodsCate);

        return BaseResponse.success(response);
    }

    /**
     *
     * @param advertisingBaseResquest 1 批发 2零售 3 散批
     * @return
     */
    @ApiOperation(value = "列表查询首页广告页 并正序排序【商家页面】")
    @PostMapping("/list-by-cache/for-store")
    public BaseResponse<AdvertisingBaseForStoreResponse> listByCacheForStoreV2(@RequestBody(required = false) AdvertisingBaseResquest advertisingBaseResquest, HttpServletRequest httpRequest) {
        final Long storeId = null == advertisingBaseResquest ? null : advertisingBaseResquest.getStoreId();
        if (null == storeId || advertisingBaseResquest.getType() == null) {
            BaseResponse.error("缺少必要参数");
        }
        AdvertisingQueryRequest request = new AdvertisingQueryRequest();
        request.putSort("sortNum", SortType.ASC.toValue());
        request.setDelFlag(DeleteFlag.NO);
        request.setStoreId(storeId);
        AdvertisingBaseForStoreResponse response = new AdvertisingBaseForStoreResponse();
        // 获取店铺广告
        response.setAdvertisingVOList(advertisingQueryProvider.listStoreIdByCache(request).getContext().getAdvertisingVOList());
        // 获取所有分类
        final StoreCateListByStoreIdRequest storeIdRequest = new StoreCateListByStoreIdRequest();
        storeIdRequest.setStoreId(storeId);
        final BaseResponse<StoreCateListByIdsResponse> baseResponse = storeCateQueryProvider.listTreeByStoreId(storeIdRequest);
        final List<StoreCateVO> storeCateResponseVOList = baseResponse.getContext().getStoreCateVOList();

        final EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        esGoodsInfoQueryRequest.setStoreId(storeId);
        esGoodsInfoQueryRequest.setQueryGoods(true);
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        final EsSearchResponse esGoodsInfoByParams = getEsGoodsInfoByParams(esGoodsInfoQueryRequest);
        Set<Long> allStoreCatIds = new HashSet<>();
        esGoodsInfoByParams.getAggResultMap().forEach((k,v) -> {
            if (!"storeCateIds".equals(k)){return;}
            v.forEach(u -> allStoreCatIds.add(Long.valueOf(u.getKey().toString())));
        });
        for (int i = storeCateResponseVOList.size() - 1; i >= 0; i--) {
            final StoreCateVO o = storeCateResponseVOList.get(i);
            if (CollectionUtils.isEmpty(o.getStoreCateList())){
                if (!allStoreCatIds.contains(o.getStoreCateId())){
                    storeCateResponseVOList.remove(i);
                }
            }else {
                final Iterator<StoreCateVO> iterator = o.getStoreCateList().iterator();
                while (iterator.hasNext()) {
                    StoreCateVO next = iterator.next();
                    if (!allStoreCatIds.contains(next.getStoreCateId())) {
                        iterator.remove();
                    }
                }
                if (CollectionUtils.isEmpty(o.getStoreCateList())){
                    storeCateResponseVOList.remove(i);
                }
            }
        }
        response.setGoodsCateVOS(storeCateResponseVOList);
        // 赋值店铺分类
        final MerchantTypeConfigListRequest configListRequest = new MerchantTypeConfigListRequest();
        configListRequest.setStoreId(advertisingBaseResquest.getStoreId());
        final BaseResponse<GoodsCateByIdsResponse> goodsCateByIdsResponse = goodsTypeConfigQueryProvider.appList(configListRequest);
        final List<StoreCateVO> recommendGoodsCate = goodsCateByIdsResponse.getContext().getStoreCateVOList();
        if(CollectionUtils.isNotEmpty(recommendGoodsCate)){
            for (int i = recommendGoodsCate.size() - 1; i >= 0; i--) {
                if (!allStoreCatIds.contains(recommendGoodsCate.get(i).getStoreCateId())){
                    recommendGoodsCate.remove(i);
                }
            }
        }
        response.setRecommendGoodsCate(CollectionUtils.isNotEmpty(recommendGoodsCate) ? recommendGoodsCate : Lists.newArrayList());
        return BaseResponse.success(response);
    }

    @ApiOperation("获取启动页广告页配置信息（缓存级）")
    @PostMapping("/get-start-page-by-cache")
    public BaseResponse<StartPageAdvertisingResponse> getStartPageByCache() {
        return BaseResponse.success(advertisingQueryProvider.getStartPageByCache().getContext());
    }

    public EsSearchResponse getEsGoodsInfoByParams(EsGoodsInfoQueryRequest queryRequest) {
        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("storeCateIds").field("storeCateIds").size(2000));
        if (Objects.nonNull(queryRequest.getSortFlag()) && queryRequest.getSortFlag() == 11){
            queryRequest.getSorts().removeAll(queryRequest.getSorts());
            queryRequest.putSort("goodsSeqNum",SortOrder.ASC);
        }
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));

        if (CollectionUtils.isEmpty(response.getGoodsData()) || response.getGoodsData().size() < 1) {
            if(response.getData() == null || CollectionUtils.isEmpty(response.getData())){
                return response;
            }
            return response;
        }
        return response;
    }

    @ApiOperation(value = "头条列表")
    @GetMapping("/headline")
    public BaseResponse<List<HeadLineResponse>> get(){
        return headLineProvider.get();
    }

}
