package com.wanmi.sbc.contract;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.employeecontract.EmployeeContractProvider;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractResponese;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCateQueryProvider;
import com.wanmi.sbc.marketing.bean.vo.CouponCateVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: hulk
 * @Date: Created In 10:39 AM 2018/9/14
 * @Description:移动端合同接口
 */
@RestController
@RequestMapping("/contract-h5")
@Validated
@Api(tags = "ContractController", description = "移动端合同接口")
public class ContractController {

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private EmployeeContractProvider employeeContractProvider;

    @ApiOperation(value = "查询合同签署状态")
    @RequestMapping(value = "/contractStatus", method = RequestMethod.POST)
    public BaseResponse<EmployeeContractResponese> contractStatus() {
        String userId = commonUtil.getUserInfo().getUserId();
        BaseResponse<EmployeeContractResponese> byAppCustomerId = employeeContractProvider.findByAppCustomerId(userId);
        if (byAppCustomerId.getContext() == null) {
            EmployeeContractResponese employeeContractResponese = new EmployeeContractResponese();
            employeeContractResponese.setStatus(0);
            return BaseResponse.success(employeeContractResponese);
        }
        return byAppCustomerId;
    }

}
