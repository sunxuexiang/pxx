package com.wanmi.sbc.goods.api.request.goodsrecommendgoods;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * <p>商品推荐商品新增参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendGoodsBatchAddRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐的商品编号
	 */
	@ApiModelProperty(value = "推荐的商品编号")
	private String[] goodsInfoId;

	@ApiModelProperty(value = "仓库id")
	private Long wareId;
	/**
	 * 公司信息ID
	 */
	@ApiModelProperty(value = "公司信息ID")
	private Long companyInfoId;

}