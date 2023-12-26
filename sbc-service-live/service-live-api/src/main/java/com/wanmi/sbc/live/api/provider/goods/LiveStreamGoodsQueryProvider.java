package com.wanmi.sbc.live.api.provider.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsInfoListResponse;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>直播商品查询服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveStreamGoodsQueryProvider")
public interface LiveStreamGoodsQueryProvider {

    /**
     * 列表查询直播商品API
     *
     * @author zwb
     * @return 直播商品的列表信息 {@link LiveStreamGoodsListResponse}
     */
    @PostMapping("/live/${application.live.version}/liveStreamgoods/list")
    BaseResponse<LiveStreamGoodsListResponse> list(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest);

    /**
            * 列表查询直播商品infoAPI
     *
             * @author zwb
     * @return 直播商品的列表信息 {@link LiveStreamGoodsListResponse}
     */
    @PostMapping("/live/${application.live.version}/liveStreamgoods/listInfo")
    BaseResponse<LiveStreamGoodsInfoListResponse> listInfo(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest);
}
