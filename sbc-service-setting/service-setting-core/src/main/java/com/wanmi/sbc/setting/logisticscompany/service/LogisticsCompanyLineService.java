package com.wanmi.sbc.setting.logisticscompany.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyLineQueryRequest;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyLineVO;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompanyLine;
import com.wanmi.sbc.setting.logisticscompany.repository.LogisticsCompanyLineRepository;
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
@Service("LogisticsCompanyLineService")
@Slf4j
public class LogisticsCompanyLineService {
	@Autowired
	private LogisticsCompanyLineRepository logisticsCompanyLineRepository;


	/**
	 * 新增物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsCompanyLine add(LogisticsCompanyLine entity) {
		entity.setDelFlag(DeleteFlag.NO);
		logisticsCompanyLineRepository.save(entity);
		return entity;
	}

	/**
	 * 修改物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsCompanyLine modify(LogisticsCompanyLine entity) {
		entity.setUpdateTime(LocalDateTime.now());
		logisticsCompanyLineRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public void deleteById(LogisticsCompanyLine entity) {
		logisticsCompanyLineRepository.deleteById(entity.getLineId());
	}



	/**
	 * 分页查询物流线路
	 */
	public Page<LogisticsCompanyLine> page(LogisticsCompanyLineQueryRequest queryReq){
		return logisticsCompanyLineRepository.findAll(
				LogisticsCompanyLineWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询物流线路
	 */
	public List<LogisticsCompanyLine> list(LogisticsCompanyLineQueryRequest queryReq){
		return logisticsCompanyLineRepository.findAll(LogisticsCompanyLineWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 */
	public LogisticsCompanyLineVO wrapperVo(LogisticsCompanyLine logisticsCompanyLine) {
		if (logisticsCompanyLine != null){
			LogisticsCompanyLineVO logisticsCompanyLineVO = KsBeanUtil.convert(logisticsCompanyLine, LogisticsCompanyLineVO.class);
			return logisticsCompanyLineVO;
		}
		return null;
	}

	public void saveAll(List<LogisticsCompanyLineVO> collect) {
		logisticsCompanyLineRepository.saveAll(KsBeanUtil.convert(collect,LogisticsCompanyLine.class));
	}

}

