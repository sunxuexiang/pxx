package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.goods.bean.enums.GoodsPriorityType;
import com.wanmi.sbc.goods.bean.enums.GoodsRecommendStatus;
import com.wanmi.sbc.goods.bean.enums.RecommendStrategyStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>商品推荐配置VO</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@Data
public class GoodsRecommendSettingVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品推荐配置主键
	 */
	@ApiModelProperty(value = "商品推荐配置主键")
	private String settingId;

	/**
	 * 商品推荐开关 （0:开启；1:关闭）
	 */
	@ApiModelProperty(value = "商品推荐开关 （0:开启；1:关闭）")
	private GoodsRecommendStatus enabled;

	/**
	 * 推荐入口
	 */
	@ApiModelProperty(value = "推荐入口")
	private String entries;

	/**
	 * 优先级
	 */
	@ApiModelProperty(value = "优先级")
	private GoodsPriorityType priority;

	/**
	 * 推荐规则
	 */
	@ApiModelProperty(value = "推荐规则")
	private Integer rule;


	@ApiModelProperty(value = "商品编号")
	private List<String> goodsInfoIds =new ArrayList<>();

	/**
	 * 商品列表
	 */
	@ApiModelProperty(value = "商品列表")
	private List<GoodsInfoVO> goodsInfos = new ArrayList<>();

	/**
	 * 品牌列表
	 */
	@ApiModelProperty(value = "品牌列表")
	private List<GoodsBrandVO> brands = new ArrayList();


	/**
	 * 分类列表
	 */
	@ApiModelProperty(value = "分类列表")
	private List<GoodsCateVO> cates = new ArrayList();


	@ApiModelProperty(value = "是否智能推荐  0 否  1.是")
	private BoolFlag isIntelligentRecommend;

	@ApiModelProperty(value = "用于智能推 推荐数量")
	private Integer intelligentRecommendAmount;

	@ApiModelProperty(value = "用于智能推 推荐维度  荐 0.三级类目 1.品牌")
	private Integer intelligentRecommendDimensionality ;

	@ApiModelProperty(value = "推荐策略 0.开启  1.关闭 ")
	private RecommendStrategyStatus intelligentStrategy ;

}