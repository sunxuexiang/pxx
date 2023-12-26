package com.wanmi.sbc.wms.provider.impl.record;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.provider.record.RecordSaveProvider;
import com.wanmi.sbc.wms.api.request.record.RecordAddRequest;
import com.wanmi.sbc.wms.api.response.record.RecordAddResponse;
import com.wanmi.sbc.wms.api.request.record.RecordModifyRequest;
import com.wanmi.sbc.wms.api.response.record.RecordModifyResponse;
import com.wanmi.sbc.wms.api.request.record.RecordDelByIdRequest;
import com.wanmi.sbc.wms.api.request.record.RecordDelByIdListRequest;
import com.wanmi.sbc.wms.record.service.RecordService;
import com.wanmi.sbc.wms.record.model.root.Record;
import javax.validation.Valid;

/**
 * <p>请求记录保存服务接口实现</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@RestController
@Validated
public class RecordSaveController implements RecordSaveProvider {
	@Autowired
	private RecordService recordService;

	@Override
	public BaseResponse<RecordAddResponse> add(@RequestBody @Valid RecordAddRequest recordAddRequest) {
		Record record = new Record();
		KsBeanUtil.copyPropertiesThird(recordAddRequest, record);
		return BaseResponse.success(new RecordAddResponse(
				recordService.wrapperVo(recordService.add(record))));
	}

	@Override
	public BaseResponse<RecordModifyResponse> modify(@RequestBody @Valid RecordModifyRequest recordModifyRequest) {
		Record record = new Record();
		KsBeanUtil.copyPropertiesThird(recordModifyRequest, record);
		return BaseResponse.success(new RecordModifyResponse(
				recordService.wrapperVo(recordService.modify(record))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid RecordDelByIdRequest recordDelByIdRequest) {
		recordService.deleteById(recordDelByIdRequest.getRecordId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid RecordDelByIdListRequest recordDelByIdListRequest) {
		recordService.deleteByIdList(recordDelByIdListRequest.getRecordIdList());
		return BaseResponse.SUCCESSFUL();
	}

}

