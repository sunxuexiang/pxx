package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
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
public class WalletRecordListRequest extends BaseQueryRequest {

    @ApiModelProperty(value = "金额类型【0收入，1支出】")
    private BudgetType budgetType;

    @ApiModelProperty(value = "明细类型")
    private WalletDetailsType walletDetailsType;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "交易开始时间")
    private LocalDateTime startTime;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "交易结束时间")
    private LocalDateTime endTime;

}
