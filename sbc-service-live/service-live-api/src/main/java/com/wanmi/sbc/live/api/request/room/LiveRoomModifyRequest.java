package com.wanmi.sbc.live.api.request.room;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>直播间ModifyRequest</p>
 * @author 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-19 14:29:19
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.room
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveRoomModifyRequest extends liveRoomBaseRequest {
    private static final long serialVersionUID = 8205102624993019838L;

	@ApiModelProperty(value = "live_room_id")
	@NotNull
	private Long liveRoomId;

	@ApiModelProperty(value = "直播间名称")
	private String liveRoomName;

	@ApiModelProperty(value = "厂商id")
	private Long companyId;

	@ApiModelProperty(value = "删除标志,0:未删除1:已删除")
	private Long delFlag;

	/**
	 * 店铺ID
	 */
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;



}