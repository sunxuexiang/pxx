package com.wanmi.sbc.live.api.request.room;

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
public class LiveStreamRoomListRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 直播主播账号id
     */
    @ApiModelProperty(value = "直播主播账号id")
    private String customerId;
}
