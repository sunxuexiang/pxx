package com.wanmi.sbc.tms.api;


import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.vo.TmsSitePickupQueryVO;
import com.wanmi.sbc.tms.api.domain.vo.TmsSiteQueryVO;
import com.wanmi.sbc.tms.api.domain.vo.TmsSiteShipmentQueryVO;
import com.wanmi.sbc.tms.api.domain.vo.TmsSiteVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.wanmi.sbc.tms.api.domain.dto.TmsCarrierDTO;

import java.util.List;

/**
 * TMS大众物流remote服务
 */
@FeignClient(value = "beluga-tms", url="${feign.url.tms:#{null}}", contextId = "remoteTmsCarrierService")
public interface RemoteTmsCarrierService {

    /**
     * 获取承运人信息
     */
    @GetMapping(value = "/carrier/info/{id}", headers = {"from-source=inner"})
    R<TmsCarrierDTO> getCarrierById(@PathVariable("id") String id);


    /**
     * 获取承运人信息
     */
    @PostMapping(value = "/carrier/info/list-ids" , headers = {"from-source=inner"})
    R<List<TmsCarrierDTO>> listCarrierByIds(@RequestBody List<Long> ids);



    /**
     * 根据客户地址获取提货点信息
     */
    @PostMapping(value = "/site/list-carrier-pickup" , headers = {"from-source=inner}"})
    R<List<TmsSiteVO>> listCarrierPickup(@RequestBody TmsSitePickupQueryVO vo);

    /**
     * 根据市场获取接货点信息
     */
    @PostMapping(value = "/site/list-market-shipment" , headers = {"from-source=inner"})
    R<List<TmsSiteVO>> listMarketShipment(@RequestBody TmsSiteShipmentQueryVO vo);

    /**
     * 获取接货点信息
     */
    @GetMapping(value="/site/info/{id}", headers = {"from-source=inner"})
    R<TmsSiteVO> getSiteById(@PathVariable("id") String id);

    /**
     * 根据承运商id和批发市场id获取接货点信息
     */
    @PostMapping(value="/site/getSiteByCarrierId", headers = {"from-source=inner"})
    R<List<TmsSiteVO>> getSiteByCarrierId(@RequestBody TmsSiteShipmentQueryVO vo);


}
