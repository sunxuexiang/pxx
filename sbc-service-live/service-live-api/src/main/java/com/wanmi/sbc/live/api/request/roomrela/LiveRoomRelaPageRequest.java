package com.wanmi.sbc.live.api.request.roomrela;

import java.util.Date;
import java.util.List;

import com.wanmi.sbc.common.base.BaseQueryRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>直播间关联PageRequest</p>
 * @author 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-19 15:00:59
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.room_rela
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomRelaPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 3839228890950052647L;

	@ApiModelProperty(value = "live_room_rela_id")
	private Long liveRoomRelaId;
	@ApiModelProperty(value = "直播间id")
	private Long liveRoomId;
	@ApiModelProperty(value = "关联类型,b:品牌,a:直播账号,o:运营账号,g:商品,bg:福袋")
	private String relaType;
	@ApiModelProperty(value = "关联id")
	private String relaId;

	@ApiModelProperty(value = "直播间id列表")
	private List<Long> liveRoomIds;
	@ApiModelProperty(value = "关联内容")
	private String relaContent;


	@ApiModelProperty(value = "状态")
	private Long status;
	@ApiModelProperty(value = "创建人")
	private Long createPerson;
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	@ApiModelProperty(value = "修改人")
	private Long updatePerson;
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private Long delFlag;

	/** 店铺ID */
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;
}