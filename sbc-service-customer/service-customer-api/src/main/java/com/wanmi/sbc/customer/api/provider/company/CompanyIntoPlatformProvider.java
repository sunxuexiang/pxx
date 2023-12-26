package com.wanmi.sbc.customer.api.provider.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBulkMarketAddRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierRecommendAddRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabAddRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallBulkMarketSaveResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierRecommendResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.response.company.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}",contextId = "CompanyIntoPlatformProvider")
public interface CompanyIntoPlatformProvider {

    @PostMapping("/customer/${application.customer.version}/company-into-platform/market-add")
    BaseResponse<CompanyMallBulkMarketSaveResponse> addMarket(@RequestBody CompanyMallBulkMarketAddRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/market-edit")
    BaseResponse<CompanyMallBulkMarketSaveResponse> editMarket(@RequestBody CompanyMallBulkMarketAddRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-recommend-add")
    BaseResponse<CompanyMallSupplierRecommendResponse> addSupplierRecommend(@RequestBody CompanyMallSupplierRecommendAddRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-recommend-edit")
    BaseResponse<CompanyMallSupplierRecommendResponse> editSupplierRecommend(@RequestBody CompanyMallSupplierRecommendAddRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-tab-add")
    BaseResponse<CompanyMallSupplierTabResponse> addSupplierTab(@RequestBody CompanyMallSupplierTabAddRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-tab-edit")
    BaseResponse<CompanyMallSupplierTabResponse> editSupplierTab(@RequestBody CompanyMallSupplierTabAddRequest request);



    @PostMapping("/customer/${application.customer.version}/company-into-platform/return-goods-address-save")
    BaseResponse<CompanyMallReturnGoodsAddressResponse> saveReturnGoodsAddress(@RequestBody CompanyMallReturnGoodsAddressAddRequest request);


    @PostMapping("/customer/${application.customer.version}/company-into-platform/contract-relation-save")
    BaseResponse<CompanyMallContactRelationBatchSaveResponse> batchContactRelation(@RequestBody CompanyMallContactRelationBatchSaveRequest request);


    @PostMapping("/customer/${application.customer.version}/company-into-platform/batch-edit-sort")
    BaseResponse<Integer> batchEditSort(@RequestBody CompanyMallBatchSortEditRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/edit-sort")
    BaseResponse<Integer> eidtSort(@RequestBody CompanyMallSortEditRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/market-refresh-name")
    BaseResponse<Integer> refreshSearchName(@RequestBody CompanyMallMarketRefreshNameRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/contract-relation-sort-batch")
    BaseResponse<Boolean> batchSortCompanyMallRelation(@RequestBody CompanyMallContactRelationBatchSortRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/batch-add-supplier-recommend")
    BaseResponse<List<CompanyMallSupplierRecommendResponse>> batchAddSupplierRecommend(@RequestBody CompanyMallSupplierRecommendBatchAddRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/batch-edit-sort-supplier-recommend")
    BaseResponse<Boolean> editSort(@RequestBody CompanyMallSupplierRecommendSortRequest request);
}
