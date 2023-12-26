package com.wanmi.sbc.logisticsbean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lm
 * @date 2022/11/08 16:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("wms订单查询出参")
public class WmsOrderStatusVo implements Serializable {

    /**
     * 订单ID
     */
    @ApiModelProperty("订单号")
    private String tid;

    /**
     * 支付状态，N:未付款，Y:已付款
     */
    @ApiModelProperty("支付状态;N:未付款,Y:已付款")
    private String payStatus;

    /**
     * 订单状态
     *     99 -> 已发货
     *     90 -> 已取消
     *     00 -> 创建状态
     *     其他-> 拣货中
     */
    @ApiModelProperty("订单状态;99:已发货,90:已取消,00:创建状态,其他值:拣货中")
    private Integer orderStatus;
}
