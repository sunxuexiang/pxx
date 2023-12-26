package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCountByStateRequest extends CustomerBaseRequest {
    private static final long serialVersionUID = -6700338370684244828L;

    @ApiModelProperty(value = "审核状态")
    private CheckState checkState;
}
