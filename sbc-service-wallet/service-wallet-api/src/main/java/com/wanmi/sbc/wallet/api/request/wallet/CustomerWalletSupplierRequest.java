package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-24 14:06
 */
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletSupplierRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "商铺名称")
    private String supplierName;

    /**
     * 客户账户
     */
    @ApiModelProperty(value = "商家账号")
    private String supplierAccount;

    /**
     * 充值时间
     * */
    @ApiModelProperty(value = "开始充值时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束充值时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @ApiModelProperty(value = "商家ID集合")
    private List<Long> storedIds;

    @ApiModelProperty(value = "商家customerId集合")
    private List<String> customerIds;


}
