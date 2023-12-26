package com.wanmi.sbc.tms.api.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-19 15:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsOrderTradeStatusUpdateVO implements Serializable {
    private static final long serialVersionUID = 5620795633028040075L;

    //   ("交易订单ID")
    private String tradeOrderId;

    //   ("承运单ID")
    private String tmsOrderId;


    //   ("接货点ID"),商家发货操作使用
    private String shipmentSiteId;

    //   ("接货点名称") 商家发货操作使用
    private String shipmentSiteName;
    //   发货商品信息 商家发货操作使用

    //   ("第三方发货单号")
    private String thirdPartyDeliveryOrderNo;

    //   ("第三方发货单号类型")
    private String thirdPartyDeliveryType;

    // 发货信息
    private List<TmsOrderTradeStatusGoodsVO> goods;

}
