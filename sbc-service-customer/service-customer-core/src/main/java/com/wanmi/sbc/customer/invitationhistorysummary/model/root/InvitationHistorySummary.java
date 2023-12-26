package com.wanmi.sbc.customer.invitationhistorysummary.model.root;

import java.math.BigDecimal;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * <p>邀新历史汇总计表实体类</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@Data
@Entity
@Table(name = "invitation_history_summary")
public class InvitationHistorySummary{
	private static final long serialVersionUID = 1L;

	/**
	 * 业务员ID
	 */
	@Id
	@Column(name = "employee_id")
	private String employeeId;

	/**
	 * 总邀新数
	 */
	@Column(name = "total_count")
	private Long totalCount = 0L;

	/**
	 * 总订单金额
	 */
	@Column(name = "total_trade_price")
	private BigDecimal totalTradePrice = BigDecimal.ZERO;

	/**
	 * 总商品数
	 */
	@Column(name = "total_goods_count")
	private Long totalGoodsCount = 0L;

	/**
	 * 总订单数
	 */
	@Column(name = "trade_total")
	private Long tradeTotal = 0L;

}