package com.wanmi.sbc.tms.api.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 配置运费金额DTO
 */
@Data
public class TmsOrderAmountDTO implements Serializable {

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
     * 计算运费规则
     */
    private String freightRule;

    /**
     * 计算运费规则描述
     */
    private String freightRuleDesc;

    /**
     * 乡镇件加收的运费模板金额
     */
    private Double increase;
}
