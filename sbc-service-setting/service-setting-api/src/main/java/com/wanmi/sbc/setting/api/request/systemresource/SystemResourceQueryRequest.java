package com.wanmi.sbc.setting.api.request.systemresource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>平台素材资源通用查询请求参数</p>
 * @author lq
 * @date 2019-11-05 16:14:27
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemResourceQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-素材资源IDList
	 */
	@ApiModelProperty(value = "批量查询-素材资源IDList")
	private List<Long> resourceIdList;

	/**
	 * 素材资源ID
	 */
	@ApiModelProperty(value = "素材资源ID")
	private Long resourceId;

	/**
	 * 资源类型(0:图片,1:视频)
	 */
	@ApiModelProperty(value = "资源类型(0:图片,1:视频)")
	private ResourceType resourceType;

	/**
	 * 素材分类ID
	 */
	@ApiModelProperty(value = "素材分类ID")
	private Long cateId;

	/**
	 * 素材KEY
	 */
	@ApiModelProperty(value = "素材KEY")
	private String resourceKey;

	/**
	 * 素材名称
	 */
	@ApiModelProperty(value = "素材名称")
	private String resourceName;

	/**
	 * 素材地址
	 */
	@ApiModelProperty(value = "素材地址")
	private String artworkUrl;

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
	 * 删除标识,0:未删除1:已删除
	 */
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private DeleteFlag delFlag;

	/**
	 * oss服务器类型，对应system_config的config_type
	 */
	@ApiModelProperty(value = "oss服务器类型，对应system_config的config_type")
	private String serverType;


	@ApiModelProperty(value = "批量查询-素材分类id")
	private List<Long> cateIds;
}