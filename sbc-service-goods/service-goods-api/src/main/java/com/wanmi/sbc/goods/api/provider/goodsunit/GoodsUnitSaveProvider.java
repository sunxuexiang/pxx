package com.wanmi.sbc.goods.api.provider.goodsunit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitAddRequest;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品单位调用</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@FeignClient(value = "${application.goods.name}", contextId = "GoodsUnitSaveProvider")
public interface GoodsUnitSaveProvider {


    /**
     * 新增商品单位
     *
     * @param request {@link StoreGoodsUnitAddRequest}
     * @return 新增结果 {@link GoodsUnitAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsUnit/add")
    BaseResponse<GoodsUnitAddResponse> add(@RequestBody @Valid StoreGoodsUnitAddRequest request);

    /**
     *  修改商品单位
     *
     * @param request {@link StoreGoodsUnitAddRequest}
     * @return 修改结果 {@link GoodsUnitAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsUnit/updateAttribute")
    BaseResponse<GoodsUnitAddResponse> updateUnit(@RequestBody @Valid StoreGoodsUnitAddRequest request);
    /**
     *  修改商品单位状态
     * @param request {@link StoreGoodsUnitAddRequest}
     * @return 修改结果 {@link GoodsUnitAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsUnit/updateStatus")
    BaseResponse<GoodsUnitAddResponse> updateStatus(@RequestBody @Valid StoreGoodsUnitAddRequest request);

    /**
     *  修改商品单位
     *
     * @param request {@link StoreGoodsUnitAddRequest}
     * @return 修改结果 {@link GoodsUnitAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goodsUnit/deleteById")
    BaseResponse<GoodsUnitAddResponse> deleteById(@RequestBody @Valid StoreGoodsUnitAddRequest request);
}
