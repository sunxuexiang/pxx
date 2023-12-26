package com.wanmi.sbc.setting.imonlineserviceitem.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>imOnlineerviceItem实体类</p>
 * @author SGY
 * @date 2023-06-05 16:10:28
 */
@Data
@Entity
@Table(name = "im_online_service_item")
public class ImOnlineServiceItem implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 在线客服座席id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "im_service_item_id")
	private Integer imServiceItemId;

	/**
	 * 商家ID
	 */
	@Column(name = "company_info_id")
	private Long companyInfoId;

	/**
	 * 在线客服主键
	 */
	@Column(name = "im_online_service_id")
	private Integer imOnlineServiceId;

	/**
	 * 客服昵称
	 */
	@Column(name = "customer_service_name")
	private String customerServiceName;

	/**
	 * 客服账号
	 */
	@Column(name = "customer_service_account")
	private String customerServiceAccount;

	/**
	 * 删除标志 默认0：未删除 1：删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	/**
	 * 操作人
	 */
	@Column(name = "operate_person")
	private String operatePerson;

	/**
	 * 店铺ID
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 员工信息ID
	 */
	@Column(name = "employee_id")
	private String employeeId;

	/**
	 * 员工手机号码
	 */
	@Column(name = "phone_no")
	private String phoneNo;

	/**
	 * 是否管理员标记：0、否；1、是；
	 */
	@Column(name = "manager_flag")
	private Integer managerFlag;

	/**
	 * 客服状态：0、正常；1、离线（不接受用户聊天请求）；2、忙碌
	 */
	@Column(name = "service_status")
	private Integer serviceStatus;

	/**
	 * 客服是否离线：true、离线；false、在线
	 */
	@Transient
	private boolean offlineStatue;

	/**
	 * 是否已达到客服接待上限：true、是；
	 */
	@Transient
	private boolean waitInLine;

	/**
	 * 接待聊天客户数量
	 */
	@Transient
	private Integer acceptQuantity;
}