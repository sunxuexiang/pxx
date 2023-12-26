package com.wanmi.sbc.customer.api.request.invitationstatistics;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.math.BigDecimal;

/**
 * <p>邀新统计新增参数</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationTradeStatisticsRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 要被增量的业务员
	 */
	private String employeeId;


	/**
	 * 订单总额
	 */
	private BigDecimal tradePrice;

	/**
	 * 商品数量
	 */
	private Long goodsCount;

	/**
	 * 订单id
	 */
	private String orderId;

}