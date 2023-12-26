package com.wanmi.sbc.shopcart.historytownshiporder.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "history_town_ship_order")
@EqualsAndHashCode()
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryTownShipOrder implements Serializable{


    private static final long serialVersionUID = 6273741309044706321L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 订单编号
     */
    @Column(name = "tid")
    private String tid;

    /**
     * 店铺id
     */
    @Column(name = "store_id")
    private String storeId;

    /**
     * 商家编码
     */
    @Column(name = "supplier_code")
    private String supplierCode;

    /**
     * spuId
     */
    @Column(name = "spu_id")
    private String spuId;

    @Column(name = "spu_name")
    private String spuName;

    @Column(name = "sku_id")
    private String skuId;

    @Column(name = "sku_name")
    private String skuName;

    @Column(name = "sku_no")
    private String skuNo;

    @Column(name = "erp_no")
    private String erpNo;

    @Column(name = "cate_id")
    private Long cateId;

    @Column(name = "cate_name")
    private String cateName;

    @Column(name = "brand")
    private Long brand;

    @Column(name = "num")
    private BigDecimal num;

    /**
     * 是否推送wms 0未推送1推送
     */
    @Column(name = "wms_flag")
    private int wmsFlag = 0;

    /**
     * 是否是赠品0是正常商品1是赠品
     */
    @Column(name = "gift_flag")
    private int giftFlag = 0;

    @Column(name = "devanning_id")
    private Long devanningId;

    @Column(name = "divisor_flag")
    private BigDecimal divisorFlag;


    /**
     * 仓库id
     */
    @Column(name = "ware_id")
    private Long wareId;

    /**
     * 是否取消订单0未取消1为取消
     */
    @Column(name = "cancel_flag")
    private int cancelFlag = 0;



    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time",updatable = false,nullable = false)
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime creattime;

    /**
     * 修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time",nullable = false)
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updatetime;






}
