package com.wanmi.sbc.advertising.repository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author zc
 *
 */
public interface BatchRepository<T> {

	@Transactional
	public void batchInsert(List<T> list);

}
