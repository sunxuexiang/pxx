package com.wanmi.sbc.live.api.request.host;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>主播PageRequest</p>
 * @author 王冬明（1010331559@qq.com） Automatic Generator
 * @date 2022-09-19 11:37:24
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.host
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveHostPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 4769795876687016880L;

	@ApiModelProperty(value = "主播姓名")
	private String hostName;

	@ApiModelProperty(value = "联系方式")
	private String contactPhone;

	@ApiModelProperty(value = "主播类型 0 官方 1入驻")
	private Long hostType;

	@ApiModelProperty(value = "在职状态 0 离职 1 在职")
	private Long workingState;

	@ApiModelProperty(value = "直播账号id")
	private String customerId;

	@ApiModelProperty(value = "直播账号")
	private String customerAccount;

	@ApiModelProperty(value = "运营账号")
	private String accountName;

	@ApiModelProperty(value = "运营账号id")
	private String employeeId;
}