package com.wanmi.sbc.wallet.api.response.wallet;

import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketsFormListResponse implements Serializable {
    private static final long serialVersionUID = 1273365232128963201L;

    private List<TicketsFormQueryVO> ticketsFormQueryVOList;

    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyPrice;


    /**
     * 申请金额
     */
    @ApiModelProperty(value = "通过金额")
    private BigDecimal withdrawalPrice;

}
