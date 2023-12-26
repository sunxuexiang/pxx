package com.wanmi.sbc.goods.api.request.warehouse;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityAddRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>仓库表修改参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * wareId
	 */
	@ApiModelProperty(value = "wareId")
	@Max(9223372036854775807L)
	private Long wareId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	@NotNull
	@Max(9223372036854775807L)
	private Long storeId;

	/**
	 * 仓库名称
	 */
	@ApiModelProperty(value = "仓库名称")
	@NotBlank
	@Length(max=20)
	private String wareName;

	/**
	 * 仓库编号
	 */
	@ApiModelProperty(value = "仓库编号")
	@NotBlank
	@Length(max=20)
	private String wareCode;

	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	@NotNull
	@Max(9223372036854775807L)
	private Long provinceId;

	/**
	 * 市
	 */
	@ApiModelProperty(value = "市")
	@NotNull
	@Max(9223372036854775807L)
	private Long cityId;

	/**
	 * 区
	 */
	@ApiModelProperty(value = "区")
	@NotNull
	@Max(9223372036854775807L)
	private Long areaId;

	/**
	 * 详细地址
	 */
	@ApiModelProperty(value = "详细地址")
	@Length(max=50)
	private String addressDetail;

	/**
	 * 是否默认仓 0：否，1：是
	 */
	@ApiModelProperty(value = "是否默认仓 0：否，1：是")
	private DefaultFlag defaultFlag;
	/**
	 * 是否默认仓 0：否，1：是
	 */
	@ApiModelProperty(value = "是否支持自提 0:否 , 1:是 ")
	private PickUpFlag pickUpFlag;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 编辑人
	 */
	@ApiModelProperty(value = "编辑人", hidden = true)
	private String updatePerson;

	/**
	 * 覆盖区域
	 */
	@ApiModelProperty(value = "覆盖区域")
	@NotEmpty
	private String[] destinationArea;

	/**
	 * 覆盖区域名称
	 */
	@ApiModelProperty(value = "覆盖区域名称")
	@NotEmpty
	private String[] destinationAreaName;



	@ApiModelProperty(value = "覆盖区域对象")
	private List<WareHouseCityAddRequest> wareHouseCityList;

	/**
	 * 纬度
	 */

	@ApiModelProperty(value = "纬度")
	private Double lat;

	/**
	 * 经度
	 */
	@ApiModelProperty(value = "经度")
	private Double lng;

	@ApiModelProperty(value = "仓库类型")
	private DefaultFlag type;

	@ApiModelProperty(value = "配送范围km")
	private Long distance;
}