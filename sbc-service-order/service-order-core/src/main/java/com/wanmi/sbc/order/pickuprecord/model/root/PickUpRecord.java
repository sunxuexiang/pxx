package com.wanmi.sbc.order.pickuprecord.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p>测试代码生成实体类</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@Data
@Entity
@Table(name = "pick_up_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecord  {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "pick_up_id")
	private String pickUpId;

	/**
	 * 店铺id
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 订单id
	 */
	@Column(name = "trade_id")
	private String tradeId;

	/**
	 * 自提码
	 */
	@Column(name = "pick_up_code")
	private String pickUpCode;

	/**
	 * 是否已自提:0:未自提 1：已自提
	 */
	@Column(name = "pick_up_flag")
	private DefaultFlag pickUpFlag;

	/**
	 * 自提时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "pick_up_time")
	private LocalDateTime pickUpTime;

	/**
	 * 手机号
	 */
	@Column(name = "contact_phone")
	private String contactPhone;

	/**
	 * 删除标志位:0:未删除1：已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	@Column(name = "create_time")
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}