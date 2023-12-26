package com.wanmi.sbc.customer.address.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeliveryAddressResponse implements Serializable {

    private String msg;

    private Integer count;

    private Integer remaincount;

    private List<DeliveryAddressDataResponse> results;

}
