package com.wanmi.sbc.goods.api.request.warehousecity;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p> 仓库地区表通用查询请求参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseCityQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-主键IDList
	 */
	@ApiModelProperty(value = "批量查询-主键IDList")
	private List<Long> idList;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "主键ID")
	private Long id;

	/**
	 * 仓库iD
	 */
	@ApiModelProperty(value = "仓库iD")
	private Long wareId;

	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	private Long provinceId;

	/**
	 * 市
	 */
	@ApiModelProperty(value = "市")
	private Long cityId;

	/**
	 * 区县ID
	 */
	@ApiModelProperty(value = "区县ID")
	private Long areaId;

}