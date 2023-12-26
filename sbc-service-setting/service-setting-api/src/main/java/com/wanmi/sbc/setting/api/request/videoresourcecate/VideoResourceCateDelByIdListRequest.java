package com.wanmi.sbc.setting.api.request.videoresourcecate;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * 批量删除视频教程资源资源分类表请求参数
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceCateDelByIdListRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-素材分类idList
	 */
	@ApiModelProperty(value = "批量删除-素材分类idList")
	@NotEmpty
	private List<String> cateIdList;
}