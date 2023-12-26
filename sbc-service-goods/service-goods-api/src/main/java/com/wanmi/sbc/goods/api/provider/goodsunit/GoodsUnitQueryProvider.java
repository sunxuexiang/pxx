package com.wanmi.sbc.goods.api.provider.goodsunit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeAddRequest;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeQueryRequest;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitAddRequest;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeAddResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeListResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributePageResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitListResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品属性调用</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@FeignClient(value = "${application.goods.name}", contextId = "GoodsUnitQueryProvider")
public interface GoodsUnitQueryProvider {


    /**
     * 分页数据
     *
     * @param request {@link StoreGoodsUnitQueryRequest}
     * @return 新增结果 {@link GoodsUnitPageResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsUnit/page")
    BaseResponse<GoodsUnitPageResponse> page(@RequestBody @Valid StoreGoodsUnitQueryRequest request);

    /**
     *  集合数据
     *
     * @param request {@link StoreGoodsUnitQueryRequest}
     * @return 修改结果 {@link GoodsUnitListResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsUnit/getList")
    BaseResponse<GoodsUnitListResponse> getList(@RequestBody @Valid StoreGoodsUnitQueryRequest request);

    /**
     *  单调数据
     *
     * @param request {@link StoreGoodsUnitAddRequest}
     * @return 修改结果 {@link GoodsUnitAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsUnit/findByStoreGoodsUnitId")
    BaseResponse<GoodsUnitAddResponse> findByStoreGoodsUnitId(@RequestBody @Valid StoreGoodsUnitAddRequest request);
}
