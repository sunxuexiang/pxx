package com.wanmi.sbc.advertising.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.model.AdPayRecord;

/**
 * @author zc
 *
 */
@Repository
public interface AdPayRecordRepository extends JpaRepository<AdPayRecord, Integer> {
    
    public AdPayRecord queryByActivityId(String actId);
    
   

	
}
