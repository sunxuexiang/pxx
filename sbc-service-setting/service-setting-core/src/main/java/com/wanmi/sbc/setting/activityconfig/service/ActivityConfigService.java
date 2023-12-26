package com.wanmi.sbc.setting.activityconfig.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.activityconfig.model.root.ActivityConfig;
import com.wanmi.sbc.setting.activityconfig.repository.ActivityConfigRepository;
import com.wanmi.sbc.setting.api.request.activityconfig.ActivityConfigQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>导航配置业务逻辑</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@Service("ActivityConfigService")
public class ActivityConfigService {
	@Autowired
	private ActivityConfigRepository activityConfigRepository;

	/**
	 * 新增导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public ActivityConfig add(ActivityConfig entity) {
		activityConfigRepository.save(entity);
		return entity;
	}

	public void addAll(List<ActivityConfig> list) {
		activityConfigRepository.saveAll(list);
	}

	/**
	 * 修改导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public ActivityConfig modify(ActivityConfig entity) {
		activityConfigRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void deleteById(ActivityConfig entity) {
		activityConfigRepository.save(entity);
	}

	/**
	 * 批量删除导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void deleteByIdList(List<ActivityConfig> infos) {
		activityConfigRepository.saveAll(infos);
	}

	/**
	 * 单个查询导航配置
	 * @author lvheng
	 */
	public ActivityConfig getOne(Long id){
		return activityConfigRepository.findByIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "导航配置不存在"));
	}

	/**
	 * 分页查询导航配置
	 * @author lvheng
	 */
	public Page<ActivityConfig> page(ActivityConfigQueryRequest queryReq){
		return activityConfigRepository.findAll(
				ActivityConfigWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询导航配置
	 * @author lvheng
	 */
	public List<ActivityConfig> list(){
		return activityConfigRepository.findAll();
	}

}

