package com.wanmi.sbc.customer.api.provider.loginregister;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.employee.EmployeeCopyQueryRequest;
import com.wanmi.sbc.customer.api.request.loginregister.EmployeeCopyLoginRequest;
import com.wanmi.sbc.customer.api.request.loginregister.EmployeeCopyResetRequest;
import com.wanmi.sbc.customer.api.response.loginregister.EmployeeCopyLoginResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeCopyVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据看板-员工登录-查询API
 * @author lm
 * @date 2022/09/15 8:26
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "EmployeeCopyQueryProvider")
public interface EmployeeCopyQueryProvider {


    @PostMapping("/employee/${application.customer.version}/login")
    BaseResponse<EmployeeCopyLoginResponse> login(@RequestBody @Valid EmployeeCopyLoginRequest request);

    @PostMapping("/employee/${application.customer.version}/queryEmployeeCopyByProvinceCode")
    BaseResponse<List<EmployeeCopyVo>> queryEmployeeCopyByProvinceCode(@RequestBody EmployeeCopyQueryRequest queryRequest);

    @PostMapping("/employee/${application.customer.version}/queryByPone")
    BaseResponse<EmployeeCopyVo> queryByPhone(@RequestParam("phone") String phone);
    @PostMapping("/employee/${application.customer.version}/resetPwd")
    BaseResponse resetPassword(@RequestBody @Valid EmployeeCopyResetRequest resetRequest);
}
