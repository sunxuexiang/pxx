package com.wanmi.sbc.goods.api.request.goodsrecommendsetting;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>单个查询商品推荐配置请求参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingByIdRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品推荐配置主键
	 */
	@ApiModelProperty(value = "商品推荐配置主键")
	@NotNull
	private String settingId;
}