package com.wanmi.sbc.customer.provider.impl.contract;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.contract.ManagerContractProviderTo;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import com.wanmi.sbc.customer.contract.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class ContractController implements ManagerContractProviderTo {

    @Autowired
    private ContractService contractService;

    public BaseResponse uploadContract(@RequestBody  ContractUploadRequest request) {

        return contractService.uploadContract(request);
    }

    public String  seachValidContract() {
        return contractService.seachValidContract();
    }

    public BaseResponse<String> updateContractStatus (ContractUpdateRequest contractUpdateRequest) {
        return contractService.updateContractStatus(contractUpdateRequest);
    }

    public BaseResponse<List<UploadContractResponese>> viewContract () {
        return contractService.viewContract();
    }

    @Override
    public BaseResponse<List<UploadContractResponese>> viewContractByIsPerson(ContractUploadRequest request) {
        return contractService.findByIsPerson(request);
    }

    @Override
    public BaseResponse<String> delContractStatus(ContractUpdateRequest contractUpdateRequest) {
        return contractService.delContractStatus(contractUpdateRequest);
    }

    public BaseResponse<List<UploadContractResponese>> seachIsPersonContract (ContractUpdateRequest contractUpdateRequest) {
        return BaseResponse.success(contractService.seachIsPersonContract(contractUpdateRequest));
    }


}
