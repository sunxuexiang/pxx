package com.wanmi.sbc.customer.api.provider.store;

import com.wanmi.sbc.customer.api.request.store.StoreQueryByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.store.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "StoreBaseQueryProvider")
public interface StoreBaseQueryProvider {

    /**
     * 根据商家id查询店铺信息
     *
     * @param storeQueryByCompanyInfoIdRequest 带商家id的请求参数 {@link StoreQueryByCompanyInfoIdRequest}
     * @return 店铺信息 {@link StoreQueryByCompanyInfoIdResponse}
     */
    @PostMapping("/customer/${application.customer.version}/store/query-store-by-company-info-id")
    BaseUtilResponse<StoreQueryByCompanyInfoIdResponse> getStoreByCompanyInfoId(@RequestBody @Valid StoreQueryByCompanyInfoIdRequest
                                                                                        storeQueryByCompanyInfoIdRequest);


    /**
     * 根据storeId查询包含商家店铺和主账号的信息
     *
     * @param storeId 带storeId的请求参数 {@link Long}
     * @return 店铺基础信息 {@link StoreInfoResponse}
     */
    @PostMapping("/customer/${application.customer.version}/store/get-store-info-by-store-id")
    BaseUtilResponse<StoreQueryByUserIdResponse> getStoreInfoByStoreId(@RequestBody @Valid Long storeId);


    /**
     * 根据storeId查询未删除店铺信息，不存在则做异常抛出
     *
     * @param storeId 带storeId的请求参数 {@link Long}
     * @return 店铺信息 {@link NoDelStoreQueryByStoreIdResponse}
     */
    @PostMapping("/customer/${application.customer.version}/store/get-no-delete-store-by-store-id")
    BaseUtilResponse<NoDelStoreQueryByStoreIdResponse> getNoDeleteStoreByStoreId(@RequestBody @Valid Long storeId);

}
