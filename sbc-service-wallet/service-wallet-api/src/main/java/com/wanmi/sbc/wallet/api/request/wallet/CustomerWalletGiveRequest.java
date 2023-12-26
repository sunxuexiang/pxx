package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.TerminalType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletGiveRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    /**
     * 钱包id
     */
    @ApiModelProperty(value = "钱包id")
    private Long walletId;
    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "用户账号")
    private String customerAccount;
    @ApiModelProperty(value = "操作类型")
    private Integer opertionType;
    @ApiModelProperty(value = "鲸币类型")
    private WalletRecordTradeType walletRecordTradeType;
    @ApiModelProperty(value = "转入的商家ID")
    private String changOverToStoreAccount;

    /**
     * 赠送金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal balance;

    @ApiModelProperty(value = "商户ID")
    private String storeId;

    @ApiModelProperty(value = "订单编号")
    private String relationOrderId;
    @ApiModelProperty(value = "退单号")
    private String returnOrderCode;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    @ApiModelProperty(value = "交易备注")
    private String tradeRemark;

    @ApiModelProperty(value = "交易时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime dealTime;

    @ApiModelProperty(value = "支付类型")
    private Integer payType;

    @ApiModelProperty(value = "订单活动类型")
    private String activityType;
}
