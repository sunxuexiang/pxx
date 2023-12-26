package com.wanmi.sbc.customer.workorderdetail.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.request.customer.CustomerForErpRequest;
import com.wanmi.sbc.customer.api.request.workorderdetail.WorkOrderDetailQueryRequest;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.enums.PointsServiceType;
import com.wanmi.sbc.customer.bean.vo.WorkOrderDetailVO;
import com.wanmi.sbc.customer.common.OperationLogMq;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.mq.ProducerService;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import com.wanmi.sbc.customer.parentcustomerrela.service.ParentCustomerRelaService;
import com.wanmi.sbc.customer.points.model.root.CustomerPointsDetail;
import com.wanmi.sbc.customer.points.service.CustomerPointsDetailService;
import com.wanmi.sbc.customer.service.CustomerService;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import com.wanmi.sbc.customer.workorder.service.WorkOrderService;
import com.wanmi.sbc.customer.workorderdetail.model.root.WorkOrderDetail;
import com.wanmi.sbc.customer.workorderdetail.repository.WorkOrderDetailRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>工单明细业务逻辑</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@Service("WorkOrderDetailService")
public class WorkOrderDetailService {
	@Autowired
	private WorkOrderDetailRepository workOrderDetailRepository;

	@Autowired
	private WorkOrderService workOrderService;
	@Autowired
	private ParentCustomerRelaService parentCustomerRelaService;
	@Autowired
	private CustomerService customerService;

	@Autowired
	private OperationLogMq operationLogMq;

	@Autowired
	private ProducerService producerService;

	@Autowired
	private CustomerPointsDetailService customerPointsDetailService;


	/**
	 * 功能描述: 账号合并，目前合并积分，信用代码，修改被合并账号的enterprise_status_xyy状态位
	 * 〈〉
	 * @Param: [parentCustomerRela, operator, workOrderId]
	 * @Return: void
	 * @Author: yxb
	 * @Date: 2020/5/27 14:38
	 */
	@LcnTransaction
	@Transactional
	public List<String> mergeAccount(ParentCustomerRela parentCustomerRela, Operator operator,String workOrderId){
		Customer parent = customerService.findById(parentCustomerRela.getParentId());
		if (StringUtils.isNotBlank(parent.getParentCustomerId())){
			throw new SbcRuntimeException(CustomerErrorCode.CHILD_ACCOUNT_WIHTOUT_EDIT,"主账号不合法");
		}
		//查询被合并账号是否有子账号
		List<ParentCustomerRela> allById = parentCustomerRelaService.findAllById(parentCustomerRela.getCustomerId());
		List<String> childCustomerIdList=new ArrayList<>(20);
		//积分变动表
		List<CustomerPointsDetail> pointsDetailList = new ArrayList<>(20);
		//没有子账号
		if (CollectionUtils.isEmpty(allById)){
			Customer child=customerService.findById(parentCustomerRela.getCustomerId());
			childCustomerIdList.add(child.getCustomerId());
			//更新主账户customer表
			customerService.updateParentMergeAccount(parent.getCustomerId(), child.getPointsAvailable()==null?0L:child.getPointsAvailable());
			//组长子主账号积分变动记录
			if (child.getPointsAvailable()!=null&&child.getPointsAvailable()>0){
				CustomerPointsDetail parentDetail = getCustomerPointsDetail(parent, child.getPointsAvailable()
						, OperateType.GROWTH, parent.getPointsAvailable() + child.getPointsAvailable());
				pointsDetailList.add(parentDetail);

				CustomerPointsDetail childDetail = getCustomerPointsDetail(child, child.getPointsAvailable(), OperateType.DEDUCT, 0L);
				pointsDetailList.add(childDetail);
			}
			if (!EnterpriseCheckState.CHECKED.equals(child.getEnterpriseCheckState())){
				child.setCustomerErpId(UUIDUtil.erpTcConstantsId());
			}
			child.setPointsAvailable(0L);
			child.setSocialCreditCode(parent.getSocialCreditCode());
			child.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
			child.setParentCustomerId(parentCustomerRela.getParentId());
			child.setBusinessLicenseUrl(parent.getBusinessLicenseUrl());
			child.setCustomerRegisterType(parent.getCustomerRegisterType());
			child.setEnterpriseName(parent.getEnterpriseName());
			customerService.update(child);
			//新增或修改联合表
			parentCustomerRelaService.add(parentCustomerRela);
		}else {//有子账号
			List<String> childrenId = allById.stream().map(ParentCustomerRela::getCustomerId).collect(Collectors.toList());
			childrenId.add(parentCustomerRela.getCustomerId());
			childCustomerIdList.addAll(childrenId);
			List<Customer> children = customerService.findByCustomerIdInAndDelFlag(childrenId);
			long result=0L;
			for (Customer inner: children){
				result=result+(inner.getPointsAvailable()==null?0L:inner.getPointsAvailable());
				//组装子账号积分变动记录
				if (inner.getPointsAvailable() != null && inner.getPointsAvailable() > 0) {
					CustomerPointsDetail childDetail = getCustomerPointsDetail(inner, inner.getPointsAvailable(), OperateType.DEDUCT, 0L);
					pointsDetailList.add(childDetail);
				}

				String customerErpId = UUIDUtil.erpTcConstantsId();
				//更新子账户
				inner.setPointsAvailable(0L);
				inner.setSocialCreditCode(parent.getSocialCreditCode());
				inner.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
				inner.setParentCustomerId(parentCustomerRela.getParentId());
				if (!EnterpriseCheckState.CHECKED.equals(inner.getEnterpriseCheckState())){
					inner.setCustomerErpId(UUIDUtil.erpTcConstantsId());
				}
				inner.setBusinessLicenseUrl(parent.getBusinessLicenseUrl());
				inner.setCustomerRegisterType(parent.getCustomerRegisterType());
				inner.setEnterpriseName(parent.getEnterpriseName());
				customerService.update(inner);
				//修改中间表的记录
				producerService.addAndFlushErpCustomer(CustomerForErpRequest.builder()
						.customerAccount(inner.getCustomerAccount())
						.customerRegisterType(parent.getCustomerRegisterType())
						.enterpriseStatusXyy(parent.getEnterpriseStatusXyy())
						.customerErpId(customerErpId)
						.build());
			}
			//组装主账号积分变动记录
			if (result>0){
				CustomerPointsDetail parentDetail = getCustomerPointsDetail(parent,
						result , OperateType.GROWTH, result+ parent.getPointsAvailable());
				pointsDetailList.add(parentDetail);
			}
			//批量更新联合表
			parentCustomerRelaService.updateParentId(childrenId,parent.getCustomerId());
			//新增联合表(本身为主账号的改为子账号需要单独新增中间表)
			parentCustomerRelaService.add(parentCustomerRela);
			//更新主账户customer表
			customerService.updateParentMergeAccount(parent.getCustomerId(), result);
		}
		//新增积分记录变动
		customerPointsDetailService.saveAll(pointsDetailList);
		//更新工单状态位
		workOrderService.updateMergeFlagById(workOrderId);

		this.operationLogMq.convertAndSend(operator, "账号合并", "账号合并修改");
		return 	childCustomerIdList;

	}

	/**
	 * 功能描述: 计数处理工单数
	 */
	public Map<String,Long> countGroupByWorOrderId(List<String> workOrderIds){
		List<Object> allByWorkId = workOrderDetailRepository.findAllByWorkId(workOrderIds);
		return KsBeanUtil.convert(allByWorkId, String.class).stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}



	@LcnTransaction
	@Transactional
	public List<String> bindAccount(List<String> childCustomer,String parenCustomer){
		Customer parent = customerService.findById(parenCustomer);
		if (StringUtils.isNotBlank(parent.getParentCustomerId())){
			throw new SbcRuntimeException(CustomerErrorCode.CHILD_ACCOUNT_WIHTOUT_EDIT,"主账号不合法");
		}
		if (childCustomer.contains(parenCustomer)){
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"主账号与子账号重复");
		}
        List<WorkOrder> workOrders = workOrderService.queryMergeStatus(DefaultFlag.NO);
		if (CollectionUtils.isNotEmpty(workOrders)){
            Optional<WorkOrder> first = workOrders.stream().filter(param -> childCustomer.contains(param.getRegistedCustomerId())
                    || childCustomer.contains(param.getApprovalCustomerId())).findFirst();
            if (first.isPresent()){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"存在子账号处于工单处理中");
            }

        }
        List<Customer> byCustomerIdIn = customerService.findByCustomerIdIn(childCustomer);
		List<Customer> childListUpdate=new ArrayList<>(20);
		//积分变动表
		List<CustomerPointsDetail> pointsDetailList = new ArrayList<>(20);
		List<ParentCustomerRela> parentCustomerRelaList=new ArrayList<>(20);
		long result=0L;
		for (Customer inner: byCustomerIdIn){
			if (StringUtils.isNotBlank(inner.getParentCustomerId())&&inner.getParentCustomerId().equals(parenCustomer)){
				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"存在子账号已关联该主账号");
			}
			//查看是否是主账号
			if (StringUtils.isBlank(inner.getParentCustomerId())){
				//查询被合并账号是否有子账号
				List<ParentCustomerRela> childAccount = parentCustomerRelaService.findAllById(inner.getCustomerId());
				if (CollectionUtils.isNotEmpty(childAccount)){
					List<Customer> children = customerService.findByCustomerIdInAndDelFlag(childAccount.stream()
							.map(ParentCustomerRela::getCustomerId).collect(Collectors.toList()));
					for (Customer child:children){
						childCustomer.add(child.getCustomerId());
						parentCustomerRelaList.add(ParentCustomerRela.builder()
								.customerId(child.getCustomerId()).parentId(parent.getCustomerId()).build());
						result=result+(child.getPointsAvailable()==null?0L:child.getPointsAvailable());
						//组装子账号积分变动记录
						if (child.getPointsAvailable() != null && child.getPointsAvailable() > 0) {
							CustomerPointsDetail childDetail = getCustomerPointsDetail(child, child.getPointsAvailable(), OperateType.DEDUCT, 0L);
							pointsDetailList.add(childDetail);
						}
						child.setPointsAvailable(0L);
						child.setSocialCreditCode(parent.getSocialCreditCode());
						child.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
						child.setParentCustomerId(parenCustomer);
						if (!EnterpriseCheckState.CHECKED.equals(child.getEnterpriseCheckState())){
							child.setCustomerErpId(UUIDUtil.erpTcConstantsId());
						}
						child.setBusinessLicenseUrl(parent.getBusinessLicenseUrl());
						child.setCustomerRegisterType(parent.getCustomerRegisterType());
						child.setEnterpriseName(parent.getEnterpriseName());

						childListUpdate.add(child);
					}

				}
			}else {
				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"该账号已关联其他账号");
			}
			//组装子账号积分变动记录
			if (inner.getPointsAvailable() != null && inner.getPointsAvailable() > 0) {
				CustomerPointsDetail childDetail = getCustomerPointsDetail(inner, inner.getPointsAvailable(), OperateType.DEDUCT, 0L);
				pointsDetailList.add(childDetail);
			}
			parentCustomerRelaList.add(ParentCustomerRela.builder()
					.customerId(inner.getCustomerId()).parentId(parent.getCustomerId()).build());
			result=result+(inner.getPointsAvailable()==null?0L:inner.getPointsAvailable());
			inner.setPointsAvailable(0L);
			inner.setSocialCreditCode(parent.getSocialCreditCode());
			inner.setEnterpriseStatusXyy(EnterpriseCheckState.CHECKED);
			inner.setParentCustomerId(parenCustomer);
			if (!EnterpriseCheckState.CHECKED.equals(inner.getEnterpriseCheckState())){
				inner.setCustomerErpId(UUIDUtil.erpTcConstantsId());
			}
			inner.setBusinessLicenseUrl(parent.getBusinessLicenseUrl());
			inner.setCustomerRegisterType(parent.getCustomerRegisterType());
			inner.setEnterpriseName(parent.getEnterpriseName());
		}
		//组装主账号积分变动记录
		if (result>0){
			CustomerPointsDetail parentDetail = getCustomerPointsDetail(parent,
					result , OperateType.GROWTH, result+ parent.getPointsAvailable());
			pointsDetailList.add(parentDetail);
		}
		//新增积分记录变动
		if (CollectionUtils.isNotEmpty(pointsDetailList)){
			customerPointsDetailService.saveAll(pointsDetailList);
		}
		//批量更新联合表
		parentCustomerRelaService.saveAll(parentCustomerRelaList);
		//加入主账号的子账号
		if (CollectionUtils.isNotEmpty(childListUpdate)){
			byCustomerIdIn.addAll(childListUpdate);
		}
		//更新子账号
		customerService.updateAll(byCustomerIdIn);
		//更新主账户customer表
		customerService.updateParentMergeAccount(parent.getCustomerId(), result);
		return childCustomer;
	}

	/**
	 * 新增工单明细
	 * @author baijz
	 */
	@Transactional
	public WorkOrderDetail add(WorkOrderDetail entity) {
		workOrderDetailRepository.save(entity);
		return entity;
	}

	/**
	 * 功能描述: <br>
	 * 〈〉
	 * @Param: 新增工单详情，如果状态改成已完成，同步更新工单列表状态
	 * @Return: com.wanmi.sbc.customer.workorderdetail.model.root.WorkOrderDetail
	 * @Author: yxb
	 * @Date: 2020/5/30 19:39
	 */
	@Transactional
	public WorkOrderDetail addAndUpdate(WorkOrderDetail entity){
		entity.setDealTime(LocalDateTime.now());
		if (entity.getStatus().equals(DefaultFlag.YES.toValue())){
			workOrderDetailRepository.save(entity);
			workOrderService.updateStatusById(entity.getWorkOrderId());
		}else {
			workOrderDetailRepository.save(entity);
		}
		return entity;
	}
	/**
	 * 修改工单明细
	 * @author baijz
	 */
	@Transactional
	public WorkOrderDetail modify(WorkOrderDetail entity) {
		workOrderDetailRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除工单明细
	 * @author baijz
	 */
	@Transactional
	public void deleteById(String id) {
		workOrderDetailRepository.deleteById(id);
	}

	/**
	 * 批量删除工单明细
	 * @author baijz
	 */
	@Transactional
	public void deleteByIdList(List<String> ids) {
		ids.forEach(id -> workOrderDetailRepository.deleteById(id));
	}

	/**
	 * 单个查询工单明细
	 * @author baijz
	 */
	public List<WorkOrderDetail> getListById(String id){
		return workOrderDetailRepository.findByWorkOrderIdOrderByDealTimeAsc(id)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "工单明细不存在"));
	}

	/**
	 * 分页查询工单明细
	 * @author baijz
	 */
	public Page<WorkOrderDetail> page(WorkOrderDetailQueryRequest queryReq){
		return workOrderDetailRepository.findAll(
				WorkOrderDetailWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询工单明细
	 * @author baijz
	 */
	public List<WorkOrderDetail> list(WorkOrderDetailQueryRequest queryReq){
		return workOrderDetailRepository.findAll(WorkOrderDetailWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author baijz
	 */
	public WorkOrderDetailVO wrapperVo(WorkOrderDetail workOrderDetail) {
		if (workOrderDetail != null){
			WorkOrderDetailVO workOrderDetailVO = KsBeanUtil.convert(workOrderDetail, WorkOrderDetailVO.class);
			return workOrderDetailVO;
		}
		return null;
	}

	/**
	 * 功能描述: <br> 组装积分详情记录
	 * 〈〉
	 * @Param: point：积分变动多少，pointsAvailable变动后积分
	 * @Return: com.wanmi.sbc.customer.points.model.root.CustomerPointsDetail
	 * @Author: yxb
	 * @Date: 2020/7/30 15:19
	 */
	private CustomerPointsDetail getCustomerPointsDetail(Customer child, Long point, OperateType deduct, long pointsAvailable) {
		CustomerPointsDetail childDetail = new CustomerPointsDetail();
		childDetail.setPoints(point);
		childDetail.setType(deduct);
		childDetail.setOpTime(LocalDateTime.now());
		childDetail.setCustomerAccount(child.getCustomerAccount());
		childDetail.setServiceType(PointsServiceType.MERGE_ACCOUNT);
		childDetail.setCustomerName(child.getCustomerDetail().getCustomerName());
		childDetail.setPointsAvailable(pointsAvailable);
		childDetail.setCustomerId(child.getCustomerId());
		return childDetail;
	}
}

