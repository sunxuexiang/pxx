package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationGetRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationDetailResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>在线客服店铺关系</p>
 * @author zzg
 * @date 2023-10-05 16:10:28
 */
@FeignClient(value = "${application.setting.name}", contextId = "CustomerServiceStoreRelationProvider")
public interface CustomerServiceStoreRelationProvider {

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/add")
    BaseResponse addRelation(@RequestBody CustomerServiceStoreRelationRequest request);

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/edit")
    BaseResponse editRelation(@RequestBody CustomerServiceStoreRelationRequest request);

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/delete")
    BaseResponse deleteRelation(@RequestBody CustomerServiceStoreRelationRequest request);

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/getList")
    BaseResponse<MicroServicePage<CustomerServiceStoreRelationResponse>> getList(@RequestBody CustomerServiceStoreRelationGetRequest request);

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/getByCompanyInfoId")
    BaseResponse<CustomerServiceStoreRelationDetailResponse> getByCompanyInfoId(@RequestBody Long companyInfoId);

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/getChildStoreIds")
    BaseResponse<List<Long>> getChildStoreIds(@RequestBody Long storeId);

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/getAllRelationStoreIds")
    BaseResponse<List<Long>> getAllRelationStoreIds();

    @PostMapping("/setting/${application.setting.version}/customerServiceRelation/getAllRelationStores")
    BaseResponse<List<CustomerServiceStoreRelationResponse>> getAllRelationStores();
}
