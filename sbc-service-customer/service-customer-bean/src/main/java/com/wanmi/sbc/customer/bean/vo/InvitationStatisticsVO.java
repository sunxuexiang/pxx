package com.wanmi.sbc.customer.bean.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新统计VO</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@ApiModel
@Data
public class InvitationStatisticsVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 业务员ID
	 */
	@ApiModelProperty(value = "业务员ID")
	private String employeeId;

	/**
	 * 日期
	 */
	@ApiModelProperty(value = "日期")
	private String date;

	/**
	 * 邀新数
	 */
	@ApiModelProperty(value = "邀新数")
	private Long resultsCount = 0L;

	/**
	 * 订单总额
	 */
	@ApiModelProperty(value = "订单总额")
	private BigDecimal tradePriceTotal=BigDecimal.ZERO;

	/**
	 * 总商品数
	 */
	@ApiModelProperty(value = "总商品数")
	private Long tradeGoodsTotal = 0L;

	/**
	 * 总订单数
	 */
	@ApiModelProperty(value = "总订单数")
	private Long tradeTotal = 0l;

}