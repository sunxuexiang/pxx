package com.wanmi.sbc.customer.provider.impl.employee;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeBaseProvider;
import com.wanmi.sbc.customer.api.request.employee.BaseConfigSettingRopResponse;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByAccountNameAndTypeRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeUnlockByIdRequest;
import com.wanmi.sbc.customer.api.response.base.BaseUtilResponse;
import com.wanmi.sbc.customer.api.response.employee.*;
import com.wanmi.sbc.customer.api.vo.CompanyBaseInfoVO;
import com.wanmi.sbc.customer.api.vo.EmployeeBaseVO;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.service.EmployeeService;
import com.wanmi.sbc.setting.api.response.baseconfig.BaseConfigRopResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@RestController
@Validated
public class EmployeeBaseController implements EmployeeBaseProvider {

    @Autowired
    private EmployeeService employeeService;
    @Override
    public BaseUtilResponse<EmployeeByAccountNameAndTypeResponse> getByAccountName(@RequestBody @Valid EmployeeByAccountNameAndTypeRequest request) {

        Employee employee = employeeService.findByAccountName(request.getAccountName(), AccountType.fromValue(request.getAccountType())).orElse(null);

        EmployeeByAccountNameAndTypeResponse response = new EmployeeByAccountNameAndTypeResponse();
        EmployeeBaseVO employeeVO = null;
        if(!Objects.isNull(employee)) {
            employeeVO = new EmployeeBaseVO();
            KsBeanUtil.copyPropertiesThird(employee, employeeVO);
            employeeVO.setAccountState(employee.getAccountState() != null?employee.getAccountState().toValue():null);
            employeeVO.setAccountType(employee.getAccountType() != null?employee.getAccountType().toValue():null);
            employeeVO.setDelFlag(employee.getDelFlag() != null?employee.getDelFlag().toValue():null);
            if (employee.getCompanyInfo() != null) {
                CompanyBaseInfoVO vo = new CompanyBaseInfoVO();
                CompanyInfo companyInfo = employee.getCompanyInfo();
                KsBeanUtil.copyPropertiesThird(employee.getCompanyInfo(), vo);
                vo.setCompanyType(Objects.isNull(companyInfo.getCompanyType())?null:companyInfo.getCompanyType().toValue());
                vo.setStoreType(Objects.isNull(companyInfo.getStoreType())?null:companyInfo.getStoreType().toValue());
                vo.setDelFlag(Objects.isNull(companyInfo.getDelFlag())?null:companyInfo.getDelFlag().toValue());
                employeeVO.setCompanyInfo(vo);
            }
        }
        response.setEmployee(employeeVO);
        return BaseUtilResponse.success(response);
    }


    @Override
    public BaseUtilResponse<EmployeeUnlockResponse> unlockByEmpId(@RequestBody @Valid EmployeeUnlockByIdRequest employeeUnlockByIdRequest){
        EmployeeUnlockResponse response = new EmployeeUnlockResponse();

        int count = employeeService.unlockEmpoyee(employeeUnlockByIdRequest.getEmployeeId());
        response.setCount(count);

        return BaseUtilResponse.success(response);
    }


    @Override
    public BaseUtilResponse<EmployeeLoginTimeModifyResponse> modifyLoginTimeByEmpId(@RequestBody @Valid String employeeId){
        EmployeeLoginTimeModifyResponse response = new EmployeeLoginTimeModifyResponse();

        int count = employeeService.updateLoginTime(employeeId);
        response.setCount(count);

        return BaseUtilResponse.success(response);
    }


    @Override
    public BaseUtilResponse<EmployeeLoginErrorCountModifyByIdResponse> modifyLoginErrorCountByEmpId(@RequestBody @Valid String employeeId){
        EmployeeLoginErrorCountModifyByIdResponse response = new EmployeeLoginErrorCountModifyByIdResponse();

        int count = employeeService.updateLoginErrorCount(employeeId);
        response.setCount(count);

        return BaseUtilResponse.success(response);
    }

    @Override

    public BaseUtilResponse<EmployeeLoginLockTimeModifyByIdResponse> modifyLoginLockTimeByEmpId(@RequestBody @Valid String employeeId){
        EmployeeLoginLockTimeModifyByIdResponse response = new EmployeeLoginLockTimeModifyByIdResponse();

        int count = employeeService.updateLoginLockTime(employeeId);
        response.setCount(count);

        return BaseUtilResponse.success(response);
    }

    @Override
    public BaseUtilResponse<BaseConfigSettingRopResponse> getBaseConfig(){
        BaseResponse<BaseConfigRopResponse> response = employeeService.getBaseConfig();
        BaseConfigSettingRopResponse context = KsBeanUtil.copyPropertiesThird(response.getContext(), BaseConfigSettingRopResponse.class);
        context.setIconFlag(response.getContext().getIconFlag().toValue());
        return BaseUtilResponse.success(context);
    }
}
