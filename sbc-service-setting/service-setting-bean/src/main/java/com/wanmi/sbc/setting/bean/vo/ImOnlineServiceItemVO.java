package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-05 17:30
 * @Description: 腾讯客服  IM
 * @Version 1.0
 */
@ApiModel
@Data
public class ImOnlineServiceItemVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 在线客服座席id
	 */
	@ApiModelProperty(value = "在线客服座席id")
	private Integer imServiceItemId;
	/**
	 * 商家ID
	 */
	@ApiModelProperty(value = "商家ID")
	private Long companyInfoId;

	/**
	 * 在线客服主键
	 */
	@ApiModelProperty(value = "在线客服主键")
	private Integer imOnlineServiceId;

	/**
	 * 客服昵称
	 */
	@ApiModelProperty(value = "客服昵称")
	private String customerServiceName;

	/**
	 * 客服账号
	 */
	@ApiModelProperty(value = "客服账号")
	private String customerServiceAccount;

	/**
	 * 删除标志 默认0：未删除 1：删除
	 */
	@ApiModelProperty(value = "删除标志 默认0：未删除 1：删除")
	private DeleteFlag delFlag;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

	/**
	 * 操作人
	 */
	@ApiModelProperty(value = "操作人")
	private String operatePerson;

	/**
	 * 店铺ID
	 */
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

	/**
	 * 员工信息ID
	 */
	@ApiModelProperty(value = "员工信息ID")
	private String employeeId;

	/**
	 * 员工手机号码
	 */
	@ApiModelProperty(value = "员工手机号码")
	private String phoneNo;

	@ApiModelProperty(value = "是否管理员标记：0、否；1、是；")
	private Integer managerFlag;

	@ApiModelProperty(value = "客服状态：0、正常；1、忙碌（不接受用户聊天请求）；2、忙碌")
	private Integer serviceStatus = 0;

}