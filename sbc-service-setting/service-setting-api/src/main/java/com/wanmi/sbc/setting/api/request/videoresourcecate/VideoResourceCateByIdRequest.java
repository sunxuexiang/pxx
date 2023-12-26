package com.wanmi.sbc.setting.api.request.videoresourcecate;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 单个查询视频教程资源资源分类表请求参数
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceCateByIdRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 素材分类id
	 */
	@ApiModelProperty(value = "素材分类id")
	@NotNull
	private String cateId;

}