package com.wanmi.sbc.goods.goodsrecommendsetting.model.root;


import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.goods.bean.enums.GoodsPriorityType;
import com.wanmi.sbc.goods.bean.enums.GoodsRecommendStatus;
import com.wanmi.sbc.goods.bean.enums.RecommendStrategyStatus;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>商品推荐配置实体类</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@Data
@Entity
@Table(name = "goods_recommend_setting")
public class GoodsRecommendSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品推荐配置主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "setting_id")
	private String settingId;

	/**
	 * 商品推荐开关 （0:开启；1:关闭）
	 */
	@Column(name = "enabled")
	@Enumerated
	private GoodsRecommendStatus enabled;

	/**
	 * 推荐入口
	 */
	@Column(name = "entries")
	private String entries;

	/**
	 * 优先级 枚举
	 */
	@Column(name = "priority")
	@Enumerated
	private GoodsPriorityType priority;

	/**
	 * 推荐规则
	 */
	@Column(name = "rule")
	private Integer rule;

	/**
	 * 是否智能推荐  0 否  1.是
	 */
	@Column(name = "is_intelligent_recommend")
	private BoolFlag isIntelligentRecommend;

	/**
	 * 用于智能推 推荐数量
	 */
	@Column(name = "intelligent_recommend_amount")
	private Integer intelligentRecommendAmount;

	/**
	 * 用于智能推 推荐维度 0.三级类目 1.品牌
	 */
	@Column(name = "intelligent_recommend_dimensionality")
	private Integer intelligentRecommendDimensionality ;

	/**
	 * 推荐策略
	 */
	@Column(name = "intelligent_strategy")
	@Enumerated
	private RecommendStrategyStatus intelligentStrategy ;

}