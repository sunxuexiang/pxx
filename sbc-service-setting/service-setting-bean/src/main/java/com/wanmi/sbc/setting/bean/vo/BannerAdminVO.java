package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>轮播管理VO</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@ApiModel
@Data
public class BannerAdminVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty(value = "id")
	private Long id;

	/**
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	private String bannerName;

	/**
	 * 一级类ID
	 */
	@ApiModelProperty(value = "一级类ID")
	private Long oneCateId;

	/**
	 * 一级分类名称
	 */
	@ApiModelProperty(value = "一级分类名称")
	private String oneCateName;

	/**
	 * 排序号
	 */
	@ApiModelProperty(value = "排序号")
	private Integer bannerSort;

	/**
	 * 添加链接
	 */
	@ApiModelProperty(value = "添加链接")
	private String link;

	/**
	 * banner图片
	 */
	@ApiModelProperty(value = "banner图片")
	private String bannerImg;

	/**
	 * 状态(0.显示 1.隐藏)
	 */
	@ApiModelProperty(value = "状态(0.显示 1.隐藏)")
	private Integer isShow;

	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTime;

}