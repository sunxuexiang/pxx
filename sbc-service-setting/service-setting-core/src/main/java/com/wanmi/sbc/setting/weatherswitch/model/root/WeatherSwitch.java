package com.wanmi.sbc.setting.weatherswitch.model.root;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p>天气设置实体类</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@Data
@Entity
@Table(name = "weather_switch")
public class WeatherSwitch {
	private static final long serialVersionUID = 1L;

	/**
	 * 开关id
	 */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 顶部背景图状态(0.关闭，1开启)
	 */
	@Column(name = "top_img_status")
	private Integer topImgStatus;

	/**
	 * 顶部背景图
	 */
	@Column(name = "top_img")
	private String topImg;

	/**
	 * slogan图图状态(0.关闭，1开启)
	 */
	@Column(name = "slogan_img_status")
	private Integer sloganImgStatus;

	/**
	 * slogan图
	 */
	@Column(name = "slogan_img")
	private String sloganImg;

	/**
	 * 组件开关状态 (0：关闭 1：开启)
	 */
	@Column(name = "component_status")
	private Integer componentStatus;

	/**
	 * 是否设置 (0：关闭 1：开启)
	 */
	@Column(name = "search_back_status")
	private Integer searchBackStatus;

	/**
	 * 搜索背景色
	 */
	@Column(name = "search_back_color")
	private String searchBackColor;


	/**
	 * 创建时间
	 */
	@CreatedDate
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;


	/**
	 * 更新时间
	 */
	@LastModifiedDate
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

}