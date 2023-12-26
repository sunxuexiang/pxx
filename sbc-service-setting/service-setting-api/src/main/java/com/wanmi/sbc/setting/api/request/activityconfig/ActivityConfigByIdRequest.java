package com.wanmi.sbc.setting.api.request.activityconfig;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询导航配置请求参数</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConfigByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 活动配置id
	 */
	@ApiModelProperty(value = "活动配置id")
	@NotNull
	private Long id;

}