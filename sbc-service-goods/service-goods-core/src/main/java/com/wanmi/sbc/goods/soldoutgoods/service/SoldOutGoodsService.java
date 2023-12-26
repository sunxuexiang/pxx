package com.wanmi.sbc.goods.soldoutgoods.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.goods.soldoutgoods.repository.SoldOutGoodsRepository;
import com.wanmi.sbc.goods.soldoutgoods.model.root.SoldOutGoods;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsQueryRequest;
import com.wanmi.sbc.goods.bean.vo.SoldOutGoodsVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>下架商品表业务逻辑</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@Service("SoldOutGoodsService")
public class SoldOutGoodsService {
	@Autowired
	private SoldOutGoodsRepository soldOutGoodsRepository;

	/**
	 * 新增下架商品表
	 * @author lvheng
	 */
	@Transactional
	public SoldOutGoods add(SoldOutGoods entity) {
		soldOutGoodsRepository.save(entity);
		return entity;
	}




	/**
	 * 批量删除下架商品表
	 * @author lvheng
	 */
	@Transactional
	public void deleteByIdList(List<String> ids) {
		soldOutGoodsRepository.deleteByGoodsIds(ids);
	}


	/**
	 * 列表查询下架商品表
	 * @author lvheng
	 */
	public List<SoldOutGoods> list(SoldOutGoodsQueryRequest queryReq){
		return soldOutGoodsRepository.findAll(SoldOutGoodsWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author lvheng
	 */
	public SoldOutGoodsVO wrapperVo(SoldOutGoods soldOutGoods) {
		if (soldOutGoods != null){
			SoldOutGoodsVO soldOutGoodsVO = KsBeanUtil.convert(soldOutGoods, SoldOutGoodsVO.class);
			return soldOutGoodsVO;
		}
		return null;
	}
}

