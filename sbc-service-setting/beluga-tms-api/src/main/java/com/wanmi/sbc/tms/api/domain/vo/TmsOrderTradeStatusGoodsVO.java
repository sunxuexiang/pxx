package com.wanmi.sbc.tms.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-19 15:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsOrderTradeStatusGoodsVO implements Serializable {
    private static final long serialVersionUID = 5620795633028040075L;

    private String skuId;

    /**
     * 发货数量
     */
    private Integer deliverNum;

    /**
     * 单个重量
     */
    private Double weight;

    /**
     * 单个体积
     */
    private Double volumn;

    /**
     * 缺货数量
     */
    private Integer refundNum;
}
