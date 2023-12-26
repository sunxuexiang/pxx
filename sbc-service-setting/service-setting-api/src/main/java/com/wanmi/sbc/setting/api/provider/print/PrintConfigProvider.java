package com.wanmi.sbc.setting.api.provider.print;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.print.PrintConfigRequest;
import com.wanmi.sbc.setting.api.response.baseconfig.BaseConfigModifyResponse;
import com.wanmi.sbc.setting.api.response.print.PrintConfigResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>打印设置保存服务Provider</p>
 * @author lq
 * @date 2019-11-05 16:08:31
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "PrintConfigProvider")
public interface PrintConfigProvider {


	/**
	 * 修改基本设置API
	 *
	 * @author lq
	 * @param printConfigRequest 基本设置修改参数结构 {@link PrintConfigRequest}
	 * @return 修改的基本设置信息 {@link BaseConfigModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/printconfig/modify")
	BaseResponse<PrintConfigResponse> modify(@RequestBody @Valid PrintConfigRequest printConfigRequest);

	/**
	 * 查询打印配置
	 *
	 * @author lq
	 */
	@PostMapping("/setting/${application.setting.version}/printconfig/findone")
	BaseResponse<PrintConfigResponse> findOne();


}

