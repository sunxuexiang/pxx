package com.wanmi.sbc.customer.provider.impl.store;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreBaseQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreQueryByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.store.NoDelStoreQueryByStoreIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.api.response.store.StoreQueryByCompanyInfoIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreQueryByUserIdResponse;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class StoreBaseQueryController implements StoreBaseQueryProvider {

    @Autowired
    private StoreService storeService;

    @Override
    public BaseUtilResponse<StoreQueryByCompanyInfoIdResponse> getStoreByCompanyInfoId(@RequestBody @Valid StoreQueryByCompanyInfoIdRequest storeQueryByCompanyInfoIdRequest){
        Store store = storeService.queryStoreByCompanyInfoId(storeQueryByCompanyInfoIdRequest.getCompanyInfoId());
        return BaseUtilResponse.success(new StoreQueryByCompanyInfoIdResponse(storeService.wraper2BaseVoFromStore(store)));
    }

    @Override
    public BaseUtilResponse<StoreQueryByUserIdResponse> getStoreInfoByStoreId(Long storeId) {
        StoreInfoResponse storeInfoResponse = storeService.queryStoreInfo(storeId);
        Store store = new Store();
        KsBeanUtil.copyPropertiesThird(storeInfoResponse,store);
        return BaseUtilResponse.success(new StoreQueryByUserIdResponse(storeService.wraper2BaseVoFromStore(store)));
    }

    @Override
    public BaseUtilResponse<NoDelStoreQueryByStoreIdResponse> getNoDeleteStoreByStoreId(Long storeId) {
        Store store = storeService.findOne(storeId);
        return BaseUtilResponse.success(new NoDelStoreQueryByStoreIdResponse(storeService.wraper2BaseVoFromStore(store)));
    }

}
