package com.wanmi.sbc.customer.workorder.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderQueryRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.WorkOrderVO;
import com.wanmi.sbc.customer.enums.MergeAccountBeforeStatus;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.parentcustomerrela.service.ParentCustomerRelaService;
import com.wanmi.sbc.customer.repository.CustomerRepository;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import com.wanmi.sbc.customer.workorder.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>工单业务逻辑</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@Service("WorkOrderService")
public class WorkOrderService {
	@Autowired
	private WorkOrderRepository workOrderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ParentCustomerRelaService parentCustomerRelaService;

	public WorkOrderVO queryWorkOrder(String workId){
		WorkOrder workOrder = getOne(workId);
		if (!Objects.nonNull(workOrder)){
			throw new SbcRuntimeException();
		}
		WorkOrderVO workOrderVO = wrapperVo(workOrder);
		//查询已注册人信息
		Customer customerAlready = customerRepository.findByCustomerIdAndDelFlag(workOrder.getRegistedCustomerId(), DeleteFlag.NO);
		CustomerVO customerAlreadyVo = KsBeanUtil.convert(customerAlready, CustomerVO.class);
		workOrderVO.setCustomerAlready(customerAlreadyVo);
		return workOrderVO;
	}

	public MergeAccountBeforeStatus getStatus(String id){
		return parentCustomerRelaService.getStateById(id);
	}

	/**
	 * 新增工单
	 * @author baijz
	 */
	@Transactional
	public WorkOrder add(WorkOrder entity) {
		workOrderRepository.save(entity);
		return entity;
	}

	/**
	 * 修改工单合并状态位
	 * @author baijz
	 */
	@Transactional
	public void  updateMergeFlagById(String id){
		workOrderRepository.updateMergeFlagById(id);
	}

	public List<WorkOrder> queryMergeStatus(DefaultFlag accountMergeStatus){
		return workOrderRepository.findAllByAccountMergeStatusAndDelFlag(accountMergeStatus,DeleteFlag.NO);
	}

	/**
	 * 功能描述: 检查customerid是否处于工单处理中

	 */
	public boolean checkWorkOrderBy(String customerId){
		Object o = workOrderRepository.checkCustomerId(customerId);
		if (Objects.isNull(o)){
			return true;
		}else {
			return false;
		}
	}


	/**
	 * 修改工单
	 * @author baijz
	 */
	@Transactional
	public WorkOrder modify(WorkOrder entity) {
		workOrderRepository.save(entity);
		return entity;
	}

	@Transactional
	public Integer updateStatusById(String id){
	 return workOrderRepository.updateStatusById(id);
	}

	/**
	 * 单个删除工单
	 * @author baijz
	 */
	@Transactional
	public void deleteById(WorkOrder entity) {
		workOrderRepository.deleteById(entity.getWorkOrderId());
	}

	/**
	 * 批量删除工单
	 * @author baijz
	 */
	@Transactional
	public void deleteByIdList(List<WorkOrder> infos) {
		workOrderRepository.saveAll(infos);
	}

	/**
	 * 单个查询工单
	 * @author baijz
	 */
	public WorkOrder getOne(String id){
		return workOrderRepository.findByWorkOrderIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "工单不存在"));
	}



	/**
	 * 分页查询工单
	 * @author baijz
	 */
	public Page<WorkOrder> page(WorkOrderQueryRequest queryReq){
		return workOrderRepository.findAll(
				WorkOrderWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询工单
	 * @author baijz
	 */
	public List<WorkOrder> list(WorkOrderQueryRequest queryReq){
		return workOrderRepository.findAll(WorkOrderWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author baijz
	 */
	public WorkOrderVO wrapperVo(WorkOrder workOrder) {
		if (workOrder != null){
			WorkOrderVO workOrderVO = KsBeanUtil.convert(workOrder, WorkOrderVO.class);
			return workOrderVO;
		}
		return null;
	}

	/**
	 * 根据已注册人的Id 和 注册人的Id 查询是否存在未处理的工单
	 * @param customerId
	 * @return
	 */
	public boolean validateCustomerWorkOrder(String customerId){
		int rg = workOrderRepository.countByRegistedCustomerIdAndStatusOrAccountMergeStatus(customerId, DefaultFlag.NO,  DefaultFlag.NO);
		int gt = workOrderRepository.countByApprovalCustomerIdAndStatusOrAccountMergeStatus(customerId, DefaultFlag.NO,  DefaultFlag.NO);
		return rg+gt > 0;
	}
}

