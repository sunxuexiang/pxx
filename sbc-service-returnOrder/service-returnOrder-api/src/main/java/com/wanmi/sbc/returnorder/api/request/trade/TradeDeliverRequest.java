package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.dto.TradeDeliverDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-05 15:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeDeliverRequest implements Serializable {

    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private String tid;

    /**
     * 交易单物流信息
     */
    @ApiModelProperty(value = "交易单物流信息")
    private TradeDeliverDTO tradeDeliver;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Operator operator;

}
