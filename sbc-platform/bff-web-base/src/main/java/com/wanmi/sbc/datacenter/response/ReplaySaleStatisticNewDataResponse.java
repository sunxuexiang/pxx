package com.wanmi.sbc.datacenter.response;

import com.wanmi.ares.view.replay.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "数据中心")
public class ReplaySaleStatisticNewDataResponse implements Serializable {

    @ApiModelProperty(value = "今昨日销售金额")
    private List<SalesAmountNewDataView> salesAmountDataViewList;

    @ApiModelProperty(value = "今昨日销售箱数")
    private List<SalesCaseNewDataView> salesCaseDataViewList;

    @ApiModelProperty(value = "今昨日订单数")
    private List<SalesOrderNewDataView> salesOrderDataViewList;

    @ApiModelProperty(value = "今昨日下单用户数")
    private List<SalesUserNewDataView> salesUserDataViewList;

    @ApiModelProperty(value = "近七日及当月销售数据")
    private List<SaleSynthesisNewDataView> saleSynthesisDataViewList;
}
