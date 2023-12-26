package com.wanmi.sbc.customer.api.request.address;

import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerDeliveryAddressByProAndNetWorkRequest implements Serializable {

    private static final long serialVersionUID = 5105651558850105057L;

    /**
     * 实体vo
     */
    @ApiModelProperty(value = "实体vo")
    private CustomerDeliveryAddressVO customerDeliveryAddressVO;


}
