package com.wanmi.sbc.returnorder.api.provider.pickuprecord;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.pickuprecord.PickUpRecordByIdRequest;
import com.wanmi.sbc.returnorder.api.request.pickuprecord.PickUpRecordByTradeIdRequest;
import com.wanmi.sbc.returnorder.api.request.pickuprecord.PickUpRecordListRequest;
import com.wanmi.sbc.returnorder.api.request.pickuprecord.PickUpRecordPageRequest;
import com.wanmi.sbc.returnorder.api.response.pickuprecord.PickUpRecordByIdResponse;
import com.wanmi.sbc.returnorder.api.response.pickuprecord.PickUpRecordListResponse;
import com.wanmi.sbc.returnorder.api.response.pickuprecord.PickUpRecordPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>自提查询服务Provider</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnPickUpRecordQueryProvider")
public interface PickUpRecordQueryProvider {

	/**
	 * 分页查询自提API
	 *
	 * @author lh
	 * @param pickUpRecordPageReq 分页请求参数和筛选对象 {@link PickUpRecordPageRequest}
	 * @return 自提分页列表信息 {@link PickUpRecordPageResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/pickuprecord/page")
	BaseResponse<PickUpRecordPageResponse> page(@RequestBody @Valid PickUpRecordPageRequest pickUpRecordPageReq);

	/**
	 * 列表查询自提API
	 *
	 * @author lh
	 * @param pickUpRecordListReq 列表请求参数和筛选对象 {@link PickUpRecordListRequest}
	 * @return 自提的列表信息 {@link PickUpRecordListResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/pickuprecord/list")
	BaseResponse<PickUpRecordListResponse> list(@RequestBody @Valid PickUpRecordListRequest pickUpRecordListReq);

	/**
	 * 单个查询自提API
	 *
	 * @author lh
	 * @param pickUpRecordByIdRequest 单个查询自提请求参数 {@link PickUpRecordByIdRequest}
	 * @return 自提详情 {@link PickUpRecordByIdResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/pickuprecord/get-by-id")
	BaseResponse<PickUpRecordByIdResponse> getById(@RequestBody @Valid PickUpRecordByIdRequest pickUpRecordByIdRequest);

	/**
	 * 单个订单id查询自提API
	 *
	 * @author lh
	 * @param pickUpRecordByIdRequest 单个查询自提请求参数 {@link PickUpRecordByTradeIdRequest}
	 * @return 自提详情 {@link PickUpRecordByIdResponse}
	 */
	@PostMapping("/returnOrder/${application.order.version}/pickuprecord/get-by-trade-id")
	BaseResponse<PickUpRecordByIdResponse> getOneByTradeId(@RequestBody @Valid PickUpRecordByTradeIdRequest pickUpRecordByIdRequest);

}

