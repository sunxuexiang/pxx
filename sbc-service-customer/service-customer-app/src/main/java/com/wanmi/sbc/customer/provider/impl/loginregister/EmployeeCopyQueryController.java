package com.wanmi.sbc.customer.provider.impl.loginregister;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.loginregister.EmployeeCopyQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeCopyQueryRequest;
import com.wanmi.sbc.customer.api.request.loginregister.EmployeeCopyLoginRequest;
import com.wanmi.sbc.customer.api.request.loginregister.EmployeeCopyResetRequest;
import com.wanmi.sbc.customer.api.response.loginregister.EmployeeCopyLoginResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeCopyVo;
import com.wanmi.sbc.customer.employee.model.root.EmployeeCopy;
import com.wanmi.sbc.customer.employee.service.EmployeeCopyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @author lm
 * @date 2022/09/15 8:28
 */
@RestController
@Slf4j
@Validated
public class EmployeeCopyQueryController implements EmployeeCopyQueryProvider {

    private EmployeeCopyService employeeCopyService;

    @Autowired
    public void setEmployeeCopyService(EmployeeCopyService employeeCopyService) {
        this.employeeCopyService = employeeCopyService;
    }

    @Override
    public BaseResponse<EmployeeCopyLoginResponse> login(@RequestBody @Valid EmployeeCopyLoginRequest request) {
        EmployeeCopy employeeCopy = employeeCopyService.login(request.getEmployeeAccount(),request.getPassword());
        if(Objects.isNull(employeeCopy)){
            return BaseResponse.SUCCESSFUL();
        }
        EmployeeCopyLoginResponse convert = KsBeanUtil.convert(employeeCopy, EmployeeCopyLoginResponse.class);
        return BaseResponse.success(convert);
    }

    @Override
    public BaseResponse<List<EmployeeCopyVo>> queryEmployeeCopyByProvinceCode(EmployeeCopyQueryRequest queryRequest) {
        List<EmployeeCopyVo> employeeCopyVoList = employeeCopyService.queryEmployeeCopyByProvinceCode(queryRequest.getProvinceCode());
        return BaseResponse.success(employeeCopyVoList);
    }

    @Override
    public BaseResponse<EmployeeCopyVo> queryByPhone(String phone) {
        EmployeeCopyVo vo =  employeeCopyService.queryByPhone(phone);
        return BaseResponse.success(vo);
    }

    @Override
    public BaseResponse resetPassword(@RequestBody @Valid EmployeeCopyResetRequest resetRequest) {
        employeeCopyService.resetPassword(resetRequest.getEmployeeId(), resetRequest.getPassword());
        return BaseResponse.SUCCESSFUL();
    }
}
