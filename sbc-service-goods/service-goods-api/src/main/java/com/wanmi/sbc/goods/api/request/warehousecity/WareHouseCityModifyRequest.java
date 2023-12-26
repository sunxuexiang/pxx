package com.wanmi.sbc.goods.api.request.warehousecity;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * <p> 仓库地区表修改参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseCityModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "主键ID")
	@Max(9223372036854775807L)
	private Long id;

	/**
	 * 仓库iD
	 */
	@ApiModelProperty(value = "仓库iD")
	@NotNull
	@Max(9223372036854775807L)
	private Long wareId;

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
	 * 区县ID
	 */
	@ApiModelProperty(value = "区县ID")
	@NotNull
	@Max(9223372036854775807L)
	private Long areaId;

}