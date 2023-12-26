package com.wanmi.sbc.goods.api.request.merchantconfig;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>商品推荐商品通用查询请求参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-推荐商品主键编号List
	 */
	@ApiModelProperty(value = "批量查询-推荐商品主键编号List")
	private List<String> recommendIdList;

	/**
	 * 推荐商品主键编号
	 */
	@ApiModelProperty(value = "推荐商品主键编号")
	private String recommendId;

	/**
	 * 推荐的商品编号
	 */
	@ApiModelProperty(value = "推荐的商品编号")
	private String goodsInfoId;

	/**
	 * 批量查询-推荐商品编号Lis
	 */
	@ApiModelProperty(value = "批量查询-推荐商品编号List")
	private List<String> goodsInfoIds;
	@ApiModelProperty(value = "商户ID")
	private Long 	companyInfoId;
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

}