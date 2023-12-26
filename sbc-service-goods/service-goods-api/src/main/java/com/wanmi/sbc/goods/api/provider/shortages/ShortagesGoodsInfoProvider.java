package com.wanmi.sbc.goods.api.provider.shortages;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoAddRequest;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoQueryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "ShortagesGoodsInfoProvider")
public interface ShortagesGoodsInfoProvider {

    /**
     * 批量插入等货中商品
     *
     * @param request 包含商品sku编号商品sku信息删除结构 {@link ShortagesGoodsInfoAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/shortages/info/saveAll")
    BaseResponse saveAll(@RequestBody @Valid ShortagesGoodsInfoAddRequest request);

    /**
     * 批量删除等货中商品
     *
     * @param request 包含商品sku编号商品sku信息删除结构 {@link ShortagesGoodsInfoAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/shortages/info/delete-by-checkTime")
    BaseResponse deleteByCheckTime(@RequestBody @Valid ShortagesGoodsInfoQueryRequest request);

}
