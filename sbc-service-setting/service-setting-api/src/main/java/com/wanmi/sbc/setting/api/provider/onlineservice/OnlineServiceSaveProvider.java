package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceSwitchUpdateRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.SobotServiceRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.*;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceAddResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>onlineService保存服务Provider</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@FeignClient(value = "${application.setting.name}", contextId = "OnlineServiceSaveProvider")
public interface OnlineServiceSaveProvider {

	/**
	 * 修改的onlineService信息
	 *
	 * @author lq
	 * @param onlineServiceModifyRequest onlineService修改参数结构 {@link OnlineServiceModifyRequest}
	 * @return 修改的onlineService信息
	 */
	@PostMapping("/setting/${application.setting.version}/onlineservice/modify")
	BaseResponse modify(@RequestBody @Valid OnlineServiceModifyRequest
								onlineServiceModifyRequest);

	/**
	 * 修改的onlineService信息
	 *
	 * @author sgy
	 * @param imOnlineServiceModifyRequest onlineService修改参数结构 {@link ImOnlineServiceModifyRequest}
	 * @return 修改的onlineService信息
	 */
	@PostMapping("/setting/${application.setting.version}/onlineservice/im-modify")
	BaseResponse imModify(@RequestBody @Valid ImOnlineServiceModifyRequest
								  imOnlineServiceModifyRequest);


	/**
	 * 添加 腾讯 Im 账号 （主要是用户端）
	 *
	 * @author sgy
	 * @param vo onlineService修改参数结构 {@link ImOnlineServiceSignRequest}
	 * @return 添加账户信息
	 */
	@PostMapping("/setting/${application.setting.version}/onlineservice/im-addImAccount")
	BaseResponse<String> addImAccount(@RequestBody @Valid ImOnlineServiceSignRequest vo);


	/**
	 * 修改商家自定义客服类型开关
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/onlineservice/updateCustomerServiceSwitch")
	BaseResponse updateCustomerServiceSwitch(@RequestBody CustomerServiceSwitchUpdateRequest request);

	@PostMapping("/setting/${application.setting.version}/onlineservice/updateServiceStatus")
	BaseResponse updateServiceStatus(@RequestBody ImOnlineServiceItemVO request);

	@PostMapping("/setting/${application.setting.version}/onlineservice/initStoreIMCustomerService")
    BaseResponse initStoreIMCustomerService(@RequestBody OnlineServiceListRequest initRequest);
}

