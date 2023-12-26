package com.wanmi.sbc.goods.api.provider.goodstypeconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsListRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigByIdRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigListRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigPageRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByCacheResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdsResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsPageResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigByIdResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigListResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>分类推荐分类查询服务Provider</p>
 *
 * @author sgy
 * @date 2013-06-07 10:53:36
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}",contextId = "GoodsTypeConfigQueryProvider")
public interface GoodsTypeConfigQueryProvider {

    /**
     * 分页查询分类推荐分类API
     *
     * @param goodsRecommendGoodsPageReq 分页请求参数和筛选对象 {@link MerchantTypeConfigPageRequest}
     * @return 分类推荐分类分页列表信息 {@link MerchantTypeConfigPageResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/goodsTypeConfig/page")
    BaseResponse<MerchantTypeConfigPageResponse> page(@RequestBody @Valid MerchantTypeConfigPageRequest goodsRecommendGoodsPageReq);

    /**
     * 列表查询分类推荐分类API
     *
     * @param goodsRecommendGoodsListReq 列表请求参数和筛选对象 {@link MerchantTypeConfigListRequest}
     * @return 分类推荐分类的列表信息 {@link MerchantTypeConfigListResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/goodsTypeConfig/list")
    BaseResponse<MerchantTypeConfigListResponse> list(@RequestBody @Valid MerchantTypeConfigListRequest goodsRecommendGoodsListReq);

    /**
     * 单个查询分类推荐分类API
     *
     * @param goodsRecommendGoodsByIdRequest 单个查询分类推荐分类请求参数 {@link MerchantTypeConfigByIdRequest}
     * @return 分类推荐分类详情 {@link GoodsRecommendGoodsByIdResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/goodsTypeConfig/get-by-id")
    BaseResponse<MerchantTypeConfigByIdResponse> getById(@RequestBody @Valid MerchantTypeConfigByIdRequest goodsRecommendGoodsByIdRequest);

    /**
     * 商户推荐分类调用
     *
     * @param goodsRecommendGoodsListReq 列表请求参数和筛选对象 {@link MerchantTypeConfigListRequest}
     * @return 分类推荐分类的列表信息 {@link MerchantTypeConfigListResponse}
     * @author sgy
     */
    @PostMapping("/goods/${application.goods.version}/goodsTypeConfig/app-list")
    BaseResponse<GoodsCateByIdsResponse> appList(@RequestBody @Valid MerchantTypeConfigListRequest goodsRecommendGoodsListReq);

}

