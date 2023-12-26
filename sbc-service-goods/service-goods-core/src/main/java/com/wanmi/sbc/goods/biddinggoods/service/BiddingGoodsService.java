package com.wanmi.sbc.goods.biddinggoods.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.goods.biddinggoods.repository.BiddingGoodsRepository;
import com.wanmi.sbc.goods.biddinggoods.model.root.BiddingGoods;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsQueryRequest;
import com.wanmi.sbc.goods.bean.vo.BiddingGoodsVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.List;

/**
 * <p>竞价商品业务逻辑</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@Service("BiddingGoodsService")
public class BiddingGoodsService {
	@Autowired
	private BiddingGoodsRepository biddingGoodsRepository;

	/**
	 * 新增竞价商品
	 * @author baijz
	 */
	@Transactional
	public BiddingGoods add(BiddingGoods entity) {
		biddingGoodsRepository.save(entity);
		return entity;
	}

	/**
	 * 修改竞价商品
	 * @author baijz
	 */
	@Transactional
	public BiddingGoods modify(BiddingGoods entity) {
		biddingGoodsRepository.save(entity);
		return entity;
	}

	@Transactional
	public void saveAll(List<BiddingGoods> infos){
		biddingGoodsRepository.saveAll(infos);
	}

	/**
	 * 单个删除竞价商品
	 * @author baijz
	 */
	@Transactional
	public void deleteById(BiddingGoods entity) {
		biddingGoodsRepository.save(entity);
	}

	/**
	 * 批量删除竞价商品
	 * @author baijz
	 */
	@Transactional
	public void deleteByIdList(List<BiddingGoods> infos) {
		biddingGoodsRepository.saveAll(infos);
	}

	/**
	 * 单个查询竞价商品
	 * @author baijz
	 */
	public BiddingGoods getOne(String id){
		return biddingGoodsRepository.findByBiddingGoodsIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "竞价商品不存在"));
	}

	/**
	 * 分页查询竞价商品
	 * @author baijz
	 */
	public Page<BiddingGoods> page(BiddingGoodsQueryRequest queryReq){
		return biddingGoodsRepository.findAll(
				BiddingGoodsWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询竞价商品
	 * @author baijz
	 */
	public List<BiddingGoods> list(BiddingGoodsQueryRequest queryReq){
		return biddingGoodsRepository.findAll(BiddingGoodsWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author baijz
	 */
	public BiddingGoodsVO wrapperVo(BiddingGoods biddingGoods) {
		if (biddingGoods != null){
			BiddingGoodsVO biddingGoodsVO = KsBeanUtil.convert(biddingGoods, BiddingGoodsVO.class);
			return biddingGoodsVO;
		}
		return null;
	}
}

