package com.wanmi.sbc.goods.api.request.merchantconfig;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * <p>商品推荐商品修改参数</p>
 * @author SGY
 * @date 2023-06-10 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsModifyRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐商品主键编号
	 */
	@ApiModelProperty(value = "推荐商品主键编号")
	@Length(max=32)
	private String merchantRecommendId;

	/**
	 * 推荐的商品编号
	 */
	@ApiModelProperty(value = "推荐的商品编号")
	@NotBlank
	@Length(max=32)
	private String goodsInfoId;
	/**
	 * 商户ID
	 */
	@ApiModelProperty(value = "商户ID")
	private Long 	companyInfoId;

}