package com.wanmi.sbc.order.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @desc  
 * @author shiy  2023/12/22 8:55
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradePushLogisticRequestDTO implements Serializable {
    private TradeDeliverRequestDTO deliverRequestDTO;
    private String  tradeId;
}
