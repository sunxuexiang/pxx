package com.wanmi.sbc.customer.api.provider.workorderdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.BindAccountRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaModifyRequest;
import com.wanmi.sbc.customer.api.request.workorderdetail.*;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderChildCustomerResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderCountrResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailAddResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>工单明细保存服务Provider</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "WorkOrderDetailProvider")
public interface WorkOrderDetailProvider {

	/**
	 * 新增工单明细API
	 *
	 * @author baijz
	 * @param workOrderDetailAddRequest 工单明细新增参数结构 {@link WorkOrderDetailAddRequest}`
	 * @return 新增的工单明细信息 {@link WorkOrderDetailAddResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/add")
	BaseResponse<WorkOrderDetailAddResponse> add(@RequestBody @Valid WorkOrderDetailAddRequest workOrderDetailAddRequest);

	/**
	 * 修改工单明细API
	 *
	 * @author baijz
	 * @param workOrderDetailModifyRequest 工单明细修改参数结构 {@link WorkOrderDetailModifyRequest}
	 * @return 修改的工单明细信息 {@link WorkOrderDetailModifyResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/modify")
	BaseResponse<WorkOrderDetailModifyResponse> modify(@RequestBody @Valid WorkOrderDetailModifyRequest workOrderDetailModifyRequest);

	/**
	 * 单个删除工单明细API
	 *
	 * @author baijz
	 * @param workOrderDetailDelByIdRequest 单个删除参数结构 {@link WorkOrderDetailDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid WorkOrderDetailDelByIdRequest workOrderDetailDelByIdRequest);

	/**
	 * 批量删除工单明细API
	 *
	 * @author baijz
	 * @param workOrderDetailDelByIdListRequest 批量删除参数结构 {@link WorkOrderDetailDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid WorkOrderDetailDelByIdListRequest workOrderDetailDelByIdListRequest);


	/**
	 * 账号合并
	 *
	 * @author
	 * @param parentCustomerRelaModifyRequest 批量删除参数结构 {@link ParentCustomerRelaModifyRequest}
	 * @return  {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/mergeAccount")
	BaseResponse<WorkOrderChildCustomerResponse> mergeAccount(@RequestBody @Valid ParentCustomerRelaModifyRequest parentCustomerRelaModifyRequest);

	/**
	 * 绑定主账号
	 *
	 * @author
	 * @param parentCustomerRelaModifyRequest 批量删除参数结构 {@link ParentCustomerRelaModifyRequest}
	 * @return  {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/bindAccount")
	BaseResponse<WorkOrderChildCustomerResponse> bindAccount(@RequestBody @Valid BindAccountRequest parentCustomerRelaModifyRequest);


	/**
	 * 计数处理工单数
	 *
	 * @author
	 * @param request 计数处理工单数 {@link WorkOrderDetailCountByIdsRequest}
	 * @return  {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/workorderdetail/countGroupByWorOrderId")
	BaseResponse<WorkOrderCountrResponse> countGroupByWorOrderId(@RequestBody @Valid WorkOrderDetailCountByIdsRequest request);

}

