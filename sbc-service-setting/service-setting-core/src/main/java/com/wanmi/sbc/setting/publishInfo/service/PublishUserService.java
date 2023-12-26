package com.wanmi.sbc.setting.publishInfo.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.companyinfo.CompanyInfoQueryRequest;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishUserRequest;
import com.wanmi.sbc.setting.api.response.companyinfo.CompanyInfoRopResponse;
import com.wanmi.sbc.setting.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.setting.bean.vo.PublishInfoVO;
import com.wanmi.sbc.setting.publishInfo.repository.PublishInfoRepository;
import com.wanmi.sbc.setting.publishInfo.repository.PublishUserRepository;
import com.wanmi.sbc.setting.publishInfo.root.PublishInfo;
import com.wanmi.sbc.setting.publishInfo.root.PublishUser;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * <p>信息发布用户业务逻辑</p>
 * @author lwp
 * @date 2023/10/18
 */
@Service("publishUserService")
public class PublishUserService {
	@Autowired
	private PublishUserRepository publishUserRepository;
	
	/** 
	 * 新增信息发布用户
	 * @author lq
	 */
	@Transactional
	public PublishUser add(PublishUser entity) {
		publishUserRepository.save(entity);
		return entity;
	}
	
	/** 
	 * 修改信息发布用户
	 * @author lq
	 */
	@Transactional
	public PublishUser modify(PublishUser entity) {
		publishUserRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除信息发布用户
	 * @author lq
	 */
	@Transactional
	public void deleteById(Long id) {
		publishUserRepository.deleteById(id);
	}
	

	
	/** 
	 * 单个查询信息发布用户
	 * @author lq
	 */
	public PublishUser getPublishUser(PublishUserRequest request){
		return publishUserRepository.findByUserNameAndUserPassAndDelFlag(request.getUserName(),request.getUserPass(), DeleteFlag.NO);
	}


}
