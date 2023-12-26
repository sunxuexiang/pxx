package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>配送到家VO</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@ApiModel
@Data
public class HomeDeliveryVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	private Long homeDeliveryId;


	/**
	 * 配送到家文案
	 */
	@ApiModelProperty(value = "配送到家文案")
	private String content;

	/**
	 * 物流文案
	 */
	@ApiModelProperty(value = "物流文案")
	private String logisticsContent;

	/**
	 * 快递文案
	 */
	@ApiModelProperty(value = "快递文案")
	private String expressContent;

	/**
	 * 自提文案
	 */
	@ApiModelProperty(value = "自提文案")
	private String pickSelfContent;

	/**
	 * 配送费用文案
	 */
	@ApiModelProperty(value = "配送费用文案")
	private String expensesCostContent;

	/**
	 * 上门自提文案
	 */
	@ApiModelProperty("上门自提文案")
	private String doorPickContent;

	/**
	 * 配送到店文案
	 */
	@ApiModelProperty("配送到店文案")
	private String deliveryToStoreContent;

	@ApiModelProperty("同城配送")
	private String intraCityLogisticsContent;

	@ApiModelProperty("指定物流")
	private String specifyLogisticsContent;

	@ApiModelProperty("快递到家(到付)")
	private String expressArrivedContent;

	@ApiModelProperty("店铺标识")
	private Long storeId;
}