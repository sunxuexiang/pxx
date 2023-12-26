package com.wanmi.sbc.goods.api.provider.retailgoodsrecommend;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description: 散批推荐provider
 * @author: XinJiang
 * @time: 2022/4/20 9:31
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BulkGoodsRecommendSettingProvider")
public interface BulkGoodsRecommendSettingProvider {

    /**
     * 批量添加散批鲸喜推荐商品
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/goods/recommend/batch-add")
    BaseResponse batchAdd(@RequestBody @Valid BulkGoodsRecommendSettingBatchAddRequest request);

    /**
     * 批量修改鲸喜推荐商品排序
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/goods/recommend/batch-modify-sort")
    BaseResponse batchModifySort(@RequestBody @Valid BulkGoodsRecommendBatchModifySortRequest request);

    /**
     * 通过id移除推荐商品
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/goods/recommend/del-by-id")
    BaseResponse delById(@RequestBody @Valid BulkGoodsRecommendDelByIdRequest request);

    /**
     * 通过ids批量移除推荐商品
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/goods/recommend/del-by-ids")
    BaseResponse delByIds(@RequestBody @Valid BulkGoodsRecommendDelByIdRequest request);

    /**
     * 刷新缓存
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/bulk/goods/recommend/fill-redis")
    BaseResponse fillRedis();

}
