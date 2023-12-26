package com.wanmi.sbc.tms.api.domain.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-19 15:19
 **/
@Data
public class TmsOrderTradePayVO implements Serializable {
    private static final long serialVersionUID = 5620795633028040075L;

    //   ("交易订单ID")
    private String tradeOrderId;

    private String tradePayId;
}
