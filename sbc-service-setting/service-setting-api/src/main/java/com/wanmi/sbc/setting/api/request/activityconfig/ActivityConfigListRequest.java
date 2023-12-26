package com.wanmi.sbc.setting.api.request.activityconfig;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置列表查询请求参数</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConfigListRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-活动配置idList
	 */
	@ApiModelProperty(value = "批量查询-活动配置idList")
	private List<Long> idList;

	/**
	 * 活动配置id
	 */
	@ApiModelProperty(value = "活动配置id")
	private Long id;

	/**
	 * 配置名称
	 */
	@ApiModelProperty(value = "配置名称")
	private String name;

	/**
	 * 配置别名
	 */
	@ApiModelProperty(value = "配置别名")
	private String alias;

	/**
	 * 配置别名
	 */
	@ApiModelProperty(value = "配置别名")
	private String value;

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
	 * 创建人id
	 */
	@ApiModelProperty(value = "创建人id")
	private String createPerson;

	/**
	 * 更新人id
	 */
	@ApiModelProperty(value = "更新人id")
	private String updatePerson;

	/**
	 * 搜索条件:更新时间开始
	 */
	@ApiModelProperty(value = "搜索条件:更新时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeBegin;
	/**
	 * 搜索条件:更新时间截止
	 */
	@ApiModelProperty(value = "搜索条件:更新时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeEnd;

	/**
	 * 删除标志 0未删除 1已删除
	 */
	@ApiModelProperty(value = "删除标志 0未删除 1已删除")
	private DeleteFlag delFlag;

}