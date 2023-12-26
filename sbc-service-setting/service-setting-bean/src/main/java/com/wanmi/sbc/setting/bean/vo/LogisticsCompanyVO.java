package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;

import com.wanmi.sbc.setting.bean.enums.LogisticsType;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>物流公司VO</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@ApiModel
@Data
public class LogisticsCompanyVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty(value = "id")
	private Long id;

	/**
	 * 公司编号
	 */
	@ApiModelProperty(value = "公司编号")
	private String companyNumber;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String logisticsName;

	/**
	 * 公司电话
	 */
	@ApiModelProperty(value = "公司电话")
	private String logisticsPhone;

	/**
	 * 物流公司地址
	 */
	@ApiModelProperty(value = "物流公司地址")
	private String logisticsAddress;

	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTime;

	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	private DeleteFlag delFlag;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 物流类型
	 */
	@ApiModelProperty("物流类型")
	private Integer logisticsType;
	private String logisticsTypeDesc;

	public String getLogisticsTypeDesc() {
		if(this.logisticsType!=null) {
			this.logisticsTypeDesc = LogisticsType.getByValue(this.logisticsType).getDesc();
		}
		return logisticsTypeDesc;
	}

	/**
	 * 市场Id
	 */
	@ApiModelProperty(value = "市场Id")
	private Long marketId;

	/**
	 * 批发市场名称
	 */
	@ApiModelProperty(value = "批发市场名称")
	private String marketName= Constants.EMPTY_STR;
	private String createPerson;
	private String receivingPoint;
}