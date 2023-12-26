package com.wanmi.sbc.customer.invitationhistorysummary.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.customer.invitationhistorysummary.repository.InvitationHistorySummaryRepository;
import com.wanmi.sbc.customer.invitationhistorysummary.model.root.InvitationHistorySummary;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryQueryRequest;
import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>邀新历史汇总计表业务逻辑</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@Service("InvitationHistorySummaryService")
public class InvitationHistorySummaryService {
	@Autowired
	private InvitationHistorySummaryRepository invitationHistorySummaryRepository;

	/**
	 * 新增邀新历史汇总计表
	 * @author fcq
	 */
	@Transactional
	public InvitationHistorySummary add(InvitationHistorySummary entity) {
		invitationHistorySummaryRepository.save(entity);
		return entity;
	}

	@Transactional
	public void batchAdd(List<InvitationHistorySummary> historySummaries ) {
		invitationHistorySummaryRepository.saveAll(historySummaries);

	}
	/**
	 * 修改邀新历史汇总计表
	 * @author fcq
	 */
	@Transactional
	public InvitationHistorySummary modify(InvitationHistorySummary entity) {
		invitationHistorySummaryRepository.save(entity);
		return entity;
	}



	/**
	 * 单个查询邀新历史汇总计表
	 * @author fcq
	 */
	public InvitationHistorySummary getOne(String employeeId){
		Optional<InvitationHistorySummary> byEmployeeId = invitationHistorySummaryRepository.findByEmployeeId(employeeId);
		if (byEmployeeId.isPresent()){
			return byEmployeeId.get();
		}
		return new InvitationHistorySummary();
	}

	/**
	 * 分页查询邀新历史汇总计表
	 * @author fcq
	 */
	public Page<InvitationHistorySummary> page(InvitationHistorySummaryQueryRequest queryReq){
		return invitationHistorySummaryRepository.findAll(
				InvitationHistorySummaryWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询邀新历史汇总计表
	 * @author fcq
	 */
	public List<InvitationHistorySummary> list(InvitationHistorySummaryQueryRequest queryReq){
		return invitationHistorySummaryRepository.findAll(InvitationHistorySummaryWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author fcq
	 */
	public InvitationHistorySummaryVO wrapperVo(InvitationHistorySummary invitationHistorySummary) {
		if (invitationHistorySummary != null){
			InvitationHistorySummaryVO invitationHistorySummaryVO = KsBeanUtil.convert(invitationHistorySummary, InvitationHistorySummaryVO.class);
			return invitationHistorySummaryVO;
		}
		return null;
	}
}

