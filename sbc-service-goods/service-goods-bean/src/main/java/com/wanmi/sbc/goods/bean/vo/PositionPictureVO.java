package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>竞价配置VO</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@Data
public class PositionPictureVO implements Serializable {
	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "id")
	private Long positionId;


	@ApiModelProperty(value = "wareId")
	private Long wareId;

	@ApiModelProperty(value = "wareName")
	private String wareName;


	@ApiModelProperty(value = "imageUrl")
	private String imageUrl;


	@ApiModelProperty(value = "delFlag")
	private int delFlag;

	/**
	 * 类型0是批发1是散批2是零售
	 */
	@ApiModelProperty(value = "type")
	private int type;

	/**
	 * 品牌店铺ID
	 */
	@ApiModelProperty(name = "storeId")
	private Long storeId;


	/**
	 * 服务电话
	 */
	@ApiModelProperty(value = "服务电话")
	private String servicePhone;


	/**
	 * 客服工作时间
	 */
	@ApiModelProperty(value = "客服工作时间")
	private String serviceTime;


	/**
	 * 关于商品
	 */
	@ApiModelProperty(value = "关于商品")
	private String aboutProduct;


	/**
	 * 关于物流
	 */
	@ApiModelProperty(value = "关于物流")
	private String aboutLogistics;


	/**
	 * 关于售后
	 */
	@ApiModelProperty(value = "关于售后")
	private String aboutSales;

	/**
	 * 背景颜色
	 */
	@ApiModelProperty(value = "背景颜色")
	private String backgroundColor;


	@ApiModelProperty(value = "客服工作时间开关：0、关闭；1、开启")
	private Integer serviceTimeSwitch;

	@ApiModelProperty(value = "关于商品开关：0、关闭；1、开启")
	private Integer aboutProductSwitch;

	@ApiModelProperty(value = "关于物流开关：0、关闭；1、开启")
	private Integer aboutLogisticsSwitch;

	@ApiModelProperty(value = "关于售后开关：0、关闭；1、开启")
	private Integer aboutSalesSwitch;

	@ApiModelProperty(value = "描述开关：0、关闭；1、开启")
	private Integer descSwitch;
}