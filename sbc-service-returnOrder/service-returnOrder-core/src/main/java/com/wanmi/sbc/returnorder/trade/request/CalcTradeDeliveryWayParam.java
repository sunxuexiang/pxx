package com.wanmi.sbc.returnorder.trade.request;

import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Consignee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName CalcTradeDeliveryWayParam
 * @Description TODO
 * @Author shiy
 * @Date 2023/9/18 12:19
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalcTradeDeliveryWayParam {
    private Consignee consignee;
    private Long provinceId;
    private Long cityId;
    private Long tradeGoodsTotalNum;
    private Long storeId;
    private Long wareId;
    private Long marketGoodsTotalNum;
    private List<Integer> mallDeliveryTypeList;
    private CompanyMallBulkMarketVO companyMallBulkMarketVO;
    private CompanyMallSupplierTabVO companyMallSupplierTabVO;
}
