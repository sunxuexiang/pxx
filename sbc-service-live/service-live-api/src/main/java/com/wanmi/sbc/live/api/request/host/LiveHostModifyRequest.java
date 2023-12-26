package com.wanmi.sbc.live.api.request.host;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>主播ModifyRequest</p>
 * @author 王冬明（1010331559@qq.com） Automatic Generator
 * @date 2022-09-19 11:37:24
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.host
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveHostModifyRequest implements Serializable {
    private static final long serialVersionUID = 1227732814721637657L;

	@NotNull
	@ApiModelProperty(value = "主播ID")
	private Integer hostId;

	@NotNull
	@ApiModelProperty(value = "主播姓名")
	private String hostName;

	@NotNull
	@ApiModelProperty(value = "联系方式")
	private String contactPhone;

	@NotNull
	@ApiModelProperty(value = "主播类型 0 官方 1入驻")
	private Long hostType;

	@ApiModelProperty(value = "在职状态 0 离职 1 在职")
	private Long workingState;

	@ApiModelProperty(value = "直播账号列表")
	private List<LiveHostCustomerAccount> accounts;

	@NotNull
	@ApiModelProperty(value = "运营账号")
	private String accountName;

	@ApiModelProperty(value = "运营账号id")
	private String employeeId;

}