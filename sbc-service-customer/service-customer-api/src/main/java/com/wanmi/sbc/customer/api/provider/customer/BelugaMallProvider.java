package com.wanmi.sbc.customer.api.provider.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.contract.BelugaInfoContractFindRequest;
import com.wanmi.sbc.customer.api.request.contract.BelugaMallContractFindRequest;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractRequest;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractSaveRequest;
import com.wanmi.sbc.customer.api.request.fadada.FadadaNotifyRequest;
import com.wanmi.sbc.customer.api.request.fadada.FadadaRegisterRequest;
import com.wanmi.sbc.customer.api.response.customer.*;
import com.wanmi.sbc.customer.api.response.fadada.BelugaMallContractResponese;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "BelugaMallProvider")
public interface BelugaMallProvider {
    @PostMapping("/beluga/${application.customer.version}/beluga/add-beluga-mall-contract")
    BaseResponse addBelugaMallContract(@RequestBody BelugaMallContractRequest batchRequest);

    @PostMapping("/beluga/${application.customer.version}/beluga/return-register")
    BaseResponse returnRegister(@RequestBody FadadaRegisterRequest batchRequest);

    @PostMapping("/beluga/${application.customer.version}/beluga/astc-extsign")
    BaseResponse astcExtSign(@RequestBody FadadaNotifyRequest batchRequest);

    @PostMapping("/beluga/${application.customer.version}/beluga/find-belugamall-contract")
    BaseResponse findBelugaMallContract(@RequestBody BelugaMallContractFindRequest batchRequest);


    @PostMapping("/beluga/${application.customer.version}/beluga/find-belugamall-contract-by-tra")
    BaseResponse<BelugaMallContractResponese> findBylugaMallContractByTra(@RequestBody BelugaMallContractFindRequest batchRequest);

    @PostMapping("/beluga/${application.customer.version}/beluga/save-frist-contract")
    BaseResponse saveFristBelugaMallContract(@RequestBody BelugaMallContractSaveRequest batchRequest);


    @PostMapping("/beluga/${application.customer.version}/beluga/find-info-contract")
    BaseResponse findBelugaInfo(@RequestBody BelugaInfoContractFindRequest batchRequest);

}
