package com.wanmi.sbc.customer.api.provider.employeecontract;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractFindRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractSaveRequest;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractListResponese;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractPageResponese;
import com.wanmi.sbc.customer.api.response.employeecontract.CustomerBlackResponese;
import com.wanmi.sbc.customer.api.request.employeecontract.CustomerBlackRequest;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractResponese;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "EmployeeContractProvider")
public interface EmployeeContractProvider {

    /**
     * 运营人员上传合同
     *
     * @param request {@link ContractUploadRequest}
     * @return {@link String}
     */
    @PostMapping("/customer/${application.customer.version}/contact/save-employee-contract")
    BaseResponse<Void> saveEmployeeContract(@RequestBody @Valid EmployeeContractSaveRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/save")
    BaseResponse<Void> save(@RequestBody @Valid EmployeeContractSaveRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/update-employee-contract")
    BaseResponse<String> updateEmployeeContract(@RequestBody @Valid EmployeeContractSaveRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/find-by-user-contract-id")
    BaseResponse<EmployeeContractResponese> findByUserContractId(@RequestBody @Valid String id);

    @PostMapping("/customer/${application.customer.version}/contact/find-by-user-contract")
    BaseResponse<EmployeeContractPageResponese> findByUserContract(@RequestBody EmployeeContractFindRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/find-by-employeeId")
    BaseResponse<EmployeeContractResponese> findByEmployeeId(@RequestBody String employeeId);

    @PostMapping("/customer/${application.customer.version}/contact/find-by-employeeIds")
    BaseResponse<EmployeeContractListResponese> findByEmployeeIdList(@RequestBody List<String> employeeIdList);

    @PostMapping("/customer/${application.customer.version}/contact/find-by-employeeName")
    BaseResponse<EmployeeContractResponese> findByEmployeeName(@RequestBody String employeeName);

    @PostMapping("/customer/${application.customer.version}/contact/find-by-customerId")
    BaseResponse<EmployeeContractResponese> findByAppCustomerId(@RequestBody String customerId);

    @PostMapping("/customer/${application.customer.version}/contact/update-employee-contract-by-emlpoyee-name")
    BaseResponse<String> updateEmployeeContractByEmlpoyeeName(@RequestBody @Valid EmployeeContractSaveRequest request);

    @PostMapping("/customer/${application.customer.version}/contact/update-employee-contract-by-emlpoyee-SupplierName")
    BaseResponse<EmployeeContractResponese> findByInverstmentIdAndSupplierName (@RequestBody EmployeeContractFindRequest employeeContractFindRequest );

    @PostMapping("/customer/${application.customer.version}/contact/findByStoreName")
    BaseResponse<CustomerBlackResponese> findByStoreName(@RequestBody CustomerBlackRequest customerBlackRequest);

    @PostMapping("/customer/${application.customer.version}/contact/saveCustomerBlack")
    BaseResponse<CustomerBlackResponese> saveCustomerBlack(@RequestBody CustomerBlackRequest customerBlackRequest);

    @PostMapping("/customer/${application.customer.version}/contact/find-by-appId")
    BaseResponse<EmployeeContractResponese> findByAppId(@RequestBody String appId);

    @PostMapping("/customer/${application.customer.version}/contact/delEmployeeContract")
    BaseResponse delEmployeeContract(@RequestBody @Valid EmployeeContractSaveRequest request);



}
