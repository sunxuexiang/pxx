package com.wanmi.ares.view.replay;

import com.wanmi.ares.base.BaseRequest;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReplayTradeStatisticView extends BaseRequest {

    private String dayTime;

    private Long totalNum = 0L;

    private BigDecimal totalPrice = BigDecimal.ZERO;
}
