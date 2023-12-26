package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class FreightTemplateDeliveryAreaByAddressIdRequest implements Serializable {
    private static final long serialVersionUID = 719026499910276135L;

    @ApiModelProperty(value = "收货地址id", name = "addressId")
    private String addressId;

    @ApiModelProperty(value = "店铺id", name = "storeId")
    private Long storeId;
}
