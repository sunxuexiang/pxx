package com.wanmi.sbc.datecenter.response;

import com.wanmi.ares.response.datecenter.CustomerTradeResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 数据面板响应数据
 * @author lm
 * @date 2022/09/15 10:39
 */
@Data
@ApiModel(value = "数据中心统计数据")
public class DataCenterStatisticResponse {

    @ApiModelProperty("月统计数据")
    MonthStatisticResponse monthStatisticResponse;

    @ApiModelProperty("客戶详细数据")
    List<CustomerTradeResponse> customerDetailStatistic;

}
