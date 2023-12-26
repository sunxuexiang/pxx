package com.wanmi.sbc.goods.api.request.goodstypeconfig;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>分类推荐分类列表查询请求参数</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTypeConfigListRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "批量查询-推荐分类主键编号List")
	private List<String> merchantRecommendTypeIdList;

	/**
	 * 推荐分类主键编号
	 */
	@ApiModelProperty(value = "推荐分类主键编号")
	private String merchantRecommendTypeId;
	/**
	 * 推荐的分类编号
	 */
	@ApiModelProperty(value = "推荐的分类编号")
	private String merchantRecommendType;
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

	@ApiModelProperty(value = "商家Id")
	private Long companyInfoId;

}