package com.wanmi.sbc.wms.api.provider.record;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wms.api.request.record.RecordAddRequest;
import com.wanmi.sbc.wms.api.response.record.RecordAddResponse;
import com.wanmi.sbc.wms.api.request.record.RecordModifyRequest;
import com.wanmi.sbc.wms.api.response.record.RecordModifyResponse;
import com.wanmi.sbc.wms.api.request.record.RecordDelByIdRequest;
import com.wanmi.sbc.wms.api.request.record.RecordDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>请求记录保存服务Provider</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@FeignClient(value = "${application.wms.name}", url="${feign.url.wms:#{null}}", contextId = "RecordSaveProvider.class" )
public interface RecordSaveProvider {

	/**
	 * 新增请求记录API
	 *
	 * @author baijz
	 * @param recordAddRequest 请求记录新增参数结构 {@link RecordAddRequest}
	 * @return 新增的请求记录信息 {@link RecordAddResponse}
	 */
	@PostMapping("/wms/${application.wms.version}/record/add")
	BaseResponse<RecordAddResponse> add(@RequestBody @Valid RecordAddRequest recordAddRequest);

	/**
	 * 修改请求记录API
	 *
	 * @author baijz
	 * @param recordModifyRequest 请求记录修改参数结构 {@link RecordModifyRequest}
	 * @return 修改的请求记录信息 {@link RecordModifyResponse}
	 */
	@PostMapping("/wms/${application.wms.version}/record/modify")
	BaseResponse<RecordModifyResponse> modify(@RequestBody @Valid RecordModifyRequest recordModifyRequest);

	/**
	 * 单个删除请求记录API
	 *
	 * @author baijz
	 * @param recordDelByIdRequest 单个删除参数结构 {@link RecordDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/wms/${application.wms.version}/record/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid RecordDelByIdRequest recordDelByIdRequest);

	/**
	 * 批量删除请求记录API
	 *
	 * @author baijz
	 * @param recordDelByIdListRequest 批量删除参数结构 {@link RecordDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/wms/${application.wms.version}/record/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid RecordDelByIdListRequest recordDelByIdListRequest);

}

