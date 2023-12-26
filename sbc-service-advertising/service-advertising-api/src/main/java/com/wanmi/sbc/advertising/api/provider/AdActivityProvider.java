package com.wanmi.sbc.advertising.api.provider;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wanmi.sbc.advertising.api.request.activity.ActAuditRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActGetByIdRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActPayCallBackRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActPayRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryListPageRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActSaveRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActUpdateRequest;
import com.wanmi.sbc.advertising.api.request.activity.AdRefundCallbackRequest;
import com.wanmi.sbc.advertising.api.response.QueryActiveActResp;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;

/**
 * @author zc
 *
 */
@FeignClient(value = "${application.advertising.name}", url = "${feign.url.advertising:#{null}}", contextId = "AdActivityProvider")
public interface AdActivityProvider {


	/**
	 * 分页查询广告活动列表
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/queryListPage")
	BaseResponse<MicroServicePage<AdActivityDTO>> queryListPage(@RequestBody ActQueryListPageRequest request);

	/**
	 * 保存广告活动
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/save")
	BaseResponse<String> save(@RequestBody @Valid ActSaveRequest request);

	/**
	 * 付款后回调
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/payCallBack")
	BaseResponse payCallBack(@RequestBody @Valid ActPayCallBackRequest request);

	/**
	 * 查询单个广告活动
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/queryOne")
	BaseResponse<AdActivityDTO> queryOne(@RequestBody @Valid ActGetByIdRequest request);

	/**
	 * 支付，鲸币支付返回鲸币付款单号，在线支付返回建行收款二维码
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/pay")
	BaseResponse<String> pay(@RequestBody @Valid ActPayRequest request);

	/**
	 * 审核广告活动
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/audit")
	BaseResponse audit(@RequestBody @Valid ActAuditRequest request);

	/**
	 * 退款
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/refund")
	BaseResponse refund(@RequestBody @Valid ActGetByIdRequest request);

	/**
	 * 下架
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/takeOff")
	BaseResponse takeOff(@RequestBody @Valid ActGetByIdRequest request);

	/**
	 * 查询运行中的广告
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/queryActiveAct")
	BaseResponse<QueryActiveActResp> queryActiveAct(@RequestBody @Valid ActQueryActiveActRequest req);
	
	/**
	 * 查询运行中的商城商品广告
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/queryMallGoodsActiveAct")
	BaseResponse<QueryActiveActResp> queryMallGoodsActiveAct(@RequestBody @Valid ActQueryActiveActRequest req);

	/**
	 * 定时任务批量完成
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/batchComplete")
	BaseResponse batchComplete();

	/**
	 * 退款中，分账后结果回调
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/adRefundCallback")
	BaseResponse adRefundCallback(@RequestBody @Valid AdRefundCallbackRequest request);

	/**
	 * 定时任务批量开始
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/batchStart")
	BaseResponse batchStart();

	/**
	 * 修改投放信息
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adActivity/update")
	BaseResponse update(@RequestBody @Valid ActUpdateRequest req);
}
