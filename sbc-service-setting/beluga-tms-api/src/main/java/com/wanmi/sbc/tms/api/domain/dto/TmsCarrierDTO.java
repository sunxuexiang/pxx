package com.wanmi.sbc.tms.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 承运商信息对象 tms_carrier
 *
 * @author jkp
 * @date 2023-09-15
 */
@Data
public class TmsCarrierDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 承运商名称 */
    private String carrierName;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactMobile;

    /** 所在批发市场 */
    private String mall;

    /** 商家账号 */
    private String carrierAccount;

    /** 企业名称 */
    private String companyName;

    /** 统一社会信用代码 */
    private String creditCode;

    /** 法人 */
    private String legalPerson;

    /** 法人电话 */
    private String legalMobile;

    /** 营业执照 */
    private String businessLicense;

    /** 法人身份证 */
    private String idCard;

    /** 道路运输许可证 */
    private String transportLicense;

    /** 营运证 */
    private String operationLicense;

    /** 其他资质 */
    private String otherLicense;

    /** 建行商家编码 */
    private String ccbCode;

    /** 收款银行 */
    private String bankName;

    /** 账户名 */
    private String bankUser;

    /** 收款账户 */
    private String bankAccount;

    /** 支行名称 */
    private String bankBranch;

    /** 分账比例 */
    private Double fzRatio;

    /** 结算周期 */
    private String finalPeriod;

    /** 保证金状态 */
    private Integer bailState;

    /** 保证金金额 */
    private Double bailMoney;

    /** 签约有效开始日期 */
    private String contractBeginTime;

    /** 签约有效截止日期 */
    private String contractEndTime;

    /** 合同附件 */
    private String contractAttach;

    /** 状态（1正常 0停用） */
    private Integer status;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer delFlag;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;

}
