package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
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
public class ImOnlineServiceVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 在线客服主键
	 */
	@ApiModelProperty(value = "在线客服主键")
	private Integer imOnlineServiceId;

	/**
	 * 商家ID
	 */
	@ApiModelProperty(value = "商家ID")
	private Long companyInfoId;

	/**
	 * 在线客服是否启用 0 不启用， 1 启用
	 */
	@ApiModelProperty(value = "在线客服是否启用 0 不启用， 1 启用")
	private DefaultFlag serverStatus;

	/**
	 * 客服标题
	 */
	@ApiModelProperty(value = "客服标题")
	private String serviceTitle;

	/**
	 * 生效终端pc 0 不生效 1 生效
	 */
	@ApiModelProperty(value = "生效终端pc 0 不生效 1 生效")
	private DefaultFlag effectivePc;

	/**
	 * 生效终端App 0 不生效 1 生效
	 */
	@ApiModelProperty(value = "生效终端App 0 不生效 1 生效")
	private DefaultFlag effectiveApp;

	/**
	 * 生效终端移动版 0 不生效 1 生效
	 */
	@ApiModelProperty(value = "生效终端移动版 0 不生效 1 生效")
	private DefaultFlag effectiveMobile;

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

}