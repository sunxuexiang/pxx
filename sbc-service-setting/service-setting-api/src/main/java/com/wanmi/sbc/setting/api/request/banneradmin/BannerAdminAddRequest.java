package com.wanmi.sbc.setting.api.request.banneradmin;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>轮播管理新增参数</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerAdminAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	@Length(max=40)
	private String bannerName;

	/**
	 * 一级类ID
	 */
	@ApiModelProperty(value = "一级类ID")
	@Max(9223372036854775807L)
	private Long oneCateId;

	/**
	 * 一级分类名称
	 */
	@ApiModelProperty(value = "一级分类名称")
	@Length(max=20)
	private String oneCateName;

	/**
	 * 排序号
	 */
	@ApiModelProperty(value = "排序号")
	@Max(9999999999L)
	private Integer bannerSort;

	/**
	 * 添加链接
	 */
	@ApiModelProperty(value = "添加链接")
	@Length(max=256)
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
	@Max(127)
	private Integer isShow;

	/**
	 * 删除标志
	 */
	@ApiModelProperty(value = "删除标志", hidden = true)
	private DeleteFlag delFlag;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人", hidden = true)
	private String createPerson;

	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人", hidden = true)
	private String updatePerson;

	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTime;

	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}