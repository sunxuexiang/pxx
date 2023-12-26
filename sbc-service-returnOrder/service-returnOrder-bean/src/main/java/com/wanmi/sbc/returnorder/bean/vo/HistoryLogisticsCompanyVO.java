package com.wanmi.sbc.returnorder.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * <p>物流公司历史记录VO</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@ApiModel
@Data
public class HistoryLogisticsCompanyVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	private String id;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private String customerId;

	/**
	 * 订单id
	 */
	@ApiModelProperty(value = "订单id")
	private String orderId;

	/**
	 * 物流公司名称
	 */
	@ApiModelProperty(value = "物流公司名称")
	private String logisticsName;

	/**
	 * 物流公司电话
	 */
	@ApiModelProperty(value = "物流公司电话")
	private String logisticsPhone;

	/**
	 * 收货站点
	 */
	@ApiModelProperty(value = "收货站点")
	private String receivingSite;

	/**
	 * 是否是客户自建物流
	 */
	@Enumerated
	private DefaultFlag selfFlag;

	/**
	 * 是否是客户自建物流
	 */
	@Enumerated
	private Long companyId;

	/**
	 * 物流公司地址
	 */
	@ApiModelProperty(value = "物流公司地址")
	private String logisticsAddress;

	/**
	 * 物流类型
	 */
	@ApiModelProperty(value = "物流类型")
	private Integer logisticsType;

	/**
	 * 市场Id
	 */
	@ApiModelProperty(value = "市场Id")
	private Long marketId;
}