package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketsFormQueryResponse implements Serializable {
    private static final long serialVersionUID = 1273365232128963201L;

    private MicroServicePage<TicketsFormQueryVO>  ticketsFormQueryPage;

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
