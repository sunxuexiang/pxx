package com.wanmi.sbc.goods.api.provider.spec;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecAddRequest;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.response.spec.GoodsInfoSpecAddResponse;
import com.wanmi.sbc.goods.api.response.spec.GoodsSpecListByGoodsIdsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 商品规格服务
 *
 * @author chenjun
 * @dateTime 2020/6/1 19:57
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsSpecProvider")
public interface GoodsSpecProvider {

    @PostMapping("/goods/${application.goods.version}/spec/addSpec")
    BaseResponse<GoodsInfoSpecAddResponse> addSpec(@RequestBody @Valid GoodsSpecAddRequest request) ;
}
