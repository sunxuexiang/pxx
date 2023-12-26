package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.base.Operator;
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
public class updateTradeLogisticCompanyRequest implements Serializable {


    private static final long serialVersionUID = 1403210037399971071L;
    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id")
    private String tid;

    /**
     * 物流公司id
     */
    @ApiModelProperty(value = "物流公司id")
    private Long companyId;

    /**
     * 订单id
     */
    @ApiModelProperty(value = "收货")
    private String areaInfo;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Operator operator;
}
