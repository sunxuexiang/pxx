package com.wanmi.sbc.customer.address.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryAddressDataResponse implements Serializable {

    private String province;
    private Long province_code;
    private String city;
    private Long city_code;
    private String area;
    private Long area_code;
    private String street;
    private Long street_code;
    private String village;
    private Long village_code;
    private String address;
}
