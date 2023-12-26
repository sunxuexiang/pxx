package com.wanmi.sbc.customer.provider.impl.workorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.workorder.WorkOrderQueryProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaAddRequest;
import com.wanmi.sbc.customer.api.request.workorder.*;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.CustomerMergeFlagResponse;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRealStatusResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderByIdResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderExistByRegisterCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderListResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderPageResponse;
import com.wanmi.sbc.customer.bean.enums.MergeAccountBeforeStatusVo;
import com.wanmi.sbc.customer.bean.vo.WorkOrderVO;
import com.wanmi.sbc.customer.enums.MergeAccountBeforeStatus;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import com.wanmi.sbc.customer.workorder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>工单查询服务接口实现</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@RestController
@Validated
public class WorkOrderQueryController implements WorkOrderQueryProvider {
	@Autowired
	private WorkOrderService workOrderService;

	@Override
	public BaseResponse<WorkOrderPageResponse> page(@RequestBody @Valid WorkOrderPageRequest workOrderPageReq) {
		WorkOrderQueryRequest queryReq = KsBeanUtil.convert(workOrderPageReq, WorkOrderQueryRequest.class);
		Page<WorkOrder> workOrderPage = workOrderService.page(queryReq);
		Page<WorkOrderVO> newPage = workOrderPage.map(entity -> workOrderService.wrapperVo(entity));
		MicroServicePage<WorkOrderVO> microPage = new MicroServicePage<>(newPage, workOrderPageReq.getPageable());
		WorkOrderPageResponse finalRes = new WorkOrderPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<WorkOrderListResponse> list(@RequestBody @Valid WorkOrderListRequest workOrderListReq) {
		WorkOrderQueryRequest queryReq = KsBeanUtil.convert(workOrderListReq, WorkOrderQueryRequest.class);
		List<WorkOrder> workOrderList = workOrderService.list(queryReq);
		List<WorkOrderVO> newList = workOrderList.stream().map(entity -> workOrderService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new WorkOrderListResponse(newList));
	}

	@Override
	public BaseResponse<WorkOrderByIdResponse> getById(@RequestBody @Valid WorkOrderByIdRequest workOrderByIdRequest) {
		return BaseResponse.success(new WorkOrderByIdResponse(workOrderService.queryWorkOrder(workOrderByIdRequest.getWorkOrderId())));
	}

	@Override
	public BaseResponse<ParentCustomerRealStatusResponse> getStatus(@RequestBody @Valid ParentCustomerRelaAddRequest parentCustomerRelaByIdRequest) {
		MergeAccountBeforeStatus status = workOrderService.getStatus(parentCustomerRelaByIdRequest.getCustomerId());
		MergeAccountBeforeStatusVo convert = KsBeanUtil.convert(status, MergeAccountBeforeStatusVo.class);
		return BaseResponse.success(new ParentCustomerRealStatusResponse(convert));
	}

	@Override
	public BaseResponse<CustomerMergeFlagResponse> checkMegerFlag(@RequestBody @Valid ParentCustomerRelaAddRequest parentCustomerRelaByIdRequest) {
		return BaseResponse.success(new CustomerMergeFlagResponse(workOrderService.checkWorkOrderBy(parentCustomerRelaByIdRequest.getCustomerId())));
	}

	@Override
	public BaseResponse<WorkOrderExistByRegisterCustomerIdResponse> validateCustomerWorkOrder(@RequestBody @Valid WorkOrderExistByRegisterCustomerIdRequest request) {
		boolean existFlag =  workOrderService.validateCustomerWorkOrder(request.getCustomerId());
		return BaseResponse.success(WorkOrderExistByRegisterCustomerIdResponse.builder().existFlag(existFlag).build());
	}
}

