package com.wanmi.sbc.live.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * <p>直播间关联VO</p>
 * @author liudan 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-16 19:58:39
 * @version 1.0
 * @package com.wanmi.sbc.live.bean.vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveRoomRelaVO implements Serializable {
    private static final long serialVersionUID = 3441805683948708667L;

	@ApiModelProperty(value = "live_room_rela_id")
	private Long liveRoomRelaId;

	@ApiModelProperty(value = "'直播间id")
	private Long liveRoomId;

	@ApiModelProperty(value = "'关联类型,b:品牌,a:直播账号,o:运营账号")
	private String relaType;

	@ApiModelProperty(value = "'关联id")
	private String relaId;


	@ApiModelProperty(value = "关联内容")
	private String relaContent;

	@ApiModelProperty(value = "'状态")
	private Long status;

	@ApiModelProperty(value = "create_user")
	private Long createUser;

	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

	@ApiModelProperty(value = "update_user")
	private Long updateUser;

	@ApiModelProperty(value = "修改时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;

	@ApiModelProperty(value = "'删除标识,0:未删除1:已删除 ")
	private Integer delFlag;

	/** 店铺ID */
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;
}