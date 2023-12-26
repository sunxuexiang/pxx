package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.returnorder.bean.dto.TradeDeliveryWayHomeFlagDTO;
import com.wanmi.sbc.returnorder.trade.request.CalcTradeDeliveryWayParam;
import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.vo.TmsSitePickupQueryVO;
import com.wanmi.sbc.tms.api.domain.vo.TmsSiteVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @desc  
 * @author shiy  2023/11/13 9:53
*/
@Service
@Slf4j
public class LocalTmsCarrierService extends TradeTmsService{

    private static final String REMOTE_TMS_CARRIER_SERVICE = "remoteTmsCarrierService";
    public TradeDeliveryWayHomeFlagDTO getToStorePickSit(CalcTradeDeliveryWayParam param) {
        TmsSitePickupQueryVO tmsSitePickupQueryVO = new TmsSitePickupQueryVO();
        tmsSitePickupQueryVO.setBuyerProvinceCode(param.getConsignee().getProvinceId().intValue());
        tmsSitePickupQueryVO.setBuyerCityCode(param.getConsignee().getCityId().intValue());
        tmsSitePickupQueryVO.setBuyerDistrictCode(param.getConsignee().getAreaId().intValue());
        tmsSitePickupQueryVO.setBuyerStreetCode(param.getConsignee().getTwonId().intValue());
        tmsSitePickupQueryVO.setCarrierProvinceCode(param.getCompanyMallBulkMarketVO().getProvinceId().intValue());
        String apiName = "listCarrierPickup";
        log.info(param_to_tms,REMOTE_TMS_CARRIER_SERVICE, apiName,JSONObject.toJSONString(tmsSitePickupQueryVO));
        R<List<TmsSiteVO>> r1 = remoteTmsCarrierService.listCarrierPickup(tmsSitePickupQueryVO);
        log.info(param_from_tms,REMOTE_TMS_CARRIER_SERVICE, apiName,JSONObject.toJSONString(r1));
        checkTmsResponse(r1);
        List<TmsSiteVO> tmsSiteVOS = r1.getData();
        if(CollectionUtils.isNotEmpty(tmsSiteVOS)) {
            TmsSiteVO tmsSiteVO = tmsSiteVOS.get(0);
            TradeDeliveryWayHomeFlagDTO toStorePickSit = new TradeDeliveryWayHomeFlagDTO();
            toStorePickSit.setFlag(DefaultFlag.NO);
            //toStorePickSit.setAdress("");
            toStorePickSit.setNetworkAddress(tmsSiteVO.getAddress());
            toStorePickSit.setContacts(tmsSiteVO.getContactPerson());
            toStorePickSit.setNetworkId(tmsSiteVO.getSiteId());
            toStorePickSit.setPhone(tmsSiteVO.getContactPhone());
            toStorePickSit.setNetworkName(tmsSiteVO.getSiteName());
            toStorePickSit.setProvince(Objects.toString(tmsSiteVO.getProvinceCode()));
            toStorePickSit.setCity(Objects.toString(tmsSiteVO.getCityCode()));
            toStorePickSit.setArea(Objects.toString(tmsSiteVO.getDistrictCode()));
            toStorePickSit.setTown(Objects.toString(tmsSiteVO.getStreetCode()));
            toStorePickSit.setProvinceName(tmsSiteVO.getProvinceName());
            toStorePickSit.setCityName(tmsSiteVO.getCityName());
            toStorePickSit.setAreaName(tmsSiteVO.getDistrictName());
            toStorePickSit.setTownName(tmsSiteVO.getStreet());
            return toStorePickSit;
        }
        return null;
    }

    public TmsSiteVO getShipmentSiteById(String shipmentSiteId){
        String apiName = "getSiteById";
        log.info(param_to_tms,REMOTE_TMS_CARRIER_SERVICE, apiName,shipmentSiteId);
        R<TmsSiteVO> r = remoteTmsCarrierService.getSiteById(shipmentSiteId);
        log.info(param_from_tms,REMOTE_TMS_CARRIER_SERVICE, apiName,JSONObject.toJSONString(r));
        checkTmsResponse(r);
        TmsSiteVO siteVO = r.getData();
        if(siteVO==null){
            siteVO = new TmsSiteVO();
        }
        return siteVO;
    }
}
