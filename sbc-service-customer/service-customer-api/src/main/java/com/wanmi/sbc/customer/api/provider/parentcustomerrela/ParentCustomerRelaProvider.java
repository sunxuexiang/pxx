package com.wanmi.sbc.customer.api.provider.parentcustomerrela;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaAddRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaAddResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaModifyRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaModifyResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaDelByIdRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>子主账号关联关系保存服务Provider</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "ParentCustomerRelaProvider")
public interface ParentCustomerRelaProvider {

	/**
	 * 新增子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaAddRequest 子主账号关联关系新增参数结构 {@link ParentCustomerRelaAddRequest}
	 * @return 新增的子主账号关联关系信息 {@link ParentCustomerRelaAddResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/add")
	BaseResponse<ParentCustomerRelaAddResponse> add(@RequestBody @Valid ParentCustomerRelaAddRequest parentCustomerRelaAddRequest);

	/**
	 * 修改子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaModifyRequest 子主账号关联关系修改参数结构 {@link ParentCustomerRelaModifyRequest}
	 * @return 修改的子主账号关联关系信息 {@link ParentCustomerRelaModifyResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/modify")
	BaseResponse<ParentCustomerRelaModifyResponse> modify(@RequestBody @Valid ParentCustomerRelaModifyRequest parentCustomerRelaModifyRequest);

	/**
	 * 单个删除子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaDelByIdRequest 单个删除参数结构 {@link ParentCustomerRelaDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid ParentCustomerRelaDelByIdRequest parentCustomerRelaDelByIdRequest);

	/**
	 * 批量删除子主账号关联关系API
	 *
	 * @author baijz
	 * @param parentCustomerRelaDelByIdListRequest 批量删除参数结构 {@link ParentCustomerRelaDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/customer/${application.customer.version}/parentcustomerrela/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid ParentCustomerRelaDelByIdListRequest parentCustomerRelaDelByIdListRequest);

}

