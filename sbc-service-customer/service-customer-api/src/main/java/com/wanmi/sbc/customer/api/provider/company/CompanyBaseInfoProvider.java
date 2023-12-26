package com.wanmi.sbc.customer.api.provider.company;

import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageBaseRequest;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageBaseResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabBaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "CompanyBaseInfoProvider")
public interface CompanyBaseInfoProvider {

    @PostMapping("/customer/${application.customer.version}/company-into-platform/contract-relation-base-page")
    BaseUtilResponse<CompanyMallContractRelationPageBaseResponse> pageContractRelationBase(@RequestBody CompanyMallContractRelationPageBaseRequest request);

    @PostMapping("/customer/${application.customer.version}/company-into-platform/supplier-tab-get-byRelationId")
    BaseUtilResponse<CompanyMallSupplierTabBaseResponse> getByRelationIdSupplierTab(@RequestBody Long relationId);

}
