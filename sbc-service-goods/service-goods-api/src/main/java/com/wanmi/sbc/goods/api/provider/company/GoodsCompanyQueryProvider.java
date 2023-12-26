package com.wanmi.sbc.goods.api.provider.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandByIdRequest;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyByIdRequest;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyListRequest;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyPageRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandByIdResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandListResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyByIdResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyListResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>对厂商查询接口</p>
 * Created by daiyitian on 2018-11-5-下午6:23.
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsCompanyQueryProvider")
public interface GoodsCompanyQueryProvider {
    /**
     * 分页查询品牌列表
     *
     * @param request 品牌查询数据结构 {@link GoodsCompanyPageRequest}
     * @return 品牌分页列表 {@link GoodsCompanyPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/company/page")
    BaseResponse<GoodsCompanyPageResponse> page(@RequestBody @Valid GoodsCompanyPageRequest request);

    /**
     * 根据id查询品牌信息
     *
     * @param request 包含id的查询数据结构 {@link GoodsCompanyByIdRequest}
     * @return 品牌信息 {@link GoodsCompanyByIdResponse}
     */
    @PostMapping("/goods/${application.goods.version}/company/get-by-id")
    BaseResponse<GoodsCompanyByIdResponse> getById(@RequestBody @Valid GoodsCompanyByIdRequest request);


    /**
     * 条件查询品牌列表
     *
     * @param request 品牌查询数据结构 {@link GoodsBrandListRequest}
     * @return 品牌列表 {@link GoodsBrandListResponse}
     */
    @PostMapping("/goods/${application.goods.version}/company/list")
    BaseResponse<GoodsCompanyListResponse> list(@RequestBody @Valid GoodsCompanyListRequest request);
}
