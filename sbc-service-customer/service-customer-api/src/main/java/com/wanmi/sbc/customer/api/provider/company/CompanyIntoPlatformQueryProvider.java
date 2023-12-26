package com.wanmi.sbc.customer.api.provider.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.response.company.*;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "CompanyIntoPlatformQueryProvider")
public interface CompanyIntoPlatformQueryProvider {

    @PostMapping("/customer/${application.customer.version}/company-into-platform/market-page")
    BaseResponse<CompanyMallBulkMarketPageResponse> pageMarket(@RequestBody CompanyMallBulkMarketPageRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/market-get-byId")
    BaseResponse<CompanyMallBulkMarketResponse> getByIdForMarket(@RequestBody CompanyMallBulkMarketQueryRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/market/list-by-store")
    BaseResponse<List<CompanyMallBulkMarketVO>> listMarketByStoreId(@RequestParam("storeId") Long storeId);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/market/get-one-by-store")
    BaseResponse<CompanyMallBulkMarketVO> getMarketByStoreId(@RequestParam("storeId") Long storeId);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier/get-one-by-store")
    BaseResponse<CompanyMallSupplierTabResponse> getSupplierByStoreId(@RequestParam("storeId") Long storeId);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-tab-page")
    BaseResponse<CompanyMallSupplierTabPageResponse> pageSupplierTab(@RequestBody CompanyMallSupplierTabPageRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-tab-list")
    BaseResponse<List<CompanyMallSupplierTabVO>> listSupplierTab(@RequestBody CompanyMallSupplierTabListQueryRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-tab-get-byId")
    BaseResponse<CompanyMallSupplierTabResponse> getByIdSupplierTab(@RequestBody CompanyMallSupplierTabQueryRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-recommend-page")
    BaseResponse<CompanyMallSupplierRecommendPageResponse> pageSupplierRecommend(@RequestBody CompanyMallSupplierRecommendPageRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-recommend-get-byId")
    BaseResponse<CompanyMallSupplierRecommendResponse> getByIdSupplierRecommend(@RequestBody CompanyMallSupplierRecommendQueryRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/contract-relation-page")
    BaseResponse<CompanyMallContractRelationPageResponse> pageContractRelation(@RequestBody CompanyMallContractRelationPageRequest request);


    @PostMapping("/customer/${application.customer.version}/company-into-platform/listReturnGoodsAddress")
    BaseResponse<List<CompanyMallReturnGoodsAddressVO>> listReturnGoodsAddress(@RequestBody CompanyMallReturnGoodsAddressRequest request);
}
