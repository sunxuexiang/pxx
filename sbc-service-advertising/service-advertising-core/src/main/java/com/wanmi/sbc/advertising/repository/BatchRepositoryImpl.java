package com.wanmi.sbc.advertising.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchRepositoryImpl<T> implements BatchRepository<T> {

	public static final int BATCH_SIZE = 60;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void batchInsert(List<T> list) {
		log.info("batchInsert开始，size[{}]", list.size());
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				entityManager.persist(list.get(i));
				if (i % BATCH_SIZE == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
			entityManager.flush();
			entityManager.clear();
		}
		log.info("batchInsert结束");

	}

}
