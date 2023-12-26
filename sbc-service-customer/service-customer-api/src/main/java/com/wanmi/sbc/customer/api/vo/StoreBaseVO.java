package com.wanmi.sbc.customer.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.customer.api.utils.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.customer.api.utils.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreBaseVO implements Serializable {

    private static final long serialVersionUID = -5716176566940635853L;

    /**
     * 店铺主键
     */
    @ApiModelProperty(value = "店铺主键")
    private Long storeId;

    /**
     * 公司信息
     */
    @ApiModelProperty(value = "公司信息")
    private CompanyBaseInfoVO companyInfo;

    /**
     * 签约开始日期
     */
    @ApiModelProperty(value = "签约开始日期")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractStartDate;

    /**
     * 签约结束日期
     */
    @ApiModelProperty(value = "签约结束日期")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractEndDate;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

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
     * 联系人名字
     */
    @ApiModelProperty(value = "联系人名字")
    private String contactPerson;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
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

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String addressDetail;

    /**
     * 结算日 多个结算日期用逗号分割 eg：5,15,25
     */
    @ApiModelProperty(value = "结算日")
    private String accountDay;

    /**
     * 审核状态 0、待审核 1、已审核 2、审核未通过
     */
    @ApiModelProperty(value = "审核状态")
    private Integer auditState;

    /**
     * 审核未通过原因
     */
    @ApiModelProperty(value = "审核未通过原因")
    private String auditReason;

    /**
     * 店铺状态 0、开启 1、关店
     */
    @ApiModelProperty(value = "店铺状态")
    private Integer storeState;

    /**
     * 店铺关店原因
     */
    @ApiModelProperty(value = "店铺关店原因")
    private String storeClosedReason;

    /**
     * 删除标志 0未删除 1已删除
     */
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型(0、平台自营 1、第三方商家)")
    private Integer companyType;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;

    /**
     * 申请入驻时间
     */
    @ApiModelProperty(value = "申请入驻时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyEnterTime;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请入驻时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;


    /**
     * 使用的运费模板类别(0:店铺运费,1:单品运费)
     */
    @ApiModelProperty(value = "使用的运费模板类别(0:店铺运费,1:单品运费)")
    private Integer freightTemplateType;

    /**
     * 一对多关系，多个SPU编号
     */
    @ApiModelProperty(value = "多个SPU编号")
    private List<String> goodsIds = new ArrayList<>();

    /**
     * 店铺小程序码
     */
    @ApiModelProperty(value = "店铺小程序码")
    private String smallProgramCode;

    /**
     * 商家类型0品牌商城，1商家
     */
    @ApiModelProperty(value = "商家类型0品牌商城，1商家")
    private Integer storeType;

    /**
     * erp的Id
     */
    @ApiModelProperty(value = "erp的Id")
    private String erpId;

    @ApiModelProperty(value = "建行商户号")
    private String constructionBankMerchantNumber;

    @ApiModelProperty(value = "分账比例")
    private BigDecimal shareRatio;

    @ApiModelProperty(value = "周期")
    private Integer settlementCycle;

    @ApiModelProperty(value = "商家提现密码")
    private Integer storePayPassword;

    @ApiModelProperty(value = "支付密码错误次数")
    private Integer payErrorTime;

    @ApiModelProperty(value = "支付锁定时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime payLockTime;

    /**
     * 囤货状态0未开启，1已开启，已关闭
     */
    @ApiModelProperty(value = "囤货状态")
    private Integer pileState;

    @ApiModelProperty(value = "1：个人，2：企业")
    private Integer personId;

    /**
     * 是否开启预售 0、未开启 1、已开启
     */
    @ApiModelProperty(value = "预售状态")
    private Integer presellState;

    private Integer selfManage;

    private Integer assignSort;
}
