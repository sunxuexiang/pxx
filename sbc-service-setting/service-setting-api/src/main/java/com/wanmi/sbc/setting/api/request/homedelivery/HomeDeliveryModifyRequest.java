package com.wanmi.sbc.setting.api.request.homedelivery;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Max;
import java.time.LocalDateTime;

/**
 * <p>配送到家修改参数</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDeliveryModifyRequest extends BaseRequest {


	private static final long serialVersionUID = -2142008810341346055L;
	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	@Max(9223372036854775807L)
	private Long homeDeliveryId;


	/**
	 * 配送到家文案
	 */
	@ApiModelProperty(value = "配送到家文案")
	private String content;

	/**
	 * 生成时间
	 */
	@ApiModelProperty(value = "生成时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

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

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	@ApiModelProperty(value = "配送方式")
	private Integer deliveryType;
}