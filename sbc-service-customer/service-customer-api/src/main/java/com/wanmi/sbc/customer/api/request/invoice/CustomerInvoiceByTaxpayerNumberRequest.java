package com.wanmi.sbc.customer.api.request.invoice;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 会员增票资质-根据纳税人识别号查询Request
 */
@ApiModel
@Data
public class CustomerInvoiceByTaxpayerNumberRequest extends CustomerBaseRequest {

    private static final long serialVersionUID = 8352110205225792803L;
    /**
     * 纳税人识别号
     */
    @ApiModelProperty(value = "纳税人识别号")
    @NotBlank
    private String taxpayerNumber;

}
