package com.wanmi.sbc.goods.api.provider.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goods.GoodsCopyByStoreRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "${application.goods.name}", url = "${feign.url.goods:#{null}}", contextId = "GoodsHandlerProvider")
public interface GoodsHandlerProvider {


    /**
     * 商品复制
     */
    @PostMapping("/goods/${application.goods.version}/handler/copyGoods/ByStore")
    BaseResponse<List<String>> copyGoodsByStore(@RequestBody GoodsCopyByStoreRequest request);
}
