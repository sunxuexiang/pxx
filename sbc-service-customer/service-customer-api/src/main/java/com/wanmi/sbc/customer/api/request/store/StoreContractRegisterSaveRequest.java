package com.wanmi.sbc.customer.api.request.store;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.customer.api.request.store.validGroups.StoreUpdate;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 店铺基本信息参数
 * Created by CHENLI on 2017/11/2.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class StoreContractRegisterSaveRequest extends BaseRequest {
    private static final long serialVersionUID = -4687907203853776352L;
    /**
     * 店铺主键
     */
    @ApiModelProperty(value = "店铺主键")
    @NotNull(groups = {StoreUpdate.class})
    private Long storeId;

    /**
     * 店铺logo
     */
    @ApiModelProperty(value = "店铺logo")
    private String storeLogo;

    /**
     * 店铺店招
     */
    @ApiModelProperty(value = "店铺店招")
    private String storeSign;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    @NotNull(groups = {StoreUpdate.class})
    private Long companyInfoId;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    @NotBlank
    @Length(max = 20)
    private String supplierName;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    @NotBlank
    @Length(max = 20)
    private String storeName;

    /**
     * 联系人名字
     */
    @ApiModelProperty(value = "联系人名字")
    @NotBlank
    @Length(min = 2, max = 15)
    private String contactPerson;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    @NotBlank
    @Length(max = 11)
    private String contactMobile;

    /**
     * 联系邮箱
     */
    @ApiModelProperty(value = "联系邮箱")
    private String contactEmail;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    @NotNull
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

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    @NotBlank
    @Length(max = 60)
    private String addressDetail;

    /**
     * 是否重置密码 0 否 1 是
     */
    @ApiModelProperty(value = "是否重置密码")
    private Boolean isResetPwd = Boolean.FALSE;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String accountPassword;

    /**
     * 商家账号
     */
    @ApiModelProperty(value = "商家账号")
    private String accountName;

    /**
     * 账号类型 0 b2b账号 1 s2b平台端账号 2 s2b商家端账号
     */
    @ApiModelProperty(value = "账号类型")
    private AccountType accountType;

    /**
     * 使用的运费模板类别(0:店铺运费,1:单品运费)
     */
    @ApiModelProperty(value = "使用的运费模板类别(0:店铺运费,1:单品运费)")
    private DefaultFlag freightTemplateType = DefaultFlag.NO;


    @ApiModelProperty(value = "商家退货地址")
    private CompanyMallReturnGoodsAddressVO returnGoodsAddress;

    @ApiModelProperty(value = "法人电话")
    private String corporateTelephone;

    @ApiModelProperty(value = "仓库地址")
    private String warehouseAddress;

    @ApiModelProperty(value = "门口照")
    private String doorImage;

    @ApiModelProperty(value = "仓库照片")
    private String warehouseImage;
    /**
     * 社会信用代码
     */
    @ApiModelProperty(value = "社会信用代码")
    private String socialCreditCode;
    @ApiModelProperty(value = "营业执照照片")
    private String businessUrl;
    @ApiModelProperty(value = "经营范围")
    private String businessScope;
    @ApiModelProperty(value = "经营地址")
    private String businessAddress;
    /**
     * 法人身份证正面
     */
    @ApiModelProperty(value = "法人身份证正面")
    private String frontIDCard;
    @ApiModelProperty(value = "1:tab ,2:商城")
    @NotNull
    private Integer relationType;
    @ApiModelProperty(value = "关联的name")
    private String relationName;
    /**
     * 关联的value
     */
    @ApiModelProperty(value = "关联的value，tabId,商城分类Id")
    private String relationValue;
    @ApiModelProperty(value = "商家类型")
    private Integer personId;
    @ApiModelProperty(value = "银行省ID")
    private Long bankProvinceId;
    @ApiModelProperty(value = "银行市ID")
    private Long bankCityId;
    @ApiModelProperty(value = "银行区ID")
    private Long bankAreaId;
    @ApiModelProperty(value = "开户行名称")
    private String bankAccountName;
    @ApiModelProperty(value = "账号")
    private String bankAccount;
    @ApiModelProperty(value = "开户行")
    private String bank;
    @ApiModelProperty(value = "签约批发市场")
    private String tabRelationValue;
    @ApiModelProperty(value = "签约皮厂市场值")
    private String tabRelationName;
    @ApiModelProperty(value = "店铺联系人")
    private String storeContract;
    @ApiModelProperty(value = "店铺联系地址")
    private String storeContractPhone;
    @ApiModelProperty(value = "收货地址")
    private String detailAddress;
    @ApiModelProperty(value = "身份证号码")
    private String idCardNo;
}
