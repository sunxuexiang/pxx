package com.wanmi.sbc.setting.api.request.videoresource;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 单个查询视频教程资源库请求参数<
 * @author hudong
 * @date 2023-06-26 16:12:49
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceByIdRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 素材ID
	 */
	@ApiModelProperty(value = "素材ID")
	@NotNull
	private Long resourceId;
}