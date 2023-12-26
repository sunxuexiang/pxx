package com.wanmi.sbc.setting.systemcancellationpolicy.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>隐私政策实体类</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@Data
@Entity
@Table(name = "system_cancellation_policy")
public class SystemCancellationPolicy implements Serializable {

	private static final long serialVersionUID = -7237785555258618980L;

	/**
	 * 隐私政策id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "cancellation_policy_id")
	private String cancellationPolicyId;

	/**
	 * 隐私政策
	 */
	@Column(name = "cancellation_policy")
	private String cancellationPolicy;

	/**
	 * 隐私政策弹窗
	 */
	@Column(name = "cancellation_policy_pop")
	private String cancellationPolicyPop;

	/**
	 * 创建人
	 */
	@Column(name = "create_person")
	private String createPerson;

	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 修改人
	 */
	@Column(name = "update_person")
	private String updatePerson;

	/**
	 * 修改时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}