package com.wanmi.sbc.goods.api.provider.activitygoodspicture;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureGetRequest;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureRequest;
import com.wanmi.sbc.goods.api.response.info.ActivityGoodsViewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "ActivityGoodsPictureProvider")
public interface ActivityGoodsPictureProvider {

    /**
     * 初始化商品
     * @return {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/wholesale/add-update-activity-goods-picture")
    BaseResponse addOrUpdateActivityGoodsPicture(@RequestBody @Valid ActivityGoodsPictureRequest request);


    /**
     * 初始化商品
     * @return {@link BaseResponse}
     */
    @PostMapping("/goods/${application.goods.version}/wholesale/get-by-goods")
    BaseResponse<ActivityGoodsViewResponse> getByGoods(@RequestBody @Valid ActivityGoodsPictureGetRequest request);
}
