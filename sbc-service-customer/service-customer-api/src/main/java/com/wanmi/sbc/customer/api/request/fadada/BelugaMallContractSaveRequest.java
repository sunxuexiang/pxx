package com.wanmi.sbc.customer.api.request.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@ApiModel
public class BelugaMallContractSaveRequest {
    @ApiModelProperty("公司名称")
    private String companyName;
    @ApiModelProperty("法人名称")
    private String legalName;
    @ApiModelProperty("法人手机号码")
    private String legalPhone;
    @ApiModelProperty("联系人")
    private String contract;
    @ApiModelProperty("联系方式")
    private String contractPhone;
    @ApiModelProperty("传真")
    private String fax;
    @ApiModelProperty(name = "邮箱")
    private String email;
    @ApiModelProperty(name = "营业执照代码")
    private String creditCode;
    @ApiModelProperty(name = "开户名称")
    private String bankAccountName;
    @ApiModelProperty(name = "开户银行")
    private String bankName;
    @ApiModelProperty(name = "开户银行账号")
    private String bankAccount;
    @ApiModelProperty(name = "开户支行")
    private String bankBranch;
    /**
     * 省
     */
    @ApiModelProperty(name = "省ID")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(name = "市ID")
    private Long cityId;

    /**
     * 区
     */
    @ApiModelProperty(name = "区ID")
    private Long areaId;
    /**
     * 街道
     */
    private Long twonId;
    @ApiModelProperty(name = "详细地址")
    private String detailAddress;
    @ApiModelProperty(name = "营业执照地址")
    private String businessUrl;
    @ApiModelProperty(name = "身份证正面")
    private String frontOfIdCrad;
    @ApiModelProperty(name = "身份证反面")
    private String reverseOfIdCrad;
}
