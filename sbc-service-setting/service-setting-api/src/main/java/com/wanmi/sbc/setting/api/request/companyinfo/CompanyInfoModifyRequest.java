package com.wanmi.sbc.setting.api.request.companyinfo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>公司信息修改参数</p>
 * @author lq
 * @date 2019-11-05 16:09:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoModifyRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 公司信息ID
	 */
	@ApiModelProperty(value = "公司信息ID")
	@Max(9223372036854775807L)
	private Long companyInfoId;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	@Length(max=255)
	private String companyName;

	/**
	 * 法人身份证反面
	 */
	@ApiModelProperty(value = "法人身份证反面")
	@Length(max=1024)
	private String backIdcard;

	/**
	 * 省
	 */
	@ApiModelProperty(value = "省")
	@Max(9223372036854775807L)
	private Long provinceId;

	/**
	 * 市
	 */
	@ApiModelProperty(value = "市")
	@Max(9223372036854775807L)
	private Long cityId;

	/**
	 * 区
	 */
	@ApiModelProperty(value = "区")
	@Max(9223372036854775807L)
	private Long areaId;

	/**
	 * 详细地址
	 */
	@ApiModelProperty(value = "详细地址")
	@Length(max=255)
	private String detailAddress;

	/**
	 * 联系人
	 */
	@ApiModelProperty(value = "联系人")
	private String contactName;

	/**
	 * 联系方式
	 */
	@ApiModelProperty(value = "联系方式")
	private String contactPhone;

	/**
	 * 版权信息
	 */
	@ApiModelProperty(value = "版权信息")
	@Length(max=255)
	private String copyright;

	/**
	 * 公司简介
	 */
	@ApiModelProperty(value = "公司简介")
	private String companyDescript;

	/**
	 * 操作人
	 */
	@ApiModelProperty(value = "操作人")
	@Length(max=45)
	private String operator;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

	/**
	 * 商家类型 0、平台自营 1、第三方商家
	 */
	@ApiModelProperty(value = "商家类型 0、平台自营 1、第三方商家")
	@Max(127)
	private Integer companyType;

	/**
	 * 账户是否全部收到打款 0、否 1、是
	 */
	@ApiModelProperty(value = "账户是否全部收到打款 0、否 1、是")
	@Max(127)
	private Integer isAccountChecked;

	/**
	 * 社会信用代码
	 */
	@ApiModelProperty(value = "社会信用代码")
	@Length(max=50)
	private String socialCreditCode;

	/**
	 * 住所
	 */
	@ApiModelProperty(value = "住所")
	@Length(max=255)
	private String address;

	/**
	 * 法定代表人
	 */
	@ApiModelProperty(value = "法定代表人")
	@Length(max=150)
	private String legalRepresentative;

	/**
	 * 注册资本
	 */
	@ApiModelProperty(value = "注册资本")
	private BigDecimal registeredCapital;

	/**
	 *  成立日期
	 */
	@ApiModelProperty(value = " 成立日期")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime foundDate;

	/**
	 * 营业期限自
	 */
	@ApiModelProperty(value = "营业期限自")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime businessTermStart;

	/**
	 * 营业期限至
	 */
	@ApiModelProperty(value = "营业期限至")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime businessTermEnd;

	/**
	 * 经营范围
	 */
	@ApiModelProperty(value = "经营范围")
	@Length(max=1024)
	private String businessScope;

	/**
	 * 营业执照副本电子版
	 */
	@ApiModelProperty(value = "营业执照副本电子版")
	@Length(max=1024)
	private String businessLicence;

	/**
	 * 法人身份证正面
	 */
	@ApiModelProperty(value = "法人身份证正面")
	@Length(max=1024)
	private String frontIdcard;

	/**
	 * 商家编号
	 */
	@ApiModelProperty(value = "商家编号")
	@Length(max=32)
	private String companyCode;

	/**
	 * 商家名称
	 */
	@ApiModelProperty(value = "商家名称")
	@Length(max=50)
	private String supplierName;

	/**
	 * 是否删除 0 否  1 是
	 */
	@ApiModelProperty(value = "是否删除 0 否  1 是")
	private DeleteFlag delFlag;

}