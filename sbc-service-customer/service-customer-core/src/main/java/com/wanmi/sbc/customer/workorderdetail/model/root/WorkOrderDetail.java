package com.wanmi.sbc.customer.workorderdetail.model.root;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * <p>工单明细实体类</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@Data
@Entity
@Table(name = "work_order_detail")
public class WorkOrderDetail {
	private static final long serialVersionUID = 1L;

	/**
	 * 工单处理明细Id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "work_order_del_id")
	private String workOrderDelId;

	/**
	 * 处理时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "deal_time")
	private LocalDateTime dealTime;

	/**
	 * 处理状态
	 */
	@Column(name = "status")
	private Integer status;

	/**
	 * 处理建议
	 */
	@Column(name = "suggestion")
	private String suggestion;

	/**
	 * 工单Id
	 */
	@Column(name = "work_order_id")
	private String workOrderId;

}