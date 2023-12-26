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

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BulkGoodsQueryProvider")
public interface BulkGoodsQueryProvider {

    /**
     * 分页查询零售商品信息
     *
     * @param goodsPageRequest {@link GoodsPageRequest}
     * @return 分页商品信息 {@link GoodsPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/bulk-page")
    BaseResponse<GoodsPageResponse> bulkpage(@RequestBody @Valid GoodsPageRequest goodsPageRequest);

    /**
     * 根据编号查询商品视图信息
     *
     * @param goodsByIdRequest {@link GoodsViewByIdRequest}
     * @return 商品信息 {@link GoodsViewByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-bulk-view-by-id")
    BaseResponse<GoodsViewByIdResponse> getBulkViewById(@RequestBody @Valid GoodsViewByIdRequest goodsByIdRequest);

    /**
     * 根据编号查询零售商品信息
     *
     * @param goodsByIdRequest {@link GoodsByIdRequest}
     * @return 商品信息 {@link GoodsByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/get-Bulk-by-id")
    BaseResponse<GoodsByIdResponse> getBulkById(@RequestBody @Valid GoodsByIdRequest goodsByIdRequest);

    /**
     * 根据goodsId批量查询零售商品信息列表
     *
     * @param request 包含goodsIds的批量查询请求结构 {@link GoodsListByIdsRequest}
     * @return 商品信息列表 {@link GoodsListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/list-bulk-by-ids")
    BaseResponse<GoodsListByIdsResponse> listBulkByIds(@RequestBody @Valid GoodsListByIdsRequest request);

    /**
     * 根据不同条件查询零售商品信息
     *
     * @param goodsByConditionRequest {@link GoodsByConditionRequest}
     * @return  {@link GoodsByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/bulk-list-by-condition")
    BaseResponse<GoodsByConditionResponse> bulkListByCondition(@RequestBody @Valid GoodsByConditionRequest goodsByConditionRequest);


    /**
     * 根据goodsId批量查询商品信息列表
     *
     * @param request 包含goodsIds的批量查询请求结构 {@link GoodsListByIdsRequest}
     * @return 商品信息列表 {@link GoodsListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/bulk-list-by-ids-no-valid")
    BaseResponse<GoodsListByIdsResponse> bulkListByGoodsIdsNoValid(@RequestBody @Valid GoodsListByIdsRequest request);


    /**
     * 根据不同条件查询商品信息
     *
     * @param goodsByConditionRequest
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk-count-by-condition")
    BaseResponse<GoodsCountByConditionResponse> countByCondition(@RequestBody @Valid GoodsCountByConditionRequest goodsByConditionRequest);

    /**
     * 根据多个SpuID查询属性关联
     *
     * @param goodsPropDetailRelByIdsRequest {@link GoodsPropDetailRelByIdsRequest}
     * @return 属性关联信息 {@link GoodsPropDetailRelByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/bulk-ref-by-goods-ids")
    BaseResponse<GoodsPropDetailRelByIdsResponse> getRefByGoodIds(@RequestBody @Valid GoodsPropDetailRelByIdsRequest goodsPropDetailRelByIdsRequest);

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk-list-by-providerGoodsIds")
    BaseResponse<GoodsListByIdsResponse> listByProviderGoodsId(@RequestBody @Valid GoodsListByIdsRequest request);

    /**
     * 根据epr不为空批量查询
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/list-by-erp")
    BaseResponse<GoodsListResponse> listByErp();



}
