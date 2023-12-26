package com.wanmi.sbc.customer.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class StoreShippingAddressVO implements Serializable {

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

    // 发件点详细地址
    private String detailAddress;

    // 发件点名称
    private String shippingName;
    // 发件人号码
    private String shippingPhone;

    // 发件人
    private String shippingPerson;


    // 1为默认地址
    private Integer defaultFlag;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    private DeleteFlag delFlag;
}
