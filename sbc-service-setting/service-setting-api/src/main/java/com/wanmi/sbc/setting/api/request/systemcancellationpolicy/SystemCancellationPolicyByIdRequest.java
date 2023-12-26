package com.wanmi.sbc.setting.api.request.systemcancellationpolicy;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>单个查询隐私政策请求参数</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCancellationPolicyByIdRequest extends SettingBaseRequest {

	private static final long serialVersionUID = 4838201756514372702L;
	/**
	 * 隐私政策id
	 */
	@ApiModelProperty(value = "隐私政策id")
	@NotNull
	private String cancellationPolicyId;
}