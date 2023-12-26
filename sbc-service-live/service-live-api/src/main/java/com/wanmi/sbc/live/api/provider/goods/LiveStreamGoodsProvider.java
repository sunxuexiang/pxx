package com.wanmi.sbc.live.api.provider.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityModifyRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsAddRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsModifyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>直播商品保存服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveStreamGoodsProvider")
public interface LiveStreamGoodsProvider {

    /**
     * supplier端保存直播商品
     * @param supplierAddReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStreamGoods/supplier")
    BaseResponse supplier(@RequestBody LiveStreamGoodsAddRequest supplierAddReq);


    /**
     * supplier端更新直播商品
     * @param supplierModifyReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStreamGoods/modify")
    BaseResponse modify(@RequestBody LiveStreamGoodsModifyRequest supplierModifyReq);

    /**
     * 直播商品上下架
     * @param supplierModifyReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStreamGoods/saleGoods")
    BaseResponse saleGoods(@RequestBody LiveStreamGoodsModifyRequest supplierModifyReq);

    /**
     * 直播批量商品上下架及移除
     * @param supplierModifyReq
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveStreamGoods/saleGoodsBatch")
    BaseResponse saleGoodsBatch(@RequestBody LiveStreamGoodsModifyRequest supplierModifyReq);
}
