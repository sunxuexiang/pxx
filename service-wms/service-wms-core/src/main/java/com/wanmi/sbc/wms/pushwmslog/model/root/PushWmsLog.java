package com.wanmi.sbc.wms.pushwmslog.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Entity
@Accessors(chain = true)
@Table(name = "push_wms_log")
@EntityListeners(AuditingEntityListener.class)
public class PushWmsLog implements Serializable {


	private static final long serialVersionUID = 2395420562701106193L;
	/**
	 * 记录主键
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "p_wms_id")
	private Long pWmsId;

	/**
	 * 作业单号
	 */
	@Column(name = "doc_no")
	private String docNo;

	/**
	 * 作业单类型
	 */
	@Column(name = "order_type")
	private String orderType;

	/**
	 * 货主ID
	 */
	@Column(name = "customer_id")
	private String customerId;

	/**
	 * 取消原因
	 */
	@Column(name = "erp_cancel_reason")
	private String erpCancelReason;

	/**
	 * 请求json
	 */
	@Column(name = "p_prarm_json")
	private String pPrarmJson;

	/**
	 * 请求时间
	 */
	@CreatedDate
	@Column(name = "create_time",updatable = false,nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime create_time;


	/**
	 * 修改时间
	 */
	@LastModifiedDate
	@Column(name = "update_time",nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;


	/**
	 *  转态
	 */
	@Column(name = "statues")
	private Integer statues;




	/**
	 * 返回信息
	 */
	@Column(name = "respose_info")
	private String resposeInfo;

	/**
	 * 错误原因
	 */
	@Column(name = "erro_info")
	private String erroInfo;

	/**
	 * 仓库ID
	 */
	@Column(name = "warehouse_id")
	private String warehouseId;

}