package com.wanmi.sbc.goods.api.provider.warehouse;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>仓库表查询服务Provider</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "WareHouseStockQueryProvider")
public interface WareHouseStockQueryProvider {


    /**
     *根据仓库ID和skuID匹配库位API
     *
     * @param wareHouseBySkuIdRequest 单个查询仓库表请求参数
     * @return 仓库表详情 {@link WareHouseByIdResponse}
     * @author zhangwenchang
     */
    @PostMapping("/goods/${application.goods.version}/warehousestock/get-by-skuId")
    BaseResponse<WareHouseBySkuIdResponse> getByWareHouseIdAndSkuId(@RequestBody @Valid WareHouseBySkuIdRequest wareHouseBySkuIdRequest);


}

