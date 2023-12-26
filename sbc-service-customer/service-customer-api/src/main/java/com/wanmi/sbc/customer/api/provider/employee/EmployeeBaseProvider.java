package com.wanmi.sbc.customer.api.provider.employee;

import com.wanmi.sbc.customer.api.request.employee.BaseConfigSettingRopResponse;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByAccountNameAndTypeRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeUnlockByIdRequest;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.employee.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "EmployeeBaseProvider")
public interface EmployeeBaseProvider {

    /**
     * 根据账号名称查询
     *
     * @param employeeByAccountNameAndTypeRequest {@link EmployeeByAccountNameAndTypeRequest}
     * @return 员工信息 {@link EmployeeByAccountNameAndTypeResponse}
     */
    @PostMapping("/customer/${application.customer.version}/employee/get-by-account-name-type")
    BaseUtilResponse<EmployeeByAccountNameAndTypeResponse> getByAccountName(@RequestBody @Valid EmployeeByAccountNameAndTypeRequest employeeByAccountNameAndTypeRequest);


    /**
     * 解锁员工
     *
     * @param employeeUnlockByIdRequest {@link EmployeeUnlockByIdRequest}
     * @return 解锁员工结果 {@link EmployeeUnlockResponse}
     */
    @PostMapping("/customer/${application.customer.version}/employee/unlock-by-employee-id")
    BaseUtilResponse<EmployeeUnlockResponse> unlockByEmpId(@RequestBody @Valid EmployeeUnlockByIdRequest employeeUnlockByIdRequest);

    /**
     * 修改员工登录时间
     *
     * @param employeeId {@link String}
     * @return 修改员工登录时间结果 {@link EmployeeLoginTimeModifyResponse}
     */
    @PostMapping("/customer/${application.customer.version}/employee/modify-login-time-by-employee-id")
    BaseUtilResponse<EmployeeLoginTimeModifyResponse> modifyLoginTimeByEmpId(@RequestBody @Valid String employeeId);

    /**
     * 修改员工登录错误次数
     *
     * @param employeeId {@link String}
     * @return 修改员工登录错误次数结果 {@link EmployeeLoginErrorCountModifyByIdResponse}
     */
    @PostMapping("/customer/${application.customer.version}/employee/modify-login-error-count-by-employee-id")
    BaseUtilResponse<EmployeeLoginErrorCountModifyByIdResponse> modifyLoginErrorCountByEmpId(@RequestBody @Valid String employeeId);

    /**
     * 修改员工登录锁定时间
     *
     * @param employeeId {@link String}
     * @return 修改员工登录锁定时间结果 {@link EmployeeLoginLockTimeModifyByIdResponse}
     */
    @PostMapping("/customer/${application.customer.version}/employee/modify-login-lock-time-by-employee-id")
    BaseUtilResponse<EmployeeLoginLockTimeModifyByIdResponse> modifyLoginLockTimeByEmpId(@RequestBody @Valid String employeeId);

    /**
     * 设置用户登录信息
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/employee/get-base-setting-config")
    BaseUtilResponse<BaseConfigSettingRopResponse> getBaseConfig();

}
