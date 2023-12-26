package com.wanmi.sbc.order.api.request.purchase;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.order.bean.dto.TradeItemDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class PurchaseOrderMarketingRequest {

    @ApiModelProperty(value = "订单金额")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "订单数量")
    private Long goodsTotalNum;

}
