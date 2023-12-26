package com.wanmi.sbc.advertising.util;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.query.internal.NativeQueryImpl;

import com.wanmi.sbc.common.base.MicroServicePage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NativeSqlQueryUtil {

	public static <T> MicroServicePage<T> listPageQuery(NativeQueryParam nativeQueryParam, Class<T> resClazz) {
		
		EntityManager entityManager = nativeQueryParam.getEntityManager();
		String countSqlTpl = "SELECT COUNT(*) FROM ({0}) tmp";
		String countSql = MessageFormat.format(countSqlTpl, nativeQueryParam.getSql());

		Query selectQuery = entityManager
				.createNativeQuery(nativeQueryParam.getSql() + " " + nativeQueryParam.getOrderSql())
				.setFirstResult((int) nativeQueryParam.getPageRequest().getOffset())
				.setMaxResults(nativeQueryParam.getPageRequest().getPageSize())
				.unwrap(NativeQueryImpl.class)
				.setResultTransformer(new XyyResultTransformer(resClazz));
		Query countQuery = entityManager.createNativeQuery(countSql);

		log.info("paramMap[{}]", nativeQueryParam.getParamMap());
		if (MapUtils.isNotEmpty(nativeQueryParam.getParamMap())) {
			nativeQueryParam.getParamMap().forEach((k, v) -> {
				selectQuery.setParameter(k, v);
				countQuery.setParameter(k, v);
			});
		}
	
		@SuppressWarnings("unchecked")
		List<T> resultList = selectQuery.getResultList();
		BigInteger singleResult = (BigInteger) countQuery.getSingleResult();

		MicroServicePage<T> pageImpl = new MicroServicePage<>(resultList, nativeQueryParam.getPageRequest(), singleResult.longValue());
		return pageImpl;
	}

	public static <T> List<T> listQuery(NativeQueryParam nativeQueryParam, Class<T> resClazz) {

		EntityManager entityManager = nativeQueryParam.getEntityManager();
		Query selectQuery = entityManager
				.createNativeQuery(nativeQueryParam.getSql() + " " + nativeQueryParam.getOrderSql())
				.unwrap(NativeQueryImpl.class)
				.setResultTransformer(new XyyResultTransformer(resClazz));

		log.info("paramMap[{}]", nativeQueryParam.getParamMap());
		if (MapUtils.isNotEmpty(nativeQueryParam.getParamMap())) {
			nativeQueryParam.getParamMap().forEach((k, v) -> {
				selectQuery.setParameter(k, v);
			});
		}

		@SuppressWarnings("unchecked")
		List<T> resultList = selectQuery.getResultList();
		return resultList;
	}
	
	public static <T> T singleQuery(NativeQueryParam nativeQueryParam, Class<T> resClazz) {
		List<T> listQuery = listQuery(nativeQueryParam, resClazz);
		return CollectionUtils.isEmpty(listQuery)? null: listQuery.get(0);
	}
	

	

}
