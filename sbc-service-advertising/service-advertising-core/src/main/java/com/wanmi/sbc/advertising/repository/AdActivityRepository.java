package com.wanmi.sbc.advertising.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryConflictListRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryListPageRequest;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.model.AdActivity;
import com.wanmi.sbc.common.base.MicroServicePage;


/**
 * @author zc
 *
 */
@Repository
public interface AdActivityRepository extends JpaRepository<AdActivity, String> {
	
    
	public MicroServicePage<AdActivityDTO> queryListPage(ActQueryListPageRequest request);


//	public List<AdActivityDTO> queryConflictList(ActQueryConflictListRequest request);

	public List<AdActivityDTO> queryActiveAct(ActQueryActiveActRequest req);
	
    @Query(value = "SELECT * from `ad_activity` WHERE activity_state = 30 AND end_time < NOW()", nativeQuery = true)
	public List<AdActivity> queryNeedCompleteList();
    
    @Query(value = "SELECT * from `ad_activity` WHERE activity_state = 20 AND start_time <= NOW()", nativeQuery = true)
	public List<AdActivity> queryNeedStartList();

//    @Query(value = "SELECT DISTINCT t1.act_id FROM `ad_activity_detail` t1\r\n"
//    		+ "INNER JOIN `ad_activity` t2\r\n"
//    		+ "ON t1.act_id=t2.id AND t2.activity_state IN (10,20,30)\r\n"
//    		+ "WHERE t1.slot_id=?1", nativeQuery = true)
//	public List<String> queryRefundableActIds(Integer slotId);
    
    @Query(value = "SELECT * FROM ad_activity WHERE activity_state=30 AND  slot_type=3  AND market_id = ?1  \r\n"
    		+ "ORDER BY real_price DESC LIMIT ?2", nativeQuery = true)
    public List<AdActivityDTO> queryMallGooodsAdTop(Integer marketId, Integer limit) ;
    
}
