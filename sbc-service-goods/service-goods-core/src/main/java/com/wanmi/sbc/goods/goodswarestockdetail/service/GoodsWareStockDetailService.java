package com.wanmi.sbc.goods.goodswarestockdetail.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailQueryRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockDetailVO;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import com.wanmi.sbc.goods.goodswarestockdetail.repository.GoodsWareStockDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p> 库存明细表业务逻辑</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@Service("GoodsWareStockDetailService")
public class GoodsWareStockDetailService {
	@Autowired
	private GoodsWareStockDetailRepository goodsWareStockDetailRepository;

	/**
	 * 新增 库存明细表
	 * @author zhangwenchang
	 */
	@Transactional
	public GoodsWareStockDetail add(GoodsWareStockDetail entity) {
		goodsWareStockDetailRepository.save(entity);
		return entity;
	}

	/**
	 * 新增 库存明细表
	 * @author zhangwenchang
	 */
	@Transactional
	@LcnTransaction
	public void addList(List<GoodsWareStockDetail> entity) {
		goodsWareStockDetailRepository.saveAll(entity);
	}

	/**
	 * 修改 库存明细表
	 * @author zhangwenchang
	 */
	@Transactional
	public GoodsWareStockDetail modify(GoodsWareStockDetail entity) {
		goodsWareStockDetailRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除 库存明细表
	 * @author zhangwenchang
	 */
	@Transactional
	public void deleteById(GoodsWareStockDetail entity) {
		goodsWareStockDetailRepository.save(entity);
	}

	/**
	 * 批量删除 库存明细表
	 * @author zhangwenchang
	 */
	@Transactional
	public void deleteByIdList(List<GoodsWareStockDetail> infos) {
		goodsWareStockDetailRepository.saveAll(infos);
	}

	/**
	 * 根据分仓库存表id批量删除 库存明细表
	 * @author zhangwenchang
	 */
	@Transactional
	public void deleteByGoodsWareStockIdList(List<Long> goodsWareStockIdList) {
		goodsWareStockDetailRepository.deleteByGoodsWareStockIdList(goodsWareStockIdList);
	}


	/**
	 * 单个查询 库存明细表
	 * @author zhangwenchang
	 */
	public GoodsWareStockDetail getOne(Long id){
		return goodsWareStockDetailRepository.findByIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, " 库存明细表不存在"));
	}

	/**
	 * 分页查询 库存明细表
	 * @author zhangwenchang
	 */
	public Page<GoodsWareStockDetail> page(GoodsWareStockDetailQueryRequest queryReq){
		return goodsWareStockDetailRepository.findAll(
				GoodsWareStockDetailWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询 库存明细表
	 * @author zhangwenchang
	 */
	public List<GoodsWareStockDetail> list(GoodsWareStockDetailQueryRequest queryReq){
		return goodsWareStockDetailRepository.findAll(GoodsWareStockDetailWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author zhangwenchang
	 */
	public GoodsWareStockDetailVO wrapperVo(GoodsWareStockDetail goodsWareStockDetail) {
		if (goodsWareStockDetail != null){
			GoodsWareStockDetailVO goodsWareStockDetailVO = KsBeanUtil.convert(goodsWareStockDetail, GoodsWareStockDetailVO.class);
			return goodsWareStockDetailVO;
		}
		return null;
	}
}

