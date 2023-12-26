package com.wanmi.sbc.goods.api.provider.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.info.GoodsCountByConditionRequest;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.api.response.info.GoodsCountByConditionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午9:31
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsQueryProvider")
public interface GoodsQueryProvider {

    /**
     * 分页查询商品信息
     *
     * @param goodsPageRequest {@link GoodsPageRequest}
     * @return 分页商品信息 {@link GoodsPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/page")
    BaseResponse<GoodsPageResponse> page(@RequestBody @Valid GoodsPageRequest goodsPageRequest);

    /**
     * 分页查询商品信息
     *
     * @param goodsPageRequest {@link GoodsPageRequest}
     * @return 分页商品信息 {@link GoodsPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/devanning/page")
    BaseResponse<GoodsPageResponse> pagedevanning(@RequestBody @Valid GoodsPageRequest goodsPageRequest);


    /**
     * 分页查询商品信息
     *
     * @param goodsPageRequest {@link GoodsPageRequest}
     * @return 分页商品信息 {@link GoodsPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/bdevanning/page")
    BaseResponse<GoodsPageResponse> bpagedevanning(@RequestBody @Valid GoodsPageRequest goodsPageRequest);


    /**
     * 根据编号查询商品视图信息
     *
     * @param goodsByIdRequest {@link GoodsViewByIdRequest}
     * @return 商品信息 {@link GoodsViewByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-view-by-id")
    BaseResponse<GoodsViewByIdResponse> getViewById(@RequestBody @Valid GoodsViewByIdRequest goodsByIdRequest);

    /**
     * 根据编号查询商品视图信息
     *
     * @param goodsByIdRequest {@link GoodsViewByIdRequest}
     * @return 商品信息 {@link GoodsViewByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-view-New-by-id")
    BaseResponse<GoodsViewByIdResponse> getViewNewById(@RequestBody @Valid GoodsNewViewByIdRequest goodsByIdRequest);




    /**
     * 根据编号查询商品信息
     *
     * @param goodsByIdRequest {@link GoodsByIdRequest}
     * @return 商品信息 {@link GoodsByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-by-id")
    BaseResponse<GoodsByIdResponse> getById(@RequestBody @Valid GoodsByIdRequest goodsByIdRequest);




    /**
     * 根据多个SpuID查询属性关联
     *
     * @param goodsPropDetailRelByIdsRequest {@link GoodsPropDetailRelByIdsRequest}
     * @return 属性关联信息 {@link GoodsPropDetailRelByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/ref-by-goods-ids")
    BaseResponse<GoodsPropDetailRelByIdsResponse> getRefByGoodIds(@RequestBody @Valid GoodsPropDetailRelByIdsRequest goodsPropDetailRelByIdsRequest);

    /**
     * 待审核商品统计
     *
     * @param goodsUnAuditCountRequest {@link GoodsUnAuditCountRequest}
     * @return 统计结果 {@link GoodsUnAuditCountResponse}
     */
    @PostMapping("/goods/${application.goods.version}/count-un-audit")
    BaseResponse<GoodsUnAuditCountResponse> countUnAudit(@RequestBody @Valid GoodsUnAuditCountRequest goodsUnAuditCountRequest);


    /**
     * 根据不同条件查询商品信息
     *
     * @param goodsByConditionRequest {@link GoodsByConditionRequest}
     * @return  {@link GoodsByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/list-by-condition")
    BaseResponse<GoodsByConditionResponse> listByCondition(@RequestBody @Valid GoodsByConditionRequest goodsByConditionRequest);




    /**
     * 根据不同条件查询商品信息
     *
     * @param goodsByConditionRequest
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/count-by-condition")
    BaseResponse<GoodsCountByConditionResponse> countByCondition(@RequestBody @Valid GoodsCountByConditionRequest goodsByConditionRequest);


    /**
     * 根据goodsId批量查询商品信息列表
     *
     * @param request 包含goodsIds的批量查询请求结构 {@link GoodsListByIdsRequest}
     * @return 商品信息列表 {@link GoodsListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/list-by-ids")
    BaseResponse<GoodsListByIdsResponse> listByIds(@RequestBody @Valid GoodsListByIdsRequest request);

;

    /**
     * 根据SPU编号和SkuId集合获取商品详情信息
     * @param request {@link GoodsViewByIdAndSkuIdsRequest}
     * @return {@link GoodsViewByIdAndSkuIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-view-by-id-and-sku-ids")
    BaseResponse<GoodsViewByIdAndSkuIdsResponse> getViewByIdAndSkuIds(@RequestBody @Valid GoodsViewByIdAndSkuIdsRequest request);

    /**
     * 根据积分商品Id获取商品详情信息
     * @param request {@link GoodsViewByPointsGoodsIdRequest}
     * @return {@link GoodsViewByPointsGoodsIdRequest}
     */
    @PostMapping("/goods/${application.goods.version}/get-view-by-points-goods-id")
    BaseResponse<GoodsViewByPointsGoodsIdResponse> getViewByPointsGoodsId(@RequestBody @Valid GoodsViewByPointsGoodsIdRequest request);


    /**
     * @Description: 店铺ID统计店铺商品总数
     * @param request {@link GoodsCountByStoreIdRequest}
     * @Author: Bob
     * @Date: 2019-04-03 10:47
     */
    @PostMapping("/goods/${application.goods.version}/count-by-storeid")
    BaseResponse<GoodsCountByStoreIdResponse> countByStoreId(@RequestBody @Valid GoodsCountByStoreIdRequest request);

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/list-by-providerGoodsIds")
    BaseResponse<GoodsListByIdsResponse> listByProviderGoodsId(@RequestBody @Valid GoodsListByIdsRequest request);

    /**
     * 查询商品属性、商品图文信息
     *
     * @param goodsByIdRequest {@link GoodsViewByIdRequest}
     * @return 商品信息 {@link GoodsViewByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-goods-detail")
    BaseResponse<GoodsDetailProperResponse> getGoodsDetail(@RequestBody @Valid GoodsDetailProperBySkuIdRequest goodsByIdRequest);

    /**
     * 查询商品简易信息
     *
     * @param goodsByIdRequest {@link GoodsDetailSimpleRequest}
     * @return 商品信息 {@link GoodsDetailSimpleResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-goods-detail-simple")
    BaseResponse<GoodsDetailSimpleResponse> getGoodsDetailSimple(@RequestBody @Valid GoodsDetailSimpleRequest goodsByIdRequest);

    /**
     * 查询导出的商品信息
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/get-export-goods")
    BaseResponse<GoodsExportListResponse> getExportGoods(@RequestBody @Valid GoodsPageRequest request);

    /**
     * 查询导出的商品信息通过创建时间和上架转态
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/get-export-bytimeAndStaues-goods")
    BaseResponse<GoodsByCreatTimeAndStaueExportListResponse> getExportGoodsByCreatetimeAndStaues(@RequestBody @Valid GoodsPageRequest request);


    /**
     * 根据epr不为空批量查询
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/list-by-erp")
    BaseResponse<GoodsListResponse> listByErp();

    /**
     * 根据goodsId批量查询商品信息列表
     *
     * @param request 包含goodsIds的批量查询请求结构 {@link GoodsListByIdsRequest}
     * @return 商品信息列表 {@link GoodsListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/list-by-ids-no-valid")
    BaseResponse<GoodsListByIdsResponse> listByGoodsIdsNoValid(@RequestBody @Valid GoodsListByIdsRequest request);
    /**
     * 查询导出的商品信息
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/get-store-export-goods")
    BaseResponse<StoreGoodsExportListResponse> getStoreExportGoods(@RequestBody @Valid GoodsPageRequest request);


    @PostMapping("/goods/${application.goods.version}/tag/listGoodsTagRel")
    BaseResponse<List<GoodsTagRelResponse>> listGoodsTagRel(@RequestBody GoodsTagRelReRequest request);


    @PostMapping("/goods/${application.goods.version}/storeGoods/onSaleNum")
    BaseResponse<List<GoodsStoreOnSaleResponse>> listStoreOnSaleGoodsNum(@RequestBody List<Long> storeIds);

    @PostMapping("/goods/${application.goods.version}/newGoods/listRecentAdded")
    BaseResponse<List<String>> listRecentAddedNewGoods();
}
