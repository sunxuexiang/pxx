package com.wanmi.sbc.goods.api.provider.marketing;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingListByCustomerIdAndGoodsInfoIdRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingListByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingListByCustomerIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品营销查询服务</p>
 * author: sunkun
 * Date: 2018-11-02
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsMarketingQueryProvider")
public interface GoodsMarketingQueryProvider {

    /**
     * 获取商品使用的营销
     * @param request 获取商品使用的营销 {@link GoodsMarketingListByCustomerIdRequest}
     * @return {@link GoodsMarketingListByCustomerIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/marketing/list-by-customer-id")
    BaseResponse<GoodsMarketingListByCustomerIdResponse> listByCustomerId(@RequestBody @Valid GoodsMarketingListByCustomerIdRequest request);


    /**
     * 获取商品使用的营销
     * @param request 获取商品使用的营销 {@link GoodsMarketingListByCustomerIdRequest}
     * @return {@link GoodsMarketingListByCustomerIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/marketing/list-by-customer-goodsInfo-id")
    BaseResponse<GoodsMarketingListByCustomerIdResponse> listByCustomerIdAndGoodsInfoId(@RequestBody @Valid GoodsMarketingListByCustomerIdAndGoodsInfoIdRequest request);
}
