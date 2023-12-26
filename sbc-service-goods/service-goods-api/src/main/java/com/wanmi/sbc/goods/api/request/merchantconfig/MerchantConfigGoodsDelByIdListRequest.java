package com.wanmi.sbc.goods.api.request.merchantconfig;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * <p>批量删除商品推荐商品请求参数</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsDelByIdListRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-推荐商品主键编号List
	 */
	@ApiModelProperty(value = "批量删除-推荐商品主键编号List")
	@NotEmpty
	private List<String>  recommendIdList;


	@ApiModelProperty(value = "商家Id")
	private Long companyInfoId;


}