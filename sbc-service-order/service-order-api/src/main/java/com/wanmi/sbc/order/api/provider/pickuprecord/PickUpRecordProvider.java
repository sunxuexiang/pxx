package com.wanmi.sbc.order.api.provider.pickuprecord;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.pickuprecord.*;
import com.wanmi.sbc.order.api.response.pickuprecord.PickUpRecordAddResponse;
import com.wanmi.sbc.order.api.response.pickuprecord.PickUpRecordModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>自提码表代码生成保存服务Provider</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "PickUpRecordProvider")
public interface PickUpRecordProvider {

	/**
	 * 新增自提码表代码生成API
	 *
	 * @author lh
	 * @param pickUpRecordAddRequest 自提码表代码生成新增参数结构 {@link PickUpRecordAddRequest}
	 * @return 新增的自提码表代码生成信息 {@link PickUpRecordAddResponse}
	 */
	@PostMapping("/order/${application.order.version}/pickuprecord/add")
	BaseResponse<PickUpRecordAddResponse> add(@RequestBody @Valid PickUpRecordAddRequest pickUpRecordAddRequest);

	/**
	 * 修改自提码表代码生成API
	 *
	 * @author lh
	 * @param pickUpRecordModifyRequest 自提码表代码生成修改参数结构 {@link PickUpRecordModifyRequest}
	 * @return 修改的自提码表代码生成信息 {@link PickUpRecordModifyResponse}
	 */
	@PostMapping("/order/${application.order.version}/pickuprecord/modify")
	BaseResponse<PickUpRecordModifyResponse> modify(@RequestBody @Valid PickUpRecordModifyRequest pickUpRecordModifyRequest);


	/**
	 * 兑换自提码顶顶那
	 *
	 * @author lh
	 * @param pickUpRecordUpdateByTradeIdRequest 自提码表代码生成修改参数结构 {@link PickUpRecordUpdateByTradeIdRequest}
	 * @return 修改的自提码表代码生成信息 {@link BaseResponse}
	 */
	@PostMapping("/order/${application.order.version}/pickuprecord/updatePickUp")
	BaseResponse updatePickUp(@RequestBody @Valid PickUpRecordUpdateByTradeIdRequest pickUpRecordUpdateByTradeIdRequest);

	/**
	 * 单个删除自提码表代码生成API
	 *
	 * @author lh
	 * @param pickUpRecordDelByIdRequest 单个删除参数结构 {@link PickUpRecordDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/order/${application.order.version}/pickuprecord/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid PickUpRecordDelByIdRequest pickUpRecordDelByIdRequest);

	/**
	 * 批量删除自提码表代码生成API
	 *
	 * @author lh
	 * @param pickUpRecordDelByIdListRequest 批量删除参数结构 {@link PickUpRecordDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/order/${application.order.version}/pickuprecord/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid PickUpRecordDelByIdListRequest pickUpRecordDelByIdListRequest);

	/**
	 * 批量新增自提码表代码生成API
	 *
	 * @author lh
	 * @param pickUpRecordDelByIdListRequest 批量删除参数结构 {@link PickUpRecordAddBatchRequest}
	 * @return  {@link BaseResponse}
	 */
	@PostMapping("/order/${application.order.version}/pickuprecord/add-batch")
	BaseResponse addBatch(@RequestBody @Valid PickUpRecordAddBatchRequest pickUpRecordDelByIdListRequest);
	
	

}

