package com.wanmi.sbc.customer.api.provider.contract;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.fadada.WordParamsRequest;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "CustomerContractProviderTo")
public interface CustomerContractProviderTo {
    /**
     * 运营人员上传合同
     *
     * @param request {@link ContractUploadRequest}
     * @return {@link String}
     */
    @PostMapping("/customer/${application.customer.version}/customercontact/save")
    BaseResponse<String> save(@RequestBody WordParamsRequest request);
    @PostMapping("/customer/${application.customer.version}/customercontact/saveAndUpdate")
    BaseResponse saveAndUpdate(@RequestBody WordParamsRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/findByEmployeeId")
    BaseResponse<WordParamsRequest> findByEmployeeId(@RequestBody WordParamsRequest request);

    @PostMapping("/customer/${application.customer.version}/customercontact/delContractInfo")
    BaseResponse delContractInfo(@RequestBody WordParamsRequest request);


}
