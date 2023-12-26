package com.wanmi.sbc.live.api.request.host;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>主播信息Request</p>
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
public class LiveHostInfoRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 4769795876687016880L;

	@ApiModelProperty(value = "主播id")
	private Integer hostId;

	@ApiModelProperty(value = "直播账号id")
	private String customerId;

	@ApiModelProperty(value = "直播账号")
	private String customerAccount;

	public static LiveHostInfoRequest request(String customerId, String customerAccount) {
		LiveHostInfoRequest request=new LiveHostInfoRequest();
		request.setCustomerId(customerId);
		request.setCustomerAccount(customerAccount);
		return request;
	}

	public static LiveHostInfoRequest request(String customerId) {
		LiveHostInfoRequest request=new LiveHostInfoRequest();
		request.setCustomerId(customerId);
		return request;
	}
}