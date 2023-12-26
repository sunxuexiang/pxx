package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentSevenDaySalePriceView extends BaseDataView implements Serializable {

    private String dayTime;

    private BigDecimal daySalePrice;
}
