package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletUserPageQueryRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    @ApiModelProperty(value = "客户名称")
    private String accountName;
    @ApiModelProperty(value = "客户账号")
    private String customerAccount;
    @ApiModelProperty(value = "用户或商家")
    private Boolean storeFlag;
    @ApiModelProperty(value = "是否有鲸币0:无,1:有")
    private Integer isMoney;



}
