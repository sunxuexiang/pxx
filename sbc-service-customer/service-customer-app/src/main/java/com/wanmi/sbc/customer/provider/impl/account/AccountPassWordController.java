package com.wanmi.sbc.customer.provider.impl.account;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.SecurityUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.account.AccountPassWordProvider;
import com.wanmi.sbc.customer.api.request.account.ResetAccountPasswordRequest;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.repository.EmployeeRepository;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-07-18 11:06
 **/
@RestController
public class AccountPassWordController implements AccountPassWordProvider {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public BaseResponse<String> resetPassword(@RequestBody ResetAccountPasswordRequest request) {
        if (request.getAccountType().equals(0)) {
            //客户
            return resetCustomerPassword(request);
        } else if (request.getAccountType().equals(1) || request.getAccountType().equals(2)) {
            //boss 供应商
            return resetBossOrSupplierPassword(request);
        } else {
            return BaseResponse.success("param error");
        }

    }

    private BaseResponse<String> resetBossOrSupplierPassword(ResetAccountPasswordRequest request) {
        Employee employee = employeeRepository.findByAccountNameAndDelFlagAndAccountType(request.getAccount(), DeleteFlag.NO
                , AccountType.fromValue(request.getAccountType())).orElse(null);
        if (employee == null) {
            throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_ACCOUNT_EXISTS_FOR_RESET);
        }
        String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(employee.getEmployeeId()), request
                .getPassword(), employee.getEmployeeSaltVal());
        employee.setAccountPassword(encryptPwd);
        employeeRepository.save(employee);
        return BaseResponse.success(JSON.toJSONString(employee));
    }

    private BaseResponse<String> resetCustomerPassword(ResetAccountPasswordRequest request) {
        Customer customer = customerRepository.findByCustomerAccountAndDelFlag(request.getAccount(), DeleteFlag.NO);
        if (customer == null) {
            throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_ACCOUNT_EXISTS_FOR_RESET);
        }
        String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(customer.getCustomerId()), request
                .getPassword(), customer.getCustomerSaltVal()); //生成加密后的登录密码
        customer.setCustomerPassword(encryptPwd);
        customerRepository.save(customer);
        return BaseResponse.success(JSON.toJSONString(customer));
    }
}
