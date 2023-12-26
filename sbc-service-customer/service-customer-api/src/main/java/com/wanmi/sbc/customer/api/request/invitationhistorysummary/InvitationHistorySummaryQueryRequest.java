package com.wanmi.sbc.customer.api.request.invitationhistorysummary;

import java.math.BigDecimal;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新历史汇总计表通用查询请求参数</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-业务员IDList
	 */
	@ApiModelProperty(value = "批量查询-业务员IDList")
	private List<String> employeeIdList;

	/**
	 * 业务员ID
	 */
	@ApiModelProperty(value = "业务员ID")
	private String employeeId;

	/**
	 * 总邀新数
	 */
	@ApiModelProperty(value = "总邀新数")
	private Long totalCount;

	/**
	 * 总订单金额
	 */
	@ApiModelProperty(value = "总订单金额")
	private BigDecimal totalTradePrice;

	/**
	 * 总商品数
	 */
	@ApiModelProperty(value = "总商品数")
	private Long totalGoodsCount;

	/**
	 * 总订单数
	 */
	@ApiModelProperty(value = "总订单数")
	private Long tradeTotal;

}