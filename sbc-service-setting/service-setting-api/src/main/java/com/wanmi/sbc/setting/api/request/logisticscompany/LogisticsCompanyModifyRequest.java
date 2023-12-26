package com.wanmi.sbc.setting.api.request.logisticscompany;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>物流公司修改参数</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty(value = "id")
	@Max(9223372036854775807L)
	private Long id;

	/**
	 * 公司编号
	 */
	@ApiModelProperty(value = "公司编号")
	@NotBlank
	@Length(max=20)
	private String companyNumber;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	@Length(max=100)
	private String logisticsName;

	/**
	 * 公司电话
	 */
	@ApiModelProperty(value = "公司电话")
	@Length(max=20)
	private String logisticsPhone;

	/**
	 * 物流公司地址
	 */
	@ApiModelProperty(value = "物流公司地址")
	@Length(max=200)
	private String logisticsAddress;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime deleteTime;

	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;;

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

	/**
	 * 市场Id
	 */
	@ApiModelProperty(value = "市场Id")
	private Long marketId;
}