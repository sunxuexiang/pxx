package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @desc 物流线路
 * @author shiy  2023/11/7 9:38
*/
@ApiModel
@Data
public class LogisticsBaseSiteVO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * site_id
	 */
	@ApiModelProperty("site_id")
	private Long siteId;

	/**
	 * 物流公司编号
	 */
	@ApiModelProperty("物流公司编号")
	private Long logisticsId;

	/**
	 * 站点名
	 */
	@ApiModelProperty("站点名")
	private String siteName;

	/**
	 * 站点人
	 */
	@ApiModelProperty("站点人")
	private String sitePerson;

	/**
	 * 站点电话
	 */
	@ApiModelProperty("站点电话")
	private String sitePhone;

	private Integer siteCrtType;

	/**
	 * 区域ID
	 */
	@ApiModelProperty("区域ID")
	private Long baseAddressId;

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