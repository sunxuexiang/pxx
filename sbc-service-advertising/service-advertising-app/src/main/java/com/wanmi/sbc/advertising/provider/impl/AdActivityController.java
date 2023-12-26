package com.wanmi.sbc.advertising.provider.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActAuditRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActGetByIdRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActPayCallBackRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActPayRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryAddedListRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryListPageRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActSaveRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActUpdateRequest;
import com.wanmi.sbc.advertising.api.request.activity.AdRefundCallbackRequest;
import com.wanmi.sbc.advertising.api.response.QueryActiveActResp;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.config.ConfigUtil;
import com.wanmi.sbc.advertising.model.AdSlotPrice;
import com.wanmi.sbc.advertising.repository.AdActivityRepository;
import com.wanmi.sbc.advertising.service.AdActivityService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;

/**
 * @author zc
 *
 */
@RestController
public class AdActivityController implements AdActivityProvider {

	@Autowired
	private AdActivityService adActivityService;

	@Autowired
	private AdActivityRepository adActivityRepository;
	
	@Autowired
	private ConfigUtil configUtil;


	@Override
	public BaseResponse<MicroServicePage<AdActivityDTO>> queryListPage(ActQueryListPageRequest request) {
		MicroServicePage<AdActivityDTO> page = adActivityService.queryListPage(request);
		return BaseResponse.success(page);
	}

	@Override
	public BaseResponse<String> save(ActSaveRequest request) {
		String save = adActivityService.save(request);
		return BaseResponse.success(save);
	}

	@Override
	public BaseResponse payCallBack(ActPayCallBackRequest request) {
//		adActivityService.payCallBack(request);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<AdActivityDTO> queryOne(ActGetByIdRequest request) {
		AdActivityDTO queryOne = adActivityService.queryOne(request.getId());
		return BaseResponse.success(queryOne);
	}

	@Override
	public BaseResponse<String> pay(ActPayRequest request) {
//		String res = adActivityService.pay(request);
//		return BaseResponse.success(res);
		return BaseResponse.success("");
	}

	@Override
	public BaseResponse<AdActivityDTO> audit(ActAuditRequest request) {
//		adActivityService.audit(request);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse refund(ActGetByIdRequest request) {
//		adActivityService.refund(request.getId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse takeOff(ActGetByIdRequest request) {
//		adActivityService.takeOff(request.getId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<QueryActiveActResp> queryActiveAct(ActQueryActiveActRequest req) {
		List<AdActivityDTO> ad = adActivityService.queryActiveAct(req);
		return BaseResponse.success(QueryActiveActResp.builder().activeActs(ad).build());
	}
	
	@Override
	public BaseResponse batchComplete() {
		adActivityService.batchComplete();
		return BaseResponse.SUCCESSFUL();
	}
	
	@Override
	public BaseResponse batchStart() {
		adActivityService.batchStart();
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse adRefundCallback(AdRefundCallbackRequest request) {
//		adActivityService.adRefundCallback(request);
		return BaseResponse.SUCCESSFUL();
	}
	
	@Override
	public BaseResponse update(ActUpdateRequest req) {
		adActivityService.sendUpdateAdInfoMq(req);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<QueryActiveActResp> queryMallGoodsActiveAct(ActQueryActiveActRequest req) {
		Map<String, String> advMap = configUtil.getAdvMap();
		String key = String.format("%s_%s_%s", req.getMarketId(), req.getMallTabId(), req.getGoodsCateId());
		String value = advMap.get(key);
		List<AdActivityDTO> activeActs = new ArrayList<>();
		if (StringUtils.isNotBlank(value)) {
			String[] list = value.split(",");
			for (int i = 0; i < list.length; i++) {
				AdActivityDTO activityDTO = new AdActivityDTO();
				activityDTO.setSlotGroupSeq(i+1);
				activityDTO.setSpuId(list[i]);
				activeActs.add(activityDTO);
			}
		}
		return BaseResponse.success(QueryActiveActResp.builder().activeActs(activeActs).build());
//		return BaseResponse.SUCCESSFUL();
	}

}