package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.vo.OfflineSettlementVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class TradeConfirmPayOrderOfflineRequest implements Serializable {
    /**
     * 支付单id列表
     */
    @ApiModelProperty(value = "支付单id列表")
    private List<OfflineSettlementVO> payOrderIds;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Operator operator;
}
