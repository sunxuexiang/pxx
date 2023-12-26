package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.order.bean.vo.TicketsFormQueryVO;
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
public class TicketsFormPushPayOrderKingdeeRequest implements Serializable {
    /**
     * 提现申请列表
     */
    @ApiModelProperty(value = "提现申请列表")
    private List<TicketsFormQueryVO> ticketsForms;

    @ApiModelProperty("结算方式")
    private String payType;

}
