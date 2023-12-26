package com.wanmi.sbc.order.api.request.InventoryDetailSamount;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDetailSamountRequest extends BaseRequest implements Serializable {


	private static final long serialVersionUID = -4844007520536215244L;


	@ApiModelProperty(value = "订单商品集合")
	private List<TradeItemVO> tradeItemVOS;

	@ApiModelProperty(value = "订单id")
	private String oid;

	@ApiModelProperty(value = "goodsInfoId")
	private String goodsInfoId;


	@ApiModelProperty(value = "take_id")
	private String takeId;
	/**
	 * 批量查詢
	 */
	@ApiModelProperty(value = "goodsInfoIds")
	private List<String> goodsInfoIds;

	@ApiModelProperty(value = "num")
	private int num;

	@ApiModelProperty(value = "take_ids")
	private List<String> takeIds;
}