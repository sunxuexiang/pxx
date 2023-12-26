package com.wanmi.sbc.setting.homedelivery.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryModifyRequest;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryQueryRequest;
import com.wanmi.sbc.setting.bean.vo.HomeDeliveryVO;
import com.wanmi.sbc.setting.homedelivery.model.root.HomeDelivery;
import com.wanmi.sbc.setting.homedelivery.repository.HomeDeliveryRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>配送到家业务逻辑</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@Service("HomeDeliveryService")
public class HomeDeliveryService {
	@Autowired
	private HomeDeliveryRepository homeDeliveryRepository;

	/**
	 * 新增配送到家
	 * @author lh
	 */
	@Transactional
	public HomeDelivery add(HomeDelivery entity) {
		homeDeliveryRepository.save(entity);
		return entity;
	}

	/**
	 * 修改配送到家
	 * @author lh
	 */
	@Transactional
	public HomeDelivery modify(HomeDelivery entity) {
		homeDeliveryRepository.save(entity);
		return entity;
	}

	/**
	 * 修改配送到家
	 * @author lh
	 */
	@Transactional
	public HomeDelivery modifyMessage(HomeDelivery entity) {
		if(Objects.nonNull(entity.getHomeDeliveryId())&&entity.getHomeDeliveryId()>0) {
			HomeDelivery one = homeDeliveryRepository.getOne(entity.getHomeDeliveryId());
			if (ObjectUtils.isEmpty(one)) {
				throw new SbcRuntimeException(CommonErrorCode.FAILED, "配置文件不存在");
			}
			entity.setCreateTime(one.getCreateTime());
		}else {
			entity.setCreateTime(LocalDateTime.now());
		}
		entity.setDelFlag(DeleteFlag.NO);
		homeDeliveryRepository.save(entity);
		return entity;
	}

	@Transactional
	public void modifyMessage(HomeDeliveryModifyRequest request) {
		List<HomeDelivery> homeDeliveryList = homeDeliveryRepository.queryByStoreId(request.getStoreId());
		List<Integer> useedDeliveryTypeList = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(homeDeliveryList)){
			for(HomeDelivery homeDelivery:homeDeliveryList){
				if(homeDelivery.getDeliveryType()==null){
					continue;
				}
				updateContentByStoreIdAndDeliveryType(request, useedDeliveryTypeList, homeDelivery);
			}
		}
		for(int i=1;i<=10;i++){
			if(useedDeliveryTypeList.contains(i)){
				continue;
			}
			HomeDelivery homeDelivery = new HomeDelivery();
			homeDelivery.setDeliveryType(i);
			homeDelivery.setStoreId(request.getStoreId());
			buildObjByStoreIdAndDeliveryType(request,homeDelivery);
			if(homeDelivery.getContent()==null){
				continue;
			}
			homeDelivery.setDelFlag(DeleteFlag.NO);
			homeDelivery.setCreateTime(LocalDateTime.now());
			homeDeliveryRepository.saveAndFlush(homeDelivery);
		}
	}

	private void buildObjByStoreIdAndDeliveryType(HomeDeliveryModifyRequest request,  HomeDelivery homeDelivery) {
		if(homeDelivery.getDeliveryType()==1){
			homeDelivery.setContent(request.getLogisticsContent());
		}
		else if(homeDelivery.getDeliveryType()==2){
			homeDelivery.setContent(request.getExpressContent());
		}
		else if(homeDelivery.getDeliveryType()==3){
			homeDelivery.setContent(request.getPickSelfContent());
		}
		else if(homeDelivery.getDeliveryType()==4){
			//homeDelivery.setContent(request.getContent());
			homeDelivery.setContent("   ");
		}
		else if(homeDelivery.getDeliveryType()==5){
			homeDelivery.setContent(request.getExpensesCostContent());
		}
		else if(homeDelivery.getDeliveryType()==6){
			homeDelivery.setContent(request.getDoorPickContent());
		}
		else if(homeDelivery.getDeliveryType()==7){
			homeDelivery.setContent(request.getDeliveryToStoreContent());
		}
		else if(homeDelivery.getDeliveryType()==8){
			homeDelivery.setContent(request.getSpecifyLogisticsContent());
		}
		else if(homeDelivery.getDeliveryType()==9){
			homeDelivery.setContent(request.getIntraCityLogisticsContent());
		}
		else if(homeDelivery.getDeliveryType()==10){
			homeDelivery.setContent(request.getExpressArrivedContent());
		}
	}

	private void buildVoByStoreIdAndDeliveryType(HomeDeliveryVO homeDeliveryVO,  HomeDelivery homeDelivery) {
		if(homeDelivery.getDeliveryType()==1){
			homeDeliveryVO.setLogisticsContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==2){
			homeDeliveryVO.setExpressContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==3){
			homeDeliveryVO.setPickSelfContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==4){
			homeDeliveryVO.setContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==5){
			homeDeliveryVO.setExpensesCostContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==6){
			homeDeliveryVO.setDoorPickContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==7){
			homeDeliveryVO.setDeliveryToStoreContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==8){
			homeDeliveryVO.setSpecifyLogisticsContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==9){
			homeDeliveryVO.setIntraCityLogisticsContent(homeDelivery.getContent());
		}
		else if(homeDelivery.getDeliveryType()==10){
			homeDeliveryVO.setExpressArrivedContent(homeDelivery.getContent());
		}
	}

	private void updateContentByStoreIdAndDeliveryType(HomeDeliveryModifyRequest request, List<Integer> useedDeliveryTypeList, HomeDelivery homeDelivery) {
		if(homeDelivery.getDeliveryType()==1){
			updateByStoreIdAndDeliverType(request.getLogisticsContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==2){
			updateByStoreIdAndDeliverType(request.getExpressContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==3){
			updateByStoreIdAndDeliverType(request.getPickSelfContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==4){
			updateByStoreIdAndDeliverType("         ", useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==5){
			updateByStoreIdAndDeliverType(request.getExpensesCostContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==6){
			updateByStoreIdAndDeliverType(request.getDoorPickContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==7){
			updateByStoreIdAndDeliverType(request.getDeliveryToStoreContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==8){
			updateByStoreIdAndDeliverType(request.getSpecifyLogisticsContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==9){
			updateByStoreIdAndDeliverType(request.getIntraCityLogisticsContent(), useedDeliveryTypeList, homeDelivery);
		}
		else if(homeDelivery.getDeliveryType()==10){
			updateByStoreIdAndDeliverType(request.getExpressArrivedContent(), useedDeliveryTypeList, homeDelivery);
		}
	}

	private void updateByStoreIdAndDeliverType(String content, List<Integer> useedDeliveryTypeList, HomeDelivery homeDelivery) {
		if(content == null){
			content="";
		}
		homeDeliveryRepository.updateById(homeDelivery.getHomeDeliveryId(),content);
		useedDeliveryTypeList.add(homeDelivery.getDeliveryType());
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateByStoreIdIdAndDeliveryType(Long storeId, Integer deliveryType, String content){
		List<HomeDelivery> homeDeliveryList = homeDeliveryRepository.queryByStoreIdAndDeliveryType(storeId,deliveryType);
		if(CollectionUtils.isEmpty(homeDeliveryList)){
			HomeDelivery homeDelivery = new HomeDelivery();
			homeDelivery.setDeliveryType(deliveryType);
			homeDelivery.setContent(content);
			homeDelivery.setDelFlag(DeleteFlag.NO);
			homeDelivery.setCreateTime(LocalDateTime.now());
			homeDeliveryRepository.save(homeDelivery);
		}else {
			for(HomeDelivery homeDelivery:homeDeliveryList) {
				homeDeliveryRepository.updateById(homeDelivery.getHomeDeliveryId(), content);
			}
		}
	}

	/**
	 * 单个删除配送到家
	 * @author lh
	 */
	@Transactional
	public void deleteById(HomeDelivery entity) {
		homeDeliveryRepository.save(entity);
	}

	/**
	 * 批量删除配送到家
	 * @author lh
	 */
	@Transactional
	public void deleteByIdList(List<HomeDelivery> infos) {
		homeDeliveryRepository.saveAll(infos);
	}

	/**
	 * 单个查询配送到家
	 * @author lh
	 */
	public HomeDelivery getOne(Long id){
		return homeDeliveryRepository.findByHomeDeliveryIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "配送到家不存在"));
	}

	/**
	 * 分页查询配送到家
	 * @author lh
	 */
	public Page<HomeDelivery> page(HomeDeliveryQueryRequest queryReq){
		return homeDeliveryRepository.findAll(
				HomeDeliveryWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询配送到家
	 * @author lh
	 */
	public List<HomeDelivery> list(HomeDeliveryQueryRequest queryReq){
		return homeDeliveryRepository.findAll(HomeDeliveryWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author lh
	 */
	public HomeDeliveryVO wrapperVo(HomeDelivery homeDelivery) {
		if (homeDelivery != null){
			HomeDeliveryVO homeDeliveryVO = KsBeanUtil.convert(homeDelivery, HomeDeliveryVO.class);
			return homeDeliveryVO;
		}
		return null;
	}

	public HomeDeliveryVO wrapperVo(List<HomeDelivery> homeDeliveryList) {
		HomeDeliveryVO homeDeliveryVO = new HomeDeliveryVO();
		if(CollectionUtils.isNotEmpty(homeDeliveryList)) {
			homeDeliveryVO.setHomeDeliveryId(homeDeliveryList.get(0).getHomeDeliveryId());
			homeDeliveryVO.setStoreId(homeDeliveryList.get(0).getStoreId());
			for (HomeDelivery homeDelivery : homeDeliveryList) {
				buildVoByStoreIdAndDeliveryType(homeDeliveryVO, homeDelivery);
			}
		}
		return homeDeliveryVO;
	}
}

