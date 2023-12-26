package com.wanmi.sbc.setting.api.request.weatherswitch;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>天气设置列表查询请求参数</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSwitchListRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-开关idList
	 */
	@ApiModelProperty(value = "批量查询-开关idList")
	private List<Long> idList;

	/**
	 * 开关id
	 */
	@ApiModelProperty(value = "开关id")
	private Long id;

	/**
	 * 顶部背景图状态(0.关闭，1开启)
	 */
	@ApiModelProperty(value = "顶部背景图状态(0.关闭，1开启)")
	private Integer topImgStatus;

	/**
	 * 顶部背景图
	 */
	@ApiModelProperty(value = "顶部背景图")
	private String topImg;

	/**
	 * slogan图图状态(0.关闭，1开启)
	 */
	@ApiModelProperty(value = "slogan图图状态(0.关闭，1开启)")
	private Integer sloganImgStatus;

	/**
	 * slogan图
	 */
	@ApiModelProperty(value = "slogan图")
	private String sloganImg;

	/**
	 * 组件开关状态 (0：关闭 1：开启)
	 */
	@ApiModelProperty(value = "组件开关状态 (0：关闭 1：开启)")
	private Integer componentStatus;

	/**
	 * 是否设置 (0：关闭 1：开启)
	 */
	@ApiModelProperty(value = "是否设置 (0：关闭 1：开启)")
	private Integer searchBackStatus;

	/**
	 * 搜索背景色
	 */
	@ApiModelProperty(value = "搜索背景色")
	private String searchBackColor;

	/**
	 * 搜索条件:创建时间开始
	 */
	@ApiModelProperty(value = "搜索条件:创建时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:创建时间截止
	 */
	@ApiModelProperty(value = "搜索条件:创建时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

	/**
	 * 搜索条件:修改时间开始
	 */
	@ApiModelProperty(value = "搜索条件:修改时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeBegin;
	/**
	 * 搜索条件:修改时间截止
	 */
	@ApiModelProperty(value = "搜索条件:修改时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeEnd;

}