package com.wanmi.sbc.advertising.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryConflictListRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryListPageRequest;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.bean.enums.ActivityState;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.advertising.util.NativeQueryParam;
import com.wanmi.sbc.advertising.util.NativeSqlQueryUtil;
import com.wanmi.sbc.common.base.MicroServicePage;



public class AdActivityRepositoryImpl {
	
	@PersistenceContext
    private EntityManager entityManager;

	public MicroServicePage<AdActivityDTO> queryListPage(ActQueryListPageRequest request) {
		String sql = "SELECT * FROM `ad_activity` WHERE 1 = 1 ";
		Map<String, Object> paramMap = new HashMap<>();
		if (request.getStoreId() != null) {
			sql += " AND store_id= :store_id";
			paramMap.put("store_id", request.getStoreId());
		}
		if (request.getSlotType() != null) {
			sql += " AND slot_type= :slot_type";
			paramMap.put("slot_type", request.getSlotType().toValue());
		}
		if (request.getActivityState() != null) {
			if (request.getActivityState() == ActivityState.RELEASED) {
				sql += " AND activity_state IN (20,30)";
			}else {
				sql += " AND activity_state = :activity_state";
				paramMap.put("activity_state", request.getActivityState().toValue());
			}
		}
		if (request.getSubmitTime1() != null && request.getSubmitTime2() != null) {
			sql += " AND submit_time BETWEEN :submit_time1 AND :submit_time2";
			paramMap.put("submit_time1", request.getSubmitTime1());
			paramMap.put("submit_time2", request.getSubmitTime2());
		}
		if (request.getStartTime1() != null && request.getStartTime2() != null) {
			sql += " AND start_time BETWEEN :start_time1 AND :start_time2";
			paramMap.put("start_time1", request.getStartTime1());
			paramMap.put("start_time2", request.getStartTime2());
		}
		if (request.getCreateTime1() != null && request.getCreateTime2() != null) {
			sql += " AND create_time BETWEEN :create_time1 AND :create_time2";
			paramMap.put("create_time1", request.getCreateTime1());
			paramMap.put("create_time2", request.getCreateTime2());
		}
		if (request.getAuditTime1() != null && request.getAuditTime2() != null) {
			sql += " AND audit_time BETWEEN :audit_time1 AND :audit_time2";
			paramMap.put("audit_time1", request.getAuditTime1());
			paramMap.put("audit_time2", request.getAuditTime2());
		}
		
		NativeQueryParam param = NativeQueryParam.builder()
				.entityManager(entityManager)
				.pageRequest(request.getPageable())
				.sql(sql)
				.paramMap(paramMap)
				.build();
		MicroServicePage<AdActivityDTO> pageImpl = NativeSqlQueryUtil.listPageQuery(param, AdActivityDTO.class);
		return pageImpl;
	}
	

//	
//	public List<AdActivityDTO> queryConflictList(ActQueryConflictListRequest request) {
//		Map<String, Object> hashMap = new HashMap<String, Object>() {
//			private static final long serialVersionUID = 1L;
//			{
//				put("slot_id", request.getSlotId());
//				put("start_time", request.getStartTime());
//				put("end_time", request.getEndTime());
//			}
//
//		};
//		String sql = "SELECT * FROM ad_activity WHERE slot_id=:slot_id ";
//		sql += " AND activity_state<=30 ";
//		sql += " AND start_time <= :end_time && end_time>= :start_time";
//		
//		NativeQueryParam param = NativeQueryParam.builder().entityManager(entityManager)
//				.sql(sql)
//				.paramMap(hashMap)
//				.build();
//		List<AdActivityDTO> listQuery = NativeSqlQueryUtil.listQuery(param, AdActivityDTO.class);
//		return listQuery;
//	}
	
	public List<AdActivityDTO> queryActiveAct(ActQueryActiveActRequest request){
		Map<String, Object> hashMap = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("slot_type", request.getSlotType().toValue());
			}

		};
		String sql = "SELECT * FROM ad_activity WHERE activity_state=30 AND slot_type=:slot_type ";
		String marketSql = handleMarketParam(request.getSlotType(), request.getMarketId(), request.getMallTabId(), request.getGoodsCateId(), hashMap);
		sql += marketSql;
		if (request.getSlotGroupSeq() != null) {
			sql += " AND slot_group_seq=:slot_group_seq";
			hashMap.put("slot_group_seq", request.getSlotGroupSeq());
		}
		NativeQueryParam param = NativeQueryParam.builder().entityManager(entityManager)
				.sql(sql)
				.paramMap(hashMap)
				.orderSql("ORDER BY slot_group_seq")
				.build();
		List<AdActivityDTO> listQuery = NativeSqlQueryUtil.listQuery(param, AdActivityDTO.class);
		return listQuery;
	}

	private String handleMarketParam(SlotType slotType, Integer marketId, Integer mallTabId, Integer goodsCateId, Map<String, Object> hashMap) {
		String sql = "";
		if (slotType == SlotType.MALL_GOOODS_LIST) {
			if (marketId != null) {
				sql += " AND market_id = :market_id";
				hashMap.put("market_id", marketId);
			}
			if (mallTabId != null) {
				sql += " AND mall_tab_id = :mall_tab_id";
				hashMap.put("mall_tab_id", mallTabId);
			}
			if (goodsCateId != null) {
				sql += " AND goods_cate_id = :goods_cate_id";
				hashMap.put("goods_cate_id", goodsCateId);
			}
		}
		return sql;
	}



}
