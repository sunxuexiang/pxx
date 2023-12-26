package com.wanmi.sbc.marketing.marketingpurchaselimit.model.root;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

 
@Data
@Entity
@Table(name = "marketing_purchase_limit")
public class MarketingPurchaseLimit implements Serializable {


    private static final long serialVersionUID = -3543743738012955686L;
    /**
     * 积分兑换券id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "customer_id")
    private String customerId;


    @Column(name = "marketing_id")
    private Long marketingId;



    @Column(name = "goods_info_id")
    private String goodsInfoId;


    @Column(name = "trade_id")
    private String tradeId;


    @Column(name = "num")
    private BigDecimal num;



    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;


}