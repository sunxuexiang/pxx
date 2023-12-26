package com.wanmi.sbc.customer.api.request.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BelugaMallContractRequest {
    @ApiModelProperty(value = "合同ID")
    private String belugaMallId;

    @ApiModelProperty(value = "承运商UserID")
    private String belugaUser;
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "签署时期")
    private String signingDate;

    @ApiModelProperty(value = "网上查询地址")
    private String onlineQueryAddress;

    @ApiModelProperty(value = "电话")
    private String phoneNumber;

    @ApiModelProperty(value = "联系地址")
    private String contactAddress;

    @ApiModelProperty(value = "联系人")
    private String contractPerson;

    @ApiModelProperty(value = "邮箱地址")
    private String emailAddress;

    @ApiModelProperty(value = "发票类型")
    private String invoiceType;

    @ApiModelProperty(value = "开始合作期限")
    private String cooperationStartDate;

    @ApiModelProperty(value = "结束合作期限")
    private String cooperationEndDate;

    @ApiModelProperty(value = "保证金")
    private double securityDeposit;

    @ApiModelProperty(value = "甲方开户名称")
    private String partyANameAccount;

    @ApiModelProperty(value = "甲方开户银行")
    private String partyABank;

    @ApiModelProperty(value = "甲方银行账号")
    private String partyABankAccount;

    @ApiModelProperty(value = "乙方开户名称")
    private String partyBNameAccount;

    @ApiModelProperty(value = "乙方开户银行")
    private String partyBBank;

    @ApiModelProperty(value = "乙方银行账号")
    private String partyBBankAccount;

    @ApiModelProperty(value = "甲方地址")
    private String partyAAddress;

    @ApiModelProperty(value = "甲方名称")
    private String partyAName;

    @ApiModelProperty(value = "甲方邮编")
    private String partyAPostcode;

    @ApiModelProperty(value = "甲方法定代表人")
    private String partyALegalRepresentative;

    @ApiModelProperty(value = "甲方委托代理人")
    private String partyAProxy;

    @ApiModelProperty(value = "甲方电话")
    private String partyAPhone;

    @ApiModelProperty(value = "甲方传真")
    private String partyAFax;

    @ApiModelProperty(value = "乙方地址")
    private String partyBAddress;

    @ApiModelProperty(value = "乙方名称")
    private String partyBName;

    @ApiModelProperty(value = "乙方邮编")
    private String partyBPostcode;

    @ApiModelProperty(value = "乙方法定代表人")
    private String partyBLegalRepresentative;

    @ApiModelProperty(value = "乙方委托代理人")
    private String partyBProxy;

    @ApiModelProperty(value = "乙方电话")
    private String partyBPhone;

    @ApiModelProperty(value = "乙方传真")
    private String partyBFax;

    @ApiModelProperty(value = "授权单位")
    private String authorizedUnit;

    @ApiModelProperty(value = "身份证号")
    private String idCardNumber;

    @ApiModelProperty(value = "有效期开始")
    private String validStartDate;

    @ApiModelProperty(value = "有效期结束")
    private String validEndDate;

    @ApiModelProperty(value = "分账法定代表人")
    private String legalRepresentative;

    @ApiModelProperty(value = "营业执照名称")
    private String licenseName;

    @ApiModelProperty(value = "分账统一信用代码")
    private String creditCode;

    @ApiModelProperty(value = "法定代表人身份证号")
    private String representativeIdNumber;

    @ApiModelProperty(value = "经营地址")
    private String businessAddress;

    @ApiModelProperty(value = "经营范围")
    private String businessScope;

    @ApiModelProperty(value = "分账手机号码")
    private String phoneForSeparation;

    @ApiModelProperty(value = "建行签署日期")
    private String signingDateForCCB;

    @ApiModelProperty(value = "单位同意")
    private String isAgreed;
    @ApiModelProperty(value = "签名")
    private String signImage;
    @ApiModelProperty(value = "营业执照地址")
    private String businessUrl;
    @ApiModelProperty(value = "身份证地址")
    private String idCradUrl;
}
