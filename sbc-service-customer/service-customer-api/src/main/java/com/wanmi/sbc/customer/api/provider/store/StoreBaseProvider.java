package com.wanmi.sbc.customer.api.provider.store;

import com.wanmi.sbc.customer.api.request.store.StoreBaseRequest;
import com.wanmi.sbc.customer.api.request.store.validGroups.StoreUpdate;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.vo.StoreBaseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "StoreBaseProvider")
public interface StoreBaseProvider {

    @PutMapping("/customer/${application.customer.version}/store/update-store")
    BaseUtilResponse<StoreBaseVO> updateStore(@Validated({StoreUpdate.class}) @RequestBody StoreBaseRequest storeBaseRequest);
}
