package com.wanmi.sbc.customer.store.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺信息
 * Created by CHENLI on 2017/11/2.
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "store")
public class StoreSimple implements Serializable {
    /**
     * 店铺主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 签约开始日期
     */
    @Column(name = "contract_start_date")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractStartDate;

    /**
     * 签约结束日期
     */
    @Column(name = "contract_end_date")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractEndDate;

    /**
     * 店铺名称
     */
    @Column(name = "store_name")
    private String storeName;

    /**
     * 店铺logo
     */
    @Column(name = "store_logo")
    private String storeLogo;

    /**
     * 店铺店招
     */
    @Column(name = "store_sign")
    private String storeSign;

    /**
     * 联系人名字
     */
    @Column(name = "contact_person")
    private String contactPerson;

    /**
     * 联系方式
     */
    @Column(name = "contact_mobile")
    private String contactMobile;

    /**
     * 联系邮箱
     */
    @Column(name = "contact_email")
    private String contactEmail;

    /**
     * 省
     */
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 市
     */
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 区
     */
    @Column(name = "area_id")
    private Long areaId;

    /**
     * 详细地址
     */
    @Column(name = "address_detail")
    private String addressDetail;

    /**
     * 结算日 多个结算日期用逗号分割 eg：5,15,25
     */
    @Column(name = "account_day")
    private String accountDay;

    /**
     * 审核状态 0、待审核 1、已审核 2、审核未通过
     */
    @Column(name = "audit_state")
    private CheckState auditState;

    /**
     * 审核未通过原因
     */
    @Column(name = "audit_reason")
    private String auditReason;

    /**
     * 店铺状态 0、开启 1、关店
     */
    @Column(name = "store_state")
    private StoreState storeState;

    /**
     * 店铺关店原因
     */
    @Column(name = "store_closed_reason")
    private String storeClosedReason;

    /**
     * 删除标志 0未删除 1已删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @Column(name = "company_type")
    @Enumerated
    private CompanyType companyType;

    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 商家名称
     */
    @Column(name = "supplier_name")
    private String supplierName;

    /**
     * 申请入驻时间
     * 目前代码的实际时间为审核通过时间
     */
    @Column(name = "apply_enter_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyEnterTime;


    /**
    *  发起申请时间
    */
    @Column(name = "apply_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;

    /**
     * 店铺小程序码
     */
    @Column(name = "small_program_code")
    private String smallProgramCode;

    /**
     * 使用的运费模板类别(0:店铺运费,1:单品运费)
     */
    @Column(name = "freight_template_type")
    private DefaultFlag freightTemplateType;

    /**
     * 一对多关系，多个SPU编号
     */
    @Transient
    private List<String> goodsIds = new ArrayList<>();

    /**
     * 商家类型0品牌商城，1商家
     */
    @Column(name = "store_type")
    private StoreType storeType;

    /**
     * erp的Id
     */
    @Column(name = "erp_id")
    private String erpId;

    @Column(name = "construction_bank_merchant_number")
    private String constructionBankMerchantNumber;

    @Column(name = "share_ratio")
    private BigDecimal shareRatio;

    @Column(name = "settlement_cycle")
    private Integer settlementCycle;

    @Column(name = "store_salt_val")
    private String storeSaltVal;

    @Column(name = "store_pay_password")
    private Integer storePayPassword;

    @Column(name = "pay_error_time")
    private Integer payErrorTime;

    @Column(name = "pay_lock_time")
    private LocalDateTime payLockTime;

    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "self_manage")
    private Integer selfManage;

    @Column(name = "border_image")
    private String borderImage;

    @Column(name = "assign_sort")
    private Integer assignSort;
}
