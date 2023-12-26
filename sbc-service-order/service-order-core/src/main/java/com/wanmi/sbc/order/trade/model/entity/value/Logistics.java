package com.wanmi.sbc.order.trade.model.entity.value;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 物流信息
 * @author wumeng[OF2627]
 *         company qianmi.com
 *         Date 2017-04-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Logistics {
    /**
     * 物流配送方式编号
     */
    private String shipMethodId;
    /**
     * 物流配送方式名称
     */
    private String shipMethodName;
    /**
     * 物流号
     */
    private String logisticNo;
    /**
     * 物流费
     */
    private BigDecimal logisticFee;
    /**
     * 物流公司编号
     */
    private String logisticCompanyId;
    /**
     * 物流公司名称
     */
    private String logisticCompanyName;
    /**
     * 物流公司标准编码
     */
    private String logisticStandardCode;

    /**
     * 物流公司电话
     */
    private String logisticPhone;

    /**
     * 建行商家编号
     */
    private String ccbMerchantNumber;

    /**
     * 收取佣金比例 0.01 - 0.99
     */
    private BigDecimal shareRatio;

    /** 结算周期 */
    private String finalPeriod;

    private String shipmentSiteId;

    private String shipmentSiteName;

    private String tmsOrderId;
    private String encloses;
}
