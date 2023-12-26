package com.wanmi.sbc.workorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.workorder.WorkOrderQueryProvider;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderExistByRegisterCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderExistByRegisterCustomerIdResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(description = "工单管理API", tags = "WorkOrderController")
@RestController
@RequestMapping(value = "/workorder")
public class WorkOrderController {

    @Autowired
    private WorkOrderQueryProvider workOrderQueryProvider;

    /**
     * 校验是否存在未处理完的工单
     * @param request
     * @return
     */
    @ApiOperation(value = "校验是否存在未处理完的工单")
    @PostMapping("/validate/exist")
    public BaseResponse<WorkOrderExistByRegisterCustomerIdResponse> validateCustomerWorkOrder(@RequestBody @Valid
                                                            WorkOrderExistByRegisterCustomerIdRequest request) {
        return workOrderQueryProvider.validateCustomerWorkOrder(request);
    }

}
