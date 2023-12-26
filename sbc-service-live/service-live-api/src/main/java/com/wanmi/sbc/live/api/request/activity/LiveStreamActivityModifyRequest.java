package com.wanmi.sbc.live.api.request.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * <p>直播活动查询参数</p>
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamActivityModifyRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "直播间id")
    private Integer liveRoomId;

    @ApiModelProperty(value = "直播活动id")
    private String activityId;
}
