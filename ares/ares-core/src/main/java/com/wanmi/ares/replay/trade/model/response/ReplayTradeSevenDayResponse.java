package com.wanmi.ares.replay.trade.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReplayTradeSevenDayResponse {

    private String dayTime;

    private Long totalNum;

    private BigDecimal totalPrice;

}
