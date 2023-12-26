package com.wanmi.sbc.customer.api.response.fadada;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
public class FadadaCompanyResponese {
    private static final long serialVersionUID = -1469274484762938357L;

    private int code;
    private MyData data;
    private String msg;
    private Agreement agreement;
    @Data
    static class MyData {
        private Agreement agreement;
        private String authenticationSubmitTime;
        private BankCard bankCard;
        private Company company;
        private Manager manager;
        private String passTime;
        private String transactionNo;
        private String type;
        private WebankOrder webankOrder;
    }

    @Data
    static class Agreement {
        private String agreementIds;
        private String confirmRecordIds;

    }

    @Data
    static class BankCard {
        private String bankCardNo;
        private String bankName;
        private String bankDetailName;
        private String branchBankCode;
        private String provinceName;
        private String cityName;
        private String status;
        private String enterTime;
        private String payType;
        private String verifyCode;
        private String enterVerifyCode;
    }

    @Data
    static class Company {
        private String auditFailReason;
        private String auditorTime;
        private String certificatesType;
        private String companyEmail;
        private String companyName;
        private String hasagent;
        private String legal;
        private String legalMobile;
        private String legalName;
        private String organization;
        private String organizationPath;
        private String organizationType;
        private String regFormPath;
        private String relatedTransactionNo;
        private String status;
        private String verifyType;
        private String identType;
        private String isThreeCertType;

    }

    @Data
    static class Manager {
        private String address;
        private String areaCode;
        private String auditFailReason;
        private String auditorTime;
        private String backgroundIdCardPath;
        private String birthday;
        private String expiresDate;
        private String fork;
        private String headPhotoPath;
        private String idCard;
        private String isLongTerm;
        private String issueAuthority;
        private String mobile;
        private String personName;
        private String photoUuid;
        private String sex;
        private String startDate;
        private String status;
        private String type;
        private String verifyType;
        private String certType;
        private String isPassFourElement;
        private String isPassThreeElement;
        // Constructors

        // Getters and Setters
    }

    @Data
    static class WebankOrder {
        private String liveRate;
        private String orderNo;
        private String similarity;
        private String status;
    }

}

