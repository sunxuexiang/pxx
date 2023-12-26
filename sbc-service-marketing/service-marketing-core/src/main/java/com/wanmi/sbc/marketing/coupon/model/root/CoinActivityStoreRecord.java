package com.wanmi.sbc.marketing.coupon.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "coin_activity_store_record")
@Data
public class CoinActivityStoreRecord {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "send_no")
    private String sendNo;


    @Column(name = "activity_id")
    private String activityId;


    @Column(name = "customer_account")
    private String customerAccount;

    @Column(name = "order_no")
    private String orderNo;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "order_time")
    private LocalDateTime orderTime;

    @Column(name = "order_price")
    private BigDecimal orderPrice;

    @Column(name = "coin_num")
    private BigDecimal coinNum;

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
