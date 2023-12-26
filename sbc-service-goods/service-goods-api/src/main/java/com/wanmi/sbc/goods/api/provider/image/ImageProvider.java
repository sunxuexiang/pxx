package com.wanmi.sbc.goods.api.provider.image;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.image.ImagesQueryRequest;
import com.wanmi.sbc.goods.api.response.image.ImagesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "ImageProvider")
public interface ImageProvider {

    @RequestMapping(value = "/goods/${application.goods.version}/images", method = RequestMethod.POST)
    BaseResponse<ImagesResponse> listByGoodsIds(@RequestBody @Valid ImagesQueryRequest imagesQueryRequest);
}
