package com.wanmi.sbc.customer.bean.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoAuditCompanyRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long recordId;


    /**
     * 商家ID
     */
    private Long companyInfoId;


    /**
     * 商家名称
     */
    private String companyName;


    /**
     * 店铺ID
     */
    private Long storeId;


    /**
     * 店铺名称
     */
    private String storeName;


    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    private CompanyType companyType;


    /**
     * 建行商家编码
     */
    private String mktMrchId;


    /**
     * 建行商家名称
     */
    private String mktMrchNm;


    /**
     * 分账比例（%）
     */
    private BigDecimal shareRatio;


    /**
     * 计算周期
     */
    private Integer settlementCycle;


    /**
     * 审核状态 0、待审核 1、已审核 2、审核未通过
     */
    private Integer auditState;


    /**
     * 签约开始时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractStartDate;


    /**
     * 签约结束时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractEndDate;


    /**
     * 自动审核时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createDate;

}
