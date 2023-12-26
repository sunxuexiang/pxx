package com.wanmi.sbc.customer.provider.impl.contract;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.customer.BelugaMallProvider;
import com.wanmi.sbc.customer.api.request.contract.BelugaInfoContractFindRequest;
import com.wanmi.sbc.customer.api.request.contract.BelugaMallContractFindRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractRequest;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractSaveRequest;
import com.wanmi.sbc.customer.api.request.fadada.FadadaNotifyRequest;
import com.wanmi.sbc.customer.api.request.fadada.FadadaRegisterRequest;
import com.wanmi.sbc.customer.api.response.fadada.BelugaMallContractPageResponese;
import com.wanmi.sbc.customer.api.response.fadada.BelugaMallContractResponese;
import com.wanmi.sbc.customer.api.response.fadada.FadadaParamsResponese;
import com.wanmi.sbc.customer.contract.service.BelugaMallContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class BelugaMallContractController implements BelugaMallProvider {
    @Autowired
    private BelugaMallContractService belugaMallContractService;

    @Override
    public BaseResponse<FadadaParamsResponese> addBelugaMallContract(@RequestBody BelugaMallContractRequest request) {

        return belugaMallContractService.addBelugaMallContract(request);
    }

    @Override
    public BaseResponse saveFristBelugaMallContract(@RequestBody BelugaMallContractSaveRequest request) {

        return belugaMallContractService.saveFristBelugaMallContract(request);
    }

    @Override
    public BaseResponse findBelugaInfo(BelugaInfoContractFindRequest batchRequest) {
        return belugaMallContractService.findBelugaInfo(batchRequest);
    }

    @Override
    public BaseResponse returnRegister(@RequestBody FadadaRegisterRequest request) {
        return  belugaMallContractService.returnRegister(request.getCompanyName(),request.getTransactionNo(),request.getAuthenticationType(),request.getStatus(),request.getSign());
    }

    @Override
    public BaseResponse astcExtSign(@RequestBody FadadaNotifyRequest request) {
        return  belugaMallContractService.astcExtSign(request.getTransaction_id(),request.getResult_code(),request.getResult_desc(),request.getDownload_url(),request.getViewpdf_url());
    }

    @Override
    public BaseResponse<BelugaMallContractPageResponese> findBelugaMallContract(@RequestBody  BelugaMallContractFindRequest request) {
        return  belugaMallContractService.findBelugaMallContract(request);
    }

    @Override
    public BaseResponse<BelugaMallContractResponese> findBylugaMallContractByTra(@RequestBody BelugaMallContractFindRequest request) {
        return belugaMallContractService.findBylugaMallContractByTra(request);
    }

}
