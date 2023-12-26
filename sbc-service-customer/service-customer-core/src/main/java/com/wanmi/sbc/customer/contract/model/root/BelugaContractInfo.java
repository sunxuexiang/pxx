package com.wanmi.sbc.customer.contract.model.root;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "beluga_contract_info")
public class BelugaContractInfo {
    @Id
    @Column(name = "info_id")
    @ApiModelProperty("合同ID")
    private String infoId;
    @Column(name = "company_name")
    @ApiModelProperty("公司名称")
    private String companyName;
    @Column(name = "legal_name")
    private String legalName;
    @Column(name = "legal_phone")
    private String legalPhone;
    @Column(name = "contract")
    private String contract;
    @Column(name = "contract_phone")
    private String contractPhone;
    @Column(name = "fax")
    private String fax;
    @Column(name = "email")
    private String email;
    @Column(name = "credit_code")
    private String creditCode;
    @Column(name = "bank_account_name")
    private String bankAccountName;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_account")
    private String bankAccount;
    @Column(name = "bank_branch")
    private String bankBranch;
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
    @Column(name = "twon_id")
    private Long twonId;
    @Column(name = "business_url")
    private String businessUrl;
    @Column(name = "front_of_id_crad")
    private String frontOfIdCrad;
    @Column(name = "reverse_of_id_crad")
    private String reverseOfIdCrad;
    @Column(name = "status")
    private Integer status;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "contract_id")
    private String contractId;
    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "contract_url")
    @ApiModelProperty("合同下载浏览地址")
    private String contractUrl;
}
