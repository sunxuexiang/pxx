package com.wanmi.sbc.customer.bean.dto;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoreShippingAddressEditDTO implements Serializable {
    private static final long serialVersionUID = 4130288167513615120L;

    private Long id;

    private Long companyInfoId;

    private Long storeId;

    private Integer provinceCode;

    private Integer cityCode;

    private Integer districtCode;

    private Integer streetCode;

    private String provinceName;

    private String cityName;

    private String districtName;

    private String streetName;

    private String detailAddress;

    private String shippingName;

    private String shippingPhone;

    private String shippingPerson;


    // 1为默认地址
    private Integer defaultFlag;

    private DeleteFlag delFlag;
}
