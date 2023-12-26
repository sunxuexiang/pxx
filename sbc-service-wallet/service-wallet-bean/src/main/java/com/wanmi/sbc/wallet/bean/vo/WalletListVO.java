package com.wanmi.sbc.wallet.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 钱包VO
 */
@Data
public class WalletListVO implements Serializable {
    @ApiModelProperty(value = "账户名称")
    private String customerAccount;
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "鲸币余额")
    private BigDecimal balance;
    @ApiModelProperty(value = "最近一次获得时间")
    private Timestamp jTime;
    @ApiModelProperty(value = "最近一次获得金额")
    private BigDecimal jMoney;
    @ApiModelProperty(value = "最后一次获得时间")
    private Timestamp hTime;
    @ApiModelProperty(value = "最后一次获得金额")
    private BigDecimal hMoney;
    @ApiModelProperty(value = "钱包ID")
    private Long walletId;
}
