package com.wanmi.sbc.walletorder.inventorydetailsamounttrade.model.root;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory_details_amount_trade")
@EqualsAndHashCode()
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDetailSamountTrade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_details_amount_trade_id")
    private Long inventoryDetailsAmountTradeId;

    /**
     * 订单id
     */
    @Column(name = "trade_id")
    private String tradeId;

    /**
     * 商品goods_info_id 这里没拆箱所有用的是goods_info_id
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 商品goods_info_id 这里没拆箱所有用的是goods_info_id
     */
    @Column(name = "return_id")
    private String returnId;


    /**
     * 数量级平摊价格 例子 10元买了3个 那么一个存的是 3.33元  最后一个存的是3.34元
     */
    @Column(name = "amortized_expenses")
    private BigDecimal amortizedExpenses;


    /**
     * 0是未退款1是已退款2已受理退款(已申请退款)
     */
    @Column(name = "return_flag")
    private int returnFlag =0;


    /**
     * 金钱类型0是余额1真实的钱
     */
    @Column(name = "money_type")
    private int moneyType;



    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time",updatable = false,nullable = false)
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;



}
