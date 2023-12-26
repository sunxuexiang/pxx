package com.wanmi.sbc.setting.api.request.weatherswitch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * <p>天气设置新增参数</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSwitchAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 顶部背景图状态(0.关闭，1开启)
	 */
	@ApiModelProperty(value = "顶部背景图状态(0.关闭，1开启)")
	@NotNull
	@Max(127)
	private Integer topImgStatus;

	/**
	 * 顶部背景图
	 */
	@ApiModelProperty(value = "顶部背景图")
	@Length(max=255)
	private String topImg;

	/**
	 * slogan图图状态(0.关闭，1开启)
	 */
	@ApiModelProperty(value = "slogan图图状态(0.关闭，1开启)")
	@NotNull
	@Max(127)
	private Integer sloganImgStatus;

	/**
	 * slogan图
	 */
	@ApiModelProperty(value = "slogan图")
	@Length(max=255)
	private String sloganImg;

	/**
	 * 组件开关状态 (0：关闭 1：开启)
	 */
	@ApiModelProperty(value = "组件开关状态 (0：关闭 1：开启)")
	@NotNull
	@Max(127)
	private Integer componentStatus;

	/**
	 * 是否设置 (0：关闭 1：开启)
	 */
	@ApiModelProperty(value = "是否设置 (0：关闭 1：开启)")
	@NotNull
	@Max(127)
	private Integer searchBackStatus;

	/**
	 * 搜索背景色
	 */
	@ApiModelProperty(value = "搜索背景色")
	@Length(max=50)
	private String searchBackColor;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;


	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

}