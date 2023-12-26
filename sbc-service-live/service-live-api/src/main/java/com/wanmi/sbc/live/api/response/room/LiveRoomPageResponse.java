package com.wanmi.sbc.live.api.response.room;

import com.wanmi.sbc.live.api.response.LiveBasePageResponse;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
public class LiveRoomPageResponse extends LiveBasePageResponse<LiveRoomInfoResponse> {
    private static final long serialVersionUID = 1L;




}
