package com.wanmi.sbc.setting.api.request.systemfile;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 单个查询平台文件请求参数<
 * @author hudong
 * @date 2023-09-08 16:12:49
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemFileByFileKeyRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 文件Key
	 */
	@ApiModelProperty(value = "文件Key")
	@NotNull
	private String fileKey;
}