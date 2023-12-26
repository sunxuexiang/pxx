package com.wanmi.sbc.wallet.bean.vo;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @create 2021-08-25 19:24
 */
@Data
public class ExtractInfoVO {
    @ApiModelProperty("用户ID")
    private String customerAccount;

    @ApiModelProperty("金额")
    private BigDecimal dealPrice;

    @ApiModelProperty("交易时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime dealTime;

    @ApiModelProperty("交易状态")
    private Integer extractStatus;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("交易数字转中文 ")
    private String status;

    @ApiModelProperty("提现方式")
    private String extractType;

    @ApiModelProperty("交易单号")
    private String recordNo;

    @ApiModelProperty("操作时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;

    @ApiModelProperty("手续费")
    private BigDecimal chargePrice;

    public ExtractInfoVO convertFromNativeSQLResult(Object result) {
        Object[] results = StringUtil.cast(result, Object[].class);
        if (results == null || results.length < 1) {
            return this;
        }
        String customerAccount = StringUtil.cast(results, 0, String.class);
        this.setCustomerAccount(customerAccount);

        BigDecimal dealPrice = StringUtil.cast(results, 1, BigDecimal.class);
        this.setDealPrice(dealPrice != null ? dealPrice : null);

        Timestamp timestamp = StringUtil.cast(results, 2, Timestamp.class);
        LocalDateTime dealTime = timestamp.toLocalDateTime();
        this.setDealTime(dealTime != null ? dealTime : null);

        Integer extractStatus = StringUtil.cast(results, 3, Integer.class);
        this.setExtractStatus(extractStatus != null ? extractStatus : null);

        String remark = StringUtil.cast(results, 4, String.class);
        this.setRemark(remark);

        String extractType = StringUtil.cast(results, 5, String.class);
        this.setExtractType(extractType);

        String recordNo = StringUtil.cast(results, 6, String.class);
        this.setRecordNo(recordNo);

        Timestamp timestamp1 = StringUtil.cast(results, 7, Timestamp.class);
        LocalDateTime applyTime = timestamp1.toLocalDateTime();
        this.setApplyTime(applyTime != null ? applyTime : null);

        BigDecimal chargePrice = StringUtil.cast(results, 8, BigDecimal.class);
        this.setChargePrice(chargePrice != null ? chargePrice : null);

        return this;
    }
}
