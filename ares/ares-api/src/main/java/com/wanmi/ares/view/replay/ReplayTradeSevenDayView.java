package com.wanmi.ares.view.replay;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReplayTradeSevenDayView implements Serializable {

    private List<ReplayTradeStatisticView> replayTradeStatisticViewList;

}
