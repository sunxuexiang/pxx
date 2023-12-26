package com.wanmi.sbc.goods.api.request.goodsrecommendsetting;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.bean.enums.GoodsPriorityType;
import com.wanmi.sbc.goods.bean.enums.GoodsRecommendStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>商品推荐配置列表查询请求参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingListRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-商品推荐配置主键List
	 */
	@ApiModelProperty(value = "批量查询-商品推荐配置主键List")
	private List<String> settingIdList;

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

}