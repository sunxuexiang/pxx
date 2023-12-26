package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 建行对账单明细分页查询请求类
 * @author hudong
 * 2023-09-11
 */
@Data
@ApiModel
public class CcbStatementDetailPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 支付流水号
     */
    @ApiModelProperty(value = "支付流水号")
    private String ordrNo;

    /**
     * 主订单编号
     */
    @ApiModelProperty(value = "主订单编号")
    private String mainOrdrNo;

    /**
     * 交易完成时间
     */
    @ApiModelProperty(value = "交易完成时间")
    private String txnDt;

}
