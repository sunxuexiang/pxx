package com.wanmi.sbc.datecenter.response;

import com.wanmi.ares.response.datecenter.CustomerTradeItemResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lm
 * @date 2022/09/17 10:48
 */
@ApiModel("客户详细数据")
@Data
public class CustomerDetailStatisticResponse {

    @ApiModelProperty("客户信息")
    private CustomerDetailBaseResponse customerDetail;

    @ApiModelProperty("订单列表")
    private List<CustomerTradeItemResponse> customerTradeItemList;

}
