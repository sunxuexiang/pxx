package com.wanmi.ares.view.replay;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReplayTradeBuyerStoreResponse {

    // 客户id
    private String customerId;

    // 公司id
    private Long companyId;

    // 实付金额
    private BigDecimal fee;

}
