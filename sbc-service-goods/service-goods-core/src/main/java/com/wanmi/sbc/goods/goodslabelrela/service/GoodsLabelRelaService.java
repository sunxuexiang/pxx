package com.wanmi.sbc.goods.goodslabelrela.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaQueryRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelRelaVO;
import com.wanmi.sbc.goods.goodslabelrela.model.root.GoodsLabelRela;
import com.wanmi.sbc.goods.goodslabelrela.repository.GoodsLabelRelaRepository;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>邀新统计业务逻辑</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@Service("GoodsLabelRelaService")
public class GoodsLabelRelaService {
	@Autowired
	private GoodsLabelRelaRepository goodsLabelRelaRepository;

	@Autowired
	private GoodsRepository goodsRepository;

	/**
	 * 新增邀新统计
	 * @author lvheng
	 */
	@Transactional
	public GoodsLabelRela add(GoodsLabelRela entity) {
		goodsLabelRelaRepository.save(entity);
		return entity;
	}

	/**
	 * 修改邀新统计
	 * @author lvheng
	 */
	@Transactional
	public GoodsLabelRela modify(GoodsLabelRela entity) {
		goodsLabelRelaRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除邀新统计
	 * @author lvheng
	 */
	@Transactional
	public void deleteById(Long id) {
		goodsLabelRelaRepository.deleteById(id);
	}


	/**
	 * 单个查询邀新统计
	 * @author lvheng
	 */
	public GoodsLabelRela getOne(Long id){
		return goodsLabelRelaRepository.findById(id)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "邀新统计不存在"));
	}

	/**
	 * 分页查询邀新统计
	 * @author lvheng
	 */
	public Page<GoodsLabelRela> page(GoodsLabelRelaQueryRequest queryReq){
		return goodsLabelRelaRepository.findAll(
				GoodsLabelRelaWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询邀新统计
	 * @author lvheng
	 */
	public List<GoodsLabelRela> list(GoodsLabelRelaQueryRequest queryReq){
		return goodsLabelRelaRepository.findAll(GoodsLabelRelaWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author lvheng
	 */
	public GoodsLabelRelaVO wrapperVo(GoodsLabelRela goodsLabelRela) {
		if (goodsLabelRela != null){
			GoodsLabelRelaVO goodsLabelRelaVO = KsBeanUtil.convert(goodsLabelRela, GoodsLabelRelaVO.class);
			return goodsLabelRelaVO;
		}
		return null;
	}

	public Optional<List<GoodsLabelRela>> findByLabelId(Long labelId) {
		return goodsLabelRelaRepository.findByLabelId(labelId);
	}

	public void findByGoodsId(String goodsId) {
		goodsLabelRelaRepository.findByGoodsId(goodsId);
	}

	public List<GoodsLabelRela> findByGoodsIds(List<String> goodsIds) {
		Optional<List<GoodsLabelRela>> byGoodsIdIn = goodsLabelRelaRepository.findByGoodsIdIn(goodsIds);
		if (byGoodsIdIn.isPresent()) {
			return byGoodsIdIn.get();
		}else {
			return new ArrayList<>();
		}
	}

	@Transactional(rollbackFor = SbcRuntimeException.class)
	public Integer deleteInGoodsIds(List<String> goodsIds, Long labelId) {
		Integer integer = goodsLabelRelaRepository.deleteByGoodsIdInAndLabelId(goodsIds, labelId);
		// 删除商品对应的标签信息
		GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
		goodsQueryRequest.setGoodsIds(goodsIds);
		List<Goods> goodsList = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
		List<Goods> collect = goodsList.stream()
				.peek(goods -> goods.setLabelIdStr(null))
				.collect(Collectors.toList());
		goodsRepository.saveAll(collect);
		return integer;
	}
}

