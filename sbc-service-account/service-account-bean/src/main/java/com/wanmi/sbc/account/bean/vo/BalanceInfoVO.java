package com.wanmi.sbc.account.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;
import springfox.documentation.service.ApiListing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @create 2021-08-26 11:06
 */
@Data
@ApiModel
public class BalanceInfoVO {

    @ApiModelProperty(value = "数字转中文类型")
    private String type;

    @ApiModelProperty(value = "交易类型【1充值，2提现，3余额支付】")
    private Integer tradeType;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal dealPrice;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "交易时间")
    private LocalDateTime dealTime;

    @ApiModelProperty(value = "当前余额")
    private BigDecimal currentBalance;

    public BalanceInfoVO convertFromNativeSQLResult(Object result) {
        Object[] results = StringUtil.cast(result, Object[].class);
        if (results == null || results.length < 1) {
            return this;
        }
        Integer tradeType = StringUtil.cast(results, 0, Integer.class);
        this.setTradeType(tradeType != null ? tradeType : 0);

        BigDecimal dealPrice = StringUtil.cast(results, 1, BigDecimal.class);
        this.setDealPrice(dealPrice != null ? dealPrice : null);

        Timestamp timestamp = StringUtil.cast(results, 2, Timestamp.class);
        LocalDateTime dealTime = timestamp.toLocalDateTime();
        this.setDealTime(dealTime != null ? dealTime : null);

        BigDecimal currentBalance = StringUtil.cast(results, 3, BigDecimal.class);
        this.setCurrentBalance(currentBalance);
        return this;
    }
}
