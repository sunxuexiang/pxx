package com.wanmi.sbc.customer.contract.model.root;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "beluga_mall_contract")
public class BelugaMallContract {
    @Id
    @Column(name = "beluga_mall_id")
    @ApiModelProperty("合同ID")
    private String belugaMallId;

    @Column(name = "info_id")
    private String infoId;

    @Column(name = "beluga_user")
    @ApiModelProperty("承运商用户ID")
    private String belugaUser;


    @ApiModelProperty(value = "签名")
    @Column(name = "sign_image")
    private String signImage;

    @Column(name = "company_name")
    @ApiModelProperty("公司名称")
    private String companyName;

    @Column(name = "signing_date")
    @ApiModelProperty("签署时期")
    private String signingDate;

    @Column(name = "online_query_address")
    @ApiModelProperty("网上查询地址")
    private String onlineQueryAddress;

    @Column(name = "phone_number")
    @ApiModelProperty("电话")
    private String phoneNumber;

    @Column(name = "contact_address")
    @ApiModelProperty("联系地址")
    private String contactAddress;

    @Column(name = "email_address")
    @ApiModelProperty("邮箱地址")
    private String emailAddress;

    @Column(name = "invoice_type")
    @ApiModelProperty("发票类型")
    private String invoiceType;

    @Column(name = "start_date_of_cooperation")
    @ApiModelProperty("开始合作期限")
    private String startDateOfCooperation;

    @Column(name = "end_date_of_cooperation")
    @ApiModelProperty("结束合作期限")
    private String endDateOfCooperation;

    @Column(name = "deposit")
    @ApiModelProperty("保证金")
    private String deposit;

    @Column(name = "party_a_account_name")
    @ApiModelProperty("甲方开户名称")
    private String partyAAccountName;

    @Column(name = "party_a_bank")
    @ApiModelProperty("甲方开户银行")
    private String partyABank;

    @Column(name = "party_a_bank_account")
    @ApiModelProperty("甲方银行账号")
    private String partyABankAccount;

    @Column(name = "party_a_address")
    @ApiModelProperty("甲方地址")
    private String partyAAddress;

    @Column(name = "party_a_name")
    @ApiModelProperty("甲方名称")
    private String partyAName;

    @Column(name = "party_a_zip_code")
    @ApiModelProperty("甲方邮编")
    private String partyAZipCode;

    @Column(name = "party_a_legal_representative")
    @ApiModelProperty("甲方法定代表人")
    private String partyALegalRepresentative;

    @Column(name = "party_a_proxy")
    @ApiModelProperty("甲方委托代理人")
    private String partyAProxy;

    @Column(name = "party_a_phone")
    @ApiModelProperty("甲方电话")
    private String partyAPhone;

    @Column(name = "party_a_fax")
    @ApiModelProperty("甲方传真")
    private String partyAFax;

    @Column(name = "party_b_address")
    @ApiModelProperty("乙方地址")
    private String partyBAddress;

    @Column(name = "party_b_name")
    @ApiModelProperty("乙方名称")
    private String partyBName;

    @Column(name = "party_b_zip_code")
    @ApiModelProperty("乙方邮编")
    private String partyBZipCode;

    @Column(name = "party_b_legal_representative")
    @ApiModelProperty("乙方法定代表人")
    private String partyBLegalRepresentative;

    @Column(name = "party_b_proxy")
    @ApiModelProperty("乙方委托代理人")
    private String partyBProxy;

    @Column(name = "party_b_phone")
    @ApiModelProperty("乙方电话")
    private String partyBPhone;

    @Column(name = "party_b_fax")
    @ApiModelProperty("乙方传真")
    private String partyBFax;

    @Column(name = "authorizing_unit")
    @ApiModelProperty("授权单位")
    private String authorizingUnit;

    @Column(name = "id_card_number")
    @ApiModelProperty("身份证号")
    private String idCardNumber;

    @Column(name = "start_date_of_validity")
    @ApiModelProperty("有效期开始")
    private String startDateOfValidity;

    @Column(name = "end_date_of_validity")
    @ApiModelProperty("有效期结束")
    private String endDateOfValidity;

    @Column(name = "legal_representative_of_settlement")
    @ApiModelProperty("分账法定代表人")
    private String legalRepresentativeOfSettlement;

    @Column(name = "business_license_name")
    @ApiModelProperty("营业执照名称")
    private String businessLicenseName;

    @Column(name = "unified_credit_code_of_settlement")
    @ApiModelProperty("分账统一信用代码")
    private String unifiedCreditCodeOfSettlement;

    @Column(name = "id_card_of_legal_representative")
    @ApiModelProperty("法定代表人身份证号")
    private String idCardOfLegalRepresentative;

    @Column(name = "business_address")
    @ApiModelProperty("经营地址")
    private String businessAddress;

    @Column(name = "business_scope")
    @ApiModelProperty("经营范围")
    private String businessScope;

    @Column(name = "mobile_number_for_separation")
    @ApiModelProperty("分账手机号码")
    private String mobileNumberForSeparation;

    @Column(name = "signing_date_of_jianhang")
    @ApiModelProperty("建行签署日期")
    private String signingDateOfJianhang;

    @Column(name = "unit_agreement")
    @ApiModelProperty("单位同意")
    private String unitAgreement;

    @Column(name = "transaction_no")
    @ApiModelProperty("交易号")
    private String transactionNo;
    @Column(name = "contract_id")
    @ApiModelProperty("合同ID")
    private String contractId;
    @Column(name = "customer_id")
    @ApiModelProperty("法大大客户ID")
    private String customerId;
    @ApiModelProperty("联系人")
    @Column(name = "contract_person")
    private String contractPerson;
    @ApiModelProperty("乙方开户名称")
    @Column(name = "party_b_name_account")
    private String partyBNameAccount;
    @Column(name = "party_b_bank")
    @ApiModelProperty(value = "乙方开户银行")
    private String partyBBank;
    @Column(name = "party_b_bank_account")
    @ApiModelProperty(value = "乙方银行账号")
    private String partyBBankAccount;
}