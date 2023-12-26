package com.wanmi.sbc.customer.api.provider.workorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaAddRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderByIdRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderExistByRegisterCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderListRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderPageRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.CustomerMergeFlagResponse;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRealStatusResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderByIdResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderExistByRegisterCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderListResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>工单查询服务Provider</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "WorkOrderQueryProvider")
public interface WorkOrderQueryProvider {

	/**
	 * 分页查询工单API
	 *
	 * @author baijz
	 * @param workOrderPageReq 分页请求参数和筛选对象 {@link WorkOrderPageRequest}
	 * @return 工单分页列表信息 {@link WorkOrderPageResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/page")
	BaseResponse<WorkOrderPageResponse> page(@RequestBody @Valid WorkOrderPageRequest workOrderPageReq);

	/**
	 * 列表查询工单API
	 *
	 * @author baijz
	 * @param workOrderListReq 列表请求参数和筛选对象 {@link WorkOrderListRequest}
	 * @return 工单的列表信息 {@link WorkOrderListResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/list")
	BaseResponse<WorkOrderListResponse> list(@RequestBody @Valid WorkOrderListRequest workOrderListReq);

	/**
	 * 单个查询工单API
	 *
	 * @author baijz
	 * @param workOrderByIdRequest 单个查询工单请求参数 {@link WorkOrderByIdRequest}
	 * @return 工单详情 {@link WorkOrderByIdResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/get-by-id")
	BaseResponse<WorkOrderByIdResponse> getById(@RequestBody @Valid WorkOrderByIdRequest workOrderByIdRequest);


	@PostMapping("/customer/${application.customer.version}/workorder/getStatus")
	BaseResponse<ParentCustomerRealStatusResponse> getStatus(@RequestBody @Valid ParentCustomerRelaAddRequest parentCustomerRelaByIdRequest);

	@PostMapping("/customer/${application.customer.version}/workorder/checkMegerFlag")
	BaseResponse<CustomerMergeFlagResponse> checkMegerFlag(@RequestBody @Valid ParentCustomerRelaAddRequest parentCustomerRelaByIdRequest);


	/**
	 * 根据已注册人的Id查询是否存在未处理的工单
	 * @param request
	 * @return
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/validate-customer-work-order")
	BaseResponse<WorkOrderExistByRegisterCustomerIdResponse> validateCustomerWorkOrder(@RequestBody @Valid WorkOrderExistByRegisterCustomerIdRequest request);
}

