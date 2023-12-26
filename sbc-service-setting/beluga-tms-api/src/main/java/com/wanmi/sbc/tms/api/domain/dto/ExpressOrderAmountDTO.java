package com.wanmi.sbc.tms.api.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 快递到家-配置运费金额DTO
 */
@Data
public class ExpressOrderAmountDTO implements Serializable {

    /**
     * 运费金额
     */
    private Double amount;

    /**
     * 承运商id
     */
    private Long carrierId;

    /**
     * 承运商名称
     */
    private String carrierName;

    /**
     * 店铺Id
     */
    private String storeId;

    /**
     * 配送数量
     */
    private Integer quantity;

    /**
     * 配送总重量
     */
    private Double totalWeight;

    /**
     * 计算运费规则
     */
    private String freightRule;

    /**
     * 计算运费规则描述
     */
    private String freightRuleDesc;


}
