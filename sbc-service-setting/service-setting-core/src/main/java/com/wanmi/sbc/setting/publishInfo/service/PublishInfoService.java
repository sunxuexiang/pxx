package com.wanmi.sbc.setting.publishInfo.service;

import com.wanmi.sbc.setting.api.request.publishInfo.PublishInfoRequest;
import com.wanmi.sbc.setting.bean.vo.PublishInfoVO;
import com.wanmi.sbc.setting.companyinfo.model.root.CompanyInfo;
import com.wanmi.sbc.setting.companyinfo.service.CompanyInfoWhereCriteriaBuilder;
import com.wanmi.sbc.setting.publishInfo.repository.PublishInfoRepository;
import com.wanmi.sbc.setting.publishInfo.root.PublishInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.setting.api.response.companyinfo.CompanyInfoRopResponse;
import com.wanmi.sbc.setting.api.request.companyinfo.CompanyInfoQueryRequest;
import com.wanmi.sbc.setting.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.common.util.KsBeanUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>公司信息业务逻辑</p>
 * @author lq
 * @date 2019-11-05 16:09:36
 */
@Service("PublishInfoService")
public class PublishInfoService {
	@Autowired
	private PublishInfoRepository publishInfoRepository;
	
	/** 
	 * 新增公司信息
	 * @author lq
	 */
	@Transactional
	public PublishInfo add(PublishInfo entity) {
		publishInfoRepository.save(entity);
		return entity;
	}
	
	/** 
	 * 修改公司信息
	 * @author lq
	 */
	@Transactional
	public PublishInfo modify(PublishInfo entity) {
		publishInfoRepository.save(entity);
		return entity;
	}

	/**
	 * 不带参查询
	 * @return
	 */
	public List<PublishInfoVO> findCompanyInfos(){
		List<PublishInfoVO> publishInfoVOList = new ArrayList<>();
		List<PublishInfo> publishInfos = publishInfoRepository.findAll();
		if(!CollectionUtils.isEmpty(publishInfos)){
			publishInfos.forEach(item -> {
				publishInfoVOList.add(wrapperVo(item));
			});
		}
		return publishInfoVOList;
	}

	/**
	 * 分页查询公司信息
	 * @author lq
	 */
	public Page<PublishInfoVO> page(PublishInfoRequest queryReq){
		Page<PublishInfo> publishInfos = publishInfoRepository.findAll(queryReq.getPageRequest());
		Page<PublishInfoVO> publishInfoVOS = publishInfos.map(item -> wrapperVo(item));
		return  publishInfoVOS;
	}

	/**
	 * 将实体包装成VO
	 * @author lq
	 */
	public PublishInfoVO wrapperVo(PublishInfo publishInfo) {
		if (publishInfo != null){
			PublishInfoVO publishInfoVO = new PublishInfoVO();
			KsBeanUtil.copyPropertiesThird(publishInfo,publishInfoVO);
			return publishInfoVO;
		}
		return null;
	}

}
