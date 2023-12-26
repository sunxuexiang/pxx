package com.wanmi.sbc.shopcart.pilepurchaseaction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/***
 * 明细
 */
@Data
@Entity
@Table(name = "pile_purchase_action")
public class PilePurchaseAction{

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 采购单编号
     */
    @Column(name = "purchase_id")
    private Long purchaseId;

    /**
     * 订单pid
     */
    @Column(name = "pid")
    private String pid;

    /**
     * 订单编号
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 订单总金额
     */
    @Column(name = "order_total_price")
    private BigDecimal orderTotalPrice;

    @Column(name = "customer_id")
    private String  customerId;

    @Column(name =  "goods_id")
    private String goodsId;

    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 订单优惠后商品均摊价
     */
    @Column(name = "goods_split_price")
    private BigDecimal goodsSplitPrice;

    @Column(name = "company_info_id")
    private Long companyInfoId;

    @Column(name = "goods_num")
    private Long goodsNum;

    /**
     * 提货状态
     * 0-待提货，1-部分提货，3-提货完成
     */
    @Column(name = "take_goods_status")
    private Integer takeGoodsStatus;

    /**
     * 采购创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "invitee_id")
    private String inviteeId;
}
