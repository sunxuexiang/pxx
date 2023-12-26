package com.wanmi.sbc.advertising.api.provider;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wanmi.sbc.advertising.api.request.activity.ActQueryAddedListRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotBatchSetPriceRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotGetByIdRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotOptRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotQueryListPageRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotSaveRequest;
import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;
import com.wanmi.sbc.advertising.bean.dto.AdSlotPriceDTO;
import com.wanmi.sbc.advertising.bean.dto.SlotSeqDTO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;

/**
 * @author zc
 *
 */
@FeignClient(value = "${application.advertising.name}", url = "${feign.url.advertising:#{null}}", contextId = "AdSlotProvider")
public interface AdSlotProvider {

	/**
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/getById")
	BaseResponse<AdSlotDTO> getById(@RequestBody @Valid SlotGetByIdRequest request);

	/**
	 * 分页查询广告位列表
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/queryListPage")
	BaseResponse<MicroServicePage<AdSlotDTO>> queryListPage(@RequestBody SlotQueryListPageRequest request);

	/**
	 * 保存广告位
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/save")
	BaseResponse save(@RequestBody @Valid SlotSaveRequest request);

	/**
	 * 删除广告位
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/delete")
	BaseResponse delete(@RequestBody @Valid SlotGetByIdRequest request);
	
	/**
	 * 上架广告位
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/added")
	BaseResponse added(@RequestBody @Valid SlotOptRequest request);
	
	/**
	 * 下架广告位
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/takeOff")
	BaseResponse takeOff(@RequestBody @Valid SlotOptRequest request);


	/**
	 * 查询广告位序号状态
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/queryAvailableSlotSeq")
	BaseResponse<List<SlotSeqDTO>> queryAvailableSlotSeq(@RequestBody @Valid ActQueryAddedListRequest request);

	/**
	 * 查询广告位序号状态
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/queryAvailableTime")
	BaseResponse<List<AdSlotPriceDTO>> queryAvailableTime(SlotGetByIdRequest request);

	/**
	 * 批量设置广告位价格
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/advertising/${application.advertising.version}/adSlot/batchSetSlotPrice")
	BaseResponse batchSetSlotPrice(SlotBatchSetPriceRequest request);

	

}
