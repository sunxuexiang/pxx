package com.wanmi.sbc.goods.api.provider.info;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.info.CatWareGetGoodsInfoMappingRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoMappingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsInfoMappingProvider")
public interface GoodsInfoMappingProvider {

    @PostMapping("/goods/${application.goods.version}/catWare/get-goods-info-mapping")
    BaseResponse<List<GoodsInfoMappingVO>> catWareGetGoodsInfoMapping(@RequestBody @Valid CatWareGetGoodsInfoMappingRequest request);
}
