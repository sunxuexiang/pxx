package com.wanmi.sbc.customer.contract.model.root;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "customer_contract")
public class CustomerContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_contract_id")
    private String customerContractId;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "address")
    private String address;

    @Column(name = "contract_phone")
    private String contractPhone;

    @Column(name = "representative")
    private String representative;

    @Column(name = "id_card_no")
    private String idCardNo;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account")
    private String account;

    @Column(name = "bank")
    private String bank;

    @Column(name = "charging_standards")
    private String chargingStandards;

    @Column(name = "unit_confirmation")
    private String unitConfirmation;

    @Column(name = "unit_login")
    private String unitLogin;

    @Column(name = "application_signing_date")
    @Temporal(TemporalType.DATE)
    private Date applicationSigningDate;

    @Column(name = "unit_authorization")
    private String unitAuthorization;

    @Column(name = "period_start")
    @Temporal(TemporalType.DATE)
    private Date periodStart;

    @Column(name = "period_end")
    @Temporal(TemporalType.DATE)
    private Date periodEnd;

    @Column(name = "business_license_name")
    private String businessLicenseName;

    @Column(name = "credit_code")
    private String creditCode;

    @Column(name = "legal_person_name")
    private String legalPersonName;

    @Column(name = "legal_id_card_no")
    private String legalIdCardNo;

    @Column(name = "business_address")
    private String businessAddress;

    @Column(name = "business_scope")
    private String businessScope;

    @Column(name = "phone")
    private String phone;

    @Column(name = "sign_time")
    private String signTime;

    @Column(name = "unit_consent")
    private String unitConsent;

    @Column(name = "is_person")
    private Integer isPerson;

    @Column(name = "sign_image")
    private String signImage;

    @Column(name = "other_prove")
    private String otherProve;

    @Column(name = "other_docment")
    private String otherDocment;
    @Column(name = "id_crad_url")
    private String idCradUrl;
    @Column(name = "business_url")
    private String businessUrl;
    @Column(name = "relation_type")
    private Integer relationType;
    @Column(name = "relation_value")
    private String relationValue;
    @Column(name = "relation_name")
    private String relationName;
    @Column(name = "store_sign")
    private String storeSign;
    @Column(name = "door_photo")
    private String doorPhoto;
    @Column(name = "app_customer_id")
    private String appCustomerId;
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
    @Column(name = "detail_address")
    private String detailAddress;
    /**
     * 省
     */
    @Column(name = "bank_province_id")
    private Long bankProvinceId;

    /**
     * 市
     */
    @Column(name = "bank_city_id")
    private Long bankCityId;

    /**
     * 区
     */
    @Column(name = "bank_area_id")
    private Long bankAreaId;
    @Column(name = "store_contract")
    private String storeContract;
    @Column(name = "store_contract_phone")
    private String storeContractPhone;
    @Column(name = "tab_relation_value")
    private String tabRelationValue;
    @Column(name = "tab_relation_name")
    private String tabRelationName;
    @Column(name = "store_name")
    private String storeName;

    public CustomerContract(String contractPhone, String tabRelationValue, String tabRelationName) {
        this.contractPhone = contractPhone;
        this.tabRelationValue = tabRelationValue;
        this.tabRelationName = tabRelationName;
    }

    public CustomerContract() {
    }
}

