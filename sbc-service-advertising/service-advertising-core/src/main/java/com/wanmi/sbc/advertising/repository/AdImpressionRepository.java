package com.wanmi.sbc.advertising.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.model.AdImpression;

/**
 * @author zc
 *
 */
@Repository
public interface AdImpressionRepository extends JpaRepository<AdImpression, Integer> {
	


	
}
