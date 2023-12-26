package com.wanmi.sbc.goods.api.request.icitem;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>配送到家分页查询请求参数</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcitemPageRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-skuList
	 */
	@ApiModelProperty(value = "批量查询-skuList")
	private List<String> skuList;

	/**
	 * sku
	 */
	@ApiModelProperty(value = "sku")
	private String sku;

	/**
	 * name
	 */
	@ApiModelProperty(value = "name")
	private String name;

	/**
	 * tiji
	 */
	@ApiModelProperty(value = "tiji")
	private BigDecimal tiji;

	/**
	 * weight
	 */
	@ApiModelProperty(value = "weight")
	private BigDecimal weight;

}