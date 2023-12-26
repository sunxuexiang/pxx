package com.wanmi.sbc.goods.api.request.stockoutdetail;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>缺货管理新增参数</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 缺货列表id
	 */
	@ApiModelProperty(value = "缺货列表id")
	private String stockoutId;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private String customerId;

	/**
	 * sku id
	 */
	@ApiModelProperty(value = "sku id")
	@NotBlank
	private String goodsInfoId;

	/**
	 * sku编码
	 */
	@ApiModelProperty(value = "sku编码")
	@NotBlank
	private String goodsInfoNo;

	/**
	 * 缺货数量
	 */
	@ApiModelProperty(value = "缺货数量")
	@NotNull
	@Min(1)
	private Long stockoutNum;

	/**
	 * 缺货市code
	 */
	@ApiModelProperty(value = "缺货市code")
	@NotBlank
	private String cityCode;

	/**
	 * 下单人详细地址
	 */
	@ApiModelProperty(value = "下单人详细地址")
	private String address;

	/**
	 * 删除标识,0:未删除1:已删除
	 */
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除", hidden = true)
	private DeleteFlag delFlag;

	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	@ApiModelProperty(value = "品牌名称")
	private String brandName;

	@ApiModelProperty(value = "仓库ID")
//	@NotNull
	private Long wareId;
}