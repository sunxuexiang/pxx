package com.wanmi.sbc.setting.api.request.navigationconfig;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置新增参数</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationConfigAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 导航名称
	 */
	@ApiModelProperty(value = "导航名称")
	@NotBlank
	@Length(max=20)
	private String navName;

	/**
	 * 导航图标-未点击状态
	 */
	@ApiModelProperty(value = "导航图标-未点击状态")
	@Length(max=500)
	private String iconShow;

	/**
	 * 导航图标-点击状态
	 */
	@ApiModelProperty(value = "导航图标-点击状态")
	@Length(max=500)
	private String iconClick;

}