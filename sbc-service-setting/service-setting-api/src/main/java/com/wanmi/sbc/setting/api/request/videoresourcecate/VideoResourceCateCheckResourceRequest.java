package com.wanmi.sbc.setting.api.request.videoresourcecate;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 平台视频教程素材资源分类列表查询请求参数
 * @author hudong
 * @date 2023-06-26 16:14:55
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceCateCheckResourceRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;


	/**
	 * 素材资源分类id
	 */
	@ApiModelProperty(value = "素材资源分类id")
	@NotNull
	private String cateId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	
}