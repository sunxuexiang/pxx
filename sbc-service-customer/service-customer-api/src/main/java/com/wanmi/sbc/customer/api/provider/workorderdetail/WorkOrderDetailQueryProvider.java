package com.wanmi.sbc.customer.api.provider.workorderdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailPageRequest;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailPageResponse;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailListRequest;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailListResponse;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailByIdRequest;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>工单明细查询服务Provider</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "WorkOrderDetailQueryProvider")
public interface WorkOrderDetailQueryProvider {

	/**
	 * 分页查询工单明细API
	 *
	 * @author baijz
	 * @param workOrderDetailPageReq 分页请求参数和筛选对象 {@link WorkOrderDetailPageRequest}
	 * @return 工单明细分页列表信息 {@link WorkOrderDetailPageResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/page")
	BaseResponse<WorkOrderDetailPageResponse> page(@RequestBody @Valid WorkOrderDetailPageRequest workOrderDetailPageReq);

	/**
	 * 列表查询工单明细API
	 *
	 * @author baijz
	 * @param workOrderDetailListReq 列表请求参数和筛选对象 {@link WorkOrderDetailListRequest}
	 * @return 工单明细的列表信息 {@link WorkOrderDetailListResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/list")
	BaseResponse<WorkOrderDetailListResponse> list(@RequestBody @Valid WorkOrderDetailListRequest workOrderDetailListReq);

	/**
	 * 单个查询工单明细API
	 *
	 * @author baijz
	 * @param workOrderDetailByIdRequest 单个查询工单明细请求参数 {@link WorkOrderDetailByIdRequest}
	 * @return 工单明细详情 {@link WorkOrderDetailByIdResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/get-by-id")
	BaseResponse<WorkOrderDetailListResponse> getById(@RequestBody @Valid WorkOrderDetailByIdRequest workOrderDetailByIdRequest);

}

