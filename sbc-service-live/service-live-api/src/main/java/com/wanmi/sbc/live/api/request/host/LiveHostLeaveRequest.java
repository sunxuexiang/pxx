package com.wanmi.sbc.live.api.request.host;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>主播删除Request</p>
 * @author 王冬明（1010331559@qq.com） Automatic Generator
 * @date 2022-09-19 11:37:24
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.host
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveHostLeaveRequest implements Serializable {
    private static final long serialVersionUID = 1227732814721637657L;

	@NotNull
	@ApiModelProperty(value = "主播ID")
	private Integer hostId;

}