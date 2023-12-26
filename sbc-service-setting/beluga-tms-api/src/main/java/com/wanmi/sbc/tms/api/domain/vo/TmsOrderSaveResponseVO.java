package com.wanmi.sbc.tms.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-19 15:14
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmsOrderSaveResponseVO implements Serializable {
    private static final long serialVersionUID = -7538947624163028847L;

    /**
     * 运单信息
     */
    private String tmsOrderId;

    /**
     * 交易订单信息
     */
    private String tradeOrderId;
}
