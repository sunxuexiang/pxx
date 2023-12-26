package com.wanmi.sbc.goods.stockoutdetail.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.stockoutdetail.StockoutDetailQueryRequest;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockouDetailVerifyGoodInfoIdResponse;
import com.wanmi.sbc.goods.bean.dto.StockoutDetailDTO;
import com.wanmi.sbc.goods.bean.dto.StockoutManageDTO;
import com.wanmi.sbc.goods.bean.vo.StockoutDetailVO;
import com.wanmi.sbc.goods.bean.vo.StockoutDetailVerifyVO;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRepository;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
import com.wanmi.sbc.goods.stockoutdetail.repository.StockoutDetailRepository;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import com.wanmi.sbc.goods.stockoutmanage.repository.StockoutManageRepository;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>缺货管理业务逻辑</p>
 * @author tzx
 * @date 2020-05-27 11:37:12
 */
@Service("StockoutDetailService")
public class StockoutDetailService {
	@Autowired
	private StockoutDetailRepository stockoutDetailRepository;

	@Autowired
	private StockoutManageRepository stockoutManageRepository;
	@Autowired
	private GoodsInfoRepository goodsInfoRepository;
	@Autowired
	private GoodsBrandRepository goodsBrandRepository;


	/**
	 * 新增缺货管理
	 * @author tzx
	 */
	@Transactional
	public StockoutDetail add(StockoutDetailDTO stockoutDetailDTO) {

	/*	List<StockoutDetail> verifyList = stockoutDetailRepository.findAll(StockoutDetailWhereCriteriaBuilder.
				build(StockoutDetailQueryRequest.builder().
						customerId(stockoutDetailDTO.getCustomerId()).
						goodsInfoId(stockoutDetailDTO.getGoodsInfoId()).
						cityCode(stockoutDetailDTO.getCityCode())
						.delFlag(DeleteFlag.NO).build()));*/
		/*// 如果记录过一次sku的话则直接返回
		if (!CollectionUtils.isEmpty(verifyList)){
			return new StockoutDetail();
		}*/


		StockoutDetail entity= KsBeanUtil.convert(stockoutDetailDTO, StockoutDetail.class);
		// 查询缺货管理列表是否存在该sku 且未补货状态
//		StockoutManage manage = stockoutManageRepository.
//				findByGoodsInfoIdAndReplenishmentFlagAndWareIdAndDelFlag(entity.getGoodsInfoId(), ReplenishmentFlag.NO,stockoutDetailDTO.getWareId(),DeleteFlag.NO);


		StockoutManage manage = null;

		List<StockoutManage> managelsit = stockoutManageRepository.findByGoodsInfoIdAndReplenishmentFlagAndWareIdAndDelFlagAndSoruce(entity.getGoodsInfoId(), stockoutDetailDTO.getWareId());


		if (!CollectionUtils.isEmpty(managelsit)){
			manage=managelsit.stream().filter(v->{
				if (Objects.nonNull(v.getSource()) && v.getSource()==1){
					return true;
				}
				else return false;

			}).findAny().orElse(null);
		}

		if (StringUtils.isEmpty(manage)){
			GoodsInfoQueryRequest goodsInfoQueryRequest = new GoodsInfoQueryRequest();
			goodsInfoQueryRequest.setWareId(stockoutDetailDTO.getWareId());
			List<String> list = new LinkedList<>();
			list.add(entity.getGoodsInfoId());
			goodsInfoQueryRequest.setGoodsInfoIds(list);
			GoodsInfo goodsInfo = goodsInfoRepository.findAll(goodsInfoQueryRequest.getWhereCriteria()).stream().findFirst().orElse(null);
//			GoodsInfo goodsInfo = goodsInfoRepository.getOne(entity.getGoodsInfoId());
			GoodsBrand goodsBrand=new GoodsBrand();
			if (Objects.nonNull(goodsInfo.getBrandId())){
				 goodsBrand = goodsBrandRepository.getOne(goodsInfo.getBrandId());
			}
			StockoutManageDTO build = StockoutManageDTO.builder().brandId(goodsInfo.getBrandId())
					.brandName(StringUtil.isNotBlank(goodsBrand.getBrandName())?goodsBrand.getBrandName():null)
					.stockoutCity(entity.getCityCode())
					.goodsInfoId(entity.getGoodsInfoId()).goodsInfoNo(entity.getGoodsInfoNo())
					.stockoutNum(entity.getStockoutNum()).delFlag(DeleteFlag.NO).
							replenishmentFlag(ReplenishmentFlag.NO)
					.goodsName(goodsInfo.getGoodsInfoName()).storeId(goodsInfo.getStoreId()).
							goodsInfoImg(goodsInfo.getGoodsInfoImg()).build();
			manage = KsBeanUtil.convert(build
					, StockoutManage.class);
			// 为空
			manage.setUpdateTime(LocalDateTime.now());
			manage.setCreateTime(LocalDateTime.now());
			manage.setWareId(stockoutDetailDTO.getWareId());
		}else {
			if (!manage.getStockoutCity().contains(entity.getCityCode())){
				manage.setStockoutCity(manage.getStockoutCity()+","+entity.getCityCode());
			}
			manage.setStockoutNum(manage.getStockoutNum()+entity.getStockoutNum());
			manage.setUpdateTime(LocalDateTime.now());
		}
		stockoutManageRepository.save(manage);
		entity.setStockoutId(manage.getStockoutId());
		entity.setDelFlag(DeleteFlag.NO);
		entity.setCreateTime(LocalDateTime.now());
		stockoutDetailRepository.save(entity);
		return entity;
	}

	@Transactional
	public void deleteByStockOutId(List<String> ids){
		stockoutDetailRepository.deleteByStockoutIdIs(ids);
	}



	/**
	 * 新增缺货管理
	 * @author tzx
	 */
	@LcnTransaction
	@Transactional
	public void addAll(List<StockoutDetailDTO> stockoutDetailDTOList) {
		List<StockoutManage> manageList=new ArrayList<>(20);
		List<StockoutDetail> entityList=new ArrayList<>(20);
		for (StockoutDetailDTO inner: stockoutDetailDTOList){
			/*List<StockoutDetail> verifyList = stockoutDetailRepository.findAll(StockoutDetailWhereCriteriaBuilder.
					build(StockoutDetailQueryRequest.builder().
							customerId(inner.getCustomerId()).
							goodsInfoId(inner.getGoodsInfoId()).
							cityCode(inner.getCityCode())
							.delFlag(DeleteFlag.NO).build()));*/
			/*// 如果记录过一次sku的话则直接返回
			if (!CollectionUtils.isEmpty(verifyList)){
				continue;
			}*/
			StockoutDetail entity= KsBeanUtil.convert(inner, StockoutDetail.class);
			// 查询缺货管理列表是否存在该sku 且未补货状态
			StockoutManage manage = stockoutManageRepository.
					findByGoodsInfoIdAndReplenishmentFlagAndWareIdAndDelFlag(entity.getGoodsInfoId(), ReplenishmentFlag.NO,inner.getWareId(),DeleteFlag.NO);
			if (StringUtils.isEmpty(manage)){
				GoodsInfo goodsInfo = goodsInfoRepository.getOne(entity.getGoodsInfoId());
				GoodsBrand goodsBrand = null;
				if (!StringUtils.isEmpty(goodsInfo.getBrandId())) {
					goodsBrand = goodsBrandRepository.getOne(goodsInfo.getBrandId());
				}
				StockoutManageDTO build = StockoutManageDTO.builder().brandId(goodsInfo.getBrandId())
						.brandName(Objects.nonNull(goodsBrand) && Objects.nonNull(goodsBrand.getBrandName()) ?goodsBrand.getBrandName():null).
								stockoutCity(entity.getCityCode())
						.goodsInfoId(entity.getGoodsInfoId()).goodsInfoNo(entity.getGoodsInfoNo())
						.stockoutNum(entity.getStockoutNum()).delFlag(DeleteFlag.NO).
								replenishmentFlag(ReplenishmentFlag.NO)
						.goodsName(goodsInfo.getGoodsInfoName()).storeId(goodsInfo.getStoreId()).
								goodsInfoImg(goodsInfo.getGoodsInfoImg()).build();
				manage = KsBeanUtil.convert(build
						, StockoutManage.class);
				// 为空
				manage.setUpdateTime(LocalDateTime.now());
				manage.setCreateTime(LocalDateTime.now());
				manage.setWareId(inner.getWareId());
			}else {
				if (!manage.getStockoutCity().contains(entity.getCityCode())){
					manage.setStockoutCity(manage.getStockoutCity()+","+entity.getCityCode());
				}
				manage.setStockoutNum(manage.getStockoutNum()+entity.getStockoutNum());
				manage.setUpdateTime(LocalDateTime.now());
			}
			manageList.add(manage);
			//entity.setStockoutId(manage.getStockoutId());
			entity.setDelFlag(DeleteFlag.NO);
			entity.setCreateTime(LocalDateTime.now());
			entityList.add(entity);
		}
		if (!CollectionUtils.isEmpty(manageList)){
			stockoutManageRepository.saveAll(manageList);
			for (StockoutDetail inner:entityList){
				inner.setStockoutId(manageList.get(entityList.indexOf(inner)).getStockoutId());
			}
			stockoutDetailRepository.saveAll(entityList);
		}
	}

	/**
	 * 修改缺货管理
	 * @author tzx
	 */
	@Transactional
	public StockoutDetail modify(StockoutDetail entity) {
		stockoutDetailRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除缺货管理
	 * @author tzx
	 */
	@Transactional
	public void deleteById(StockoutDetail entity) {
		stockoutDetailRepository.save(entity);
	}

	/**
	 * 批量删除缺货管理
	 * @author tzx
	 */
	@Transactional
	public void deleteByIdList(List<StockoutDetail> infos) {
		stockoutDetailRepository.saveAll(infos);
	}

	/**
	 * 单个查询缺货管理
	 * @author tzx
	 */
	public StockoutDetail getOne(String id){
		return stockoutDetailRepository.findByStockoutDetailIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "缺货管理不存在"));
	}

	/**
	 * 分页查询缺货管理
	 * @author tzx
	 */
	public Page<StockoutDetail> page(StockoutDetailQueryRequest queryReq){
		return stockoutDetailRepository.findAll(
				StockoutDetailWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询缺货管理
	 * @author tzx
	 */
	public List<StockoutDetail> list(StockoutDetailQueryRequest queryReq){
		return stockoutDetailRepository.findAll(StockoutDetailWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author tzx
	 */
	public StockoutDetailVO wrapperVo(StockoutDetail stockoutDetail) {
		if (stockoutDetail != null){
			StockoutDetailVO stockoutDetailVO = KsBeanUtil.convert(stockoutDetail, StockoutDetailVO.class);
			return stockoutDetailVO;
		}
		return null;
	}

	/**
	 * 通过用户id sku 查询用户是否在明细表当中有过记录
	 * @author tzx
	 */
	public List<StockoutDetailVerifyVO> verifyDetail(StockoutDetailQueryRequest request) {
		List<StockoutDetailVerifyVO> stockoutList = new ArrayList<>();
		List<StockoutDetail> details = stockoutDetailRepository.findAll(StockoutDetailWhereCriteriaBuilder.
				build(request));
		List<String> goodsInfoIdList = details.stream().map
				(s -> s.getGoodsInfoId()).distinct().collect(Collectors.toList());
		request.getGoodsInfoIdLsit().stream().forEach(goodsInfoId->{
			StockoutDetailVerifyVO stockoutDetailVerifyVO = new StockoutDetailVerifyVO();
			stockoutDetailVerifyVO.setGoodsInfoId(goodsInfoId);
			if (goodsInfoIdList.contains(goodsInfoId)){
				// 当前用户已经插入缺货明细
				stockoutDetailVerifyVO.setFlag(Boolean.FALSE);
			}else {
				stockoutDetailVerifyVO.setFlag(Boolean.TRUE);
			}
			stockoutList.add(stockoutDetailVerifyVO);
		});
		return stockoutList;
	}


	/**
	 * 通过用户id sku 查询用户是否在明细表当中有过记录
	 * @author tzx
	 */
	public StockouDetailVerifyGoodInfoIdResponse verifyByGoodInfoIdDetail(StockoutDetailQueryRequest request) {
	return StockouDetailVerifyGoodInfoIdResponse.builder().flag(CollectionUtils.isEmpty(
			stockoutDetailRepository.findAll(StockoutDetailWhereCriteriaBuilder.build(request)))
	).build();
	}
}

