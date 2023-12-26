package com.wanmi.sbc.goods.marketing.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * 采购单商品选择的营销
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsMarketing {
    /**
     * 唯一编号
     */
    @Id
    private String id;

    /**
     * sku编号
     */
    private String goodsInfoId;

    /**
     * 客户编号
     */
    private String customerId;

    /**
     * 营销编号
     */
    private Long marketingId;


    /**
     * 是否是快照
     */
    private Boolean isSnapshot = false;
}
