package com.wanmi.sbc.live.api.request.room;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>直播间PageRequest</p>
 * @author 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-19 14:29:19
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.room
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 1931671530108084630L;

	@ApiModelProperty(value = "live_room_id")
	private Integer liveRoomId;
	@ApiModelProperty(value = "直播间名称")
	private String liveRoomName;
	@ApiModelProperty(value = "直播间图片")
	private String imgPath;
	
	@ApiModelProperty(value = "平台标志，0：平台，1：非平台")
	private Integer sysFlag;
	
	@ApiModelProperty(value = "厂商id")
	private Integer companyId;
	@ApiModelProperty(value = "创建人")
	private Integer createPerson;
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	@ApiModelProperty(value = "修改人")
	private Integer updatePerson;
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除 ")
	private Long delFlag;
	@ApiModelProperty(value = "直播账号")
	private String accountName;

	@ApiModelProperty(value = "运营账号")
	private String employeeAccount;

	@ApiModelProperty(value = "品牌")
	private Integer brandId;

	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

	/** 直播间类型：0：非自营直播间；1、自营直播间；2：平台直播间 */
	private Integer roomType;

	@ApiModelProperty(value = "开始时间")
	private String startTime;

	@ApiModelProperty(value = "结束时间")
	private String endTime;

	private List<Long> liveRoomIdList;
}