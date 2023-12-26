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
public class LiveStreamRecordListRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 直播记录id
     */
    @ApiModelProperty(value = "直播记录liveId")
    private Integer liveId;
}
