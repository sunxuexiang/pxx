package com.wanmi.sbc.goods.api.provider.shortages;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoAddRequest;
import com.wanmi.sbc.goods.api.request.shortages.ShortagesGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.api.response.shortages.ShortagesGoodsInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "ShortagesGoodsInfoQueryProvider")
public interface ShortagesGoodsInfoQueryProvider {

    /**
     * 按日期查询等货中商品
     *
     * @param request 按日期查询等货中商品 {@link ShortagesGoodsInfoAddRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/shortages/info/by-check-time")
    BaseResponse<ShortagesGoodsInfoResponse> queryShortagesGoodsInfoByCheckTime(@RequestBody @Valid ShortagesGoodsInfoQueryRequest request);
}
