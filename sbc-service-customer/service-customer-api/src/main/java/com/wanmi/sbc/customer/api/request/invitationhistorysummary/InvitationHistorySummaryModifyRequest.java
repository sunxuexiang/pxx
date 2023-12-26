package com.wanmi.sbc.customer.api.request.invitationhistorysummary;

import java.math.BigDecimal;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>邀新历史汇总计表修改参数</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationHistorySummaryModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 业务员ID
	 */
	@ApiModelProperty(value = "业务员ID")
	@Length(max=32)
	private String employeeId;

	/**
	 * 总邀新数
	 */
	@ApiModelProperty(value = "总邀新数")
	@Max(9223372036854775807L)
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
	@Max(9223372036854775807L)
	private Long totalGoodsCount;

	/**
	 * 总订单数
	 */
	@ApiModelProperty(value = "总订单数")
	@Max(9223372036854775807L)
	private Long tradeTotal;

}