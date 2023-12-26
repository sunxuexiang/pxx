package com.wanmi.sbc.goods.api.provider.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.cate.*;
import com.wanmi.sbc.goods.api.response.cate.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.provider.goodscate.GoodsCateQueryProvider
 * 商品分类查询接口
 * @author lipeng
 * @dateTime 2018/11/1 下午3:09
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsCateQueryProvider")
public interface GoodsCateQueryProvider {

    /**
     * 根据条件查询商品分类列表信息
     *
     * @param request {@link GoodsCateListByConditionRequest}
     * @return 商品分类列表信息 {@link GoodsCateListByConditionResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/list-by-condition")
    BaseResponse<GoodsCateListByConditionResponse> listByCondition(
            @RequestBody @Valid GoodsCateListByConditionRequest request);

    /**
     * 根据编号查询分类信息
     * @param request {@link GoodsCateByIdRequest}
     * @return 商品分类信息 {@link GoodsCateByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-by-id")
    BaseResponse<GoodsCateByIdResponse> getById(@RequestBody @Valid GoodsCateByIdRequest request);

    /**
     * 根据cateId递归查询至二级分类信息
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-by-id-for-tow")
    BaseResponse<GoodsCateByIdResponse> getByIdForLevel(@RequestBody @Valid GoodsCateByIdRequest request);

    /**
     * 根据编号批量查询分类信息
     * @param request {@link GoodsCateByIdsRequest}
     * @return 商品分类列表信息 {@link GoodsCateByIdsResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-by-ids")
    BaseResponse<GoodsCateByIdsResponse> getByIds(@RequestBody @Valid GoodsCateByIdsRequest request);

    /**
     * 根据编号查询当前分类是否存在子分类
     *
     * @param request {@link GoodsCateExistsChildByIdRequest}
     * @return 是否存在 {@link GoodsCateExistsChildByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/exists-child-by-id")
    BaseResponse<GoodsCateExistsChildByIdResponse> existsChildById(
            @RequestBody @Valid GoodsCateExistsChildByIdRequest request);

    /**
     * 根据编号查询当前分类是否存在商品
     *
     * @param request {@link GoodsCateExistsGoodsByIdRequest}
     * @return 是否存在 {@link GoodsCateExistsGoodsByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/exists-goods-by-id")
    BaseResponse<GoodsCateExistsGoodsByIdResponse> existsGoodsById(
            @RequestBody @Valid GoodsCateExistsGoodsByIdRequest request);

    /**
     * 从缓存中获取商品分类信息
     *
     * @return 商品分类结果数据 {@link GoodsCateByCacheResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-by-cache")
    BaseResponse<GoodsCateByCacheResponse> getByCache();
    /**
     * 从缓存中获取商品分类信息
     *
     * @return 商品分类结果数据 {@link GoodsCateByCacheResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-by-store-cache")
    BaseResponse<GoodsCateByCacheResponse> getByStoreCache(@RequestBody @Valid ContractCateListByConditionRequest  request);

    /**
     * 从缓存中获取商品分类信息
     *
     * @return 商品分类结果数据 {@link GoodsCateByCacheResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-by-cache-new")
    BaseResponse<GoodsCateByCacheResponse> getByCacheNew();

    /**
     * 根据编号查询当前分类下面所有的子分类编号
     *
     * @param request {@link GoodsCateChildCateIdsByIdRequest}
     * @return 子商品分类编号集合 {@link GoodsCateChildCateIdsByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-child-cate-id-by-id")
    BaseResponse<GoodsCateChildCateIdsByIdResponse> getChildCateIdById(
            @RequestBody @Valid GoodsCateChildCateIdsByIdRequest request);

    /**
     * 查询所有的分类信息
     *
     * @return 分类列表信息 {@link GoodsCateListResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/list")
    BaseResponse<GoodsCateListResponse> list();

    /**
     * 查询所有的分类信息
     *
     * @return 分类列表信息 {@link GoodsCateListResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/listNew")
    BaseResponse<GoodsCateListNewResponse> listNew();

    /**
     * 根据店铺获取叶子分类列表
     *
     * @param request 包含店铺id获取叶子分类列表请求结构 {@link GoodsCateLeafByStoreIdRequest}
     * @return 叶子分类列表 {@link GoodsCateLeafByStoreIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/list-leaf-by-store-id")
    BaseResponse<GoodsCateLeafByStoreIdResponse> listLeafByStoreId(
            @RequestBody @Valid GoodsCateLeafByStoreIdRequest request);

    /**
     * 根据店铺获取叶子分类列表
     *
     * @param request 包含店铺id获取叶子分类列表请求结构 {@link GoodsCateLeafByStoreIdRequest}
     * @return 叶子分类列表 {@link GoodsCateLeafByStoreIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/list-cate-by-store-id")
    BaseResponse<GoodsCateLeafByStoreIdResponse> listGoodsCateByStoreId(
            @RequestBody @Valid GoodsCateLeafByStoreIdRequest request);

    /**
     * 获取叶子分类列表
     *
     * @return 叶子分类列表 {@link GoodsCateLeafResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/list-leaf")
    BaseResponse<GoodsCateLeafResponse> listLeaf();

    /**
     * 从缓存中获取商品分类信息
     *
     * @return 商品分类结果数据 {@link GoodsCateByCacheResponse}
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-by-cache-root")
    BaseResponse<GoodsCateByCacheRootResponse> getByCacheAndRoot(@RequestBody @Valid GoodsCateByIdRequest request);


    /**
     * 缓存中获取白鲸推荐商品类目
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-recommend-by-cache")
    BaseResponse<GoodsCateListByConditionResponse> getRecommendByCache();

    /**
     * 缓存中获取散批推荐商品类目
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/cate/get-retail-recommend-by-cache")
    BaseResponse<GoodsCateListByConditionResponse> getRetailRecommendByCache();

    /**
     * 获取所有的3级分类无商品类别
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/cate/getNoGoodsCateids")
    BaseResponse<List<Long>> getNoGoodsCateids();
}
