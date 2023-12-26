package com.wanmi.sbc.live.api.request.activity;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>直播活动新增参数</p>
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveStreamActivityAddRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "直播间ID")
    private Integer liveRoomId;

    /**
     * 活动add参数
     */
    @ApiModelProperty(value = "直播活动保存list")
    private List<String> activityIds;
}
