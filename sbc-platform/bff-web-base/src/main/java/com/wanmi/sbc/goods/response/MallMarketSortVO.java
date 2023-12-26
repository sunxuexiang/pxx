package com.wanmi.sbc.goods.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-20 10:36
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MallMarketSortVO implements Serializable {

    // 店铺Id/市场Id/tabId
    private String id;

    private String name;

    private BigDecimal one;

    private BigDecimal two;

    private BigDecimal three;

    private BigDecimal four;
}
