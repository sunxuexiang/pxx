package com.wanmi.sbc.advertisingnew;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.provider.AdSlotProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActAuditRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActGetByIdRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotBatchSetPriceRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotGetByIdRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotOptRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotQueryListPageRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotSaveRequest;
import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;
import com.wanmi.sbc.advertising.bean.dto.AdSlotPriceDTO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
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
	private AdSlotProvider adSlotProvider;

	@Autowired
	private OperateLogMQUtil operateLogMQUtil;
	
	@Autowired
	private AdActivityProvider adActivityProvider;
	
	
	@ApiOperation(value = "批量设置广告位价格")
	@PostMapping("/adSlot/batchSetSlotPrice")
	public BaseResponse<List<AdSlotPriceDTO>> batchSetSlotPrice(@RequestBody SlotBatchSetPriceRequest request) {
		return adSlotProvider.batchSetSlotPrice(request);
	}
	
	/**
	 * 查询可用的广告位时间
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "查询可用的广告位时间")
	@PostMapping("/adSlot/queryAvailableTime")
	public BaseResponse<List<AdSlotPriceDTO>> queryAvailableTime(@RequestBody SlotGetByIdRequest request) {
		BaseResponse<List<AdSlotPriceDTO>> queryAvailableTime = adSlotProvider.queryAvailableTime(request);
		return queryAvailableTime;
	}

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "保存广告位信息")
	@PostMapping("/adSlot/save")
	BaseResponse saveSlot(@RequestBody SlotSaveRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "保存广告位");
		request.setUpdateUser(commonUtil.getOperator().getName());
		adSlotProvider.save(request);
		return BaseResponse.SUCCESSFUL();
	}

	/**
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "删除广告位")
	@DeleteMapping("/adSlot/delete")
	BaseResponse deleteSlot(@RequestBody SlotGetByIdRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "删除广告位");
		return adSlotProvider.delete(request);
	}

	@ApiOperation(value = "分页查询广告位")
	@PostMapping("/adSlot/queryListPage")
	BaseResponse<MicroServicePage<AdSlotDTO>> querySlotListPage(@RequestBody SlotQueryListPageRequest request) {
		BaseResponse<MicroServicePage<AdSlotDTO>> queryListPage = adSlotProvider.queryListPage(request);
		return queryListPage;
	}

	@ApiOperation(value = "查询单个广告位")
	@GetMapping("/adSlot/getById")
	BaseResponse<AdSlotDTO> getSlotById(SlotGetByIdRequest request) {
		return adSlotProvider.getById(request);
	}

	@ApiOperation(value = "上架广告位")
	@PostMapping("/adSlot/added")
	BaseResponse addedSlot(@RequestBody SlotOptRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "上架广告位");
		return adSlotProvider.added(request);
	}

	@ApiOperation(value = "下架广告位")
	@PostMapping("/adSlot/takeOff")
	BaseResponse takeOffSlot(@RequestBody SlotOptRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "下架广告位");
		request.setOptUser(commonUtil.getOperator().getName());
		return adSlotProvider.takeOff(request);
	}


	@ApiOperation(value = "广告活动审核")
	@PostMapping("/adActivity/audit")
	BaseResponse<String> audit(@RequestBody ActAuditRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "广告活动审核");
		return adActivityProvider.audit(request);
	}
	
	@ApiOperation(value = "广告活动退款")
	@PostMapping("/adActivity/refund")
	BaseResponse<String> refund(@RequestBody ActGetByIdRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "广告活动退款");
		return adActivityProvider.refund(request);
	}
	
	@ApiOperation(value = "广告活动下架")
	@PostMapping("/adActivity/takeOff")
	BaseResponse<String> takeOff(@RequestBody ActGetByIdRequest request) {
		operateLogMQUtil.convertAndSend("设置", "广告", "广告活动下架");
		return adActivityProvider.takeOff(request);
	}
	
	
}
