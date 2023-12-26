package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @desc  物流轨迹
 * @author shiy  2023/6/8 14:25
*/
@ApiModel
@Data
public class OrderTrackVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 自增id
	 */
	@ApiModelProperty("自增id")
	private Long id;

	/**
	 * 物流公司编号
	 */
	@ApiModelProperty("物流公司编号")
	private String com;

	/**
	 * 快递单号
	 */
	@ApiModelProperty("快递单号")
	private String num;

	/**
	 * 运单轨迹
	 */
	@ApiModelProperty("运单轨迹")
	private String data;

	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private Date createTime;
}