package com.wanmi.sbc.goods.api.provider.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.api.request.enterprise.goods.EnterpriseGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoSimpleVo;
import com.wanmi.sbc.goods.bean.vo.ListGoodsInfoByGoodsInfoIdsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>对商品sku查询接口</p>
 * Created by daiyitian on 2018-11-5-下午6:23.
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsInfoQueryProvider")
public interface GoodsInfoQueryProvider {

    @PostMapping("/goods/${application.goods.version}/info/page-view-wrapper")
    BaseResponse<GoodsInfoViewPageResponse> pageViewWrapper(@RequestBody @Valid GoodsInfoViewPageRequest request);

    /**
     * 分页查询商品sku视图列表
     *
     * @param request 商品sku视图分页条件查询结构 {@link GoodsInfoViewPageRequest}
     * @return 商品sku视图分页列表 {@link GoodsInfoViewPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/page-view")
    BaseResponse<GoodsInfoViewPageResponse> pageView(@RequestBody @Valid GoodsInfoViewPageRequest request);

    /**
     * 分页查询商品sku列表
     *
     * @param request 商品sku分页条件查询结构 {@link GoodsInfoPageRequest}
     * @return 商品sku分页列表 {@link GoodsInfoPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/page")
    BaseResponse<GoodsInfoPageResponse> page(@RequestBody @Valid GoodsInfoPageRequest request);

    /**
     * 列出满足条件的商品id集合
     *
     * @param request 列出满足条件的商品id集合查询结构 {@link ListGoodsInfoIdsRequest}
     * @return 商品id列表 {@link List<String>}
     */
    @PostMapping("/goods/${application.goods.version}/info/listGoodsInfoIds")
    BaseResponse<List<String>> listGoodsInfoIds(@RequestBody @Valid ListGoodsInfoIdsRequest request);

    /**
     * 列出满足条件的商品信息
     *
     * @param request 列出满足条件的商品信息查询结构 {@link List<String>}
     * @return 满足条件的商品skuid与商品信息映射 {@link Map<String,ListGoodsInfoByGoodsInfoIdsVO>}
     */
    @PostMapping("/goods/${application.goods.version}/info/listGoodsInfoByGoodsInfoIds")
    BaseResponse<Map<String,ListGoodsInfoByGoodsInfoIdsVO>> listGoodsInfoByGoodsInfoIds(@RequestBody @Valid List<String> request);

    /**
     * 根据商品skuId批量查询商品sku视图列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoViewByIdsRequest}
     * @return 商品sku视图列表 {@link GoodsInfoViewByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-view-by-ids")
    BaseResponse<GoodsInfoViewByIdsResponse> listViewByIds(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    @PostMapping("/goods/${application.goods.version}/info/list-view-by-ids-limit-wareId")
    BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsLimitWareId(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    @PostMapping("/goods/${application.goods.version}/info/list-view-by-ids-matchFlag")
    BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsByMatchFlag(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    /**
     * 根据商品skuId查询商品sku视图
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoViewByIdRequest}
     * @return 商品sku视图 {@link GoodsInfoViewByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/get-view-by-id")
    BaseResponse<GoodsInfoViewByIdResponse> getViewById(@RequestBody @Valid GoodsInfoViewByIdRequest request);




    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-by-ids")
    BaseResponse<GoodsInfoListByIdsResponse> listByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-by-parentIds")
    BaseResponse<GoodsInfoListByIdsResponse> listByParentIds(@RequestBody @Valid GoodsInfoListByParentIdsRequest request);

    /**
     * 根据商品skuId查询商品sku
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoByIdRequest}
     * @return 商品sku {@link GoodsInfoByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/get-by-id")
    BaseResponse<GoodsInfoByIdResponse> getById(@RequestBody @Valid GoodsInfoByIdRequest request);

    /**
     * 根据商品skuNo查询商品sku
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoByIdRequest}
     * @return 商品sku {@link GoodsInfoByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/find-special-goods-by-erp-nos")
    BaseResponse<GoodsInfoByNoResponse> findSpecialGoodsByErpNos(@RequestBody @Valid GoodsInfoByErpNosRequest request);

    /**
     * 根据商品skuNo查询商品sku
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoByIdRequest}
     * @return 商品sku {@link GoodsInfoByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/find-all-goods-by-erp-nos")
    BaseResponse<GoodsInfoByNoResponse> findAllGoodsByErpNos(@RequestBody @Valid GoodsInfoByErpNosRequest request);

    /**
     * 根据动态条件查询商品sku列表
     *
     * @param request 根据动态条件查询结构 {@link GoodsInfoListByConditionRequest}
     * @return 商品sku列表 {@link GoodsInfoListByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-by-condition")
    BaseResponse<GoodsInfoListByConditionResponse> listByCondition(@RequestBody @Valid GoodsInfoListByConditionRequest
                                                                           request);

    /**
     * 根据动态条件统计商品sku个数
     *
     * @param request 根据动态条件统计结构 {@link GoodsInfoCountByConditionRequest}
     * @return 商品sku个数 {@link GoodsInfoCountByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/count-by-condition")
    BaseResponse<GoodsInfoCountByConditionResponse> countByCondition(@RequestBody @Valid
                                                                             GoodsInfoCountByConditionRequest
                                                                           request);

    /**
     * 分页查询分销商品sku视图列表
     *
     * @param request 分销商品sku视图分页条件查询结构 {@link DistributionGoodsPageRequest}
     * @return 分销商品sku视图分页列表 {@link DistributionGoodsInfoPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/page-distribution")
    BaseResponse<DistributionGoodsInfoPageResponse> distributionGoodsInfoPage(@RequestBody @Valid DistributionGoodsPageRequest request);


    /**
     * 分页查询特价商品sku视图列表
     *
     * @param request 特价商品sku视图分页条件查询结构 {@link DistributionGoodsPageRequest}
     * @return 特价商品sku视图分页列表 {@link DistributionGoodsInfoPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/page-special")
    BaseResponse<SpecialGoodsInfoResponse> specialGoodsInfoPage(@RequestBody @Valid SpecialGoodsPageRequest request);



    /*
     * @Description: 商品ID<spu> 查询sku信息
     * @Author: Bob
     * @Date: 2019-03-11 20:43
    */
    @PostMapping("/goods/${application.goods.version}/info/get-by-goodsid")
    BaseResponse<GoodsInfoByGoodsIdresponse> getBygoodsId(@RequestBody @Valid DistributionGoodsChangeRequest request);

    /**
     * 分页查询企业购商品sku视图列表
     *
     * @param request 分页查询企业购商品sku视图列表 {@link EnterpriseGoodsInfoPageRequest}
     * @return 分销商品sku视图分页列表 {@link EnterpriseGoodsInfoPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/page-enterprise")
    BaseResponse<EnterpriseGoodsInfoPageResponse> enterpriseGoodsInfoPage(@RequestBody @Valid EnterpriseGoodsInfoPageRequest request);



    /**
     * 根据erpGoodsInfoNo 查询skuNo
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoViewByIdsRequest}
     * @return 商品sku视图列表 {@link GoodsInfoViewByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-goods-info-nos")
    BaseResponse<GoodsInfoNoResponse> listGoodsInfoNoByErpNos(@RequestBody @Valid GoodsInfoNoByErpNoRequest request);

    /**
     * 根据erpGoodsInfoNo 查询skuNo
     *
     * @return 商品sku视图列表 {@link GoodsInfoViewByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/find-all-special-goods")
    BaseResponse<GoodsInfoListByIdsResponse> findAllSpecialGoods();

    /**
     * 根据skuid获取storeid
     *
     * @param   {@link GoodsInfoStoreIdBySkuIdRequest}
     * @return  {@link GoodsInfoStoreIdBySkuIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/get-storeId-By-GoodsId")
    BaseResponse<GoodsInfoStoreIdBySkuIdResponse> getStoreIdByGoodsId(@RequestBody @Valid GoodsInfoStoreIdBySkuIdRequest request);

    /**
     * 根据skuIds获取stock
     *
     * @param   {@link GoodsInfoStockByIdsRequest}
     * @return  {@link GetGoodsInfoStockByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/find-stock")
    BaseResponse<GetGoodsInfoStockByIdResponse> findGoodsInfoStock(@RequestBody @Valid GoodsInfoStockByIdsRequest request);

    /**
     * 根据skuIds获取stock(囤货)
     *
     * @param   {@link GoodsInfoStockByIdsRequest}
     * @return  {@link GetGoodsInfoStockByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/find-stock-for-pile")
    BaseResponse<GetGoodsInfoStockByIdResponse> findGoodsInfoStockForPile(@RequestBody @Valid GoodsInfoStockByIdsRequest request);

    /**
     * 根据skuIds获取stock
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/find-stock-ids")
    BaseResponse<GoodsInfoStockByIdsResponse> findGoodsInfoStockByIds(@RequestBody @Valid GoodsInfoStockByGoodsInfoIdsRequest request);

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-goodsinfo-stock-by-ids")
    BaseResponse<GoodsInfoListByIdsResponse> listGoodsInfoAndStcokByIds(@RequestBody @Valid GoodsInfoAndStockListByIdsRequest request);

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-goodsinfoid-by-ids")
    BaseResponse<GiftGoodsInfoListByIdsResponse> findGoodsInfoByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);



    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-goodsinfoid-cahe-by-ids")
    BaseResponse<GiftGoodsInfoListByIdsResponse> findGoodsInfoByIdsAndCache(@RequestBody @Valid GoodsInfoListByIdsRequest request);


    /**
     * 根据商品skuId批量查询商品sku列表(只有goodsid 和 保质期)
     */
    @PostMapping("/goods/${application.goods.version}/info/list-goodsinfoid-by-ids/shelflife")
    BaseResponse<GoodsInfoOnlyShelflifeResponse> findGoodsInfoShelflifeByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);

    /**
     * 查询商品库存为0
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/list-goodsinfoid-by-stock")
    BaseResponse<List<String>> listGoodsInfoByStock();

    /**
     * 查询带T的商品信息
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/get-goodsinfo-by-t")
    BaseResponse<GoodsInfoViewByIdsResponse> getGoodsInfoByT();

    /**
     * 根据erpNo查询正常上架的商品
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoAndStockListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/list-valid-goodsinfo-stock-by-ErpGoodsInfoNos")
    BaseResponse<GoodsInfoListByIdsResponse> listValidGoodsInfoAndStcokByErpGoodsInfoNos(@RequestBody @Valid GoodsInfoByErpNosRequest request);

    /**
     * 根据erpNo查询正常上架的商品
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoAndStockListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/info/listGoodsStatusByGoodsIds")
    BaseResponse<GoodsInfoListByIdsResponse> listGoodsStatusByGoodsIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);

    /**
     * 根据分类查商品近30天销量，取前10个
     * @return
     */
//    @PostMapping("/goods/${application.goods.version}/salesRanking")
//    BaseResponse<GoodsSalesRankingResponse> getSalesRanking(@RequestBody GoodsSalesRankingRequest request);

    /**
     * 根据传入skuid,查询top名次
     * @return
     */
//    @PostMapping("/goods/${application.goods.version}/salesRankingTop")
//    BaseResponse<GoodsSalesRankingTopResponse> getSalesRankingTop(@RequestBody  @Valid GoodsSalesRankingTopRequest request);

    /**
     * 查询所有商品库存为0s
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/listStockoutGoods")
    BaseResponse<List<String>> listStockoutGoods();

    /**
     * 根据筛选条件查找参与囤货活动的商品
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/listByCondition4PileActivity")
    BaseResponse<List<String>> listByCondition4PileActivity(@RequestBody @Valid ListByCondition4PileActivityRequest request);

    @PostMapping("/goods/${application.goods.version}/info/findBrandsHasAddedSku")
    BaseResponse<List<Long>> findBrandsHasAddedSku(@RequestBody @Valid FindBrandsHasAddedSkuRequest request);

    @PostMapping("/goods/${application.goods.version}/info/getChangShaSkuInfoByNanChangSkuIdMap")
    BaseResponse<Map<String, Map<String, String>>> getChangShaSkuInfoByNanChangSkuIdMap();

    @PostMapping("/goods/${application.goods.version}/info/getChangShaSkuInfoBySkuIdMap")
    BaseResponse<Map<String, Map<String, String>>> getChangShaSkuInfoBySkuIdMap();

    @PostMapping("/goods/${application.goods.version}/info/findGoodsInfoVoBySkuIds")
    BaseResponse<List<GoodsInfoSimpleVo>>  findGoodsInfoSimpleVoBySkuIds(@RequestBody @Valid List<String> skuIds);


    /**
     * 仅仅查询上线的数据
     * */
    @PostMapping("/goods/${application.goods.version}/info/get-by-added_flag")
    BaseResponse<GoodsInfoByGoodsIdresponse> getByGoodsIdAndAdded(@RequestBody @Valid DistributionGoodsChangeRequest request);

    /**
     * 区域限购列表
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/goodsInfoAreaLimitPage")
    BaseResponse<MicroServicePage<GoodsInfoAreaLimitPageResponse>> goodsInfoAreaLimitPage(@RequestBody GoodsInfoAreaLimitPageRequest request);

    /**
     * 批量增加商品区域限购列表
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/info/goodsInfoAreaLimitAdd")
    BaseResponse goodsInfoAreaLimitAdd(@RequestBody GoodsInfoAreaEditRequest request);

    /**
     * 根据ID获取详情
     * @param goodsInfoId
     * @return
     */
    @GetMapping("/goods/${application.goods.version}/info/goodsInfoAreaLimitGetById")
    BaseResponse<GoodsInfoAreaLimitPageResponse> goodsInfoAreaLimitGetById(@RequestParam("goodsInfoId") String goodsInfoId);

    /**
     * 根据ID删除商品限购
     * @param goodsInfoId
     * @return
     */
    @GetMapping("/goods/${application.goods.version}/info/goodsInfoAreaLimitDeleteById")
    BaseResponse goodsInfoAreaLimitDeleteById(@RequestParam("goodsInfoId") String goodsInfoId);
}
