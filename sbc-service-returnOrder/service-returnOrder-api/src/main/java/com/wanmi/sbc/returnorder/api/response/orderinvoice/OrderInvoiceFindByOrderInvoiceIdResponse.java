package com.wanmi.sbc.returnorder.api.response.orderinvoice;

import com.wanmi.sbc.returnorder.bean.vo.OrderInvoiceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
@ApiModel
public class OrderInvoiceFindByOrderInvoiceIdResponse {

    @ApiModelProperty(value = "订单开票信息")
    Optional<OrderInvoiceVO> orderInvoiceVO;
}
