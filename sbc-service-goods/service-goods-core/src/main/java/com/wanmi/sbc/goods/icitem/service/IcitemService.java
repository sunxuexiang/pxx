package com.wanmi.sbc.goods.icitem.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.icitem.IcitemQueryRequest;
import com.wanmi.sbc.goods.bean.vo.IcitemVO;
import com.wanmi.sbc.goods.icitem.model.root.Icitem;
import com.wanmi.sbc.goods.icitem.repository.IcitemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>配送到家业务逻辑</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@Service("IcitemService")
public class IcitemService {
	@Autowired
	private IcitemRepository icitemRepository;

	/**
	 * 新增配送到家
	 * @author lh
	 */
	@Transactional
	public Icitem add(Icitem entity) {
		icitemRepository.save(entity);
		return entity;
	}

	/**
	 * 修改配送到家
	 * @author lh
	 */
	@Transactional
	public Icitem modify(Icitem entity) {
		icitemRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除配送到家
	 * @author lh
	 */
	@Transactional
	public void deleteById(String id) {
		icitemRepository.deleteById(id);
	}

	/**
	 * 批量删除配送到家
	 * @author lh
	 */
	@Transactional
	public void deleteByIdList(List<String> ids) {
		//icitemRepository.deleteByIdList(ids);
	}

	/**
	 * 单个查询配送到家
	 * @author lh
	 */
	public Icitem getOne(String id){
		/*return icitemRepository.findBySku(id)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "配送到家不存在"));*/
		return null;
	}

	/**
	 * 分页查询配送到家
	 * @author lh
	 */
	public Page<Icitem> page(IcitemQueryRequest queryReq){
		return icitemRepository.findAll(
				IcitemWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询配送到家
	 * @author lh
	 */
	public List<Icitem> list(IcitemQueryRequest queryReq){
		return icitemRepository.findAll(IcitemWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author lh
	 */
	public IcitemVO wrapperVo(Icitem icitem) {
		if (icitem != null){
			IcitemVO icitemVO = KsBeanUtil.convert(icitem, IcitemVO.class);
			return icitemVO;
		}
		return null;
	}
}

