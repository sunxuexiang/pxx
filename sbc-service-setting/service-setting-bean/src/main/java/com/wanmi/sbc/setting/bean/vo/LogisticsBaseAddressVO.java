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
 * @desc 物流线路
 * @author shiy  2023/11/7 9:38
*/
@ApiModel
@Data
public class LogisticsBaseAddressVO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	@ApiModelProperty("id")
	private Long id;

	/**
	 * 出发省
	 */
	@ApiModelProperty("出发省")
	private String provinceCode;

	/**
	 * 出发省
	 */
	@ApiModelProperty("出发省")
	private String provinceName;

	/**
	 * 出发城
	 */
	@ApiModelProperty("出发城")
	private String cityCode;

	/**
	 * 出发城
	 */
	@ApiModelProperty("出发城")
	private String cityName;

	/**
	 * 出发区
	 */
	@ApiModelProperty("出发区")
	private String areaCode;

	/**
	 * 出发区
	 */
	@ApiModelProperty("出发区")
	private String areaName;

	/**
	 * 出发街
	 */
	@ApiModelProperty("出发街")
	private String townCode;

	/**
	 * 出发街
	 */
	@ApiModelProperty("出发街")
	private String townName;

	/**
	 * 备注
	 */
	@ApiModelProperty("备注")
	private String remark;

	/**
	 * 删除标志
	 */
	@ApiModelProperty("删除标志")
	private DeleteFlag delFlag;

	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTime;

	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;
}