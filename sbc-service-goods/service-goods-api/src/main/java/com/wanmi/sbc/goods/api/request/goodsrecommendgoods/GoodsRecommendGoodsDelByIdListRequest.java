package com.wanmi.sbc.goods.api.request.goodsrecommendgoods;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * <p>批量删除商品推荐商品请求参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendGoodsDelByIdListRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-推荐商品主键编号List
	 */
	@ApiModelProperty(value = "批量删除-推荐商品主键编号List")
	@NotEmpty
	private List<String> recommendIdList;
	/**
	 * 公司信息ID
	 */
	@ApiModelProperty(value = "公司信息ID")
	private Long companyInfoId;
}