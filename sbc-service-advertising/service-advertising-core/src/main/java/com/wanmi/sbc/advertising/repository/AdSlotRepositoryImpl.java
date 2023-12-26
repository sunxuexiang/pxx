package com.wanmi.sbc.advertising.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.wanmi.sbc.advertising.api.request.activity.ActQueryAddedListRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotQueryListPageRequest;
import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.advertising.util.NativeQueryParam;
import com.wanmi.sbc.advertising.util.NativeSqlQueryUtil;
import com.wanmi.sbc.common.base.MicroServicePage;

public class AdSlotRepositoryImpl {

	@PersistenceContext
	private EntityManager entityManager;

	public MicroServicePage<AdSlotDTO> queryListPage(SlotQueryListPageRequest request) {
		Map<String, Object> paramMap = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("slot_type", request.getSlotType().toValue());
			}

		};
		String sql = " SELECT\r\n"
				+ "	t1.*,\r\n"
				+ "	( SELECT count(*) FROM `ad_slot_date_price` t2 WHERE t1.id = t2.slot_id AND t2.state = 1 ) availableDaysCount,\r\n"
				+ "	( SELECT count(*) FROM `ad_slot_date_price` t3 WHERE t1.id = t3.slot_id AND t3.state = 2 ) occupiedDaysCount \r\n"
				+ "FROM\r\n"
				+ "	`ad_slot` t1 \r\n"
				+ "WHERE\r\n"
				+ "	t1.slot_type = :slot_type \r\n"
				+ "	AND t1.slot_state = 1 \r\n";
		if (request.getMarketId() != null) {
			sql += " AND t1.market_id =  :market_id";
			paramMap.put("market_id", request.getMarketId());
		}
		if (request.getMallTabId() != null) {
			sql += " AND t1.mall_tab_id =  :mall_tab_id";
			paramMap.put("mall_tab_id", request.getMallTabId());
		}
		if (request.getGoodsCateId() != null) {
			sql += " AND t1.goods_cate_id =  :goods_cate_id";
			paramMap.put("goods_cate_id", request.getGoodsCateId());
		}

		NativeQueryParam param = NativeQueryParam.builder().entityManager(entityManager)
				.pageRequest(request.getPageable()).sql(sql).paramMap(paramMap).build();
		MicroServicePage<AdSlotDTO> pageImpl = NativeSqlQueryUtil.listPageQuery(param, AdSlotDTO.class);
		return pageImpl;
	}


	public List<AdSlotDTO> queryAddedList(ActQueryAddedListRequest request) {
		Map<String, Object> hashMap = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("slot_type", request.getSlotType().toValue());
			}

		};
		String sql = "SELECT * FROM ad_slot WHERE slot_type=:slot_type AND slot_state=1  ";
		
		if (request.getSlotType() == SlotType.MALL_GOOODS_LIST) {
			sql += " AND market_id = :market_id AND mall_tab_id = :mall_tab_id  AND goods_cate_id=:goods_cate_id ";
			hashMap.put("market_id", request.getMarketId());
			hashMap.put("mall_tab_id", request.getMallTabId());
			hashMap.put("goods_cate_id", request.getGoodsCateId());
		}
		if (request.getSlotGroupSeq() != null) {
			sql += " AND slot_group_seq=:slot_group_seq";
			hashMap.put("slot_group_seq", request.getSlotGroupSeq());
		}
		
		NativeQueryParam param = NativeQueryParam.builder().entityManager(entityManager).sql(sql).paramMap(hashMap).build();
		List<AdSlotDTO> listQuery = NativeSqlQueryUtil.listQuery(param, AdSlotDTO.class);
		return listQuery;
	}

}
