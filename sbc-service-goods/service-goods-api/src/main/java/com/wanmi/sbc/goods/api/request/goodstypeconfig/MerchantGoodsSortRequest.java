package com.wanmi.sbc.goods.api.request.goodstypeconfig;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendGoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>排序修改</p>
 * @author sgy
 * @date 2023-06-08 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantGoodsSortRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * merchant_recommend_type
	 * 批量查询-推荐商品主键编号List
	 */
	@ApiModelProperty(value = "批量查询-推荐商品主键编号List")
	private List<MerchantRecommendGoodsVO> sortList;
	@ApiModelProperty(value = "商家Id")
	private Long companyInfoId;

}