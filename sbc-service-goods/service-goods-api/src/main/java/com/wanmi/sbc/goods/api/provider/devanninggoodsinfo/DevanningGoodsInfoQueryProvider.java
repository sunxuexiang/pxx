package com.wanmi.sbc.goods.api.provider.devanninggoodsinfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.*;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAndStockListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByInfoIdAndStepResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author: wanggang
 * @createDate: 2018/11/6 10:08
 * @version: 1.0
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "DevanningGoodsInfoQueryProvider")
public interface DevanningGoodsInfoQueryProvider {

    /**
     * 根据id，规格查询当前规格是否存在
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/getInfoByIdAndStep")
    BaseResponse<DevanningGoodsInfoByInfoIdAndStepResponse> getInfoByIdAndStep(@RequestBody @Valid DevanningGoodsInfoRequest request);

    /**
     * 根据id查询商品
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/getInfoById")
    BaseResponse<DevanningGoodsInfoByIdResponse> getInfoById(@RequestBody @Valid DevanningGoodsInfoByIdRequest request);



    /**
     * 根据商品skuId批量查询商品sku列表
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/getInfoByIds")
    BaseResponse<DevanningGoodsInfoListResponse> getInfoByIds(@RequestBody @Valid DevanningGoodsInfoByIdRequest request);

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/devanning/list-by-ids")
    BaseResponse<DevanningGoodsInfoListResponse> listByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);

    /**
     * 根据商品skuId批量查询商品sku视图列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoViewByIdsRequest}
     * @return 商品sku视图列表 {@link GoodsInfoViewByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/devanning/list-view-by-ids")
    BaseResponse<DevanningGoodsInfoListResponse> listViewByIds(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    @PostMapping("/goods/${application.goods.version}/devanning/list-view-byIds-bymatchFlag")
    BaseResponse<DevanningGoodsInfoListResponse> listViewByIdsByMatchFlag(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    @PostMapping("/goods/${application.goods.version}/devanning/list-view-byIds-bymatchFlag-no-stock")
    BaseResponse<DevanningGoodsInfoListResponse> listViewByIdsByMatchFlagNoStock(@RequestBody @Valid GoodsInfoViewByIdsRequest request);

    /**
     * 根据动态条件查询商品sku列表
     *
     * @param request 根据动态条件查询结构 {@link GoodsInfoListByConditionRequest}
     * @return 商品sku列表 {@link GoodsInfoListByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/devanning/list-by-condition")
    BaseResponse<DevanningGoodsInfoListResponse> listByCondition(@RequestBody @Valid DevanningGoodsInfoListByConditionRequest
                                                                           request);



    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/devanninginfo/list-goodsinfo-stock-by-ids")
    BaseResponse<DevanningGoodsInfoListResponse> listDevanningGoodsInfoAndStcokByIds(@RequestBody @Valid GoodsInfoAndStockListByIdsRequest request);


    /**
     * 根据商品sku父级Id查询商品
     *
     */
    @PostMapping("/goods/${application.goods.version}/devanning/list-by-parent-id")
    BaseResponse<DevanningGoodsInfoListResponse> listByParentId(@RequestBody @Valid DevanningGoodsInfoListByParentIdRequest
                                                                         request);
    /**
     * 根据商品sku查询拆箱信息
     *
     */
    @PostMapping("/goods/${application.goods.version}/devanning/list-by-sku-id")
    BaseResponse<DevanningGoodsInfoByIdResponse> findBySkuId(@RequestBody @Valid DevanningGoodsInfoRequest devanningGoodsInfoByIdRequest);

    /**
     * 根据商品sku批量查询拆箱信息
     *
     */
    @PostMapping("/goods/${application.goods.version}/devanning/list-by-sku-ids")
    BaseResponse<DevanningGoodsInfoByIdResponse> findBySkuIds(@RequestBody @Valid GoodsInfoListByIdsRequest request);
}
