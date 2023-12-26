package com.wanmi.sbc.order.historylogisticscompany.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyQueryRequest;
import com.wanmi.sbc.order.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.order.historylogisticscompany.model.root.HistoryLogisticsCompany;
import com.wanmi.sbc.order.historylogisticscompany.repository.HistoryLogisticsCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>物流公司历史记录业务逻辑</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@Service("HistoryLogisticsCompanyService")
public class HistoryLogisticsCompanyService {
	@Autowired
	private HistoryLogisticsCompanyRepository historyLogisticsCompanyRepository;

	/**
	 * 新增物流公司历史记录
	 * @author fcq
	 */
	@Transactional
	public HistoryLogisticsCompany add(HistoryLogisticsCompany entity) {
		historyLogisticsCompanyRepository.save(entity);
		return entity;
	}

	/**
	 * 修改物流公司历史记录
	 * @author fcq
	 */
	@Transactional
	public HistoryLogisticsCompany modify(HistoryLogisticsCompany entity) {
		historyLogisticsCompanyRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除物流公司历史记录
	 * @author fcq
	 */
	@Transactional
	public void deleteById(HistoryLogisticsCompany entity) {
		historyLogisticsCompanyRepository.save(entity);
	}

	/**
	 * 批量删除物流公司历史记录
	 * @author fcq
	 */
	@Transactional
	public void deleteByIdList(List<HistoryLogisticsCompany> infos) {
		historyLogisticsCompanyRepository.saveAll(infos);
	}

	/**
	 * 单个查询物流公司历史记录
	 * @author fcq
	 */
	public HistoryLogisticsCompany getOne(String id){
		return historyLogisticsCompanyRepository.findByIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "物流公司历史记录不存在"));
	}

	/**
	 * 分页查询物流公司历史记录
	 * @author fcq
	 */
	public Page<HistoryLogisticsCompany> page(HistoryLogisticsCompanyQueryRequest queryReq){
		return historyLogisticsCompanyRepository.findAll(
				HistoryLogisticsCompanyWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询物流公司历史记录
	 * @author fcq
	 */
	public List<HistoryLogisticsCompany> list(HistoryLogisticsCompanyQueryRequest queryReq){
		return historyLogisticsCompanyRepository.findAll(HistoryLogisticsCompanyWhereCriteriaBuilder.build(queryReq));
	}
	public HistoryLogisticsCompanyVO wrapperVo(HistoryLogisticsCompany historyLogisticsCompany) {
		if (historyLogisticsCompany != null){
			HistoryLogisticsCompanyVO historyLogisticsCompanyVO = KsBeanUtil.convert(historyLogisticsCompany, HistoryLogisticsCompanyVO.class);
			return historyLogisticsCompanyVO;
		}
		return null;
	}

	/**
	 * 根据会员id查询最新一次选择物流公司 且非自建物流
	 * @param customerId
	 * @return
	 */
	public HistoryLogisticsCompany findByCustomerId(String customerId) {

		return historyLogisticsCompanyRepository.findByByCustomerId(customerId);
	}

	public HistoryLogisticsCompany findByCustomerId(String customerId,Integer logisticsType) {

		return historyLogisticsCompanyRepository.findByByCustomerId(customerId,logisticsType);
	}

	public HistoryLogisticsCompany findByByCustomerIdAndMarketId(String customerId,Integer logisticsType,Long marketId) {

		return historyLogisticsCompanyRepository.findByByCustomerIdAndMarketId(customerId,logisticsType,marketId);
	}

}

