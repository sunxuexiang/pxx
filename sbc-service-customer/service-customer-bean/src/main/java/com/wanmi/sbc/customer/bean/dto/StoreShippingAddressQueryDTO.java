package com.wanmi.sbc.customer.bean.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StoreShippingAddressQueryDTO implements Serializable {
    private static final long serialVersionUID = 4130288167513615120L;

    private Long storeId;

    private Long companyInfoId;

    private Long id;

    // 1:代表默认地址
    private Integer defaultFlag;
}
