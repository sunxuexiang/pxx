package com.wanmi.sbc.goods.bidding.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.bidding.BiddingQueryRequest;
import com.wanmi.sbc.goods.api.request.bidding.BiddingValidateGoodsRequest;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsQueryRequest;
import com.wanmi.sbc.goods.bean.enums.ActivityStatus;
import com.wanmi.sbc.goods.bean.enums.BiddingType;
import com.wanmi.sbc.goods.bean.vo.BiddingVO;
import com.wanmi.sbc.goods.bidding.model.root.Bidding;
import com.wanmi.sbc.goods.bidding.repository.BiddingRepository;
import com.wanmi.sbc.goods.biddinggoods.model.root.BiddingGoods;
import com.wanmi.sbc.goods.biddinggoods.service.BiddingGoodsService;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.mq.GoodsBiddingProducerService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>竞价配置业务逻辑</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@Service("BiddingService")
public class BiddingService {
	@Autowired
	private BiddingRepository biddingRepository;

	@Autowired
	private BiddingGoodsService biddingGoodsService;

	@Autowired
	private GoodsInfoService goodsInfoService;

	@Autowired
	private GoodsBiddingProducerService goodsBiddingProducerService;


	/**
	 * 新增竞价配置
	 * @author baijz
	 */
	@LcnTransaction
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public Bidding add(Bidding entity , List<String> goodsInfoIds) {
		entity.setDelFlag(DeleteFlag.NO);
		entity.setBiddingStatus(ActivityStatus.ABOUT_TO_START);
		entity = biddingRepository.save(entity);
		this.batchAddBaddingGoods(goodsInfoIds,entity,this.getSortMap(goodsInfoIds));
		Long millis = entity.getStartTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() - System.currentTimeMillis();
		goodsBiddingProducerService.sendStartBiddingActivity(entity.getBiddingId(),millis);
		return entity;
	}

	@LcnTransaction
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_UNCOMMITTED)
	public void batchAddBaddingGoods(List<String> goodsInfoIds, Bidding entity, Map<String,Integer> sortMap){
		//批量新增竞价商品
		if(CollectionUtils.isNotEmpty(goodsInfoIds) ){
			for(String goodsInfoId : goodsInfoIds){
				BiddingGoods biddingGoods = new BiddingGoods();
				biddingGoods.setGoodsInfoId(goodsInfoId);
				biddingGoods.setSort(sortMap.get(goodsInfoId));
				biddingGoods.setDelFlag(DeleteFlag.NO);
				biddingGoods.setBiddingId(entity.getBiddingId());
				biddingGoods.setModifyTime(LocalDateTime.now());
				biddingGoodsService.add(biddingGoods);
				if(BiddingType.KEY_WORDS_TYPE.equals(entity.getBiddingType())) {
					goodsInfoService.updateGoodsInfoKeywordsAndSortNum(entity.getKeywords(), 6-sortMap.get(goodsInfoId), goodsInfoId);
				}else if(BiddingType.CATE_WORDS_TYPE.equals(entity.getBiddingType())){
					goodsInfoService.updateGoodsInfoCateSortNum(6-sortMap.get(goodsInfoId), goodsInfoId);
				}
			}
		}
	}


	/**
	 * 修改竞价配置
	 * @author baijz
	 */
	@LcnTransaction
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_UNCOMMITTED)
	public Bidding modify(Bidding entity, List<String> goodsInfoIds) {
		if(CollectionUtils.isNotEmpty(goodsInfoIds)){
			Map<String,Integer> sortMap = this.getSortMap(goodsInfoIds);

			List<BiddingGoods> biddingGoods = biddingGoodsService.list(BiddingGoodsQueryRequest.builder()
					.biddingId(entity.getBiddingId())
					.delFlag(DeleteFlag.NO)
					.build());
			//1.对比修改后的数据获取需删除的SKU
			List<String> goodsInfoIdsSource = biddingGoods.stream().map(BiddingGoods::getGoodsInfoId).collect(Collectors.toList());
			//不在更新列表中的商品——需要删除
			List<String> needDeleteBiddingSku = goodsInfoIdsSource.stream().filter(g->!goodsInfoIds.contains(g)).collect(Collectors.toList());
			//2. 需要新增的竞价商品
			List<String> needAddBiddingSku = goodsInfoIds.stream().filter(g->!goodsInfoIdsSource.contains(g)).collect(Collectors.toList());
			//3. 需更新的数据
			List<String> needUpdateBiddingSku = goodsInfoIds.stream().filter(g->goodsInfoIdsSource.contains(g)).collect(Collectors.toList());

			if(CollectionUtils.isNotEmpty(needDeleteBiddingSku)){
				//需要删除
				List<BiddingGoods> deleteBiddingGoods = biddingGoods.stream()
						.filter(b->needDeleteBiddingSku.contains(b.getGoodsInfoId())).collect(Collectors.toList());
				deleteBiddingGoods.stream().forEach(d->{
					d.setDelFlag(DeleteFlag.YES);
					d.setDelTime(LocalDateTime.now());
				});
				biddingGoodsService.deleteByIdList(deleteBiddingGoods);
				deleteBiddingGoods.stream().forEach(d->goodsInfoService
						.resetSkuSortNumAndKeywords(d.getGoodsInfoId()));

			}
			if(CollectionUtils.isNotEmpty(needAddBiddingSku)){
				//批量新增竞价商品
				this.batchAddBaddingGoods(needAddBiddingSku,entity,sortMap);

			}
			if(CollectionUtils.isNotEmpty(needUpdateBiddingSku)){
				List<BiddingGoods> modifyBiddingSkus = biddingGoods.stream()
						.filter(b->needUpdateBiddingSku.contains(b.getGoodsInfoId())).collect(Collectors.toList());
				modifyBiddingSkus.stream().forEach(m->m.setSort(sortMap.get(m.getGoodsInfoId())));
				biddingGoodsService.saveAll(modifyBiddingSkus);
				if(BiddingType.KEY_WORDS_TYPE.equals(entity.getBiddingType())) {
					modifyBiddingSkus.stream().forEach(m -> goodsInfoService
							.updateGoodsInfoKeywordsAndSortNum(entity.getKeywords(), 6 - sortMap.get(m.getGoodsInfoId()), m.getGoodsInfoId()));
				}else if(BiddingType.CATE_WORDS_TYPE.equals(entity.getBiddingType())){
					modifyBiddingSkus.stream().forEach(m -> goodsInfoService
							.updateGoodsInfoCateSortNum(6 - sortMap.get(m.getGoodsInfoId()), m.getGoodsInfoId()));
				}
			}
		}
		biddingRepository.save(entity);
		//未开始方可更新
		if(ActivityStatus.ABOUT_TO_START.equals(entity.getBiddingStatus())){
			Long millis = entity.getStartTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() - System.currentTimeMillis();
			goodsBiddingProducerService.sendStartBiddingActivity(entity.getBiddingId(),millis);
		}
		return entity;
	}

	/**
	 * 活动结束
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void finishBiddingActivity(String biddingId){
		Bidding bidding = biddingRepository.getOne(biddingId);
		bidding.setBiddingStatus(ActivityStatus.COMPLETED);
		bidding.setModifyTime(LocalDateTime.now());
		List<BiddingGoods> biddingGoods = biddingGoodsService.list(BiddingGoodsQueryRequest.builder().biddingId(bidding.getBiddingId()).build());
		biddingRepository.save(bidding);
		if(CollectionUtils.isNotEmpty(biddingGoods)){
			biddingGoods.stream().forEach(d->goodsInfoService
					.resetSkuSortNumAndKeywords(d.getGoodsInfoId()));
		}
	}

	private Map<String, Integer> getSortMap(List<String> goodsInfoIds){
		AtomicInteger i = new AtomicInteger(1);
		Map<String,Integer> sortMap = new HashMap<>();
		goodsInfoIds.stream().forEach(g->{
			sortMap.put(g,i.get());
			i.incrementAndGet();
		});
		return sortMap;
	}

	/**
	 * 单个删除竞价配置
	 * @author baijz
	 */
	@Transactional
	public void deleteById(String biddingId) {
		Bidding bidding = biddingRepository.getOne(biddingId);
		if (LocalDateTime.now().isAfter(bidding.getStartTime())){
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "活动已开始，不可以删除");
		}
		bidding.setDelFlag(DeleteFlag.YES);
		List<BiddingGoods> biddingGoods = biddingGoodsService.list(BiddingGoodsQueryRequest.builder().biddingId(bidding.getBiddingId()).build());
		biddingRepository.save(bidding);
		if(CollectionUtils.isNotEmpty(biddingGoods)){
			biddingGoods.stream().forEach(d->goodsInfoService
					.resetSkuSortNumAndKeywords(d.getGoodsInfoId()));
		}
	}

	/**
	 * 批量删除竞价配置
	 * @author baijz
	 */
	@Transactional
	public void deleteByIdList(List<Bidding> infos) {
		biddingRepository.saveAll(infos);
	}

	/**
	 * 单个查询竞价配置
	 * @author baijz
	 */
	public Bidding getOne(String id){
		return biddingRepository.findByBiddingIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "竞价配置不存在"));
	}

	/**
	 * 分页查询竞价配置
	 * @author baijz
	 */
	public Page<Bidding> page(BiddingQueryRequest queryReq){
		return biddingRepository.findAll(
				BiddingWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询竞价配置
	 * @author baijz
	 */
	public List<Bidding> list(BiddingQueryRequest queryReq){
		return biddingRepository.findAll(BiddingWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author baijz
	 */
	public BiddingVO wrapperVo(Bidding bidding) {
		if (bidding != null){
			BiddingVO biddingVO = KsBeanUtil.convert(bidding, BiddingVO.class);
			return biddingVO;
		}
		return null;
	}

	/**
	 * 校验关键字是否已添加，校验分类是否已添加
	 */
	public List<String> validateKeywords(List<String> keywords, String biddingId,LocalDateTime startTime,LocalDateTime endTime){
		List<Bidding> biddings = this.list(BiddingQueryRequest.builder()
				.noCompleted(true)
				.delFlag(DeleteFlag.NO)
				.build());
		//根据时间过滤有时间冲突的竞价配置
		biddings = biddings.stream().filter(bidding ->
				(bidding.getStartTime().isBefore(startTime) && bidding.getEndTime().isAfter(startTime)) ||
						(bidding.getStartTime().isBefore(endTime) && bidding.getEndTime().isAfter(endTime)))
				.collect(Collectors.toList());
		List<String> keywordsAll = new ArrayList<>();
		if(StringUtils.isNotEmpty(biddingId)){
			biddings = biddings.stream().filter(bidding -> !bidding.getBiddingId().equals(biddingId)).collect(Collectors.toList());
		}
		//keywrods all
		List<String> keywordList = biddings.stream().map(Bidding::getKeywords).collect(Collectors.toList());
		keywordList.stream().forEach(b->keywordsAll.addAll(Arrays.asList(b.split(","))));
		List<String> repeatKeywords = keywords.stream().filter(k->keywordsAll.contains(k)).collect(Collectors.toList());
		return repeatKeywords;
	}

	/**
	 * 校验竞价商品是否重复
	 * @param request
	 * @return
	 */
	public List<String> validateBiddingGoods(BiddingValidateGoodsRequest request){
		List<String> repeatSkuIds = new ArrayList<>();
		List<Bidding> biddings = this.list(BiddingQueryRequest.builder()
				.noCompleted(true)
				.delFlag(DeleteFlag.NO)
				.biddingType(request.getBiddingType())
				.build());
		//根据时间过滤有时间冲突的竞价配置
		List<Bidding> biddingList = biddings.stream().filter(bidding ->
				(bidding.getStartTime().isBefore(request.getStartTime()) && bidding.getEndTime().isAfter(request.getStartTime())) ||
						(bidding.getStartTime().isBefore(request.getEndTime()) && bidding.getEndTime().isAfter(request.getEndTime())))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(biddingList)){
			List<String> biddingIds = biddingList.stream().map(Bidding::getBiddingId).collect(Collectors.toList());
			//去除自身的校验
			if(StringUtils.isNotEmpty(request.getBiddingId())){
				biddingIds.remove(request.getBiddingId());
			}
			if(CollectionUtils.isNotEmpty(biddingIds)) {
				List<BiddingGoods> biddingGoods = biddingGoodsService.list(BiddingGoodsQueryRequest.builder()
						.biddingIds(biddingIds)
						.delFlag(DeleteFlag.NO)
						.build());
				if (CollectionUtils.isNotEmpty(biddingGoods)) {
					List<String> compareGoodsInfoIds = biddingGoods.stream().map(BiddingGoods::getGoodsInfoId).collect(Collectors.toList());
					repeatSkuIds = request.getGoodsInfoIds().stream().filter(g -> compareGoodsInfoIds.contains(g)).collect(Collectors.toList());
				}
			}
		}
		return repeatSkuIds;
	}
}

