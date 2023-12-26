package com.wanmi.sbc.goods.api.request.goodsrecommendsetting;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import com.wanmi.sbc.goods.bean.enums.GoodsPriorityType;
import com.wanmi.sbc.goods.bean.enums.GoodsRecommendStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * <p>商品推荐配置新增参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingAddRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品推荐开关 （0:开启；1:关闭）
	 */
	@ApiModelProperty(value = "商品推荐开关 （0:开启；1:关闭）")
	@NotNull
	private GoodsRecommendStatus enabled;

	/**
	 * 推荐入口
	 */
	@ApiModelProperty(value = "推荐入口")
	@Length(max=32)
	private String entries;

	/**
	 * 优先级
	 */
	@ApiModelProperty(value = "优先级")
	@NotNull
	private GoodsPriorityType priority;

	/**
	 * 推荐规则
	 */
	@ApiModelProperty(value = "推荐规则")
	@NotNull
	@Max(127)
	private Integer rule;

}