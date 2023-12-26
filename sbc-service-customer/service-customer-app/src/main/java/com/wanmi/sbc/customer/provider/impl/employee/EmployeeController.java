package com.wanmi.sbc.customer.provider.impl.employee;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeProvider;
import com.wanmi.sbc.customer.api.request.employee.*;
import com.wanmi.sbc.customer.api.response.employee.*;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeDisableOrEnableByCompanyIdVO;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.service.EmployeeService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class EmployeeController implements EmployeeProvider {

    @Autowired
    private EmployeeService employeeService;

    @Override

    public BaseResponse<EmployeeAddResponse> add(@RequestBody @Valid EmployeeAddRequest request) {
        Optional<Employee> employeeOptional = employeeService.saveEmployee(request);

        EmployeeAddResponse response = employeeOptional
                .map(employee -> {
                    EmployeeAddResponse employeeAddResponse = new EmployeeAddResponse();
                    KsBeanUtil.copyPropertiesThird(employee, employeeAddResponse);
                    return employeeAddResponse;
                })
                .orElseGet(null);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<EmployeeModifyResponse> modify(@RequestBody @Valid EmployeeModifyRequest request) {
        Optional<Employee> employeeOptional = employeeService.updateEmployee(request);

        EmployeeModifyResponse response = employeeOptional
                .map(employee -> {
                    EmployeeModifyResponse employeeModifyResponse = new EmployeeModifyResponse();
                    KsBeanUtil.copyPropertiesThird(employee, employeeModifyResponse);
                    return employeeModifyResponse;
                })
                .orElseGet(null);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse modifyAllById(@RequestBody @Valid EmployeeModifyAllRequest request) {
        employeeService.update(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse modifyNameById(@RequestBody @Valid EmployeeNameModifyByIdRequest request) {
        employeeService.updateEmployeeName(request.getEmployeeName(), request.getEmployeeId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse modifyMobileById(@RequestBody @Valid EmployeeMobileModifyByIdRequest request) {
        employeeService.updateEmployeeMobile(request.getMobile(), request.getEmployeeId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse<EmployeeLoginTimeModifyResponse> modifyLoginTimeById(
            @RequestBody @Valid EmployeeLoginTimeModifyByIdRequest request) {

        EmployeeLoginTimeModifyResponse response = new EmployeeLoginTimeModifyResponse();

        int count = employeeService.updateLoginTime(request.getEmployeeId());
        response.setCount(count);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<EmployeeUnlockResponse> unlockById(@RequestBody @Valid EmployeeUnlockByIdRequest request) {
        EmployeeUnlockResponse response = new EmployeeUnlockResponse();

        int count = employeeService.unlockEmpoyee(request.getEmployeeId());
        response.setCount(count);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse batchEnableByIds(@RequestBody @Valid EmployeeBatchEnableByIdsRequest request) {
        employeeService.enableEmployee(request.getEmployeeIds());
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse disableById(@RequestBody @Valid EmployeeDisableByIdRequest request) {
        employeeService.disableEmployee(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse<EmployeeDisableOrEnableByCompanyIdResponse> disableOrEnableByCompanyId(
            @RequestBody @Valid EmployeeDisableOrEnableByCompanyIdRequest employeeDisableOrEnableRequest) {

        Optional<List<Employee>> optionalEmployeeList = employeeService.disableOrEnable(employeeDisableOrEnableRequest);

        EmployeeDisableOrEnableByCompanyIdResponse response = optionalEmployeeList
                .map(employeeList -> {
                    EmployeeDisableOrEnableByCompanyIdResponse employeeDisableOrEnableByCompanyIdResponse =
                            new EmployeeDisableOrEnableByCompanyIdResponse();
                    List<EmployeeDisableOrEnableByCompanyIdVO> employeeDisableOrEnableVOList = new ArrayList<>();
                    KsBeanUtil.copyList(employeeList, employeeDisableOrEnableVOList);
                    employeeDisableOrEnableByCompanyIdResponse.setEmployeeList(employeeDisableOrEnableVOList);
                    return employeeDisableOrEnableByCompanyIdResponse;
                })
                .orElseGet(null);

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse batchDisableByIds(@RequestBody @Valid EmployeeBatchDisableByIdsRequest request) {
        employeeService.batchDisableEmployee(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse batchDeleteByIds(@RequestBody @Valid EmployeeBatchDeleteByIdsRequest request) {
        employeeService.deleteEmployee(request.getEmployeeIds(), request.getAccountType());
        return BaseResponse.SUCCESSFUL();
    }

    @Override

    public BaseResponse<EmployeeLoginErrorCountModifyByIdResponse> modifyLoginErrorCountById(
            @RequestBody @Valid EmployeeLoginErrorCountModifyByIdRequest request) {

        EmployeeLoginErrorCountModifyByIdResponse response = new EmployeeLoginErrorCountModifyByIdResponse();

        int count = employeeService.updateLoginErrorCount(request.getEmployeeId());
        response.setCount(count);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<EmployeeLoginLockTimeModifyByIdResponse> modifyLoginLockTimeById(@RequestBody @Valid
                                                                                                     EmployeeLoginLockTimeModifyByIdRequest request) {

        EmployeeLoginLockTimeModifyByIdResponse response = new EmployeeLoginLockTimeModifyByIdResponse();

        int count = employeeService.updateLoginLockTime(request.getEmployeeId());
        response.setCount(count);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<EmployeePasswordModifyByIdResponse> modifyPasswordById(@RequestBody @Valid
                                                                                           EmployeePasswordModifyByIdRequest request) {

        EmployeePasswordModifyByIdResponse response = new EmployeePasswordModifyByIdResponse();

        int count = employeeService.setAccountPassword(request.getEmployeeId(), request.getEncodePwd());
        response.setCount(count);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<EmployeeRegisterResponse> register(@RequestBody @Valid EmployeeRegisterRequest request) {
        Optional<Employee> optionalEmployee = employeeService.register(request);

        EmployeeRegisterResponse response = optionalEmployee
                .map(employee -> {
                    EmployeeRegisterResponse employeeRegisterResponse = new EmployeeRegisterResponse();
                    KsBeanUtil.copyPropertiesThird(employee, employeeRegisterResponse);
                    return employeeRegisterResponse;
                })
                .orElseGet(null);

        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse sms(@RequestBody @Valid EmployeeSmsRequest employeeSmsRequest) {
        if(Constants.yes.equals(employeeService.doMobileSms(employeeSmsRequest.getRedisKey(), employeeSmsRequest.getMobile(),
                employeeSmsRequest.getSmsTemplate()))){
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.FAILED();
    }

    @Override
    public BaseResponse batchDimissionByIds(@RequestBody @Valid EmployeeBatchDimissionByIdsRequest request) {
        employeeService.dimissionEmployeeByIds(request.getEmployeeIds(), request.getAccountState(), request.getAccountDimissionReason());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Integer> batchSetEmployeeByIds(@RequestBody @Valid EmployeeListByIdsRequest request) {
        Integer num = employeeService.batchSetEmployeeByIds(request.getEmployeeIds());
        return BaseResponse.success(num);
    }

    @Override
    public BaseResponse changeDepartment(@RequestBody @Valid EmployeeChangeDepartmentRequest request) {
        employeeService.changeDepartment(request.getEmployeeIds(), request.getDepartmentIds());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Integer> handoverEmployee(@RequestBody @Valid EmployeeHandoverRequest request) {
        Integer num = employeeService.handoverEmployee(request.getEmployeeIds(), request.getNewEmployeeId());
        return BaseResponse.success(num);
    }

    @Override
    public BaseResponse<Integer> activateAccount(@RequestBody @Valid EmployeeActivateAccountRequest request) {
        Integer num = employeeService.activateAccount(request);
        return BaseResponse.success(num);
    }

    @Override
    public BaseResponse<EmployeeExcelExportTemplateResponse> exportTemplate() {
        String file = employeeService.exportTemplate();
        return BaseResponse.success(new EmployeeExcelExportTemplateResponse(file));
    }

    @Override
    public BaseResponse importEmployee(@RequestBody @Valid EmployeeImportRequest request) {
        employeeService.importEmployees(request.getEmployeeList());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse refreshCompanyCode() throws Exception {

        return employeeService.refreshCompanyCode();
    }

    @Override
    public BaseResponse<List<CompanyInfoVO>> refreshIdCardNo() {
        return employeeService.refreshIdCardNo();
    }

    @Override
    @LcnTransaction
    public BaseResponse deleteEmployeeById(EmployeeNameModifyByIdRequest employeeNameModifyByIdRequest) {
        employeeService.deleteEmployeeById(employeeNameModifyByIdRequest);
        return BaseResponse.SUCCESSFUL();
    }
}
