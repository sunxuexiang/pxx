package com.wanmi.sbc.live.api.request.roomrela;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * <p>直播间关联AddRequest</p>
 * @author 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-19 15:00:59
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.room_rela
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveRoomRelaAddRequest implements Serializable {
    private static final long serialVersionUID = 6351035567449726124L;

	@ApiModelProperty(value = "live_room_rela_id")
	private Integer liveRoomRelaId;

	@ApiModelProperty(value = "直播间id")
	private Integer liveRoomId;

	@ApiModelProperty(value = "关联类型,b:品牌,a:直播账号,o:运营账号")
	private String relaType;

	@ApiModelProperty(value = "关联内容")
	private String relaContent;

	@ApiModelProperty(value = "关联id")
	private String relaId;

	@ApiModelProperty(value = "状态")
	private Long status;

	@ApiModelProperty(value = "创建人")
	private Integer createPerson;

	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

	@ApiModelProperty(value = "修改人")
	private Integer updatePerson;

	@ApiModelProperty(value = "修改时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;

	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private Long delFlag;


}