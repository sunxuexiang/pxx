package com.wanmi.sbc.goods.api.provider.prop;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.prop.*;
import com.wanmi.sbc.goods.api.response.prop.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author: wanggang
 * @createDate: 2018/10/31 14:38
 * @version: 1.0
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsPropQueryProvider")
public interface GoodsPropQueryProvider {

    /**
     * 根据类目ID查询商品属性
     * @param goodsPropListAllByCateIdRequest {@link GoodsPropListAllByCateIdRequest }
     * @return 商品属性集合 {@link GoodsPropListAllByCateIdResponse }
    */
    @PostMapping("/goods/${application.goods.version}/prop/list-all-by-cate-id")
    BaseResponse<GoodsPropListAllByCateIdResponse> listAllByCateId(@RequestBody @Valid GoodsPropListAllByCateIdRequest goodsPropListAllByCateIdRequest);

    /**
     * 根据类目ID查询需要索引的商品属性列表
     * (供用户根据商品属性进行筛选商品)
     * @param goodsPropListIndexByCateIdRequest {@link GoodsPropListIndexByCateIdRequest }
     * @return 商品属性集合 {@link GoodsPropListIndexByCateIdResponse }
     */
    @PostMapping("/goods/${application.goods.version}/prop/list-index-by-cate-id")
    BaseResponse<GoodsPropListIndexByCateIdResponse> listIndexByCateId(@RequestBody @Valid GoodsPropListIndexByCateIdRequest goodsPropListIndexByCateIdRequest);

    /**
     * 每次新增初始化排序
     * @param goodsPropListInitSortRequest {@link GoodsPropListInitSortRequest }
     * @return 商品属性集合 {@link GoodsPropListInitSortResponse }
     */
    @PostMapping("/goods/${application.goods.version}/prop/list-init-sort")
    BaseResponse<GoodsPropListInitSortResponse> listInitSort(@RequestBody @Valid GoodsPropListInitSortRequest goodsPropListInitSortRequest);

    /**
     * 判断属性值是否超限
     * @param goodsPropQueryPropDetailsOverStepRequest {@link GoodsPropQueryPropDetailsOverStepRequest }
     * @return {@link GoodsPropQueryPropDetailsOverStepResponse }
    */
    @PostMapping("/goods/${application.goods.version}/prop/query-prop-details-over-step")
    BaseResponse<GoodsPropQueryPropDetailsOverStepResponse> queryPropDetailsOverStep(@RequestBody @Valid GoodsPropQueryPropDetailsOverStepRequest goodsPropQueryPropDetailsOverStepRequest);

    /**
     * 判断是否是商品属性三级节点
     * @param goodsPropQueryIsChildNodeRequest {@link GoodsPropQueryIsChildNodeRequest }
     * @return {@link GoodsPropQueryIsChildNodeResponse }
     */
    @PostMapping("/goods/${application.goods.version}/prop/query-is-child-node")
    BaseResponse<GoodsPropQueryIsChildNodeResponse> queryIsChildNode(@RequestBody @Valid GoodsPropQueryIsChildNodeRequest goodsPropQueryIsChildNodeRequest);

    /**
     * 根据类别Id查询该类别下所有spuId
     * @param goodsPropListByCateIdRequest {@link GoodsPropListByCateIdRequest }
     * @return 所有spuId集合 {@link GoodsPropListByCateIdResponse }
     */
    @PostMapping("/goods/${application.goods.version}/prop/list-by-cate-id")
    BaseResponse<GoodsPropListByCateIdResponse> listByCateId(@RequestBody @Valid GoodsPropListByCateIdRequest goodsPropListByCateIdRequest);

}
