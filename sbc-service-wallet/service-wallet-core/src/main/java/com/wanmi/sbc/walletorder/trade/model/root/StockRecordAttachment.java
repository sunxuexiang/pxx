package com.wanmi.sbc.walletorder.trade.model.root;

import lombok.*;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockRecordAttachment implements Serializable {
    private static final long serialVersionUID = -7131928081696360778L;
    /**
     * 订单id
     */
    private String tid;


    /**
     * 囤货订单id
     */
    private String orderCode;

    public static StockRecordAttachment convertFromNativeSQLResult(Object result) {
        StockRecordAttachment response = new StockRecordAttachment();
        response.setOrderCode((String) ((Object[]) result)[0]);
        response.setTid(((String) ((Object[]) result)[1]));
        return response;
    }

}
