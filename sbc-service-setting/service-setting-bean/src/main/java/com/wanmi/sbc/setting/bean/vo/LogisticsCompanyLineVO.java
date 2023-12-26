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
public class LogisticsCompanyLineVO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * line_id
	 */
	@ApiModelProperty("line_id")
	private Long lineId;

	/**
	 * 物流公司编号
	 */
	@ApiModelProperty("物流公司编号")
	private Long logisticsId;

	/**
	 * 出发站点
	 */
	@ApiModelProperty("出发站点")
	private Long fromSiteId;

	/**
	 * 出发站点
	 */
	@ApiModelProperty("出发站点")
	private Long toSiteId;
	/**
	 * 到达点
	 */
	@ApiModelProperty("到达点")
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