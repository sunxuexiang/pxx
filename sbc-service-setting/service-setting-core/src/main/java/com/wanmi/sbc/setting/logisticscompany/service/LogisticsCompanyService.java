package com.wanmi.sbc.setting.logisticscompany.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyQueryRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanySyncRequest;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompany;
import com.wanmi.sbc.setting.logisticscompany.repository.LogisticsCompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>物流公司业务逻辑</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@Service("LogisticsCompanyService")
@Slf4j
public class LogisticsCompanyService {
	@Autowired
	private LogisticsCompanyRepository logisticsCompanyRepository;


	/**
	 * 新增物流公司
	 * @author fcq
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsCompany add(LogisticsCompany entity) {
		Long aLong = getCountByLogisticsName(entity);
		if (aLong>0){
			log.info("物流公司名字[{}]在批发市场Id[{}]物流类型[{}]系统中已存在",entity.getLogisticsName(),entity.getMarketId(),entity.getLogisticsType());
			throw new SbcRuntimeException(CommonErrorCode.DATA_EXIST);
		}
		entity.setDelFlag(DeleteFlag.NO);
		logisticsCompanyRepository.save(entity);
		return entity;
	}

	public Long getCountByLogisticsName(LogisticsCompany entity) {
		Long aLong = logisticsCompanyRepository.selectLogisticsNameByMarketId(entity.getLogisticsName(), entity.getMarketId(), entity.getLogisticsType());
		if(aLong==null){
			return 0L;
		}
		return aLong;
	}

	public LogisticsCompany selectLogisticsListByMarketIdAndLogisticsName(LogisticsCompany entity) {
		List<LogisticsCompany> logisticsCompanyList = logisticsCompanyRepository.selectLogisticsListByMarketIdAndLogisticsName(entity.getLogisticsName(), entity.getMarketId(), entity.getLogisticsType());
		if(CollectionUtils.isNotEmpty(logisticsCompanyList)){
			return logisticsCompanyList.get(0);
		}
		return null;
	}

	/**
	 * 修改物流公司
	 * @author fcq
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsCompany modify(LogisticsCompany entity) {
		Optional<LogisticsCompany> logisticsCompany = logisticsCompanyRepository.findByIdAndDelFlag(entity.getId(), DeleteFlag.NO);
		if (logisticsCompany.isPresent()) {
			LogisticsCompany dbCompany = logisticsCompany.get();
			dbCompany.setCompanyNumber(entity.getCompanyNumber());
			dbCompany.setLogisticsAddress(entity.getLogisticsAddress());
			dbCompany.setLogisticsPhone(entity.getLogisticsPhone());
			dbCompany.setLogisticsName(entity.getLogisticsName());
			dbCompany.setUpdateTime(LocalDateTime.now());
			logisticsCompanyRepository.save(dbCompany);
			entity = dbCompany;
		} else {
			add(entity);
		}
		return entity;
	}

	/**
	 * 单个删除物流公司
	 * @author fcq
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public void deleteById(LogisticsCompany entity) {
		logisticsCompanyRepository.deleteById(entity.getId());
	}

	/**
	 * 批量删除物流公司
	 * @author fcq
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public void deleteByIdList(List<Long> ids) {
		logisticsCompanyRepository.deleteByIdList(ids);
	}

	/**
	 * 单个查询物流公司
	 * @author fcq
	 */
	public LogisticsCompany getOne(Long id){
		Optional<LogisticsCompany> logisticsCompany = logisticsCompanyRepository.findByIdAndDelFlag(id, DeleteFlag.NO);
		if (logisticsCompany.isPresent()) {
			return logisticsCompany.get();
		}
		return null;
	}

	/**
	 * 分页查询物流公司
	 * @author fcq
	 */
	public Page<LogisticsCompany> page(LogisticsCompanyQueryRequest queryReq){
		return logisticsCompanyRepository.findAll(
				LogisticsCompanyWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询物流公司
	 * @author fcq
	 */
	public List<LogisticsCompany> list(LogisticsCompanyQueryRequest queryReq){
		return logisticsCompanyRepository.findAll(LogisticsCompanyWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author fcq
	 */
	public LogisticsCompanyVO wrapperVo(LogisticsCompany logisticsCompany) {
		if (logisticsCompany != null){
			LogisticsCompanyVO logisticsCompanyVO = KsBeanUtil.convert(logisticsCompany, LogisticsCompanyVO.class);
			return logisticsCompanyVO;
		}
		return null;
	}

	public void saveAll(List<LogisticsCompanyVO> collect) {
		logisticsCompanyRepository.saveAll(KsBeanUtil.convert(collect,LogisticsCompany.class));
	}

	public List<String> selectLogisticsCompanyNames() {
		return logisticsCompanyRepository.selectLogisticsCompanyNumbers();
	}

	public List<String> selectLogisticsNames() {
		return logisticsCompanyRepository.selectLogisticsNames();
	}

	public List<String> selectLogisticsCompanyNames(Long storeId, Long marketId, Integer logisticsType) {
		if(marketId==null) {
			return logisticsCompanyRepository.selectLogisticsNames(storeId, logisticsType);
		}
		return logisticsCompanyRepository.selectLogisticsNamesByMarketId(marketId, logisticsType);
	}

	public Long selectMaxId() {
		return logisticsCompanyRepository.selectMaxId();
	}

	@Transactional
    public void syncLogisticsCompany(LogisticsCompanySyncRequest addReq) {
		boolean isDeleted =Objects.nonNull(addReq.getDelFlag())&& addReq.getDelFlag() == 1;
		LogisticsCompanyQueryRequest queryReq = new LogisticsCompanyQueryRequest();
		queryReq.setStoreId(addReq.getSourceStoreId());
		queryReq.setIdList(addReq.getIdList());
		queryReq.setDelFlag(DeleteFlag.NO);
		List<LogisticsCompany> logisticsCompanies = logisticsCompanyRepository.findAll(LogisticsCompanyWhereCriteriaBuilder.build(queryReq));
		if (isDeleted) {
			logisticsCompanies.forEach(logistics -> {
				logistics.setDelFlag(DeleteFlag.YES);
			});
			logisticsCompanyRepository.saveAll(logisticsCompanies);
		}
		int targetStoreSize = addReq.getTargetStoreIdList().size();
		ExecutorService executorService = Executors.newFixedThreadPool(targetStoreSize > 50 ? 50 : targetStoreSize);
		addReq.getTargetStoreIdList().forEach(storeId -> {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					threadExecuteCopyLogistic(isDeleted, logisticsCompanies, storeId,addReq.getLogisticsType());
				}
			});
		});
		executorService.shutdown();
	}

	private void threadExecuteCopyLogistic(boolean isDeleted, List<LogisticsCompany> logisticsCompanies, Long storeId,Integer logisticsType) {
		logisticsCompanies.forEach(logistics -> {
			Long maxId = logisticsCompanyRepository.selectLogisticsName(logistics.getLogisticsName(), storeId,logisticsType);
			if (maxId != null) {
				LogisticsCompany newLogis = KsBeanUtil.convert(logistics, LogisticsCompany.class);
				newLogis.setId(maxId);
				newLogis.setStoreId(storeId);
				logisticsCompanyRepository.save(newLogis);
			} else {
				if (isDeleted) {
					//删除的记录在对应商家中没有不保存
				} else {
					LogisticsCompany newLogis = KsBeanUtil.convert(logistics, LogisticsCompany.class);
					newLogis.setId(null);
					newLogis.setCreateTime(LocalDateTime.now());
					newLogis.setStoreId(storeId);
					logisticsCompanyRepository.save(newLogis);
				}
			}
		});
	}
}

