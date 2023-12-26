package com.wanmi.sbc.customer.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>日分销业绩数据Bean</p>
 * Created by of628-wenzhi on 2019-04-18-16:27.
 */
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistributionPerformanceDTO implements Serializable {
    private static final long serialVersionUID = -9165502445041409861L;
    /**
     * 业务员id
     */
    @ApiModelProperty("业务员id")
    @NotNull
    private String distributionId;

    /**
     * 会员id，此处做冗余
     */
    @ApiModelProperty("会员id")
    @NotNull
    private String customerId;

    /**
     * 销售额
     */
    @ApiModelProperty("销售额")
    @NotNull
    @Min(0)
    private BigDecimal saleAmount;

    /**
     * 预估收益
     */
    @ApiModelProperty("预估收益")
    @NotNull
    @Min(0)
    private BigDecimal commission;

    /**
     * 业绩录入日期
     */
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @ApiModelProperty("业绩录入日期")
    @NotNull
    private LocalDate targetDate;
}
