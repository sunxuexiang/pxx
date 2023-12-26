package com.wanmi.sbc.goods.api.provider.goodsdevanning;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchUpdateRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.response.goods.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * com.wanmi.sbc.goods.api.provider.goods.GoodsProvider
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午9:30
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsDevanningProvider")
public interface GoodsDevanningProvider {


    @PostMapping("/goodsdevving/${application.goods.version}/getmaxdata")
    BaseResponse<GoodsDevanningResponse> getmaxdata(@RequestBody @Valid GoodsDevanningQueryRequest request);


}

