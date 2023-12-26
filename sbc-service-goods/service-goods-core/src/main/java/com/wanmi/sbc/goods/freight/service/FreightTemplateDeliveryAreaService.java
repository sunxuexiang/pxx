package com.wanmi.sbc.goods.freight.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.request.freight.FreightStoreSyncRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveListRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveRequest;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateDeliveryArea;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateDeliveryAreaRepository;
import com.wanmi.sbc.goods.freight.request.FreightTemplateDeliveryAreaQueryRequest;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyProvider;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanySyncRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>配送到家范围业务逻辑</p>
 *
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@Service("FreightTemplateDeliveryAreaService")
@Slf4j
public class FreightTemplateDeliveryAreaService {
    @Autowired
    private FreightTemplateDeliveryAreaRepository freightTemplateDeliveryAreaRepository;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;
    @Autowired
    private LogisticsCompanyProvider logisticsCompanyProvider;

    @Autowired
    private VillagesAddressConfigQueryProvider villagesAddressConfigQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaRedisService freightTemplateDeliveryAreaRedisService;

    /**
     * 新增配送到家范围
     *
     * @author zhaowei
     */
    @Transactional
    public FreightTemplateDeliveryArea saveEntity(FreightTemplateDeliveryArea entity) {
        entity.setDelFlag(DeleteFlag.NO);
        entity.setCreateTime(LocalDateTime.now());
        freightTemplateDeliveryAreaRepository.save(entity);
        boolean isNeedCache = (Constants.BOSS_DEFAULT_COMPANY_INFO_ID.compareTo(entity.getCompanyInfoId())==0) && entity.getDestinationType()!=null;
        if(isNeedCache){
            deleteRedis(entity.getStoreId(), entity.getDestinationType().toValue());
        }
        /*if(freightTemplateDeliveryType.isStoreVillage(entity.getDestinationType())){
            if(org.apache.commons.lang3.StringUtils.isNotBlank(entity.getDestinationArea())){
                List<Long> villagelist = Arrays.stream(entity.getDestinationArea().split(",")).map(Long::parseLong).collect(Collectors.toList());
                List<VillagesAddressConfigVO> villagesAddressConfigVOList = villagesAddressConfigQueryProvider.findListVillageByVillageIdList(VillagesAddressConfigQueryRequest.builder().villagesIds(villagelist).build()).getContext().getVillagesAddressConfigVOList();

            }
        }*/
        return entity;
    }

    @Transactional
    public void saveRequest(FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest) {
        FreightTemplateDeliveryArea freightTemplateDeliveryArea = new FreightTemplateDeliveryArea();
        KsBeanUtil.copyPropertiesThird(freightTemplateDeliveryAreaSaveRequest, freightTemplateDeliveryArea);
        if(null!=freightTemplateDeliveryAreaSaveRequest.getDestinationArea()) {
            freightTemplateDeliveryArea.setDestinationArea(org.apache.commons.lang3.StringUtils.join(freightTemplateDeliveryAreaSaveRequest.getDestinationArea(), ","));

            if(Constants.BOSS_DEFAULT_STORE_ID.compareTo(freightTemplateDeliveryArea.getCompanyInfoId())==0 && (freightTemplateDeliveryType.isDeliverytostore5(freightTemplateDeliveryAreaSaveRequest.getDestinationType()) || freightTemplateDeliveryType.isDeliverytostore10(freightTemplateDeliveryAreaSaveRequest.getDestinationType()))){
                saveBossCompanyConfig(freightTemplateDeliveryAreaSaveRequest, freightTemplateDeliveryArea);
            }
        }
        if(null!=freightTemplateDeliveryAreaSaveRequest.getDestinationAreaName()) {
            freightTemplateDeliveryArea.setDestinationAreaName(org.apache.commons.lang3.StringUtils.join(freightTemplateDeliveryAreaSaveRequest.getDestinationAreaName(), ","));
        }
        if(null!=freightTemplateDeliveryAreaSaveRequest.getCustomCfgVO()){
            freightTemplateDeliveryArea.setCustomCfg(JSONObject.toJSONString(freightTemplateDeliveryAreaSaveRequest.getCustomCfgVO()));
        }
        saveEntity(freightTemplateDeliveryArea);
    }

    private void saveBossCompanyConfig(FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest, FreightTemplateDeliveryArea freightTemplateDeliveryArea) {
        Map<String,List<String>> provinceMap = new HashMap<>(32);
        for(String areaCode: freightTemplateDeliveryAreaSaveRequest.getDestinationArea()){
            String provinceId = areaCode.substring(0,2);
            List<String> areaList =  provinceMap.get(provinceId);
            if(areaList==null){
                areaList = new ArrayList<>(freightTemplateDeliveryAreaSaveRequest.getDestinationArea().length);
            }
            areaList.add(areaCode);
            provinceMap.put(provinceId,areaList);
        }
        if(freightTemplateDeliveryType.isDeliverytostore10(freightTemplateDeliveryArea.getDestinationType())){
            List<Long> storeIdList = freightTemplateDeliveryAreaRepository.getBossDeliveryCfg(freightTemplateDeliveryType.DELIVERYTOSTORE_5.toValue());
            for(String area:provinceMap.keySet()){
                Long storeId =Long.valueOf(area+"0000");
                storeIdList.remove(storeId);
            }
           if(!CollectionUtils.isEmpty(storeIdList)){
               storeIdList.forEach(s->{
                   if(null==provinceMap.get(s)){
                       provinceMap.put(s.toString(),new ArrayList<>(0));
                   }
               });
            }
        }
        for (Map.Entry<String,List<String>> provinceEntry:provinceMap.entrySet()){
            Long storeId =Constants.BOSS_DEFAULT_STORE_ID;
            if(provinceEntry.getKey().length()==2){
                storeId =Long.valueOf(provinceEntry.getKey()+"0000");
            }else{
                storeId=Long.valueOf(provinceEntry.getKey());
            }
            List<String> areaList = provinceEntry.getValue();
            List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreaList = freightTemplateDeliveryAreaRepository.findByStoreIdAndDestinationType(storeId, freightTemplateDeliveryArea.getDestinationType().toValue());
            FreightTemplateDeliveryArea provinceTemplateDeliveryArea = new FreightTemplateDeliveryArea();
            if(!CollectionUtils.isEmpty(freightTemplateDeliveryAreaList)){
                provinceTemplateDeliveryArea =freightTemplateDeliveryAreaList.get(0);
            }else{
                provinceTemplateDeliveryArea.setStoreId(storeId);
                provinceTemplateDeliveryArea.setDestinationType(freightTemplateDeliveryArea.getDestinationType());
                provinceTemplateDeliveryArea.setCompanyInfoId(freightTemplateDeliveryArea.getCompanyInfoId());
                provinceTemplateDeliveryArea.setDelFlag(freightTemplateDeliveryArea.getDelFlag());
                provinceTemplateDeliveryArea.setFreightFreeNumber(freightTemplateDeliveryArea.getFreightFreeNumber());
                provinceTemplateDeliveryArea.setWareId(freightTemplateDeliveryArea.getWareId());
                provinceTemplateDeliveryArea.setOpenFlag(freightTemplateDeliveryArea.getOpenFlag());
                provinceTemplateDeliveryArea.setCustomCfg(freightTemplateDeliveryArea.getCustomCfg());
                provinceTemplateDeliveryArea.setCreateTime(freightTemplateDeliveryArea.getCreateTime());
            }
            provinceTemplateDeliveryArea.setDestinationArea(String.join(",",areaList));
            provinceTemplateDeliveryArea.setDestinationAreaName(provinceTemplateDeliveryArea.getDestinationArea());
            saveEntity(provinceTemplateDeliveryArea);
        }
    }

    public void deleteRedis(Long storeId,Integer destinationType) {
        freightTemplateDeliveryAreaRedisService.delete(storeId, destinationType);
    }

    @Transactional
    public void updateOpenFlag(FreightTemplateDeliveryAreaSaveListRequest listRequest) {
        listRequest.getFreightTemplateDeliveryAreaList().forEach(area->{
            freightTemplateDeliveryAreaRepository.updateOpenFlag(area.getOpenFlag(),area.getStoreId(),area.getDestinationType().toValue());
        });
    }

    /***
     * @desc  开仓初始化
     * @author shiy  2023/7/4 9:48
     */
    @Transactional
    public void initByStoreId(FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest) {
        List<Integer> deliveryTypeList = null;
        /*if(freightTemplateDeliveryAreaSaveRequest.getSystemInit()!=1 && freightTemplateDeliveryAreaSaveRequest.getStoreId()>0) {
            CompanyMallSupplierTabResponse tabResponse = companyIntoPlatformQueryProvider.getSupplierByStoreId(freightTemplateDeliveryAreaSaveRequest.getStoreId()).getContext();
            CompanyMallBulkMarketVO mallBulkMarketVO = companyIntoPlatformQueryProvider.getMarketByStoreId(freightTemplateDeliveryAreaSaveRequest.getStoreId()).getContext();
            if(tabResponse!=null){
                if(mallBulkMarketVO!=null && null!=mallBulkMarketVO.getCityId() && mallBulkMarketVO.getCityId().compareTo(430100L)==0) {
                    deliveryTypeList = tabResponse.getDeliveryTypeList();
                    log.info("商家[{}]初始化物流配送方式.商城[{}],商城配送方式[{}]",freightTemplateDeliveryAreaSaveRequest.getStoreId(),tabResponse.getId(), JSONObject.toJSONString(deliveryTypeList));
                }else{
                    log.info("商家[{}]所属批发市场所在城市不属于长沙，不复制物流",freightTemplateDeliveryAreaSaveRequest.getStoreId());
                }
            }else{
                log.info("商家[{}]初始化物流配送方式.无商城信息",freightTemplateDeliveryAreaSaveRequest.getStoreId());
            }
        }*/
        if(deliveryTypeList == null){
            deliveryTypeList = new ArrayList<>(6);
        }
        //boolean isThirdlogistics = deliveryTypeList.contains(freightTemplateDeliveryType.THIRDLOGISTICS.toValue());
        //boolean isSpecifyLogistics = deliveryTypeList.contains(freightTemplateDeliveryType.SPECIFY_LOGISTICS.toValue());

        FreightTemplateDeliveryAreaQueryRequest queryRequest = FreightTemplateDeliveryAreaQueryRequest.builder().
                storeId(freightTemplateDeliveryAreaSaveRequest.getStoreId()).delFlag(DeleteFlag.NO).
                companyInfoId(freightTemplateDeliveryAreaSaveRequest.getCompanyInfoId()).
                wareId(freightTemplateDeliveryAreaSaveRequest.getWareId()).build();
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.CONVENTION,0,5L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.AREATENDELIVER,1,10L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.THIRDLOGISTICS,1,1L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.TODOORPICK,1,1L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.PAIDEXPRESS,0,1L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.DELIVERYTOSTORE,1,1L);
        if(Constants.BOSS_DEFAULT_STORE_ID.compareTo(freightTemplateDeliveryAreaSaveRequest.getStoreId())==0) {
            initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.DELIVERYTOSTORE,1,1L);
            initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest, freightTemplateDeliveryType.DELIVERYTOSTORE_5, 1, 5L);
            initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest, freightTemplateDeliveryType.DELIVERYTOSTORE_10, 1, 10L);
        }else{
            initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.DELIVERYTOSTORE,1,1L);
        }
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.SPECIFY_LOGISTICS,1,1L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.INTRA_CITY_LOGISTICS,1,1L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.EXPRESS_ARRIVED,1,1L);
        initByDeliveryType(freightTemplateDeliveryAreaSaveRequest, queryRequest,freightTemplateDeliveryType.EXPRESS_SELF_PAID,1,1L);
        /*if(isSpecifyLogistics) {
            syncLogisticsCompany(queryRequest.getStoreId(),LogisticsType.SPECIFY_LOGISTICS.toValue());
        }
        if(isThirdlogistics) {
            syncLogisticsCompany(queryRequest.getStoreId(),LogisticsType.THIRD_PARTY_LOGISTICS.toValue());
        }*/
    }

    private void syncLogisticsCompany(Long targetStoreId,Integer logisticsType) {
        if(true){
            return;
        }
        LogisticsCompanySyncRequest syncRequest = new LogisticsCompanySyncRequest();
        syncRequest.setSourceStoreId(123457929L);
        syncRequest.setTargetStoreIdList(Arrays.asList(targetStoreId));
        syncRequest.setLogisticsType(logisticsType);
        logisticsCompanyProvider.syncLogisticsCompany(syncRequest);
    }

    private void initByDeliveryType(FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest, FreightTemplateDeliveryAreaQueryRequest queryRequest,freightTemplateDeliveryType deliveryType,Integer openFlag,Long freeNumber) {
        queryRequest.setDestinationType(deliveryType.toValue());
        long iCount = freightTemplateDeliveryAreaRepository.count(FreightTemplateDeliveryAreaWhereCriteriaBuilder.build(queryRequest));
        if(iCount==0) {
            freightTemplateDeliveryAreaSaveRequest.setOpenFlag(openFlag);
            freightTemplateDeliveryAreaSaveRequest.setFreightFreeNumber(freeNumber);
            initFreightTemplate(freightTemplateDeliveryAreaSaveRequest, deliveryType);
        }
    }

    private void initFreightTemplate(FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest, freightTemplateDeliveryType destinationType) {
        FreightTemplateDeliveryArea freightTemplateDeliveryArea = new FreightTemplateDeliveryArea();
        freightTemplateDeliveryArea.setStoreId(freightTemplateDeliveryAreaSaveRequest.getStoreId());
        freightTemplateDeliveryArea.setCompanyInfoId(freightTemplateDeliveryAreaSaveRequest.getCompanyInfoId());
        freightTemplateDeliveryArea.setWareId(freightTemplateDeliveryAreaSaveRequest.getWareId());
        freightTemplateDeliveryArea.setOpenFlag(freightTemplateDeliveryAreaSaveRequest.getOpenFlag());
        freightTemplateDeliveryArea.setFreightFreeNumber(freightTemplateDeliveryAreaSaveRequest.getFreightFreeNumber());
        freightTemplateDeliveryArea.setDestinationType(destinationType);
        freightTemplateDeliveryArea.setDelFlag(DeleteFlag.NO);
        freightTemplateDeliveryArea.setCreateTime(LocalDateTime.now());
        if(freightTemplateDeliveryArea.getOpenFlag()==1 && (freightTemplateDeliveryType.THIRDLOGISTICS == freightTemplateDeliveryArea.getDestinationType() ||freightTemplateDeliveryType.SPECIFY_LOGISTICS==freightTemplateDeliveryArea.getDestinationType())){
            freightTemplateDeliveryArea.setDestinationArea("430000");
            freightTemplateDeliveryArea.setDestinationAreaName("湖南省");
        }
        freightTemplateDeliveryAreaRepository.save(freightTemplateDeliveryArea);
    }


    /**
     * 列表查询配送到家范围
     *
     * @author zhaowei
     */
    public List<FreightTemplateDeliveryArea> query(FreightTemplateDeliveryAreaQueryRequest queryReq) {
        return freightTemplateDeliveryAreaRepository.findAll(
                FreightTemplateDeliveryAreaWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }

    /**
     * 将实体包装成VO
     *
     * @author zhaowei
     */
    public FreightTemplateDeliveryAreaVO wrapperVo(FreightTemplateDeliveryArea freightTemplateDeliveryArea) {
        if (freightTemplateDeliveryArea != null) {
            FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = new FreightTemplateDeliveryAreaVO();
            KsBeanUtil.copyPropertiesThird(freightTemplateDeliveryArea, freightTemplateDeliveryAreaVO);
            if(!StringUtils.isEmpty(freightTemplateDeliveryArea.getDestinationArea())){
                freightTemplateDeliveryAreaVO.setDestinationArea(freightTemplateDeliveryArea.getDestinationArea().split(","));
                freightTemplateDeliveryAreaVO.setDestinationAreaName(freightTemplateDeliveryArea.getDestinationAreaName().split(","));
            }
            freightTemplateDeliveryAreaVO.setDestinationType(freightTemplateDeliveryArea.getDestinationType());
            return freightTemplateDeliveryAreaVO;
        }
        return null;
    }

    public List<Long> getUseDeliveryToStoreSupplierList(Integer destinationType) {
        return freightTemplateDeliveryAreaRepository.getUseDeliveryToStoreSupplierList(destinationType);
    }

    @Transactional
    public void syncStoreDeliveryArea(FreightStoreSyncRequest freightStoreSyncRequest) {

        FreightTemplateDeliveryAreaQueryRequest queryRequest = FreightTemplateDeliveryAreaQueryRequest.builder().
                storeId(freightStoreSyncRequest.getSourceStoreId()).delFlag(DeleteFlag.NO).
                wareId(freightStoreSyncRequest.getWareId()).destinationType(freightStoreSyncRequest.getDestinationType()).build();

        List<FreightTemplateDeliveryArea> freightTemplateDeliveryAreaList = freightTemplateDeliveryAreaRepository.findAll(FreightTemplateDeliveryAreaWhereCriteriaBuilder.build(queryRequest));
        if(CollectionUtils.isEmpty(freightTemplateDeliveryAreaList)){
            throw new SbcRuntimeException("同步失败，源店铺规则不存在");
        }
        FreightTemplateDeliveryArea temp = freightTemplateDeliveryAreaList.get(freightTemplateDeliveryAreaList.size()-1);
        freightTemplateDeliveryAreaRepository.updateDeliveryAreaByStore(freightStoreSyncRequest.getTargetStoreIdList(),freightStoreSyncRequest.getWareId(),freightStoreSyncRequest.getDestinationType());
        freightStoreSyncRequest.getTargetStoreIdList().forEach(storeId->{
            FreightTemplateDeliveryArea entity = new FreightTemplateDeliveryArea();
            KsBeanUtil.copyPropertiesThird(temp,entity);
            entity.setId(null);
            StoreVO storeVO = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(storeId).build()).getContext().getStoreVO();
            entity.setStoreId(storeId);
            entity.setWareId(freightStoreSyncRequest.getWareId());
            entity.setCompanyInfoId(storeVO.getCompanyInfo().getCompanyInfoId());
            saveEntity(entity);
        });
    }

    public void initByBossStoreId() {
    }
}
