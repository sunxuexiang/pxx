package com.wanmi.sbc.customer.provider.impl.workorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.workorder.WorkOrderProvider;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderAddRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderDelByIdListRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderDelByIdRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderModifyRequest;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderAddResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderModifyResponse;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import com.wanmi.sbc.customer.workorder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>工单保存服务接口实现</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@RestController
@Validated
public class WorkOrderController implements WorkOrderProvider {
	@Autowired
	private WorkOrderService workOrderService;

	@Override
	public BaseResponse<WorkOrderAddResponse> add(@RequestBody @Valid WorkOrderAddRequest workOrderAddRequest) {
		WorkOrder workOrder = KsBeanUtil.convert(workOrderAddRequest, WorkOrder.class);
		return BaseResponse.success(new WorkOrderAddResponse(
				workOrderService.wrapperVo(workOrderService.add(workOrder))));
	}

	@Override
	public BaseResponse<WorkOrderModifyResponse> modify(@RequestBody @Valid WorkOrderModifyRequest workOrderModifyRequest) {
		WorkOrder workOrder = KsBeanUtil.convert(workOrderModifyRequest, WorkOrder.class);
		return BaseResponse.success(new WorkOrderModifyResponse(
				workOrderService.wrapperVo(workOrderService.modify(workOrder))));
	}


	@Override
	public BaseResponse deleteById(@RequestBody @Valid WorkOrderDelByIdRequest workOrderDelByIdRequest) {
		WorkOrder workOrder = KsBeanUtil.convert(workOrderDelByIdRequest, WorkOrder.class);
		workOrder.setDelFlag(DeleteFlag.YES);
		workOrderService.deleteById(workOrder);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid WorkOrderDelByIdListRequest workOrderDelByIdListRequest) {
		List<WorkOrder> workOrderList = workOrderDelByIdListRequest.getWorkOrderIdList().stream()
			.map(WorkOrderId -> {
				WorkOrder workOrder = KsBeanUtil.convert(WorkOrderId, WorkOrder.class);
				workOrder.setDelFlag(DeleteFlag.YES);
				return workOrder;
			}).collect(Collectors.toList());
		workOrderService.deleteByIdList(workOrderList);
		return BaseResponse.SUCCESSFUL();
	}

}

