package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.order.bean.enums.AuditState;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-05 15:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeAuditRequest implements Serializable {

    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private String tid;


    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private List<String> tids;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private AuditState auditState;

    /**
     * 原因
     */
    @ApiModelProperty(value = "原因")
    private String reason;

    /**
     * 是否财务审核
     */
    @ApiModelProperty(value = "是否财务审核")
    private Boolean financialFlag = false;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Operator operator;
}
