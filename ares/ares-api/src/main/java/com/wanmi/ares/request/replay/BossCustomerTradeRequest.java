package com.wanmi.ares.request.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * boss权限查询当月订单参数
 * @author lm
 * @date 2022/09/17 14:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BossCustomerTradeRequest {

    /*省code*/
    private String provinceCode;

    /*时间*/
    private String date;

    private Integer orderType = 0;
}
