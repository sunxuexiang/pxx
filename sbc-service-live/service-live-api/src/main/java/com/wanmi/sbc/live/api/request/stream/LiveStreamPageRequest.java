package com.wanmi.sbc.live.api.request.stream;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * <p>直播查询参数</p>
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 直播间名称标题
     */
    @ApiModelProperty(value = "直播间名称标题")
    private String roomName;

    /**
     * 直播间id
     */
    @ApiModelProperty(value = "直播间id")
    private Integer liveRoomId;

    /**
     * 直播间记录id
     */
    @ApiModelProperty(value = "直播间记录id")
    private Integer liveId;

    /**
     * 直播状态0: 未开始 1: 直播中 2: 已结束
     */
    @ApiModelProperty(value = "直播状态0: 未开始 1: 直播中 2: 已结束")
    private Integer liveStatus;

    /**
     * 直播开始时间
     */
    @ApiModelProperty(value = "直播开始时间")
    private String startTime;

    /**
     * 直播结束时间
     */
    @ApiModelProperty(value = "直播结束时间")
    private String endTime;

    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /** 直播间类型：0：非自营直播间；1、自营直播间；2：平台直播间 */
    private Integer roomType;
}
