package com.wanmi.sbc.customer.api.provider.parentcustomerrela;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaPageRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaPageResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaListResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaByIdRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>子主账号关联关系查询服务Provider</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "ParentCustomerRelaQueryProvider")
public interface ParentCustomerRelaQueryProvider {

	/**
	 * 分页查询子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaPageReq 分页请求参数和筛选对象 {@link ParentCustomerRelaPageRequest}
	 * @return 子主账号关联关系分页列表信息 {@link ParentCustomerRelaPageResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/page")
	BaseResponse<ParentCustomerRelaPageResponse> page(@RequestBody @Valid ParentCustomerRelaPageRequest parentCustomerRelaPageReq);

	/**
	 * 列表查询子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaListReq 列表请求参数和筛选对象 {@link ParentCustomerRelaListRequest}
	 * @return 子主账号关联关系的列表信息 {@link ParentCustomerRelaListResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/list")
	BaseResponse<ParentCustomerRelaListResponse> list(@RequestBody @Valid ParentCustomerRelaListRequest parentCustomerRelaListReq);

	/**
	 * 单个查询子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaByIdRequest 单个查询子主账号关联关系请求参数 {@link ParentCustomerRelaByIdRequest}
	 * @return 子主账号关联关系详情 {@link ParentCustomerRelaByIdResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/get-by-id")
	BaseResponse<ParentCustomerRelaByIdResponse> getById(@RequestBody @Valid ParentCustomerRelaByIdRequest parentCustomerRelaByIdRequest);


	/**
	 * 列表查询子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaListReq 列表请求参数和筛选对象 {@link ParentCustomerRelaListRequest}
	 * @return 子主账号关联关系的列表信息 {@link ParentCustomerRelaListResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/findAllByParentId")
	BaseResponse<ParentCustomerRelaListResponse> findAllByParentId(@RequestBody @Valid ParentCustomerRelaListRequest parentCustomerRelaListReq);

}

