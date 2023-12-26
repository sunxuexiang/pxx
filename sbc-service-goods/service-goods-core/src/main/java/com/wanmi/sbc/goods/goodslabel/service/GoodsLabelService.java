package com.wanmi.sbc.goods.goodslabel.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelQueryRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodslabel.repository.GoodsLabelRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>导航配置业务逻辑</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@Service("GoodsLabelService")
public class GoodsLabelService {
	@Autowired
	private GoodsLabelRepository goodsLabelRepository;

	/**
	 * 新增导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public GoodsLabel add(GoodsLabel entity) {
		final Integer MAX_LABEL_NUM = 20;
		Integer count = goodsLabelRepository.countByDelFlag(DeleteFlag.NO);
		if (count >= MAX_LABEL_NUM) {
			throw new SbcRuntimeException("K-180005");
		}

		Integer nameCount = goodsLabelRepository.countByNameAndDelFlag(entity.getName(), DeleteFlag.NO);
		if (nameCount > 0) {
			throw new SbcRuntimeException("K-180006");
		}
		Optional<Integer> maxSort = goodsLabelRepository.findMaxSort();
		if (maxSort.isPresent()) {
			Integer sort = maxSort.get();
			entity.setSort(++sort);
		} else {
			entity.setSort(1);
		}


		entity.setCreateTime(LocalDateTime.now());
		goodsLabelRepository.save(entity);
		return entity;
	}

	/**
	 * 修改导航配置
	 *
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public GoodsLabel modify(GoodsLabel entity) {
		GoodsLabel goodsLabelOld = goodsLabelRepository.getOne(entity.getId());

		Integer nameCount = goodsLabelRepository.countByNameAndDelFlag(entity.getName(), DeleteFlag.NO);
		if (!goodsLabelOld.getName().equals(entity.getName()) && nameCount > 0) {
			throw new SbcRuntimeException("K-180006");
		}
		GoodsLabel goodsLabelNew = new GoodsLabel();
		//空值转换后深拷贝
		KsBeanUtil.copyPropertiesIgnoreNull(goodsLabelOld, entity);
		KsBeanUtil.copyProperties(goodsLabelOld,goodsLabelNew);
		goodsLabelNew.setUpdateTime(LocalDateTime.now());
		goodsLabelRepository.save(goodsLabelNew);
		return goodsLabelNew;
	}

	/**
	 * 修改导航顺序
	 *
	 * @param goodsLabels
	 * @return
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void modifySort(List<GoodsLabelVO> goodsLabels) {
		for (GoodsLabelVO goodsLabel : goodsLabels) {
			modify(KsBeanUtil.convert(goodsLabel, GoodsLabel.class));
		}
	}



	/**
	 * 单个删除导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void deleteById(GoodsLabel entity) {
		GoodsLabel goodsLabel = getOne(entity.getId());
		GoodsLabelQueryRequest goodsLabelQueryRequest = new GoodsLabelQueryRequest();
		List<GoodsLabel> list = list(goodsLabelQueryRequest);
		//排序比当前大的全部减一
		list = list.stream().filter(item -> item.getSort() > goodsLabel.getSort()).collect(Collectors.toList());
		list.stream().forEach(item -> item.setSort(item.getSort() - 1));

		//物理删除
		goodsLabelRepository.delete(goodsLabel);
		if (CollectionUtils.isNotEmpty(list)) {
			goodsLabelRepository.saveAll(list);
		}

	}

	/**
	 * 批量删除导航配置
	 * @author lvheng
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void deleteByIdList(List<GoodsLabel> infos) {
		goodsLabelRepository.saveAll(infos);
	}

	/**
	 * 单个查询导航配置
	 * @author lvheng
	 */
	public GoodsLabel getOne(Long id){
		return goodsLabelRepository.findByIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "导航配置不存在"));
	}

	/**
	 * 列表查询导航配置
	 * @author lvheng
	 */
	public List<GoodsLabel> list(GoodsLabelQueryRequest queryReq){
		return goodsLabelRepository.findAll(GoodsLabelWhereCriteriaBuilder.build(queryReq));
	}


	public List<GoodsLabel> findTopByDelFlag(){
		return goodsLabelRepository.findTopByDelFlag();
	}

	/**
	 * 将实体包装成VO
	 * @author lvheng
	 */
	public GoodsLabelVO wrapperVo(GoodsLabel goodsLabel) {
		if (goodsLabel != null){
			GoodsLabelVO goodsLabelVO = KsBeanUtil.convert(goodsLabel, GoodsLabelVO.class);
			return goodsLabelVO;
		}
		return null;
	}
}

