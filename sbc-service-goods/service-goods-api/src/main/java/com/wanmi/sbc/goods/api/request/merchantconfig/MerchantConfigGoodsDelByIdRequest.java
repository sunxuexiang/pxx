package com.wanmi.sbc.goods.api.request.merchantconfig;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>单个删除商品推荐商品请求参数</p>
 * @author SGY
 * @date 2023-06-07 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsDelByIdRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐商品主键编号
	 */
	@ApiModelProperty(value = "推荐商品主键编号")
	@NotNull
	private String merchantRecommendId;
	/**
	 * 商户ID
	 */
	@ApiModelProperty(value = "商户ID")
	private Long 	companyInfoId;
}