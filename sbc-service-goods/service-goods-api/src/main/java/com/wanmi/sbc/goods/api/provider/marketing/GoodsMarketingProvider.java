package com.wanmi.sbc.goods.api.provider.marketing;

import ch.qos.logback.classic.gaffer.GafferConfigurator;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.marketing.*;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingModifyResponse;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingSyncResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品营销</p>
 * author: sunkun
 * Date: 2018-11-02
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsMarketingProvider")
public interface GoodsMarketingProvider {

    /**
     * 根据用户编号删除商品使用的营销
     * @param request 根据用户编号删除商品使用的营销 {@link GoodsMarketingDeleteByCustomerIdRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/marketing/delete-by-customer-id")
    BaseResponse deleteByCustomerId(@RequestBody @Valid GoodsMarketingDeleteByCustomerIdRequest request);


    /**
     * 根据用户编号和商品编号列表删除商品使用的营销
     * @param request 根据用户编号和商品编号列表删除商品使用的营销 {@link GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/marketing/delete-by-customer-id-and-goods-info-ids")
    BaseResponse deleteByCustomerIdAndGoodsInfoIds(@RequestBody @Valid GoodsMarketingDeleteByCustomerIdAndGoodsInfoIdsRequest request);


    /**
     * 批量添加商品使用的营销
     * @param request 批量添加商品使用的营销 {@link GoodsMarketingBatchAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/marketing/batch-add")
    BaseResponse batchAdd(@RequestBody @Valid GoodsMarketingBatchAddRequest request);


    /**
     * 修改商品使用的营销
     * @param request 修改商品使用的营销 {@link GoodsMarketingModifyRequest}
     * @return {@link GoodsMarketingModifyResponse}
     */
    @PostMapping("/goods/${application.goods.version}/marketing/modify")
    BaseResponse<GoodsMarketingModifyResponse> modify(@RequestBody @Valid GoodsMarketingModifyRequest request);


    /**
     * 商品营销同步
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/marketing/sync")
    BaseResponse<GoodsMarketingSyncResponse> syncGoodsMarketings(@RequestBody @Valid GoodsMarketingSyncRequest request);

    /**
     * 变动购物车商品数量后修改或生成促销关联记录
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/marketing/merge-goods-marketing")
    BaseResponse mergeGoodsMarketings(@RequestBody @Valid GoodsMarketingSyncRequest request);


    /**
     * 变动购物车商品数量后修改或生成促销关联记录
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/marketing/delete-goods-marketing")
    BaseResponse deleteGoodsMarketings(@RequestBody @Valid GoodsMarketingDeleteByMaketingIdAndGoodsInfoIdsRequest request);
}
