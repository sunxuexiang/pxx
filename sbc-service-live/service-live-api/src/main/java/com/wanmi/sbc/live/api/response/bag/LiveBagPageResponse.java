package com.wanmi.sbc.live.api.response.bag;

import com.wanmi.sbc.live.api.response.LiveBasePageResponse;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
public class LiveBagPageResponse  extends LiveBasePageResponse<LiveBagInfoResponse> {
    private static final long serialVersionUID = 1L;

}
