package com.wanmi.sbc.advertising.provider.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.advertising.api.provider.AdSlotProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryAddedListRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotBatchSetPriceRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotGetByIdRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotOptRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotQueryListPageRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotSaveRequest;
import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;
import com.wanmi.sbc.advertising.bean.dto.AdSlotPriceDTO;
import com.wanmi.sbc.advertising.bean.dto.SlotSeqDTO;
import com.wanmi.sbc.advertising.bean.enums.SlotState;
import com.wanmi.sbc.advertising.model.AdSlot;
import com.wanmi.sbc.advertising.model.AdSlotPrice;
import com.wanmi.sbc.advertising.repository.AdActivityRepository;
import com.wanmi.sbc.advertising.repository.AdSlotRepository;
import com.wanmi.sbc.advertising.service.AdActivityService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 *
 */
@Slf4j
@RestController
public class AdSlotController implements AdSlotProvider {

	@Autowired
	private AdSlotRepository adSlotRepository;

	@Autowired
	private AdActivityRepository adActivityRepository;
	
	@Autowired
	private AdActivityService adActivityService;

//	@Autowired
//    private YunServiceProvider yunServiceProvider;

	@Override
	public BaseResponse<AdSlotDTO> getById(SlotGetByIdRequest request) {
		Optional<AdSlot> findById = adSlotRepository.findById(request.getId());
		AdSlotDTO copyPropertiesThird = null;
		if (findById.isPresent()) {
			copyPropertiesThird = KsBeanUtil.copyPropertiesThird(findById.get(), AdSlotDTO.class);
		}
		return BaseResponse.success(copyPropertiesThird);
	}

	@Override
	public BaseResponse<MicroServicePage<AdSlotDTO>> queryListPage(SlotQueryListPageRequest request) {
		MicroServicePage<AdSlotDTO> page = adSlotRepository.queryListPage(request);
		return BaseResponse.success(page);
	}

	@Override
	public BaseResponse save(SlotSaveRequest request) {
		adActivityService.init(request);
		return BaseResponse.SUCCESSFUL();
	}


	@Override
	@Transactional
	public BaseResponse delete(SlotGetByIdRequest request) {
		Optional<AdSlot> slot = adSlotRepository.findById(request.getId());
		if (!slot.isPresent()) {
			return BaseResponse.SUCCESSFUL();
		}
		AdSlot adSlot = slot.get();
		// 已上架不能删除
		if (adSlot.getSlotState() == SlotState.ADDED) {
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "删除失败，广告位[" + request.getId() + "]状态不正确");
		}
		adSlotRepository.delete(adSlot);
		if (StringUtils.isNotBlank(adSlot.getCornerMarkKey())) {
			// TODO 从oss删除文件
			
		}
		
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse added(SlotOptRequest request) {
		Optional<AdSlot> oSlot = adSlotRepository.findById(request.getId());
		if (!oSlot.isPresent()) {
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "上架失败，广告位[" + request.getId() + "]不存在");
		}
		AdSlot adSlot = oSlot.get();
		if (adSlot.getSlotState() == SlotState.ADDED) {
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "上架失败，广告位[" + request.getId() + "]状态不正确");
		}
		adSlot.setSlotState(SlotState.ADDED);
		adSlot.setAddedTime(new Date());
		adSlotRepository.save(adSlot);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	@Transactional
	public BaseResponse takeOff(SlotOptRequest request) {
//		Optional<AdSlot> oSlot = adSlotRepository.findById(request.getId());
//		if (!oSlot.isPresent()) {
//			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "下架失败，广告位[" + request.getId() + "]不存在");
//		}
//		AdSlot adSlot = oSlot.get();
//		// 已上架才能下架
//		if (adSlot.getSlotState() != SlotState.ADDED) {
//			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "下架失败，广告位[" + request.getId() + "]状态不正确");
//		}
//		
//		// 查出所有待审核,待履行,履行中的广告活动
//		List<String> actIds = adActivityRepository.queryRefundableActIds(request.getId());
//		log.info("所有待审核,待履行,履行中的广告活动id[{}]", actIds);
//		List<AdActivity> acts = adActivityRepository.findAllById(actIds);
//		for (AdActivity adActivity : acts) {
//			// 待审核|待履行的退款，履行中的下架
//			if (adActivity.getActivityState() == ActivityState.SUCCESS) {
//				adActivityService.takeOff(adActivity.getId());
//			}else {
//				adActivityService.refund(adActivity.getId());
//			}
//		}
//		
//		adSlot.setSlotState(SlotState.TAKE_OFF);
//		adSlotRepository.save(adSlot);
		return BaseResponse.SUCCESSFUL();
	}

	
	@Override
	public BaseResponse<List<SlotSeqDTO>> queryAvailableSlotSeq(ActQueryAddedListRequest request) {
		List<SlotSeqDTO> queryAvailableSlotSeq = adActivityService.queryAvailableSlotSeq(request);
		return BaseResponse.success(queryAvailableSlotSeq);
	}
	
	@Override
	public BaseResponse<List<AdSlotPriceDTO>> queryAvailableTime(SlotGetByIdRequest request) {
		List<AdSlotPrice> queryAvailableTime = adActivityService.queryAvailableTime(request.getId());
		List<AdSlotPriceDTO> convertList = KsBeanUtil.convertList(queryAvailableTime, AdSlotPriceDTO.class);
		return BaseResponse.success(convertList);
	}
	
	@Override
	public BaseResponse batchSetSlotPrice(SlotBatchSetPriceRequest request) {
		adActivityService.batchSetSlotPrice(request);
		return BaseResponse.SUCCESSFUL();
	}

}