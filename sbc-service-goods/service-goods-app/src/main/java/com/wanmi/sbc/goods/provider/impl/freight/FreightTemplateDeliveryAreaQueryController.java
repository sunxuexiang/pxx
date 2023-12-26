package com.wanmi.sbc.goods.provider.impl.freight;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.goods.api.request.freight.FreightStoreSyncRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateDeliveryAreaByStoreIdResponse;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaCustomCfgVO;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateDeliveryArea;
import com.wanmi.sbc.goods.freight.request.FreightTemplateDeliveryAreaQueryRequest;
import com.wanmi.sbc.goods.freight.service.FreightTemplateDeliveryAreaRedisService;
import com.wanmi.sbc.goods.freight.service.FreightTemplateDeliveryAreaService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseWhereCriteriaBuilder;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>配送到家范围查询服务接口实现</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@RestController
@Validated
@Slf4j
public class FreightTemplateDeliveryAreaQueryController implements FreightTemplateDeliveryAreaQueryProvider {
	@Autowired
	private FreightTemplateDeliveryAreaService freightTemplateDeliveryAreaService;

	@Autowired
	private VillagesAddressConfigQueryProvider villagesAddressConfigQueryProvider;

	@Autowired
	private WareHouseRepository wareHouseRepository;

	@Autowired
	private FreightTemplateDeliveryAreaRedisService freightTemplateDeliveryAreaRedisService;

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaByStoreIdResponse>> query(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq) {
		List<FreightTemplateDeliveryAreaByStoreIdResponse> freightTemplateDeliveryAreaByStoreIdResponseList = new ArrayList<>();
		FreightTemplateDeliveryAreaQueryRequest queryReq = new FreightTemplateDeliveryAreaQueryRequest();
		KsBeanUtil.copyPropertiesThird(freightTemplateDeliveryAreaListReq, queryReq);
		queryReq.setDelFlag(DeleteFlag.NO);
		if(Objects.nonNull(freightTemplateDeliveryAreaListReq.getDestinationAreaName())){
			queryReq.setDestinationAreaName(Arrays.stream(freightTemplateDeliveryAreaListReq.getDestinationAreaName()).findFirst().orElse(null));
		}
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQuery(queryReq);

		if(CollectionUtils.isEmpty(freightTemplateDeliveryAreas)){
			List<FreightTemplateDeliveryAreaByStoreIdResponse> freightTemplateDeliveryAreaByStoreIdResponses =new LinkedList<>();
			freightTemplateDeliveryAreaByStoreIdResponses.add(FreightTemplateDeliveryAreaByStoreIdResponse.builder().build());
			return BaseResponse.success(freightTemplateDeliveryAreaByStoreIdResponses);
		}
		log.info("数据结果==========="+freightTemplateDeliveryAreas);
		List<Long> wareIdList = freightTemplateDeliveryAreas.stream().map(FreightTemplateDeliveryArea::getWareId).distinct().collect(Collectors.toList());

		WareHouseQueryRequest queryReqForWareHouse = new WareHouseQueryRequest();
		queryReq.setDelFlag(DeleteFlag.NO);
		List<WareHouse> wareHouseList = wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(queryReqForWareHouse));

		wareIdList.forEach(wareId ->{
			WareHouse wareHouse = wareHouseList.stream().filter(wareHouseOne -> wareId.equals(wareHouseOne.getWareId())).findFirst().get();
			String wareName = Objects.nonNull(wareHouse) ? wareHouse.getWareName() : wareId + "";
			//常规
			FreightTemplateDeliveryArea freightTemplateDeliveryArea = freightTemplateDeliveryAreas.stream().filter(f -> f.getDestinationType().equals(freightTemplateDeliveryType.CONVENTION))
					.filter(f -> f.getWareId().equals(wareId)).collect(Collectors.toList()).stream().findFirst().orElse(null);
			FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = baseWrapperAreaVO(freightTemplateDeliveryArea);
			freightTemplateDeliveryAreaVO.setWareName(wareName);

			//乡镇满十件
			FreightTemplateDeliveryArea areaTenFreightTemplateDeliveryArea = freightTemplateDeliveryAreas.stream().filter(f -> f.getDestinationType().equals(freightTemplateDeliveryType.AREATENDELIVER))
					.filter(f -> f.getWareId().equals(wareId)).collect(Collectors.toList()).stream().findFirst().orElse(null);
			FreightTemplateDeliveryAreaVO areaTenFreightTemplateDeliveryAreaVO = baseWrapperAreaVO(areaTenFreightTemplateDeliveryArea);
			areaTenFreightTemplateDeliveryAreaVO.setWareName(wareName);

			FreightTemplateDeliveryAreaByStoreIdResponse build = FreightTemplateDeliveryAreaByStoreIdResponse
					.builder()
					.freightTemplateDeliveryAreaVO(freightTemplateDeliveryAreaVO)
					.areaTenFreightTemplateDeliveryAreaVO(areaTenFreightTemplateDeliveryAreaVO)
					.build();

			freightTemplateDeliveryAreaByStoreIdResponseList.add(build);
		});
		log.info("返回结果==========="+freightTemplateDeliveryAreaByStoreIdResponseList);
		return BaseResponse.success(freightTemplateDeliveryAreaByStoreIdResponseList);
	}

	private FreightTemplateDeliveryAreaVO baseWrapperAreaVO(FreightTemplateDeliveryArea freightTemplateDeliveryArea) {
		FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO= freightTemplateDeliveryAreaService.wrapperVo(freightTemplateDeliveryArea);
		return freightTemplateDeliveryAreaVO;
	}

	private FreightTemplateDeliveryAreaVO baseWrapperFromVillages(FreightTemplateDeliveryArea freightTemplateDeliveryArea) {
		FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = new FreightTemplateDeliveryAreaVO();
		KsBeanUtil.copyPropertiesThird(freightTemplateDeliveryArea, freightTemplateDeliveryAreaVO);
		FreightTemplateDeliveryArea cityArea = baseQueryFirst(FreightTemplateDeliveryAreaQueryRequest.builder().storeId(freightTemplateDeliveryArea.getStoreId()).wareId(freightTemplateDeliveryArea.getWareId()).openFlag(1).destinationType(freightTemplateDeliveryType.DELIVERYTOSTORE_5.toValue()).build());
		if(cityArea==null){
			return freightTemplateDeliveryAreaVO;
		}
		if(StringUtils.isBlank(cityArea.getDestinationArea())){
			return freightTemplateDeliveryAreaVO;
		}
		List<Long> citylist = Arrays.stream(cityArea.getDestinationArea().split(",")).map(Long::parseLong).collect(Collectors.toList());
		List<VillagesAddressConfigVO> villagesAddressConfigVOList= villagesAddressConfigQueryProvider.findListVillageByCityList(VillagesAddressConfigQueryRequest.builder().cityIdList(citylist).storeId(Constants.BOSS_DEFAULT_STORE_ID).build()).getContext().getVillagesAddressConfigVOList();
		String[] idArr = new String[villagesAddressConfigVOList.size()];
		String[] nameArr = new String[villagesAddressConfigVOList.size()];
		for (int i = 0; i <villagesAddressConfigVOList.size() ; i++) {
			idArr[i]=Objects.toString(villagesAddressConfigVOList.get(i).getVillageId());
			nameArr[i]=Objects.toString(villagesAddressConfigVOList.get(i).getVillageName());
		}
		freightTemplateDeliveryAreaVO.setDestinationArea(idArr);
		freightTemplateDeliveryAreaVO.setDestinationAreaName(nameArr);
		return freightTemplateDeliveryAreaVO;
	}

	@Override
	public BaseResponse<FreightTemplateDeliveryAreaVO> queryByStoreIdAndWareIdAndDestinationType(Long storeId, Long wareId, Integer destinationType) {
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQuery(FreightTemplateDeliveryAreaQueryRequest.builder().storeId(storeId).wareId(wareId).openFlag(1).destinationType(destinationType).build());
		if(CollectionUtils.isNotEmpty(freightTemplateDeliveryAreas)){
			return BaseResponse.success(baseWrapperAreaVO(freightTemplateDeliveryAreas.get(0)));
		}
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryByStoreIdAndWareIdAndOpened(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq) {
		FreightTemplateDeliveryAreaQueryRequest freightTemplateDeliveryAreaQueryRequest = FreightTemplateDeliveryAreaQueryRequest.builder()
				.storeId(freightTemplateDeliveryAreaListReq.getStoreId())
				.wareId(freightTemplateDeliveryAreaListReq.getWareId())
				.delFlag(DeleteFlag.NO)
				.destinationTypeList(freightTemplateDeliveryAreaListReq.getDestinationTypeList())
				.openFlag(1).build();
		freightTemplateDeliveryAreaQueryRequest.putSort("freightFreeNumber", SortType.ASC.toValue());//门槛升序排序
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQuery(freightTemplateDeliveryAreaQueryRequest);
		if(CollectionUtils.isNotEmpty(freightTemplateDeliveryAreas)) {
			List<FreightTemplateDeliveryAreaVO> voList = getDeliveryAreaVOList(freightTemplateDeliveryAreas);
			return BaseResponse.success(voList);
		}
		return BaseResponse.SUCCESSFUL();
	}

	private List<FreightTemplateDeliveryArea> baseQuery(FreightTemplateDeliveryAreaQueryRequest freightTemplateDeliveryAreaQueryRequest) {
		boolean isNeedCache = (Constants.BOSS_DEFAULT_STORE_ID.compareTo(freightTemplateDeliveryAreaQueryRequest.getStoreId())==0 ||freightTemplateDeliveryAreaQueryRequest.getStoreId().toString().length()<7) && freightTemplateDeliveryAreaQueryRequest.getDestinationType()!=null;
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = null;
		if(isNeedCache){
			freightTemplateDeliveryAreas = freightTemplateDeliveryAreaRedisService.get(freightTemplateDeliveryAreaQueryRequest.getStoreId(),freightTemplateDeliveryAreaQueryRequest.getDestinationType());
		}
		if(freightTemplateDeliveryAreas==null) {
			freightTemplateDeliveryAreas = baseQueryNoCache(freightTemplateDeliveryAreaQueryRequest);
			if(isNeedCache && null!=freightTemplateDeliveryAreas){
				freightTemplateDeliveryAreaRedisService.put(freightTemplateDeliveryAreaQueryRequest.getStoreId(),freightTemplateDeliveryAreaQueryRequest.getDestinationType(),freightTemplateDeliveryAreas);
			}
		}
		return freightTemplateDeliveryAreas;
	}

	private List<FreightTemplateDeliveryArea> baseQueryNoCache(FreightTemplateDeliveryAreaQueryRequest freightTemplateDeliveryAreaQueryRequest) {
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = freightTemplateDeliveryAreaService.query(freightTemplateDeliveryAreaQueryRequest);
		return freightTemplateDeliveryAreas;
	}

	private FreightTemplateDeliveryArea baseQueryFirst(FreightTemplateDeliveryAreaQueryRequest freightTemplateDeliveryAreaQueryRequest) {
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQuery(freightTemplateDeliveryAreaQueryRequest);
		if(CollectionUtils.isNotEmpty(freightTemplateDeliveryAreas))
			return freightTemplateDeliveryAreas.get(0);
		return null;
	}

	private List<FreightTemplateDeliveryAreaVO> getDeliveryAreaVOList(List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas) {
		List<FreightTemplateDeliveryAreaVO> deliveryAreaVOList = new ArrayList<>(freightTemplateDeliveryAreas.size());
		freightTemplateDeliveryAreas.forEach(f->{
			deliveryAreaVOList.add(baseWrapperAreaVO(f));
		});
		return deliveryAreaVOList;
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryHomeConfifg(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq) {
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQuery(FreightTemplateDeliveryAreaQueryRequest.builder()
				.storeId(freightTemplateDeliveryAreaListReq.getStoreId())
				.wareId(freightTemplateDeliveryAreaListReq.getWareId())
				.delFlag(DeleteFlag.NO)
				.openFlag(1)
				.destinationTypeList(Arrays.asList(freightTemplateDeliveryType.CONVENTION.toValue(), freightTemplateDeliveryType.AREATENDELIVER.toValue())).build());
		if(CollectionUtils.isNotEmpty(freightTemplateDeliveryAreas)) {
			List<FreightTemplateDeliveryAreaVO> voList = getDeliveryAreaVOList(freightTemplateDeliveryAreas);
			return BaseResponse.success(voList);
		}
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVO() {
		return queryByStoreIdAndWareIdAndDestinationType(Constants.BOSS_DEFAULT_STORE_ID, 1L, freightTemplateDeliveryType.DELIVERYTOSTORE_5.toValue());
	}

	@Override
	public BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVillageVO() {
		return queryByStoreIdAndWareIdAndDestinationType(Constants.BOSS_DEFAULT_STORE_ID, 1L, freightTemplateDeliveryType.DELIVERYTOSTORE_10.toValue());
	}

	/**
	 * @param provinceId
	 * @desc 配送到店区域
	 * @author shiy  2023/11/6 17:25
	 */
	@Override
	public BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVOByProvinceId(Long provinceId) {
		return queryByStoreIdAndWareIdAndDestinationType(provinceId, 1L, freightTemplateDeliveryType.DELIVERYTOSTORE_5.toValue());
	}

	@Override
	public BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVillageVOByProvinceId(Long provinceId) {
		return queryByStoreIdAndWareIdAndDestinationType(provinceId, 1L, freightTemplateDeliveryType.DELIVERYTOSTORE_10.toValue());
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryFreeStoreDilivery(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq) {
		return getListBaseResponse(freightTemplateDeliveryAreaListReq,freightTemplateDeliveryType.CONVENTION);
	}

	private BaseResponse<List<FreightTemplateDeliveryAreaVO>> getBossConfigListResponse(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq,freightTemplateDeliveryType destinationType) {
		FreightTemplateDeliveryAreaQueryRequest queryReq = new FreightTemplateDeliveryAreaQueryRequest();
		KsBeanUtil.copyPropertiesThird(freightTemplateDeliveryAreaListReq, queryReq);
		queryReq.setDelFlag(DeleteFlag.NO);
		queryReq.setDestinationType(destinationType.toValue());
		queryReq.setWareId(1L);
		if (Objects.nonNull(freightTemplateDeliveryAreaListReq.getDestinationAreaName())) {
			queryReq.setDestinationAreaName(Arrays.stream(freightTemplateDeliveryAreaListReq.getDestinationAreaName()).findFirst().orElse(null));
		}
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQueryNoCache(queryReq);
		if (CollectionUtils.isEmpty(freightTemplateDeliveryAreas)) {
			List<FreightTemplateDeliveryAreaVO> freightTemplateDeliveryAreaVOS = new LinkedList<>();
			return BaseResponse.success(freightTemplateDeliveryAreaVOS);
		}
		queryReq.setDelFlag(DeleteFlag.NO);
		List<FreightTemplateDeliveryAreaVO> deliveryAreaVOList = new LinkedList<>();
		String wareName = "默认仓";
		freightTemplateDeliveryAreas.forEach(freightTemplateDeliveryArea -> {
			FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = null;
			if (false && freightTemplateDeliveryType.isStoreVillage(freightTemplateDeliveryArea.getDestinationType())) {
				freightTemplateDeliveryAreaVO = baseWrapperFromVillages(freightTemplateDeliveryArea);
			} else {
				freightTemplateDeliveryAreaVO = baseWrapperAreaVO(freightTemplateDeliveryArea);
			}
			freightTemplateDeliveryAreaVO.setWareName(wareName);
			if (StringUtils.isNotBlank(freightTemplateDeliveryArea.getCustomCfg())) {
				freightTemplateDeliveryAreaVO.setCustomCfgVO(JSONObject.parseObject(freightTemplateDeliveryArea.getCustomCfg(), FreightTemplateDeliveryAreaCustomCfgVO.class));
			}
			deliveryAreaVOList.add(freightTemplateDeliveryAreaVO);
		});
		return BaseResponse.success(deliveryAreaVOList);
	}

	private BaseResponse<List<FreightTemplateDeliveryAreaVO>> getListBaseResponse(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq,freightTemplateDeliveryType destinationType) {
		if(true){
			return getBossConfigListResponse(freightTemplateDeliveryAreaListReq,destinationType);
		}
		FreightTemplateDeliveryAreaQueryRequest queryReq = new FreightTemplateDeliveryAreaQueryRequest();
		KsBeanUtil.copyPropertiesThird(freightTemplateDeliveryAreaListReq, queryReq);
		queryReq.setDelFlag(DeleteFlag.NO);
		queryReq.setDestinationType(destinationType.toValue());
		if(Objects.nonNull(freightTemplateDeliveryAreaListReq.getDestinationAreaName())){
			queryReq.setDestinationAreaName(Arrays.stream(freightTemplateDeliveryAreaListReq.getDestinationAreaName()).findFirst().orElse(null));
		}
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQuery(queryReq);

		if(CollectionUtils.isEmpty(freightTemplateDeliveryAreas)){
			List<FreightTemplateDeliveryAreaVO> freightTemplateDeliveryAreaVOS =new LinkedList<>();
			return BaseResponse.success(freightTemplateDeliveryAreaVOS);
		}
		List<Long> wareIdList = freightTemplateDeliveryAreas.stream().map(FreightTemplateDeliveryArea::getWareId).distinct().collect(Collectors.toList());
		WareHouseQueryRequest queryReqForWareHouse = new WareHouseQueryRequest();
		queryReq.setDelFlag(DeleteFlag.NO);
		List<WareHouse> wareHouseList = wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(queryReqForWareHouse));
		List<FreightTemplateDeliveryAreaVO> deliveryAreaVOList = new LinkedList<>();
		wareIdList.forEach(wareId ->{
			WareHouse wareHouse = wareHouseList.stream().filter(wareHouseOne -> wareId.equals(wareHouseOne.getWareId())).findFirst().get();
			String wareName = Objects.nonNull(wareHouse) ? wareHouse.getWareName() : wareId + "";
			//常规
			FreightTemplateDeliveryArea freightTemplateDeliveryArea = freightTemplateDeliveryAreas.stream()
					.filter(f -> f.getWareId().equals(wareId)).collect(Collectors.toList()).stream().findFirst().orElse(null);
			if(freightTemplateDeliveryArea != null) {
				FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = baseWrapperAreaVO(freightTemplateDeliveryArea);
				freightTemplateDeliveryAreaVO.setWareName(wareName);
				if(StringUtils.isNotBlank(freightTemplateDeliveryArea.getCustomCfg())){
					freightTemplateDeliveryAreaVO.setCustomCfgVO(JSONObject.parseObject(freightTemplateDeliveryArea.getCustomCfg(), FreightTemplateDeliveryAreaCustomCfgVO.class));
				}
				deliveryAreaVOList.add(freightTemplateDeliveryAreaVO);
			}
		});
		return BaseResponse.success(deliveryAreaVOList);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryVillagesDilivery(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq) {
		return getListBaseResponse(freightTemplateDeliveryAreaListReq,freightTemplateDeliveryType.AREATENDELIVER);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryThirdLogisticsDilivery(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq) {
		return getListBaseResponse(freightTemplateDeliveryAreaListReq,freightTemplateDeliveryType.THIRDLOGISTICS);
	}


	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryToDoorPick(FreightTemplateDeliveryAreaListRequest queryRequest) {
		return getListBaseResponse(queryRequest,freightTemplateDeliveryType.TODOORPICK);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryPaidExpress(FreightTemplateDeliveryAreaListRequest queryRequest) {
		return getListBaseResponse(queryRequest,freightTemplateDeliveryType.PAIDEXPRESS);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryToStore(FreightTemplateDeliveryAreaListRequest queryRequest) {
		List<FreightTemplateDeliveryAreaVO> deliveryStoreList = new ArrayList<>(3);
		List<FreightTemplateDeliveryAreaVO> tmpList = getListBaseResponse(queryRequest,freightTemplateDeliveryType.DELIVERYTOSTORE).getContext();
		if(CollectionUtils.isNotEmpty(tmpList)){
			deliveryStoreList.add(tmpList.get(0));
		}
		return getListBaseResponse(queryRequest, deliveryStoreList);
	}

	private BaseResponse<List<FreightTemplateDeliveryAreaVO>> getListBaseResponse(FreightTemplateDeliveryAreaListRequest queryRequest, List<FreightTemplateDeliveryAreaVO> deliveryStoreList) {
		List<FreightTemplateDeliveryAreaVO> tmpList;
		queryRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
		queryRequest.setCompanyInfoId(Constants.BOSS_DEFAULT_COMPANY_INFO_ID);
		tmpList = getListBaseResponse(queryRequest,freightTemplateDeliveryType.DELIVERYTOSTORE_5).getContext();
		if(CollectionUtils.isNotEmpty(tmpList)){
			deliveryStoreList.add(tmpList.get(0));
		}
		queryRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
		queryRequest.setCompanyInfoId(Constants.BOSS_DEFAULT_COMPANY_INFO_ID);
		tmpList = getBossConfigListResponse(queryRequest,freightTemplateDeliveryType.DELIVERYTOSTORE_10).getContext();
		if(CollectionUtils.isNotEmpty(tmpList)){
			deliveryStoreList.add(tmpList.get(0));
		}
		return BaseResponse.success(deliveryStoreList);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryToStoreBoss(FreightTemplateDeliveryAreaListRequest queryRequest) {
		List<FreightTemplateDeliveryAreaVO> deliveryStoreList = new ArrayList<>(2);
		return getListBaseResponse(queryRequest, deliveryStoreList);
	}

	@Override
	public BaseResponse<List<Long>> querySupplierUseDeliveryToStore(Integer destinationType) {
		List<Long> storeIdList = freightTemplateDeliveryAreaService.getUseDeliveryToStoreSupplierList(destinationType);
		return BaseResponse.success(storeIdList);
	}

	@Override
	public BaseResponse<Boolean> queryIsToStoreVillageFlag(Long cityId,Long townId) {
		if(cityId==null || townId==null){
			return BaseResponse.success(false);
		}
		FreightTemplateDeliveryArea cityArea = baseQueryFirst(FreightTemplateDeliveryAreaQueryRequest.builder().storeId(Constants.BOSS_DEFAULT_STORE_ID).wareId(1L).openFlag(1).destinationType(freightTemplateDeliveryType.DELIVERYTOSTORE_5.toValue()).build());
		if(StringUtils.isNotBlank(cityArea.getDestinationArea())){
			boolean isVillageFlag = ArrayUtils.contains(cityArea.getDestinationArea().split(","),cityId.toString());
			if(!isVillageFlag){
				return BaseResponse.success(false);
			}
		}

		FreightTemplateDeliveryArea villageArea = baseQueryFirst(FreightTemplateDeliveryAreaQueryRequest.builder().storeId(Constants.BOSS_DEFAULT_STORE_ID).wareId(1L).openFlag(1).destinationType(freightTemplateDeliveryType.DELIVERYTOSTORE_10.toValue()).build());
		if(cityArea==null || villageArea==null) {
			return BaseResponse.success(false);
		}
		if(StringUtils.isNotBlank(villageArea.getDestinationArea())){
			boolean isVillageFlag = ArrayUtils.contains(villageArea.getDestinationArea().split(","),townId.toString());
			return BaseResponse.success(isVillageFlag);
		}
		Integer vCount = villagesAddressConfigQueryProvider.getCountByVillageIdAndStoreId(townId,Constants.BOSS_DEFAULT_STORE_ID).getContext();
		boolean isVillageFlag = vCount>0;
		return BaseResponse.success(isVillageFlag);
	}

	@Override
	public BaseResponse<Boolean> queryIsToStoreVillageFlag(Long provinceId, Long cityId, Long townId) {
		if(provinceId==null || cityId==null || townId==null){
			return BaseResponse.success(false);
		}
		FreightTemplateDeliveryArea cityArea = baseQueryFirst(FreightTemplateDeliveryAreaQueryRequest.builder().storeId(provinceId).wareId(1L).openFlag(1).destinationType(freightTemplateDeliveryType.DELIVERYTOSTORE_5.toValue()).build());
		if(cityArea==null) {
			return BaseResponse.success(false);
		}
		if(StringUtils.isNotBlank(cityArea.getDestinationArea())){
			boolean isVillageFlag = ArrayUtils.contains(cityArea.getDestinationArea().split(","),cityId.toString());
			if(!isVillageFlag){
				return BaseResponse.success(false);
			}
		}

		FreightTemplateDeliveryArea villageArea = baseQueryFirst(FreightTemplateDeliveryAreaQueryRequest.builder().storeId(provinceId).wareId(1L).openFlag(1).destinationType(freightTemplateDeliveryType.DELIVERYTOSTORE_10.toValue()).build());
		if(cityArea==null || villageArea==null) {
			return BaseResponse.success(false);
		}
		if(StringUtils.isNotBlank(villageArea.getDestinationArea())){
			boolean isVillageFlag = ArrayUtils.contains(villageArea.getDestinationArea().split(","),townId.toString());
			return BaseResponse.success(isVillageFlag);
		}
		Integer vCount = villagesAddressConfigQueryProvider.getCountByVillageIdAndStoreId(townId,Constants.BOSS_DEFAULT_STORE_ID).getContext();
		boolean isVillageFlag = vCount>0;
		return BaseResponse.success(isVillageFlag);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> syncStoreDeliveryArea(FreightStoreSyncRequest freightStoreSyncRequest) {
		freightTemplateDeliveryAreaService.syncStoreDeliveryArea(freightStoreSyncRequest);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> querySpecifyLogistics(FreightTemplateDeliveryAreaListRequest queryRequest) {
		return getListBaseResponse(queryRequest,freightTemplateDeliveryType.SPECIFY_LOGISTICS);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryIntraCityLogistics(FreightTemplateDeliveryAreaListRequest queryRequest) {
		return getListBaseResponse(queryRequest,freightTemplateDeliveryType.INTRA_CITY_LOGISTICS);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryExpressArrived(FreightTemplateDeliveryAreaListRequest queryRequest) {
		return getListBaseResponse(queryRequest,freightTemplateDeliveryType.EXPRESS_ARRIVED);
	}

	@Override
	public BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryByStoreId(FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq) {
		FreightTemplateDeliveryAreaQueryRequest queryReq = new FreightTemplateDeliveryAreaQueryRequest();
		KsBeanUtil.copyPropertiesThird(freightTemplateDeliveryAreaListReq, queryReq);
		queryReq.setDelFlag(DeleteFlag.NO);
		List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreas = baseQuery(queryReq);

		if(CollectionUtils.isEmpty(freightTemplateDeliveryAreas)){
			return BaseResponse.success(Lists.newArrayList());
		}

		List<FreightTemplateDeliveryAreaVO> result = Lists.newArrayList();

		freightTemplateDeliveryAreas.forEach(f->{
			result.add(baseWrapperAreaVO(f));
		});

		return BaseResponse.success(result);
	}

}

