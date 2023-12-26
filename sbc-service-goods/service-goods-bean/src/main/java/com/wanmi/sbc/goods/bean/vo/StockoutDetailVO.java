package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>缺货管理VO</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@ApiModel
@Data
public class StockoutDetailVO implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 缺货明细
	 */
	@ApiModelProperty(value = "缺货明细")
	private String stockoutDetailId;

	/**
	 * 缺货列表id
	 */
	@ApiModelProperty(value = "缺货列表id")
	private String stockoutId;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private String customerId;

	/**
	 * sku id
	 */
	@ApiModelProperty(value = "sku id")
	private String goodsInfoId;

	/**
	 * sku编码
	 */
	@ApiModelProperty(value = "sku编码")
	private String goodsInfoNo;

	/**
	 * 缺货数量
	 */
	@ApiModelProperty(value = "缺货数量")
	private Long stockoutNum;

	/**
	 * 缺货市code
	 */
	@ApiModelProperty(value = "缺货市code")
	private String cityCode;

	/**
	 * 下单人详细地址
	 */
	@ApiModelProperty(value = "下单人详细地址")
	private String address;


	@ApiModelProperty(value = "客户名称")
	private String customerName;

	@ApiModelProperty(value = "业务员名称")
	private String employeeName;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	private Long wareId;
}