package com.wanmi.sbc.wms.provider.impl.record;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.provider.record.RecordQueryProvider;
import com.wanmi.sbc.wms.api.request.record.RecordPageRequest;
import com.wanmi.sbc.wms.api.request.record.RecordQueryRequest;
import com.wanmi.sbc.wms.api.response.record.RecordPageResponse;
import com.wanmi.sbc.wms.api.request.record.RecordListRequest;
import com.wanmi.sbc.wms.api.response.record.RecordListResponse;
import com.wanmi.sbc.wms.api.request.record.RecordByIdRequest;
import com.wanmi.sbc.wms.api.response.record.RecordByIdResponse;
import com.wanmi.sbc.wms.bean.vo.RecordVO;
import com.wanmi.sbc.wms.record.service.RecordService;
import com.wanmi.sbc.wms.record.model.root.Record;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>请求记录查询服务接口实现</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@RestController
@Validated
public class RecordQueryController implements RecordQueryProvider {
	@Autowired
	private RecordService recordService;

	@Override
	public BaseResponse<RecordPageResponse> page(@RequestBody @Valid RecordPageRequest recordPageReq) {
		RecordQueryRequest queryReq = new RecordQueryRequest();
		KsBeanUtil.copyPropertiesThird(recordPageReq, queryReq);
		Page<Record> recordPage = recordService.page(queryReq);
		Page<RecordVO> newPage = recordPage.map(entity -> recordService.wrapperVo(entity));
		MicroServicePage<RecordVO> microPage = new MicroServicePage<>(newPage, recordPageReq.getPageable());
		RecordPageResponse finalRes = new RecordPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<RecordListResponse> list(@RequestBody @Valid RecordListRequest recordListReq) {
		RecordQueryRequest queryReq = new RecordQueryRequest();
		KsBeanUtil.copyPropertiesThird(recordListReq, queryReq);
		List<Record> recordList = recordService.list(queryReq);
		List<RecordVO> newList = recordList.stream().map(entity -> recordService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new RecordListResponse(newList));
	}

	@Override
	public BaseResponse<RecordByIdResponse> getById(@RequestBody @Valid RecordByIdRequest recordByIdRequest) {
		Record record = recordService.getById(recordByIdRequest.getRecordId());
		return BaseResponse.success(new RecordByIdResponse(recordService.wrapperVo(record)));
	}

}

