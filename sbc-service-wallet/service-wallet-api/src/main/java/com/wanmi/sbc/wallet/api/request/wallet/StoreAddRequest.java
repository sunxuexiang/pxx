package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.TerminalType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
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
public class StoreAddRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    @ApiModelProperty(value = "商家编号")
    private String storeAccount;
    @ApiModelProperty(value = "商家店铺名称")
    private String storeName;
    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeBalance;
    @ApiModelProperty(value = "付款方式")
    private String paymentName;
    @ApiModelProperty(value = "付款单号")
    private String payOrderNo;
    @ApiModelProperty(value = "商家Id")
    private Long storeId;

}
