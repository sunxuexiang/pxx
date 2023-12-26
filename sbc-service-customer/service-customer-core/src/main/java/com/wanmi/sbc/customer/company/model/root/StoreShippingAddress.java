package com.wanmi.sbc.customer.company.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "store_shipping_address")
public class StoreShippingAddress implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_info_id")
    private Long companyInfoId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "province_code")
    private Integer provinceCode;

    @Column(name = "city_code")
    private Integer cityCode;

    @Column(name = "district_code")
    private Integer districtCode;

    @Column(name = "street_code")
    private Integer streetCode;

    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "shipping_name")
    private String shippingName;

    @Column(name = "shipping_person")
    private String shippingPerson;

    @Column(name = "shipping_phone")
    private String shippingPhone;

    @Column(name = "default_flag")
    private Integer defaultFlag;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;
}
