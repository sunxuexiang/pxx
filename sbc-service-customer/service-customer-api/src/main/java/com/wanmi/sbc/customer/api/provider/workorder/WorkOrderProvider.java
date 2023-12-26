package com.wanmi.sbc.customer.api.provider.workorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderAddRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderDelByIdListRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderDelByIdRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderModifyRequest;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderAddResponse;
import com.wanmi.sbc.customer.api.response.workorder.WorkOrderModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>工单保存服务Provider</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "WorkOrderProvider")
public interface WorkOrderProvider {

	/**
	 * 新增工单API
	 *
	 * @author baijz
	 * @param workOrderAddRequest 工单新增参数结构 {@link WorkOrderAddRequest}
	 * @return 新增的工单信息 {@link WorkOrderAddResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/add")
	BaseResponse<WorkOrderAddResponse> add(@RequestBody @Valid WorkOrderAddRequest workOrderAddRequest);

	/**
	 * 修改工单API
	 *
	 * @author baijz
	 * @param workOrderModifyRequest 工单修改参数结构 {@link WorkOrderModifyRequest}
	 * @return 修改的工单信息 {@link WorkOrderModifyResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/modify")
	BaseResponse<WorkOrderModifyResponse> modify(@RequestBody @Valid WorkOrderModifyRequest workOrderModifyRequest);

	/**
	 * 单个删除工单API
	 *
	 * @author baijz
	 * @param workOrderDelByIdRequest 单个删除参数结构 {@link WorkOrderDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid WorkOrderDelByIdRequest workOrderDelByIdRequest);

	/**
	 * 批量删除工单API
	 *
	 * @author baijz
	 * @param workOrderDelByIdListRequest 批量删除参数结构 {@link WorkOrderDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorder/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid WorkOrderDelByIdListRequest workOrderDelByIdListRequest);


}

