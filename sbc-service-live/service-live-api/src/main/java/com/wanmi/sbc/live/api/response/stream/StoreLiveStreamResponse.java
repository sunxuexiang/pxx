package com.wanmi.sbc.live.api.response.stream;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreLiveStreamResponse {

    private Integer liveId;

    /**
     * 直播间id
     */
    private Integer liveRoomId;

    /**
     * 店铺ID
     */
    private Long storeId;
}
