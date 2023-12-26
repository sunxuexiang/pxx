package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>仓库表新增参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识", hidden = true)
	@Max(9223372036854775807L)
	private Long storeId;

	/**
	 * 仓库名称
	 */
	@ApiModelProperty(value = "仓库名称")
	@NotBlank
	@Length(max=10)
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
	@NotNull
	private Long areaId;

	/**
	 * 详细地址
	 */
	@ApiModelProperty(value = "详细地址", hidden = true)
	@Length(max=30)
	private String addressDetail;

	/**
	 * 是否默认仓 0：否，1：是
	 */
	@ApiModelProperty(value = "是否默认仓 0：否，1：是", hidden = true)
	private DefaultFlag defaultFlag;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人", hidden = true)
	private String createPerson;

	/**
	 * 编辑人
	 */
	@ApiModelProperty(value = "编辑人", hidden = true)
	private String updatePerson;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@ApiModelProperty(value = "是否删除标志 0：否，1：是", hidden = true)
	private DeleteFlag delFlag;

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