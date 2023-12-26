package com.wanmi.sbc.customer.workorder.model.root;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.workorderdetail.model.root.WorkOrderDetail;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;
import java.util.List;

import com.wanmi.sbc.common.enums.DeleteFlag;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * <p>工单实体类</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@Data
@Entity
@Table(name = "work_order")
public class WorkOrder{
	private static final long serialVersionUID = 1L;

	/**
	 * 工单Id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "work_order_id")
	private String workOrderId;

	/**
	 * 工单号
	 */
	@Column(name = "work_order_no")
	private String workOrderNo;

	/**
	 * 社会信用代码
	 */
	@Column(name = "social_credit_code")
	private String socialCreditCode;

	/**
	 * 注册人Id
	 */
	@Column(name = "approval_customer_id")
	private String approvalCustomerId;

	/**
	 * 映射注册会员
	 */
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "approval_customer_id", insertable = false, updatable = false)
	@JsonBackReference
	private Customer customer;

	/**
	 * 已注册会员的Id
	 */
	@Column(name = "registed_customer_id")
	private String registedCustomerId;

	/**
	 * 账号合并状态
	 */
	@Column(name = "account_merge_status")
	private DefaultFlag accountMergeStatus;

	/**
	 * 状态 0:待处理，1：已完成
	 */
	@Column(name = "status")
	private DefaultFlag status;

	/**
	 * 修改时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "modify_time")
	private LocalDateTime modifyTime;

	/**
	 * 创建时间
	 */
	@CreatedDate
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 删除时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "delete_tiime")
	private LocalDateTime deleteTiime;

	/**
	 * 删除标志位
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;


}