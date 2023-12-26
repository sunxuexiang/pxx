package com.wanmi.sbc.advertising.util;

import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.data.domain.PageRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NativeQueryParam {
	
	private EntityManager entityManager;
	
	// 分页对象
	private PageRequest pageRequest;
	
	// 查询的sql
	private String sql;
	
	// 排序的sql
	@Builder.Default
	private String orderSql = "";
	
	// 查询参数
	private Map<String, Object> paramMap;
	

}
