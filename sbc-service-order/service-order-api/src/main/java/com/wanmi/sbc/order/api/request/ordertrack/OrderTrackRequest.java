package com.wanmi.sbc.order.api.request.ordertrack;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName OrderTrackRequest
 * @Description TODO
 * @Author shiy
 * @Date 2023/6/17 10:09
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class OrderTrackRequest  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @ApiModelProperty("自增id")
    private Long id;

    /**
     * 单据编号
     */
    @ApiModelProperty("单据编号")
    private String orderCode;

    /**
     * 1:订单;2退单
     */
    @ApiModelProperty("1:订单;2退单")
    private int orderType;

    /**
     * 物流公司编号
     */
    @ApiModelProperty("物流公司编号")
    private String com;

    /**
     * 快递单号
     */
    @ApiModelProperty("快递单号")
    private String num;

    /**
     * 运单轨迹
     */
    @ApiModelProperty("运单轨迹")
    private String data;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
}
