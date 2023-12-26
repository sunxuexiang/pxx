package com.wanmi.sbc.message.api.provider.smssend;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.message.api.request.smssend.SmsSendAddRequest;
import com.wanmi.sbc.message.api.response.smssend.SmsSendAddResponse;
import com.wanmi.sbc.message.api.request.smssend.SmsSendModifyRequest;
import com.wanmi.sbc.message.api.response.smssend.SmsSendModifyResponse;
import com.wanmi.sbc.message.api.request.smssend.SmsSendDelByIdRequest;
import com.wanmi.sbc.message.api.request.smssend.SmsSendDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>短信发送保存服务Provider</p>
 * @author zgl
 * @date 2019-12-03 15:36:05
 */
@FeignClient(value = "${application.message.name}", url="${feign.url.message:#{null}}",contextId = "SmsSendSaveProvider")
public interface SmsSendSaveProvider {

	/**
	 * 新增短信发送API
	 *
	 * @author zgl
	 * @param smsSendAddRequest 短信发送新增参数结构 {@link SmsSendAddRequest}
	 * @return 新增的短信发送信息 {@link SmsSendAddResponse}
	 */
	@PostMapping("/sms/${application.sms.version}/smssend/add")
	BaseResponse<SmsSendAddResponse> add(@RequestBody @Valid SmsSendAddRequest smsSendAddRequest);

	/**
	 * 修改短信发送API
	 *
	 * @author zgl
	 * @param smsSendModifyRequest 短信发送修改参数结构 {@link SmsSendModifyRequest}
	 * @return 修改的短信发送信息 {@link SmsSendModifyResponse}
	 */
	@PostMapping("/sms/${application.sms.version}/smssend/modify")
	BaseResponse<SmsSendModifyResponse> modify(@RequestBody @Valid SmsSendModifyRequest smsSendModifyRequest);

	/**
	 * 单个删除短信发送API
	 *
	 * @author zgl
	 * @param smsSendDelByIdRequest 单个删除参数结构 {@link SmsSendDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/sms/${application.sms.version}/smssend/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid SmsSendDelByIdRequest smsSendDelByIdRequest);

	/**
	 * 批量删除短信发送API
	 *
	 * @author zgl
	 * @param smsSendDelByIdListRequest 批量删除参数结构 {@link SmsSendDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/sms/${application.sms.version}/smssend/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid SmsSendDelByIdListRequest smsSendDelByIdListRequest);

}

