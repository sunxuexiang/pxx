package com.wanmi.sbc.goods.api.request.merchantconfig;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;

/**
 * <p>商品推荐商品新增参数</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsAddRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐的商品编号
	 */
	@ApiModelProperty(value = "推荐的商品编号")
	@NotBlank
	@Length(max=32)
	private String goodsInfoId;

	@ApiModelProperty(value = "店铺ID")
	private Long storeId;
	/**
	 * 商户ID
	 */
	@ApiModelProperty(value = "商户ID")
	private Long 	companyInfoId;

	/**
	 *排序字段
	 */
	@ApiModelProperty(value = "排序字段")
	private int  sort;
}