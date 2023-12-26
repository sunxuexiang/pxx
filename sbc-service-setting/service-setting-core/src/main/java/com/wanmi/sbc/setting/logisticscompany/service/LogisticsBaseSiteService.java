package com.wanmi.sbc.setting.logisticscompany.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsBaseSiteQueryRequest;
import com.wanmi.sbc.setting.bean.vo.LogisticsBaseSiteVO;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsBaseSite;
import com.wanmi.sbc.setting.logisticscompany.repository.LogisticsBaseSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:14
*/
@Service("LogisticsBaseSiteService")
@Slf4j
public class LogisticsBaseSiteService {
	@Autowired
	private LogisticsBaseSiteRepository logisticsBaseSiteRepository;


	/**
	 * 新增物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsBaseSite add(LogisticsBaseSite entity) {
		entity.setDelFlag(DeleteFlag.NO);
		logisticsBaseSiteRepository.save(entity);
		return entity;
	}

	/**
	 * 修改物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsBaseSite modify(LogisticsBaseSite entity) {
		entity.setUpdateTime(LocalDateTime.now());
		logisticsBaseSiteRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除物流线路
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public void deleteById(LogisticsBaseSite entity) {
		logisticsBaseSiteRepository.deleteById(entity.getSiteId());
	}



	/**
	 * 分页查询物流线路
	 */
	public Page<LogisticsBaseSite> page(LogisticsBaseSiteQueryRequest queryReq){
		return logisticsBaseSiteRepository.findAll(
				LogisticsBaseSiteWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询物流线路
	 */
	public List<LogisticsBaseSite> list(LogisticsBaseSiteQueryRequest queryReq){
		return logisticsBaseSiteRepository.findAll(LogisticsBaseSiteWhereCriteriaBuilder.build(queryReq));
	}

	public String getLatestSiteNameByCustomerId(String customerId){
		return logisticsBaseSiteRepository.getLatestSiteNameByCustomerId(customerId);
	}

	public long count(LogisticsBaseSiteQueryRequest queryReq){
		queryReq.setDelFlag(DeleteFlag.NO);
		return logisticsBaseSiteRepository.count(LogisticsBaseSiteWhereCriteriaBuilder.build(queryReq));
	}

	public List<LogisticsBaseSiteVO> wrapperVoList(List<LogisticsBaseSite> logisticsBaseSiteList) {
		if(CollectionUtils.isNotEmpty(logisticsBaseSiteList)){
			List<LogisticsBaseSiteVO> voList = new ArrayList<>();
			logisticsBaseSiteList.forEach(logisticsBaseSite -> {
				voList.add(wrapperVo(logisticsBaseSite));
			});
			return voList;
		}
		return null;
	}

	/**
	 * 将实体包装成VO
	 */
	public LogisticsBaseSiteVO wrapperVo(LogisticsBaseSite logisticsBaseSite) {
		if (logisticsBaseSite != null){
			LogisticsBaseSiteVO logisticsBaseSiteVO = KsBeanUtil.convert(logisticsBaseSite, LogisticsBaseSiteVO.class);
			return logisticsBaseSiteVO;
		}
		return null;
	}

	public void saveAll(List<LogisticsBaseSiteVO> collect) {
		logisticsBaseSiteRepository.saveAll(KsBeanUtil.convert(collect,LogisticsBaseSite.class));
	}

}

