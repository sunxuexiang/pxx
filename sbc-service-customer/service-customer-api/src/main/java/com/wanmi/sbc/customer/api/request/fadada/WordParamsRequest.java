package com.wanmi.sbc.customer.api.request.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
@Component
public class WordParamsRequest {
    private static final long serialVersionUID = -1469274484762938357L;

    @ApiModelProperty(value = "商家ID")
    private Long storeId;
    @ApiModelProperty(value = "公司ID")
    private Long companyId;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "联系方式")
    private String contractPhone;
    @ApiModelProperty(value = "代表人")
    private String representative;
    @ApiModelProperty(value = "身份证号码")
    private String idCardNo;
    @ApiModelProperty(value = "户名")
    private String accountName;
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "开户行")
    private String bank;
    @ApiModelProperty(value = "收费标准")
    private String chargingStandards;
    @ApiModelProperty(value = "单位确认")
    private String unitConfirmation;
    @ApiModelProperty(value = "单位登录")
    private String unitlogin;
    @ApiModelProperty(value = "申请书签署日")
    private String applicationSigningDate;
    @ApiModelProperty(value = "单位授权")
    private String unitAuthorization;
    @ApiModelProperty(value = "授权有效期开始")
    private String periodStart;
    @ApiModelProperty(value = "授权有效期结束")
    private String periodEnd;
    @ApiModelProperty(value = "营业执照名称")
    private String businessLicenseName;
    @ApiModelProperty(value = "信用代码")
    private String creditCode;
    @ApiModelProperty(value = "法人名称")
    private String legalPersonName;
    @ApiModelProperty(value = "法人身份证号码")
    private String legalIdCardNo;
    @ApiModelProperty(value = "经营地址")
    private String businessAddress;
    @ApiModelProperty(value = "经营范围")
    private String businessScope;
    @ApiModelProperty(value = "手机号码")
    private String phone;
    @ApiModelProperty(value = "签署时间")
    private String signTime;
    @ApiModelProperty(value = "单位同意")
    private String unitConsent;
    @ApiModelProperty(value = "员工ID")
    private String employeeId;
    @ApiModelProperty(value = "法大大合同ID")
    private String contractId;
    @ApiModelProperty(value = "交易ID")
    private String transactionId;
    @ApiModelProperty(value = "个人或者企业：1:个人 2:企业")
    private Integer isPerson;
    @ApiModelProperty(value = "企业类型，1.个人，2.企业")
    @Value(value = "2")
    private String accountType;
    @ApiModelProperty(value = "签名图片")
    private String signImage;
    @ApiModelProperty(value = "其他相关证明材料")
    private String otherProve;
    @ApiModelProperty(value = "其他相关证件材料")
    private String otherDocment;
    @ApiModelProperty(value = "身份证照片URL")
    private String idCradUrl;
    @ApiModelProperty(value = "营业执照照片")
    private String businessUrl;
    @ApiModelProperty(name = "1: 签约商城Tab， 2：签约批发市场")
    private Integer relationType;
    @ApiModelProperty(name = "签约类型对应的value")
    private String relationValue;
    @ApiModelProperty(name = "签约类型对应的name")
    private String relationName;
    @ApiModelProperty(name = "仓库门头照")
    private String storeSign;
    @ApiModelProperty(name = "门头照片")
    private String doorPhoto;

    @ApiModelProperty(value = "招商经理")
    private String investmentManager;

    @ApiModelProperty(value = "招商经理ID")
    private String investemntManagerId;

    @ApiModelProperty(value = "移动端用户ID")
    private String appCustomerId;

    @ApiModelProperty(value = "银行省ID")
    private Long bankProvinceId;
    @ApiModelProperty(value = "银行市ID")
    private Long bankCityId;
    @ApiModelProperty(value = "银行区ID")
    private Long bankAreaId;
    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private Long areaId;
    @ApiModelProperty(value = "街道")
    private Long street;
    @ApiModelProperty(value = "详情地址")
    private String detailAddress;
    @ApiModelProperty(value = "店铺联系人")
    private String storeContract;
    @ApiModelProperty(value = "店铺联系地址")
    private String storeContractPhone;
    @ApiModelProperty(value = "签约批发市场")
    private String tabRelationValue;
    @ApiModelProperty(value = "签约皮厂市场值")
    private String tabRelationName;
    @ApiModelProperty(value = "店铺名称")
    private String storeName;
    @ApiModelProperty(value = "提交APPID")
    private String appId;




}

