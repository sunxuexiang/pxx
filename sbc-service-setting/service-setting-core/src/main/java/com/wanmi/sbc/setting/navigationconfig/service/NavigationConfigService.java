package com.wanmi.sbc.setting.navigationconfig.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigQueryRequest;
import com.wanmi.sbc.setting.bean.vo.NavigationConfigVO;
import com.wanmi.sbc.setting.navigationconfig.model.root.NavigationConfig;
import com.wanmi.sbc.setting.navigationconfig.repository.NavigationConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>导航配置业务逻辑</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@Service("NavigationConfigService")
public class NavigationConfigService {
	@Autowired
	private NavigationConfigRepository navigationConfigRepository;

	/**
	 * 新增导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public NavigationConfig add(NavigationConfig entity) {
		navigationConfigRepository.save(entity);
		return entity;
	}

	/**
	 * 修改导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public List<NavigationConfig> modify(List<NavigationConfig> entity) {
		List<NavigationConfig> navigationConfigs = navigationConfigRepository.saveAll(entity);
		return navigationConfigs;
	}

	/**
	 * 删除导航配置
	 * @author liaozhaohong
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void delete(Integer id) {
		navigationConfigRepository.deleteById(id);
	}


	/**
	 * 单个查询导航配置
	 * @author lvheng
	 */
	public NavigationConfig getOne(Integer id){
		return navigationConfigRepository.findById(id)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "导航配置不存在"));
	}

	/**
	 * 分页查询导航配置
	 * @author lvheng
	 */
	public Page<NavigationConfig> page(NavigationConfigQueryRequest queryReq){
		return navigationConfigRepository.findAll(
				NavigationConfigWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询导航配置
	 * @author lvheng
	 */
	public List<NavigationConfig> list(NavigationConfigQueryRequest queryReq){
		return navigationConfigRepository.findAll(NavigationConfigWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author lvheng
	 */
	public NavigationConfigVO wrapperVo(NavigationConfig navigationConfig) {
		if (navigationConfig != null){
			NavigationConfigVO navigationConfigVO = KsBeanUtil.convert(navigationConfig, NavigationConfigVO.class);
			return navigationConfigVO;
		}
		return null;
	}
}

