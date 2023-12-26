package com.wanmi.sbc.returnorder.provider.impl.pickuprecord;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.pickuprecord.PickUpRecordQueryProvider;
import com.wanmi.sbc.returnorder.api.response.pickuprecord.PickUpRecordByIdResponse;
import com.wanmi.sbc.returnorder.api.response.pickuprecord.PickUpRecordListResponse;
import com.wanmi.sbc.returnorder.api.response.pickuprecord.PickUpRecordPageResponse;
import com.wanmi.sbc.returnorder.bean.vo.PickUpRecordVO;
import com.wanmi.sbc.returnorder.pickuprecord.model.root.PickUpRecord;
import com.wanmi.sbc.returnorder.pickuprecord.service.PickUpRecordService;
import com.wanmi.sbc.returnorder.api.request.pickuprecord.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>测试代码生成查询服务接口实现</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@RestController
@Validated
public class PickUpRecordQueryController implements PickUpRecordQueryProvider {
	@Autowired
	private PickUpRecordService pickUpRecordService;

	@Override
	public BaseResponse<PickUpRecordPageResponse> page(@RequestBody @Valid PickUpRecordPageRequest pickUpRecordPageReq) {
		PickUpRecordQueryRequest queryReq = KsBeanUtil.convert(pickUpRecordPageReq, PickUpRecordQueryRequest.class);
		Page<PickUpRecord> pickUpRecordPage = pickUpRecordService.page(queryReq);
		Page<PickUpRecordVO> newPage = pickUpRecordPage.map(entity -> pickUpRecordService.wrapperVo(entity));
		MicroServicePage<PickUpRecordVO> microPage = new MicroServicePage<>(newPage, pickUpRecordPageReq.getPageable());
		PickUpRecordPageResponse finalRes = new PickUpRecordPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<PickUpRecordListResponse> list(@RequestBody @Valid PickUpRecordListRequest pickUpRecordListReq) {
		PickUpRecordQueryRequest queryReq = KsBeanUtil.convert(pickUpRecordListReq, PickUpRecordQueryRequest.class);
		List<PickUpRecord> pickUpRecordList = pickUpRecordService.list(queryReq);
		List<PickUpRecordVO> newList = pickUpRecordList.stream().map(entity -> pickUpRecordService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new PickUpRecordListResponse(newList));
	}

	@Override
	public BaseResponse<PickUpRecordByIdResponse> getById(@RequestBody @Valid PickUpRecordByIdRequest pickUpRecordByIdRequest) {
		PickUpRecord pickUpRecord =
		pickUpRecordService.getOne(pickUpRecordByIdRequest.getPickUpId());
		return BaseResponse.success(new PickUpRecordByIdResponse(pickUpRecordService.wrapperVo(pickUpRecord)));
	}

	@Override
	public BaseResponse<PickUpRecordByIdResponse> getOneByTradeId(@Valid PickUpRecordByTradeIdRequest pickUpRecordByIdRequest) {
		PickUpRecord pickUpRecord =
				pickUpRecordService.getOneByTradeId(pickUpRecordByIdRequest.getTradeId());
		return BaseResponse.success(new PickUpRecordByIdResponse(pickUpRecordService.wrapperVo(pickUpRecord)));
	}
}

