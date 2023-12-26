package com.wanmi.sbc.goods.api.provider.goodsrecommendsetting;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingListRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingPageRequest;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingPageResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * <p>商品推荐配置查询服务Provider</p>
 *
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsRecommendSettingQueryProvider")
public interface GoodsRecommendSettingQueryProvider {

    /**
     * 分页查询商品推荐配置API
     *
     * @param goodsRecommendSettingPageReq 分页请求参数和筛选对象 {@link GoodsRecommendSettingPageRequest}
     * @return 商品推荐配置分页列表信息 {@link GoodsRecommendSettingPageResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/page")
    BaseResponse<GoodsRecommendSettingPageResponse> page(@RequestBody @Valid GoodsRecommendSettingPageRequest goodsRecommendSettingPageReq);

    /**
     * 列表查询商品推荐配置API
     *
     * @param goodsRecommendSettingListReq 列表请求参数和筛选对象 {@link GoodsRecommendSettingListRequest}
     * @return 商品推荐配置的列表信息 {@link GoodsRecommendSettingListResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/list")
    BaseResponse<GoodsRecommendSettingListResponse> list(@RequestBody @Valid GoodsRecommendSettingListRequest goodsRecommendSettingListReq);

    /**
     * 单个查询商品推荐配置API
     *
     * @param goodsRecommendSettingByIdRequest 单个查询商品推荐配置请求参数 {@link GoodsRecommendSettingByIdRequest}
     * @return 商品推荐配置详情 {@link GoodsRecommendSettingByIdResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/get-by-id")
    BaseResponse<GoodsRecommendSettingByIdResponse> getById(@RequestBody @Valid GoodsRecommendSettingByIdRequest goodsRecommendSettingByIdRequest);


    /**
     * 单个查询商品推荐配置API
     *
     * @return 商品推荐配置详情 {@link GoodsRecommendSettingResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/get-setting")
    BaseResponse<GoodsRecommendSettingResponse> getSetting(@RequestParam("wareId") Long wareId);

}

