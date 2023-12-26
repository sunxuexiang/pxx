package com.wanmi.sbc.customer.company.model.root;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.store.model.root.Store;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 公司信息
 * Created by CHENLI on 2017/5/12.
 */
@Setter
@Getter
@Entity
@Table(name = "company_info")
public class CompanyInfoUpdate implements Serializable {
    private static final long serialVersionUID = -3430186713937000934L;
    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 商家编号
     */
    @Column(name = "company_code")
    private String companyCode;

    /**
     * 商家编号
     */
    @Column(name = "company_code_new")
    private String companyCodeNew;

    /**
     * 商家名称
     */
    @Column(name = "supplier_name")
    private String supplierName;

    /**
     * 公司名称
     */
    @Column(name = "company_name")
    private String companyName;

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
    @Column(name = "detail_address")
    private String detailAddress;

    /**
     * 联系人名字
     */
    @Column(name = "contact_name")
    private String contactName;

    /**
     * 联系方式
     */
    @Column(name = "contact_phone")
    private String contactPhone;

    /**
     * 版权信息
     */
    @Column(name = "copyright")
    private String copyright;

    /**
     * 公司简介
     */
    @Column(name = "company_descript")
    private String companyDescript;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    @Column(name = "operator")
    private String operator;

    /**
     * 修改时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 社会信用代码
     */
    @Column(name = "social_credit_code")
    private String socialCreditCode;

    /**
     * 住所
     */
    @Column(name = "address")
    private String address;

    /**
     * 法定代表人
     */
    @Column(name = "legal_representative")
    private String legalRepresentative;

    /**
     * 注册资本
     */
    @Column(name = "registered_capital")
    private BigDecimal registeredCapital;

    /**
     * 成立日期
     */
    @Column(name = "found_date")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime foundDate;

    /**
     * 营业期限自
     */
    @Column(name = "business_term_start")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime businessTermStart;

    /**
     * 营业期限至
     */
    @Column(name = "business_term_end")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime businessTermEnd;

    /**
     * 经营范围
     */
    @Column(name = "business_scope")
    private String businessScope;

    /**
     * 营业执照副本电子版
     */
    @Column(name = "business_licence")
    private String businessLicence;

    /**
     * 法人身份证正面
     */
    @Column(name = "front_ID_card")
    private String frontIDCard;

    /**
     * 法人身份证反面
     */
    @Column(name = "back_ID_card")
    private String backIDCard;

    /**
     * 删除标志 0未删除 1已删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 是否确认打款
     */
    @Column(name = "remit_affirm")
    private BoolFlag remitAffirm = BoolFlag.NO;

    /**
     * 入驻时间(第一次审核通过时间)
     */
    @Column(name = "apply_enter_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyEnterTime;

    /**
     * erp的Id
     */
    @Column(name = "erp_id")
    private String erpId;

    /**
     * 商家类型0品牌商城，1商家
     */
    @Column(name = "store_type")
    private StoreType storeType;

    @Column(name = "corporate_telephone")
    private String corporateTelephone;

    @Column(name = "warehouse_address")
    private String warehouseAddress;

    @Column(name = "door_image")
    private String doorImage;

    @Column(name = "warehouse_image")
    private String warehouseImage;

    @Column(name = "id_card_no")
    private String idCardNo;


    public StoreType getStoreType() {
        if (Objects.isNull(storeType)) {
            return StoreType.SUPPLIER;
        }
        return storeType;
    }


}
