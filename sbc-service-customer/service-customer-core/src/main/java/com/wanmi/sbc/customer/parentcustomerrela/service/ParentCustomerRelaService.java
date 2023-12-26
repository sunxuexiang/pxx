package com.wanmi.sbc.customer.parentcustomerrela.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaQueryRequest;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.customer.enums.MergeAccountBeforeStatus;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import com.wanmi.sbc.customer.parentcustomerrela.repository.ParentCustomerRelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>子主账号关联关系业务逻辑</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@Service("ParentCustomerRelaService")
public class ParentCustomerRelaService {
	@Autowired
	private ParentCustomerRelaRepository parentCustomerRelaRepository;

	/**
	 * 新增子主账号关联关系
	 * @author baijz
	 */
	@Transactional
	public ParentCustomerRela add(ParentCustomerRela entity) {
		parentCustomerRelaRepository.save(entity);
		return entity;
	}

	/**
	 * 根据主账号查询绑定关系
	 * @param id
	 * @return
	 */
	public List<ParentCustomerRela> findAllById(String id){
		return parentCustomerRelaRepository.findAllByParentId(id);
	}

	/**
	 * 功能描述: 批量更新parentId
	 * 〈〉
	 * @Param: [customerList, parentId]
	 * @Return: void
	 * @Author: yxb
	 * @Date: 2020/5/27 14:26
	 */
	@Transactional
	public void updateParentId(List<String> customerId,String parentId){
		parentCustomerRelaRepository.updateParentId(customerId, parentId);
	}



	/**
	 * 修改子主账号关联关系
	 * @author baijz
	 */
	@Transactional
	public ParentCustomerRela modify(ParentCustomerRela entity) {
		parentCustomerRelaRepository.save(entity);
		return entity;
	}

	@Transactional
	public void saveAll(List<ParentCustomerRela> entity){
		parentCustomerRelaRepository.saveAll(entity);
	}

	/**
	 * 根据会员的Id删除主子账号
	 * @param customerId
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void deleteByParentIdOrCustomerId(String customerId){
		parentCustomerRelaRepository.deleteAllByParentId(customerId);
		parentCustomerRelaRepository.deleteById(customerId);
	}

	/**
	 * 单个删除子主账号关联关系
	 * @author baijz
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public void deleteById(String id) {
		parentCustomerRelaRepository.deleteById(id);
	}

	/**
	 * 批量删除子主账号关联关系
	 * @author baijz
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public void deleteByIdList(List<String> ids) {
		ids.forEach(i->parentCustomerRelaRepository.deleteById(i));
	}

	/**
	 * 单个查询子主账号关联关系
	 * @author baijz
	 */
	public ParentCustomerRela getOne(String id){
		return parentCustomerRelaRepository.findById(id)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "子主账号关联关系不存在"));
	}

	/**
	 * 分页查询子主账号关联关系
	 * @author baijz
	 */
	public Page<ParentCustomerRela> page(ParentCustomerRelaQueryRequest queryReq){
		return parentCustomerRelaRepository.findAll(
				ParentCustomerRelaWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询子主账号关联关系
	 * @author baijz
	 */
	public List<ParentCustomerRela> list(ParentCustomerRelaQueryRequest queryReq){
		return parentCustomerRelaRepository.findAll(ParentCustomerRelaWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author baijz
	 */
	public ParentCustomerRelaVO wrapperVo(ParentCustomerRela parentCustomerRela) {
		if (parentCustomerRela != null){
			ParentCustomerRelaVO parentCustomerRelaVO = KsBeanUtil.convert(parentCustomerRela, ParentCustomerRelaVO.class);
			return parentCustomerRelaVO;
		}
		return null;
	}

	/**
	 * 功能描述: 验证是否存在关联的主账号
	 * 〈〉
	 * @Param: [id]
	 * @Return: com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela
	 * @Author: yxb
	 * @Date: 2020/5/27 9:41
	 */
	public Object  findFirstByCustomerId(String id){
		return parentCustomerRelaRepository.findByCustomerIdStatus(id);
	}

	/**
	 * 功能描述:  验证是否存在关联的子账号
	 * 〈〉
	 * @Param: [id]
	 * @Return: com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela
	 * @Author: yxb
	 * @Date: 2020/5/27 9:42
	 */
	public  Object findFirstByParentId(String id){
		return parentCustomerRelaRepository.findByParentIdStatus(id);
	}

	public MergeAccountBeforeStatus getStateById(String id) {
		if (!Objects.isNull(findFirstByCustomerId(id))) {
			return MergeAccountBeforeStatus.HAVE_PARENT;
		}
		if ((!Objects.isNull(findFirstByParentId(id)))) {
			return MergeAccountBeforeStatus.HAVE_CHILD;
		}
		return MergeAccountBeforeStatus.NONE;
	}

	public ParentCustomerRela findByCustomerId(String id){
		return parentCustomerRelaRepository.findByCustomerId(id);
	}
	public List<ParentCustomerRela> findByCustomerIdOrParentId(String customerId ,String parentId){
		return parentCustomerRelaRepository.findByCustomerIdOrParentId(customerId,parentId);
	}
}

