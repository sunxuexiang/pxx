package com.wanmi.sbc.setting.logisticscompany.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsBaseAddressQueryRequest;
import com.wanmi.sbc.setting.bean.vo.LogisticsBaseAddressVO;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsBaseAddress;
import com.wanmi.sbc.setting.logisticscompany.repository.LogisticsBaseAddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:14
*/
@Service("LogisticsBaseAddressService")
@Slf4j
public class LogisticsBaseAddressService {
	@Autowired
	private LogisticsBaseAddressRepository logisticsBaseAddressRepository;


	/**
	 * 新增物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsBaseAddress add(LogisticsBaseAddress entity) {
		entity.setDelFlag(DeleteFlag.NO);
		logisticsBaseAddressRepository.save(entity);
		return entity;
	}

	/**
	 * 修改物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsBaseAddress modify(LogisticsBaseAddress entity) {
		entity.setUpdateTime(LocalDateTime.now());
		logisticsBaseAddressRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public void deleteById(LogisticsBaseAddress entity) {
		logisticsBaseAddressRepository.deleteById(entity.getId());
	}



	/**
	 * 分页查询物流线路
	 */
	public Page<LogisticsBaseAddress> page(LogisticsBaseAddressQueryRequest queryReq){
		return logisticsBaseAddressRepository.findAll(
				LogisticsBaseAddressWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询物流线路
	 */
	public List<LogisticsBaseAddress> list(LogisticsBaseAddressQueryRequest queryReq){
		return logisticsBaseAddressRepository.findAll(LogisticsBaseAddressWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 */
	public LogisticsBaseAddressVO wrapperVo(LogisticsBaseAddress logisticsBaseAddress) {
		if (logisticsBaseAddress != null){
			LogisticsBaseAddressVO logisticsBaseAddressVO = KsBeanUtil.convert(logisticsBaseAddress, LogisticsBaseAddressVO.class);
			return logisticsBaseAddressVO;
		}
		return null;
	}

	public void saveAll(List<LogisticsBaseAddressVO> collect) {
		logisticsBaseAddressRepository.saveAll(KsBeanUtil.convert(collect,LogisticsBaseAddress.class));
	}

}

