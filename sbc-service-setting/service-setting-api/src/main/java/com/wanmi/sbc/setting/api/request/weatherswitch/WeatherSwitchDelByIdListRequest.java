package com.wanmi.sbc.setting.api.request.weatherswitch;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除天气设置请求参数</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSwitchDelByIdListRequest extends SettingBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-开关idList
	 */
	@ApiModelProperty(value = "批量删除-开关idList")
	@NotEmpty
	private List<Long> idList;
}
