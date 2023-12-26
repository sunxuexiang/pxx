package com.wanmi.sbc.customer.provider.impl.workorderdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.workorderdetail.WorkOrderDetailProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.BindAccountRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaModifyRequest;
import com.wanmi.sbc.customer.api.request.workorderdetail.*;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderChildCustomerResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderCountrResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailAddResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailModifyResponse;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import com.wanmi.sbc.customer.workorderdetail.model.root.WorkOrderDetail;
import com.wanmi.sbc.customer.workorderdetail.service.WorkOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>工单明细保存服务接口实现</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@RestController
@Validated
public class WorkOrderDetailController implements WorkOrderDetailProvider {
	@Autowired
	private WorkOrderDetailService workOrderDetailService;

	@Override
	public BaseResponse<WorkOrderDetailAddResponse> add(@RequestBody @Valid WorkOrderDetailAddRequest workOrderDetailAddRequest) {
		WorkOrderDetail workOrderDetail = KsBeanUtil.convert(workOrderDetailAddRequest, WorkOrderDetail.class);
		return BaseResponse.success(new WorkOrderDetailAddResponse(
				workOrderDetailService.wrapperVo(workOrderDetailService.addAndUpdate(workOrderDetail))));
	}

	@Override
	public BaseResponse<WorkOrderDetailModifyResponse> modify(@RequestBody @Valid WorkOrderDetailModifyRequest workOrderDetailModifyRequest) {
		WorkOrderDetail workOrderDetail = KsBeanUtil.convert(workOrderDetailModifyRequest, WorkOrderDetail.class);
		return BaseResponse.success(new WorkOrderDetailModifyResponse(
				workOrderDetailService.wrapperVo(workOrderDetailService.modify(workOrderDetail))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid WorkOrderDetailDelByIdRequest workOrderDetailDelByIdRequest) {
		workOrderDetailService.deleteById(workOrderDetailDelByIdRequest.getWorkOrderDelId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid WorkOrderDetailDelByIdListRequest workOrderDetailDelByIdListRequest) {
		workOrderDetailService.deleteByIdList(workOrderDetailDelByIdListRequest.getWorkOrderDelIdList());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<WorkOrderChildCustomerResponse> mergeAccount(@RequestBody @Valid ParentCustomerRelaModifyRequest parentCustomerRelaModifyRequest) {
		ParentCustomerRela workOrderDetail = KsBeanUtil.convert(parentCustomerRelaModifyRequest, ParentCustomerRela.class);
		return BaseResponse.success(new WorkOrderChildCustomerResponse(workOrderDetailService
				.mergeAccount(workOrderDetail,parentCustomerRelaModifyRequest.getOperator(),parentCustomerRelaModifyRequest.getWorkOrderId())));
	}

	@Override
	public BaseResponse<WorkOrderChildCustomerResponse> bindAccount(@RequestBody @Valid BindAccountRequest request) {
		return BaseResponse.success(new WorkOrderChildCustomerResponse(workOrderDetailService
				.bindAccount(request.getCustomerIds(),request.getParentId())));
	}

	@Override
	public BaseResponse<WorkOrderCountrResponse> countGroupByWorOrderId(@Valid WorkOrderDetailCountByIdsRequest request) {
		return BaseResponse.success(new WorkOrderCountrResponse(workOrderDetailService.countGroupByWorOrderId(request.getWorOrderIds())));
	}
}

