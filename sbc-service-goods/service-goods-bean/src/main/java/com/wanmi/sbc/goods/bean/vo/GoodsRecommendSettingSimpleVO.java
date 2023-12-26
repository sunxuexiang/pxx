package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.goods.bean.enums.GoodsRecommendStatus;
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
public class GoodsRecommendSettingSimpleVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品推荐开关 （0:开启；1:关闭）
	 */
	@ApiModelProperty(value = "商品推荐开关 （0:开启；1:关闭）")
	private GoodsRecommendStatus enabled;

	/**
	 * 商品列表
	 */
	@ApiModelProperty(value = "商品列表")
	private List<SettingGoodsInfoVO> goodsInfos = new ArrayList<>();

}