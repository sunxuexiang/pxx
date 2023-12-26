package com.wanmi.sbc.order.api.request.trade;

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
public class TradeCancelRequest implements Serializable {

    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private String tid;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Operator operator;

    @ApiModelProperty(value = "取消类型：autoCancel,manualCancel,rejectCancel")
    private String cancelType;

    @ApiModelProperty(value = "驳回原因")
    private String reason;

}
