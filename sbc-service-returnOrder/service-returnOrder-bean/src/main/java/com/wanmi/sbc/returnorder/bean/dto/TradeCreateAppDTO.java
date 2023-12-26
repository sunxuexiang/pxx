package com.wanmi.sbc.returnorder.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class TradeCreateAppDTO extends TradeRemedyAppDTO {


    private static final long serialVersionUID = -5684663987225020720L;

    @ApiModelProperty("自定义")
    private String custom;

    @ApiModelProperty("app端代客下单list")
    @Valid
    List<SupplierAppOrderListDTO> orderList;

}
