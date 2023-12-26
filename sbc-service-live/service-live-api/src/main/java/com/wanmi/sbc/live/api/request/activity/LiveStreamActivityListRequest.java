package com.wanmi.sbc.live.api.request.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>直播活动列表查询参数</p>
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamActivityListRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 直播id
     */
    @NotNull
    @ApiModelProperty(value = "直播间ID")
    private Integer liveRoomId;

}
