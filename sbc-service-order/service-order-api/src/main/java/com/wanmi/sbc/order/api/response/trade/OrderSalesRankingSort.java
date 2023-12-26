package com.wanmi.sbc.order.api.response.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sgy
 * @create 2021-08-07 16:43
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSalesRankingSort implements Serializable {
    private String spuId;
    private String skuId;
    private Long cateId;
    private String cateName;
    private Integer count;

}