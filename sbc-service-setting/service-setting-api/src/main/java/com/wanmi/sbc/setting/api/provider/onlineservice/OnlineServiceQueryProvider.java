package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceSwitchResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceListResponse;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceByIdRequest;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceByIdResponse;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>onlineService查询服务Provider</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "OnlineServiceQueryProvider")
public interface OnlineServiceQueryProvider {

	/**
	 * 座席列表查询onlineServiceAPI
	 *
	 * @author lq
	 * @param onlineServiceListReq 座席列表请求参数和筛选对象 {@link OnlineServiceListRequest}
	 * @return onlineService座席的列表信息 {@link OnlineServiceListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/onlineservice/list")
	BaseResponse<OnlineServiceListResponse> list(@RequestBody @Valid OnlineServiceListRequest onlineServiceListReq);

	/**
	 * 单个查询onlineServiceAPI
	 *
	 * @author lq
	 * @param onlineServiceByIdRequest 单个查询onlineService请求参数 {@link OnlineServiceByIdRequest}
	 * @return onlineService详情 {@link OnlineServiceByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/onlineservice/get-by-id")
	BaseResponse<OnlineServiceByIdResponse> getById(@RequestBody @Valid OnlineServiceByIdRequest
                                                            onlineServiceByIdRequest);


	/**
	 * （腾讯Im）座席列表查询onlineServiceAPI
	 *
	 * @author sgy
	 * @param onlineServiceListReq 座席列表请求参数和筛选对象 {@link OnlineServiceListRequest}
	 * @return onlineService座席的列表信息 {@link ImOnlineServiceListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/im-list")
	BaseResponse<ImOnlineServiceListResponse> imList(@RequestBody @Valid OnlineServiceListRequest onlineServiceListReq);


	/**
	 * 单个查询onlineServiceAPI
	 *
	 * @author lq
	 * @param onlineServiceByIdRequest 单个查询onlineService请求参数 {@link OnlineServiceByIdRequest}
	 * @return onlineService详情 {@link OnlineServiceByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/get-im-by-id")
	BaseResponse<ImOnlineServiceByIdResponse> getImById(@RequestBody @Valid OnlineServiceByIdRequest
															onlineServiceByIdRequest);
     /**
       * (腾讯Im）平台提供用户签名
       * */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/platform_sign")
    BaseResponse<String> platformSign(@RequestBody ImOnlineServiceSignRequest imOnlineServiceSignRequest);

	/**
	 * 根据公司ID，获取一个商家客服账号
	 * 	APP端发起客服聊天时，在公司多IM账号中，使用轮询策略分配一个客服给APP段提供IM聊天功能
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/get-company-online-account")
	BaseResponse<ImOnlineServiceItemVO> getCompanyOnlineAccount (@RequestBody @Valid ImOnlineServiceSignRequest signRequest);

	/**
	 * 根据公司ID，获取一个商家客服账号
	 * 	APP端发起客服聊天时，在公司多IM账号中，使用轮询策略分配一个客服给APP段提供IM聊天功能
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/get-company-service-account")
	BaseResponse<ImOnlineServiceListResponse> getCompanyServiceAccount(@RequestBody @Valid OnlineServiceListRequest onlineServiceListReq);
	/**
	 *
	 * 平台端获取im 账号信息
	 * @author sgy
	 * @param ropRequest 座席列表请求参数和筛选对象 {@link OnlineServiceListRequest}
	 * @return onlineService座席的列表信息 {@link ImOnlineServiceListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/platform_im-list")
	BaseResponse<ImOnlineServiceListResponse> platformImList(@RequestBody @Valid ImOnlineServiceModifyRequest ropRequest);
	/**
	 *
	 * app 获取当前聊天id
	 * @author sgy
	 * @param request 座席列表请求参数和筛选对象 {@link ImOnlineServiceModifyRequest}
	 * @return 账户信息 {@link String}
	 */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/platform_user_id")
    BaseResponse<String> platformUserId(@RequestBody @Valid ImOnlineServiceModifyRequest request);

	/**
	 * 查询商家客服类型开关
	 * @author zhouzhenguo
	 */
	@PostMapping("/setting/${application.setting.version}/imOnlineService/getCustomerServiceSwitch")
    BaseResponse<CustomerServiceSwitchResponse> getCustomerServiceSwitch(@RequestBody ImOnlineServiceSignRequest request);

	@PostMapping("/setting/${application.setting.version}/imOnlineService/getCustomerServiceAccountByMobile")
	BaseResponse getCustomerServiceAccountByMobile(@RequestBody String mobileNumber);

	@PostMapping("/setting/${application.setting.version}/imOnlineService/getHaveCustomerServiceStoreIds")
    BaseResponse<List<Long>> getHaveCustomerServiceStoreIds();

	@PostMapping("/setting/${application.setting.version}/imOnlineService/getOnlineImAccount1")
    BaseResponse getOnlineImAccount(@RequestBody ImOnlineServiceSignRequest request);

	@PostMapping("/setting/${application.setting.version}/imOnlineService/switchStoreIMAccount")
    BaseResponse switchStoreIMAccount(@RequestBody OnlineServiceListRequest request);

	@PostMapping("/setting/${application.setting.version}/imOnlineService/getAllCustomerServiceCompanyIds")
	BaseResponse<List<Long>> getAllCustomerServiceCompanyIds();

	@PostMapping("/setting/${application.setting.version}/imOnlineService/getImAccountListByCompanyId")
	BaseResponse<List<ImOnlineServiceItemVO>> getImAccountListByCompanyId(@RequestBody OnlineServiceListRequest onlineServiceListRequest);
}

