package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频教程资源资源分类表VO
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@ApiModel
@Data
public class VideoResourceCateShortVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 素材分类id
	 */
	@ApiModelProperty(value = "素材分类id")
	private String cateId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;


	/**
	 * 分类名称
	 */
	@ApiModelProperty(value = "分类名称")
	private String cateName;


	/**
	 * 分类层级
	 */
	@ApiModelProperty(value = "分类层级")
	private Integer cateGrade;


}