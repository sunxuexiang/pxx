package com.wanmi.sbc.advertising.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.model.AdClick;

/**
 * @author zc
 *
 */
@Repository
public interface AdClickRepository extends JpaRepository<AdClick, Integer> {
	


	
}
