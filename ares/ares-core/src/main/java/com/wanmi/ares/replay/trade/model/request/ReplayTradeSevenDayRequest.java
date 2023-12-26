package com.wanmi.ares.replay.trade.model.request;

import com.wanmi.ares.base.BaseRequest;
import lombok.Data;

@Data
public class ReplayTradeSevenDayRequest extends BaseRequest {

    private String startTime;

    private String endTime;
}
