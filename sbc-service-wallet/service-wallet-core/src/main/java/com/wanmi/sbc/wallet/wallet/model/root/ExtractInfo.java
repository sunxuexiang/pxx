package com.wanmi.sbc.wallet.wallet.model.root;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @create 2021-08-25 11:05
 */

@SqlResultSetMapping(
        name = "queryExtractInfo",  // 如果@Query 不指定name 会默认使用方法名
        classes = {
                @ConstructorResult(
                        targetClass = ExtractInfo.class,
                        columns= {
                        @ColumnResult(name = "customerAccount", type = String.class),
                        @ColumnResult(name = "dealPrice", type = BigDecimal.class),
                        @ColumnResult(name = "dealTime", type = LocalDateTime.class),
                        @ColumnResult(name = "extractStatus", type = Integer.class),
                        @ColumnResult(name = "remark", type = String.class)
                }
                )
        }
)
public class ExtractInfo {
    private String customerAccount;
    private BigDecimal dealPrice;
    private LocalDateTime dealTime;
    private Integer extractStatus;
    private String remark;

    public ExtractInfo() {
    }

    public ExtractInfo(String customerAccount, BigDecimal dealPrice, LocalDateTime dealTime, Integer extractStatus, String remark) {
        this.customerAccount = customerAccount;
        this.dealPrice = dealPrice;
        this.dealTime = dealTime;
        this.extractStatus = extractStatus;
        this.remark = remark;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public LocalDateTime getDealTime() {
        return dealTime;
    }

    public void setDealTime(LocalDateTime dealTime) {
        this.dealTime = dealTime;
    }

    public Integer getExtractStatus() {
        return extractStatus;
    }

    public void setExtractStatus(Integer extractStatus) {
        this.extractStatus = extractStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
