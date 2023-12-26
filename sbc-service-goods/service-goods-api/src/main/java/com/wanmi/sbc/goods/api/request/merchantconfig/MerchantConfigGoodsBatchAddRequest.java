package com.wanmi.sbc.goods.api.request.merchantconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>商品推荐商品新增参数</p>
 * @author sgy
 * @date 2019-06-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsBatchAddRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐的商品编号
	 */
	@ApiModelProperty(value = "推荐的商品编号")
	private List<String> goodsInfoId;

	@ApiModelProperty(value = "店铺ID")
	private Long storeId;
	/**
	 * 商户ID
	 */
	@ApiModelProperty(value = "商户ID")
	private Long 	companyInfoId;
}