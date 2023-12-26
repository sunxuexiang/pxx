package com.wanmi.sbc.advertisingnew;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.provider.AdSlotProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActPayRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryAddedListRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActSaveRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotGetByIdRequest;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;
import com.wanmi.sbc.advertising.bean.dto.AdSlotPriceDTO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 *
 */
@Slf4j
@Api(description = "广告相关功能API", tags = "AdvertisingNewController")
@RestController
@RequestMapping(value = "advertising")
public class AdvertisingNewController {

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private AdActivityProvider adActivityProvider;

	@Autowired
	private OperateLogMQUtil operateLogMQUtil;
	
	@Autowired
	private AdSlotProvider adSlotProvider;

	/**
	 * 保存广告活动
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "保存广告活动")
	@PostMapping("/adActivity/save")
	BaseResponse<String> saveActivity(@RequestBody @Valid  ActSaveRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "保存广告活动");
		for (AdActivityDTO adActivityDTO : request.getActs()) {
			adActivityDTO.setCreateUser(commonUtil.getOperator().getName());
			adActivityDTO.setUpdateUser(commonUtil.getOperator().getName());
			adActivityDTO.setStoreId(commonUtil.getStoreId());
			adActivityDTO.setBuyUserId(commonUtil.getOperator().getAccount());
			adActivityDTO.setBuyUser(commonUtil.getAccountName());
		}
		return adActivityProvider.save(request);
	}




	@ApiOperation(value = "付款")
	@PostMapping("/adActivity/pay")
	BaseResponse<String> pay(@RequestBody ActPayRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "广告付款");
		return adActivityProvider.pay(request);
	}

}
