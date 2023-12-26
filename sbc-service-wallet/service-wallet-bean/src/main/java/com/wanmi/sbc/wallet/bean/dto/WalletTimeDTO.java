package com.wanmi.sbc.wallet.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.wallet.bean.enums.GatherType;
import com.wanmi.sbc.wallet.bean.enums.TradeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by hht on 2017/12/6.
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTimeDTO implements Serializable{

    private static final long serialVersionUID = 7191673821175696972L;
//    cw.customer_account,cw.balance,cw.block_balance,cw.store_id
    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String customerAccount;


    /**
     * 用户可用余额
     */
    @ApiModelProperty(value = "用户可用余额")
    private BigDecimal balance;

    /**
     * 用户冻结金额
     */
    @ApiModelProperty(value = "用户冻结金额")
    private BigDecimal blockBalance;
    /**
     * 商户ID
     * */
    @ApiModelProperty(value = "商户名称")
    private String storeName;

}
