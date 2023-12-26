package com.wanmi.sbc.order.provider.impl.pickuprecord;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.pickuprecord.PickUpRecordProvider;
import com.wanmi.sbc.order.api.request.pickuprecord.*;
import com.wanmi.sbc.order.api.response.pickuprecord.PickUpRecordAddResponse;
import com.wanmi.sbc.order.api.response.pickuprecord.PickUpRecordModifyResponse;
import com.wanmi.sbc.order.pickuprecord.model.root.PickUpRecord;
import com.wanmi.sbc.order.pickuprecord.service.PickUpRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>测试代码生成保存服务接口实现</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@RestController
@Validated
public class PickUpRecordController implements PickUpRecordProvider {
	@Autowired
	private PickUpRecordService pickUpRecordService;

	@Override
	public BaseResponse<PickUpRecordAddResponse> add(@RequestBody @Valid PickUpRecordAddRequest pickUpRecordAddRequest) {
		PickUpRecord pickUpRecord = KsBeanUtil.convert(pickUpRecordAddRequest, PickUpRecord.class);
		return BaseResponse.success(new PickUpRecordAddResponse(
				pickUpRecordService.wrapperVo(pickUpRecordService.add(pickUpRecord))));
	}

	@Override
	public BaseResponse<PickUpRecordModifyResponse> modify(@RequestBody @Valid PickUpRecordModifyRequest pickUpRecordModifyRequest) {
		PickUpRecord pickUpRecord = KsBeanUtil.convert(pickUpRecordModifyRequest, PickUpRecord.class);
		return BaseResponse.success(new PickUpRecordModifyResponse(
				pickUpRecordService.wrapperVo(pickUpRecordService.modify(pickUpRecord))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid PickUpRecordDelByIdRequest pickUpRecordDelByIdRequest) {
		PickUpRecord pickUpRecord = KsBeanUtil.convert(pickUpRecordDelByIdRequest, PickUpRecord.class);
		pickUpRecord.setDelFlag(DeleteFlag.YES);
		pickUpRecordService.deleteById(pickUpRecord);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid PickUpRecordDelByIdListRequest pickUpRecordDelByIdListRequest) {
		List<PickUpRecord> pickUpRecordList = pickUpRecordDelByIdListRequest.getPickUpIdList().stream()
			.map(PickUpId -> {
				PickUpRecord pickUpRecord = KsBeanUtil.convert(PickUpId, PickUpRecord.class);
				pickUpRecord.setDelFlag(DeleteFlag.YES);
				return pickUpRecord;
			}).collect(Collectors.toList());
		pickUpRecordService.deleteByIdList(pickUpRecordList);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse addBatch(@Valid PickUpRecordAddBatchRequest pickUpRecordDelByIdListRequest) {
		List<PickUpRecord> pickUpRecordList = pickUpRecordDelByIdListRequest.getPickUpRecordAddRequestList().stream()
				.map(PickUpId -> {
					PickUpRecord pickUpRecord = KsBeanUtil.convert(PickUpId, PickUpRecord.class);
					return pickUpRecord;
				}).collect(Collectors.toList());
		pickUpRecordService.addBatch(pickUpRecordList);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse updatePickUp(@Valid PickUpRecordUpdateByTradeIdRequest pickUpRecordUpdateByTradeIdRequest) {
		pickUpRecordService.updateChangePickUpFlag(pickUpRecordUpdateByTradeIdRequest.getTradeId()
				,pickUpRecordUpdateByTradeIdRequest.getOperator(),pickUpRecordUpdateByTradeIdRequest.getPhone());
		return BaseResponse.SUCCESSFUL();
	}
}

