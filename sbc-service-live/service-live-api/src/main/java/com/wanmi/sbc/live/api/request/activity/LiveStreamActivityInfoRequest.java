package com.wanmi.sbc.live.api.request.activity;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>直播活动查询参数</p>
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamActivityInfoRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 直播id
     */
    @ApiModelProperty(value = "直播liveId")
    private Integer liveId;

    @ApiModelProperty(value = "直播活动id")
    private String activityId;
}
