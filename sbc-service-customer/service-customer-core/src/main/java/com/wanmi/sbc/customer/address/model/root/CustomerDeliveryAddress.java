package com.wanmi.sbc.customer.address.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户配送地址
 * Created by CHENLI on 2017/4/13.
 */
@Data
@Entity
@Table(name = "customer_delivery_address")
public class CustomerDeliveryAddress implements Serializable {

    /**
     * 收货地址ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "delivery_address_id")
    private String deliveryAddressId;
    /**
     * 客户ID
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 收货人
     */
    @Column(name = "consignee_name")
    private String consigneeName;

    /**
     * 收货人手机号码
     */
    @Column(name = "consignee_number")
    private String consigneeNumber;

    /**
     * 省
     */
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 市
     */
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 区
     */
    @Column(name = "area_id")
    private Long areaId;

    /**
     * 详细地址
     */
    @Column(name = "delivery_address")
    private String deliveryAddress;

    /**
     * 省名
     */
    @Column(name = "province_name")
    private String provinceName;

    /**
     * 市名
     */
    @Column(name = "city_name")
    private String cityName;

    /**
     * 区名
     */
    @Column(name = "area_name")
    private String areaName;

    /**
     * 是否是默认地址 0：否 1：是
     */
    @Column(name = "is_defalt_address")
    @Enumerated
    private DefaultFlag isDefaltAddress;

    /**
     * 删除标志 0未删除 1已删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 修改时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Column(name = "update_person")
    private String updatePerson;

    /**
     * 删除时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "delete_time")
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @Column(name = "delete_person")
    private String deletePerson;

    /**
     * 选中标识
     */
    @Column(name = "choose_flag")
    @Enumerated
    private DefaultFlag chooseFlag;

    /**
     * 纬度
     */
    @Column(name = "lat")
    private Double lat;

    /**
     * 经度
     */
    @Column(name = "lng")
    private Double lng;

    /**
     * 详细地址
     */
    @Column(name = "detail_delivery_address")
    private String detailDeliveryAddress;

    /**
     * 街道
     */
    @Column(name = "twon_id")
    private Long twonId;

    /**
     * 街道名
     */
    @Column(name = "twon_name")
    private String twonName;

    /**
     * 街道名
     */
    @Column(name = "is_delivery")
    private Integer isDelivery;

    /**
     * 纬度值
     */
    @Column(name = "n_lat")
    private BigDecimal nLat;


    /**
     * 经度值
     */
    @Column(name = "n_lng")
    private BigDecimal nLng;

    /**
     * 满足网点id
     */
    @Column(name = "network_id")
    private Long networkId;


    /**
     * 满足网点id
     */
    @Column(name = "network_ids")
    private String networkIds;


}
