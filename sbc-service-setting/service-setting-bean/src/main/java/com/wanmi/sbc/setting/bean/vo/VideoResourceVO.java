package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作教程资源库VO
 * @author hudong
 * @date 2023-06-26 16:12:49
 */
@ApiModel
@Data
public class VideoResourceVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 素材ID
	 */
	@ApiModelProperty(value = "素材ID")
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
	private String cateId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 商家标识
	 */
	@ApiModelProperty(value = "商家标识")
	private Long companyInfoId;

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
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

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

	/**
	 * 横竖屏类别 1-横屏 2-竖屏
	 */
	private Integer hvType;


}