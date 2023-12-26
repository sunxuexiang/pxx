package com.wanmi.sbc.goods.stockoutmanage.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.goods.stockoutmanage.repository.StockoutManageRepository;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageQueryRequest;
import com.wanmi.sbc.goods.bean.vo.StockoutManageVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.List;

/**
 * <p>缺货管理业务逻辑</p>
 * @author tzx
 * @date 2020-05-27 11:37:26
 */
@Service("StockoutManageService")
public class StockoutManageService {
	@Autowired
	private StockoutManageRepository stockoutManageRepository;

	/**
	 * 新增缺货管理
	 * @author tzx
	 */
	@Transactional
	public StockoutManage add(StockoutManage entity) {
		stockoutManageRepository.save(entity);
		return entity;
	}

	/**
	 * 修改缺货管理
	 * @author tzx
	 */
	@Transactional
	public StockoutManage modify(StockoutManage entity) {
		stockoutManageRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除缺货管理
	 * @author tzx
	 */
	@Transactional
	public void deleteById(StockoutManage entity) {
		stockoutManageRepository.save(entity);
	}

	/**
	 * 批量删除缺货管理
	 * @author tzx
	 */
	@Transactional
	public void deleteByIdList(List<StockoutManage> infos) {
		stockoutManageRepository.saveAll(infos);
	}

	/**
	 * 单个查询缺货管理
	 * @author tzx
	 */
	public StockoutManage getOne(String id){
		return stockoutManageRepository.findByStockoutIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "缺货管理不存在"));
	}

	/**
	 * 分页查询缺货管理
	 * @author tzx
	 */
	public Page<StockoutManage> page(StockoutManageQueryRequest queryReq){
		return stockoutManageRepository.findAll(
				StockoutManageWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	@Transactional
	public void upddateFlagByIdList(List<String> ids){
		stockoutManageRepository.upddateFlagByIdList(ids);
	}
	/**
	 * 列表查询缺货管理
	 * @author tzx
	 */
	public List<StockoutManage> list(StockoutManageQueryRequest queryReq){
		return stockoutManageRepository.findAll(StockoutManageWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author tzx
	 */
	public StockoutManageVO wrapperVo(StockoutManage stockoutManage) {
		if (stockoutManage != null){
			StockoutManageVO stockoutManageVO = KsBeanUtil.convert(stockoutManage, StockoutManageVO.class);
			return stockoutManageVO;
		}
		return null;
	}
}

