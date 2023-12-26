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
 * <p>直播间VO</p>
 * @author liudan 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-16 19:58:39
 * @version 1.0
 * @package com.wanmi.sbc.live.bean.vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveRoomVO implements Serializable {
    private static final long serialVersionUID = 2106762184493919666L;

	@ApiModelProperty(value = "'直播间id")
	private Long liveRoomId;

	@ApiModelProperty(value = "'直播间名称")
	private String liveRoomName;

	@ApiModelProperty(value = "'平台标志，0：平台，1：非平台")
	private Long sysFlag;

	@ApiModelProperty(value = "'厂商id")
	private Long companyId;

	@ApiModelProperty(value = "'直播间封面")
	private String imgPath;

	@ApiModelProperty(value = "'删除标识,0:未删除1:已删除 ")
	private Long delFlag;

	/** 店铺ID */
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

	private LiveStreamVO liveStreamVO;

}