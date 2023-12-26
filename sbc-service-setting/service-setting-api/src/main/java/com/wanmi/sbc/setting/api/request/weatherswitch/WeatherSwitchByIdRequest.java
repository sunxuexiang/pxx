package com.wanmi.sbc.setting.api.request.weatherswitch;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询天气设置请求参数</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSwitchByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 开关id
	 */
	@ApiModelProperty(value = "开关id")
	@NotNull
	private Long id;

}