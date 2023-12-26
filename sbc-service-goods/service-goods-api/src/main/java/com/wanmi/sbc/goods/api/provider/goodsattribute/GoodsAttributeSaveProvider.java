package com.wanmi.sbc.goods.api.provider.goodsattribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeAddRequest;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeAddResponse;
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
@FeignClient(value = "${application.goods.name}", contextId = "GoodsAttributeSaveProvider")
public interface GoodsAttributeSaveProvider {


    /**
     * 新增商品属性
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 新增结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttribute/add")
    BaseResponse<GoodsAttributeAddResponse> add(@RequestBody @Valid GoodsAttributeAddRequest request);

    /**
     *  修改商品属性
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttribute/updateAttribute")
    BaseResponse<GoodsAttributeAddResponse> updateAttribute(@RequestBody @Valid GoodsAttributeAddRequest request);
    /**
     *  修改商品属性状态
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttribute/updateStatus")
    BaseResponse<GoodsAttributeAddResponse> updateStatus(@RequestBody @Valid GoodsAttributeAddRequest request);

    /**
     *  修改商品属性
     *
     * @param request {@link GoodsAttributeAddRequest}
     * @return 修改结果 {@link GoodsAttributeAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsAttribute/deleteById")
    BaseResponse<GoodsAttributeAddResponse> deleteById(@RequestBody @Valid GoodsAttributeAddRequest request);
}
