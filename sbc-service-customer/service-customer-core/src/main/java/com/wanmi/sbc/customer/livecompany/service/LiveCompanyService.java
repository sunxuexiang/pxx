package com.wanmi.sbc.customer.livecompany.service;

import com.wanmi.sbc.common.util.StringUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.customer.livecompany.repository.LiveCompanyRepository;
import com.wanmi.sbc.customer.livecompany.model.root.LiveCompany;
import com.wanmi.sbc.customer.api.request.livecompany.LiveCompanyQueryRequest;
import com.wanmi.sbc.customer.bean.vo.LiveCompanyVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.List;
import java.util.Optional;

/**
 * <p>直播商家业务逻辑</p>
 * @author zwb
 * @date 2020-06-06 18:06:59
 */
@Service("LiveCompanyService")
public class LiveCompanyService {
	@Autowired
	private LiveCompanyRepository liveCompanyRepository;

	/**
	 * 新增直播商家
	 * @author zwb
	 */
	@Transactional
	public LiveCompany add(LiveCompany entity) {
		Optional<LiveCompany> liveCompany = liveCompanyRepository.findByStoreIdAndDelFlag(entity.getStoreId(), DeleteFlag.NO);
		//第一次审核
		if (!liveCompany.isPresent()){
			entity.setLiveBroadcastStatus(1);
			liveCompanyRepository.save(entity);
		}else {
			//再次审核
			entity.setLiveBroadcastStatus(1);
			liveCompanyRepository.updateLiveBroadcastStatusByStoreId(entity.getLiveBroadcastStatus(),"",entity.getStoreId());
		}
		return entity;
	}

	/**
	 * 直播商家开通审核
	 * @author zwb
	 */
	@Transactional
	public LiveCompany modify(LiveCompany entity) {

		String auditReason = entity.getAuditReason();
		if (auditReason==null){
			auditReason="";
		}
		liveCompanyRepository.updateLiveBroadcastStatusByStoreId(entity.getLiveBroadcastStatus(),auditReason,entity.getStoreId());

		return entity;
	}

	/**
	 * 单个删除直播商家
	 * @author zwb
	 */
	@Transactional
	public void deleteById(LiveCompany entity) {
		liveCompanyRepository.save(entity);
	}

	/**
	 * 批量删除直播商家
	 * @author zwb
	 */
	@Transactional
	public void deleteByIdList(List<LiveCompany> infos) {
		liveCompanyRepository.saveAll(infos);
	}

	/**
	 * 单个查询直播商家
	 * @author zwb
	 */
	public LiveCompany getOne(Long storeId){
		LiveCompany liveCompany = liveCompanyRepository.findByStoreIdAndDelFlag(  storeId,DeleteFlag.NO)
				.orElse(new LiveCompany());
		if (liveCompany.getLiveBroadcastStatus()==null){
			liveCompany.setLiveBroadcastStatus(0);
		}
		return liveCompany;
	}

	/**
	 * 分页查询直播商家
	 * @author zwb
	 */
	public Page<LiveCompany> page(LiveCompanyQueryRequest queryReq){
		return liveCompanyRepository.findAll(
				LiveCompanyWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询直播商家
	 * @author zwb
	 */
	public List<LiveCompany> list(LiveCompanyQueryRequest queryReq){
		return liveCompanyRepository.findAll(LiveCompanyWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author zwb
	 */
	public LiveCompanyVO wrapperVo(LiveCompany liveCompany) {
		if (liveCompany != null){
			LiveCompanyVO liveCompanyVO = KsBeanUtil.convert(liveCompany, LiveCompanyVO.class);
			return liveCompanyVO;
		}
		return null;
	}
}

