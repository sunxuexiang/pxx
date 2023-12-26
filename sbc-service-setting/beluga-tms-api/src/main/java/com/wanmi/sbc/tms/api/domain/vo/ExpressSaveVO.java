package com.wanmi.sbc.tms.api.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 快递到家-运单信息汇总
 * @author jkp
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressSaveVO implements Serializable {

    //("运单信息")
    private ExpressOrderSaveVO orderSaveVO;

    //("订单信息")
    private ExpressTradeSaveVO tradeSaveVO;

    //("订单商品信息")
    private List<ExpressTradeItemSaveVO> items;

}
