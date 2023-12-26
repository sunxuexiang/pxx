package com.wanmi.sbc.goods.api.request.goodsrecommendsetting;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import com.wanmi.sbc.goods.bean.enums.RecommendStrategyStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 商品推荐策略
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingModifyStrategyRequest extends GoodsBaseRequest {


	@ApiModelProperty(value = "ture 开启智能推荐策略 false 开启手动推荐策略  ")
	private Boolean isOPenIntelligentStrategy =Boolean.FALSE ;



}