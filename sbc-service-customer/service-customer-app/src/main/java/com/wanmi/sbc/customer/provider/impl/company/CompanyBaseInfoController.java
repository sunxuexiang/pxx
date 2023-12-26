package com.wanmi.sbc.customer.provider.impl.company;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.base.MicroServiceBasePage;
import com.wanmi.sbc.customer.api.provider.company.CompanyBaseInfoProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageBaseRequest;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageBaseResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabBaseResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.api.vo.CompanyMallContractRelationBaseVO;
import com.wanmi.sbc.customer.company.model.root.CompanyMallContractRelation;
import com.wanmi.sbc.customer.company.request.CompanyMallContractRelationRequest;
import com.wanmi.sbc.customer.company.service.CompanyMallContractRelationService;
import com.wanmi.sbc.customer.company.service.CompanyMallSupplierTabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class CompanyBaseInfoController implements CompanyBaseInfoProvider {

    @Autowired
    private CompanyMallContractRelationService companyMallContractRelationService;

    @Autowired
    private CompanyMallSupplierTabService supplierTabService;

    @Override
    public BaseUtilResponse<CompanyMallSupplierTabBaseResponse> getByRelationIdSupplierTab(@RequestBody Long relationId){
        CompanyMallSupplierTabBaseResponse companyMallSupplierTabBaseResponse = new CompanyMallSupplierTabBaseResponse();

        CompanyMallSupplierTabResponse response = supplierTabService.getCompanyMallSupplierTabResponseById(relationId);
        KsBeanUtil.copyPropertiesThird(response,companyMallSupplierTabBaseResponse);
        return BaseUtilResponse.success(companyMallSupplierTabBaseResponse);
    }


    @Override
    public BaseUtilResponse<CompanyMallContractRelationPageBaseResponse> pageContractRelationBase(CompanyMallContractRelationPageBaseRequest request) {
        CompanyMallContractRelationRequest targetRequest = new CompanyMallContractRelationRequest();
        KsBeanUtil.copyPropertiesThird(request, targetRequest);
        Page<CompanyMallContractRelation> companyInfoPage = companyMallContractRelationService.page(targetRequest);
        List<CompanyMallContractRelationBaseVO> voList = KsBeanUtil.convertList(companyInfoPage.getContent(), CompanyMallContractRelationBaseVO.class);
        CompanyMallContractRelationPageBaseResponse response = CompanyMallContractRelationPageBaseResponse.builder().page(new MicroServiceBasePage<>(voList, request.getPageable(), companyInfoPage.getTotalElements())).build();
        return BaseUtilResponse.success(response);
    }
}
