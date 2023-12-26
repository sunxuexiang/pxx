package com.wanmi.sbc.customer.bean.vo;

import java.math.BigDecimal;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新历史汇总计表VO</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@Data
public class InvitationHistorySummaryVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 业务员ID
	 */
	@ApiModelProperty(value = "业务员ID")
	private String employeeId;

	/**
	 * 总邀新数
	 */
	@ApiModelProperty(value = "总邀新数")
	private Long totalCount = 0L;

	/**
	 * 总订单金额
	 */
	@ApiModelProperty(value = "总订单金额")
	private BigDecimal totalTradePrice =BigDecimal.ZERO;

	/**
	 * 总商品数
	 */
	@ApiModelProperty(value = "总商品数")
	private Long totalGoodsCount = 0L;

	/**
	 * 总订单数
	 */
	@ApiModelProperty(value = "总订单数")
	private Long tradeTotal = 0L;

}