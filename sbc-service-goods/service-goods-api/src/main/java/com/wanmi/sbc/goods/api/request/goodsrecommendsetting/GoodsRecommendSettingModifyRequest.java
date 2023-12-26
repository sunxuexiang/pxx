package com.wanmi.sbc.goods.api.request.goodsrecommendsetting;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import com.wanmi.sbc.goods.bean.enums.GoodsPriorityType;
import com.wanmi.sbc.goods.bean.enums.GoodsRecommendStatus;
import com.wanmi.sbc.goods.bean.enums.RecommendStrategyStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * <p>商品推荐配置修改参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingModifyRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品推荐配置主键
	 */
	@ApiModelProperty(value = "商品推荐配置主键")
	@Length(max=32)
	private String settingId;

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

	/**
	 * 商品编号
	 */
	@ApiModelProperty(value = "商品编号")
	private String[] goodsInfoIds;

	@ApiModelProperty(value = "是否智能推荐  0 否  1.是")
	private BoolFlag isIntelligentRecommend;

	@ApiModelProperty(value = "用于智能推 推荐数量")
	private Integer intelligentRecommendAmount;

	@ApiModelProperty(value = "用于智能推 推荐维度  荐 0.三级类目 1.品牌")
	private Integer intelligentRecommendDimensionality ;

	@ApiModelProperty(value = "推荐策略 0.开启  1.关闭 ")
	private RecommendStrategyStatus intelligentStrategy ;



}