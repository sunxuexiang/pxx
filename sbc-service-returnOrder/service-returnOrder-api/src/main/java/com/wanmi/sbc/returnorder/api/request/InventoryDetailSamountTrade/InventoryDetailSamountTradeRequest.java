package com.wanmi.sbc.returnorder.api.request.InventoryDetailSamountTrade;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.returnorder.bean.vo.InventoryDetailSamountTradeVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;


@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDetailSamountTradeRequest extends BaseRequest implements Serializable {


	private static final long serialVersionUID = -4844007520536215244L;

	@ApiModelProperty(value = "InventoryDetailSamountTradeVO")
	private List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS;

	@ApiModelProperty(value = "订单商品集合")
	private List<TradeItemVO> tradeItemVOS;

	@ApiModelProperty(value = "订单id")
	private String oid;

	@ApiModelProperty(value = "goodsInfoId")
	private String goodsInfoId;

	@ApiModelProperty(value = "return_id")
	private String returnId;

	/**
	 * 0是未退款1是已退款2已受理退款(已申请退款)3预退款
	 */
	@Column(name = "return_flag")
	private int returnFlag =0;

	/**
	 * 批量查詢
	 */
	@ApiModelProperty(value = "goodsInfoIds")
	private List<String> goodsInfoIds;

	@ApiModelProperty(value = "num")
	private int num;


}