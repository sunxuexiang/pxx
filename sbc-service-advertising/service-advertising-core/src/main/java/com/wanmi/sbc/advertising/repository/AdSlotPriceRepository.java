package com.wanmi.sbc.advertising.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.model.AdSlotPrice;

/**
 * @author zc
 *
 */
@Repository
public interface AdSlotPriceRepository extends JpaRepository<AdSlotPrice, Integer>, BatchRepository<AdSlotPrice> {

	AdSlotPrice queryBySlotIdAndEffectiveDate(Integer slotId, Date effectiveDate);
	
    @Query(value = "SELECT * FROM `ad_slot_date_price` WHERE slot_id =?1 AND state=1", nativeQuery = true)
	List<AdSlotPrice> queryAvailableList(Integer slotId);
    
    @Query(value = "SELECT * FROM `ad_slot_date_price` WHERE slot_id =?1 AND state=2", nativeQuery = true)
	List<AdSlotPrice> queryOccupiedList(Integer slotId);


}
