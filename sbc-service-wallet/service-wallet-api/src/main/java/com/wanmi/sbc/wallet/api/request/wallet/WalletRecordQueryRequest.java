package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-26 17:34
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletRecordQueryRequest extends BaseQueryRequest {
    @ApiModelProperty(value = "用户ID")
    private String customerAccountId;

    @ApiModelProperty(value = "用户帐号")
    private String customerAccount;

    @ApiModelProperty(value = "交易编号")
    private String recordNo;

    @ApiModelProperty(value = "用户名称")
    private List<String> customerNames;

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

    @ApiModelProperty(value ="关联单号")
    private String relationOrderId;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "交易时间")
    private LocalDateTime dealTime;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "明细类型")
    private WalletDetailsType walletDetailsType;

    //花花需求
    @ApiModelProperty(value = "收支类型【0收入，1支出】")
    private Integer budgetTypeInt;

    @ApiModelProperty(value = "通过客户账号批量查询数据")
    private List<String> accounts;

    @ApiModelProperty(value = "通过钱包ID查询")
    private Long storeId;

    @ApiModelProperty(value = "是否商家")
    private Boolean storeFlag;
}
