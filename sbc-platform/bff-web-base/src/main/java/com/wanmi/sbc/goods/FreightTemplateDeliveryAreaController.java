package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.request.FreightTemplateDeliveryAreaByAddressIdRequest;
import com.wanmi.sbc.order.api.provider.historydeliver.HistoryOrderDeliverWayProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeByCustomerIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeDeliveryWayRequest;
import com.wanmi.sbc.order.api.response.trade.TradeDeliveryWayResponse;
import com.wanmi.sbc.order.bean.dto.TradeDeliveryWayHomeFlagDTO;
import com.wanmi.sbc.order.bean.dto.TradeDeliveryWayResDTO;
import com.wanmi.sbc.order.bean.dto.TradeDeliveryWayResMergeDTO;
import com.wanmi.sbc.order.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayQueryVO;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayVO;
import com.wanmi.sbc.order.service.DeliveryhomeCfgHubei;
import com.wanmi.sbc.setting.api.provider.homedelivery.HomeDeliveryQueryProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyByIdRequest;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyByIdResponse;
import com.wanmi.sbc.setting.bean.enums.LogisticsType;
import com.wanmi.sbc.setting.bean.vo.HomeDeliveryVO;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyResponseVO;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 免费店配Controller
 * Created by daiyitian on 17/4/12.
 */
@RestController
@RequestMapping("/freight")
@Api(tags = "FreightTemplateDeliveryAreaController", description = "S2B web公用-免费店配信息API")
@Slf4j
public class FreightTemplateDeliveryAreaController {

    private static final String DELIVERY_REMARK = "   ";
    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private VillagesAddressConfigQueryProvider villagesAddressConfigQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private DeliveryhomeCfgHubei deliveryhomeCfgHubei;

    @Autowired
    private HomeDeliveryQueryProvider homeDeliveryQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Autowired
    private HistoryOrderDeliverWayProvider historyOrderDeliverWayProvider;

    /**
     * 查询乡镇免费店配信息
     *
     * @param queryRequest
     * @return 返回结果
     */
    @ApiOperation(value = "查询是否满足乡镇免费店配")
    @RequestMapping(value = "/checkFreightTemplateDeliveryAreaByAddressId", method = RequestMethod.POST)
    public BaseResponse<DeleteFlag> checkFreightTemplateDeliveryAreaByAddressId(@RequestBody FreightTemplateDeliveryAreaByAddressIdRequest queryRequest) {

        if(Objects.isNull(queryRequest.getAddressId()) || Objects.isNull(queryRequest.getStoreId())){
            throw new SbcRuntimeException("K-000009");
        }

        CustomerDeliveryAddressByIdResponse addressByIdResponse = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder()
                .deliveryAddressId(queryRequest.getAddressId())
                .build())
                .getContext();

        if(Objects.isNull(addressByIdResponse)){
            return BaseResponse.success(DeleteFlag.NO);
        }

        //暂时只有湖南省内 写死 后续可以用枚举
        if (Objects.nonNull(addressByIdResponse.getProvinceId())
//                && 430000 == addressByIdResponse.getProvinceId()
        ) {
            //如果四级地址（乡、镇）不为空且不为-1
            if (Objects.nonNull(addressByIdResponse.getTwonId()) && -1 != addressByIdResponse.getTwonId()) {
                Integer count = villagesAddressConfigQueryProvider.getCountByAllId(VillagesAddressConfigQueryRequest.builder()
                        .provinceId(addressByIdResponse.getProvinceId()).cityId(addressByIdResponse.getCityId())
                        .areaId(addressByIdResponse.getAreaId()).villageId(addressByIdResponse.getTwonId()).storeId(queryRequest.getStoreId()).build()).getContext();
                if (Objects.nonNull(count) && count > 0) {
                    return BaseResponse.success(DeleteFlag.YES);
                }
            } else {
                //根据省、市、区id获取乡镇件地址信息
                List<VillagesAddressConfigVO> villagesAddressConfigVOList = villagesAddressConfigQueryProvider.list(VillagesAddressConfigQueryRequest.builder()
                        .provinceId(addressByIdResponse.getProvinceId()).cityId(addressByIdResponse.getCityId())
                        .areaId(addressByIdResponse.getAreaId()).storeId(queryRequest.getStoreId()).build()).getContext().getVillagesAddressConfigVOList();
                if (CollectionUtils.isNotEmpty(villagesAddressConfigVOList)) {
                    for (VillagesAddressConfigVO config : villagesAddressConfigVOList) {
                        //如果收货地址街道为手动填写 则用填写街道地址去比较乡镇件
                        if (Objects.nonNull(addressByIdResponse.getTwonName())) {
                            if (addressByIdResponse.getTwonName().contains(config.getVillageName())) {
                                return BaseResponse.success(DeleteFlag.YES);
                            }
                        } else {
                            if (addressByIdResponse.getDeliveryAddress().contains(config.getVillageName())) {
                                return BaseResponse.success(DeleteFlag.YES);
                            }
                        }
                    }
                }
            }
        }
        return BaseResponse.success(DeleteFlag.NO);
    }


    /**
     * 订单配送方式
     *
     * @param queryRequest
     * @return 返回结果
     */
    @ApiOperation(value = "订单配送方式")
    @RequestMapping(value = "/checkFreightTemplateDelivery", method = RequestMethod.POST)
    public BaseResponse<TradeDeliveryWayResponse> checkFreightTemplateDelivery(@RequestBody TradeDeliveryWayRequest queryRequest) {

        if(Objects.isNull(queryRequest.getAddressId()) || CollectionUtils.isEmpty(queryRequest.getDeliveryWayDTOS())){
            throw new SbcRuntimeException("K-000009");
        }
        List<TradeDeliveryWayResMergeDTO> resMergeDTOList = new ArrayList<>(0);
        List<TradeDeliveryWayResMergeDTO> resIOSMergeDTOList = new ArrayList<>(0);
        BaseResponse<TradeDeliveryWayResponse> response = tradeProvider.getTradeDiliveryAreaVo(queryRequest);
        if(CollectionUtils.isNotEmpty(response.getContext().getResDTOList())) {
            HomeDeliveryVO homeDeliveryVO = homeDeliveryQueryProvider.getBossCfg().getContext().getHomeDeliveryVO();
            /*boolean isHubeiSitePickSelf = null != response.getContext().getResDTOList().stream().filter(res -> res.getCompanyType() == CompanyType.PLATFORM.toValue() && res.getProvinceId() == 420000).findAny().orElse(null);
            CustomerDeliveryAddressByIdResponse deliveryAddressByIdResponse;
            if (isHubeiSitePickSelf) {
                deliveryAddressByIdResponse = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                        .builder().deliveryAddressId(queryRequest.getAddressId()).build()).getContext();
            } else {
                deliveryAddressByIdResponse = null;
            }*/
            Map<Long, List<TradeDeliveryWayResDTO>> storeTempListMap = new HashMap<>(queryRequest.getDeliveryWayDTOS().size());
            response.getContext().getResDTOList().forEach(res -> {
                /*if (res.getCompanyType() == CompanyType.PLATFORM.toValue() && res.getProvinceId() == 420000) {
                    DeliveryHomeFlagResponse resultResponse = new DeliveryHomeFlagResponse();
                    resultResponse.setAdress("");
                    if (deliveryAddressByIdResponse != null) {
                        DeliveryHomeFlagResponse hubeiResultResponse = deliveryhomeCfgHubei.getHubeiDeliveryHomeFlagResponse(queryRequest.getAddressId(), resultResponse, deliveryAddressByIdResponse);
                        res.setHomeFlagDTO(KsBeanUtil.copyPropertiesThird(hubeiResultResponse, TradeDeliveryWayHomeFlagDTO.class));
                    }
                }*/
                setResDeliveryRemark(homeDeliveryVO, res);
                List<TradeDeliveryWayResDTO> resDTOList = storeTempListMap.get(res.getStoreId());
                if (resDTOList == null) {
                    resDTOList = new ArrayList<TradeDeliveryWayResDTO>(6);
                    storeTempListMap.put(res.getStoreId(), resDTOList);
                }
                if(DeliverWay.isLogistics(res.getDeliverWay()) && StringUtils.isNotBlank(queryRequest.getCustomerId())){
                    int logisticType =DeliverWay.LOGISTICS.equals(res.getDeliverWay())? LogisticsType.THIRD_PARTY_LOGISTICS.toValue():LogisticsType.SPECIFY_LOGISTICS.toValue();
                    HistoryLogisticsCompanyVO historyLogisticsCompanyVO = tradeQueryProvider.getHistoryVoByCustomerIdAndMarketId(TradeByCustomerIdRequest.builder().customerId(queryRequest.getCustomerId()).marketId(res.getMallBulkMarketId()).logisticsType(logisticType).build()).getContext();
                    if(historyLogisticsCompanyVO!=null &&  historyLogisticsCompanyVO.getCompanyId()>0){
                        Long logisticId = historyLogisticsCompanyVO.getCompanyId();
                        LogisticsCompanyByIdResponse companyByIdResponse = logisticsCompanyQueryProvider.getById(LogisticsCompanyByIdRequest.builder().id(logisticId).build()).getContext();
                        if (Objects.nonNull(companyByIdResponse) && Objects.nonNull(companyByIdResponse.getLogisticsCompanyVO())) {
                            res.setLogisticsCompanyVO(KsBeanUtil.copyPropertiesThird(companyByIdResponse.getLogisticsCompanyVO(), LogisticsCompanyResponseVO.class));
                            res.getLogisticsCompanyVO().setReceivingPoint(historyLogisticsCompanyVO.getReceivingSite());
                        }
                    }
                }
                res.setDeliverWayDesc(getDeliverWayDesc(res));
                resDTOList.add(res);
            });
            Map<Long, List<HistoryOrderDeliverWayVO>> storeMap=null;
            if(storeTempListMap.size()>0) {
                List<HistoryOrderDeliverWayVO> historyOrderDeliverWayVOList = historyOrderDeliverWayProvider.queryDeliverWayByStoreIdAndCustomerId(HistoryOrderDeliverWayQueryVO.builder().storeIdList(new ArrayList<>(storeTempListMap.keySet())).customerId(queryRequest.getCustomerId()).build()).getContext();
                storeMap = historyOrderDeliverWayVOList.stream().collect(Collectors.groupingBy(HistoryOrderDeliverWayVO::getStoreId));
            }
            //配合APP造数据，APP说前端数据太麻烦
            resMergeDTOList = new ArrayList<>(storeTempListMap.size());
            String spiltA=",";
            for (Map.Entry<Long, List<TradeDeliveryWayResDTO>> storeTempEntry : storeTempListMap.entrySet()) {
                List<TradeDeliveryWayResDTO> resDTOList = storeTempEntry.getValue();
                TradeDeliveryWayResMergeDTO resMergeDTO = new TradeDeliveryWayResMergeDTO();
                resMergeDTOList.add(resMergeDTO);
                for (int i = 0; i < resDTOList.size(); i++) {
                    TradeDeliveryWayResDTO resDTO = resDTOList.get(i);
                    String mergeDeliveryRemark = resDTO.getDeliveryRemark();
                    if(StringUtils.isBlank(mergeDeliveryRemark)){
                        mergeDeliveryRemark = DELIVERY_REMARK;
                    }
                    if(i==0){
                        resMergeDTO.setStoreId(resDTO.getStoreId());
                        resMergeDTO.setWareId(resDTO.getWareId());
                        resMergeDTO.setCompanyId(resDTO.getCompanyId());
                        resMergeDTO.setDeliverWay(resDTO.getDeliverWay().toValue().toString());
                        resMergeDTO.setDeliverWayDesc(getDeliverWayDesc(resDTO));
                        resMergeDTO.setMatchFlag(resDTO.getMatchFlag().toString());
                        resMergeDTO.setCompanyType(resDTO.getCompanyType());
                        resMergeDTO.setFreightFreeNumber(resDTO.getFreightFreeNumber().toString());
                        resMergeDTO.setTemplateId(resDTO.getTemplateId().toString());
                        resMergeDTO.setProvinceId(resDTO.getProvinceId());
                        resMergeDTO.setPatchFlag(resDTO.getPatchFlag());
                        resMergeDTO.setMallBulkMarketId(resDTO.getMallBulkMarketId());
                        resMergeDTO.setMallSupplierTabId(resDTO.getMallSupplierTabId());
                        resMergeDTO.setMallBulkMarkeName(resDTO.getMallBulkMarkeName());
                        resMergeDTO.setMallSupplierTabName(resDTO.getMallSupplierTabName());
                        resMergeDTO.setDeliveryRemark(mergeDeliveryRemark);
                    }else{
                        resMergeDTO.setDeliverWay(resMergeDTO.getDeliverWay()+spiltA+resDTO.getDeliverWay().toValue().toString());
                        resMergeDTO.setDeliverWayDesc(resMergeDTO.getDeliverWayDesc()+spiltA+ getDeliverWayDesc(resDTO));
                        resMergeDTO.setMatchFlag(resMergeDTO.getMatchFlag()+spiltA+resDTO.getMatchFlag().toString());
                        resMergeDTO.setFreightFreeNumber(resMergeDTO.getFreightFreeNumber()+spiltA+resDTO.getFreightFreeNumber().toString());
                        resMergeDTO.setTemplateId(resMergeDTO.getTemplateId()+spiltA+resDTO.getTemplateId().toString());
                        resMergeDTO.setDeliveryRemark(resMergeDTO.getDeliveryRemark()+spiltA+mergeDeliveryRemark);
                    }
                    if(storeMap!=null) {
                        List<HistoryOrderDeliverWayVO> historyOrderDeliverWayVOS = storeMap.get(resMergeDTO.getStoreId());
                        if (CollectionUtils.isNotEmpty(historyOrderDeliverWayVOS)) {
                            resMergeDTO.setLastDeliverWay(DeliverWay.fromValue(historyOrderDeliverWayVOS.get(0).getDeliverWay()));
                        }
                    }

                    if(resDTO.getHomeFlagDTO()!=null){
                        resMergeDTO.setHomeFlagDTO(resDTO.getHomeFlagDTO());
                    }
                    if(resDTO.getLogisticsCompanyVO()!=null){
                        if(DeliverWay.isDeliveryTYB(resDTO.getDeliverWay())) {
                            resMergeDTO.setLogisticsCompanyVOTYB(resDTO.getLogisticsCompanyVO());
                        }
                        if(DeliverWay.isDeliveryZDZX(resDTO.getDeliverWay())) {
                            resMergeDTO.setLogisticsCompanyVOZDZX(resDTO.getLogisticsCompanyVO());
                        }
                    }
                    if(resDTO.getToStorePickSit()!=null){
                        resMergeDTO.setToStorePickSit(resDTO.getToStorePickSit());
                    }
                }
            }
            resIOSMergeDTOList = new ArrayList<>(storeTempListMap.size());
            for (Map.Entry<Long, List<TradeDeliveryWayResDTO>> storeTempEntry : storeTempListMap.entrySet()) {
                List<TradeDeliveryWayResDTO> resDTOList = storeTempEntry.getValue();
                TradeDeliveryWayResMergeDTO resMergeDTO = new TradeDeliveryWayResMergeDTO();
                resIOSMergeDTOList.add(resMergeDTO);

                List<TradeDeliveryWayResDTO> storeWayList = new ArrayList<>(7);
                resMergeDTO.setStoreWayResDTOList(storeWayList);
                for (int i = 0; i < resDTOList.size(); i++) {
                    TradeDeliveryWayResDTO resDTO = resDTOList.get(i);
                    storeWayList.add(resDTO);
                    if(i==0){
                        resMergeDTO.setStoreId(resDTO.getStoreId());
                        resMergeDTO.setCompanyId(resDTO.getCompanyId());
                        resMergeDTO.setWareId(resDTO.getWareId());
                        resMergeDTO.setCompanyType(resDTO.getCompanyType());
                        resMergeDTO.setProvinceId(resDTO.getProvinceId());
                        resMergeDTO.setMallSupplierTabId(resDTO.getMallSupplierTabId());
                        resMergeDTO.setMallBulkMarketId(resDTO.getMallBulkMarketId());
                        resMergeDTO.setPatchFlag(resDTO.getPatchFlag());
                        resMergeDTO.setMallBulkMarkeName(resDTO.getMallBulkMarkeName());
                        resMergeDTO.setMallSupplierTabName(resDTO.getMallSupplierTabName());
                    }
                }
                if(storeMap!=null) {
                    List<HistoryOrderDeliverWayVO> historyOrderDeliverWayVOS = storeMap.get(resMergeDTO.getStoreId());
                    if (CollectionUtils.isNotEmpty(historyOrderDeliverWayVOS)) {
                        resMergeDTO.setLastDeliverWay(DeliverWay.fromValue(historyOrderDeliverWayVOS.get(0).getDeliverWay()));
                    }
                }
            }
        }
        /*Collections.sort(resIOSMergeDTOList,Comparator.comparingLong(TradeDeliveryWayResMergeDTO::getMallBulkMarketId));
        Collections.sort(resMergeDTOList,Comparator.comparingLong(TradeDeliveryWayResMergeDTO::getMallBulkMarketId));*/
        response.getContext().setResIOSMergeDTOList(resIOSMergeDTOList);
        response.getContext().setResMergeDTOList(resMergeDTOList);
        return response;
    }

    private static void setResDeliveryRemark(HomeDeliveryVO homeDeliveryVO, TradeDeliveryWayResDTO res) {
        if(res.getDeliverWay()==DeliverWay.LOGISTICS){
            res.setDeliveryRemark(homeDeliveryVO.getLogisticsContent());
        }else if (DeliverWay.isExpress(res.getDeliverWay())){
            res.setDeliveryRemark(homeDeliveryVO.getExpressContent());
        }else if (res.getDeliverWay()==DeliverWay.PICK_SELF){
            res.setDeliveryRemark(homeDeliveryVO.getPickSelfContent());
        }else if (res.getDeliverWay()==DeliverWay.DELIVERY_HOME){
            res.setDeliveryRemark(homeDeliveryVO.getContent());
        }else if (res.getDeliverWay()==DeliverWay.DELIVERY_TO_STORE){
            TradeDeliveryWayHomeFlagDTO homeFlagDTO = res.getToStorePickSit();
            StringBuffer tmp= new StringBuffer();
            if(homeFlagDTO!=null){
                tmp.append("自提地址: ")
                        .append(homeFlagDTO.getProvinceName())
                        .append(homeFlagDTO.getCityName())
                        .append(homeFlagDTO.getAreaName())
                        .append(homeFlagDTO.getTownName())
                        .append(homeFlagDTO.getNetworkAddress()==null? Constants.EMPTY_STR:homeFlagDTO.getNetworkAddress());
            }
            if(null!=homeDeliveryVO.getDeliveryToStoreContent()){
                tmp.append(CharUtils.LF).append(homeDeliveryVO.getDeliveryToStoreContent());
            }
            res.setDeliveryRemark(tmp.toString());
        }else if (res.getDeliverWay()==DeliverWay.TO_DOOR_PICK){
            TradeDeliveryWayHomeFlagDTO homeFlagDTO = res.getHomeFlagDTO();
            StringBuffer tmp= new StringBuffer();
            if(homeFlagDTO!=null){
                tmp.append("自提时间: 现在下单并支付，").append(homeFlagDTO.getPickNote()).append(CharUtils.LF);
                tmp.append("自提地址: ")
                        .append(homeFlagDTO.getProvinceName())
                        .append(homeFlagDTO.getCityName())
                        .append(homeFlagDTO.getAreaName())
                        .append(homeFlagDTO.getTownName())
                        .append(homeFlagDTO.getNetworkAddress()==null? Constants.EMPTY_STR:homeFlagDTO.getNetworkAddress());
            }
            if(null!=homeDeliveryVO.getDoorPickContent()){
                tmp.append(CharUtils.LF).append(homeDeliveryVO.getDoorPickContent());
            }
            res.setDeliveryRemark(tmp.toString());
        }else if (res.getDeliverWay()==DeliverWay.SPECIFY_LOGISTICS){
            res.setDeliveryRemark(homeDeliveryVO.getSpecifyLogisticsContent());
        }else if (res.getDeliverWay()==DeliverWay.INTRA_CITY_LOGISTICS){
            res.setDeliveryRemark(homeDeliveryVO.getIntraCityLogisticsContent());
        }else if (res.getDeliverWay()==DeliverWay.EXPRESS_ARRIVED){
            res.setDeliveryRemark(homeDeliveryVO.getExpressArrivedContent());
        }
        if(StringUtils.isBlank(res.getDeliveryRemark())){
            res.setDeliveryRemark(Constants.EMPTY_STR);
        }
    }

    private static String getDeliverWayDesc(TradeDeliveryWayResDTO res) {
        if(DeliverWay.LOGISTICS ==res.getDeliverWay()|| DeliverWay.SPECIFY_LOGISTICS ==res.getDeliverWay()){
            //return "托运部或指定物流(自费)";
            return res.getDeliverWay().getDesc();
        }else {
            return res.getDeliverWay().getDesc();
        }
    }
}
