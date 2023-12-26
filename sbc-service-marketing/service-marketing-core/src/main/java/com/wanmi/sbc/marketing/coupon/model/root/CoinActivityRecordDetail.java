package com.wanmi.sbc.marketing.coupon.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
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
@Table(name = "coin_activity_record_detail")
@Data
public class CoinActivityRecordDetail {

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

    /**
     * 是否叠加（0：否，1：是）
     */
    @Enumerated
    @Column(name = "is_overlap")
    private DefaultFlag isOverlap;

    @Column(name = "single_coin_num")
    private BigDecimal singleCoinNum;

    @Column(name = "goods_num")
    private Long goodsNum;


}
