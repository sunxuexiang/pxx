package com.wanmi.sbc.setting.homedelivery.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>配送到家实体类</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@Data
@Entity
@Table(name = "home_delivery")
public class HomeDelivery implements Serializable {

	private static final long serialVersionUID = 3973098615696965036L;
	/**
	 * 主键
	 */
	@Id
	@GeneratedValue
	@Column(name = "home_delivery_id")
	private Long homeDeliveryId;

	/**
	 * 配送到家文案
	 */
	@Column(name = "content")
	private String content;

	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 删除标志位
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;


	/**
	 * 店铺标识
	 */
	@Column(name="store_id")
	private Long storeId;

	/**
	 * 配送方式
	 */
	@Column(name="delivery_type")
	private Integer deliveryType;
}