package com.wanmi.sbc.goods.api.provider.standard;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.standard.*;
import com.wanmi.sbc.goods.api.response.standard.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品库查询接口</p>
 * author: sunkun
 * Date: 2018-11-07
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "StandardGoodsQueryProvider")
public interface StandardGoodsQueryProvider {

    /**
     * 分页查询商品库
     * @param request 分页查询商品库 {@link StandardGoodsPageRequest}
     * @return {@link StandardGoodsPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/standard/page")
    BaseResponse<StandardGoodsPageResponse> page(@RequestBody @Valid StandardGoodsPageRequest request);

    /**
     * 根据ID查询商品库
     * @param request 根据ID查询商品库 {@link StandardGoodsByIdRequest}
     * @return {@link StandardGoodsByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/standard/get-by-id")
    BaseResponse<StandardGoodsByIdResponse> getById(@RequestBody @Valid StandardGoodsByIdRequest request);

    /**
     * 列出已被导入的商品库ID
     * @param request 列出已被导入的商品库ID {@link StandardGoodsGetUsedStandardRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/standard/get-used-standard")
    BaseResponse<StandardGoodsGetUsedStandardResponse> getUsedStandard(@RequestBody @Valid StandardGoodsGetUsedStandardRequest request);

    /**
     * 列出已被导入的商品ID
     * @param request 列出已被导入的商品ID {@link StandardGoodsGetUsedGoodsRequest}
     * @return {@link StandardGoodsGetUsedGoodsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/standard/get-used-goods")
    BaseResponse<StandardGoodsGetUsedGoodsResponse> getUsedGoods(@RequestBody @Valid StandardGoodsGetUsedGoodsRequest request);

    /**
     * 列出已被导入的SKUID
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/standard/list-used-goods-id")
    BaseResponse<StandardGoodsListUsedGoodsIdResponse> listUsedGoodsId(@RequestBody @Valid StandardGoodsListUsedGoodsIdRequest request);

    /**
     * 分页查询商品库
     * @param request 分页查询商品库 {@link StandardGoodsPageRequest}
     * @return {@link StandardGoodsPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/queryGoodsIds/page")
    BaseResponse<StandardGoodsIdsQueryResponse> queryGoodsIds(@RequestBody @Valid StandardGoodsPageRequest request);

    /**
     * 列出已被导入的商品ID
     * @param request 列出已被导入的商品ID {@link StandardGoodsGetUsedGoodsRequest}
     * @return {@link StandardGoodsGetUsedGoodsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/standard/get-used-goods-to-standard-goods")
    BaseResponse<StandardGoodsGetMapResponse> getStandardGoodsIds(@RequestBody @Valid StandardGoodsGetUsedGoodsRequest request);

}
