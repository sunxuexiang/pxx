package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeCheckRequest extends BaseRequest {
    private static final long serialVersionUID = -5483465106894784005L;

    @ApiModelProperty("父订单号，用于不同商家订单合并支付场景")
    private String parentTid ;

    @ApiModelProperty("订单Id")
    private String tid;
}