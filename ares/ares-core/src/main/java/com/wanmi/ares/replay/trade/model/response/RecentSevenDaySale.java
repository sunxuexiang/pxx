package com.wanmi.ares.replay.trade.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class RecentSevenDaySale extends BaseData implements Serializable {

    @ApiModelProperty(value = "日期")
    private String dayTime;

    @ApiModelProperty(value = "箱数")
    private Long totalNum;

}
