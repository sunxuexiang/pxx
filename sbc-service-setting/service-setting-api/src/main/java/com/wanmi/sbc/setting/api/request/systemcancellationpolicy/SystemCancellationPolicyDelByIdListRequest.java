package com.wanmi.sbc.setting.api.request.systemcancellationpolicy;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * <p>批量删除隐私政策请求参数</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCancellationPolicyDelByIdListRequest extends SettingBaseRequest {

	private static final long serialVersionUID = -2341777716067060993L;
	/**
	 * 批量删除-隐私政策idList
	 */
	@ApiModelProperty(value = "批量删除-注销政策idList")
	@NotEmpty
	private List<String> cancellationPolicyIdList;
}