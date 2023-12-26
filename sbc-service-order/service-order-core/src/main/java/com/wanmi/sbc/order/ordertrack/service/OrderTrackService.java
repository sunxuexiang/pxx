package com.wanmi.sbc.order.ordertrack.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.bean.vo.OrderTrackVO;
import com.wanmi.sbc.order.ordertrack.repository.OrderTrackRepository;
import com.wanmi.sbc.order.ordertrack.root.OrderTrack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @desc  物流轨迹
 * @author shiy  2023/6/8 14:44
*/
@Service("orderTrackService")
@Slf4j
public class OrderTrackService {
	@Autowired
	private OrderTrackRepository orderTrackRepository;


	/**
	 * 新增物流轨迹
	 * @author fcq
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public OrderTrack add(OrderTrack entity) {
		Long aLong = orderTrackRepository.selectLogisticsNumNumber(entity.getCom(),entity.getNum());
		if (null!=aLong && aLong>0){
			entity.setId(aLong);
		}
		return orderTrackRepository.saveAndFlush(entity);
	}


	/**
	 * @desc  根据物流单号查
	 * @author shiy  2023/6/8 14:56
	*/
	public OrderTrack findByComNum(String com, String num) {
		List<OrderTrack> trackList = orderTrackRepository.queryList(com,num);
		if(CollectionUtils.isNotEmpty(trackList)){
			return trackList.get(0);
		}
		return null;
	}


	/**
	 * 单个查询物流轨迹
	 * @author fcq
	 */
	public OrderTrack getOne(Long id){
		return orderTrackRepository.getOne(id);
	}


	/**
	 * 将实体包装成VO
	 * @author fcq
	 */
	public OrderTrackVO wrapperVo(OrderTrack orderTrack) {
		if (orderTrack != null){
			OrderTrackVO orderTrackVO = KsBeanUtil.convert(orderTrack, OrderTrackVO.class);
			return orderTrackVO;
		}
		return null;
	}

	public void saveAll(List<OrderTrackVO> collect) {
		orderTrackRepository.saveAll(KsBeanUtil.convert(collect, OrderTrack.class));
	}

}

