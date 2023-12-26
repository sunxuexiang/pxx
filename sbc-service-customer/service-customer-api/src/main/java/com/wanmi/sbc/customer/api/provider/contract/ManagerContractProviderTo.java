package com.wanmi.sbc.customer.api.provider.contract;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "ManagerContractProviderTo")
public interface ManagerContractProviderTo {
    /**
     * 运营人员上传合同
     *
     * @param request {@link ContractUploadRequest}
     * @return {@link String}
     */
    @PostMapping("/customer/${application.customer.version}/contact/upload-contract")
    BaseResponse<String> uploadContract(@RequestBody ContractUploadRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/seach-valid-contract")
    String seachValidContract();

    @PostMapping("/customer/${application.customer.version}/contact/update-contract-statis")
    BaseResponse<String> updateContractStatus(@RequestBody  ContractUpdateRequest contractUpdateRequest);

    @PostMapping("/customer/${application.customer.version}/contact/view-contract")
    BaseResponse<List<UploadContractResponese>> viewContract ();

    @PostMapping("/customer/${application.customer.version}/contact/view-by-isPersson-contract")
    BaseResponse<List<UploadContractResponese>> viewContractByIsPerson (@RequestBody ContractUploadRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/del-contract-statis")
    BaseResponse<String> delContractStatus(@RequestBody  ContractUpdateRequest contractUpdateRequest);

    @PostMapping("/customer/${application.customer.version}/contact/seach-contract-statis")
    BaseResponse<List<UploadContractResponese>> seachIsPersonContract(@RequestBody  ContractUpdateRequest contractUpdateRequest);

}
