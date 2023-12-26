package com.wanmi.sbc.goods.api.provider.goodsrecommendsetting;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.*;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingAddResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品推荐配置保存服务Provider</p>
 *
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsRecommendSettingSaveProvider")
public interface GoodsRecommendSettingSaveProvider {

    /**
     * 新增商品推荐配置API
     *
     * @param goodsRecommendSettingAddRequest 商品推荐配置新增参数结构 {@link GoodsRecommendSettingAddRequest}
     * @return 新增的商品推荐配置信息 {@link GoodsRecommendSettingAddResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/add")
    BaseResponse<GoodsRecommendSettingAddResponse> add(@RequestBody @Valid GoodsRecommendSettingAddRequest goodsRecommendSettingAddRequest);

    /**
     * 修改商品推荐配置API
     *
     * @param goodsRecommendSettingModifyRequest 商品推荐配置修改参数结构 {@link GoodsRecommendSettingModifyRequest}
     * @return 修改的商品推荐配置信息 {@link GoodsRecommendSettingModifyResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/modify")
    BaseResponse<GoodsRecommendSettingModifyResponse> modify(@RequestBody @Valid GoodsRecommendSettingModifyRequest goodsRecommendSettingModifyRequest);


    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/modify-strategy")
    BaseResponse<GoodsRecommendSettingModifyResponse> modifyStrategy(@RequestBody @Valid GoodsRecommendSettingModifyStrategyRequest goodsRecommendSettingModifyRequest);

    /**
     * 单个删除商品推荐配置API
     *
     * @param goodsRecommendSettingDelByIdRequest 单个删除参数结构 {@link GoodsRecommendSettingDelByIdRequest}
     * @return 删除结果 {@link BaseResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid GoodsRecommendSettingDelByIdRequest goodsRecommendSettingDelByIdRequest);

    /**
     * 批量删除商品推荐配置API
     *
     * @param goodsRecommendSettingDelByIdListRequest 批量删除参数结构 {@link GoodsRecommendSettingDelByIdListRequest}
     * @return 删除结果 {@link BaseResponse}
     * @author chenyufei
     */
    @PostMapping("/goods/${application.goods.version}/goodsrecommendsetting/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid GoodsRecommendSettingDelByIdListRequest goodsRecommendSettingDelByIdListRequest);

}

