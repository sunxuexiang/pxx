package com.wanmi.sbc.goods.api.provider.standard;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsGetUsedGoodsRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardSkuByIdRequest;
import com.wanmi.sbc.goods.api.response.standard.StandardGoodsGetMapResponse;
import com.wanmi.sbc.goods.api.response.standard.StandardSkuByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>对商品库查询接口</p>
 * Created by daiyitian on 2018-11-5-下午6:23.
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "StandardSkuQueryProvider")
public interface StandardSkuQueryProvider {

    /**
     * 根据id获取商品库信息
     *
     * @param request 包含id的商品库信息查询结构 {@link StandardSkuByIdRequest}
     * @return 商品库信息 {@link StandardSkuByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/standard/sku/get-by-id")
    BaseResponse<StandardSkuByIdResponse> getById(@RequestBody @Valid StandardSkuByIdRequest request);


    /**
     * 根据erpid获取商品库id
     *
     * @param request 包含id的商品库信息查询结构 {@link StandardGoodsGetUsedGoodsRequest}
     * @return 商品库信息 {@link StandardGoodsGetUsedGoodsRequest}
     */
    @PostMapping("/goods/${application.goods.version}/standard/sku/get-by-erpids")
    BaseResponse<StandardGoodsGetMapResponse> findByErpGoodsInfoNo(@RequestBody @Valid StandardGoodsGetUsedGoodsRequest request);

    @PostMapping("/goods/${application.goods.version}/standard/sku/get-by-skuids")
    BaseResponse<StandardGoodsGetMapResponse> findBySkuIds(@RequestBody @Valid StandardGoodsGetUsedGoodsRequest request);

}
