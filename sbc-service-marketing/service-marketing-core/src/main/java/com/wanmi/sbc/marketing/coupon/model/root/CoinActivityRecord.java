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

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/30 11:52
 */
@Entity
@Table(name = "coin_activity_record")
@Data
public class CoinActivityRecord {

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

}
