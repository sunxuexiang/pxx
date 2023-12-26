package com.wanmi.sbc.customer.company.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "auto_audit_company_record")
public class AutoAuditCompanyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "record_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    /**
     * 商家ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 商家名称
     */
    @Column(name = "company_name")
    private String companyName;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 店铺名称
     */
    @Column(name = "store_name")
    private String storeName;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @Column(name = "company_type")
    @Enumerated
    private CompanyType companyType;

    /**
     * 建行商家编码
     */
    @Column(name = "mkt_mrch_id")
    private String mktMrchId;

    /**
     * 建行商家名称
     */
    @Column(name = "mkt_mrch_nm")
    private String mktMrchNm;

    /**
     * 分账比例（%）
     */
    @Column(name = "share_ratio")
    private BigDecimal shareRatio;

    /**
     * 计算周期
     */
    @Column(name = "settlement_cycle")
    private Integer settlementCycle;

    /**
     * 审核状态 0、待审核 1、已审核 2、审核未通过
     */
    @Column(name = "audit_state")
    private Integer auditState;

    /**
     * 签约开始时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "contract_start_date")
    private LocalDateTime contractStartDate;

    /**
     * 签约结束时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "contract_end_date")
    private LocalDateTime contractEndDate;

    /**
     * 自动审核时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_date")
    private LocalDateTime createDate;

}
