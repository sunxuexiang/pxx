package com.wanmi.sbc.goods.customerarealimitdetail.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户区域限购明细表
 */
@Proxy(lazy = false)
@Data
@Entity
@Table(name = "customer_area_limit_detail")
public class CustomerAreaLimitDetail {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 区域id
     */
    @Column(name = "region_id")
    private int regionId;

    /**
     * 商品id
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;


    /**
     * 订单id
     */
    @Column(name = "trade_id")
    private String tradeId;

    /**
     * 单笔购买数量
     */
    @Column(name = "num")
    private BigDecimal num;



    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;



}
