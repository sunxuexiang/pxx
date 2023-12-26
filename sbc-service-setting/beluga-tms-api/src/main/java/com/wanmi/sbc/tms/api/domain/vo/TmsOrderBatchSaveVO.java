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
 * @create: 2023-09-19 09:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmsOrderBatchSaveVO implements Serializable {
    private static final long serialVersionUID = 1466616553963975952L;

    //   ("承运商信息")
    private TmsOrderSaveVO tmsOrder;

    //   ("订单信息")
    private TmsOrderTradeSaveVO tradeOrder;

    //   ("订单商品信息")
    private List<TmsOrderTradeItemSaveVO> items;

}
