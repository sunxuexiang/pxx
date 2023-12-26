package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @create 2021-08-23 16:52
 */

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletRecordRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "用户ID")
    private String customerAccountId;

    @ApiModelProperty(value = "用户帐号")
    private String customerAccount;

    @ApiModelProperty(value = "交易编号")
    private String recordNo;

    @ApiModelProperty(value = "交易类型【1充值，2提现，3余额支付】")
    private WalletRecordTradeType tradeType;

    @ApiModelProperty(value = "收支类型【0收入，1支出】")
    private BudgetType budgetType;

    @ApiModelProperty(value="交易状态 0 未支付 1 待确认 3 已支付")
    private TradeStateEnum tradeState;

    @ApiModelProperty(value="充值申请单状态【1待审核，2充值成功，3充值失败")
    private Integer rechargeStatus;

    @ApiModelProperty(value ="提现申请单状态【1待审核，2已审核，3已打款，4已拒绝】")
    private Integer extractStatus;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "交易时间")
    private LocalDateTime dealTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    //花花需求
    @ApiModelProperty(value = "收支类型【0收入，1支出】")
    private Integer budgetTypeInt;

}
