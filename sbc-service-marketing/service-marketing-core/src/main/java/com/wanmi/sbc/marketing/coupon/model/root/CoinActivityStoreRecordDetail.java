package com.wanmi.sbc.marketing.coupon.model.root;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;

import lombok.Data;

@Entity
@Table(name = "coin_activity_store_record_detail")
@Data
public class CoinActivityStoreRecordDetail {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;

    @Column(name = "record_id")
    private Long recordId;


    @Column(name = "activity_id")
    private String activityId;


    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "coin_num")
    private BigDecimal coinNum;

    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     *  1：赠送，2：退回
     */
    @Column(name = "record_type")
    private Integer recordType;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "record_time")
    private LocalDateTime recordTime;


    @Column(name = "single_coin_num")
    private BigDecimal singleCoinNum;

    @Column(name = "goods_num")
    private Long goodsNum;
    
	/**
	 * 店铺id
	 */
	@Column(name = "store_id")
	private Long storeId;
	
	/**
	 * 店铺name
	 */
	@Column(name = "store_name")
	private String storeName;


}
