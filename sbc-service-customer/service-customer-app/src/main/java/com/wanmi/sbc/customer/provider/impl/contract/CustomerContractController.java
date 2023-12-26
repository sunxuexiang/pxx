package com.wanmi.sbc.customer.provider.impl.contract;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.contract.CustomerContractProviderTo;
import com.wanmi.sbc.customer.api.provider.contract.ManagerContractProviderTo;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.fadada.WordParamsRequest;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import com.wanmi.sbc.customer.contract.service.ContractService;
import com.wanmi.sbc.customer.contract.service.CustomerContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class CustomerContractController implements CustomerContractProviderTo {

    @Autowired
    private CustomerContractService customerContractService;

    @Override
    public BaseResponse save(@RequestBody WordParamsRequest request) {
        customerContractService.save(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse saveAndUpdate(@RequestBody WordParamsRequest request) {
        customerContractService.saveAndUpdate(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<WordParamsRequest> findByEmployeeId(@RequestBody WordParamsRequest request){
        return BaseResponse.success(customerContractService.findByEmployeeId(request));
    }

    @Override
    @LcnTransaction
    public BaseResponse delContractInfo(WordParamsRequest request) {

        return customerContractService.delContractInfo(request);
    }
}
