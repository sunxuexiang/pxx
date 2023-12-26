package com.wanmi.sbc.goods.api.provider.goodsattribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeAddRequest;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeAddResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeListResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributePageResponse;
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
@FeignClient(value = "${application.goods.name}", contextId = "GoodsAttributeQueryProvider")
public interface GoodsAttributeQueryProvider {


    /**
     * 分页数据
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 新增结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttribute/page")
    BaseResponse<GoodsAttributePageResponse> page(@RequestBody @Valid GoodsAttributeQueryRequest request);

    /**
     *  集合数据
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttribute/getList")
    BaseResponse<GoodsAttributeListResponse> getList(@RequestBody @Valid GoodsAttributeQueryRequest request);

    /**
     *  单调数据
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttribute/findByGoodsAttributeId")
    BaseResponse<GoodsAttributeAddResponse> findByGoodsAttributeId(@RequestBody @Valid GoodsAttributeAddRequest request);
}
