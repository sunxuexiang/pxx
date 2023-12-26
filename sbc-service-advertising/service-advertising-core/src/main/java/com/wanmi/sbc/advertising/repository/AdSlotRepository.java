package com.wanmi.sbc.advertising.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.api.request.activity.ActQueryAddedListRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotQueryListPageRequest;
import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;
import com.wanmi.sbc.advertising.model.AdSlot;
import com.wanmi.sbc.common.base.MicroServicePage;

/**
 * @author zc
 *
 */
@Repository
public interface AdSlotRepository extends JpaRepository<AdSlot, Integer> {

	MicroServicePage<AdSlotDTO> queryListPage(SlotQueryListPageRequest request);


	List<AdSlotDTO> queryAddedList(ActQueryAddedListRequest request);
	



}
