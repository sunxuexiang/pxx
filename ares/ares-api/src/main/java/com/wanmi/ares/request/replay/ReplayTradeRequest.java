package com.wanmi.ares.request.replay;

import com.wanmi.ares.base.BaseRequest;
import lombok.Data;

@Data
public class ReplayTradeRequest extends BaseRequest {

    private String startTime;

    private String endTime;
}
