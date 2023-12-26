package com.wanmi.sbc.wms.api.provider.wms;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.request.wms.InventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @ClassName: RequestWMSInventoryProvider
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 17:36
 * @Version: 1.0
 */
@FeignClient(value = "${application.wms.name}", url="${feign.url.wms:#{null}}", contextId = "RequestWMSInventoryProvider.class" )
public interface RequestWMSInventoryProvider {

    /**
     * 查询WMS库存
     *
     * @author lihui
     * @param inventoryQueryRequest 请求记录新增参数结构 {@link InventoryQueryRequest}
     */
    @PostMapping("/wms/${application.wms.version}/inventory/get-by-id")
    BaseResponse<InventoryQueryResponse> queryInventory(@RequestBody @Valid InventoryQueryRequest inventoryQueryRequest);


    /**
     * 批量查询WMS库存（根据skuids）———— 库存同步时使用
     *
     * @author lihui
     * @param inventoryQueryRequest 请求记录新增参数结构 {@link InventoryQueryRequest}
     */
    @PostMapping("/wms/${application.wms.version}/inventory/fetch-stocks-by-ids")
    BaseResponse<InventoryQueryResponse> batchQueryInventory(@RequestBody @Valid BatchInventoryQueryRequest inventoryQueryRequest);



    /**
     * 单个查询sku
     *
     * @author lihui
     * @param inventoryQueryRequest 请求记录新增参数结构 {@link InventoryQueryRequest}
     */
    @PostMapping("/wms/${application.wms.version}/inventory/get-by-sku")
    BaseResponse<InventoryQueryResponse> queryInventoryBySku(@RequestBody @Valid InventoryQueryRequest inventoryQueryRequest);


}
