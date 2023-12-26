package com.wanmi.sbc.advertising.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wanmi.sbc.advertising.model.AdConsumerMsg;

/**
 * @author zc
 *
 */
@Repository
public interface AdConsumerMsgRepository extends JpaRepository<AdConsumerMsg, Integer> {
	
	public AdConsumerMsg queryByMsgId(String msgId);



	
}
