package com.wanmi.sbc.returnorder.api.request.pickuprecord;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.base.Operator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


/**
 * <p>单个查询测试代码生成请求参数</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordUpdateByTradeIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	@NotNull
	private String tradeId;


	/**
	 * 手机号
	 */
	@ApiModelProperty(value = "手机号")
	@NotNull
	private String phone;

	/**
	 * 操作人
	 */
	@ApiModelProperty(value = "操作人")
	@NotNull
	private Operator operator;

}