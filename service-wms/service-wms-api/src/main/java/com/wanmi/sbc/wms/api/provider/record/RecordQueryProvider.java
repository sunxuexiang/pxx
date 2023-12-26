package com.wanmi.sbc.wms.api.provider.record;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wms.api.request.record.RecordPageRequest;
import com.wanmi.sbc.wms.api.response.record.RecordPageResponse;
import com.wanmi.sbc.wms.api.request.record.RecordListRequest;
import com.wanmi.sbc.wms.api.response.record.RecordListResponse;
import com.wanmi.sbc.wms.api.request.record.RecordByIdRequest;
import com.wanmi.sbc.wms.api.response.record.RecordByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>请求记录查询服务Provider</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@FeignClient(value = "${application.wms.name}", url="${feign.url.wms:#{null}}", contextId = "RecordQueryProvider.class" )
public interface RecordQueryProvider {

	/**
	 * 分页查询请求记录API
	 *
	 * @author baijz
	 * @param recordPageReq 分页请求参数和筛选对象 {@link RecordPageRequest}
	 * @return 请求记录分页列表信息 {@link RecordPageResponse}
	 */
	@PostMapping("/wms/${application.wms.version}/record/page")
	BaseResponse<RecordPageResponse> page(@RequestBody @Valid RecordPageRequest recordPageReq);

	/**
	 * 列表查询请求记录API
	 *
	 * @author baijz
	 * @param recordListReq 列表请求参数和筛选对象 {@link RecordListRequest}
	 * @return 请求记录的列表信息 {@link RecordListResponse}
	 */
	@PostMapping("/wms/${application.wms.version}/record/list")
	BaseResponse<RecordListResponse> list(@RequestBody @Valid RecordListRequest recordListReq);

	/**
	 * 单个查询请求记录API
	 *
	 * @author baijz
	 * @param recordByIdRequest 单个查询请求记录请求参数 {@link RecordByIdRequest}
	 * @return 请求记录详情 {@link RecordByIdResponse}
	 */
	@PostMapping("/wms/${application.wms.version}/record/get-by-id")
	BaseResponse<RecordByIdResponse> getById(@RequestBody @Valid RecordByIdRequest recordByIdRequest);

}

