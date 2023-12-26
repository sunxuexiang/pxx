package com.wanmi.sbc.goods.info.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jeffrey
 * @create 2021-08-07 16:43
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSalesRanking implements Serializable {

    private static final long serialVersionUID = -3345961109775737749L;

    private String spuId;
    private String skuId;
    private Long cateId;
    private String cateName;

}
