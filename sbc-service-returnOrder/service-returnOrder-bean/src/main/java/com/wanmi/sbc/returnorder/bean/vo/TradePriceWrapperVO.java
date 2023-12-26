package com.wanmi.sbc.returnorder.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单金额归总
 * Created by jinwei on 19/03/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradePriceWrapperVO implements Serializable {

    private static final long serialVersionUID = 5258208696186469296L;
    /**
     * 订单ID
     */
    @ApiModelProperty(value = "订单ID")
    private String id;

    /**
     * 商品金额
     */
    @ApiModelProperty(value = "商品金额")
    private TradePriceVO tradePriceVO;
}
