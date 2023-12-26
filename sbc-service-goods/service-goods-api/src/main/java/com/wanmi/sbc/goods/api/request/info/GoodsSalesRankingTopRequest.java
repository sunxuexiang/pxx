package com.wanmi.sbc.goods.api.request.info;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jeffrey
 * @create 2021-08-07 9:16
 */
@Data
public class GoodsSalesRankingTopRequest implements Serializable {
    private static final long serialVersionUID = -2908106907870427257L;

    /**
     * 商品skuId
     */
    private String skuId;

    /**
     * 分类id
     */
    private Long cateId;
}
