package com.wanmi.sbc.goods.lastgoodswrite.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.goods.lastgoodswrite.repository.LastGoodsWriteRepository;
import com.wanmi.sbc.goods.lastgoodswrite.model.root.LastGoodsWrite;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteQueryRequest;
import com.wanmi.sbc.goods.bean.vo.LastGoodsWriteVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>用户最后一次商品记录业务逻辑</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@Service("LastGoodsWriteService")
public class LastGoodsWriteService {
	@Autowired
	private LastGoodsWriteRepository lastGoodsWriteRepository;

	/**
	 * 新增用户最后一次商品记录
	 * @author 费传奇
	 */
	@Transactional
	public LastGoodsWrite add(LastGoodsWrite entity) {
		lastGoodsWriteRepository.save(entity);
		return entity;
	}

	/**
	 * 修改用户最后一次商品记录
	 * @author 费传奇
	 */
	@Transactional
	public LastGoodsWrite modify(LastGoodsWrite entity) {
		lastGoodsWriteRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除用户最后一次商品记录
	 * @author 费传奇
	 */
	@Transactional
	public void deleteById(Long id) {
		lastGoodsWriteRepository.deleteById(id);
	}



	/**
	 * 单个查询用户最后一次商品记录
	 * @author 费传奇
	 */
	public LastGoodsWrite getOne(Long id){
		return lastGoodsWriteRepository.findById(id)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "用户最后一次商品记录不存在"));
	}

	/**
	 * 分页查询用户最后一次商品记录
	 * @author 费传奇
	 */
	public Page<LastGoodsWrite> page(LastGoodsWriteQueryRequest queryReq){
		return lastGoodsWriteRepository.findAll(
				LastGoodsWriteWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询用户最后一次商品记录
	 * @author 费传奇
	 */
	public List<LastGoodsWrite> list(LastGoodsWriteQueryRequest queryReq){
		return lastGoodsWriteRepository.findAll(LastGoodsWriteWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author 费传奇
	 */
	public LastGoodsWriteVO wrapperVo(LastGoodsWrite lastGoodsWrite) {
		if (lastGoodsWrite != null){
			LastGoodsWriteVO lastGoodsWriteVO = KsBeanUtil.convert(lastGoodsWrite, LastGoodsWriteVO.class);
			return lastGoodsWriteVO;
		}
		return null;
	}
}

