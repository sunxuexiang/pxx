package com.wanmi.sbc.walletorder.bean.vo;

import io.swagger.annotations.ApiModel;
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
@ApiModel
public class LogisticsVO {
    /**
     * 物流配送方式编号
     */
    @ApiModelProperty(value = "物流配送方式编号")
    private String shipMethodId;

    /**
     * 物流配送方式名称
     */
    @ApiModelProperty(value = "物流配送方式名称")
    private String shipMethodName;

    /**
     * 物流号
     */
    @ApiModelProperty(value = "物流号")
    private String logisticNo;

    /**
     * 物流费
     */
    @ApiModelProperty(value = "物流费")
    private BigDecimal logisticFee;

    /**
     * 物流公司编号
     */
    @ApiModelProperty(value = "物流公司编号")
    private String logisticCompanyId;

    /**
     * 物流公司名称
     */
    @ApiModelProperty(value = "物流公司名称")
    private String logisticCompanyName;

    /**
     * 物流公司标准编码
     */
    @ApiModelProperty(value = "物流公司标准编码")
    private String logisticStandardCode;

}
