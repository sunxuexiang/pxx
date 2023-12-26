package com.wanmi.sbc.customer.provider.impl.workorderdetail;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.workorderdetail.WorkOrderDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailPageRequest;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailQueryRequest;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailPageResponse;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailListRequest;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailListResponse;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailByIdRequest;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailByIdResponse;
import com.wanmi.sbc.customer.bean.vo.WorkOrderDetailVO;
import com.wanmi.sbc.customer.workorderdetail.service.WorkOrderDetailService;
import com.wanmi.sbc.customer.workorderdetail.model.root.WorkOrderDetail;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>工单明细查询服务接口实现</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@RestController
@Validated
public class WorkOrderDetailQueryController implements WorkOrderDetailQueryProvider {
	@Autowired
	private WorkOrderDetailService workOrderDetailService;

	@Override
	public BaseResponse<WorkOrderDetailPageResponse> page(@RequestBody @Valid WorkOrderDetailPageRequest workOrderDetailPageReq) {
		WorkOrderDetailQueryRequest queryReq = KsBeanUtil.convert(workOrderDetailPageReq, WorkOrderDetailQueryRequest.class);
		Page<WorkOrderDetail> workOrderDetailPage = workOrderDetailService.page(queryReq);
		Page<WorkOrderDetailVO> newPage = workOrderDetailPage.map(entity -> workOrderDetailService.wrapperVo(entity));
		MicroServicePage<WorkOrderDetailVO> microPage = new MicroServicePage<>(newPage, workOrderDetailPageReq.getPageable());
		WorkOrderDetailPageResponse finalRes = new WorkOrderDetailPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<WorkOrderDetailListResponse> list(@RequestBody @Valid WorkOrderDetailListRequest workOrderDetailListReq) {
		WorkOrderDetailQueryRequest queryReq = KsBeanUtil.convert(workOrderDetailListReq, WorkOrderDetailQueryRequest.class);
		List<WorkOrderDetail> workOrderDetailList = workOrderDetailService.list(queryReq);
		List<WorkOrderDetailVO> newList = workOrderDetailList.stream().map(entity -> workOrderDetailService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new WorkOrderDetailListResponse(newList));
	}

	@Override
	public BaseResponse<WorkOrderDetailListResponse> getById(@RequestBody @Valid WorkOrderDetailByIdRequest workOrderDetailByIdRequest) {
		List<WorkOrderDetail> workOrderDetail =
		workOrderDetailService.getListById(workOrderDetailByIdRequest.getWorkOrderDelId());
		List<WorkOrderDetailVO> newList = workOrderDetail.stream().map(entity -> workOrderDetailService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new WorkOrderDetailListResponse(newList));
	}

}

